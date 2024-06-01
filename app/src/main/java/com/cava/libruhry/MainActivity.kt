package com.cava.libruhry

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.cava.libruhry.backend.TEST_IMAGE_PATH
import com.cava.libruhry.backend.TEST_PATH
import com.cava.libruhry.compose.MainView
import com.cava.libruhry.dao.LiBRUHryDatabase
import com.cava.libruhry.dataclass.Author
import com.cava.libruhry.dataclass.Book
import com.cava.libruhry.dataclass.BookData
import com.cava.libruhry.dataclass.Category
import com.cava.libruhry.dataclass.Person
import com.cava.libruhry.dataclass.PersonWithReadDates
import com.cava.libruhry.dataclass.relationship.BookAuthorCrossRef
import com.cava.libruhry.dataclass.relationship.BookCategoryCrossRef
import com.cava.libruhry.dataclass.relationship.BookPersonCrossRef
import com.cava.libruhry.ui.theme.LiBRUHryTheme
import com.cava.libruhry.viewmodel.CameraPermissionTextProvider
import com.cava.libruhry.viewmodel.LiBRUHryViewModel
import com.cava.libruhry.viewmodel.PermissionDialog
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

class MainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            LiBRUHryDatabase::class.java,
            "contacts.db"
        ).allowMainThreadQueries().build()
    }

    private val viewModel by viewModels<LiBRUHryViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return LiBRUHryViewModel(
                        application = application,
                        bookDao = db.bookDao(),
                        authorDao = db.authorDao(),
                        bookAuthorCrossRefDao = db.bookAuthorCrossRefDao(),
                        categoryDao = db.categoryDao(),
                        bookCategoryCrossRefDao = db.bookCategoryCrossRefDao(),
                        personDao = db.personDao(),
                        bookPersonCrossRefDao = db.bookPersonCrossRefDao(),
                    ) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LiBRUHryTheme {
                val dialogQueue = viewModel.visiblePermissionDialogQueue

                val cameraPermissionResultLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { isGranted ->
                        viewModel.onPermissionResult(
                            permission = Manifest.permission.CAMERA,
                            isGranted = isGranted
                        )
                    }
                )

                val state by viewModel.state.collectAsState()

                if (!state.areBooksLoading) {
                    MainView(
                        state = state,
                        onEvent = viewModel::onEvent,
                        cameraPermissionResultLauncher
                    )
                } else {
                    Box (
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                dialogQueue
                    .reversed()
                    .forEach { permission ->
                        PermissionDialog(
                            permissionTextProvider = when (permission) {
                                Manifest.permission.CAMERA -> {
                                    CameraPermissionTextProvider()
                                }

                                else -> return@forEach
                            },
                            isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                                permission
                            ),
                            onDismiss = viewModel::dismissDialog,
                            onOkClick = {
                                viewModel.dismissDialog()
                                cameraPermissionResultLauncher.launch(
                                    Manifest.permission.CAMERA
                                )
                            },
                            onGoToAppSettingsClick = ::openAppSettings
                        )
                    }

            }
        }
//        lifecycleScope.launch {
//            testDatabaseInsertion(db)
//        }
    }
}


fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}

