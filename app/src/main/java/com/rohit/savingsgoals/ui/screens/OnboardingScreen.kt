package com.rohit.savingsgoals.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rohit.savingsgoals.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(onDone: (name: String) -> Unit) {
    var name by remember { mutableStateOf("") }

    Scaffold(containerColor = Color.Transparent) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(OrangeAccent, OrangeDeep))),
                contentAlignment = Alignment.Center
            ) {
                Text("🐷", style = MaterialTheme.typography.headlineMedium)
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Welcome to your Vault",
                style = MaterialTheme.typography.titleLarge,
                color = InkBlack,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "What should we call you?",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(28.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it.take(24) },
                placeholder = { Text("Your name") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = warmFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { if (name.isNotBlank()) onDone(name.trim()) },
                enabled = name.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = InkBlack,
                    contentColor = CardWhite,
                    disabledContainerColor = TrackGray,
                    disabledContentColor = TextTertiary
                )
            ) {
                Text("Get started", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}
