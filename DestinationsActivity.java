package com.example.travelplane;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelplane.adapters.DestinationAdapter;
import com.example.travelplane.data.EnglishDestinationsData;
import com.example.travelplane.models.Destination;
import com.example.travelplane.utils.DestinationStorage;

import java.util.ArrayList;
import java.util.List;

/**
 * Destinations Activity
 * Displays travel destinations fetched from API with grid/list toggle and filtering
 */
public class DestinationsActivity extends AppCompatActivity implements DestinationAdapter.OnDestinationDeleteListener, DestinationAdapter.OnDestinationClickListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvError;
    private Spinner spinnerFilter;

    private DestinationAdapter adapter;
    private List<Destination> allDestinations;
    private boolean isGridView = false;
    private DestinationStorage destinationStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destinations);

        destinationStorage = new DestinationStorage(this); // initialize storage early

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Travel Destinations");
        }

        // Initialize views
        initViews();

        // Setup RecyclerView
        setupRecyclerView();

        // Load destinations from storage
        loadDestinations();
    }

    /**
     * Initialize views
     */
    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewDestinations);
        progressBar = findViewById(R.id.progressBar);
        tvError = findViewById(R.id.tvError);
        spinnerFilter = findViewById(R.id.spinnerFilter);

        // Setup filter spinner
        setupFilterSpinner();

        if (destinationStorage == null) {
            destinationStorage = new DestinationStorage(this);
        }
    }

    /**
     * Setup filter spinner with categories
     */
    private void setupFilterSpinner() {
        // Create filter options
        String[] filterOptions = {"All Destinations", "ID 1-25", "ID 26-50", "ID 51-75", "ID 76-100"};

        // Create adapter for spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            filterOptions
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(spinnerAdapter);

        // Set spinner listener
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterDestinations(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    /**
     * Filter destinations based on spinner selection
     */
    private void filterDestinations(int filterPosition) {
        if (allDestinations == null || allDestinations.isEmpty()) {
            return;
        }

        List<Destination> filtered = new ArrayList<>();

        switch (filterPosition) {
            case 0: // All
                filtered.addAll(allDestinations);
                break;
            case 1: // ID 1-25
                for (Destination d : allDestinations) {
                    if (d.getId() >= 1 && d.getId() <= 25) filtered.add(d);
                }
                break;
            case 2: // ID 26-50
                for (Destination d : allDestinations) {
                    if (d.getId() >= 26 && d.getId() <= 50) filtered.add(d);
                }
                break;
            case 3: // ID 51-75
                for (Destination d : allDestinations) {
                    if (d.getId() >= 51 && d.getId() <= 75) filtered.add(d);
                }
                break;
            case 4: // ID 76-100
                for (Destination d : allDestinations) {
                    if (d.getId() >= 76 && d.getId() <= 100) filtered.add(d);
                }
                break;
        }

        adapter.setDestinations(filtered);
        Toast.makeText(this, "Showing " + filtered.size() + " destinations", Toast.LENGTH_SHORT).show();
    }

    /**
     * Setup RecyclerView
     */
    private void setupRecyclerView() {
        adapter = new DestinationAdapter(this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    /**
     * Load destinations with English content
     */
    private void loadDestinations() {
        // Show loading
        progressBar.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        // Simulate loading delay (like API call)
        new android.os.Handler().postDelayed(() -> {
            if (destinationStorage == null) {
                destinationStorage = new DestinationStorage(this);
            }
            // Load English destinations
            allDestinations = destinationStorage.loadDestinations();

            progressBar.setVisibility(View.GONE);

            if (allDestinations.isEmpty()) {
                showError("No destinations found");
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                adapter.setDestinations(allDestinations);
                Toast.makeText(DestinationsActivity.this,
                        "Loaded " + allDestinations.size() + " English destinations",
                        Toast.LENGTH_SHORT).show();
            }
        }, 500); // 500ms delay to simulate network call
    }

    /**
     * Show error message
     * @param message Error message to display
     */
    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_destinations, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_toggle_view) {
            toggleViewMode();
            return true;
        } else if (item.getItemId() == R.id.action_refresh) {
            loadDestinations();
            return true;
        } else if (item.getItemId() == R.id.action_sort) {
            sortDestinations();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Toggle between grid and list view
     */
    private void toggleViewMode() {
        isGridView = !isGridView;

        if (isGridView) {
            // Switch to grid view (2 columns)
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            adapter.setGridView(true);
            Toast.makeText(this, "Grid View", Toast.LENGTH_SHORT).show();
        } else {
            // Switch to list view
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter.setGridView(false);
            Toast.makeText(this, "List View", Toast.LENGTH_SHORT).show();
        }

        adapter.notifyDataSetChanged();
    }

    /**
     * Sort destinations by ID
     */
    private void sortDestinations() {
        if (allDestinations == null || allDestinations.isEmpty()) {
            return;
        }

        // Sort in reverse order (pre-Java 8 compatible)
        List<Destination> sorted = new ArrayList<>(allDestinations);
        java.util.Collections.sort(sorted, new java.util.Comparator<Destination>() {
            @Override
            public int compare(Destination d1, Destination d2) {
                return Integer.compare(d2.getId(), d1.getId());
            }
        });

        adapter.setDestinations(sorted);
        destinationStorage.saveDestinations(sorted);
        Toast.makeText(this, "Sorted by ID (descending)", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteDestination(int position) {
        if (allDestinations == null || position < 0 || position >= allDestinations.size()) {
            return;
        }

        // Show confirmation dialog
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Destination")
                .setMessage("Are you sure you want to delete \"" + allDestinations.get(position).getTitle() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Remove from list
                    Destination removed = allDestinations.remove(position);
                    // Update adapter and persist
                    adapter.setDestinations(allDestinations);
                    destinationStorage.saveDestinations(allDestinations);

                    Toast.makeText(this,
                            "Deleted: " + removed.getTitle(),
                            Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestinationClick(Destination destination) {
        if (destination == null) return;
        Intent intent = new Intent(this, DestinationDetailActivity.class);
        intent.putExtra(DestinationDetailActivity.EXTRA_TITLE, destination.getTitle());
        intent.putExtra(DestinationDetailActivity.EXTRA_DESC, destination.getBody());
        startActivity(intent);
    }
}
