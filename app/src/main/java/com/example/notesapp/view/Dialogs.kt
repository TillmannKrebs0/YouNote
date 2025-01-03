package com.example.notesapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.SubdirectoryArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.notesapp.model.Category
import com.example.notesapp.model.Note

@Composable
fun EditDeleteBottomCard(
    selectedNote: Note?,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
    onChangeCategory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onDismiss() })
            }
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.32f),
        ) {}

        Card(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onEdit,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Edit")
                }
                Button(
                    onClick = onDelete,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Delete")
                }
                Button(
                    onClick = onChangeCategory,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.SubdirectoryArrowRight,
                        contentDescription = "Delete",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Change Category")
                }
            }
        }
    }
}


@Composable
fun DeleteConfirmDialog(
    title: String = "Confirm Delete",
    message: String = "Are you sure you want to delete this item? This action cannot be undone.",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm()
                        onDismiss()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }


@Composable
fun AddCategoryBox(
    addCategoryInput: String,
    categoryPassword: String,
    isChecked: Boolean,
    onTextInputChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onCheckboxChecked: () -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    var passwordConfirm by remember { mutableStateOf("") }
    var passwordsMatch by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        // Background overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { onDismiss() }
        )

        // Popup Box content
        Box(
            modifier = Modifier
                .wrapContentSize()
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .padding(16.dp)
                .pointerInput(Unit) {
                    detectTapGestures { } // Prevent clicks from propagating to the background
                }
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Add Category",
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                TextField(
                    value = addCategoryInput,
                    onValueChange = { onTextInputChange(it) },
                    label = { Text("Enter Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { onCheckboxChecked() }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Make Private")
                }

                // Show password fields only when checkbox is checked
                if (isChecked) {
                    Text(
                        text = "Enter a Password",
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    TextField(
                        value = categoryPassword,
                        onValueChange = {
                            onPasswordChange(it)
                        },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Confirm Password",
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    TextField(
                        value = passwordConfirm,
                        onValueChange = {
                            passwordConfirm = it
                        },
                        label = { Text("Confirm Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        isError = !passwordsMatch
                    )

                    if (!passwordsMatch) {
                        Text(
                            text = "Passwords do not match",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        passwordsMatch = categoryPassword == passwordConfirm

                        if (!isChecked or  isChecked && passwordsMatch) {
                            onConfirm()
                            onDismiss()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = addCategoryInput.isNotBlank() && (!isChecked || (categoryPassword.isNotBlank()))
                ) {
                    Text("Confirm")
                }
            }
        }
    }
}


@Composable
fun PasswordPopup(
    category: Category,
    password: String,
    onPasswordChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var passwordsMatch by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Background overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { onDismiss() }
        )

        // Popup content
        Box(
            modifier = Modifier
                .wrapContentSize()
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .padding(16.dp)
                .pointerInput(Unit) {
                    // Prevent clicks from propagating to background
                    detectTapGestures { }
                }
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Enter Password",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Category: ${category.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                TextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    isError = !passwordsMatch
                )

                if (!passwordsMatch) {
                    Text(
                        text = "Passwords do not match",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text("Cancel")
                    }

                    category.password?.let {
                        Button(


                            onClick =
                            {
                                if (category.password == password) {
                                    onConfirm()
                                } else {
                                    passwordsMatch = false
                                }
                            },
                            enabled = it.isNotBlank()
                        ) {
                            Text("Unlock")
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun DeleteCategory(
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onDismiss() })
            }
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.32f),
        ) {}

        Card(
            modifier = modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = {
                        onDelete()
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Delete")
                }
            }
        }
    }
}
