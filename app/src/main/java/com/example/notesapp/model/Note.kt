package com.example.notesapp.model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Entity(
    tableName = "notes",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "categoryId")  // Use categoryId instead of category
    val categoryId: Int,  // Store the category ID

    @ColumnInfo(name = "isSecret")
    val isSecret: Boolean,

    @ColumnInfo(name = "creationDate")
    val creationDate: String = getCurrentDateTime(),

    @ColumnInfo(name = "isEdited")
    val isEdited: Boolean
) {
    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        fun getCurrentDateTime(): String {
            val currentDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            return currentDateTime.format(formatter)
        }
    }
}
