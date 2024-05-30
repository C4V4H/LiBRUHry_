package com.cava.libruhry.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.cava.libruhry.dataclass.Book
import com.cava.libruhry.dataclass.BookWithAuthors
import com.cava.libruhry.dataclass.BookWithCategories
import com.cava.libruhry.dataclass.BookWithPersons
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Upsert
    suspend fun upsertBook(book: Book)

    @Delete
    suspend fun deleteBook(book: Book)

    @Query("SELECT * FROM books ORDER BY title ASC")
    fun getBooksOrderByTitle(): Flow<List<Book>>

    @Query("SELECT * FROM books ORDER BY series ASC")
    fun getBooksOrderBySeries(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE liked = 1 ORDER BY title ASC")
    fun getLikedBooks(): Flow<List<Book>>

    @Query("UPDATE books SET liked = NOT liked WHERE isbn = :isbn")
    suspend fun toggleBookLike(isbn: String)

    @Transaction
    @Query("SELECT * FROM books WHERE isbn = :isbn")
    fun getBookWithAuthors(isbn: String): Flow<BookWithAuthors>

    @Transaction
    @Query("SELECT * FROM books WHERE isbn = :isbn")
    fun getBookWithPersons(isbn: String): Flow<BookWithPersons>

    @Transaction
    @Query("SELECT * FROM books WHERE isbn = :isbn")
    fun getBookWithCategories(isbn: String): Flow<BookWithCategories>
}
