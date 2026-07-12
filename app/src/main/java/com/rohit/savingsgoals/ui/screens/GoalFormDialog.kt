package com.rohit.savingsgoals.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rohit.savingsgoals.ui.theme.*
import com.rohit.savingsgoals.util.DeadlineFormat
import com.rohit.savingsgoals.util.ImageStorage
import kotlinx.coroutines.launch
import java.io.File

val goalEmojis = listOf("🎯", "💻", "✈️", "🏍️", "📱", "🎮", "👟", "🏠", "🎓", "📷", "⌚", "🎧")

data class GoalFormResult(
    val name: String,
    val emoji: String,
    val imagePath: String?,
    val category: String,
    val targetAmount: Double,
    val targetDateMillis: Long?
)

/**
 * Shared dialog for both creating a new goal and editing an existing one.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun GoalFormDialog(
    title: String,
    confirmLabel: String,
    initialName: String = "",
    initialEmoji: String = goalEmojis.first(),
    initialImagePath: String? = null,
    initialCategory: String = "",
    initialTarget: String = "",
    initialTargetDateMillis: Long? = null,
    onDismiss: () -> Unit,
    onConfirm: (GoalFormResult) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf(initialName) }
    var category by remember { mutableStateOf(initialCategory) }
    var targetText by remember { mutableStateOf(initialTarget) }
    var selectedEmoji by remember { mutableStateOf(initialEmoji) }
    var pickedImagePath by remember { mutableStateOf(initialImagePath) }
    var pickedImageUri by remember { mutableStateOf<Uri?>(null) }
    var usePhoto by remember { mutableStateOf(initialImagePath != null) }
    var targetDateMillis by remember { mutableStateOf(initialTargetDateMillis) }
    var showDatePicker by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            pickedImageUri = uri
            usePhoto = true
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardWhite,
        title = { Text(title, fontWeight = FontWeight.ExtraBold, color = InkBlack) },
        text = {
            Column {
                // Icon preview
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(84.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(PeachTint)
                        .border(1.5.dp, OrangeAccent.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                        .clickable {
                            photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                    contentAlignment = Alignment.Center
                ) {
                    val displayModel: Any? = pickedImageUri ?: pickedImagePath?.let { File(it) }
                    if (usePhoto && displayModel != null) {
                        AsyncImage(
                            model = displayModel,
                            contentDescription = "Goal image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(20.dp))
                        )
                    } else {
                        Text(selectedEmoji, style = MaterialTheme.typography.titleLarge)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Tap to choose a photo",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (usePhoto) {
                    TextButton(
                        onClick = { usePhoto = false; pickedImageUri = null; pickedImagePath = null },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Use an emoji instead", color = OrangeDeep)
                    }
                } else {
                    Text("OR PICK AN EMOJI", style = MaterialTheme.typography.labelLarge, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(goalEmojis) { emoji ->
                            val selected = emoji == selectedEmoji
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (selected) PeachTint else TrackGray)
                                    .border(
                                        width = if (selected) 1.5.dp else 0.dp,
                                        color = if (selected) OrangeAccent else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedEmoji = emoji },
                                contentAlignment = Alignment.Center
                            ) { Text(emoji) }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Goal name") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = warmFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = targetText,
                        onValueChange = { input -> targetText = input.filter { it.isDigit() || it == '.' } },
                        label = { Text("Target (₹)") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = warmFieldColors(),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it.take(14) },
                        label = { Text("Category") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = warmFieldColors(),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(TrackGray)
                        .clickable { showDatePicker = true }
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Text(
                        targetDateMillis?.let { "Deadline: ${DeadlineFormat.formatFull(it)}" } ?: "Set a deadline (optional)",
                        color = if (targetDateMillis != null) InkBlack else TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = name.isNotBlank() && targetText.toDoubleOrNull() != null && !isSaving,
                onClick = {
                    val target = targetText.toDoubleOrNull() ?: return@TextButton
                    if (name.isBlank()) return@TextButton

                    val uriToPersist = pickedImageUri
                    if (usePhoto && uriToPersist != null) {
                        isSaving = true
                        scope.launch {
                            val savedPath = ImageStorage.persist(context, uriToPersist)
                            isSaving = false
                            onConfirm(
                                GoalFormResult(
                                    name = name.trim(),
                                    emoji = selectedEmoji,
                                    imagePath = savedPath,
                                    category = category.trim(),
                                    targetAmount = target,
                                    targetDateMillis = targetDateMillis
                                )
                            )
                        }
                    } else {
                        onConfirm(
                            GoalFormResult(
                                name = name.trim(),
                                emoji = selectedEmoji,
                                imagePath = if (usePhoto) pickedImagePath else null,
                                category = category.trim(),
                                targetAmount = target,
                                targetDateMillis = targetDateMillis
                            )
                        )
                    }
                }
            ) {
                Text(if (isSaving) "Saving…" else confirmLabel, color = OrangeDeep, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        }
    )

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = targetDateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    targetDateMillis = datePickerState.selectedDateMillis
                    showDatePicker = false
                }) { Text("OK", color = OrangeDeep, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel", color = TextSecondary) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun warmFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = OrangeAccent,
    unfocusedBorderColor = BorderFaint,
    focusedLabelColor = OrangeDeep,
    unfocusedLabelColor = TextSecondary,
    focusedTextColor = InkBlack,
    unfocusedTextColor = InkBlack,
    cursorColor = OrangeAccent
)
