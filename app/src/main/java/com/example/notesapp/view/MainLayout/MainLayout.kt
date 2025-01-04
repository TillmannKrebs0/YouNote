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