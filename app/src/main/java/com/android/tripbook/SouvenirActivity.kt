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

class SouvenirActivity : AppCompatActivity() {

    private lateinit var imagePreview: ImageView
    private lateinit var selectImageBtn: Button
    private lateinit var descriptionEditText: EditText
    private lateinit var saveBtn: Button
    private lateinit var souvenirLayout: LinearLayout
    private lateinit var souvenirInstruction: TextView
    private lateinit var souvenirHeading: TextView
    private lateinit var appTitle: TextView

    private var selectedImageUri: Uri? = null
    private val IMAGE_PICK_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_souvenir)

        imagePreview = findViewById(R.id.imageView)
        selectImageBtn = findViewById(R.id.chooseImageButton)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        saveBtn = findViewById(R.id.saveButton)
        souvenirLayout = findViewById(R.id.savedSouvenirsLayout)
        souvenirInstruction = findViewById(R.id.souvenirInstruction)
        souvenirHeading  = findViewById(R.id.souvenirHeading)
        appTitle = findViewById(R.id.souvenirHeading)

        selectImageBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        saveBtn.setOnClickListener {
            val description = descriptionEditText.text.toString()
            if (selectedImageUri != null && description.isNotBlank()) {
                addSouvenirToLayout(selectedImageUri!!, description)
                // Clear UI for next entry
                imagePreview.setImageURI(null)
                descriptionEditText.text.clear()
                selectedImageUri = null
                Toast.makeText(this, "Souvenir saved!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please select an image and write a description", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            selectedImageUri = data?.data
            imagePreview.setImageURI(selectedImageUri)
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

        souvenirLayout.addView(container)
    }
}
