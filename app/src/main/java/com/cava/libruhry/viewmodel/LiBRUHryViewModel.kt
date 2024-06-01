package com.cava.libruhry.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.cava.libruhry.compose.PaletteGenerator
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.pow
import kotlin.math.sqrt

@OptIn(ExperimentalCoroutinesApi::class)
class LiBRUHryViewModel(
    private val application: Application,
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
                    val brush = withContext(Dispatchers.IO) { calculateBrushForBook(book.imageThumbnail) }
                    val bookData = BookData(book, authors, categories, persons, brush = brush)
                    bookDataList.add(bookData)
                }
                emit(bookDataList)
                _state.value = _state.value.copy(areBooksLoading = false)
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

            is LiBRUHryEvent.DeleteBook -> {
                viewModelScope.launch {
                    bookPersonCrossRefDao.deleteFromIsbn(event.book.isbn)
                    bookCategoryCrossRefDao.deleteFromIsbn(event.book.isbn)
                    bookAuthorCrossRefDao.deleteFromIsbn(event.book.isbn)
                    bookDao.deleteBook(event.book)
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

//                    _state.update { it.copy(
//                        books =
//                    ) }

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

    private suspend fun calculateBrushForBook(imageUrl: String): Brush {
        val bitmap = PaletteGenerator.convertImageUrlToBitmap(imageUrl, application.applicationContext)
        val palette = bitmap?.let { PaletteGenerator.extractColorsFromBitmap(it, true)?.swatches }
        val result = palette?.sortedByDescending { it.population }
            ?.let { findMostPopulousDifferentColors(it, 100.0) }

        return Brush.linearGradient(
            colors = listOfNotNull(
                result?.first?.let { Color(it.rgb) },
                result?.second?.let { Color(it.rgb) }
            )
        )
    }


    private fun calculateColorDistance(swatch1: Palette.Swatch, swatch2: Palette.Swatch): Double {
        val r1 = (swatch1.rgb shr 16) and 0xFF
        val g1 = (swatch1.rgb shr 8) and 0xFF
        val b1 = swatch1.rgb and 0xFF

        val r2 = (swatch2.rgb shr 16) and 0xFF
        val g2 = (swatch2.rgb shr 8) and 0xFF
        val b2 = swatch2.rgb and 0xFF

        return sqrt((r1 - r2).toDouble().pow(2.0) + (g1 - g2).toDouble().pow(2.0) + (b1 - b2).toDouble().pow(2.0))
    }
    private fun findMostPopulousDifferentColors(swatches: List<Palette.Swatch>, minDistance: Double): Pair<Palette.Swatch, Palette.Swatch> {
        val sortedSwatches = swatches.sortedByDescending { it.population }
        for (j in 1 until sortedSwatches.size) {
            if (calculateColorDistance(sortedSwatches[0], sortedSwatches[j]) >= minDistance) {
                return Pair(sortedSwatches[0], sortedSwatches[j])
            }
        }
        return swatches.first() to swatches.last()
    }



}