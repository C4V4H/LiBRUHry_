package com.cava.libruhry.dataclass.relationship

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.cava.libruhry.dataclass.Book
import com.cava.libruhry.dataclass.Category

@Entity(
    tableName = "book_category_cross_ref",
    primaryKeys = ["isbn", "name"],
    foreignKeys = [
        ForeignKey(entity = Book::class, parentColumns = ["isbn"], childColumns = ["isbn"]),
        ForeignKey(entity = Category::class, parentColumns = ["name"], childColumns = ["name"])
    ],
    indices = [Index("isbn"), Index("name")]
)
data class BookCategoryCrossRef(
    val isbn: String,
    val name: String
)