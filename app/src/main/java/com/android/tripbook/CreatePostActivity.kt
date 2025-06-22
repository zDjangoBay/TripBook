package com.android.tripbook

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tohpoh.database.DataBaseHelper
import com.example.tohpoh.models.Post
import com.example.tohpoh.utils.ImageUtils
import com.example.tohpoh.utils.SessionManager

class CreatePostActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var dbHelper: DataBaseHelper
    private var selectedImageUri: Uri? = null

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_first) // Utilisez le bon nom de layout

        sessionManager = SessionManager(this)
        dbHelper = DataBaseHelper(this)

        val buttonBack: ImageButton = findViewById(R.id.buttonBack)
        val buttonPublish: Button = findViewById(R.id.buttonPublish)
        val buttonAddImage: Button = findViewById(R.id.buttonAddImage)
        val editTextContent: EditText = findViewById(R.id.editTextContent)
        val imagePreview: ImageView = findViewById(R.id.imagePreview)
        val spinnerPrivacy: Spinner = findViewById(R.id.spinnerPrivacy)

        // Configurer le spinner de confidentialité
        val privacyOptions = arrayOf("Public", "Amis", "Moi uniquement")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, privacyOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPrivacy.adapter = adapter

        // Bouton de retour
        buttonBack.setOnClickListener {
            finish()
        }

        // Bouton pour ajouter une image
        buttonAddImage.setOnClickListener {
            ImageUtils.openImagePicker(this)
        }

        // Bouton de publication
        buttonPublish.setOnClickListener {
            val content = editTextContent.text.toString().trim()
            val privacy = spinnerPrivacy.selectedItem.toString()
            val userId = sessionManager.getUserSession()

            if (userId == null) {
                Toast.makeText(this, "Session invalide. Veuillez vous reconnecter.", Toast.LENGTH_SHORT).show()
                finish()
                return@setOnClickListener
            }

            if (content.isEmpty()) {
                Toast.makeText(this, "Veuillez écrire quelque chose", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Convertir l'image en chaîne base64 si elle existe
            val imageBase64 = selectedImageUri?.let { uri ->
                ImageUtils.uriToBase64(this, uri)
            }

            // Créer un nouveau post
            val newPost = Post(
                id = 0,
                userId = userId,
                content = content,
                imageUrl = imageBase64,
                createdAt = System.currentTimeMillis().toString(),
                likeCount = 0,
                commentCount = 0,
                shareCount = 0,
                privacy = privacy,
                user = null
            )

            // Insérer dans la base de données
            try {
                if (dbHelper.addPost(newPost)) {
                    Toast.makeText(this, "Publication créée avec succès", Toast.LENGTH_SHORT).show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        setResult(Activity.RESULT_OK)
                        finish()
                    }, 1000)
                } else {
                    Toast.makeText(this, "Erreur lors de la publication", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Erreur DB: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == ImageUtils.PICK_IMAGE_REQUEST) {
            data?.data?.let { uri ->
                selectedImageUri = uri
                val imagePreview: ImageView = findViewById(R.id.imagePreview)
                imagePreview.setImageURI(uri)
                imagePreview.visibility = View.VISIBLE
            }
        }
    }
}