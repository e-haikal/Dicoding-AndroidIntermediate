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

/**
 * A custom EditText for validating email input with real-time feedback.
 * It extends the TextInputEditText to integrate with the TextInputLayout for styling and validation.
 */
class CustomEmailEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : TextInputEditText(context, attrs, defStyleAttr) {

    // Reference to the TextInputLayout parent view for showing validation errors.
    private var textInputLayout: TextInputLayout? = null

    // Flag to indicate whether the view is fully initialized and ready for validation.
    private var isInitialized = false

    init {
        // Setup the EditText view with default configurations.
        setupView()

        // Add a TextWatcher to monitor text changes and trigger validation.
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed before the text changes.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Perform email validation after text changes, if initialization is complete.
                if (isInitialized) {
                    validateEmail(s)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // No action needed after the text changes.
            }
        })
    }

    /**
     * Setup the default view properties for this EditText.
     */
    private fun setupView() {
        // Make the EditText focusable and clickable for user interaction.
        isFocusable = true
        isFocusableInTouchMode = true
        isClickable = true
        isEnabled = true

        // Set the input type to accept text in email format.
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        // Set gravity to center the text vertically.
        gravity = Gravity.CENTER_VERTICAL

        // Set text size for the input field.
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
    }

    /**
     * Validate the email input and show an error if the email is invalid.
     * The error is displayed using the parent TextInputLayout.
     */
    private fun validateEmail(email: CharSequence?) {
        // Initialize the TextInputLayout if it's not already set.
        if (textInputLayout == null) {
            textInputLayout = findParentTextInputLayout()
        }

        // Simple email pattern for basic validation.
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

        // Check if the email matches the pattern and show or clear the error message.
        if (email != null && !email.matches(emailPattern.toRegex())) {
            // Invalid email: show error in the TextInputLayout.
            textInputLayout?.error = context.getString(R.string.email_validation)
        } else {
            // Valid email: clear any error.
            textInputLayout?.error = null
        }
    }

    /**
     * Recursively search for the parent TextInputLayout view.
     * This is used to display error messages in the TextInputLayout.
     */
    private fun findParentTextInputLayout(): TextInputLayout? {
        var parentView: ViewParent? = parent
        while (parentView != null) {
            // Check if the parent view is a TextInputLayout.
            if (parentView is TextInputLayout) {
                return parentView
            }
            // Move up to the next parent view.
            parentView = (parentView as? ViewParent)?.parent
        }
        // Return null if no TextInputLayout is found.
        return null
    }

    /**
     * Called when the view is attached to the window.
     * It marks the view as initialized, so validation can proceed.
     */
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isInitialized = true
    }
}
