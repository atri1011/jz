package com.jizhang.ak.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign

// Mock Composable for ScreenTitle
@Composable
fun ScreenTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 16.dp),
        textAlign = TextAlign.Center
    )
}

// Mock Composable for StatusBar
@Composable
fun MockStatusBar() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp) // Approximate height of status bar
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)) // Just for visualization
    )
}