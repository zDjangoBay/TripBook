package com.android.tripbook

import android.os.Bundle
//import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.*
import androidx.core.view.setPadding
import android.graphics.Color
//import androidx.compose.ui.graphics.Color

class SouvenirActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var selectImageBtn: Button
    private lateinit var descriptionEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var savedSouvenirsLayout: LinearLayout
    private lateinit var souvenirInstruction: TextView
    private lateinit var souvenirHeading: TextView
    private lateinit var appTitle: TextView

    private var selectedImageUri: Uri? = null
    private val IMAGE_PICK_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_souvenir)

        imageView = findViewById(R.id.imageView)
        selectImageBtn = findViewById(R.id.chooseImageButton)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        saveButton = findViewById(R.id.saveButton)
        savedSouvenirsLayout = findViewById(R.id.savedSouvenirsLayout)
        souvenirInstruction = findViewById(R.id.souvenirInstruction)
        souvenirHeading  = findViewById(R.id.souvenirHeading)
        appTitle = findViewById(R.id.souvenirHeading)

        selectImageBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        saveButton.setOnClickListener {
            if (selectedImageUri != null) {
                val description = descriptionEditText.text.toString()

                val context = this
                val entryLayout = LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    setPadding(0, 20, 0, 20)
                }

                val savedImage = ImageView(context).apply {
                    setImageURI(selectedImageUri)
                    layoutParams = LinearLayout.LayoutParams(300, 300)
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }

                val descriptionView = TextView(context).apply {
                    text = description
                    setTextColor(Color.BLACK)  // Ensures visibility
                    textSize = 16f
                    setPadding(16, 0, 0, 0)
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                }

                entryLayout.addView(savedImage)
                entryLayout.addView(descriptionView)
                savedSouvenirsLayout.addView(entryLayout)

                // Clear fields after saving
                imageView.setImageDrawable(null)
                descriptionEditText.text.clear()
                selectedImageUri = null
            } else {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            selectedImageUri = data?.data
            imageView.setImageURI(selectedImageUri)
        }
    }

    private fun addSouvenirToLayout(imageUri: Uri, description: String) {
        val container = LinearLayout(this)
        container.orientation = LinearLayout.HORIZONTAL
        container.setPadding(16)

        val imageView = ImageView(this)
        imageView.setImageURI(imageUri)
        imageView.layoutParams = LinearLayout.LayoutParams(200, 200)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP

        val textView = TextView(this)
        textView.text = description
        textView.setPadding(16, 0, 0, 0)
        textView.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

        container.addView(imageView)
        container.addView(textView)

        savedSouvenirsLayout.addView(container)
    }
}
