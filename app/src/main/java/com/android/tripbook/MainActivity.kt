package com.android.tripbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.android.tripbook.ui.theme.TripBookTheme

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TripBookTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    InscriptionScreen()
                    )
                }
            }
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview
@Composable
fun GreetingPreview() {
    TripBookTheme {
        Greeting("Android")
    }
}


class MainActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var dbHelper: DataBaseHelper
    private lateinit var recyclerViewUserPosts: RecyclerView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_home_user)

        sessionManager = SessionManager(this)
        dbHelper = DataBaseHelper(this)

        val userId = sessionManager.getUserSession()

        if (userId == null) {
            redirectToLogin()
            return
        }

        val user = dbHelper.getUserById(userId)
        if (user == null) {
            sessionManager.clearSession()
            redirectToLogin()
            return
        }

        // 1. Affichage du nom utilisateur
        val userNameView = findViewById<TextView>(R.id.textUsername)
        userNameView.text = " ${user.username}"

        // 2. Déconnexion
        val logoutButton = findViewById<Button>(R.id.buttonLogout)
        logoutButton.setOnClickListener {
            sessionManager.clearSession()
            redirectToLogin()
        }

        // 3. Initialisation du RecyclerView
        recyclerViewUserPosts = findViewById(R.id.recyclerViewUserPosts)
        recyclerViewUserPosts.layoutManager = LinearLayoutManager(this)

        // 4. Bouton créer une publication
        val addPostButton = findViewById<Button>(R.id.buttonAddPost)
        addPostButton.setOnClickListener {
            val intent = Intent(this, CreatePostActivity::class.java)
            startActivity(intent)
        }

        // Chargement initial
        chargerLesPublications()
    }

    override fun onResume() {
        super.onResume()
        chargerLesPublications()
    }

    private fun chargerLesPublications() {
        val userId = sessionManager.getUserSession() ?: return
        val posts = dbHelper.getAllPosts().map { post ->
            post.copy(user = dbHelper.getUserById(post.userId))
        }

        recyclerViewUserPosts.adapter = PostAdapter(
            postList = posts,
            onLikeClick = { postId ->
                if (dbHelper.likePost(userId, postId)) {
                    Toast.makeText(this, "Like enregistré", Toast.LENGTH_SHORT).show()
                    chargerLesPublications()
                }
            },
            onCommentClick = { postId ->
                Toast.makeText(this, "Zone commentaire pour le post $postId à venir 💬", Toast.LENGTH_SHORT).show()
            },
            onShareClick = { postId ->
                Toast.makeText(this, "Fonction partager pour le post $postId à venir 📤", Toast.LENGTH_SHORT).show()
            }
        )

    }

    private fun redirectToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}