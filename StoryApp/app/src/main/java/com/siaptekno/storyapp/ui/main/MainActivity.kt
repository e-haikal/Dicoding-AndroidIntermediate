package com.siaptekno.storyapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
import com.siaptekno.storyapp.ui.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: MainAdapter
    private val mainViewModel: MainViewModel by viewModels {
        MainFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        sessionManager = SessionManager(this)
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        checkAuthentication()
        observeViewModel()

        binding.fabAddStory.setOnClickListener {
            val intent = Intent(this@MainActivity, AddStoryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val token = sessionManager.getAuthToken()
        if (token != null) {
            mainViewModel.stories(token)
        }
    }

    private fun setupRecyclerView() {
        adapter = MainAdapter { story ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("STORY_ID", story.id)
            }
            startActivity(intent)
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = this@MainActivity.adapter
        }
        binding.recyclerView.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter{
                adapter.retry()
            }
        )
    }


    private fun checkAuthentication() {
        val token = sessionManager.getAuthToken()
        if (token == null) {
            navigateToLogin()
        } else {
            mainViewModel.stories(token)
        }
    }

    private fun observeViewModel() {
        val token = sessionManager.getAuthToken()
        if (token != null) {
            mainViewModel.stories(token).observe(this) { pagingData ->
                adapter.submitData(lifecycle, pagingData)
            }
        }
        mainViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                showLogoutConfirmation()
                true
            }
            R.id.action_localization -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.confirm_logout))
            .setMessage(getString(R.string.are_you_sure_you_want_to_logout))
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                logout()
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun logout() {
        sessionManager.clearAuthToken()
        navigateToLogin()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
