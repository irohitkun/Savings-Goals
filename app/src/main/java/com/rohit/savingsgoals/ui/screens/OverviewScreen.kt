package com.rohit.savingsgoals.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rohit.savingsgoals.data.Contribution
import com.rohit.savingsgoals.data.GoalWithSaved
import com.rohit.savingsgoals.ui.theme.*
import java.util.Calendar
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(
    goals: List<GoalWithSaved>,
    allContributions: List<Contribution>,
    onBack: () -> Unit,
    onOpenGoal: (Long) -> Unit
) {
    val totalSaved = goals.sumOf { it.savedAmount }
    val totalTarget = goals.sumOf { it.targetAmount }
    val overallProgress = if (totalTarget > 0) min(totalSaved / totalTarget, 1.0).toFloat() else 0f
    val animatedProgress by animateFloatAsState(targetValue = overallProgress, animationSpec = tween(800), label = "overviewRing")

    val completedCount = goals.count { it.targetAmount > 0 && it.savedAmount >= it.targetAmount }
    val thisMonthSaved = remember(allContributions) { sumThisMonth(allContributions) }
    val rankedGoals = remember(goals) {
        goals.sortedByDescending { if (it.targetAmount > 0) it.savedAmount / it.targetAmount else 0.0 }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Overview", color = InkBlack) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = InkBlack)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp))
                        .background(Brush.linearGradient(listOf(OrangeAccent, OrangeDeep)))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressRing(
                            progress = animatedProgress,
                            size = 128.dp,
                            strokeWidth = 9.dp,
                            trackColor = Color.White.copy(alpha = 0.28f),
                            progressColor = Color.White
                        ) {
                            Text(
                                "${(overallProgress * 100).toInt()}%",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("TOTAL SAVED", style = MaterialTheme.typography.labelLarge, color = Color.White.copy(alpha = 0.85f))
                        Text(
                            "₹${formatAmount(totalSaved)}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White
                        )
                        Text(
                            "of ₹${formatAmount(totalTarget)} across ${goals.size} goal${if (goals.size == 1) "" else "s"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatBlock(label = "GOALS", value = goals.size.toString(), modifier = Modifier.weight(1f))
                    StatBlock(label = "COMPLETED", value = completedCount.toString(), modifier = Modifier.weight(1f))
                    StatBlock(label = "THIS MONTH", value = "₹${formatAmount(thisMonthSaved)}", modifier = Modifier.weight(1f))
                }
            }

            item {
                Text(
                    "RANKED BY PROGRESS",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (rankedGoals.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No goals yet", color = TextSecondary)
                    }
                }
            } else {
                itemsIndexed(rankedGoals) { index, goal ->
                    RankedGoalRow(rank = index + 1, goal = goal, onClick = { onOpenGoal(goal.id) })
                }
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun StatBlock(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(CardWhite)
            .border(1.dp, BorderFaint, RoundedCornerShape(18.dp))
            .padding(vertical = 16.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, style = MaterialTheme.typography.titleMedium, color = InkBlack)
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelMedium, color = TextTertiary)
    }
}

@Composable
private fun RankedGoalRow(rank: Int, goal: GoalWithSaved, onClick: () -> Unit) {
    val progress = if (goal.targetAmount > 0) min(goal.savedAmount / goal.targetAmount, 1.0).toFloat() else 0f
    val isComplete = progress >= 1f
    val accentColor = if (isComplete) SuccessGreen else OrangeAccent

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(CardWhite)
            .border(1.dp, BorderFaint, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(TrackGray),
                contentAlignment = Alignment.Center
            ) {
                Text("$rank", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            }
            Spacer(modifier = Modifier.width(10.dp))
            GoalAvatar(goal = goal, size = 38.dp)
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(goal.name, style = MaterialTheme.typography.bodyLarge, color = InkBlack)
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(TrackGray)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress.coerceIn(0f, 1f))
                            .clip(RoundedCornerShape(3.dp))
                            .background(accentColor)
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.labelLarge,
                color = accentColor
            )
        }
    }
}

private fun sumThisMonth(contributions: List<Contribution>): Double {
    val cal = Calendar.getInstance()
    val month = cal.get(Calendar.MONTH)
    val year = cal.get(Calendar.YEAR)
    return contributions.filter {
        val c = Calendar.getInstance().apply { timeInMillis = it.timestamp }
        c.get(Calendar.MONTH) == month && c.get(Calendar.YEAR) == year
    }.sumOf { it.amount }
}
