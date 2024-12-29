package com.example.notesapp.view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.notesapp.model.Category

@Composable
fun Sidebar(
    items: List<Category>,
    onAddCategory: () -> Unit,
    onItemClick: (Category) -> Unit,
    onSideBarClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val expandedWidth = screenWidth * 0.8f

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures { onSideBarClose() }}
    ) {
        Box(
            modifier = modifier
                .width(expandedWidth)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .animateContentSize()
                .pointerInput(Unit) { detectTapGestures {  }}
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(text = "Note Categories")

                    IconButton(
                        onClick = { onSideBarClose() },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBackIosNew,
                            contentDescription = "close Categories view"
                        )
                    }
                }

                // Add Item button
                Button(
                    onClick = onAddCategory,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Item",
                        modifier = Modifier.size(24.dp)
                    )
                }

                // List of items
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(items) { item ->
                        SidebarItem(
                            text = item.name,
                            onClick = { onItemClick(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SidebarItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(MaterialTheme.shapes.medium),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
        }
    }
}

@Composable
fun  AddCategoryBox(
    addCategoryInput: String,
    onTextInputChange: (String) -> Unit,
    isChecked: Boolean,
    onCheckboxChange: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    // Box to position the popup in the center of the screen
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        // The background overlay (optional, you can remove this if you want a transparent background)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { onDismiss() } // dismiss popup when clicked outside
        )

        // Popup Box content
        Box(
            modifier = Modifier
                .wrapContentSize()
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .padding(16.dp)
                .pointerInput(Unit) {}
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                // Title
                Text(
                    text = "Add Category",
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Text input field
                TextField(
                    value = addCategoryInput,
                    onValueChange = { onTextInputChange(it) },
                    label = { Text("Enter Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Checkbox
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { onCheckboxChange(it) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("make private")
                }

                // Confirm Button
                Button(
                    onClick = {
                        onConfirm()
                        onDismiss() // Optionally dismiss the popup after confirmation
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Confirm")
                }
            }
        }
    }
}
