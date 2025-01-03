package com.example.notesapp.viewmodel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.notesapp.model.Note
import com.example.notesapp.data.NoteRepository
import com.example.notesapp.model.Category
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class NotesUiState(
    val notes: List<Note> = emptyList(),
    val textInput: String = "",
    val searchQuery: String = "",
    val selectedNote: Note? = null,
    val showOldestFirst: Boolean = false,
    val onlyShowEdited: Boolean = false,
    val showNotesAtDate:  String? = null,
    val showNotesAfterDate:  String? = null,
    val showNotesBeforeDate:  String? = null
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
            val state = _uiState.value

            val notes = noteRepository.getNotesByFilters(
                    categoryID = category.id,
                    onlyEdited =  state.onlyShowEdited,
                    showOldestFirst = state.showOldestFirst,
                    searchQuery = state.searchQuery,
                    onDate = state.showNotesAtDate,
                    beforeDate = state.showNotesBeforeDate,
                    afterDate = state.showNotesAfterDate
            )

            _uiState.value = _uiState.value.copy(notes = notes)
        }
    }


    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        reloadNotes()
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
                    isSecret = false,
                    isEdited = false
                )
                noteRepository.insert(newNote)
                reloadNotes()
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
                isEdited = true,
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
        reloadNotes()
        _uiState.value = _uiState.value.copy(
            selectedNote = null,
            textInput = ""
        )
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteRepository.delete(note)
            reloadNotes()
        }
    }



    fun selectNote(note: Note?) {
        _uiState.value = _uiState.value.copy(selectedNote = note)
    }

    fun clearSelectedNote() {
        _uiState.value = _uiState.value.copy(selectedNote = null)
    }

    fun toggleShowOldestFirst() {
        _uiState.value = _uiState.value.copy(showOldestFirst = !uiState.value.showOldestFirst)
        reloadNotes()
    }

    fun toggleShowEdited() {
        _uiState.value = _uiState.value.copy(onlyShowEdited = !uiState.value.onlyShowEdited)
        reloadNotes()
    }

    fun filterByDate(selectedDate: Long) {
        val date = Date(selectedDate)
        val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)

        _uiState.value = _uiState.value.copy(showNotesAtDate = formattedDate)

        reloadNotes()
    }

    fun resetFilters() {
        _uiState.value = _uiState.value.copy(
            searchQuery = "",
            showOldestFirst = false,
            onlyShowEdited = false,
            showNotesAtDate= null,
            showNotesBeforeDate = null,
            showNotesAfterDate = null
        )
        reloadNotes()
    }

    private fun reloadNotes() {
        categoryViewModel.uiState.value.activeCategory?.let { category ->
            loadNotesForCategory(category)
        }
    }

    fun filterBeforeDate(selectedDate: Long) {
        val date = Date(selectedDate)
        val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)



        _uiState.value = _uiState.value.copy(
            showNotesBeforeDate = formattedDate,
            showNotesAtDate = null,
            showNotesAfterDate = null
        )

        reloadNotes()
    }

    fun filterAfterDate(selectedDate: Long) {
        val date = Date(selectedDate)
        val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)

        _uiState.value = _uiState.value.copy(
            showNotesAfterDate = formattedDate,
            showNotesAtDate = null,
            showNotesBeforeDate = null

        )

        reloadNotes()
    }

}
