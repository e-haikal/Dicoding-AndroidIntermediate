package com.siaptekno.storyapp.ui.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.siaptekno.storyapp.databinding.ActivityDetailBinding
import com.siaptekno.storyapp.pref.SessionManager
import com.siaptekno.storyapp.R
import com.siaptekno.storyapp.data.Result
import com.siaptekno.storyapp.data.remote.response.Story

class DetailActivity : AppCompatActivity() {

    // View binding for accessing and manipulating layout views
    private lateinit var binding: ActivityDetailBinding

    // SessionManager for managing user session (e.g., token retrieval)
    private lateinit var sessionManager: SessionManager

    // ViewModel for managing UI-related data in a lifecycle-aware way
    private val detailViewModel: DetailViewModel by viewModels {
        DetailFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate layout using view binding
        binding = ActivityDetailBinding.inflate(layoutInflater)

        // Enable edge-to-edge content for better visual experience
        enableEdgeToEdge()
        setContentView(binding.root)

        // Set up the toolbar with back navigation
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // Initialize SessionManager to retrieve user token
        sessionManager = SessionManager(this)

        // Adjust padding to account for system UI insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Retrieve story ID and token from intent and session
        val storyId = intent.getStringExtra("STORY_ID")
        val token = sessionManager.getAuthToken()

        // Fetch story details if both token and story ID are available
        if (token != null && storyId != null) {
            detailViewModel.getDetail(token, storyId)
        }

        // Observe LiveData from the ViewModel
        observeViewModel()
    }

    // Observe LiveData from ViewModel to update UI based on data changes
    private fun observeViewModel() {
        detailViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading) // Show or hide loading indicator
        }
        detailViewModel.detail.observe(this) { result ->
            result?.let { setDetailData(it) } // Update UI with story details
        }
    }

    // Populate the UI with story details or display an error message
    private fun setDetailData(data: Result<Story>) {
        if (data is Result.Success) {
            val story = data.data
            // Set story name and description
            binding.nameDetail.text = story.name
            binding.descDetail.text = story.description

            // Load and display the story image using Glide
            Glide.with(this)
                .load(story.photoUrl)
                .into(binding.imgDetail)
        } else if (data is Result.Error) {
            // Show error message if data fetching fails
            Toast.makeText(this, data.error, Toast.LENGTH_SHORT).show()
        }
    }

    // Show or hide the progress bar based on loading state
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
