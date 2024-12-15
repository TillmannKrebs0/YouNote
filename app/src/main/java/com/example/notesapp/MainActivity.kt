package com.example.notesapp

import NotesViewModel
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notesapp.ui.theme.NotesAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

import com.example.notesapp.Note

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

    // Use a Box to layer the note list and input field
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Note list occupies the entire screen but does not overlap the input field
        NoteList(
            listState = listState,
            notes = notes,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp) // Reserve space for the input field
        )

        // Input field anchored to the bottom
        NoteInputField(
            textInput = textInput,
            onTextChange = { viewModel.onTextChanged(it) },
            onPostNote = {
                viewModel.addNote()
                coroutineScope.launch {
                    listState.animateScrollToItem(0) // Scroll to top when new note is added
                }

            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )
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
        reverseLayout = true,
        modifier = modifier
    ) {
        items(notes) { currentNote ->
            NoteItem(note = currentNote)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NoteItem(note: Note) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(text = "Inhalt: ${note.content}")
        Text(text = "Datum: ${note.creationDate}", style = MaterialTheme.typography.bodySmall)
        Divider()
    }
}

@Composable
fun NoteInputField(
    textInput: String,
    onTextChange: (String) -> Unit,
    onPostNote: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(vertical = 8.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = textInput,
            onValueChange = onTextChange,
            modifier = Modifier.weight(1f),
            singleLine = true,
            placeholder = { Text("Enter note...") }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = onPostNote) {
            Text("Send")
        }
    }
}
