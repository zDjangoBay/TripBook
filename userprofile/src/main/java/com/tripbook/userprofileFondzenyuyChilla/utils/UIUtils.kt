package com.example.tripbooktest.utils

import android.view.View
import android.widget.Toast

object UIUtils {
    fun showToast(view: View, message: String) {
        Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()
    }
}
