package com.example.notesapp.viewmodel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.data.CategoryRepository
import com.example.notesapp.model.Category
import com.example.notesapp.model.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CategoriesUiState(
    val categories: List<Category> = emptyList(),
    val activeCategory: Category? = null,
    val categoryInput: String = "",
    val secretFlagSet: Boolean = false,
    val categoryPassword: String = "",
    val selectedCategory: Category? = null
) {

}

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

    fun onCategoryPasswordChanged(text: String) {
        _uiState.value = _uiState.value.copy(categoryPassword = text)
    }

    fun setActiveCategory(category: Category) {
        _uiState.value = _uiState.value.copy(activeCategory = category)
    }

    fun toggleSecretFlag() {
        _uiState.value = _uiState.value.copy(secretFlagSet = !_uiState.value.secretFlagSet)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addCategory() {
        val currentState = _uiState.value
        if (currentState.categoryInput.isBlank()) return

        viewModelScope.launch {
            if (_uiState.value.secretFlagSet) {
                val newCategory =
                    Category(name = currentState.categoryInput, isSecret = true, password = _uiState.value.categoryPassword)
                categoryRepository.insert(newCategory)
                _uiState.value = _uiState.value.copy(secretFlagSet = false, categoryPassword = "")
            } else {
                val newCategory =
                    Category(name = currentState.categoryInput, isSecret = false, password = null)
                categoryRepository.insert(newCategory)
            }
            loadCategories()
            _uiState.value = _uiState.value.copy(categoryInput = "")
        }
    }

    fun selectCategory(category: Category?) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun clearSelectedCategory() {
        _uiState.value = _uiState.value.copy(selectedCategory = null)
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            categoryRepository.delete(category)
            loadCategories()
        }
    }
}