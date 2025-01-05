package com.example.notesapp.view

import com.example.notesapp.viewmodel.NotesViewModel
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.notesapp.ui.theme.NotesAppTheme
import com.example.notesapp.view.CategoryMenu.CategoryMenu
import com.example.notesapp.view.CategoryMenu.rememberSidebarConfiguration
import com.example.notesapp.view.Dialogs.DialogHandler
import com.example.notesapp.viewmodel.CategoryViewModel
import com.example.notesapp.viewmodel.ScreenViewModel
import com.example.notesapp.viewmodel.ViewModelFactory
import com.example.notesapp.view.MainLayout.HandleNotesScrolling
import com.example.notesapp.view.MainLayout.NotesAppLayout
import com.example.notesapp.view.MainLayout.NotesMainContent

/**
 * Main activity of the Notes app, responsible for initializing view models and setting up the UI.
 */
class MainActivity : ComponentActivity() {
    private val viewModelFactory by lazy { ViewModelFactory(application, this) }
    private val categoryViewModel: CategoryViewModel by viewModels { viewModelFactory }
    private val notesViewModel: NotesViewModel by viewModels { viewModelFactory }
    private val screenViewModel: ScreenViewModel by viewModels { viewModelFactory }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotesAppTheme {
                NotesApp(
                    categoryViewModel = categoryViewModel,
                    notesViewModel = notesViewModel,
                    screenViewModel = screenViewModel
                )
            }
        }
    }
}

/**
 * Composable function for the main Notes app UI.
 *
 * @param categoryViewModel ViewModel for managing category-related state.
 * @param notesViewModel ViewModel for managing notes-related state.
 * @param screenViewModel ViewModel for managing screen navigation and UI state.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotesApp(
    categoryViewModel: CategoryViewModel,
    notesViewModel: NotesViewModel,
    screenViewModel: ScreenViewModel
) {
    // Collecting UI states from ViewModels.
    val noteUiState by notesViewModel.uiState.collectAsState()
    val categoryUiState by categoryViewModel.uiState.collectAsState()
    val screenState by screenViewModel.uiState.collectAsState()

    // State and coroutine scope for managing scrolling and sidebar.
    val notes = noteUiState.notes
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val sidebarConfig = rememberSidebarConfiguration()

    // Handle scroll actions and synchronization.
    HandleNotesScrolling(notes, listState, coroutineScope)

    // Layout of the app
    NotesAppLayout(
        modifier = Modifier.fillMaxSize(),
        sidebarConfig = sidebarConfig,
        coroutineScope = coroutineScope,
        screenViewModel = screenViewModel
    ) {
        // Renders the main View, consisting of Control Bar, Notes and Input Bar
        NotesMainContent(
            noteUiState = noteUiState,
            screenState = screenState,
            listState = listState,
            notesViewModel = notesViewModel,
            screenViewModel = screenViewModel,
            sidebarConfig = sidebarConfig,
            coroutineScope = coroutineScope,
            categoryUiState = categoryUiState,
        )
        // Render The Category Menu
        CategoryMenu(
            screenState = screenState,
            coroutineScope = coroutineScope,
            sidebarConfig = sidebarConfig,
            screenViewModel = screenViewModel,
            categoryUiState = categoryUiState,
            categoryViewModel = categoryViewModel
        )

        // Handle any active Dialogs
        DialogHandler(
            screenState = screenState,
            noteUiState = noteUiState,
            screenViewModel = screenViewModel,
            notesViewModel = notesViewModel,
            categoryViewModel = categoryViewModel,
            categoryUiState = categoryUiState
        )
    }
}
