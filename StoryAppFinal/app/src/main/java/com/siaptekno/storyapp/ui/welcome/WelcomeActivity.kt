package com.siaptekno.storyapp.ui.welcome

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.siaptekno.storyapp.databinding.ActivityWelcomeBinding
import com.siaptekno.storyapp.ui.login.LoginActivity
import com.siaptekno.storyapp.R as R1

/**
 * Welcome screen activity with animations and a start button.
 */
class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R1.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        playAnimation() // Start animations

        // Navigate to LoginActivity on button click
        binding.btnStart.setOnClickListener {
            val intent = Intent(this@WelcomeActivity, LoginActivity::class.java)
            startActivity(intent)
            finish() // Finish WelcomeActivity
        }
    }

    /**
     * Animates the welcome screen elements sequentially.
     */
    private fun playAnimation() {
        val img = ObjectAnimator.ofFloat(binding.imgWelcome, View.ALPHA, 1f).setDuration(1000)
        val title = ObjectAnimator.ofFloat(binding.tvWelcomeTitle, View.ALPHA, 1f).setDuration(1000)
        val desc = ObjectAnimator.ofFloat(binding.tvWelcomeDescription, View.ALPHA, 1f).setDuration(1000)
        val btn = ObjectAnimator.ofFloat(binding.btnStart, View.ALPHA, 1f).setDuration(1000)

        // Play animations sequentially
        AnimatorSet().apply {
            playSequentially(img, title, desc, btn)
            start()
        }
    }
}
