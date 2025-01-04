package com.example.notesapp.data

import androidx.room.util.query
import com.example.notesapp.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NoteRepository(private val noteDao: NoteDao) {
    suspend fun getAllNotesOldestFirst(categoryID: Int): List<Note> {
        return withContext(Dispatchers.IO) {
            noteDao.getAllNotesOldestFirst(categoryID = categoryID)
        }
    }

    suspend fun getFilteredNotesOldestFirst( categoryID: Int, query: String): List<Note> {
        return withContext(Dispatchers.IO) {
            noteDao.getFilteredNotesOldestFirst(query, categoryID)
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

    suspend fun getNotesByFilters(
        categoryID: Int,
        onlyEdited: Boolean,
        showOldestFirst: Boolean,
        searchQuery: String,
        beforeDate: String?,
        afterDate: String?,
        onDate: String?): List<Note> {
            val sortOrder = if (showOldestFirst) "DESC" else "ASC"
            return withContext(Dispatchers.IO) {
                noteDao.getNotesByFilters(
                    categoryID = categoryID,
                    onlyEdited = onlyEdited,
                    sortOrder = sortOrder,
                    beforeDate = beforeDate,
                    afterDate = afterDate,
                    query = searchQuery,
                    onDate = onDate
                )
            }
    }
}
