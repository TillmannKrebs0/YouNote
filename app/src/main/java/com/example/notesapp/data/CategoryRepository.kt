package com.example.notesapp.data

import com.example.notesapp.model.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CategoryRepository(private val categoryDao: CategoryDao) {
    suspend fun getAllCategories(): List<Category> {
        return withContext(Dispatchers.IO) {
            categoryDao.getAllCategories()
        }
    }


    suspend fun insert(category: Category) {
        withContext(Dispatchers.IO) {
            categoryDao.insert(category)
        }
    }

    suspend fun delete(category: Category) {
        withContext(Dispatchers.IO) {
            categoryDao.delete(category)
        }
    }

    suspend fun update(category: Category) {
        withContext(Dispatchers.IO) {
            categoryDao.update(category)
        }
    }
}
