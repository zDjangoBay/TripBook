package com.android.tripbook

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tohpoh.R
import com.example.tohpoh.adapters.PostAdapter
import com.example.tohpoh.database.DataBaseHelper
import com.example.tohpoh.models.Post
import com.example.tohpoh.ui.auth.LoginActivity
import com.example.tohpoh.ui.auth.RegisterActivity
import com.example.tohpoh.utils.SessionManager

class HomeVisitorFragment : Fragment(R.layout.fragment_home_user) {
    private lateinit var buttonLogin: Button
    private lateinit var buttonRegister: Button
    private lateinit var dbHelper: DataBaseHelper
    private lateinit var adapter: PostAdapter
    private lateinit var sessionManager: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonLogin = view.findViewById(R.id.buttonLogin)
        buttonRegister = view.findViewById(R.id.buttonRegister)
        val recyclerViewPosts = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerViewPosts)

        dbHelper = DataBaseHelper(requireContext())
        sessionManager = SessionManager(requireContext())

        recyclerViewPosts.layoutManager = LinearLayoutManager(requireContext())
        loadPosts(recyclerViewPosts)

        buttonLogin.setOnClickListener {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }

        buttonRegister.setOnClickListener {
            startActivity(Intent(requireContext(), RegisterActivity::class.java))
        }
    }

    private fun loadPosts(recyclerView: androidx.recyclerview.widget.RecyclerView) {
        val posts = dbHelper.getAllPosts()
        adapter = PostAdapter(posts, { redirectToLogin() }, { redirectToLogin() }, { redirectToLogin() })

        recyclerView.adapter = adapter
    }

    private fun redirectToLogin() {
        startActivity(Intent(requireContext(), LoginActivity::class.java))
    }
}
