package com.cava.libruhry.compose

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cava.libruhry.R
import com.cava.libruhry.backend.getBookData
import com.cava.libruhry.dataclass.Author
import com.cava.libruhry.dataclass.Book
import com.cava.libruhry.dataclass.BookData
import com.cava.libruhry.dataclass.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewBookView(
    paddings: PaddingValues = PaddingValues(0.dp),
    barcodeValue: String = "AAAAAAAAA",
    onSubmit: (BookData) -> Unit,
) {

    //  if (!isValidText(barcodeValue)) return
    val state = rememberDatePickerState()
    var openDialog by remember { mutableStateOf(false) }

    var book: BookData? by remember {
        mutableStateOf(
            null
        )
    }
//            BookData(
//                book = Book(
//                    isbn = "",
//                    title = "Pantis",
//                    subtitle = "the pantis",
//                    liked = false,
//                    series = "",
//                    publisher = "Mondadori",
////                    publishedDate = "12-12-1212",
//                    pages = 1000,
//                    language = "it",
//                    description = "PantiPantiPanti",
//                    imageThumbnail = ""
//                ),
//                categories = listOf(Category("panti")),
//                authors = listOf(Author("andrea")),
//                people = listOf()
//            )

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            book = getBookData(barcodeValue)
        }
    }
    if (book != null) {
        var title by remember { mutableStateOf(TextFieldValue(book!!.book.title)) }
        var subtitle by remember { mutableStateOf(TextFieldValue(book!!.book.subtitle)) }
        var series by remember { mutableStateOf(TextFieldValue(book!!.book.series)) }
        var publisher by remember { mutableStateOf(TextFieldValue(book!!.book.publisher)) }
        var pages by remember { mutableStateOf(TextFieldValue(book!!.book.pages.toString())) }
        var language by remember { mutableStateOf(TextFieldValue(book!!.book.language)) }
        var description by remember { mutableStateOf(TextFieldValue(book!!.book.description)) }

        LazyColumn(
            contentPadding = paddings,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // @TODO add search bar
                Text(text = barcodeValue)
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = book!!.book.imageThumbnail,
                        contentDescription = "",
                        placeholder = painterResource(id = R.drawable.thumbnail_placeholder),
                        error = painterResource(id = R.drawable.thumbnail_placeholder),
                        modifier = Modifier
                            .height(200.dp)
                            .padding(6.dp)
                            .clip(RoundedCornerShape(10.dp)),
                    )
                    Button(onClick = {
                            println("aaaaaaaaaaaa")
                            onSubmit(book!!)

                    }) {
                        Text(text = "Submit")
                    }
                }
            }
            //AsyncImage(model = book.book.imageThumbnail, contentDescription = "Book Thumbnail")

            item {
                RacistTextField(
                    name = "Title",
                    value = title,
                    onValueChange = { title = it }
                )
            }
            item {
                RacistTextField(
                    name = "Subtitle",
                    value = subtitle,
                    onValueChange = { subtitle = it }
                )
            }
            item {
                Text(
                    text = book!!.authors.joinToString(separator = ", ") { it.name },
                    modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            item {
                Text(
                    text = book!!.categories.joinToString(separator = ", ") { it.name },
                    modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            item {
                RacistTextField(
                    name = "Pages",
                    value = pages,
                    onValueChange = { pages = it }
                )
            }
            item {
                RacistTextField(
                    name = "Language",
                    value = language,
                    onValueChange = { language = it }
                )
            }
            item {
                RacistTextField(
                    name = "Description",
                    value = description,
                    onValueChange = { description = it }
                )

            }


//        item {
//            if (openDialog) {
//                DatePickerDialog(
//                    onDismissRequest = {
//                        openDialog = false
//                    },
//                    confirmButton = {
//                        TextButton(
//                            onClick = {
////                            publishedDate = TextFieldValue(millisToDate(state.selectedDateMillis!!))
//                                openDialog = false
//                            }
//                        ) {
//                            Text("OK")
//                        }
//                    },
//                    dismissButton = {
//                        TextButton(
//                            onClick = {
//                                openDialog = false
//                            }
//                        ) {
//                            Text("CANCEL")
//                        }
//                    }
//                ) {
//                    DatePicker(
//                        state = state
//                    )
//                }
//            }
//        }
            //        state
//            .displayedMonthMillis
//            .selectedDateMillis
//            .selectableDates
//            .yearRange
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RacistTextField(
    name: String,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    isError: Boolean = false,
    enabled: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null,
    onClick: () -> Unit = {},
) {
//    Text(
//        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
//        text = name,
//        fontSize = 16.sp,
//        fontWeight = FontWeight.Bold
//    )
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = "Insert here the $name"
            )
        },
        enabled = enabled,
//        shape = RoundedCornerShape(10.dp),
//        colors = TextFieldDefaults.textFieldColors(
//            focusedIndicatorColor = Color.Transparent,
//            unfocusedIndicatorColor = Color.Transparent
//        ),
        trailingIcon = trailingIcon,
        isError = isError,
        label = {
            Text(
                text = name
            )
        }

    )

}

@SuppressLint("SimpleDateFormat")
fun millisToDate(millis: Long): String {
    val date = Date(millis)
    val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
    return simpleDateFormat.format(date)
}