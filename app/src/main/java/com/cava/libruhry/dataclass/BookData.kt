package com.cava.libruhry.dataclass

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.cava.libruhry.dataclass.relationship.BookAuthorCrossRef
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

data class BookData(
    /*@Embedded*/ val book: Book,
//    @Relation (
//        parentColumn = "isbn",
//        entityColumn = "",
//        associateBy = Junction(BookAuthorCrossRef::class)
//    )
    val authors: List<Author>,
    val categories: List<Category>,
    var people: List<PersonWithReadDates>,
    var brush: Brush = Brush.linearGradient(
        listOf(
            Color.White,
            Color.Black,
        )
    )
)

//@Dao
//interface BookDAO {
//    @Transaction
//    @Query("SELECT * FROM books WHERE isbn = (:isbn)")
//    fun getAllBookData(isbn: String): List<Book>
//}




//@Query("SELECT * FROM books " +
//        "LEFT JOIN written_by ON books.isbn = written_by.isbn " +
//        "LEFT JOIN authors ON written_by.id = authors.id " +
//        "LEFT JOIN book_has_category ON books.isbn = book_has_category.isbn " +
//        "LEFT JOIN categories ON book_has_category.id = categories.id " +
//        "LEFT JOIN person_reads_book ON books.isbn = person_reads_book.isbn " +
//        "LEFT JOIN peoples ON person_reads_book.name = peoples.name " +
//        "GROUP BY books.isbn;")
//fun getAll(): List<Book>
//
//@Query("SELECT * FROM books " +
//        "LEFT JOIN written_by ON books.isbn = written_by.isbn " +
//        "LEFT JOIN authors ON written_by.id = authors.id " +
//        "LEFT JOIN book_has_category ON books.isbn = book_has_category.isbn " +
//        "LEFT JOIN categories ON book_has_category.id = categories.id " +
//        "LEFT JOIN person_reads_book ON books.isbn = person_reads_book.isbn " +
//        "LEFT JOIN peoples ON person_reads_book.name = peoples.name " +
//        "WHERE books.isbn = (:isbn) " +
//        "GROUP BY books.isbn;")
//fun getFromPK(isbn: String): Book
//
//@Insert
//fun insertAll(vararg books: Book)
//
//@Delete
//fun delete(isbn: String)