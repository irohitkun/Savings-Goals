package com.rohit.savingsgoals.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rohit.savingsgoals.data.GoalWithSaved
import com.rohit.savingsgoals.ui.theme.*
import com.rohit.savingsgoals.util.DeadlineFormat
import java.io.File
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalListScreen(
    goals: List<GoalWithSaved>,
    userName: String,
    onAddGoal: (GoalFormResult) -> Unit,
    onEditGoal: (Long, GoalFormResult) -> Unit,
    onDeleteGoal: (GoalWithSaved) -> Unit,
    onQuickSave: (goalId: Long, amount: Double, note: String) -> Unit,
    onOpenGoal: (Long) -> Unit,
    onOpenOverview: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingGoal by remember { mutableStateOf<GoalWithSaved?>(null) }
    var quickSaveGoal by remember { mutableStateOf<GoalWithSaved?>(null) }
    var confirmDeleteGoal by remember { mutableStateOf<GoalWithSaved?>(null) }

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                shape = CircleShape,
                containerColor = InkBlack,
                contentColor = CardWhite
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add goal")
            }
        }
    ) { padding ->
        if (goals.isEmpty()) {
            EmptyState(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { GreetingHeader(userName) }
                item { HeroCard(goals = goals, onClick = onOpenOverview) }
                items(goals, key = { it.id }) { goal ->
                    GoalCard(
                        goal = goal,
                        onClick = { onOpenGoal(goal.id) },
                        onQuickSave = { quickSaveGoal = goal },
                        onEdit = { editingGoal = goal },
                        onDelete = { confirmDeleteGoal = goal }
                    )
                }
                item { Spacer(modifier = Modifier.height(72.dp)) }
            }
        }
    }

    if (showAddDialog) {
        GoalFormDialog(
            title = "New goal",
            confirmLabel = "Create",
            onDismiss = { showAddDialog = false },
            onConfirm = { result -> onAddGoal(result); showAddDialog = false }
        )
    }

    editingGoal?.let { goal ->
        GoalFormDialog(
            title = "Edit goal",
            confirmLabel = "Save",
            initialName = goal.name,
            initialEmoji = goal.emoji,
            initialImagePath = goal.imagePath,
            initialCategory = goal.category,
            initialTarget = formatPlainAmount(goal.targetAmount),
            initialTargetDateMillis = goal.targetDateMillis,
            onDismiss = { editingGoal = null },
            onConfirm = { result -> onEditGoal(goal.id, result); editingGoal = null }
        )
    }

    quickSaveGoal?.let { goal ->
        QuickSaveDialog(
            goalName = goal.name,
            onDismiss = { quickSaveGoal = null },
            onConfirm = { amount, note -> onQuickSave(goal.id, amount, note); quickSaveGoal = null }
        )
    }

    confirmDeleteGoal?.let { goal ->
        AlertDialog(
            onDismissRequest = { confirmDeleteGoal = null },
            containerColor = CardWhite,
            title = { Text("Delete this goal?", color = InkBlack) },
            text = {
                Text(
                    "This removes \"${goal.name}\" and all its contribution history. This can't be undone.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = { onDeleteGoal(goal); confirmDeleteGoal = null }) {
                    Text("Delete", color = DangerSoft, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDeleteGoal = null }) { Text("Cancel", color = TextSecondary) }
            }
        )
    }
}

@Composable
private fun GreetingHeader(userName: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("YOUR GOALS", style = MaterialTheme.typography.labelLarge, color = TextSecondary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                "Keep saving, $userName",
                style = MaterialTheme.typography.titleLarge,
                color = InkBlack
            )
        }
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(PeachTint),
            contentAlignment = Alignment.Center
        ) {
            Text(
                userName.take(1).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = OrangeDeep
            )
        }
    }
}

