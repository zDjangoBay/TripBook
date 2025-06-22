package com.android.tripbook

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tohpoh.CreatePostActivity
import com.example.tohpoh.MainActivity
import com.example.tohpoh.R
import com.example.tohpoh.adapters.PostAdapter
import com.example.tohpoh.database.DataBaseHelper
import com.example.tohpoh.utils.SessionManager

class HomeUserFragment : Fragment(R.layout.fragment_home_user) {
    private lateinit var dbHelper: DataBaseHelper
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: PostAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DataBaseHelper(requireContext())
        sessionManager = SessionManager(requireContext())

        val recyclerViewUserPosts = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerViewUserPosts)
        recyclerViewUserPosts.layoutManager = LinearLayoutManager(requireContext())


        view.findViewById<Button>(R.id.buttonAddPost).setOnClickListener {
            startActivity(Intent(requireContext(), CreatePostActivity::class.java))
        }

        view.findViewById<Button>(R.id.buttonLogout).setOnClickListener {
            sessionManager.clearSession()
            startActivity(Intent(requireContext(), MainActivity::class.java))
        }
    }




}
