package com.siaptekno.storyapp.ui.story_map

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.siaptekno.storyapp.R
import com.siaptekno.storyapp.databinding.ActivityStoryMapsBinding
import com.siaptekno.storyapp.pref.SessionManager
import com.siaptekno.storyapp.data.Result
import com.siaptekno.storyapp.data.remote.response.ListStoryItem

class StoryMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityStoryMapsBinding
    private lateinit var sessionManager: SessionManager

    // Lazy initialization of StoryMapsViewModel using a factory.
    private val storyMapsViewModel: StoryMapsViewModel by viewModels {
        StoryMapsFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStoryMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sessionManager = SessionManager(this) // Initialize session manager for token management.

        val token = sessionManager.getAuthToken() // Retrieve the user authentication token.

        if (token != null) {
            // Fetch stories with location if token is valid.
            storyMapsViewModel.getAllStoriesWithMap(token)
        }

        observeViewModel() // Observe LiveData changes to update the UI.

        // Set up toolbar with back navigation.
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // Initialize the map fragment and set the callback for when the map is ready.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    // Observe ViewModel LiveData and handle different states (Success, Loading, Error).
    private fun observeViewModel() {
        storyMapsViewModel.stories.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    showLoading(false)
                    val stories = result.data
                    addManyMarker(stories) // Add markers for the fetched stories.
                }
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Error -> {
                    showLoading(false)
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Add markers for all stories on the map and adjust the camera view to fit all markers.
    private fun addManyMarker(stories: List<ListStoryItem>) {
        if (::mMap.isInitialized) {
            val boundsBuilder = LatLngBounds.builder()

            stories.forEach { story ->
                val lat = story.lat
                val lon = story.lon
                if (lat != null && lon != null) {
                    val latLng = LatLng(lat as Double, lon as Double)
                    mMap.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(story.name) // Set the marker title as the story name.
                            .snippet(story.description) // Add a description as the marker snippet.
                    )
                    boundsBuilder.include(latLng) // Include this marker in the bounds.
                }
            }

            // Adjust the camera to show all markers within the bounds.
            val bounds = boundsBuilder.build()
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    resources.displayMetrics.widthPixels,
                    resources.displayMetrics.heightPixels,
                    300 // Padding around the markers.
                )
            )
        }
    }

    // Callback when the Google Map is ready to use.
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Enable various map UI settings for better usability.
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        setMapStyle() // Apply custom map styling.
    }

    // Apply a custom style to the map using a raw resource file.
    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.") // Log error if style parsing fails.
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception) // Log exception for missing style resource.
        }
    }

    // Inflate the menu for map options (e.g., map types like satellite, terrain).
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_options, menu)
        return true
    }

    // Handle menu item selections for changing map types.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.normal_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.satellite_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            R.id.hybrid_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    // Show or hide the progress bar based on the loading state.
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        private const val TAG = "MapsActivity"
    }
}
