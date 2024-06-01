package com.cava.libruhry.dao.relationship

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cava.libruhry.dataclass.BookWithReadDates
import com.cava.libruhry.dataclass.PersonWithReadDates
import com.cava.libruhry.dataclass.relationship.BookPersonCrossRef

@Dao
interface BookPersonCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookPersonCrossRef: BookPersonCrossRef)

    // Per ottenere tutte le persone coinvolte in un libro con un determinato ISBN
    @Query("SELECT people.*, book_person_cross_ref.startDate, book_person_cross_ref.endDate FROM people JOIN book_person_cross_ref ON people.name = book_person_cross_ref.name JOIN books ON book_person_cross_ref.isbn = books.isbn WHERE books.isbn = :isbn")
    suspend fun getPersonsWithReadDatesFromIsbn(isbn: String): List<PersonWithReadDates>

    // Per ottenere tutti i libri di una persona con un determinato ID
    @Query("SELECT books.*, book_person_cross_ref.startDate, book_person_cross_ref.endDate FROM books JOIN book_person_cross_ref ON books.isbn = book_person_cross_ref.isbn JOIN people ON book_person_cross_ref.name = people.name WHERE people.name = :name")
    suspend fun getBooksWithReadDatesFromPersonName(name: String): List<BookWithReadDates>

    @Query("DELETE FROM book_person_cross_ref WHERE isbn = :isbn")
    suspend fun deleteFromIsbn(isbn: String)

    @Query("DELETE FROM book_person_cross_ref WHERE name = :name")
    suspend fun deleteFromName(name: String)
}
