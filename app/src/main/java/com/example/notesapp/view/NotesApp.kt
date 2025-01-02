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
import com.example.notesapp.viewmodel.CategoryViewModel
import com.example.notesapp.viewmodel.NotesViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotesApp(categoryViewModel: CategoryViewModel, notesViewModel: NotesViewModel) {
    val noteUiState by notesViewModel.uiState.collectAsState()
    val categoryUiState by categoryViewModel.uiState.collectAsState()

    val textInput = noteUiState.textInput
    val notes = noteUiState.notes
    val categories = categoryUiState.categories
    val categoryInput = categoryUiState.categoryInput
    val categoryPassword = categoryUiState.categoryPassword

    /// TODO: write own ViewModel for these States
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var inputFieldHeight by remember { mutableStateOf(70.dp) }
    var filterOpen by remember { mutableStateOf(false) }
    var noteOptionsOpen by remember { mutableStateOf(false) }
    var sideBarOpen by remember { mutableStateOf(false) }
    var addCategoryBoxOpen by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var categoryOptionsOpen by remember { mutableStateOf(false) }

    var askPasswordOpen by remember { mutableStateOf(false) }
    var passwordInput by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var pendingDeleteAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    val sidebarWidth = 260.dp // Sidebar width
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
                        // Update sidebar position based on drag
                        coroutineScope.launch {
                            val newOffset =
                                (sidebarOffset.value + dragAmount).coerceIn(-maxSidebarOffset, 0f)
                            sidebarOffset.snapTo(newOffset)
                        }
                        change.consume()
                    },
                    onDragEnd = {
                        // Smoothly animate to open or closed state
                        coroutineScope.launch {
                            if (sidebarOffset.value > -maxSidebarOffset * 0.75) {
                                // Open the sidebar
                                sidebarOffset.animateTo(
                                    0f,
                                    animationSpec = tween(durationMillis = 300)
                                )
                                sideBarOpen = true
                            } else {
                                // Close the sidebar
                                sidebarOffset.animateTo(
                                    -maxSidebarOffset,
                                    animationSpec = tween(durationMillis = 300)
                                )
                                sideBarOpen = false
                            }
                        }
                    }
                )
            }
    ) {
        // Main content
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
        ) {
            ControlBar(
                modifier = Modifier.fillMaxWidth(),
                onTextChange = { notesViewModel.onSearchQueryChanged(it) },
                onFilterOpen = { filterOpen = !filterOpen },
                onSideBarOpen = {
                    coroutineScope.launch {
                        sidebarOffset.animateTo(0f, animationSpec = tween(durationMillis = 300))
                        sideBarOpen = true
                    }
                }
            )

            NoteList(
                listState = listState,
                notes = notes,
                onNoteSelected = { note -> notesViewModel.selectNote(note)},
                onToggleNoteOptions = {noteOptionsOpen = !noteOptionsOpen},
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = inputFieldHeight)
                    .padding(top = 70.dp)
            )

            InputBar(
                textInput = textInput,
                onTextChange = { notesViewModel.onTextChanged(it) },
                onPostNote = { notesViewModel.handleNoteAction() },
                onHeightChanged = { newHeight -> inputFieldHeight = newHeight },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            )
        }

        // Overlay bottom card above everything
        if (noteOptionsOpen) {
            EditDeleteBottomCard(
                selectedNote = noteUiState.selectedNote,
                onEdit = {
                    noteOptionsOpen = false
                    noteUiState.selectedNote?.let { notesViewModel.onTextChanged(it.content) }
                },
                onDelete = {
                    pendingDeleteAction = {
                        noteUiState.selectedNote?.let { notesViewModel.deleteNote(it) }
                        noteOptionsOpen = false
                        notesViewModel.clearSelectedNote()
                    }

                    showDeleteConfirmation = true
                },
                onDismiss = {
                    notesViewModel.clearSelectedNote()
                    noteOptionsOpen = false },
                onChangeCategory = {/*todo: implement*/},
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        if (filterOpen) {
            FilterMenu(
                notesViewModel,
                onToggleFilter = {filterOpen = !filterOpen}
            )
        }

        // Overlay That closes Sidebar if Tap on Screen beneath
        if (sideBarOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        coroutineScope.launch {
                            sidebarOffset.animateTo(
                                -maxSidebarOffset,
                                animationSpec = tween(durationMillis = 300)
                            )
                            sideBarOpen = false
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
                items = categories,
                onAddCategory = { addCategoryBoxOpen = true },
                onItemClick = { category ->
                    if (category.isSecret) {
                        selectedCategory = category
                        askPasswordOpen = true
                    } else {
                        categoryViewModel.setActiveCategory(category)
                        coroutineScope.launch {
                            sidebarOffset.animateTo(-maxSidebarOffset, animationSpec = tween(durationMillis = 300))
                            sideBarOpen = false
                        }
                    }
                },
                onSideBarClose = {
                    coroutineScope.launch {
                        sidebarOffset.animateTo(-maxSidebarOffset, animationSpec = tween(durationMillis = 300))
                        sideBarOpen = false
                    }
                },
                onItemLongPress = {
                    category -> categoryViewModel.selectCategory(category)
                    categoryOptionsOpen = true
                }
            )
        }



        if (askPasswordOpen) {
            PasswordPopup(
                category = selectedCategory!!,
                password = passwordInput,
                onConfirm = {
                        categoryViewModel.setActiveCategory(selectedCategory!!)
                        sideBarOpen = false
                        askPasswordOpen = false
                        passwordInput = ""
                        selectedCategory = null
                },
                onDismiss = {
                    askPasswordOpen = false
                    passwordInput = ""
                    selectedCategory = null
                },
                onPasswordChange = { passwordInput = it }

            )
        }

        if (addCategoryBoxOpen) {
            AddCategoryBox(
                addCategoryInput = categoryInput,
                onTextInputChange = { categoryViewModel.onCategoryInputChanged(it) },
                categoryPassword = categoryPassword,
                onPasswordChange = { categoryViewModel.onCategoryPasswordChanged(it) },
                isChecked = categoryUiState.secretFlagSet,
                onCheckboxChecked = {categoryViewModel.toggleSecretFlag()},
                onDismiss = { addCategoryBoxOpen = false },
                onConfirm = { categoryViewModel.addCategory()}
            )
        }

        if (categoryOptionsOpen) {
            DeleteCategory(
                onDelete = {
                    pendingDeleteAction = {
                        categoryUiState.selectedCategory?.let { categoryViewModel.deleteCategory(it) }
                        categoryViewModel.clearSelectedCategory()
                        categoryOptionsOpen = false
                    }
                    showDeleteConfirmation = true
                },
                onDismiss = {
                    categoryViewModel.clearSelectedCategory()
                    categoryOptionsOpen = false
                }
            )
        }

        if (showDeleteConfirmation) {
            DeleteConfirmDialog(
                onConfirm = {
                    pendingDeleteAction?.invoke()  // Execute the stored delete action
                    pendingDeleteAction = null
                    showDeleteConfirmation = false
                },
                onDismiss = {
                    showDeleteConfirmation = false
                    pendingDeleteAction = null
                }
            )
        }
    }
}


