package com.example.notesapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.notesapp.model.Category
import com.example.notesapp.model.Note

@Dao
interface NoteDao {
    @Insert
    fun insert(note: Note): Long

    @Delete
    fun  delete(note: Note)

    @Update
    fun update(note: Note)

    @Query("SELECT * FROM notes")
    fun getAllNotes(): List<Note>

    @Query("SELECT * FROM notes WHERE content LIKE '%' || :query || '%'")
    fun getFilteredNotes(query: String): List<Note>

    @Query("SELECT * FROM notes WHERE categoryId = :categoryID")
    fun getAllNotesOfCategory(categoryID: Int): List<Note>

    @Query("SELECT * FROM notes WHERE content LIKE '%' || :query || '%' AND categoryId = :categoryID")
    fun getFilteredNotesOfCategory(query: String, categoryID: Int): List<Note>

}
