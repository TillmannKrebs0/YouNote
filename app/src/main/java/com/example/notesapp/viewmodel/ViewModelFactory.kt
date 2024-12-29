package com.example.notesapp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.notesapp.data.CategoryRepository
import com.example.notesapp.data.NoteRepository
import com.example.notesapp.data.YouNoteDatabase

class ViewModelFactory(
    private val application: Application,
    private val owner: ViewModelStoreOwner
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(CategoryViewModel::class.java) -> {
                val categoryDao = YouNoteDatabase.getDatabase(application).categoryDao()
                val categoryRepository = CategoryRepository(categoryDao)
                return CategoryViewModel(application, categoryRepository) as T
            }

            modelClass.isAssignableFrom(NotesViewModel::class.java) -> {
                val noteDao = YouNoteDatabase.getDatabase(application).noteDao()
                val noteRepository = NoteRepository(noteDao)
                val categoryViewModel = ViewModelProvider(owner)[CategoryViewModel::class.java]
                return NotesViewModel(application, noteRepository, categoryViewModel) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}



