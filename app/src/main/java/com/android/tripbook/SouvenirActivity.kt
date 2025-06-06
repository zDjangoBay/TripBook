package com.android.tripbook

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class SouvenirActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var selectImageBtn: MaterialButton
    private lateinit var descriptionEditText: TextInputEditText
    private lateinit var saveButton: MaterialButton
    private lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var emptyStateLayout: View
    private lateinit var repository: SouvenirRepository
    private lateinit var adapter: SouvenirAdapter
    private var selectedImageUri: Uri? = null

    // Permission launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            pickImage()
        } else {
            Toast.makeText(this, "Permission denied. Cannot select image.", Toast.LENGTH_SHORT).show()
        }
    }

    // Image picker launcher
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            imageView.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_souvenir)

        // Initialize views
        imageView = findViewById(R.id.imageView)
        selectImageBtn = findViewById(R.id.chooseImageButton)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        saveButton = findViewById(R.id.saveButton)
        recyclerView = findViewById(R.id.savedSouvenirsRecyclerView)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)

        // Initialize Room
        val database = AppDatabase.getDatabase(this)
        repository = SouvenirRepository(database.souvenirDao())

        // Setup RecyclerView
        adapter = SouvenirAdapter(
            mutableListOf(),
            onDeleteClick = { souvenir ->
                lifecycleScope.launch {
                    repository.delete(souvenir)
                }
            },
            onImageClick = { uri ->
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "image/*")
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                }
                startActivity(intent)
            }
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        // Observe souvenirs
        lifecycleScope.launch {
            repository.allSouvenirs.collect { souvenirs ->
                adapter.updateSouvenirs(souvenirs)
                emptyStateLayout.visibility = if (souvenirs.isEmpty()) View.VISIBLE else View.GONE
            }
        }

        // Setup click listeners
        selectImageBtn.setOnClickListener {
            val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                android.Manifest.permission.READ_MEDIA_IMAGES
            } else {
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            }
            if (checkSelfPermission(permission) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                pickImage()
            } else {
                requestPermissionLauncher.launch(permission)
            }
        }

        saveButton.setOnClickListener {
            val description = descriptionEditText.text.toString().trim()
            if (selectedImageUri == null) {
                Toast.makeText(this, R.string.select_image_prompt, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (description.isEmpty()) {
                Toast.makeText(this, R.string.description_empty_prompt, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val savedImageUri = saveImageToInternalStorage(selectedImageUri!!)
                if (savedImageUri != null) {
                    val souvenir = Souvenir(
                        imageUri = savedImageUri.toString(),
                        description = description,
                        timestamp = System.currentTimeMillis()
                    )
                    repository.insert(souvenir)
                    // Clear inputs
                    imageView.setImageDrawable(null)
                    descriptionEditText.text?.clear()
                    selectedImageUri = null
                } else {
                    Toast.makeText(this@SouvenirActivity, "Failed to save image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun pickImage() {
        imagePickerLauncher.launch("image/*")
    }

    private suspend fun saveImageToInternalStorage(uri: Uri): Uri? = withContext(Dispatchers.IO) {
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            val compressedBitmap = compressBitmap(bitmap)
            val file = File(filesDir, "souvenir_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { out ->
                compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
            }
            FileProvider.getUriForFile(
                this@SouvenirActivity,
                "${packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun compressBitmap(bitmap: Bitmap): Bitmap {
        val maxSize = 1024
        var width = bitmap.width
        var height = bitmap.height
        val aspectRatio = width.toFloat() / height
        if (width > height) {
            if (width > maxSize) {
                width = maxSize
                height = (width / aspectRatio).toInt()
            }
        } else {
            if (height > maxSize) {
                height = maxSize
                width = (height * aspectRatio).toInt()
            }
        }
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }
}