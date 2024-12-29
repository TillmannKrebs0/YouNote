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

    @Query("SELECT * FROM notes WHERE content LIKE '%' || :query || '%' AND categoryId = :categoryID ORDER BY creationDate DESC")
    fun getFilteredNotesOldestFirst(query: String, categoryID: Int): List<Note>

    @Query("SELECT * FROM notes WHERE categoryId = :categoryID ORDER BY creationDate DESC")
    fun getAllNotesOldestFirst(categoryID: Int): List<Note>

    @Query("""
    SELECT * FROM notes 
    WHERE (:categoryID IS NULL OR categoryId = :categoryID) 
    AND (:onlyEdited = 0 OR isEdited = 1)
    AND (:onDate IS NULL OR creationDate = :onDate)
    AND (:beforeDate IS NULL OR creationDate < :beforeDate)
    AND (:afterDate IS NULL OR creationDate > :afterDate)
    AND content LIKE '%' || :query || '%'
    ORDER BY 
        CASE WHEN :sortOrder = 'ASC' THEN creationDate END ASC, 
        CASE WHEN :sortOrder = 'DESC' THEN creationDate END DESC
""")
    fun getNotesByFilters(
        categoryID: Int?,
        onlyEdited: Boolean?,
        onDate: String?,
        beforeDate: String?,
        afterDate: String?,
        query: String,
        sortOrder: String = "ASC"
    ): List<Note>
}
