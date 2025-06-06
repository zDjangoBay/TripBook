package com.android.tripbook.data

object SampleData {
    val sampleUsers = listOf(
        User(
            id = "1",
            name = "Marie Nkomo",
            destination = "Yaoundé, Cameroon",
            profileImageRes = android.R.drawable.ic_menu_gallery
        ),
        User(
            id = "2",
            name = "Paul Essomba",
            destination = "Douala, Cameroon",
            profileImageRes = android.R.drawable.ic_menu_camera
        ),
        User(
            id = "3",
            name = "Fatima Abba",
            destination = "Maroua, Cameroon",
            profileImageRes = android.R.drawable.ic_menu_gallery
        ),
        User(
            id = "4",
            name = "Jean Baptiste",
            destination = "Kribi, Cameroon",
            profileImageRes = android.R.drawable.ic_menu_camera
        )
    )
}
