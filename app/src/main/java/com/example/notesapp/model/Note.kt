package com.example.notesapp.model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "isSecret")
    val isSecret: Boolean,

    @ColumnInfo(name = "creationDate")
    val creationDate: String = getCurrentDateTime()
) {
    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        fun getCurrentDateTime(): String {
            val currentDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            return currentDateTime.format(formatter)
        }
    }
}