private suspend fun testDatabaseInsertion(db: LiBRUHryDatabase) {
    // Creazione del libro
    val books: List<BookData> = (1..10).map {
        BookData(
            Book(
                isbn = UUID.randomUUID().toString(),
                liked = false,
                title = "Libro $it",
                imageThumbnail = if (Random.nextBoolean()) TEST_IMAGE_PATH else TEST_PATH,
            ),
            authors = listOf(
                Author("Andrea Panti"),
                Author("Paolo Bruno Porrati")
            ),
            categories = listOf(
                Category("andrea")
            ),
            people = listOf(
                PersonWithReadDates(
                    Person("Andrea", 0xFFFFFF), 1234567890, 1234567890
                )
            ),
        )
    }

    // Creazione delle categorie
    val category1 = Category(name = "Fiction")
    val category2 = Category(name = "Adventure")

    // Creazione delle persone
    val person1 = Person(name = "Alice", color = 0xFF0000)
    val person2 = Person(name = "Bob", color = 0x00FF00)

//        // Date di lettura (millisecondi dal 1 gennaio 1970)
    val startDate1 = System.currentTimeMillis() - 1000000000 // 11 giorni fa
    val endDate1 = System.currentTimeMillis() - 500000000 // 5 giorni fa
    val startDate2 = System.currentTimeMillis() - 2000000000 // 23 giorni fa
    val endDate2 = System.currentTimeMillis() - 1000000000 // 11 giorni fa


    // Inserimento dei dati nel database
    books.forEach { bookData ->
        db.bookDao().upsertBook(bookData.book)
        bookData.authors.forEach {
            db.authorDao().upsertAuthor(it)
            db.bookAuthorCrossRefDao().insert(
                BookAuthorCrossRef(
                    isbn = bookData.book.isbn,
                    name = it.name
                )
            )
        }
    }

    db.categoryDao().upsertCategory(category1)
    db.categoryDao().upsertCategory(category2)

    db.bookCategoryCrossRefDao().insert(
        BookCategoryCrossRef(
            isbn = books[0].book.isbn,
            name = category1.name
        )
    )

    db.personDao().upsertPerson(person1)
    db.personDao().upsertPerson(person2)

    db.bookPersonCrossRefDao().insert(
        BookPersonCrossRef(
            isbn = books[0].book.isbn,
            name = person1.name,
            startDate = startDate1,
            endDate = endDate1
        )
    )

}


/*
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            LiBRUHryDatabase::class.java,
            "contacts.db"
        ).build()
    }

    private val viewModel by viewModels<LiBRUHryViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return LiBRUHryViewModel(
                        db.bookDao(),
                        db.authorDao(),
                        db.categoryDao(),
                        db.personDao(),
                    ) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LiBRUHryTheme {
                val state by viewModel.state.collectAsState()
                MainView(state = state, onEvent = viewModel::onEvent)
            }
        }
//        lifecycleScope.launch {
//            testDatabaseInsertion()
//        }
    }

    private suspend fun testDatabaseInsertion() {
        // Creazione del libro
        val book = Book(
            isbn = "1234567890",
            title = "Sample Book",
            subtitle = "A Subtitle",
            liked = true,
            series = "Sample Series",
            publisher = "Sample Publisher",
            pages = 300,
            language = "English",
            description = "A sample book for testing.",
            imageThumbnail = "http://example.com/image.jpg"
        )

        // Creazione delle categorie
        val category1 = Category(name = "Fiction")
        val category2 = Category(name = "Adventure")

        // Creazione delle persone
        val person1 = Person(name = "Alice", color = 0xFF0000)
        val person2 = Person(name = "Bob", color = 0x00FF00)

//        // Date di lettura (millisecondi dal 1 gennaio 1970)
        val startDate1 = System.currentTimeMillis() - 1000000000 // 11 giorni fa
        val endDate1 = System.currentTimeMillis() - 500000000 // 5 giorni fa
        val startDate2 = System.currentTimeMillis() - 2000000000 // 23 giorni fa
        val endDate2 = System.currentTimeMillis() - 1000000000 // 11 giorni fa

        // Inserimento dei dati nel database
        db.bookDao().upsertBook(book)

        db.categoryDao().upsertCategory(category1)
        db.categoryDao().upsertCategory(category2)

//        db.categoryDao().getCategories()

        db.bookCategoryCrossRefDao().insert(BookCategoryCrossRef(isbn = book.isbn, id = 1))
//        db.bookCategoryCrossRefDao().insert(BookCategoryCrossRef(isbn = book.isbn, id = category2.id))
//
        db.personDao().upsertPerson(person1)
//        db.personDao().upsertPerson(person2)
//
        db.bookPersonCrossRefDao().insert(
            BookPersonCrossRef(isbn = book.isbn, id = 1, startDate = startDate1, endDate = endDate1)
        )
//        db.bookPersonCrossRefDao().insert(
//            BookPersonCrossRef(isbn = book.isbn, id = person2.id, /*startDate = startDate2, endDate = endDate2*/)
//        )
//
//        // Verifica dell'inserimento
//        val books = db.bookDao().getBooksOrderByTitle().first()
//        books.forEach {
//            println(it)
//        }
//
//        val bookWithCategories = db.bookDao().getBookWithAuthors(book.isbn).first()
//        println(bookWithCategories)
//
//        val bookWithPersons = db.bookDao().getBookWithPersons(book.isbn).first()
//        println(bookWithPersons)
    }
}

 */
