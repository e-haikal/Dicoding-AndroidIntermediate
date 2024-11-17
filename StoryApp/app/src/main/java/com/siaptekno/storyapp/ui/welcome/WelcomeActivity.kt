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


        playAnimation()
        binding.btnStart.setOnClickListener {
            val intent = Intent(this@WelcomeActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun playAnimation() {


        val title = ObjectAnimator.ofFloat(binding.tvWelcomeTitle, View.ALPHA, 1f).setDuration(1000)
        val desc = ObjectAnimator.ofFloat(binding.tvWelcomeDescription, View.ALPHA, 1f).setDuration(1000)
        val btn = ObjectAnimator.ofFloat(binding.btnStart, View.ALPHA, 1f).setDuration(1000)
        val img = ObjectAnimator.ofFloat(binding.imgWelcome, View.ALPHA, 1f).setDuration(1000)


        AnimatorSet().apply {
            playSequentially(img, title, desc, btn)
            start()
        }
    }


}