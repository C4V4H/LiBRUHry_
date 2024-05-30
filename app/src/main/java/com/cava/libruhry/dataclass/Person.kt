package com.cava.libruhry.dataclass

import androidx.room.DatabaseView
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.cava.libruhry.dataclass.relationship.BookPersonCrossRef

@Entity(tableName = "people")
data class Person(
    @PrimaryKey(autoGenerate = false)
    val name: String,
    val color: Long,
    val image: String = ""
)

data class PersonWithBooks(
    @Embedded val person: Person,
    @Relation(
        parentColumn = "name",
        entityColumn = "bookIsbn",
        associateBy = Junction(BookPersonCrossRef::class)
    )
    val books: List<BookWithReadDates>
)

data class PersonWithReadDates(
    @Embedded val person: Person,
    val startDate: Long,
    val endDate: Long
)