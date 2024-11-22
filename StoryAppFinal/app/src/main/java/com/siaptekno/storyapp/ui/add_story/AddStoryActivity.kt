package com.siaptekno.storyapp.ui.add_story

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.siaptekno.storyapp.R
import com.siaptekno.storyapp.databinding.ActivityAddStoryBinding
import com.siaptekno.storyapp.pref.SessionManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import com.siaptekno.storyapp.data.Result
import com.siaptekno.storyapp.ui.main.MainActivity
import com.siaptekno.storyapp.utils.getImageUri
import com.siaptekno.storyapp.utils.reduceFileImage
import com.siaptekno.storyapp.utils.uriToFile
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddStoryActivity : AppCompatActivity() {

    // Binding for accessing layout views
    private lateinit var binding: ActivityAddStoryBinding

    // SessionManager for handling user session (e.g., auth token)
    private lateinit var sessionManager: SessionManager

    // ViewModel to handle UI-related data in lifecycle-conscious way
    private val viewModel: AddStoryViewModel by viewModels {
        AddStoryFactory.getInstance(this)
    }

    // Holds the URI of the selected image
    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        enableEdgeToEdge() // Enables edge-to-edge content for better visual experience
        setContentView(binding.root)

        // Set up the toolbar with back navigation
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // Adjust padding to account for system UI insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize session manager and get user token
        sessionManager = SessionManager(this)
        val token = sessionManager.getAuthToken().toString()

        observeViewModel() // Set up ViewModel observers for live data

        // Set up button listeners
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnCamera.setOnClickListener { startCamera() }
        binding.buttonAdd.setOnClickListener { uploadImage(token) }
    }

    // Observes LiveData from ViewModel and updates UI accordingly
    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }
        viewModel.uploadResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    // Show success message and navigate to MainActivity
                    Toast.makeText(this, result.data.message, Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
                is Result.Error -> {
                    // Show error message
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Show or hide loading indicator
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    // Validates input and uploads the image with description
    private fun uploadImage(token: String) {
        val description = binding.edAddDescription.text.toString().trim()

        // Validate description input
        if (description.isEmpty()) {
            Toast.makeText(this, getString(R.string.please_add_description), Toast.LENGTH_SHORT).show()
            return
        }

        // Validate image selection
        if (currentImageUri == null) {
            Toast.makeText(this, getString(R.string.please_upload_an_image), Toast.LENGTH_SHORT).show()
            return
        }

        // Convert the selected image URI to a file and compress it
        val file = uriToFile(currentImageUri!!, this).reduceFileImage()

        // Create request bodies for description and image
        val descriptionRequestBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo", file.name, requestImageFile
        )

        // Call ViewModel to upload the story
        viewModel.uploadStory(token, descriptionRequestBody, multipartBody, null, null)
    }

    // Launch gallery picker for selecting an image
    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    // Result launcher for gallery selection
    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage() // Display the selected image
        } else {
            Toast.makeText(this, getString(R.string.no_media_selected), Toast.LENGTH_SHORT).show()
        }
    }

    // Launch camera to capture an image
    private fun startCamera() {
        currentImageUri = getImageUri(this) // Create URI for the captured image
        launcherIntentCamera.launch(currentImageUri!!)
    }

    // Result launcher for camera capture
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage() // Display the captured image
        } else {
            currentImageUri = null // Reset URI if capture fails
        }
    }

    // Display the selected or captured image in the ImageView
    private fun showImage() {
        currentImageUri?.let {
            binding.imgPreview.setImageURI(it)
        }
    }
}
