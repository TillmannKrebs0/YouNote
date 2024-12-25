package com.example.notesapp.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Settings
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.pointer.pointerInput
import com.example.notesapp.model.Note
import com.example.notesapp.viewmodel.NotesViewModel

@Composable
fun ControlBar(
    modifier: Modifier = Modifier,
    onTextChange: (String) -> Unit,
    onFilterOpen: () -> Unit
) {
    var search by remember { mutableStateOf("") }

    Row {
        Button(onClick = { /*TODO*/ }) {
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
            modifier = Modifier.weight(1f),
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
                .padding(top = 70.dp, end = 16.dp) // Adjust position relative to the filter button
                .width(200.dp) // Set width of the card
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Button(onClick = {
                    viewModel.showOldestFirst()
                    onToggleFilter()
                }) {
                    Text(text = "Change Note order")
                }
            }
        }
    }
}
