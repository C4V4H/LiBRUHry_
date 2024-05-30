package com.cava.libruhry.compose

import android.view.animation.OvershootInterpolator
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.cava.libruhry.R
import com.cava.libruhry.backend.getBookData
import com.cava.libruhry.dataclass.BookData
import com.cava.libruhry.viewmodel.LiBRUHryEvent
import com.cava.libruhry.viewmodel.LiBRUHryState
import com.cava.libruhry.wiggleButtonItems
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Parabolic
import com.exyte.animatednavbar.animation.indendshape.Height
import com.exyte.animatednavbar.items.wigglebutton.WiggleButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val Duration = 500
const val DoubleDuration = 1000

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MainView(
    state: LiBRUHryState,
    onEvent: (LiBRUHryEvent) -> Unit,
    cameraPermissionResultLauncher: ManagedActivityResultLauncher<String, Boolean>,
) {
    var page by remember {
        mutableStateOf(Pages.HOME)
    }
    val mainNavController = rememberNavController()

    var scannedBarcode by remember { mutableStateOf<String?>(null) } // Aggiunto stato per salvare il barcode scansionato
    val navigator = rememberListDetailPaneScaffoldNavigator<Any>()

    // @TODO Add the animation and the swipe for the changing of the page


    NavigableListDetailPaneScaffold(
        navigator = navigator,
        listPane = {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    // this is 64.dp height :P
                    TopAppBar(
                        navigationIcon = {
                            if (page.undoAction != null) {
                                IconButton(
                                    onClick = {
                                        mainNavController.navigate("HOME")
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ArrowBack,
                                        contentDescription = null,
                                    )
                                }
                            }
                        },
                        title = {
                            Text(text = page.description)
                        },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.tertiary),
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            scrolledContainerColor = MaterialTheme.colorScheme.secondary,
                            navigationIconContentColor = MaterialTheme.colorScheme.tertiary,
                            titleContentColor = MaterialTheme.colorScheme.tertiary,
                            actionIconContentColor = MaterialTheme.colorScheme.tertiary
                        )
                    )

                },
                floatingActionButton = {
                    if (page == Pages.HOME) {
                        ExtendedFloatingActionButton(
                            onClick = { mainNavController.navigate("SCANNER") },
                            icon = { Icon(Icons.Filled.Add, null) },
                            text = {
                                Text(text = "New Book")
                            },
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            expanded = true
                        )
                    } else if (page == Pages.SCANNER) {
                        ExtendedFloatingActionButton(
                            onClick = { mainNavController.navigate("ISBN") },
                            icon = { Icon(Icons.Filled.Create, null) },
                            text = {
                                Text(text = "Use ISBN")
                            },
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            expanded = true
                        )
                    }
                },
                bottomBar = {
                    AnimatedNavigationBar(
                        modifier = Modifier.height(75.dp),
                        selectedIndex = page.index,
                        ballColor = MaterialTheme.colorScheme.primary,
                        ballAnimation = Parabolic(tween(Duration, easing = LinearOutSlowInEasing)),
                        indentAnimation = Height(
                            indentWidth = 56.dp,
                            indentHeight = 15.dp,
                            animationSpec = tween(DoubleDuration,
                                easing = { OvershootInterpolator().getInterpolation(it) })
                        )
                    ) {
                        wiggleButtonItems.forEach { item ->
                            WiggleButton(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                isSelected = page == item.page,
                                onClick = {
                                    page = item.page
                                    mainNavController.navigate("HOME")
                                },
                                icon = item.icon,
                                backgroundIcon = item.backgroundIcon,
                                wiggleColor = MaterialTheme.colorScheme.primary,
                                outlineColor = MaterialTheme.colorScheme.primary,
                                backgroundIconColor = MaterialTheme.colorScheme.primaryContainer,
                                contentDescription = stringResource(id = item.description),
                                enterExitAnimationSpec = tween(
                                    durationMillis = Duration, easing = LinearEasing
                                ),
                                wiggleAnimationSpec = spring(dampingRatio = .45f, stiffness = 35f)
                            )
                        }
                    }
                }
            ) { paddingValues ->

                when (page) {
                    Pages.FAVORITES -> {
                        Column(
                            modifier = Modifier.padding(paddingValues)
                        ) {
                            Text(
                                fontSize = 30.sp,
                                text = ""
                            )
                        }
                    }

                    // @TODO add the list of books
                    Pages.HOME, Pages.SCANNER, Pages.ISBN, Pages.NEW_BOOK -> {
                        if (page != Pages.NEW_BOOK) scannedBarcode = null
                        HomeView(
                            navController = mainNavController,
                            scanAction = {
                                scannedBarcode?.let {
                                    Pages.NEW_BOOK.undoAction = Pages.SCANNER
                                    page = Pages.NEW_BOOK
                                    NewBookView(barcodeValue = it)
                                } ?: run {
                                    CameraScreen(cameraPermissionResultLauncher = cameraPermissionResultLauncher) { barcode ->
                                        scannedBarcode = barcode
                                    }
                                }
                            },
                            setPage = {
                                page = it
                            },
                            paddings = paddingValues,
                            books = state.books,
                            onBookClick = { item ->
                                navigator.navigateTo(
                                    pane = ListDetailPaneScaffoldRole.Detail,
                                    content = item
                                )
                            },
                            bookAddEvent = {
                                onEvent(LiBRUHryEvent.SaveBook(it))
                            }
                        )
                    }

                    // @TODO also this mf
                    Pages.PROFILE, Pages.SETTINGS -> {
                    }
                }
            }
        },
        detailPane = {
            val item = navigator.currentDestination?.content as? BookData
            var isLoadingImage by remember {
                mutableStateOf(true)
            }
            if (item != null) {
//                AnimatedPane { }
                Scaffold(
                    topBar = {
                        TopAppBar(
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        navigator.navigateBack()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ArrowBack,
                                        contentDescription = null,
                                    )
                                }
                            },
                            title = {
                                Text(text = "Book Details")
                            },
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.tertiary),
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                scrolledContainerColor = MaterialTheme.colorScheme.secondary,
                                navigationIconContentColor = MaterialTheme.colorScheme.tertiary,
                                titleContentColor = MaterialTheme.colorScheme.tertiary,
                                actionIconContentColor = MaterialTheme.colorScheme.tertiary
                            )
                        )
                    }
                ) { paddings ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddings)
                            .background(item.brush)
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
                                .height(200.dp)
                                .padding(top = 20.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .align(Alignment.CenterHorizontally),
                        )

                        Text(
                            text = item.book.title,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(20.dp)
                        )

                        Text(
                            text = item.authors.joinToString(separator = ", ") { it.name },
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .padding(20.dp)
                                .clickable {
                                    navigator.navigateTo(
                                        pane = ListDetailPaneScaffoldRole.Extra,
                                        content = item.categories
                                    )
                                }
                        )

                        Text(
                            text = item.categories.joinToString(separator = ", ") { it.name },
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .padding(20.dp)
                                .clickable {
                                    navigator.navigateTo(
                                        pane = ListDetailPaneScaffoldRole.Extra,
                                        content = item.categories
                                    )
                                }
                        )


                        val context = LocalContext.current
                        var bookData by remember { mutableStateOf<BookData?>(null) }

                        Button(onClick = {
                            CoroutineScope(Dispatchers.Main).launch {
                                bookData = getBookData("9788878876682")
                            }
                        }) {
                            Text(text = "Read")
                        }

                        // Puoi usare bookData per mostrare i dati del libro nella tua UI
                        bookData?.let {
                            Text(text = it.book.title)
                        }
                    }
                }

            }

        },
        extraPane = {

        }
    )
}