package com.example.travelplane;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.travelplane.backend.BackendService;
import com.example.travelplane.models.User;
import com.example.travelplane.utils.SessionManager;

/**
 * Profile Activity - Demonstrates backend service usage with spinners and menus
 * Shows user profile with editable fields and backend sync
 */
public class ProfileActivity extends AppCompatActivity {

    private TextView tvEmail;
    private EditText etName;
    private EditText etPassword;
    private Spinner spinnerLanguage;
    private Button btnSaveChanges;
    private ProgressBar progressBar;

    private SessionManager sessionManager;
    private BackendService backendService;
    private String selectedLanguage = "English";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Profile");
        }

        // Initialize services
        sessionManager = new SessionManager(this);
        backendService = BackendService.getInstance(this);

        // Initialize views
        initViews();

        // Setup spinner
        setupLanguageSpinner();

        // Load user data
        loadUserProfile();

        // Setup listeners
        btnSaveChanges.setOnClickListener(v -> saveChanges());
    }

    /**
     * Initialize views
     */
    private void initViews() {
        tvEmail = findViewById(R.id.tvEmail);
        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        spinnerLanguage = findViewById(R.id.spinnerLanguage);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        progressBar = findViewById(R.id.progressBar);
    }

    /**
     * Setup language spinner
     */
    private void setupLanguageSpinner() {
        String[] languages = {"English", "Spanish", "French", "German", "Italian", "Japanese", "Chinese"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                languages
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(adapter);

        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLanguage = languages[position];
                Toast.makeText(ProfileActivity.this,
                        "Language: " + selectedLanguage,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    /**
     * Load user profile from backend
     */
    private void loadUserProfile() {
        String email = sessionManager.getUserEmail();
        String name = sessionManager.getUserName();

        tvEmail.setText(email);
        etName.setText(name);

        // Load from backend (async)
        progressBar.setVisibility(View.VISIBLE);
        backendService.getUserByEmail(email, new BackendService.BackendCallback<User>() {
            @Override
            public void onSuccess(User user) {
                progressBar.setVisibility(View.GONE);
                etName.setText(user.getName());
                Toast.makeText(ProfileActivity.this,
                        "Profile loaded from backend",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ProfileActivity.this,
                        "Using cached profile data",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Save changes to backend
     */
    private void saveChanges() {
        String newName = etName.getText().toString().trim();
        String newPassword = etPassword.getText().toString().trim();
        String email = sessionManager.getUserEmail();

        if (newName.isEmpty()) {
            etName.setError("Name cannot be empty");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSaveChanges.setEnabled(false);

        // Update password if provided
        if (!newPassword.isEmpty()) {
            backendService.updateUserPassword(email, newPassword, new BackendService.BackendCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean success) {
                    progressBar.setVisibility(View.GONE);
                    btnSaveChanges.setEnabled(true);

                    // Update session
                    sessionManager.createLoginSession(email, newName);

                    Toast.makeText(ProfileActivity.this,
                            "Profile updated successfully!\nLanguage: " + selectedLanguage,
                            Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(String error) {
                    progressBar.setVisibility(View.GONE);
                    btnSaveChanges.setEnabled(true);
                    Toast.makeText(ProfileActivity.this,
                            "Error: " + error,
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Just update session
            sessionManager.createLoginSession(email, newName);
            progressBar.setVisibility(View.GONE);
            btnSaveChanges.setEnabled(true);
            Toast.makeText(this,
                    "Profile updated!\nLanguage: " + selectedLanguage,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_reset) {
            resetFields();
            return true;
        } else if (item.getItemId() == R.id.action_delete_account) {
            deleteAccount();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Reset all fields
     */
    private void resetFields() {
        loadUserProfile();
        etPassword.setText("");
        Toast.makeText(this, "Fields reset", Toast.LENGTH_SHORT).show();
    }

    /**
     * Delete account (placeholder)
     */
    private void deleteAccount() {
        Toast.makeText(this,
                "Account deletion not available in demo",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

