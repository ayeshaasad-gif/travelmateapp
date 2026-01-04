package com.example.travelplane;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.travelplane.utils.UserManager;
import com.example.travelplane.utils.ValidationUtils;

/**
 * Reset Password Activity
 * Handles password reset functionality
 */
public class ResetPasswordActivity extends AppCompatActivity {

    private EditText etNewPassword, etConfirmPassword;
    private Button btnResetPassword;

    private UserManager userManager;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Initialize manager
        userManager = new UserManager(this);

        // Get email from intent
        userEmail = getIntent().getStringExtra("email");

        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "Invalid request", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        initViews();
        setupListeners();
    }

    /**
     * Initialize views
     */
    private void initViews() {
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
    }

    /**
     * Setup click listeners
     */
    private void setupListeners() {
        btnResetPassword.setOnClickListener(v -> handleResetPassword());
    }

    /**
     * Handle reset password button click
     */
    private void handleResetPassword() {
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validate inputs
        if (!validateInputs(newPassword, confirmPassword)) {
            return;
        }

        // Update password
        boolean success = userManager.updatePassword(userEmail, newPassword);

        if (success) {
            Toast.makeText(this, "Password reset successful! Please login with new password.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to reset password. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Validate password inputs
     * @param newPassword New password input
     * @param confirmPassword Confirm password input
     * @return true if valid, false otherwise
     */
    private boolean validateInputs(String newPassword, String confirmPassword) {
        if (ValidationUtils.isEmpty(newPassword)) {
            etNewPassword.setError("Password is required");
            etNewPassword.requestFocus();
            return false;
        }

        if (!ValidationUtils.isValidPassword(newPassword)) {
            etNewPassword.setError("Password must be at least 6 characters");
            etNewPassword.requestFocus();
            return false;
        }

        if (ValidationUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Please confirm password");
            etConfirmPassword.requestFocus();
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return false;
        }

        return true;
    }
}

