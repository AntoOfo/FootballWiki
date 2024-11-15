package com.example.cw2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ClubsByLeague_Activity extends AppCompatActivity {

    private EditText enterLeagueText;
    private Button retrieveBtn;
    private Button saveClubsBtn;
    private TextView clubLeagueText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clubs_by_league);

        enterLeagueText = findViewById(R.id.enterLeagueText);
        retrieveBtn = findViewById(R.id.retrieveBtn);
        saveClubsBtn = findViewById(R.id.saveClubsBtn);
        clubLeagueText = findViewById(R.id.clubLeagueText);

        retrieveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the league name entered by the user
                String leagueName = enterLeagueText.getText().toString().trim();

                if (!leagueName.isEmpty()) {
                    // call method to get clubs from web
                    callVolley(leagueName);
                } else {
                    // Show a message if no league name is entered
                    clubLeagueText.setText("Please enter a league name.");
                }
            }
        });
    }

    private void callVolley(String leagueName) {
        String url = "https://www.thesportsdb.com/api/v1/json/3/search_all_teams.php?l=English%20Premier%20League" + leagueName.replace(" ", "%20");

        RequestQueue queue = Volley.newRequestQueue(this);
    }
}