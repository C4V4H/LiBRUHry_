package com.cava.libruhry.compose

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import androidx.palette.graphics.Palette.Swatch
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.cava.libruhry.R
import com.cava.libruhry.dataclass.BookData
import kotlin.math.pow
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun BookList(
    books: List<BookData>,
    paddings: PaddingValues = PaddingValues(0.dp),
    onBookClick: (BookData) -> Unit = {},
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
) {

    LazyColumn(
        contentPadding = paddings, modifier = modifier.fillMaxSize(),
        //  .padding(paddings),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Spacer(modifier = Modifier.size(7.dp))
            // @TODO add search bar
        }
        items(books, key = { it.book.isbn }) { item ->
            BookRow(
                item = item,
                onClick = {
                    onBookClick(item)
                },
                onLikeEvent = {
                    //TODO implement like event
                }
            )
        }
    }
}


@Composable
fun BookRow(item: BookData, onClick: () -> Unit, onLikeEvent: () -> Unit) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var palette: List<Swatch>? = null
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
            item.book.imageThumbnail, context
        )!!

        bitmap = resultBitmap
        isLoadingGradient = false
    }

    if (bitmap != null) {
//        val colorCover: Map<String, String>?
//        colorCover = PaletteGenerator.extractColorsFromBitmap(bitmap)
        palette = PaletteGenerator.extractColorsFromBitmap(bitmap, true)?.swatches?.sortedByDescending { it.population }
//        brush = Brush.linearGradient(
//            colors = listOfNotNull(
//                Color(android.graphics.Color.parseColor(colorCover?.get("mutedSwatch"))),
//                Color(android.graphics.Color.parseColor(colorCover?.get("lightVibrant"))),
//                Color(android.graphics.Color.parseColor(colorCover?.get("vibrant"))),
//                Color(android.graphics.Color.parseColor(colorCover?.get("darkVibrant"))),
//            )
//        )
        val result = palette?.let { findMostPopulousDifferentColors(it, 100.0) }
        brush = Brush.linearGradient(

//            colors = palette?.map { swatch ->
//                Color(swatch.rgb)
//            } ?: listOf(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.secondaryContainer)

            colors = listOfNotNull(
                result?.first?.let { Color(it.rgb) },
                result?.first?.let { Color(it.rgb) },
                result?.second?.let { Color(it.rgb) },
                )
//            listOfNotNull(
////                palette?.mutedSwatch?.let { Color(it.rgb) } ?: MaterialTheme.colorScheme.secondaryContainer,
//                palette?.get(0)?.let { Color(it.rgb) },
//                palette?.get(palette.size/2)?.let { Color(it.rgb) },
//            )
        )
        item.brush = brush
    }

    Row(modifier = Modifier
        .bounceClick { onClick() }
        .fillMaxWidth()
        .padding(vertical = 6.dp, horizontal = 10.dp)
        .clip(RoundedCornerShape(10.dp))
        .then(
            if (palette == null) Modifier.background(MaterialTheme.colorScheme.secondaryContainer)
            else Modifier.background(brush)
        )
    ) {
        AsyncImage(
            model = item.book.imageThumbnail,
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
                text = item.book.title,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = item.authors.joinToString(separator = ", ") { it.name },
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        IconButton(
            onClick = onLikeEvent,
            modifier = Modifier
                .padding(10.dp)
                .clip(RoundedCornerShape(10.dp))
                .size(30.dp)
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .padding(2.dp),
                imageVector = if (item.book.liked) Icons.Filled.Favorite else Icons.Outlined.Favorite,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary//Container
            )
        }
    }
}

@Composable
fun AnimatedHeart(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    var isPlaying by remember { mutableStateOf(false) }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.heart_animation_pop))
    val progress by animateLottieCompositionAsState(
        composition,
        isPlaying = isPlaying,
    )

    LottieAnimation(composition = composition,
        progress = { progress },
        modifier = modifier.noRippleClickable {
            isPlaying = !isPlaying
            onClick()
        })
}


/*
val context = LocalContext.current
            val bitmap = remember {
                BitmapFactory.decodeResource(context.resources, R.drawable.provaimmaginegradiente)
            }
            val palette = remember {
                Palette.from(bitmap).generate()
            }
            val vibrantSwatch = palette.vibrantSwatch
            val lightVibrantSwatch = palette.lightVibrantSwatch
            val darkVibrantSwatch = palette.darkVibrantSwatch
            val mutedSwatch = palette.mutedSwatch
            val brush = Brush.linearGradient(
                colors = listOfNotNull(
                    vibrantSwatch?.let { Color(it.rgb) },
                    lightVibrantSwatch?.let { Color(it.rgb) },
                    mutedSwatch?.let { Color(it.rgb) },
                    darkVibrantSwatch?.let { Color(it.rgb) },
                )
            )


            Row(
                modifier = Modifier
                    .bounceClick {
                        // @TODO navigate to detail
                    }
                    .fillMaxWidth()
                    .padding(6.dp)
                    .clip(RoundedCornerShape(10.dp))
//                    .border(1.dp, Color.Black, RoundedCornerShape(10.dp))
                    .background(brush = brush/*MaterialTheme.colorScheme.secondaryContainer*/)
            ) {
                AsyncImage(
                    model = item.book.imageThumbnail,
                    contentDescription = "",
                    placeholder = painterResource(id = R.drawable.copertina_prova),
                    error = painterResource(id = R.drawable.cool_image),
                    onLoading = {
                        println("LODING...")
                    },
                    onError = {
                        println("ERROR: ${it.result.request.error}")
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
                        text = item.book.title,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = item.authors.joinToString(separator = ", ") { it.firstName + " " + it.lastName },
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                IconButton(
                    onClick = { item.book.liked = !item.book.liked },
                    modifier = Modifier
                        .padding(10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .size(30.dp)
                ) {
                    Icon(
                        modifier = Modifier
                            .size(24.dp)
                            .padding(2.dp),
                        imageVector = if (!item.book.liked) Icons.Outlined.Favorite else Icons.Filled.Favorite,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary//Container
                    )
                }
            }
 */
fun calculateColorDistance(swatch1: Swatch, swatch2: Swatch): Double {
    val r1 = (swatch1.rgb shr 16) and 0xFF
    val g1 = (swatch1.rgb shr 8) and 0xFF
    val b1 = swatch1.rgb and 0xFF

    val r2 = (swatch2.rgb shr 16) and 0xFF
    val g2 = (swatch2.rgb shr 8) and 0xFF
    val b2 = swatch2.rgb and 0xFF

    return sqrt((r1 - r2).toDouble().pow(2.0) + (g1 - g2).toDouble().pow(2.0) + (b1 - b2).toDouble().pow(2.0))
}

//TODO if not too distance use last
fun findMostPopulousDifferentColors(swatches: List<Swatch>, minDistance: Double): Pair<Swatch, Swatch>? {
    val sortedSwatches = swatches.sortedByDescending { it.population }

    for (i in sortedSwatches.indices) {
        for (j in i + 1 until sortedSwatches.size) {
            if (calculateColorDistance(sortedSwatches[i], sortedSwatches[j]) >= minDistance) {
                return Pair(sortedSwatches[i], sortedSwatches[j])
            }
        }
    }
    return null
}