package com.cava.libruhry.dao.relationship

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cava.libruhry.dataclass.Author
import com.cava.libruhry.dataclass.Book
import com.cava.libruhry.dataclass.relationship.BookAuthorCrossRef

@Dao
interface BookAuthorCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookAuthorCrossRef: BookAuthorCrossRef)

    @Query("SELECT authors.* FROM Authors JOIN book_author_cross_ref ON authors.name = book_author_cross_ref.name JOIN books ON book_author_cross_ref.isbn = books.isbn WHERE books.isbn = :isbn")
    suspend fun getAuthorsFromIsbn(isbn: String): List<Author>

    @Query("SELECT books.* FROM books JOIN book_author_cross_ref ON books.isbn = book_author_cross_ref.isbn JOIN authors ON book_author_cross_ref.name = authors.name WHERE authors.name = :name")
    suspend fun getBooksFromName(name: String): List<Book>
}
