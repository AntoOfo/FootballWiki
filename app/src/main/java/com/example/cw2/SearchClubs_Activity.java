package com.example.cw2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class SearchClubs_Activity extends AppCompatActivity {

    private EditText searchEntry;
    private Button searchBtn;
    private LinearLayout resultsContainer;

    private ClubDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_clubs);

        searchEntry = findViewById(R.id.searchEntry);
        searchBtn = findViewById(R.id.searchBtn);
        resultsContainer = findViewById(R.id.resultsContainer);

        db = Room.databaseBuilder(getApplicationContext(),
                ClubDatabase.class, "club-database").build();

        searchBtn.setOnClickListener(v -> {
            String query = searchEntry.getText().toString().trim();

            if (!query.isEmpty()) {
                displayMessage("Searching");
                // Start search in a background thread
                new Thread(() -> searchClubs(query)).start();
            } else {
                displayMessage("Please enter a search.");
            }
        });
    }

    private void searchClubs(String query) {
        // Perform a case-insensitive search and find clubs or leagues with the query
        List<ClubEntity> clubs = db.clubDao().searchClubsByName(query);

        runOnUiThread(() -> {
            resultsContainer.removeAllViews(); // Clear previous results
            if (clubs.isEmpty()) {
                displayMessage("No clubs found.");
            } else {
                for (ClubEntity club : clubs) {
                    addClub(club);
                }
            }
        });
    }

    private void addClub(ClubEntity club) {
        // Create ImageView for the logo
        ImageView logoView = new ImageView(this);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(200, 200);
        imageParams.setMargins(0, 0, 0, 16);
        logoView.setLayoutParams(imageParams);

        // Load the logo (background thread)
        new Thread(() -> {
            Bitmap logo = loadImage(club.logoUrl);
            runOnUiThread(() -> {
                if (logo != null) {
                    logoView.setImageBitmap(logo);
                } else {
                    logoView.setImageResource(R.drawable.placeholder_img); // Default image
                }
            });
        }).start();

        TextView clubDetails = new TextView(this);
        clubDetails.setText(
                "Club Name: " + club.teamName + "\n" +
                "League: " + club.leagueId + "\n" +
                "Short Name: " + club.teamShort + "\n" +
                "Year Formed: " + club.formedYear + "\n" +
                "Stadium: " + club.stadium + "\n" +
                "Location: " + club.location + "\n" +
                "Website: " + club.website
        );
        clubDetails.setPadding(16, 16, 16, 16);

        // Add ImageView and TextView to the container
        resultsContainer.addView(logoView);
        resultsContainer.addView(clubDetails);
    }

    private void displayMessage(String message) {
        resultsContainer.removeAllViews();
        TextView messageView = new TextView(this);
        messageView.setText(message);
        messageView.setPadding(16, 16, 16, 16);
        resultsContainer.addView(messageView);
    }

    private Bitmap loadImage(String urlString) {

        if (urlString == null || urlString.isEmpty()) {
            Log.e("SearchClubs_Activity", "null URL: " + urlString);  // keeps returning null
            return null;
        }

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    }
