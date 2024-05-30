package com.cava.libruhry.dataclass.relationship

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.cava.libruhry.dataclass.Book
import com.cava.libruhry.dataclass.Person

@Entity(
    tableName = "book_person_cross_ref",
    primaryKeys = ["isbn", "name"],
    foreignKeys = [
        ForeignKey(entity = Book::class, parentColumns = ["isbn"], childColumns = ["isbn"]),
        ForeignKey(entity = Person::class, parentColumns = ["name"], childColumns = ["name"])
    ],
    indices = [Index("isbn"), Index("name")]
)
data class BookPersonCrossRef(
    val isbn: String,
    val name: String,
    val startDate: Long = 0,
    val endDate: Long = 0
)
