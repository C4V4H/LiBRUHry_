package com.cava.libruhry.viewmodel

import com.cava.libruhry.dataclass.Book
import com.cava.libruhry.dataclass.BookData

sealed interface LiBRUHryEvent {
    data class SaveBook(val book: BookData): LiBRUHryEvent
    data class SortBooks(val sortType: SortType): LiBRUHryEvent
    data class ToggleLike(val isbn: String): LiBRUHryEvent
    data class DeleteBook(val book: Book): LiBRUHryEvent

}