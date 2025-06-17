package com.android.tripbook.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.android.tripbook.R

enum class BottomNavItem(
    val route: String,
    @DrawableRes val iconRes: Int,
    @StringRes val labelRes: Int
) {
    Home("home", R.drawable.bottom_btn1, R.string.Home),
    Schedule("schedule", R.drawable.bottom_btn2, R.string.Explorer),
    Catalog("catalog", R.drawable.bottom_btn3, R.string.Catalog),
    Profile("profile", R.drawable.bottom_btn4, R.string.Profile),
    Notification("notification", R.drawable.ic_notification, R.string.Notification);

    companion object {
        fun fromRoute(route: String?): BottomNavItem? {
            return values().find { it.route == route }
        }
    }
}
