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
import com.example.notesapp.data.YouNoteDatabase
import kotlinx.coroutines.flow.collect

class NotesViewModel(application: Application) : AndroidViewModel(application) {

    private val noteDao = YouNoteDatabase.getDatabase(application).noteDao()
    private val repository = NoteRepository(noteDao)

    private val _textInput = MutableStateFlow("")
    val textInput: StateFlow<String> = _textInput

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes

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
        if (_textInput.value.isNotBlank()) {
            // Create new note
            val newNote = Note(
                content = _textInput.value,
                category = "cat1", // You can update this to use dynamic categories if needed
                isSecret = false
            )
            // Use the repository to insert the note into the database
            viewModelScope.launch {
                repository.insert(newNote)
                // After adding the note, refresh the list of notes
                getFilteredNotes()
            }
            _textInput.value = "" // Clear input field
        }
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
