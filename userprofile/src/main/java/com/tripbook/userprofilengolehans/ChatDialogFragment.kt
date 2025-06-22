package com.android.tripbook.userprofilengolehans

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.android.tripbook.R

class ChatDialogFragment : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.layout_chat_dialog, container, false)
        val sendButton = view.findViewById<Button>(R.id.send_button)
        val inputField = view.findViewById<EditText>(R.id.input_field)
        val chatHistory = view.findViewById<TextView>(R.id.chat_history)

        sendButton.setOnClickListener {
            val userInput = inputField.text.toString()
            if (userInput.isNotBlank()) {
                chatHistory.append("\nYou: $userInput")
                inputField.text.clear()
                // TODO: Integrate AI response here
            }
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}
