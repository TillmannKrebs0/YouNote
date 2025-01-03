package com.example.notesapp.view.Dialogs

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import com.example.notesapp.view.Dialogs.Components.AddCategoryBox
import com.example.notesapp.view.Dialogs.Components.DeleteCategory
import com.example.notesapp.view.Dialogs.Components.DeleteConfirmDialog
import com.example.notesapp.view.Dialogs.Components.EditDeleteBottomCard
import com.example.notesapp.view.Dialogs.Components.FilterMenu
import com.example.notesapp.view.Dialogs.Components.PasswordPopup
import com.example.notesapp.viewmodel.CategoriesUiState
import com.example.notesapp.viewmodel.CategoryViewModel
import com.example.notesapp.viewmodel.NotesScreenState
import com.example.notesapp.viewmodel.NotesUiState
import com.example.notesapp.viewmodel.NotesViewModel
import com.example.notesapp.viewmodel.ScreenViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DialogHandler(
    screenState: NotesScreenState,
    noteUiState: NotesUiState,
    screenViewModel: ScreenViewModel,
    notesViewModel: NotesViewModel,
    categoryViewModel: CategoryViewModel,
    categoryUiState: CategoriesUiState
) {
    if (screenState.noteOptionsOpen) {
        EditDeleteBottomCard(
            selectedNote = noteUiState.selectedNote,
            onEdit = {
                screenViewModel.toggleNoteOptions()
                noteUiState.selectedNote?.let { notesViewModel.onTextChanged(it.content) }
            },
            onDelete = {
                screenViewModel.setPendingDeleteAction {
                    noteUiState.selectedNote?.let { notesViewModel.deleteNote(it) }
                    screenViewModel.toggleNoteOptions()
                    notesViewModel.clearSelectedNote()
                }
                screenViewModel.toggleDeleteConfirmation()
            },
            onDismiss = {
                notesViewModel.clearSelectedNote()
                screenViewModel.toggleNoteOptions()
            },
            onChangeCategory = {/* TODO: implement */ },
        )
    }

    if (screenState.filterOpen) {
        FilterMenu(
            notesViewModel,
            onToggleFilter = { screenViewModel.toggleFilter() }
        )
    }


    if (screenState.askPasswordOpen) {
        PasswordPopup(
            category = screenState.selectedCategory!!,
            password = screenState.passwordInput,
            onConfirm = {
                categoryViewModel.setActiveCategory(screenState.selectedCategory!!)
                screenViewModel.toggleSideBar()
                screenViewModel.toggleAskPassword()
                screenViewModel.updatePasswordInput("")
                screenViewModel.selectCategory(null)
            },
            onDismiss = {
                screenViewModel.toggleAskPassword()
                screenViewModel.updatePasswordInput("")
                screenViewModel.selectCategory(null)
            },
            onPasswordChange = { screenViewModel.updatePasswordInput(it) }
        )
    }

    if (screenState.addCategoryBoxOpen) {
        AddCategoryBox(
            addCategoryInput = categoryUiState.categoryInput,
            onTextInputChange = { categoryViewModel.onCategoryInputChanged(it) },
            categoryPassword = categoryUiState.categoryPassword,
            onPasswordChange = { categoryViewModel.onCategoryPasswordChanged(it) },
            isChecked = categoryUiState.secretFlagSet,
            onCheckboxChecked = { categoryViewModel.toggleSecretFlag() },
            onDismiss = { screenViewModel.toggleAddCategoryBox() },
            onConfirm = { categoryViewModel.addCategory() }
        )
    }

    if (screenState.categoryOptionsOpen) {
        DeleteCategory(
            onDelete = {
                screenViewModel.setPendingDeleteAction {
                    categoryUiState.selectedCategory?.let { categoryViewModel.deleteCategory(it) }
                    categoryViewModel.clearSelectedCategory()
                    screenViewModel.toggleCategoryOptions()
                }
                screenViewModel.toggleDeleteConfirmation()
            },
            onDismiss = {
                categoryViewModel.clearSelectedCategory()
                screenViewModel.toggleCategoryOptions()
            }
        )
    }

    if (screenState.showDeleteConfirmation) {
        DeleteConfirmDialog(
            onConfirm = {
                screenViewModel.executePendingDeleteAction()
                screenViewModel.toggleDeleteConfirmation()
            },
            onDismiss = {
                screenViewModel.toggleDeleteConfirmation()
                screenViewModel.setPendingDeleteAction(null)
            }
        )
    }
}
