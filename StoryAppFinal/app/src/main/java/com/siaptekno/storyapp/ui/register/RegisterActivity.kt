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

    // Use the ViewModelFactory to provide an instance of RegisterViewModel.
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

        // Trigger the registration screen animation.
        playAnimation()

        // Set click listener for the "To Login" button to navigate to the LoginActivity.
        binding.toLogin.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Set click listener for the "Register" button to initiate registration logic.
        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString().trim()
            val email = binding.edRegisterEmail.text.toString().trim()
            val password = binding.edRegisterPassword.text.toString().trim()

            // Validate input before proceeding with registration.
            if (isInputValid(name, email, password)) {
                registerViewModel.register(name, email, password)
            }
        }

        // Observe changes from the ViewModel.
        observeViewModel()
    }

    private fun observeViewModel() {
        // Observe loading state to display/hide the loading indicator.
        registerViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        // Observe registration result and handle success/error scenarios.
        registerViewModel.registerResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    Toast.makeText(this, result.data.message, Toast.LENGTH_SHORT).show()
                    // Navigate to LoginActivity on successful registration.
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is Result.Error -> {
                    showLoading(false)
                    // Display error message on failure.
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun isInputValid(name: String, email: String, password: String): Boolean {
        var isValid = true

        // Check if the name field is empty.
        if (name.isEmpty()) {
            binding.nameEditTextLayout.error = getString(R.string.empty_name)
            isValid = false
        } else {
            binding.nameEditTextLayout.error = null
        }

        // Check if the email field is empty.
        if (email.isEmpty()) {
            binding.emailEditTextLayout.error = getString(R.string.empty_email)
            isValid = false
        } else {
            binding.emailEditTextLayout.error = null
        }

        // Check if the password field is empty.
        if (password.isEmpty()) {
            binding.passwordEditTextLayout.error = getString(R.string.empty_password)
            isValid = false
        } else {
            binding.passwordEditTextLayout.error = null
        }

        return isValid
    }

    private fun showLoading(isLoading: Boolean) {
        // Show or hide the progress bar based on the loading state.
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun playAnimation() {
        // Define animations for various UI elements.
        val img = ObjectAnimator.ofFloat(binding.imgRegister, View.ALPHA, 1f).setDuration(1000)
        val desc = ObjectAnimator.ofFloat(binding.tvRegisterTitle, View.ALPHA, 1f).setDuration(1000)
        val msgDescription = ObjectAnimator.ofFloat(binding.msgRegisterDescription, View.ALPHA, 1f).setDuration(1000)

        val txtName = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(1000)
        val inputName = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(1000)
        val editName = ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(1000)

        val txtEmail = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(1000)
        val inputEmail = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(1000)
        val editEmail = ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(1000)

        val txtPassword = ObjectAnimator.ofFloat(binding.txtPassword, View.ALPHA, 1f).setDuration(1000)
        val inputPassword = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(1000)
        val editPassword = ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f).setDuration(1000)

        val btn = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(1000)
        val toLogin = ObjectAnimator.ofFloat(binding.toLogin, View.ALPHA, 1f).setDuration(1000)

        // Group animations for better organization and smoother transitions.
        val togetherName = AnimatorSet().apply { playTogether(txtName, inputName, editName) }
        val togetherEmail = AnimatorSet().apply { playTogether(txtEmail, inputEmail, editEmail) }
        val togetherPassword = AnimatorSet().apply { playTogether(txtPassword, inputPassword, editPassword) }

        // Play all animations sequentially.
        AnimatorSet().apply {
            playSequentially(img, desc, msgDescription, togetherName, togetherEmail, togetherPassword, btn, toLogin)
            start()
        }
    }
}
