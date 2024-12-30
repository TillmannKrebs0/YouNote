package com.example.notesapp.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.unit.dp
import com.example.notesapp.model.Category
import com.example.notesapp.viewmodel.CategoryViewModel
import com.example.notesapp.viewmodel.NotesViewModel
import kotlinx.coroutines.launch


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
    var deleteConfirmOpen by remember { mutableStateOf(false) }

    var askPasswordOpen by remember { mutableStateOf(false) }
    var passwordInput by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }


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
                onTextChange = { notesViewModel.onSearchQueryChanged(it) },
                onFilterOpen = { filterOpen = !filterOpen },
                onSideBarOpen = { sideBarOpen = !sideBarOpen}
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
                    noteUiState.selectedNote?.let { notesViewModel.deleteNote(it) }
                    noteOptionsOpen = false
                    notesViewModel.clearSelectedNote()
                },
                onDismiss = {
                    notesViewModel.clearSelectedNote()
                    noteOptionsOpen = false },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        if (filterOpen) {
            FilterMenu(
                notesViewModel,
                onToggleFilter = {filterOpen = !filterOpen}
            )
        }
        if (sideBarOpen) {
            Sidebar(
                items = categories,
                onAddCategory = {
                    addCategoryBoxOpen = true
                },
                onItemClick = { category ->
                    if (category.isSecret) {
                        selectedCategory = category
                        askPasswordOpen = true
                    } else {
                        categoryViewModel.setActiveCategory(category)
                        sideBarOpen = false
                    }
                },
                onSideBarClose = { sideBarOpen = false } ,
            )
        }


        if (askPasswordOpen) {
            PasswordPopup(
                category = selectedCategory!!,
                password = passwordInput,
                onConfirm = {
                    if (passwordInput == selectedCategory?.password) {
                        selectedCategory?.let { categoryViewModel.setActiveCategory(it) }
                        sideBarOpen = false
                        askPasswordOpen = false
                        passwordInput = ""
                        selectedCategory = null
                    }
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
    }
}


