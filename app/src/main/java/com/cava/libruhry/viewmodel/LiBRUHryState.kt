package com.cava.libruhry.viewmodel

import com.cava.libruhry.dataclass.Book
import com.cava.libruhry.dataclass.BookData

data class LiBRUHryState (
    val books: List<BookData> = emptyList(),
    val sortType: SortType = SortType.TITLE,
    val isAddingContact: Boolean = false,
    val selectedBook: BookData? = null,
)