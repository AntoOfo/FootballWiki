package com.example.cw2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

public class SearchClubs_Activity extends AppCompatActivity {

    private EditText searchEntry;
    private Button searchBtn;
    private TextView allDataText;

    private ClubDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_clubs);

        searchEntry = findViewById(R.id.searchEntry);
        searchBtn = findViewById(R.id.searchBtn);
        allDataText = findViewById(R.id.allDataText);

        db = Room.databaseBuilder(getApplicationContext(),
                ClubDatabase.class, "club-database").build();

        searchBtn.setOnClickListener(v -> {
            String query = searchEntry.getText().toString().trim();

            if (!query.isEmpty()) {
                // Start search in a background thread
                new Thread(() -> searchClubs(query)).start();
            } else {
                allDataText.setText("Please enter a search.");
            }
        });
    }

    private void searchClubs(String query) {
        // Perform a case-insensitive search and find clubs or leagues with the query
        List<ClubEntity> clubs = db.clubDao().searchClubsByName(query);

        // Display the results on the main thread
        runOnUiThread(() -> {
            if (clubs.isEmpty()) {
                allDataText.setText("No clubs found.");
            } else {
                StringBuilder results = new StringBuilder();

                for (ClubEntity club : clubs) {
                    results.append("Club Name: ").append(club.teamName).append("\n")
                            .append("League: ").append(club.leagueId).append("\n")
                            .append("Short Name: ").append(club.teamShort).append("\n")
                            .append("Year Formed: ").append(club.formedYear).append("\n")
                            .append("Stadium: ").append(club.stadium).append("\n")
                            .append("Location: ").append(club.location).append("\n")
                            .append("Website: ").append(club.website).append("\n\n");
                }

                allDataText.setText(results.toString());
            }
        });
    }
}