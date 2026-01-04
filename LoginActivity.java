package com.example.travelplane;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.travelplane.models.User;
import com.example.travelplane.utils.SessionManager;
import com.example.travelplane.utils.ThemeManager;
import com.example.travelplane.utils.UserManager;
import com.example.travelplane.utils.ValidationUtils;

/**
 * Login Activity
 * Handles user authentication.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister, tvForgotPassword;

    private UserManager userManager;
    private SessionManager sessionManager;
    private ThemeManager themeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize theme manager and apply saved theme
        try {
            themeManager = new ThemeManager(this);
            themeManager.applySavedTheme();
        } catch (Exception e) {
            // Log but don't crash - theme can fail
            Toast.makeText(this, "Theme loading failed, using default", Toast.LENGTH_SHORT).show();
        }

        setContentView(R.layout.activity_login);

        // Initialize managers with error handling
        try {
            userManager = new UserManager(this);
            sessionManager = new SessionManager(this);
        } catch (Exception e) {
            Toast.makeText(this, "ERROR: Failed to initialize app. " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // If user is already logged in, go directly to Home
        try {
            if (sessionManager.isLoggedIn()) {
                navigateToHome();
                return;
            }
        } catch (Exception e) {
            Toast.makeText(this, "Session check failed", Toast.LENGTH_SHORT).show();
        }

        // Initialize views and listeners
        initViews();
        setupListeners();
    }

    /**
     * Initialize views from layout.
     * Includes null checks to catch layout ID mismatches.
     */
    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        // Defensive null checks - if any view is null, warn and return
        if (etEmail == null || etPassword == null || btnLogin == null ||
            tvRegister == null || tvForgotPassword == null) {
            Toast.makeText(this, "ERROR: Layout views not found. Check XML IDs.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Setup click listeners for buttons and text views.
     */
    private void setupListeners() {
        btnLogin.setOnClickListener(v -> handleLogin());

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Handle login button click.
     */
    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate inputs
        if (!validateInputs(email, password)) {
            return;
        }

        // Authenticate user using UserManager
        User user = userManager.authenticateUser(email, password);

        if (user != null) {
            // Login successful
            sessionManager.createLoginSession(user.getEmail(), user.getName());
            Toast.makeText(this, "Login successful! Welcome " + user.getName(), Toast.LENGTH_SHORT).show();
            navigateToHome();
        } else {
            // Login failed
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Validate login inputs.
     *
     * @param email    Email input
     * @param password Password input
     * @return true if valid, false otherwise
     */
    private boolean validateInputs(String email, String password) {
        if (ValidationUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return false;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            etEmail.setError("Invalid email format");
            etEmail.requestFocus();
            return false;
        }

        if (ValidationUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Navigate to HomeActivity and finish LoginActivity so user can't go back.
     */
    private void navigateToHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
