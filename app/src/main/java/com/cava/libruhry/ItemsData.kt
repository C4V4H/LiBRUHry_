package com.cava.libruhry

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Stable
import com.cava.libruhry.compose.Pages
import com.exyte.navbar.colorButtons.BellColorButton
import com.exyte.navbar.colorButtons.ButtonBackground
import com.exyte.navbar.colorButtons.ColorButtonAnimation

@Stable
data class WiggleButtonItem(
    val page: Pages,
    @DrawableRes val backgroundIcon: Int,
    val icon: Int,
    var isSelected: Boolean,
    @StringRes val description: Int,
    val imageUrl: String? = null,
    val animationType: ColorButtonAnimation = BellColorButton(
        tween(500),
        background = ButtonBackground(R.drawable.plus)
    ),
)


@Stable
data class Item(
    @DrawableRes val icon: Int,
    var isSelected: Boolean,
    @StringRes val description: Int,
    val animationType: ColorButtonAnimation = BellColorButton(
        tween(500),
        background = ButtonBackground(R.drawable.plus)
    ),
)

val wiggleButtonItems = listOf(
    WiggleButtonItem(
        page = Pages.FAVORITES,
        icon = R.drawable.outline_favorite,
        backgroundIcon = R.drawable.favorite,
        isSelected = false,
        description = R.string.Heart,
    ),
    WiggleButtonItem(
        page = Pages.HOME,
        icon = R.drawable.outline_house,
        backgroundIcon = R.drawable.house,
        isSelected = false,
        description = R.string.Home,
    ),
    WiggleButtonItem(
        page = Pages.PROFILE,
        icon = R.drawable.outline_user, // Icona nulla poiché verrà utilizzata un'immagine asincrona
        backgroundIcon = R.drawable.user,
        isSelected = false,
        description = R.string.Person,
        imageUrl = "URL_dell_immagine_asincrona" // URL dell'immagine asincrona da caricare
    )
)
