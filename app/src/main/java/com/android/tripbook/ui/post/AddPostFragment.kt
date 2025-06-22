package com.android.tripbook

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.tohpoh.R
import com.example.tohpoh.database.DataBaseHelper
import com.example.tohpoh.utils.SessionManager

class AddPostFragment : Fragment(R.layout.item_post) {

    private lateinit var editTextPostContent: EditText
    private lateinit var buttonPublishPost: Button
    private lateinit var dbHelper: DataBaseHelper
    private lateinit var sessionManager: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialisation des vues
        editTextPostContent = view.findViewById(R.id.postContent)
        buttonPublishPost = view.findViewById(R.id.likeButton)

        // Initialisation base de données et session
        dbHelper = DataBaseHelper(requireContext())
        sessionManager = SessionManager(requireContext())

        // Action bouton publier
        buttonPublishPost.setOnClickListener { publishPost() }
    }

    private fun publishPost() {
        val content = editTextPostContent.text.toString().trim()
        val userId = sessionManager.getUserSession()

        if (userId == null || userId == -1) {
            Toast.makeText(requireContext(), "Vous devez être connecté pour publier.", Toast.LENGTH_SHORT).show()
            return
        }

        if (content.isEmpty()) {
            Toast.makeText(requireContext(), "Votre publication est vide !", Toast.LENGTH_SHORT).show()
            return
        }

        val isPublished: Boolean = dbHelper.addPost(userId, content) == true
        if (isPublished) {
            Toast.makeText(requireContext(), "Publication réussie !", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.popBackStack() // Retour
        } else {
            Toast.makeText(requireContext(), "Erreur lors de la publication.", Toast.LENGTH_SHORT).show()
        }
    }
}
