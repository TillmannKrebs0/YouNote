package com.example.notesapp

import com.example.notesapp.viewmodel.NotesViewModel
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

import com.example.notesapp.model.Note

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Get the ViewModel instance
            val notesViewModel: NotesViewModel by viewModels()
            // Pass ViewModel to NotesApp
            NotesApp(viewModel = notesViewModel)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotesApp(viewModel: NotesViewModel) {
    val textInput by viewModel.textInput.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var inputFieldHeight by remember { mutableStateOf(70.dp) }
    var searchQuery by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        ControlBar(
            modifier = Modifier.fillMaxWidth(),
            onTextChange = { query ->
                searchQuery = query
            }
        )

        NoteList(
            listState = listState,
            notes = notes,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = inputFieldHeight)
                .padding(top = 70.dp)
        )

        NoteInputField(
            textInput = textInput,
            onTextChange = { viewModel.onTextChanged(it) },
            onPostNote = {
                viewModel.addNote()
                coroutineScope.launch {
                    listState.animateScrollToItem(
                        index = notes.size - 1,
                        scrollOffset = 0
                    )
                }
            },
            onHeightChanged = { newHeight -> inputFieldHeight = newHeight },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )
    }
}


@Composable
fun ControlBar(
    modifier: Modifier = Modifier,
    onTextChange: (String) -> Unit
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

        Button(onClick = { /*TODO*/ }) {
            Icon(
                Icons.Rounded.Settings,
                contentDescription = "set Filter"
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NoteList(
    listState: LazyListState,
    notes: List<Note>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        state = listState,
        modifier = modifier
    ) {
        items(notes) { currentNote ->
            NoteItem(note = currentNote)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NoteItem(note: Note, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp) // External padding for the entire NoteItem
    ) {
        // Display the creation date above the card
        Text(
            text = "Datum: ${note.creationDate}",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 4.dp) // Space between date and card
        )
        // Card for the note content
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 64.dp) // Set minimum height for the card
                .padding(4.dp), // Space between card and surrounding elements
            elevation = CardDefaults.cardElevation(8.dp),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            // Text inside the card
            Text(
                text = "Inhalt: ${note.content}",
                modifier = Modifier.padding(16.dp), // Padding inside the card
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun NoteInputField(
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