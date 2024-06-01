package com.cava.libruhry.compose

import android.graphics.Bitmap
import android.graphics.Color.parseColor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.cava.libruhry.R
import com.cava.libruhry.backend.TEST_IMAGE_PATH
import com.cava.libruhry.backend.TEST_PATH
import com.cava.libruhry.dataclass.Author
import com.cava.libruhry.dataclass.Book
import com.cava.libruhry.dataclass.BookData
import com.cava.libruhry.dataclass.Category
import com.cava.libruhry.dataclass.Person
import com.cava.libruhry.dataclass.PersonWithReadDates
import com.cava.libruhry.dataclass.relationship.BookPersonCrossRef
import com.skydoves.cloudy.Cloudy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

@Preview(device = "spec:id=reference_phone,shape=Normal,width=411,height=891,unit=dp,dpi=420")
@Composable
fun HomeView(
    navController: NavHostController = rememberNavController(),
    books: List<BookData> = (1..100).map {
        BookData(
            Book(
                isbn = UUID.randomUUID().toString(),
                liked = false,
                title = "Libro $it",
                imageThumbnail = if (Random.nextBoolean()) TEST_IMAGE_PATH else TEST_PATH,
            ),
            authors = listOf(Author("andrea")),
            categories = listOf(Category("andrea")),
            people = listOf(
                PersonWithReadDates(
                    Person("Andrea", 0xFFFFFF), 1234567890, 1234567890
                )
            ),
        )
    },
    setPage: (Pages) -> Unit = {},
    paddings: PaddingValues = PaddingValues(0.dp),
    onBookClick: (BookData) -> Unit = {},
    bookAddEvent: (BookData) -> Unit = {},
    scanAction: @Composable () -> Unit = {},
) {
    NavHost(navController = navController, startDestination = "HOME") {
        composable("HOME") {
            setPage(Pages.HOME)
            BookList(books = books, paddings = paddings, onBookClick = onBookClick)
//            ProductScreen()
        }
        composable("SCANNER") {
            setPage(Pages.SCANNER)
            scanAction()
        }
        composable("ISBN") {
            setPage(Pages.ISBN)
            IsbnFormView {
                setPage(Pages.NEW_BOOK)
                navController.navigate("NEW_BOOK")
            }
        }
        composable("NEW_BOOK") {
            setPage(Pages.NEW_BOOK)
            NewBookView(
                paddings = paddings,
                onSubmit = { data ->
                    CoroutineScope(Dispatchers.IO).launch {
                        bookAddEvent(data)
                    }
                    navController.navigate("HOME")
                },
            )
        }
    }
}


@Composable
fun ProductScreen() {
    val context = LocalContext.current

    val imageUrl = TEST_PATH
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val colorCover: Map<String, String>?
    var brush by remember {
        mutableStateOf(Brush.linearGradient(listOf(Color.White, Color.Black)))
    }

    var isLoadingGradient by remember {
        mutableStateOf(true)
    }
    var isLoadingImage by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(true) {
        val resultBitmap = PaletteGenerator.convertImageUrlToBitmap(
            imageUrl, context
        )!!

        bitmap = resultBitmap
        isLoadingGradient = false
    }

    if (bitmap != null) {
        colorCover = PaletteGenerator.extractColorsFromBitmap(bitmap)
        brush = Brush.linearGradient(
            colors = listOfNotNull(
                Color(parseColor(colorCover?.get("mutedSwatch"))),
                Color(parseColor(colorCover?.get("lightVibrant"))),
                Color(parseColor(colorCover?.get("vibrant"))),
                Color(parseColor(colorCover?.get("darkVibrant"))),
            )
        )
    }

    Row(
        modifier = Modifier
            .bounceClick {
                // @TODO navigate to detail
            }
            .fillMaxWidth()
            .padding(top = 100.dp)
            .clip(RoundedCornerShape(10.dp))
            .then(
                if (isLoadingImage)
                    Modifier.background(MaterialTheme.colorScheme.secondaryContainer)
                else
                    Modifier.background(brush)
            )
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "",
            placeholder = painterResource(id = R.drawable.thumbnail_placeholder),
            error = painterResource(id = R.drawable.thumbnail_placeholder),
            onSuccess = {
                isLoadingImage = false
            },
            modifier = Modifier
                .height(120.dp)
                .padding(6.dp)
                .clip(RoundedCornerShape(10.dp)),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(10.dp)
        ) {
            Text(
                text = "AaaaAAAAAA",
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "PROVA",
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        IconButton(
            onClick = { },
            modifier = Modifier
                .padding(10.dp)
                .clip(RoundedCornerShape(10.dp))
                .size(30.dp)
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .padding(2.dp),
                imageVector = Icons.Outlined.Favorite,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary
            )
        }

    }


//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(top = 60.dp)
//    ) {
//
//        AsyncImage(
//            model = imageUrl,
//            contentDescription = "",
//            placeholder = painterResource(id = R.drawable.testimage),
//            error = painterResource(id = R.drawable.cool_image),
//            onLoading = {
//                println("LODING...")
//            },
//            onError = {
//                println("ERROR: ${it.result.request.error}")
//            },
//            modifier = Modifier.size(200.dp),
//        )
////        Cloudy {
//        Canvas(modifier = Modifier.size(200.dp), onDraw = {
//            drawCircle(brush)
//        })
////        }

//    }
}


//    val bitmap = remember {
//        BitmapFactory.decodeResource(context.resources, R.drawable.provaimmaginegradiente)
//    }
//    val palette = remember {
//        Palette.from(bitmap).generate()
//    }
//        val vibrantSwatch = palette?.vibrantSwatch
//        val lightVibrantSwatch = palette?.lightVibrantSwatch
//        val darkVibrantSwatch = palette?.darkVibrantSwatch
//        val mutedSwatch = palette?.mutedSwatch
//        val brush = Brush.linearGradient(
//            colors = listOfNotNull(
//                        vibrantSwatch?.let { Color(it.rgb) } ?: Color.Black,
//                        lightVibrantSwatch?.let { Color(it.rgb) } ?: Color.Black,
//                        mutedSwatch?.let { Color(it.rgb) } ?: Color.Black,
//                        darkVibrantSwatch?.let { Color(it.rgb) } ?: Color.Black,
//            )
//        )
