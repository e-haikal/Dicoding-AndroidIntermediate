package com.siaptekno.storyapp.ui.register

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
import com.siaptekno.storyapp.R
import com.siaptekno.storyapp.databinding.ActivityRegisterBinding
import com.siaptekno.storyapp.ui.login.LoginActivity
import com.siaptekno.storyapp.data.Result

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val registerViewModel: RegisterViewModel by viewModels {
        RegisterFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        playAnimation()
        binding.toLogin.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString().trim()
            val email = binding.edRegisterEmail.text.toString().trim()
            val password = binding.edRegisterPassword.text.toString().trim()

            if (isInputValid(name, email, password)) {
                registerViewModel.register(name, email, password)
            }
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        registerViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }
        registerViewModel.registerResult.observe(this) { result ->
            when(result) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    Toast.makeText(this, result.data.message, Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is Result.Error -> {
                    showLoading(false)
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun isInputValid(name: String, email: String, password: String): Boolean {
        var isValid = true

        if (name.isEmpty()) {
            binding.nameEditTextLayout.error = getString(R.string.empty_name)
            isValid = false
        } else {
            binding.nameEditTextLayout.error = null
        }

        if (email.isEmpty()) {
            binding.emailEditTextLayout.error = getString(R.string.empty_email)
            isValid = false
        } else {
            binding.emailEditTextLayout.error = null
        }

        if (password.isEmpty()) {
            binding.passwordEditTextLayout.error = getString(R.string.empty_password)
            isValid = false
        } else {
            binding.passwordEditTextLayout.error = null
        }

        return isValid
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE

    }

    private fun playAnimation() {
        val img = ObjectAnimator.ofFloat(binding.imgRegister, View.ALPHA, 1f).setDuration(1000)
        val desc = ObjectAnimator.ofFloat(binding.tvRegisterTitle, View.ALPHA, 1f).setDuration(1000)

        // Add msg_description animation
        val msgDescription = ObjectAnimator.ofFloat(binding.msgRegisterDescription, View.ALPHA, 1f).setDuration(1000)

        val txtName = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(1000)
        val inputName =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(1000)
        val editName =
            ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(1000)

        val txtEmail =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(1000)
        val inputEmail =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(1000)
        val editEmail =
            ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(1000)

        val txtPassword =
            ObjectAnimator.ofFloat(binding.txtPassword, View.ALPHA, 1f).setDuration(1000)
        val inputPassword =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(1000)
        val editPassword =
            ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f).setDuration(1000)

        val btn = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(1000)
        val toLogin = ObjectAnimator.ofFloat(binding.toLogin, View.ALPHA, 1f).setDuration(1000)

        val togetherName = AnimatorSet().apply {
            playTogether(txtName, inputName, editName)
        }

        val togetherEmail = AnimatorSet().apply {
            playTogether(txtEmail, inputEmail, editEmail)
        }

        val togetherPassword = AnimatorSet().apply {
            playTogether(txtPassword, inputPassword, editPassword)
        }

        AnimatorSet().apply {
            playSequentially(img, desc, msgDescription, togetherName, togetherEmail, togetherPassword, btn, toLogin)
            start()
        }
    }
}