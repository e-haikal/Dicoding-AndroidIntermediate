package com.siaptekno.storyapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.siaptekno.storyapp.R
import com.siaptekno.storyapp.databinding.ActivityMainBinding
import com.siaptekno.storyapp.pref.SessionManager
import com.siaptekno.storyapp.ui.add_story.AddStoryActivity
import com.siaptekno.storyapp.ui.detail.DetailActivity
import com.siaptekno.storyapp.ui.story_map.StoryMapsActivity
import com.siaptekno.storyapp.ui.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: MainAdapter
    private val mainViewModel: MainViewModel by viewModels {
        MainFactory.getInstance(this) // Get an instance of the MainViewModel using a factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the activity's layout and bind UI elements
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Enable edge-to-edge UI for immersive experience
        enableEdgeToEdge()

        // Set up RecyclerView for displaying stories
        setupRecyclerView()

        // Initialize session manager for authentication token
        sessionManager = SessionManager(this)

        // Set up toolbar and apply window insets for layout adjustments
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Check if the user is authenticated and proceed accordingly
        checkAuthentication()

        // Observe ViewModel for updates on data and loading state
        observeViewModel()

        // Set up FAB (Floating Action Button) to navigate to AddStoryActivity
        binding.fabAddStory.setOnClickListener {
            val intent = Intent(this@MainActivity, AddStoryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Re-fetch stories when the activity resumes, if authenticated
        val token = sessionManager.getAuthToken()
        if (token != null) {
            mainViewModel.stories(token)
        }
    }

    private fun setupRecyclerView() {
        // Initialize the adapter with a lambda for handling item clicks
        adapter = MainAdapter { story ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("STORY_ID", story.id) // Pass the story ID for detail view
            }
            startActivity(intent)
        }

        // Configure RecyclerView with layout manager and adapter
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = this@MainActivity.adapter
        }

        // Attach a footer to handle loading state
        binding.recyclerView.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry() // Retry loading on failure
            }
        )
    }

    private fun checkAuthentication() {
        // Verify if the user is authenticated by checking the token
        val token = sessionManager.getAuthToken()
        if (token == null) {
            // If no token found, redirect to login
            navigateToLogin()
        } else {
            // If token exists, fetch stories using the token
            mainViewModel.stories(token)
        }
    }

    private fun observeViewModel() {
        // Observe the ViewModel for paging data and loading state changes
        val token = sessionManager.getAuthToken()
        if (token != null) {
            mainViewModel.stories(token).observe(this) { pagingData ->
                adapter.submitData(lifecycle, pagingData) // Submit the data to the adapter
            }
        }

        // Observe the loading state and show/hide the progress bar accordingly
        mainViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        // Toggle the visibility of the loading progress bar
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu with options for logout and localization
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                // Show confirmation dialog for logout
                showLogoutConfirmation()
                true
            }
            R.id.action_localization -> {
                // Open device localization settings
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            R.id.action_map -> {
                startActivity(Intent(this@MainActivity, StoryMapsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutConfirmation() {
        // Show a confirmation dialog when the user attempts to log out
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.confirm_logout))
            .setMessage(getString(R.string.are_you_sure_you_want_to_logout))
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                logout() // Log out the user
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss() // Close the dialog without action
            }
            .show()
    }

    private fun logout() {
        // Clear the stored authentication token and navigate to login screen
        sessionManager.clearAuthToken()
        navigateToLogin()
    }

    private fun navigateToLogin() {
        // Start the WelcomeActivity and finish the current activity
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}

