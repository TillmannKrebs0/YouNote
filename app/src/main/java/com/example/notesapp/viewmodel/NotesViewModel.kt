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

class NotesViewModel(application: Application) : AndroidViewModel(application) {

    private val noteDao = YouNoteDatabase.getDatabase(application).noteDao()
    private val noteRepository = NoteRepository(noteDao)
    private  val categoryDao = YouNoteDatabase.getDatabase(application).categoryDao()
    private val categoryRepository = CategoryRepository(categoryDao)

    private val _textInput = MutableStateFlow("")
    val textInput: StateFlow<String> = _textInput

    private val _categoryInput = MutableStateFlow("")
    val categoryInput: StateFlow<String> = _categoryInput

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    var selectedNote: Note? = null
    private val _activeCategory = MutableStateFlow<Category?>(null)
    val activeCategory: StateFlow<Category?> = _activeCategory


    init {
        getAllCategories()

        // set initial Category to be the first Category, then get all associated notes
        viewModelScope.launch {
            categories.collect { categoriesList ->
                if (_activeCategory.value == null && categoriesList.isNotEmpty()) {
                    _activeCategory.value = categoriesList.first()
                    getAllNotes()
                }
            }
        }
    }

    private fun getAllCategories() {
        viewModelScope.launch {
            _categories.value = categoryRepository.getAllCategories()
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
        getFilteredNotes()
    }

    fun onTextChanged(newText: String) {
        _textInput.value = newText
    }

    fun onCategoryInputChanged(newText: String) {
        _categoryInput.value = newText
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addNote() {
        if (_textInput.value.isBlank()) {
            return
        }

        viewModelScope.launch {
            val currentCategory = _activeCategory.value
            if (currentCategory != null) {
                val newNote = Note(
                    content = _textInput.value,
                    categoryId = currentCategory.id,
                    isSecret = false
                )
                noteRepository.insert(newNote)
                refreshNotes()
            }
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
            noteRepository.update(updatedNote)
        }
        selectedNote = null
        refreshNotes()
    }

    private fun refreshNotes() {
        getFilteredNotes()
        _textInput.value = "" // Clear input field
    }



    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteRepository.delete(note)
            getFilteredNotes()
        }
    }

    fun showOldestFirst() {
        _notes.value = _notes.value.reversed()
    }

    private fun getAllNotes() {
        // Fetch all notes from the noteRepository asynchronously
        viewModelScope.launch {
            val currentCategory = activeCategory.value ?: return@launch
            _notes.value = noteRepository.getAllNotesOfCategory(categoryID = currentCategory.id)
        }
    }

    private fun getFilteredNotes() {
        viewModelScope.launch {
            val currentCategory = activeCategory.value ?: return@launch

            _notes.value = if (searchQuery.value.isBlank()) {
                noteRepository.getAllNotesOfCategory(currentCategory.id)
            } else {
                noteRepository.getFilteredNotesOfCategory(searchQuery.value, currentCategory.id)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addCategory() {
        if (_categoryInput.value.isBlank()) {
            return
        }

        viewModelScope.launch {
            val newCategory = Category(
                name = _categoryInput.value
            )
            categoryRepository.insert(newCategory)

            refreshCategories()
        }

    }

    private fun refreshCategories() {
        getAllCategories()
        _categoryInput.value = ""
    }

    fun setActiveCategory(category: Category) {
        _activeCategory.value = category
        getFilteredNotes()
    }

}
