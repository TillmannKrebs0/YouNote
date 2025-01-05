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

/**
 * A composable that displays a scrollable list of notes using LazyColumn.
 * Handles note selection and option toggles through callbacks.
 *
 * @param listState Controls the scroll state of the list
 * @param notes List of notes to display
 * @param onNoteSelected Callback triggered when a note is selected
 * @param onToggleNoteOptions Callback to show/hide note options menu
 * @param notesUiState Current UI state containing selected note information
 */
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

/**
 * A composable that renders an individual note card.
 * Displays creation date, edited status, and note content.
 * Supports long press interaction for note selection.
 * Visual feedback is provided through different background colors for selected state.
 *
 * @param note The note to be displayed
 * @param onLongPress Callback triggered on long press gesture
 * @param notesUiState Current UI state containing selected note information
 */
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
