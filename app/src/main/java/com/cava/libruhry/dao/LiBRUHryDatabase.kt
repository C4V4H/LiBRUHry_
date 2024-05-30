package com.cava.libruhry.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cava.libruhry.dao.relationship.BookAuthorCrossRefDao
import com.cava.libruhry.dao.relationship.BookCategoryCrossRefDao
import com.cava.libruhry.dao.relationship.BookPersonCrossRefDao
import com.cava.libruhry.dataclass.Author
import com.cava.libruhry.dataclass.Book
import com.cava.libruhry.dataclass.Category
import com.cava.libruhry.dataclass.Person
import com.cava.libruhry.dataclass.relationship.BookAuthorCrossRef
import com.cava.libruhry.dataclass.relationship.BookCategoryCrossRef
import com.cava.libruhry.dataclass.relationship.BookPersonCrossRef

@Database(
    entities = [
        Book::class,
        Author::class,
        Category::class,
        Person::class,
        BookAuthorCrossRef::class,
        BookCategoryCrossRef::class,
        BookPersonCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LiBRUHryDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun authorDao(): AuthorDao
    abstract fun categoryDao(): CategoryDao
    abstract fun personDao(): PersonDao
    abstract fun bookAuthorCrossRefDao(): BookAuthorCrossRefDao
    abstract fun bookCategoryCrossRefDao(): BookCategoryCrossRefDao
    abstract fun bookPersonCrossRefDao(): BookPersonCrossRefDao
}

