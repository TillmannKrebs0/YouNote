package com.example.notesapp.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun InputBar(
    textInput: String,
    onTextChange: (String) -> Unit,
    onPostNote: () -> Unit,
    onHeightChanged: (Dp) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    Row(
        modifier = modifier
            .padding(vertical = 8.dp)
            .onSizeChanged { size ->
                with(density) {
                    // Convert height to dp and add some padding
                    onHeightChanged(size.height.toDp() + 16.dp)
                }
            },
        verticalAlignment = Alignment.Bottom
    ) {
        OutlinedTextField(
            value = textInput,
            onValueChange = onTextChange,
            modifier = Modifier.weight(1f),
            singleLine = false,
            maxLines = 5, // Limit maximum lines to prevent excessive growth
            placeholder = { Text("Enter note...") }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = onPostNote,
            modifier = Modifier.align(Alignment.Bottom)
        ) {
            Icon(
                Icons.Rounded.Send,
                contentDescription = "send"
            )
        }
    }
}