package com.rohit.savingsgoals

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rohit.savingsgoals.data.UserPrefs
import com.rohit.savingsgoals.ui.GoalViewModel
import com.rohit.savingsgoals.ui.screens.GoalDetailScreen
import com.rohit.savingsgoals.ui.screens.GoalListScreen
import com.rohit.savingsgoals.ui.screens.OnboardingScreen
import com.rohit.savingsgoals.ui.screens.OverviewScreen
import com.rohit.savingsgoals.ui.theme.SavingsGoalsTheme
import com.rohit.savingsgoals.ui.theme.ambientBackground
import com.rohit.savingsgoals.ui.theme.ambientGlow

private const val ROUTE_ONBOARDING = "onboarding"
private const val ROUTE_GOALS = "goals"
private const val ROUTE_DETAIL = "goals/{goalId}"
private const val ROUTE_OVERVIEW = "overview"
private const val ARG_GOAL_ID = "goalId"

private const val SLIDE_MS = 340
private val SLIDE_EASING = FastOutSlowInEasing

class MainActivity : ComponentActivity() {

    private val viewModel: GoalViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SavingsGoalsTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Base warm gradient across the whole app
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(ambientBackground())
                    )
                    // Soft glow accent near the top for extra depth
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(420.dp)
                            .align(Alignment.TopCenter)
                            .background(ambientGlow())
                    )
                    AppRoot(viewModel)
                }
            }
        }
    }
}

@Composable
private fun AppRoot(viewModel: GoalViewModel) {
    val context = LocalContext.current
    val userPrefs = remember { UserPrefs(context) }
    var userName by remember { mutableStateOf(userPrefs.getName()) }

    val navController = rememberNavController()
    val goals by viewModel.goals.collectAsState()
    val startDestination = if (userPrefs.hasOnboarded()) ROUTE_GOALS else ROUTE_ONBOARDING

    NavHost(navController = navController, startDestination = startDestination) {
        composable(
            route = ROUTE_ONBOARDING,
            exitTransition = {
                fadeOut(animationSpec = tween(SLIDE_MS, easing = SLIDE_EASING))
            }
        ) {
            OnboardingScreen(
                onDone = { name ->
                    userPrefs.setName(name)
                    userName = name
                    navController.navigate(ROUTE_GOALS) {
                        popUpTo(ROUTE_ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = ROUTE_GOALS,
            enterTransition = {
                fadeIn(animationSpec = tween(SLIDE_MS, easing = SLIDE_EASING))
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(SLIDE_MS, easing = SLIDE_EASING)
                ) + fadeOut(animationSpec = tween(SLIDE_MS, easing = SLIDE_EASING))
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(SLIDE_MS, easing = SLIDE_EASING)
                ) + fadeIn(animationSpec = tween(SLIDE_MS, easing = SLIDE_EASING))
            }
        ) {
            GoalListScreen(
                goals = goals,
                userName = userName ?: "there",
                onAddGoal = { result ->
                    viewModel.addGoal(
                        result.name, result.emoji, result.imagePath, result.category,
                        result.targetAmount, result.targetDateMillis
                    )
                },
                onEditGoal = { goalId, result ->
                    val existing = goals.firstOrNull { it.id == goalId }
                    viewModel.updateGoal(
                        goalId = goalId,
                        name = result.name,
                        emoji = result.emoji,
                        imagePath = result.imagePath,
                        category = result.category,
                        targetAmount = result.targetAmount,
                        targetDateMillis = result.targetDateMillis,
                        createdAt = existing?.createdAt ?: System.currentTimeMillis()
                    )
                },
                onDeleteGoal = { goal -> viewModel.deleteGoal(goal) },
                onQuickSave = { goalId, amount, note -> viewModel.addContribution(goalId, amount, note) },
                onOpenGoal = { id -> navController.navigate("goals/$id") },
                onOpenOverview = { navController.navigate(ROUTE_OVERVIEW) }
            )
        }

        composable(
            route = ROUTE_DETAIL,
            arguments = listOf(navArgument(ARG_GOAL_ID) { type = NavType.LongType }),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(SLIDE_MS, easing = SLIDE_EASING)
                ) + fadeIn(animationSpec = tween(SLIDE_MS, easing = SLIDE_EASING))
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(SLIDE_MS, easing = SLIDE_EASING)
                ) + fadeOut(animationSpec = tween(SLIDE_MS, easing = SLIDE_EASING))
            }
        ) { backStackEntry ->
            val goalId = backStackEntry.arguments?.getLong(ARG_GOAL_ID) ?: return@composable
            val goalFlow = remember(goalId) { viewModel.goalFlow(goalId) }
            val contributionsFlow = remember(goalId) { viewModel.contributionsFlow(goalId) }
            val goal by goalFlow.collectAsState()
            val contributions by contributionsFlow.collectAsState()

            val currentGoal = goal
            if (currentGoal != null) {
                GoalDetailScreen(
                    goal = currentGoal,
                    contributions = contributions,
                    onBack = { navController.popBackStack() },
                    onAddContribution = { amount, note -> viewModel.addContribution(goalId, amount, note) },
                    onDeleteContribution = { contribution -> viewModel.removeContribution(contribution) },
                    onDeleteGoal = {
                        viewModel.deleteGoal(currentGoal)
                        navController.popBackStack()
                    },
                    onEditGoal = { result ->
                        viewModel.updateGoal(
                            goalId = currentGoal.id,
                            name = result.name,
                            emoji = result.emoji,
                            imagePath = result.imagePath,
                            category = result.category,
                            targetAmount = result.targetAmount,
                            targetDateMillis = result.targetDateMillis,
                            createdAt = currentGoal.createdAt
                        )
                    }
                )
            }
        }

        composable(
            route = ROUTE_OVERVIEW,
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { it }, animationSpec = tween(SLIDE_MS, easing = SLIDE_EASING)
                ) + fadeIn(animationSpec = tween(SLIDE_MS, easing = SLIDE_EASING))
            },
            popExitTransition = {
                slideOutVertically(
                    targetOffsetY = { it }, animationSpec = tween(SLIDE_MS, easing = SLIDE_EASING)
                ) + fadeOut(animationSpec = tween(SLIDE_MS, easing = SLIDE_EASING))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(SLIDE_MS, easing = SLIDE_EASING))
            }
        ) {
            val allContributions by viewModel.allContributions.collectAsState()
            OverviewScreen(
                goals = goals,
                allContributions = allContributions,
                onBack = { navController.popBackStack() },
                onOpenGoal = { id -> navController.navigate("goals/$id") }
            )
        }
    }
}
