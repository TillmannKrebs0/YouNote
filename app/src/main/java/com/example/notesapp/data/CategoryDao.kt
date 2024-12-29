package com.example.notesapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.notesapp.model.Category

@Dao
interface CategoryDao {
    @Insert
    fun insert(note: Category): Long

    @Delete
    fun  delete(note: Category)

    @Update
    fun update(note: Category)

    @Query("SELECT * FROM categories")
    fun getAllCategories(): List<Category>
}
