package com.example.cw2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ClubsByLeague_Activity extends AppCompatActivity {

    private EditText enterLeagueText;
    private Button retrieveBtn;
    private Button saveClubsBtn;
    private TextView clubLeagueText;

    private List<ClubEntity> clubsList = new ArrayList<>();

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
                    getClubsByLeague(leagueName);
                } else {
                    // Show a message if no league name is entered
                    clubLeagueText.setText("Please enter a league name.");
                }
            }
        });

        saveClubsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!clubsList.isEmpty()) {
                    saveClubs(clubsList);  // Save the clubs to the Room database
                } else {
                    Toast.makeText(ClubsByLeague_Activity.this, "No clubs to save.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // get teams based on the league id
    private void getClubsByLeague(String leagueName) {
        // Replace spaces in league name with "%20" to match API format
        String formattedLeagueName = leagueName.replace(" ", "%20");
        String url = "https://www.thesportsdb.com/api/v1/json/3/search_all_teams.php?l=" + formattedLeagueName;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        // Check if "teams" array is in the response
                        if (response.has("teams")) {
                            JSONArray teamsArray = response.getJSONArray("teams");
                            StringBuilder clubsInfo = new StringBuilder();

                            clubsList.clear(); // Clear list to avoid duplicates

                            // Iterate through each team and extract details
                            for (int i = 0; i < teamsArray.length(); i++) {
                                JSONObject team = teamsArray.getJSONObject(i);

                                String idTeam = team.optString("idTeam", "N/A");
                                String name = team.optString("strTeam", "N/A");
                                String teamShort = team.optString("strTeamShort", "N/A");
                                String alternate = team.optString("strAlternate", "N/A");
                                String formedYear = team.optString("intFormedYear", "N/A");
                                String strLeague = team.optString("strLeague", "N/A");
                                String idLeague = team.optString("idLeague", "N/A");
                                String stadium = team.optString("strStadium", "N/A");
                                String keywords = team.optString("strKeywords", "N/A");
                                String stadiumLocation = team.optString("strStadiumLocation", "N/A");
                                String stadiumCapacity = team.optString("intStadiumCapacity", "N/A");
                                String website = team.optString("strWebsite", "N/A");
                                String teamLogo = team.optString("strTeamLogo", "N/A");

                                // Create a new ClubEntity and add it to the list
                                ClubEntity club = new ClubEntity();
                                club.idTeam = idTeam;
                                club.strTeam = name;
                                club.strTeamShort = teamShort;
                                club.strTeamAlternate = alternate;
                                club.intFormedYear = formedYear;
                                club.strLeague = strLeague;
                                club.idLeague = idLeague;
                                club.strStadium = stadium;
                                club.strKeywords = keywords;
                                club.strLocation = stadiumLocation;
                                club.intStadiumCapacity = stadiumCapacity;
                                club.strWebsite = website;
                                club.strLogo = teamLogo;

                                clubsList.add(club);

                                // Append details to display on UI
                                clubsInfo.append("ID Team: ").append(idTeam).append("\n")
                                        .append("Name: ").append(name).append("\n")
                                        .append("Short Name: ").append(teamShort).append("\n")
                                        .append("Alternate Names: ").append(alternate).append("\n")
                                        .append("Formed Year: ").append(formedYear).append("\n")
                                        .append("League: ").append(strLeague).append("\n")
                                        .append("ID League: ").append(idLeague).append("\n")
                                        .append("Stadium: ").append(stadium).append("\n")
                                        .append("Keywords: ").append(keywords).append("\n")
                                        .append("Stadium Location: ").append(stadiumLocation).append("\n")
                                        .append("Stadium Capacity: ").append(stadiumCapacity).append("\n")
                                        .append("Website: ").append(website).append("\n")
                                        .append("Logo URL: ").append(teamLogo).append("\n\n");
                            }

                            clubLeagueText.setText(clubsInfo.toString());
                        } else {
                            clubLeagueText.setText("No teams found for this league.");
                        }
                    } catch (Exception e) {
                        clubLeagueText.setText("Error processing data. Try again.");
                    }
                },
                error -> clubLeagueText.setText("Error fetching data. Check your internet connection.")
        );

        queue.add(jsonObjectRequest);
    }

    private void saveClubs(List<ClubEntity> clubs) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Build the database instance
                ClubDatabase db = Room.databaseBuilder(getApplicationContext(),
                        ClubDatabase.class, "club-database").build();

                // Insert all clubs into the database
                db.clubDao().insertAll(clubs);

                // Notify the user after saving in the UI thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ClubsByLeague_Activity.this, "Clubs saved to database!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }
}