package com.android.tripbook.ui.chat

<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
xmlns:app="http://schemas.android.com/apk/res-auto"
xmlns:tools="http://schemas.android.com/tools"
android:layout_width="match_parent"
android:layout_height="match_parent"
android:background="#F5F5F5"
tools:context=".ui.chat.GroupChatActivity">

<!-- Toolbar -->
<androidx.appcompat.widget.Toolbar
android:id="@+id/toolbar"
android:layout_width="0dp"
android:layout_height="?attr/actionBarSize"
android:background="@color/colorPrimary"
android:elevation="4dp"
app:layout_constraintEnd_toEndOf="parent"
app:layout_constraintStart_toStartOf="parent"
app:layout_constraintTop_toTopOf="parent">

<TextView
android:id="@+id/tvChatTitle"
android:layout_width="wrap_content"
android:layout_height="wrap_content"
android:layout_gravity="center"
android:text="Trip Group Chat"
android:textColor="@android:color/white"
android:textSize="18sp"
android:textStyle="bold" />

</androidx.appcompat.widget.Toolbar>

<!-- Messages RecyclerView -->
<androidx.recyclerview.widget.RecyclerView
android:id="@+id/rvMessages"
android:layout_width="0dp"
android:layout_height="0dp"
android:layout_marginBottom="8dp"
android:paddingHorizontal="8dp"
android:paddingVertical="8dp"
app:layout_constraintBottom_toTopOf="@+id/messageInputContainer"
app:layout_constraintEnd_toEndOf="parent"
app:layout_constraintStart_toStartOf="parent"
app:layout_constraintTop_toBottomOf="@+id/toolbar"
tools:listitem="@layout/item_message" />

<!-- Message Input Container -->
<LinearLayout
android:id="@+id/messageInputContainer"
android:layout_width="0dp"
android:layout_height="wrap_content"
android:background="@android:color/white"
android:elevation="8dp"
android:orientation="horizontal"
android:padding="12dp"
app:layout_constraintBottom_toBottomOf="parent"
app:layout_constraintEnd_toEndOf="parent"
app:layout_constraintStart_toStartOf="parent">

<EditText
android:id="@+id/etMessage"
android:layout_width="0dp"
android:layout_height="wrap_content"
android:layout_weight="1"
android:background="@drawable/bg_message_input"
android:hint="Type a message..."
android:maxLines="3"
android:padding="12dp"
android:textSize="16sp" />

<ImageButton
android:id="@+id/btnSend"
android:layout_width="48dp"
android:layout_height="48dp"
android:layout_marginStart="8dp"
android:background="@drawable/bg_send_button"
android:contentDescription="Send message"
android:src="@drawable/ic_send"
android:tint="@android:color/white" />

</LinearLayout>

</androidx.constraintlayout.widget.ConstraintLayout>