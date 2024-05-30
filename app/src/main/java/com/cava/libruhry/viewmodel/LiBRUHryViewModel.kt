package com.cava.libruhry.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cava.libruhry.dao.AuthorDao
import com.cava.libruhry.dao.BookDao
import com.cava.libruhry.dao.CategoryDao
import com.cava.libruhry.dao.PersonDao
import com.cava.libruhry.dao.relationship.BookAuthorCrossRefDao
import com.cava.libruhry.dao.relationship.BookCategoryCrossRefDao
import com.cava.libruhry.dao.relationship.BookPersonCrossRefDao
import com.cava.libruhry.dataclass.Book
import com.cava.libruhry.dataclass.BookData
import com.cava.libruhry.dataclass.relationship.BookAuthorCrossRef
import com.cava.libruhry.dataclass.relationship.BookCategoryCrossRef
import com.cava.libruhry.dataclass.relationship.BookPersonCrossRef
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


@OptIn(ExperimentalCoroutinesApi::class)
class LiBRUHryViewModel(
    private val bookDao: BookDao,
    private val authorDao: AuthorDao,
    private val bookAuthorCrossRefDao: BookAuthorCrossRefDao,
    private val categoryDao: CategoryDao,
    private val bookCategoryCrossRefDao: BookCategoryCrossRefDao,
    private val personDao: PersonDao,
    private val bookPersonCrossRefDao: BookPersonCrossRefDao,
) : ViewModel() {

    val visiblePermissionDialogQueue = mutableStateListOf<String>()
    private val _sortType = MutableStateFlow(SortType.TITLE)
    private val _books = _sortType
        .flatMapLatest { sortType ->
            when (sortType) {
                SortType.TITLE -> getBookData(bookDao.getBooksOrderByTitle())
                SortType.SERIES -> getBookData(bookDao.getBooksOrderByTitle())
                SortType.AUTHOR -> getBookData(bookDao.getBooksOrderByTitle())
                SortType.CATEGORY -> getBookData(bookDao.getBooksOrderByTitle())
                SortType.LIKED -> getBookData(bookDao.getLikedBooks())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _state = MutableStateFlow(LiBRUHryState())
    val state = combine(_state, _sortType, _books) { state, sortType, books ->
        state.copy(
            books = books,
            sortType = sortType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LiBRUHryState())


    private fun getBookData(books: Flow<List<Book>>): Flow<List<BookData>> =
        books.flatMapLatest { bookList ->
            flow {
                val bookDataList = mutableListOf<BookData>()
                for (book in bookList) {
                    val authors = bookAuthorCrossRefDao.getAuthorsFromIsbn(book.isbn)
                    val categories = bookCategoryCrossRefDao.getCategoriesFromIsbn(book.isbn)
                    val persons = bookPersonCrossRefDao.getPersonsWithReadDatesFromIsbn(book.isbn)
                    val bookData = BookData(book, authors, categories, persons)
                    bookDataList.add(bookData)
                }
                emit(bookDataList)
            }
        }

    fun onEvent(event: LiBRUHryEvent) {
        when (event) {
            is LiBRUHryEvent.SortBooks -> {
                _sortType.value = event.sortType
            }

            is LiBRUHryEvent.ToggleLike -> {
                viewModelScope.launch {
                    bookDao.toggleBookLike(event.isbn)
                }
            }

            is LiBRUHryEvent.SaveBook -> {
                viewModelScope.launch {
                    val bookData = event.book
                    bookDao.upsertBook(bookData.book)
                    bookData.authors.forEach { author ->
                        authorDao.upsertAuthor(author)
                        bookAuthorCrossRefDao.insert(
                            BookAuthorCrossRef(
                                isbn = bookData.book.isbn,
                                name = author.name
                            )
                        )
                    }
                    bookData.categories.forEach { category ->
                        categoryDao.upsertCategory(category)
                        bookCategoryCrossRefDao.insert(
                            BookCategoryCrossRef(
                                isbn = bookData.book.isbn,
                                name = category.name
                            )
                        )
                    }
                    bookData.people.forEach { person ->
                        personDao.upsertPerson(person.person)
                        bookPersonCrossRefDao.insert(
                            BookPersonCrossRef(
                                isbn = bookData.book.isbn,
                                name = person.person.name,
                                startDate = person.startDate,
                                endDate = person.endDate
                            )
                        )
                    }
                }
            }
        }
    }

    fun dismissDialog() {
        visiblePermissionDialogQueue.removeFirst()
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean,
    ) {
        if (!isGranted && !visiblePermissionDialogQueue.contains(permission)) {
            visiblePermissionDialogQueue.add(permission)
        }
    }
}