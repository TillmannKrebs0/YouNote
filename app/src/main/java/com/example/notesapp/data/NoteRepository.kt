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

    suspend fun insert(note: Note) {
        withContext(Dispatchers.IO) {
            noteDao.insert(note)
        }
    }
}