@Composable
private fun HeroCard(goals: List<GoalWithSaved>, onClick: () -> Unit) {
    val totalSaved = goals.sumOf { it.savedAmount }
    val totalTarget = goals.sumOf { it.targetAmount }
    val overallProgress = if (totalTarget > 0) min(totalSaved / totalTarget, 1.0).toFloat() else 0f
    val animatedProgress by animateFloatAsState(targetValue = overallProgress, animationSpec = tween(900), label = "heroRing")
    val animatedTotal by animateFloatAsState(targetValue = totalSaved.toFloat(), animationSpec = tween(900), label = "heroCount")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(listOf(OrangeAccent, OrangeDeep)))
            .clickable(onClick = onClick)
            .padding(22.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("TOTAL SAVED", style = MaterialTheme.typography.labelLarge, color = Color.White.copy(alpha = 0.85f))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "₹${formatAmount(animatedTotal.toDouble())}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                }
                CircularProgressRing(
                    progress = animatedProgress,
                    size = 72.dp,
                    strokeWidth = 5.dp,
                    trackColor = Color.White.copy(alpha = 0.28f),
                    progressColor = Color.White
                ) {
                    Text(
                        "${(overallProgress * 100).toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.18f))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        "🎯 of ₹${formatAmount(totalTarget)} goal",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
                Text(
                    "See breakdown →",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🐷", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("No goals yet", style = MaterialTheme.typography.titleMedium, color = InkBlack)
            Text(
                "Tap + to set your first savings goal",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun GoalCard(
    goal: GoalWithSaved,
    onClick: () -> Unit,
    onQuickSave: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val progress = if (goal.targetAmount > 0) min(goal.savedAmount / goal.targetAmount, 1.0).toFloat() else 0f
    val animatedProgress by animateFloatAsState(targetValue = progress, animationSpec = tween(700), label = "goalBar")
    val isComplete = progress >= 1f
    val accentColor = if (isComplete) SuccessGreen else OrangeAccent
    var menuExpanded by remember { mutableStateOf(false) }
    val deadlineLabel = remember(goal.targetDateMillis) { DeadlineFormat.chipLabel(goal.targetDateMillis) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(CardWhite)
            .border(1.dp, BorderFaint, RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.Top) {
                GoalAvatar(goal = goal, size = 48.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(goal.name, style = MaterialTheme.typography.titleMedium, color = InkBlack)
                    if (goal.category.isNotBlank()) {
                        Text(
                            goal.category.uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            color = TextTertiary
                        )
                    }
                }
                if (deadlineLabel != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(TrackGray)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.CalendarToday,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(deadlineLabel, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                        }
                    }
                }
                Box {
                    IconButton(onClick = { menuExpanded = true }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Goal options", tint = TextTertiary)
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                            onClick = { menuExpanded = false; onEdit() }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = null) },
                            onClick = { menuExpanded = false; onDelete() }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "₹${formatAmount(goal.savedAmount)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = InkBlack
                )
                Text(
                    " / ${formatAmount(goal.targetAmount)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextTertiary
                )
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(accentColor.copy(alpha = 0.14f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = accentColor
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(TrackGray)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress.coerceIn(0f, 1f))
                        .clip(RoundedCornerShape(3.dp))
                        .background(accentColor)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val remaining = (goal.targetAmount - goal.savedAmount).coerceAtLeast(0.0)
                Text(
                    if (isComplete) "Goal reached 🎉" else "₹${formatAmount(remaining)} more",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(PeachTint)
                        .clickable(onClick = onQuickSave)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(15.dp), tint = OrangeDeep)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save", style = MaterialTheme.typography.labelLarge, color = OrangeDeep)
                    }
                }
            }
        }
    }
}

@Composable
fun GoalAvatar(goal: GoalWithSaved, size: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(size / 3.2f))
            .background(PeachTint),
        contentAlignment = Alignment.Center
    ) {
        if (goal.imagePath != null) {
            AsyncImage(
                model = File(goal.imagePath),
                contentDescription = goal.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(size / 3.2f))
            )
        } else {
            Text(goal.emoji, style = MaterialTheme.typography.titleLarge)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuickSaveDialog(
    goalName: String,
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, note: String) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardWhite,
        title = { Text("Save to $goalName", color = InkBlack) },
        text = {
            Column {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { input -> amountText = input.filter { it.isDigit() || it == '.' } },
                    label = { Text("Amount (₹)") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = warmFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (optional)") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = warmFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amount = amountText.toDoubleOrNull()
                    if (amount != null && amount > 0) onConfirm(amount, note.trim())
                },
                enabled = amountText.toDoubleOrNull() != null
            ) { Text("Save", color = OrangeDeep, fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        }
    )
}

fun formatAmount(amount: Double): String {
    return if (amount == amount.toLong().toDouble()) {
        "%,d".format(amount.toLong())
    } else {
        "%,.2f".format(amount)
    }
}

fun formatPlainAmount(amount: Double): String {
    return if (amount == amount.toLong().toDouble()) {
        amount.toLong().toString()
    } else {
        amount.toString()
    }
}
