package com.example.notesapp.view.MainLayout.Components


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import com.example.notesapp.viewmodel.CategoriesUiState
import com.example.notesapp.viewmodel.CategoryViewModel

@Composable
fun ControlBar(
    onTextChange: (String) -> Unit,
    onFilterOpen: () -> Unit,
    onSideBarOpen: () -> Unit,
) {
    var search by remember { mutableStateOf("") }


    Row {
        Button(onClick = { onSideBarOpen() }) {
            Icon(
                Icons.Rounded.Menu,
                contentDescription = "openMenu"
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = search,
            onValueChange = {
                search = it
                onTextChange(it)
            },
            modifier = Modifier
                .weight(1f)
                .heightIn(max = 56.dp),  // Add this line
            singleLine = true,
            placeholder = { Text("Search for Note...") },
            trailingIcon = {
                if (search.isNotEmpty()) {
                    IconButton(onClick = {
                        search = ""
                        onTextChange("")
                    }) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Clear search"
                        )
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                // Container (background) colors
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,

                // Text colors
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,

                // Placeholder text color
                focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        Button(onClick = { onFilterOpen() }) {
            Icon(
                Icons.Rounded.FilterAlt,
                contentDescription = "set Filter"
            )
        }
    }
}