package com.example.notesapp.view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notesapp.model.Category
import com.example.notesapp.model.Note

@Composable
fun Sidebar(
    items: List<Category>,
    onAddCategory: () -> Unit,
    onItemClick: (Category) -> Unit,
    onSideBarClose: () -> Unit,
    modifier: Modifier = Modifier,
    onItemLongPress: (Category) -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val expandedWidth = screenWidth * 0.8f

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures { onSideBarClose() } }
    ) {
        Box(
            modifier = modifier
                .width(expandedWidth)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .animateContentSize()
                .pointerInput(Unit) { detectTapGestures { } }
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Categories" ,
                        style = TextStyle(fontSize = 26.sp),
                        modifier = Modifier
                            .padding(8.dp)
                    )

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

                HorizontalDivider(Modifier.height(2.dp))

                // Add Item button
                Button(
                    onClick = onAddCategory,
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 16.dp)
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
                            category = item,
                            onClick = { onItemClick(item) },
                            onLongPress = { onItemLongPress(item) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SidebarItem(
    category: Category,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongPress: (Category) -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(MaterialTheme.shapes.medium)
            .combinedClickable( // Supports both click and long press
                onClick = onClick,
                onLongClick = { onLongPress(category) }
            ),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (category.isSecret) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "locked",
                    modifier = Modifier.padding(end = 8.dp)
                )
            } else
            {
                Icon(
                    imageVector = Icons.Default.LockOpen,
                    contentDescription = "locked",
                    modifier = Modifier.padding(end = 8.dp).graphicsLayer(alpha = 1f) // change to 0f if want to change to transparent
                )
            }
            Text(
                text = category.name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}