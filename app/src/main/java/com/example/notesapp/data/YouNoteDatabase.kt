package com.example.notesapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.notesapp.model.Category
import com.example.notesapp.model.Note

@Database(entities = [Note::class, Category::class], version = 2, exportSchema = false)
abstract class YouNoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: YouNoteDatabase? = null

        fun getDatabase(context: Context): YouNoteDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            //context.deleteDatabase("note_database")


            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    YouNoteDatabase::class.java,
                    "note_database"
                ).fallbackToDestructiveMigration()  // Ensures that the database is dropped on version change
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
