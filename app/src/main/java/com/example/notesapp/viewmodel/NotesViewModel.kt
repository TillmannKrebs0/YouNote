package com.example.notesapp.viewmodel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.notesapp.model.Note
import com.example.notesapp.data.NoteRepository
import com.example.notesapp.data.YouNoteDatabase

class NotesViewModel(application: Application) : AndroidViewModel(application) {

    private val noteDao = YouNoteDatabase.getDatabase(application).noteDao()
    private val repository = NoteRepository(noteDao)

    private val _textInput = MutableStateFlow("")
    val textInput: StateFlow<String> = _textInput

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes

    var selectedNote: Note? = null


    init {
        getAllNotes()
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
        getFilteredNotes()
    }

    fun onTextChanged(newText: String) {
        _textInput.value = newText
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addNote() {
        if (_textInput.value.isBlank()) {
            return
        }

        viewModelScope.launch {
            // Create new note
            val newNote = Note(
                content = _textInput.value,
                category = "cat1",
                isSecret = false
            )
            repository.insert(newNote)

            refreshNotes()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun handleNoteAction() {
        viewModelScope.launch {
            if (selectedNote != null) {
                updateNoteContent()
            } else {
                addNote()
            }
        }
    }


    fun updateNoteContent() {
        if (_textInput.value.isBlank()) {
            selectedNote?.let { deleteNote(it) }
        }

        val updatedNote = selectedNote!!.copy(
            content = _textInput.value,
        )
        viewModelScope.launch {
            repository.update(updatedNote)
        }
        selectedNote = null
        refreshNotes()
    }

    private fun refreshNotes() {
        getFilteredNotes()
        _textInput.value = "" // Clear input field
    }



    fun deleteNote(note:Note) {
        viewModelScope.launch {
            repository.delete(note)
            getFilteredNotes()
        }
    }

    fun showOldestFirst() {
        _notes.value = _notes.value.reversed()
    }

    private fun getAllNotes() {
        // Fetch all notes from the repository asynchronously
        viewModelScope.launch {
            _notes.value = repository.getAllNotes()
        }
    }

    private fun getFilteredNotes() {
        viewModelScope.launch {
            if (searchQuery.value.isBlank()) {
                _notes.value = repository.getAllNotes()
            }
            else {
                _notes.value = repository.getFilteredNotes(searchQuery.value)
            }
        }
    }

}
