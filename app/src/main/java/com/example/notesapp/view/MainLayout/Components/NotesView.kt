package com.example.notesapp.view.MainLayout.Components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.notesapp.model.Note
import com.example.notesapp.viewmodel.NotesUiState

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NoteList(
    listState: LazyListState,
    notes: List<Note>,
    onNoteSelected: (Note) -> Unit,
    onToggleNoteOptions: () -> Unit,
    modifier: Modifier = Modifier,
    notesUiState: NotesUiState
) {
    LazyColumn(
        state = listState,
        modifier = modifier
    ) {
        items(notes) { currentNote ->
            NoteItem(
                note = currentNote,
                onLongPress = {
                    onNoteSelected(currentNote)
                    onToggleNoteOptions()
                },
                notesUiState = notesUiState
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NoteItem(
    note: Note,
    onLongPress: (Note) -> Unit,
    modifier: Modifier = Modifier,
    notesUiState: NotesUiState
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = if (note.isEdited) {
                        "Date: ${note.creationDate} | Edited"
                    } else {
                        "Date: ${note.creationDate}"
                    },
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 4.dp),
            color = MaterialTheme.colorScheme.onBackground
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 64.dp)
                .padding(4.dp)
                .pointerInput(note) {
                    detectTapGestures(
                        onLongPress = { onLongPress(note) }
                    )
                },
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = if (note == notesUiState.selectedNote) {
                    MaterialTheme.colorScheme.tertiary
                } else {
                    MaterialTheme.colorScheme.surface
                }
            )
        ) {
            Text(
                text = note.content,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
