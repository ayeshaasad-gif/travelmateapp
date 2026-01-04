package com.example.travelplane;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelplane.adapters.TripNotesAdapter;
import com.example.travelplane.models.Destination;
import com.example.travelplane.models.TripNote;
import com.example.travelplane.utils.DestinationStorage;
import com.example.travelplane.utils.TripNotesManager;
import com.example.travelplane.utils.ValidationUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Trip Notes Activity
 * Displays and manages trip notes
 */
public class TripNotesActivity extends AppCompatActivity implements TripNotesAdapter.OnNoteDeleteListener {

    private RecyclerView recyclerView;
    private TextView tvEmptyState;
    private FloatingActionButton fabAddNote;

    private TripNotesAdapter adapter;
    private TripNotesManager notesManager;
    private DestinationStorage destinationStorage;
    private boolean isGridView = false;

    private Uri selectedImageUri;
    private ActivityResultLauncher<String> imagePickerLauncher;
    private TripNote editingNote; // For edit mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_notes);

        // Initialize image picker
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        // Grant persistent permission
                        getContentResolver().takePersistableUriPermission(uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                });

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Trip Notes");
        }

        // Initialize manager
        notesManager = new TripNotesManager(this);
        destinationStorage = new DestinationStorage(this);

        // Initialize views
        initViews();

        // Setup RecyclerView
        setupRecyclerView();

        // Load notes
        loadNotes();

        // Setup FAB click listener
        fabAddNote.setOnClickListener(v -> showAddNoteDialog());
    }

    /**
     * Initialize views
     */
    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewNotes);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        fabAddNote = findViewById(R.id.fabAddNote);
    }

    /**
     * Setup RecyclerView
     */
    private void setupRecyclerView() {
        adapter = new TripNotesAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    /**
     * Load notes from storage
     */
    private void loadNotes() {
        List<TripNote> notes = notesManager.getAllNotes();

        if (notes.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.setNotes(notes);
        }
    }

    /**
     * Show dialog to add new note
     */
    private void showAddNoteDialog() {
        showNoteDialog(null);
    }

    /**
     * Show dialog to add or edit note
     * @param note Existing note to edit, or null for new note
     */
    private void showNoteDialog(TripNote note) {
        boolean isEditMode = (note != null);
        editingNote = note;
        selectedImageUri = null;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_note, null);

        EditText etTitle = dialogView.findViewById(R.id.etNoteTitle);
        EditText etDescription = dialogView.findViewById(R.id.etNoteDescription);
        MaterialButton btnSelectImage = dialogView.findViewById(R.id.btnSelectImage);
        MaterialButton btnRemoveImage = dialogView.findViewById(R.id.btnRemoveImage);
        ImageView ivPreview = dialogView.findViewById(R.id.ivPreview);

        // Pre-fill if editing
        if (isEditMode) {
            etTitle.setText(note.getTitle());
            etDescription.setText(note.getDescription());
            if (note.getImageUri() != null && !note.getImageUri().isEmpty()) {
                selectedImageUri = Uri.parse(note.getImageUri());
                ivPreview.setImageURI(selectedImageUri);
                ivPreview.setVisibility(View.VISIBLE);
                btnRemoveImage.setVisibility(View.VISIBLE);
            }
        }

        // Image selection button
        btnSelectImage.setOnClickListener(v -> {
            imagePickerLauncher.launch("image/*");
            // Update preview after selection
            new android.os.Handler().postDelayed(() -> {
                if (selectedImageUri != null) {
                    ivPreview.setImageURI(selectedImageUri);
                    ivPreview.setVisibility(View.VISIBLE);
                    btnRemoveImage.setVisibility(View.VISIBLE);
                }
            }, 500);
        });

        // Remove image button
        btnRemoveImage.setOnClickListener(v -> {
            selectedImageUri = null;
            ivPreview.setVisibility(View.GONE);
            btnRemoveImage.setVisibility(View.GONE);
        });

        builder.setView(dialogView)
                .setTitle(isEditMode ? "Edit Trip Note" : "Add Trip Note")
                .setPositiveButton(isEditMode ? "Update" : "Add", null)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        // Override positive button to prevent auto-dismiss
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            // Validate inputs
            if (ValidationUtils.isEmpty(title)) {
                etTitle.setError("Title is required");
                return;
            }

            if (ValidationUtils.isEmpty(description)) {
                etDescription.setError("Description is required");
                return;
            }

            if (isEditMode) {
                // Update existing note
                editingNote.setTitle(title);
                editingNote.setDescription(description);
                editingNote.setImageUri(selectedImageUri != null ? selectedImageUri.toString() : editingNote.getImageUri());

                // Update in storage
                notesManager.updateNote(editingNote);
                Toast.makeText(TripNotesActivity.this, "Note updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                // Create and save new note
                TripNote newNote = new TripNote();
                newNote.setTitle(title);
                newNote.setDescription(description);
                newNote.setImageUri(selectedImageUri != null ? selectedImageUri.toString() : null);

                notesManager.addNote(newNote);
                Toast.makeText(TripNotesActivity.this, "Note added successfully", Toast.LENGTH_SHORT).show();

                // Also add to destinations list
                Destination destination = new Destination();
                destination.setId(destinationStorage.nextId());
                destination.setUserId(999); // mark as user-created
                destination.setTitle(newNote.getTitle());
                destination.setBody(newNote.getDescription());
                destinationStorage.addDestination(destination);
            }

            // Reload notes
            loadNotes();
            dialog.dismiss();
        });
    }

    @Override
    public void onEditNote(TripNote note, int position) {
        showNoteDialog(note);
    }

    @Override
    public void onDeleteNote(int noteId, int position) {
        // Show confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Delete Note")
                .setMessage("Are you sure you want to delete this note?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    notesManager.deleteNote(noteId);
                    Toast.makeText(TripNotesActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();
                    loadNotes();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_trip_notes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_toggle_view) {
            toggleViewMode();
            return true;
        } else if (item.getItemId() == R.id.action_delete_all) {
            deleteAllNotes();
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
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            Toast.makeText(this, "Grid View", Toast.LENGTH_SHORT).show();
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            Toast.makeText(this, "List View", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Delete all notes with confirmation
     */
    private void deleteAllNotes() {
        new AlertDialog.Builder(this)
                .setTitle("Delete All Notes")
                .setMessage("Are you sure you want to delete all notes? This cannot be undone.")
                .setPositiveButton("Delete All", (dialog, which) -> {
                    // Clear all notes
                    getSharedPreferences("TravelPlannerNotes", MODE_PRIVATE)
                            .edit()
                            .clear()
                            .apply();
                    Toast.makeText(this, "All notes deleted", Toast.LENGTH_SHORT).show();
                    loadNotes();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
