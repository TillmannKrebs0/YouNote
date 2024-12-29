package com.example.notesapp.viewmodel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.data.CategoryRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.notesapp.model.Note
import com.example.notesapp.data.NoteRepository
import com.example.notesapp.model.Category
import com.example.notesapp.data.YouNoteDatabase
import kotlinx.coroutines.flow.first

data class NotesUiState(
    val notes: List<Note> = emptyList(),
    val textInput: String = "",
    val searchQuery: String = "",
    val selectedNote: Note? = null
)

class NotesViewModel(
    application: Application,
    private val noteRepository: NoteRepository,
    private val categoryViewModel: CategoryViewModel
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> = _uiState


    init {
        viewModelScope.launch {
            // Observe active category changes and update notes accordingly
            categoryViewModel.uiState.collect { categoriesState ->
                categoriesState.activeCategory?.let { category ->
                    loadNotesForCategory(category)
                }
            }
        }
    }

    private fun loadNotesForCategory(category: Category) {
        viewModelScope.launch {
            val notes = if (_uiState.value.searchQuery.isBlank()) {
                noteRepository.getAllNotesOfCategory(category.id)
            } else {
                noteRepository.getFilteredNotesOfCategory(_uiState.value.searchQuery, category.id)
            }
            _uiState.value = _uiState.value.copy(notes = notes)
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        categoryViewModel.uiState.value.activeCategory?.let { loadNotesForCategory(it) }
    }

    fun onTextChanged(text: String) {
        _uiState.value = _uiState.value.copy(textInput = text)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addNote() {
        val currentState = _uiState.value
        if (currentState.textInput.isBlank()) return

        viewModelScope.launch {
            categoryViewModel.uiState.value.activeCategory?.let { category ->
                val newNote = Note(
                    content = currentState.textInput,
                    categoryId = category.id,
                    isSecret = false
                )
                noteRepository.insert(newNote)
                loadNotesForCategory(category)
                _uiState.value = _uiState.value.copy(textInput = "")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun handleNoteAction() {
        val currentState = _uiState.value

        viewModelScope.launch {
            if (currentState.selectedNote != null) {
                updateNoteContent()
            } else {
                addNote()
            }
        }
    }

    private fun updateNoteContent() {
        val currentState = _uiState.value

        if (currentState.textInput.isBlank()) {
            currentState.selectedNote?.let { deleteNote(it) }
            return
        }

        viewModelScope.launch {
            val updatedNote = currentState.selectedNote!!.copy(
                content = currentState.textInput,
            )
            noteRepository.update(updatedNote)

            _uiState.value = _uiState.value.copy(
                selectedNote = null,
                textInput = ""
            )
            refreshNotes()
        }
    }

    private fun refreshNotes() {
        categoryViewModel.uiState.value.activeCategory?.let { category ->
            loadNotesForCategory(category)
        }
        _uiState.value = _uiState.value.copy(
            selectedNote = null,
            textInput = ""
        )
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteRepository.delete(note)
            categoryViewModel.uiState.value.activeCategory?.let { category ->
                loadNotesForCategory(category)
            }
        }
    }

    fun showOldestFirst() {
        _uiState.value = _uiState.value.copy(
            notes = _uiState.value.notes.reversed()
        )
    }

    fun selectNote(note: Note?) {
        _uiState.value = _uiState.value.copy(selectedNote = note)
    }

    fun clearSelectedNote() {
        _uiState.value = _uiState.value.copy(selectedNote = null)
    }

}
