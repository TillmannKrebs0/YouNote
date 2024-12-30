package com.example.notesapp.model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@RequiresApi(Build.VERSION_CODES.O)
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "content")
    val name: String,

    @ColumnInfo(name = "isSecret")
    val isSecret: Boolean,

    @ColumnInfo(name = "password")
    val password: String?

)
