package com.example.notesapp

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
data class Note(
    val content: String,
    val category: String,
    val isSecret: Boolean
) {
    var creationDate: String // Date set dynamically
    init {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        creationDate = currentDateTime.format(formatter)
    }
}