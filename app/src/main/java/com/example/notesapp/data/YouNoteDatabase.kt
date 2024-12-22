package com.example.notesapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.notesapp.model.Note

@Database(entities = [Note::class], version = 1, exportSchema = false) // Include all entities here
abstract class YouNoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: YouNoteDatabase? = null

        fun getDatabase(context: Context): YouNoteDatabase {
            val tempInstance = INSTANCE
            if(tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    YouNoteDatabase::class.java,
                    "note_database" // A descriptive name for the database file
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
