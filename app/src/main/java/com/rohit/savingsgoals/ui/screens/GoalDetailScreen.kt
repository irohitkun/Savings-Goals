package com.rohit.savingsgoals.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rohit.savingsgoals.data.Contribution
import com.rohit.savingsgoals.data.GoalWithSaved
import com.rohit.savingsgoals.ui.theme.*
import com.rohit.savingsgoals.util.DeadlineFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreen(
    goal: GoalWithSaved,
    contributions: List<Contribution>,
    onBack: () -> Unit,
    onAddContribution: (amount: Double, note: String) -> Unit,
    onDeleteContribution: (Contribution) -> Unit,
    onDeleteGoal: () -> Unit,
    onEditGoal: (GoalFormResult) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }

    val progress = if (goal.targetAmount > 0) min(goal.savedAmount / goal.targetAmount, 1.0).toFloat() else 0f
    val animatedProgress by animateFloatAsState(targetValue = progress, animationSpec = tween(700), label = "detailGauge")
    val isComplete = progress >= 1f
    val accentColor = if (isComplete) SuccessGreen else OrangeAccent
    val remaining = (goal.targetAmount - goal.savedAmount).coerceAtLeast(0.0)

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            Box(modifier = Modifier.padding(16.dp)) {
                Button(
                    onClick = { showAddDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = InkBlack, contentColor = CardWhite)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Money", style = MaterialTheme.typography.titleMedium, color = CardWhite)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CircleIconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = InkBlack)
                }
                Text(goal.name, style = MaterialTheme.typography.titleMedium, color = InkBlack)
                Box {
                    CircleIconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Options", tint = InkBlack)
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text("Edit goal") },
                            leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                            onClick = { menuExpanded = false; showEditDialog = true }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete goal") },
                            leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = null) },
                            onClick = { menuExpanded = false; showDeleteConfirm = true }
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))
                        SemiCircleGauge(
                            progress = animatedProgress,
                            diameter = 220.dp,
                            strokeWidth = 16.dp,
                            trackColor = TrackGray,
                            progressColor = accentColor
                        ) {
                            GoalAvatar(goal = goal, size = 72.dp)
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("CURRENTLY SAVED", style = MaterialTheme.typography.labelLarge, color = TextSecondary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "₹${formatAmount(goal.savedAmount)}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = InkBlack
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(TrackGray)
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                "🎯 Goal: ₹${formatAmount(goal.targetAmount)} · ${(progress * 100).toInt()}% there",
                                style = MaterialTheme.typography.bodyMedium,
                                color = InkBlack
                            )
                        }
                        Spacer(modifier = Modifier.height(18.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            StatCard(
                                label = "REMAINING",
                                value = if (isComplete) "₹0" else "₹${formatAmount(remaining)}",
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                label = "PROGRESS",
                                value = "${(progress * 100).toInt()}%",
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                label = "TIME LEFT",
                                value = DeadlineFormat.timeLeftStat(goal.targetDateMillis),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("History", style = MaterialTheme.typography.titleMedium, color = InkBlack)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                if (contributions.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No contributions yet — add your first one", color = TextSecondary)
                        }
                    }
                } else {
                    items(contributions, key = { it.id }) { contribution ->
                        ContributionRow(contribution = contribution, onDelete = { onDeleteContribution(contribution) })
                    }
                }
                item { Spacer(modifier = Modifier.height(90.dp)) }
            }
        }
    }

    if (showAddDialog) {
        AddContributionDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { amount, note -> onAddContribution(amount, note); showAddDialog = false }
        )
    }

    if (showEditDialog) {
        GoalFormDialog(
            title = "Edit goal",
            confirmLabel = "Save",
            initialName = goal.name,
            initialEmoji = goal.emoji,
            initialImagePath = goal.imagePath,
            initialCategory = goal.category,
            initialTarget = formatPlainAmount(goal.targetAmount),
            initialTargetDateMillis = goal.targetDateMillis,
            onDismiss = { showEditDialog = false },
            onConfirm = { result -> onEditGoal(result); showEditDialog = false }
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            containerColor = CardWhite,
            title = { Text("Delete this goal?", color = InkBlack) },
            text = {
                Text(
                    "This removes \"${goal.name}\" and all its contribution history. This can't be undone.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = { showDeleteConfirm = false; onDeleteGoal() }) {
                    Text("Delete", color = DangerSoft, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel", color = TextSecondary) }
            }
        )
    }
}

@Composable
private fun CircleIconButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(TrackGray)
            .then(Modifier),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = onClick, modifier = Modifier.size(40.dp)) { content() }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CardWhite)
            .border(1.dp, BorderFaint, RoundedCornerShape(16.dp))
            .padding(vertical = 14.dp, horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, style = MaterialTheme.typography.titleMedium, color = InkBlack)
        Spacer(modifier = Modifier.height(2.dp))
        Text(label, style = MaterialTheme.typography.labelMedium, color = TextTertiary)
    }
}

@Composable
private fun ContributionRow(contribution: Contribution, onDelete: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("d MMM, h:mm a", Locale.getDefault()) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardWhite)
            .border(1.dp, BorderFaint, RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("+₹${formatAmount(contribution.amount)}", style = MaterialTheme.typography.titleMedium, color = SuccessGreen)
                if (contribution.note.isNotBlank()) {
                    Text(contribution.note, style = MaterialTheme.typography.bodyMedium, color = InkBlack)
                }
                Text(dateFormat.format(Date(contribution.timestamp)), style = MaterialTheme.typography.labelMedium, color = TextTertiary)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Remove", tint = TextTertiary)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddContributionDialog(
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, note: String) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardWhite,
        title = { Text("Add money", color = InkBlack) },
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
            ) { Text("Add", color = OrangeDeep, fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        }
    )
}
