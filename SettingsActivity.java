package com.example.travelplane;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.travelplane.utils.ThemeManager;

/**
 * Settings Activity
 * Handles app settings including theme selection
 */
public class SettingsActivity extends AppCompatActivity {

    private RadioGroup radioGroupTheme;
    private RadioButton radioLight, radioDark;

    private ThemeManager themeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Settings");
        }

        // Initialize theme manager
        themeManager = new ThemeManager(this);

        // Initialize views
        initViews();

        // Load current theme
        loadCurrentTheme();

        // Setup listeners
        setupListeners();
    }

    /**
     * Initialize views
     */
    private void initViews() {
        radioGroupTheme = findViewById(R.id.radioGroupTheme);
        radioLight = findViewById(R.id.radioLight);
        radioDark = findViewById(R.id.radioDark);
    }

    /**
     * Load and display current theme
     */
    private void loadCurrentTheme() {
        int currentTheme = themeManager.getTheme();

        if (currentTheme == ThemeManager.THEME_DARK) {
            radioDark.setChecked(true);
        } else {
            radioLight.setChecked(true);
        }
    }

    /**
     * Setup theme selection listener
     */
    private void setupListeners() {
        radioGroupTheme.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioLight) {
                themeManager.setTheme(ThemeManager.THEME_LIGHT);
                Toast.makeText(this, "Light theme applied", Toast.LENGTH_SHORT).show();
            } else if (checkedId == R.id.radioDark) {
                themeManager.setTheme(ThemeManager.THEME_DARK);
                Toast.makeText(this, "Dark theme applied", Toast.LENGTH_SHORT).show();
            }

            // Recreate activity to apply theme
            recreate();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

