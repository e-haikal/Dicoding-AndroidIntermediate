package com.siaptekno.storyapp.ui.custom_view

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewParent
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.siaptekno.storyapp.R

class CustomEmailEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : TextInputEditText(context, attrs, defStyleAttr) {

    private var textInputLayout: TextInputLayout? = null // To store reference to the parent TextInputLayout
    private var isInitialized = false // Flag to indicate whether the view is fully initialized

    init {
        setupView() // Configure the view properties

        // Add a text watcher to validate the email input dynamically
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isInitialized) {
                    validateEmail(s) // Validate the email whenever the input changes
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupView() {
        // Set input properties to ensure appropriate behavior for email entry
        isFocusable = true
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        gravity = Gravity.CENTER_VERTICAL // Align text vertically centered
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f) // Set text size in SP units
    }

    private fun validateEmail(email: CharSequence?) {
        // Lazily find the parent TextInputLayout to display error messages
        if (textInputLayout == null) {
            textInputLayout = findParentTextInputLayout()
        }

        // Define a regex pattern for email validation
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        if (email != null && !email.matches(emailPattern.toRegex())) {
            // Set an error message if the email is invalid
            textInputLayout?.error = context.getString(R.string.email_validation)
        } else {
            // Clear the error message if the email is valid
            textInputLayout?.error = null
        }
    }

    private fun findParentTextInputLayout(): TextInputLayout? {
        // Traverse up the view hierarchy to locate the parent TextInputLayout
        var parentView: ViewParent? = parent
        while (parentView != null) {
            if (parentView is TextInputLayout) {
                return parentView
            }
            parentView = (parentView as? ViewParent)?.parent
        }
        return null // Return null if no TextInputLayout is found
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isInitialized = true // Mark the view as fully initialized
    }
}
