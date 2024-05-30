package com.cava.libruhry.dataclass.relationship

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.cava.libruhry.dataclass.Author
import com.cava.libruhry.dataclass.Book

@Entity(
    tableName = "book_author_cross_ref",
    primaryKeys = ["isbn", "name"],
    foreignKeys = [
        ForeignKey(entity = Book::class, parentColumns = ["isbn"], childColumns = ["isbn"]),
        ForeignKey(entity = Author::class, parentColumns = ["name"], childColumns = ["name"])
    ],
    indices = [Index("isbn"), Index("name")]
)
data class BookAuthorCrossRef(
    val isbn: String,
    val name: String
)