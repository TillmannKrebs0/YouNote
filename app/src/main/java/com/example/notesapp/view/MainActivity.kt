package com.example.notesapp.view

import com.example.notesapp.viewmodel.NotesViewModel
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
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
    var filterOpen by remember { mutableStateOf<Boolean>(false) }
    var noteOptionsOpen by remember { mutableStateOf<Boolean>(false) }
    var sideBarOpen by remember { mutableStateOf<Boolean>(false) }

    var items by remember { mutableStateOf(listOf("Item 1", "Item 2", "Item 3")) }

    LaunchedEffect(notes.size) {
        if (notes.isNotEmpty()) {
            coroutineScope.launch { listState.scrollToItem(index = notes.size - 1) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Main content
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
        ) {
            ControlBar(
                modifier = Modifier.fillMaxWidth(),
                onTextChange = { viewModel.onSearchQueryChanged(it) },
                onFilterOpen = { filterOpen = !filterOpen },
                onSideBarOpen = { sideBarOpen = !sideBarOpen}
            )

            NoteList(
                listState = listState,
                notes = notes,
                onNoteSelected = { note -> viewModel.selectedNote = note },
                onToggleNoteOptions = {noteOptionsOpen = !noteOptionsOpen},
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = inputFieldHeight)
                    .padding(top = 70.dp)
            )

            InputBar(
                textInput = textInput,
                onTextChange = { viewModel.onTextChanged(it) },
                onPostNote = { viewModel.handleNoteAction() },
                onHeightChanged = { newHeight -> inputFieldHeight = newHeight },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            )
        }

        // Overlay bottom card above everything
        if (noteOptionsOpen) {
            EditDeleteBottomCard(
                selectedNote = viewModel.selectedNote,
                onEdit = {
                    noteOptionsOpen = false
                    viewModel.selectedNote?.let { viewModel.onTextChanged(it.content) }
                },
                onDelete = {
                    viewModel.selectedNote?.let { viewModel.deleteNote(it) }
                    noteOptionsOpen = false
                    viewModel.selectedNote = null
                },
                onDismiss = { viewModel.selectedNote = null },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        if (filterOpen) {
            FilterMenu(
                viewModel,
                onToggleFilter = {filterOpen = !filterOpen}
            )
        }
        if (sideBarOpen) {
            Sidebar(
                items = items,
                onAddItem = {
                    items = items + "Item ${items.size + 1}"
                },
                onItemClick = { selectedItem ->
                    // Handle item click
                } ,
                onSideBarClose = { sideBarOpen = !sideBarOpen}
            )
        }
    }
}


