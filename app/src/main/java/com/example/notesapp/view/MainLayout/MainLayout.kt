package com.example.notesapp.view.MainLayout

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.notesapp.model.Note
import com.example.notesapp.view.MainLayout.Components.ControlBar
import com.example.notesapp.view.MainLayout.Components.InputBar
import com.example.notesapp.view.MainLayout.Components.NoteList
import com.example.notesapp.view.CategoryMenu.SidebarConfiguration
import com.example.notesapp.viewmodel.CategoryViewModel
import com.example.notesapp.viewmodel.NotesScreenState
import com.example.notesapp.viewmodel.NotesUiState
import com.example.notesapp.viewmodel.CategoriesUiState
import com.example.notesapp.viewmodel.NotesViewModel
import com.example.notesapp.viewmodel.ScreenViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Handles scrolling to the last note in the list when the notes list size changes.
 *
 * @param notes List of notes to display.
 * @param listState State object for controlling list scrolling.
 * @param coroutineScope Scope for launching coroutines.
 */
@Composable
fun HandleNotesScrolling(
    notes: List<Note>,
    listState: LazyListState,
    coroutineScope: CoroutineScope
) {
    LaunchedEffect(notes.size) {
        if (notes.isNotEmpty()) {
            coroutineScope.launch {
                listState.scrollToItem(index = notes.size - 1)
            }
        }
    }
}

@Composable
fun NotesAppLayout(
    modifier: Modifier = Modifier,
    sidebarConfig: SidebarConfiguration,
    coroutineScope: CoroutineScope,
    screenViewModel: ScreenViewModel,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .then(
            Modifier.pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { change, dragAmount ->
                        coroutineScope.launch {
                            val newOffset = (sidebarConfig.offset.value + dragAmount)
                                .coerceIn(-sidebarConfig.maxOffset, 0f)
                            sidebarConfig.offset.snapTo(newOffset)
                        }
                        change.consume()
                    },
                    onDragEnd = {
                        coroutineScope.launch {
                            val targetOffset = if (sidebarConfig.offset.value > -sidebarConfig.maxOffset * 0.75) {
                                0f
                            } else {
                                -sidebarConfig.maxOffset
                            }
                            sidebarConfig.offset.animateTo(
                                targetOffset,
                                animationSpec = tween(durationMillis = 300)
                            )
                            screenViewModel.toggleSideBar()
                        }
                    }
                )
            }
        )
    ) {
        content()
    }
}

/**
 * Main content area of the Notes app, including the control bar, note list, and input bar.
 *
 * @param noteUiState State of the notes UI, including the list of notes and user input.
 * @param screenState State of the screen, including UI component visibility and dimensions.
 * @param listState State object for controlling note list scrolling.
 * @param notesViewModel ViewModel for managing notes-related actions and state.
 * @param screenViewModel ViewModel for managing screen-related state and actions.
 * @param sidebarConfig Configuration for the sidebar's offset and behavior.
 * @param coroutineScope Scope for managing coroutine-based actions.
 * @param categoryUiState State of the category UI.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotesMainContent(
    noteUiState: NotesUiState,
    screenState: NotesScreenState,
    listState: LazyListState,
    notesViewModel: NotesViewModel,
    screenViewModel: ScreenViewModel,
    sidebarConfig: SidebarConfiguration,
    coroutineScope: CoroutineScope,
    categoryUiState: CategoriesUiState
) {
    Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        // Control bar at the top of the screen for search, filter and navigation.
        ControlBar(
            onTextChange = { notesViewModel.onSearchQueryChanged(it) },
            onFilterOpen = { screenViewModel.toggleFilter() },
            onSideBarOpen = {
                coroutineScope.launch {
                    sidebarConfig.offset.animateTo(
                        0f,
                        animationSpec = tween(durationMillis = 300)
                    )
                    screenViewModel.toggleSideBar()
                }
            },
        )

        // List of notes displayed in the main content area.
        NoteList(
            listState = listState,
            notes = noteUiState.notes,
            onNoteSelected = { note -> notesViewModel.selectNote(note) },
            onToggleNoteOptions = { screenViewModel.toggleNoteOptions() },
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = screenState.inputFieldHeight)
                .padding(top = 70.dp),
            notesUiState = noteUiState
        )

        // Input bar at the bottom of the screen for adding new notes.
        InputBar(
            textInput = noteUiState.textInput,
            onTextChange = { notesViewModel.onTextChanged(it) },
            onPostNote = { notesViewModel.handleNoteAction() },
            onHeightChanged = { newHeight -> screenViewModel.updateInputFieldHeight(newHeight) },
            categoriesUiState = categoryUiState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )
    }
}