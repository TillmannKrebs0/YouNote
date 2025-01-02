package com.example.notesapp.view

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
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
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.pointer.pointerInput
import com.example.notesapp.viewmodel.NotesViewModel

@Composable
fun ControlBar(
    modifier: Modifier = Modifier,
    onTextChange: (String) -> Unit,
    onFilterOpen: () -> Unit,
    onSideBarOpen: () -> Unit
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
                .weight(1f),
            singleLine = true,
            placeholder = { Text("Serch for Note") }
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

@Composable
fun FilterMenu(
    viewModel: NotesViewModel,
    onToggleFilter: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var currentDateOperation by remember { mutableStateOf<DateOperation>(DateOperation.None) }
    val noteUiState by viewModel.uiState.collectAsState()

    val showOldestFirst = noteUiState.showOldestFirst
    val onlyShowEdited = noteUiState.onlyShowEdited

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { onToggleFilter() }
            },
        contentAlignment = Alignment.TopEnd // Align dropdown to top-right
    ) {
        Card(
            modifier = Modifier
                .padding(top = 65.dp, end = 8.dp) // Adjust position relative to the filter button
                .width(200.dp) // Set width of the card
        ) {
            Column(modifier = Modifier
                .padding(8.dp)
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        viewModel.toggleShowOldestFirst()
                        onToggleFilter()
                }) {
                    Text(
                        text = if (showOldestFirst) {
                            "Show Newest First"
                        } else {
                            "Show Oldest First"
                        }
                    )
                }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        viewModel.toggleShowEdited()
                        onToggleFilter()
                }) {
                    Text(text =
                        if (onlyShowEdited) {
                            "Edited & Unedited"
                        } else {
                            "Edited Notes"
                        }
                    )
                }

                // TODO: Add color indication if following states are active
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        currentDateOperation = DateOperation.OnDate
                        showDatePicker = true
                }) {
                    Text(text = "Show on Date")
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        currentDateOperation = DateOperation.Before
                        showDatePicker = true
                }) {
                    Text(text = "Show until Date")
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        currentDateOperation = DateOperation.After
                        showDatePicker = true
                }) {
                    Text(text = "Show after Date")
                }



                HorizontalDivider(thickness = 2.dp, modifier = Modifier.padding(vertical = 4.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.resetFilters() }) {
                    Text(text = "Remove all Filters")
                }

                if (showDatePicker) {
                    DatePickerModal(
                        onDateSelected = { dateMillis ->
                            dateMillis?.let {
                                when (currentDateOperation) {
                                    DateOperation.OnDate -> viewModel.filterByDate(it)
                                    DateOperation.Before -> viewModel.filterBeforeDate(it)
                                    DateOperation.After -> viewModel.filterAfterDate(it)
                                    DateOperation.None -> { /* Should never happen */ }
                                }
                            }
                            currentDateOperation = DateOperation.None
                        },
                        onDismiss = {
                            showDatePicker = false
                            currentDateOperation = DateOperation.None
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()



    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}


private sealed class DateOperation {
    object None : DateOperation()
    object OnDate : DateOperation()
    object Before : DateOperation()
    object After : DateOperation()
}