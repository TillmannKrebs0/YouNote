package com.example.notesapp.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
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
            modifier = Modifier
                .weight(1f),
            singleLine = false,
            maxLines = 10,
            placeholder = { Text("Write a Note...") },

        )

        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = onPostNote,
            modifier = Modifier
                .align(Alignment.Bottom)
        ) {
            Icon(
                Icons.Rounded.Send,
                contentDescription = "send"
            )
        }
    }
}