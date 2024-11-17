package com.siaptekno.storyapp.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.siaptekno.storyapp.MainActivity
import com.siaptekno.storyapp.databinding.ActivityLoginBinding
import com.siaptekno.storyapp.pref.SessionManager
import kotlinx.coroutines.launch
import com.siaptekno.storyapp.R
import com.siaptekno.storyapp.data.Result
import com.siaptekno.storyapp.ui.register.RegisterActivity


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager

    private val loginViewModel: LoginViewModel by viewModels {
        LoginFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        playAnimation()
        binding.toRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString().trim()
            val password = binding.edLoginPassword.text.toString().trim()

            if (isInputValid(email, password)) {
                loginViewModel.login(email, password)
            }
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        loginViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        loginViewModel.loginResult.observe(this) { result ->
            when(result) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    val token = result.data.loginResult.token
                    lifecycleScope.launch {
                        sessionManager.saveAuthToken(token)
                    }
                    Toast.makeText(this, result.data.message, Toast.LENGTH_SHORT).show()

                    navigateToMainActivity()
                }
                is Result.Error -> {
                    showLoading(false)
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE

    }

    private fun isInputValid(email: String, password: String): Boolean {
        var isValid = true
        if (email.isEmpty()) {
            binding.emailEditTextLayout.error = getString(R.string.empty_email)
            isValid = false
        } else {
            binding.emailEditTextLayout.error = null
        }

        if (password.isEmpty()) {
            binding.PasswordEditTextLayout.error = getString(R.string.empty_password)
            isValid = false
        } else {
            binding.PasswordEditTextLayout.error = null
        }

        return isValid
    }

    private fun playAnimation() {
        val img = ObjectAnimator.ofFloat(binding.imgLogin, View.ALPHA, 1f).setDuration(1000)
        val desc = ObjectAnimator.ofFloat(binding.msgLogin, View.ALPHA, 1f).setDuration(1000)

        val txtEmail = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(1000)
        val inputEmail = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(1000)
        val editEmail = ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(1000)

        val txtPassword = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(1000)
        val inputPassword = ObjectAnimator.ofFloat(binding.PasswordEditTextLayout, View.ALPHA, 1f).setDuration(1000)
        val editPassword = ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(1000)

        val btn = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(1000)
        val toReg = ObjectAnimator.ofFloat(binding.toRegister, View.ALPHA, 1f).setDuration(1000)

        val togetherEmail = AnimatorSet().apply {
            playTogether(txtEmail, inputEmail, editEmail)
        }

        val togetherPassword = AnimatorSet().apply {
            playTogether(txtPassword, inputPassword, editPassword)
        }

        AnimatorSet().apply {
            playSequentially(img, desc, togetherEmail, togetherPassword, btn, toReg)
            start()
        }
    }



}