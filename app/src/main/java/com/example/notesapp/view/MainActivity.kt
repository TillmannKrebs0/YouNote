package com.example.notesapp.view

import com.example.notesapp.viewmodel.NotesViewModel
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

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
    val categoryInput by viewModel.categoryInput.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var inputFieldHeight by remember { mutableStateOf(70.dp) }
    var filterOpen by remember { mutableStateOf<Boolean>(false) }
    var noteOptionsOpen by remember { mutableStateOf<Boolean>(false) }
    var sideBarOpen by remember { mutableStateOf<Boolean>(false) }
    val categories by viewModel.categories.collectAsState()

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
                items = categories,
                onAddCategory = {
                    viewModel.addCategory()
                },
                onItemClick = { selectedItem ->
                    viewModel.setActiveCategory(selectedItem)
                    sideBarOpen = false
                } ,
                onSideBarClose = { sideBarOpen = false } ,
                addCategoryInput = categoryInput,
                onCategoryInputChange = { viewModel.onCategoryInputChanged(it)}
            )
        }
    }
}


