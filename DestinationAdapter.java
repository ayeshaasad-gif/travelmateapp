package com.example.travelplane.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelplane.R;
import com.example.travelplane.models.Destination;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying Travel Destinations in a RecyclerView.
 * Each item shows the destination title, description (body), and id.
 * Supports both list and grid view modes.
 */
public class DestinationAdapter extends RecyclerView.Adapter<DestinationAdapter.DestinationViewHolder> {

    // List of destinations to display
    private List<Destination> destinations;
    private boolean isGridView = false;
    private OnDestinationDeleteListener deleteListener;
    private OnDestinationClickListener clickListener;

    /**
     * Interface for handling destination deletion
     */
    public interface OnDestinationDeleteListener {
        void onDeleteDestination(int position);
    }

    /**
     * Interface for handling destination clicks
     */
    public interface OnDestinationClickListener {
        void onDestinationClick(Destination destination);
    }

    public DestinationAdapter() {
        // Initialize with an empty list to avoid null checks
        this.destinations = new ArrayList<>();
    }

    public DestinationAdapter(OnDestinationDeleteListener listener) {
        this.destinations = new ArrayList<>();
        this.deleteListener = listener;
    }

    public DestinationAdapter(OnDestinationDeleteListener listener, OnDestinationClickListener clickListener) {
        this.destinations = new ArrayList<>();
        this.deleteListener = listener;
        this.clickListener = clickListener;
    }

    public void setDeleteListener(OnDestinationDeleteListener listener) {
        this.deleteListener = listener;
    }

    public void setClickListener(OnDestinationClickListener clickListener) {
        this.clickListener = clickListener;
    }

    /**
     * Update the destinations list and refresh the RecyclerView.
     *
     * @param destinations New list of destinations
     */
    public void setDestinations(List<Destination> destinations) {
        if (destinations == null) {
            this.destinations = new ArrayList<>();
        } else {
            this.destinations = destinations;
        }
        notifyDataSetChanged();
    }

    /**
     * Set grid view mode
     * @param isGridView true for grid view, false for list view
     */
    public void setGridView(boolean isGridView) {
        this.isGridView = isGridView;
    }

    @NonNull
    @Override
    public DestinationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout for each destination row
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_destination, parent, false);
        return new DestinationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DestinationViewHolder holder, int position) {
        // Get the destination for this position and bind it to the ViewHolder
        Destination destination = destinations.get(position);
        holder.bind(destination, position);
    }

    @Override
    public int getItemCount() {
        // Return how many items we have in the list
        return destinations != null ? destinations.size() : 0;
    }

    /**
     * ViewHolder class for Destination items.
     * Holds references to the views for a single row in the RecyclerView.
     */
    class DestinationViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitle;
        private final TextView tvDescription;
        private final TextView tvId;
        private final android.widget.ImageButton btnDelete;

        public DestinationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvDestinationTitle);
            tvDescription = itemView.findViewById(R.id.tvDestinationDescription);
            tvId = itemView.findViewById(R.id.tvDestinationId);
            btnDelete = itemView.findViewById(R.id.btnDeleteDestination);

            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    int pos = getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION && destinations != null && pos < destinations.size()) {
                        clickListener.onDestinationClick(destinations.get(pos));
                    }
                }
            });
        }

        /**
         * Bind destination data to views.
         *
         * @param destination Destination object to display
         * @param position Position in list
         */
        public void bind(Destination destination, int position) {
            if (destination == null) {
                return;
            }
            tvTitle.setText(destination.getTitle());
            tvDescription.setText(destination.getBody());
            tvId.setText("ID: " + destination.getId());

            // Set delete button click listener
            if (btnDelete != null) {
                btnDelete.setOnClickListener(v -> {
                    if (deleteListener != null) {
                        deleteListener.onDeleteDestination(position);
                    }
                });
            }
        }
    }
}
