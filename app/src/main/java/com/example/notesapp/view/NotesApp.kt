package com.example.notesapp.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.notesapp.model.Category
import com.example.notesapp.viewmodel.CategoriesUiState
import com.example.notesapp.viewmodel.CategoryViewModel
import com.example.notesapp.viewmodel.NotesScreenState
import com.example.notesapp.viewmodel.NotesUiState
import com.example.notesapp.viewmodel.NotesViewModel
import com.example.notesapp.viewmodel.ScreenViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotesApp(
    categoryViewModel: CategoryViewModel,
    notesViewModel: NotesViewModel,
    screenViewModel: ScreenViewModel
) {
    val noteUiState by notesViewModel.uiState.collectAsState()
    val categoryUiState by categoryViewModel.uiState.collectAsState()
    val screenState by screenViewModel.uiState.collectAsState()

    val notes = noteUiState.notes
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val sidebarWidth = 260.dp
    val maxSidebarOffset = with(LocalDensity.current) { sidebarWidth.toPx() }
    val sidebarOffset = remember { Animatable(-maxSidebarOffset) } // Start hidden

    LaunchedEffect(notes.size) {
        if (notes.isNotEmpty()) {
            coroutineScope.launch { listState.scrollToItem(index = notes.size - 1) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { change, dragAmount ->
                        coroutineScope.launch {
                            val newOffset =
                                (sidebarOffset.value + dragAmount).coerceIn(-maxSidebarOffset, 0f)
                            sidebarOffset.snapTo(newOffset)
                        }
                        change.consume()
                    },
                    onDragEnd = {
                        coroutineScope.launch {
                            if (sidebarOffset.value > -maxSidebarOffset * 0.75) {
                                sidebarOffset.animateTo(0f, animationSpec = tween(durationMillis = 300))
                                screenViewModel.toggleSideBar()
                            } else {
                                sidebarOffset.animateTo(-maxSidebarOffset, animationSpec = tween(durationMillis = 300))
                                screenViewModel.toggleSideBar()
                            }
                        }
                    }
                )
            }
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            ControlBar(
                modifier = Modifier.fillMaxWidth(),
                onTextChange = { notesViewModel.onSearchQueryChanged(it) },
                onFilterOpen = { screenViewModel.toggleFilter() },
                onSideBarOpen = {
                    coroutineScope.launch {
                        sidebarOffset.animateTo(0f, animationSpec = tween(durationMillis = 300))
                        screenViewModel.toggleSideBar()
                    }
                }
            )

            NoteList(
                listState = listState,
                notes = notes,
                onNoteSelected = { note -> notesViewModel.selectNote(note) },
                onToggleNoteOptions = { screenViewModel.toggleNoteOptions() },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = screenState.inputFieldHeight)
                    .padding(top = 70.dp)
            )

            InputBar(
                textInput = noteUiState.textInput,
                onTextChange = { notesViewModel.onTextChanged(it) },
                onPostNote = { notesViewModel.handleNoteAction() },
                onHeightChanged = { newHeight -> screenViewModel.updateInputFieldHeight(newHeight) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            )
        }

        if (screenState.sideBarOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        coroutineScope.launch {
                            sidebarOffset.animateTo(-maxSidebarOffset, animationSpec = tween(durationMillis = 300))
                            screenViewModel.toggleSideBar()
                        }
                    }
            )
        }

        Box(
            modifier = Modifier
                .offset { IntOffset(sidebarOffset.value.roundToInt(), 0) }
                .width(sidebarWidth)
                .fillMaxSize()
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            Sidebar(
                items = categoryUiState.categories,
                onAddCategory = { screenViewModel.toggleAddCategoryBox() },
                onItemClick = { category ->
                    if (category.isSecret) {
                        screenViewModel.selectCategory(category)
                        screenViewModel.toggleAskPassword()
                    } else {
                        categoryViewModel.setActiveCategory(category)
                        coroutineScope.launch {
                            sidebarOffset.animateTo(-maxSidebarOffset, animationSpec = tween(durationMillis = 300))
                            screenViewModel.toggleSideBar()
                        }
                    }
                },
                onSideBarClose = {
                    coroutineScope.launch {
                        sidebarOffset.animateTo(-maxSidebarOffset, animationSpec = tween(durationMillis = 300))
                        screenViewModel.toggleSideBar()
                    }
                },
                onItemLongPress = { category ->
                    categoryViewModel.selectCategory(category)
                    screenViewModel.toggleCategoryOptions()
                }
            )
        }


        DialogHandler(
            screenState,
            noteUiState,
            screenViewModel,
            notesViewModel,
            categoryViewModel,
            categoryUiState
        )
    }
}

