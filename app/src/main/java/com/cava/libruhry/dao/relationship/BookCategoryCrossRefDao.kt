package com.cava.libruhry.dao.relationship

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cava.libruhry.dataclass.Book
import com.cava.libruhry.dataclass.Category
import com.cava.libruhry.dataclass.relationship.BookCategoryCrossRef

@Dao
interface BookCategoryCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookCategoryCrossRef: BookCategoryCrossRef)

    @Query("SELECT categories.* FROM categories JOIN book_category_cross_ref ON categories.name = book_category_cross_ref.name JOIN books ON book_category_cross_ref.isbn = books.isbn WHERE books.isbn = :isbn")
    suspend fun getCategoriesFromIsbn(isbn: String): List<Category>

    @Query("SELECT books.* FROM books JOIN book_category_cross_ref ON books.isbn = book_category_cross_ref.isbn JOIN categories ON book_category_cross_ref.name = categories.name WHERE categories.name = :name")
    suspend fun getBooksFromCategoryName(name: String): List<Book>

    @Query("DELETE FROM book_category_cross_ref WHERE isbn = :isbn")
    suspend fun deleteFromIsbn(isbn: String)

    @Query("DELETE FROM book_category_cross_ref WHERE name = :name")
    suspend fun deleteFromName(name: String)
}
