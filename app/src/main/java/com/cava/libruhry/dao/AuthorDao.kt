package com.cava.libruhry.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.cava.libruhry.dataclass.Author
import com.cava.libruhry.dataclass.AuthorWithBooks
import kotlinx.coroutines.flow.Flow

@Dao
interface AuthorDao {
    @Upsert
    suspend fun upsertAuthor(author: Author)

    @Query("SELECT * FROM authors")
    fun getAuthors(): Flow<List<Author>>

    @Query("SELECT * FROM authors WHERE name = :name")
    fun getAuthor(name: String): Flow<Author>

    @Query("SELECT * FROM authors WHERE name = :name")
    fun getAuthorFromName(name: String): Author

    @Transaction
    @Query("SELECT * FROM authors WHERE name = :name")
    fun getAuthorWithBooks(name: String): Flow<AuthorWithBooks>
}