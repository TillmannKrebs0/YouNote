package com.example.notesapp.view

import com.example.notesapp.viewmodel.NotesViewModel
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.example.notesapp.viewmodel.CategoryViewModel
import com.example.notesapp.viewmodel.ViewModelFactory


class MainActivity : ComponentActivity() {
    private val viewModelFactory by lazy { ViewModelFactory(application, this) }
    private val categoryViewModel: CategoryViewModel by viewModels { viewModelFactory }
    private val notesViewModel: NotesViewModel by viewModels { viewModelFactory }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotesApp(
                categoryViewModel = categoryViewModel,
                notesViewModel = notesViewModel
            )
        }
    }
}
