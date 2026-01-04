package com.example.travelplane;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.travelplane.utils.UserManager;
import com.example.travelplane.utils.ValidationUtils;

/**
 * Forgot Password Activity
 * Handles password reset email verification
 */
public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmail;
    private Button btnVerifyEmail;

    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize manager
        userManager = new UserManager(this);

        // Initialize views
        initViews();
        setupListeners();
    }

    /**
     * Initialize views
     */
    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        btnVerifyEmail = findViewById(R.id.btnVerifyEmail);
    }

    /**
     * Setup click listeners
     */
    private void setupListeners() {
        btnVerifyEmail.setOnClickListener(v -> handleVerifyEmail());
    }

    /**
     * Handle verify email button click
     */
    private void handleVerifyEmail() {
        String email = etEmail.getText().toString().trim();

        // Validate email
        if (ValidationUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            etEmail.setError("Invalid email format");
            etEmail.requestFocus();
            return;
        }

        // Check if user exists
        if (userManager.userExists(email)) {
            Toast.makeText(this, "Email verified! Please reset your password.", Toast.LENGTH_SHORT).show();

            // Navigate to Reset Password Activity
            Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "No account found with this email", Toast.LENGTH_SHORT).show();
        }
    }
}

