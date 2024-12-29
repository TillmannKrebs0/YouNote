package com.example.notesapp.data

import com.example.notesapp.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NoteRepository(private val noteDao: NoteDao) {
    suspend fun getAllNotes(): List<Note> {
        return withContext(Dispatchers.IO) {
            noteDao.getAllNotes()
        }
    }

    suspend fun getFilteredNotes( query: String): List<Note> {
        return withContext(Dispatchers.IO) {
            noteDao.getFilteredNotes(query)
        }
    }

    suspend fun getAllNotesOfCategory(categoryID: Int): List<Note> {
        return withContext(Dispatchers.IO) {
            noteDao.getAllNotesOfCategory(categoryID)
        }
    }

    suspend fun getFilteredNotesOfCategory(query: String, categoryID: Int): List<Note> {
        return withContext(Dispatchers.IO) {
            noteDao.getFilteredNotesOfCategory(query, categoryID)
        }
    }

    suspend fun insert(note: Note) {
        withContext(Dispatchers.IO) {
            noteDao.insert(note)
        }
    }

    suspend fun delete(note: Note) {
        withContext(Dispatchers.IO) {
            noteDao.delete(note)
        }
    }

    suspend fun update(note: Note) {
        withContext(Dispatchers.IO) {
            noteDao.update(note)
        }
    }
}
