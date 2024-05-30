package com.cava.libruhry.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.cava.libruhry.dataclass.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Upsert
    suspend fun upsertCategory(category: Category)

    @Query("SELECT * FROM categories")
    fun getCategories(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE name = :name")
    fun getCategory(name: String): Category

    @Query("SELECT * FROM categories WHERE name = :name")
    fun getCategoryFromName(name: String): Category


}