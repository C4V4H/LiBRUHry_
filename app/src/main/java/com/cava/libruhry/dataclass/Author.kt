package com.cava.libruhry.dataclass

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.cava.libruhry.dataclass.relationship.BookAuthorCrossRef

@Entity(tableName = "authors")
data class Author(
    @PrimaryKey(autoGenerate = false)
    val name: String = "",
)

data class AuthorWithBooks(
    @Embedded val author: Author,
    @Relation(
        parentColumn = "name",
        entityColumn = "isbn",
        associateBy = Junction(BookAuthorCrossRef::class)
    )
    val books: List<Book>
)