package com.cava.libruhry.compose

enum class Pages(val index: Int = -1, val description: String = "", var undoAction: Pages? = null) {
    FAVORITES(0, "Favorites"),
    HOME(1, "Home"),
    SCANNER(1, "Frame the book's barcode", undoAction = HOME),
    ISBN(1, "Add a new Book", undoAction = SCANNER),
    NEW_BOOK(1, "Add the new Book information", undoAction = HOME),
    PROFILE(2, "Your Profile"),
    SETTINGS(2, "Settings");

}