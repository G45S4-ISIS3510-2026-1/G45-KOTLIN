package com.example.g45_kotlin.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CommonErrorDialog(modifier: Modifier = Modifier, message:String, onDismiss:()->Unit={}){
    AlertDialog(modifier=modifier,
        containerColor = MaterialTheme.colorScheme.errorContainer,
        onDismissRequest = {
            onDismiss()
        },
        title = {
            Text(text = "Error", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onErrorContainer)
        },
        text = {
            Text(text = message, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onErrorContainer)
        },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        }
    )
}