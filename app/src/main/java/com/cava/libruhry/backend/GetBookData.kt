package com.cava.libruhry.backend

import com.cava.libruhry.dataclass.Author
import com.cava.libruhry.dataclass.BookData
import com.cava.libruhry.dataclass.Book
import com.cava.libruhry.dataclass.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.net.URL

suspend fun getBookData(isbn: String): BookData? {
    if (isbn.length != 13) return null
    return withContext(Dispatchers.IO) {
        try {
            val jsonString = URL("https://www.googleapis.com/books/v1/volumes?q=isbn:$isbn&key=$API_KEY").readText()
//            println(jsonString)
            val json = Json { ignoreUnknownKeys = true }
            val jsonObject = json.parseToJsonElement(jsonString).jsonObject

            val items = jsonObject["items"]?.jsonArray ?: return@withContext null
            val bookInfo = items.firstOrNull()?.jsonObject ?: return@withContext null
            val volumeInfo = bookInfo["volumeInfo"]?.jsonObject ?: return@withContext null

            val image = (volumeInfo["imageLinks"]?.jsonObject ?: return@withContext null)["thumbnail"]?.jsonPrimitive?.content ?: ""
            val authors = (volumeInfo["authors"]?.jsonArray ?: emptyList()).map {
                Author(name = it.toString().replace("\"", ""))
            }
            val categories = (volumeInfo["categories"]?.jsonArray ?: emptyList()).map {
                Category(name = it.toString().replace("\"", ""))
            }
            val title = volumeInfo["title"]?.jsonPrimitive?.content ?: ""

            return@withContext BookData(
                book = Book(
                    isbn = isbn,
                    title = title.split(".")[0].trim() + ".",
                    subtitle = title.removePrefix(title.split(".")[0] + ".").trim(),
                    liked = false,
                    series = "",
                    publisher = volumeInfo["publisher"]?.jsonPrimitive?.content ?: "",
                    pages = volumeInfo["pageCount"]?.jsonPrimitive?.intOrNull,
                    language = volumeInfo["language"]?.jsonPrimitive?.content ?: "",
                    description = "",
                    imageThumbnail = setupIMG(image)
                ),
                authors = authors,
                categories = categories,
                people = listOf()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }
}

private fun setupIMG(url: String): String {
    if (url == "")
        return "https://edu.lnf.infn.it/wp-content/uploads/2014/09/book_placeholder.gif"
    val arr = url.split("://")
    return if (arr[0] == "https")
        url
    else
        "${arr[0]}s://${arr[1]}"
}

//https://books.google.com/books/content?id=B0_4zQEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api