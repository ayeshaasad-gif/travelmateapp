package com.example.travelplane.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelplane.R;
import com.example.travelplane.models.TripNote;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying Trip Notes in RecyclerView
 */
public class TripNotesAdapter extends RecyclerView.Adapter<TripNotesAdapter.TripNoteViewHolder> {

    private List<TripNote> notes;
    private OnNoteDeleteListener deleteListener;

    /**
     * Interface for handling note deletion and editing
     */
    public interface OnNoteDeleteListener {
        void onDeleteNote(int noteId, int position);
        void onEditNote(TripNote note, int position);
    }

    public TripNotesAdapter(OnNoteDeleteListener deleteListener) {
        this.notes = new ArrayList<>();
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public TripNoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trip_note, parent, false);
        return new TripNoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripNoteViewHolder holder, int position) {
        TripNote note = notes.get(position);
        holder.bind(note, position);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    /**
     * Update the notes list
     * @param notes New list of trip notes
     */
    public void setNotes(List<TripNote> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class for TripNote items
     */
    class TripNoteViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvDescription;
        private TextView tvDate;
        private ImageButton btnDelete;
        private ImageButton btnEdit;
        private android.widget.ImageView ivNoteImage;

        public TripNoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvNoteTitle);
            tvDescription = itemView.findViewById(R.id.tvNoteDescription);
            tvDate = itemView.findViewById(R.id.tvNoteDate);
            btnDelete = itemView.findViewById(R.id.btnDeleteNote);
            btnEdit = itemView.findViewById(R.id.btnEditNote);
            ivNoteImage = itemView.findViewById(R.id.ivNoteImage);
        }

        /**
         * Bind note data to views
         * @param note TripNote object to display
         * @param position Position in the list
         */
        public void bind(TripNote note, int position) {
            tvTitle.setText(note.getTitle());
            tvDescription.setText(note.getDescription());

            // Format timestamp to readable date
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
            String dateString = sdf.format(new Date(note.getTimestamp()));
            tvDate.setText(dateString);

            // Handle image display
            if (note.getImageUri() != null && !note.getImageUri().isEmpty()) {
                ivNoteImage.setVisibility(android.view.View.VISIBLE);
                try {
                    ivNoteImage.setImageURI(android.net.Uri.parse(note.getImageUri()));
                } catch (Exception e) {
                    ivNoteImage.setVisibility(android.view.View.GONE);
                }
            } else {
                ivNoteImage.setVisibility(android.view.View.GONE);
            }

            // Set delete button click listener
            btnDelete.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDeleteNote(note.getId(), position);
                }
            });

            // Set edit button click listener
            btnEdit.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onEditNote(note, position);
                }
            });
        }
    }
}

