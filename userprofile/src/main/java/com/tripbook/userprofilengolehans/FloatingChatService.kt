package com.android.tripbook.userprofilengolehans

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import com.android.tripbook.R

class FloatingChatService : Service() {
    private lateinit var windowManager: WindowManager
    private var chatHeadView: View? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        android.widget.Toast.makeText(this, "FloatingChatService started", android.widget.Toast.LENGTH_SHORT).show()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        addChatHead()
    }

    private fun addChatHead() {
        val inflater = LayoutInflater.from(this)
        chatHeadView = inflater.inflate(R.layout.layout_floating_chat, null)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.END or Gravity.BOTTOM
        params.x = 50
        params.y = 100

        windowManager.addView(chatHeadView, params)

        chatHeadView?.findViewById<ImageView>(R.id.chat_head_icon)?.setOnClickListener {
            // TODO: Open chat dialog
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (chatHeadView != null) windowManager.removeView(chatHeadView)
    }
}
