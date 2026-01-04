package com.example.travelplane;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;

/**
 * Destination Detail Activity
 * Shows destination title/description and actions: view map, open booking.
 */
public class DestinationDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_DESC = "extra_desc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        String title = getIntent().getStringExtra(EXTRA_TITLE);
        String desc = getIntent().getStringExtra(EXTRA_DESC);

        if (title == null) title = "Destination";
        if (desc == null) desc = "";

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvDesc = findViewById(R.id.tvDescription);
        MaterialButton btnMap = findViewById(R.id.btnOpenMap);
        MaterialButton btnBooking = findViewById(R.id.btnBooking);
        MaterialButton btnWeb = findViewById(R.id.btnOpenWeb);

        tvTitle.setText(title);
        tvDesc.setText(desc);

        // Open Maps via geo intent; fallback to web maps
        String finalTitle = title;
        btnMap.setOnClickListener(v -> {
            try {
                Uri geoUri = Uri.parse("geo:0,0?q=" + Uri.encode(finalTitle));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, geoUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            } catch (Exception e) {
                // Fallback to web maps
                String url = "https://www.google.com/maps/search/?api=1&query=" + Uri.encode(finalTitle);
                openWeb(url, finalTitle);
            }
        });

        btnBooking.setOnClickListener(v -> openWeb("https://www.booking.com", "Booking.com"));

        btnWeb.setOnClickListener(v -> {
            // Open Maps search in our WebView for this destination
            String url = "https://www.google.com/maps/search/?api=1&query=" + Uri.encode(finalTitle);
            openWeb(url, finalTitle);
        });
    }

    private void openWeb(String url, String title) {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Unable to open web page", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

