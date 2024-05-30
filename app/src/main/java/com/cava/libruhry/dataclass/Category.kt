package com.cava.libruhry.dataclass

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.cava.libruhry.dataclass.relationship.BookCategoryCrossRef

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = false)
    val name: String = "",
)

data class CategoryWithBooks(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "name",
        entityColumn = "isbn",
        associateBy = Junction(BookCategoryCrossRef::class)
    )
    val books: List<Book>
)
