package com.example.notesapp.viewmodel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.data.CategoryRepository
import com.example.notesapp.model.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CategoriesUiState(
    val categories: List<Category> = emptyList(),
    val activeCategory: Category? = null,
    val categoryInput: String = ""
)

class CategoryViewModel(
    application: Application,
    private val categoryRepository: CategoryRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(CategoriesUiState())
    val uiState: StateFlow<CategoriesUiState> = _uiState

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val categories = categoryRepository.getAllCategories()
            _uiState.value = _uiState.value.copy(
                categories = categories,
                activeCategory = _uiState.value.activeCategory ?: categories.firstOrNull()
            )
        }
    }

    fun onCategoryInputChanged(text: String) {
        _uiState.value = _uiState.value.copy(categoryInput = text)
    }

    fun setActiveCategory(category: Category) {
        _uiState.value = _uiState.value.copy(activeCategory = category)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addCategory() {
        val currentState = _uiState.value
        if (currentState.categoryInput.isBlank()) return

        viewModelScope.launch {
            val newCategory = Category(name = currentState.categoryInput)
            categoryRepository.insert(newCategory)
            loadCategories()
            _uiState.value = _uiState.value.copy(categoryInput = "")
        }
    }
}