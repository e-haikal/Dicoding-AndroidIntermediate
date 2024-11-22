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

class CustomPasswordEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : TextInputEditText(context, attrs, defStyleAttr) {

    private var textInputLayout: TextInputLayout? = null // Reference to parent TextInputLayout
    private var isInitialized = false // Initialization status flag

    init {
        setupView() // Configure view properties

        // Add a TextWatcher to monitor and validate password input
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isInitialized) {
                    validatePassword(s) // Validate password whenever it changes
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupView() {
        // Configure the EditText for password input
        isFocusable = true
        isFocusableInTouchMode = true
        isClickable = true
        isEnabled = true
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        gravity = Gravity.CENTER_VERTICAL // Center text vertically
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f) // Set text size
    }

    private fun validatePassword(password: CharSequence?) {
        // Lazily initialize the parent TextInputLayout reference
        if (textInputLayout == null) {
            textInputLayout = findParentTextInputLayout()
        }

        // Check password length and set error if it doesn't meet the requirement
        if (password != null && password.length < 8) {
            textInputLayout?.error = context.getString(R.string.password_validation)
        } else {
            textInputLayout?.error = null // Clear error when password is valid
        }
    }

    private fun findParentTextInputLayout(): TextInputLayout? {
        // Traverse the view hierarchy to find the parent TextInputLayout
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
