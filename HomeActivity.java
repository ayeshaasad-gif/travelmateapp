package com.example.travelplane;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.travelplane.utils.SessionManager;

/**
 * Home Activity
 * Main landing screen after login
 */
public class HomeActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private CardView cardDestinations, cardTripNotes, cardWebView, cardSettings;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize session manager
        sessionManager = new SessionManager(this);

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            navigateToLogin();
            return;
        }

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize views
        initViews();
        setupListeners();

        // Display welcome message
        String userName = sessionManager.getUserName();
        tvWelcome.setText("Welcome, " + userName + "!");
    }

    /**
     * Initialize views
     */
    private void initViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        cardDestinations = findViewById(R.id.cardDestinations);
        cardTripNotes = findViewById(R.id.cardTripNotes);
        cardWebView = findViewById(R.id.cardWebView);
        cardSettings = findViewById(R.id.cardSettings);

        // Defensive null checks
        if (tvWelcome == null || cardDestinations == null || cardTripNotes == null ||
            cardWebView == null || cardSettings == null) {
            Toast.makeText(this, "ERROR: Home layout views not found.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Setup click listeners for cards
     */
    private void setupListeners() {
        cardDestinations.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, DestinationsActivity.class);
            startActivity(intent);
        });

        cardTripNotes.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, TripNotesActivity.class);
            startActivity(intent);
        });

        cardWebView.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, WebViewActivity.class);
            startActivity(intent);
        });

        cardSettings.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            handleLogout();
            return true;
        } else if (item.getItemId() == R.id.action_profile) {
            showProfile();
            return true;
        } else if (item.getItemId() == R.id.action_about) {
            showAbout();
            return true;
        } else if (item.getItemId() == R.id.action_sync) {
            syncData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Show user profile
     */
    private void showProfile() {
        Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    /**
     * Show about dialog
     */
    private void showAbout() {
        Toast.makeText(this,
            "Travel Planner v1.0\nA complete travel management app",
            Toast.LENGTH_LONG).show();
    }

    /**
     * Sync data with backend
     */
    private void syncData() {
        Toast.makeText(this, "Syncing data with backend...", Toast.LENGTH_SHORT).show();
        // Here you would call BackendService to sync data
    }

    /**
     * Handle logout
     */
    private void handleLogout() {
        sessionManager.logout();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        navigateToLogin();
    }

    /**
     * Navigate to Login Activity
     */
    private void navigateToLogin() {
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Disable back button to prevent going back to login after login
        // User must logout explicitly
        super.onBackPressed();
    }
}

