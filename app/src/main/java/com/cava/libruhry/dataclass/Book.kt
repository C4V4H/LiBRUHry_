package com.cava.libruhry.dataclass

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.cava.libruhry.dao.relationship.BookPersonCrossRefDao
import com.cava.libruhry.dataclass.relationship.BookAuthorCrossRef
import com.cava.libruhry.dataclass.relationship.BookCategoryCrossRef
import com.cava.libruhry.dataclass.relationship.BookPersonCrossRef

@Entity(
    tableName = "books",
)
data class Book(
    @PrimaryKey(autoGenerate = false)
    val isbn: String,
    val title: String,
    val subtitle: String = "",
    var liked: Boolean = false,
    val series: String = "",
    val publisher: String = "",
    val pages: Int? = null,
    val language: String = "",
    val description: String = "",
    val imageThumbnail: String = ""
)


data class BookWithAuthors(
    @Embedded val book: Book,
    @Relation(
        parentColumn = "isbn",
        entityColumn = "name",
        associateBy = Junction(BookAuthorCrossRef::class)
    )
    val authors: List<Author>
)

data class BookWithCategories(
    @Embedded val book: Book,
    @Relation(
        parentColumn = "isbn",
        entityColumn = "name",
        associateBy = Junction(BookCategoryCrossRef::class)
    )
    val categories: List<Category>
)

data class BookWithPersons(
    @Embedded val book: Book,
    @Relation(
        parentColumn = "isbn",
        entityColumn = "name",
        associateBy = Junction(BookPersonCrossRef::class)
    )
    val persons: List<BookPersonCrossRef>
)

data class BookWithReadDates(
    @Embedded val book: Book,
    val startDate: Long,
    val endDate: Long
)