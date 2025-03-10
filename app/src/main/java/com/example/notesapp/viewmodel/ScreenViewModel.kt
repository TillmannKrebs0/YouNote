package com.example.notesapp.viewmodel

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.notesapp.model.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Represents the complete UI state for the Notes screen.
 * Manages visibility states for various UI components and dialogs,
 * tracks user inputs, and handles temporary state for delete operations.
 *
 * @property inputFieldHeight Current height of the input field
 * @property filterOpen Controls visibility of the filter dialog
 * @property noteOptionsOpen Controls visibility of note options menu
 * @property sideBarOpen Controls visibility of the navigation sidebar
 * @property addCategoryBoxOpen Controls visibility of add category dialog
 * @property showDeleteConfirmation Controls visibility of delete confirmation dialog
 * @property categoryOptionsOpen Controls visibility of category options menu
 * @property askPasswordOpen Controls visibility of password prompt
 * @property passwordInput Current password input value
 * @property selectedCategory Currently selected category
 * @property pendingDeleteAction Stores a delete operation that requires confirmation
 */

data class NotesScreenState(
    val inputFieldHeight: Dp = 70.dp,
    val filterOpen: Boolean = false,
    val noteOptionsOpen: Boolean = false,
    val sideBarOpen: Boolean = false,
    val addCategoryBoxOpen: Boolean = false,
    val showDeleteConfirmation: Boolean = false,
    val categoryOptionsOpen: Boolean = false,
    val askPasswordOpen: Boolean = false,
    val passwordInput: String = "",
    val selectedCategory: Category? = null,
    val pendingDeleteAction: (() -> Unit)? = null
)

/**
 * ViewModel responsible for managing the Notes screen state.
 * Provides methods to update various UI states and handle user interactions.
 * Uses StateFlow to expose immutable state to the UI while maintaining
 * encapsulation of the mutable state.
 */
class ScreenViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(NotesScreenState())
    val uiState: StateFlow<NotesScreenState> = _uiState.asStateFlow()

    fun updateInputFieldHeight(height: Dp) {
        _uiState.update { it.copy(inputFieldHeight = height) }
    }

    fun toggleFilter() {
        _uiState.update { it.copy(filterOpen = !it.filterOpen) }
    }

    fun toggleNoteOptions() {
        _uiState.update { it.copy(noteOptionsOpen = !it.noteOptionsOpen) }
    }

    fun toggleSideBar() {
        _uiState.update { it.copy(sideBarOpen = !it.sideBarOpen) }
    }

    fun toggleAddCategoryBox() {
        _uiState.update { it.copy(addCategoryBoxOpen = !it.addCategoryBoxOpen) }
    }

    fun toggleDeleteConfirmation() {
        _uiState.update { it.copy(showDeleteConfirmation = !it.showDeleteConfirmation) }
    }

    fun toggleCategoryOptions() {
        _uiState.update { it.copy(categoryOptionsOpen = !it.categoryOptionsOpen) }
    }

    fun toggleAskPassword() {
        _uiState.update { it.copy(askPasswordOpen = !it.askPasswordOpen) }
    }

    fun updatePasswordInput(password: String) {
        _uiState.update { it.copy(passwordInput = password) }
    }

    fun selectCategory(category: Category?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun setPendingDeleteAction(action: (() -> Unit)?) {
        _uiState.update { it.copy(pendingDeleteAction = action) }
    }

    fun executePendingDeleteAction() {
        _uiState.value.pendingDeleteAction?.invoke()
        _uiState.update { it.copy(pendingDeleteAction = null, showDeleteConfirmation = false) }
    }
}
