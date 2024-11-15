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
                    getLeagueId(leagueName);
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

    // grabs league id
    private void getLeagueId(String leagueName) {
        String url = "https://www.thesportsdb.com/api/v1/json/3/all_leagues.php";
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        // to find matching leagues
                        JSONArray leaguesArray = response.getJSONArray("leagues");
                        String idLeague = null;

                        for (int i = 0;i < leaguesArray.length();i++) {
                            JSONObject league = leaguesArray.getJSONObject(i);
                            String name = league.optString("strLeague", "").trim();

                            // check if the league name matches user input
                            if (name.equalsIgnoreCase(leagueName)) {
                                idLeague = league.optString("idLeague", null);   // store id
                                break;
                            }
                        } if (idLeague != null) {
                            getClubsByLeagueId(idLeague); // get club with league id
                        } else {
                            clubLeagueText.setText("League not found. Please try again.");
                        }
                    } catch (Exception e) {
                        clubLeagueText.setText("There was a problem. Try again.");
                    }
                },
                error -> {
                    // Handle any network errors
                    clubLeagueText.setText("There was a problem. Try again.");
                }
        ); queue.add(jsonObjectRequest);
    }

    // get teams based on the league id
    private void getClubsByLeagueId(String leagueId) {
        String url = "https://www.thesportsdb.com/api/v1/json/3/lookup_all_teams.php?id=" + leagueId;
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        // check if teams array is in response
                        if (response.has("teams")) {
                            JSONArray teamsArray = response.getJSONArray("teams");
                            StringBuilder clubsInfo = new StringBuilder();

                            clubsList.clear();   // clear list for duplicates

                            // go through teams and add them to list
                            for (int i = 0; i < teamsArray.length(); i++) {
                                JSONObject team = teamsArray.getJSONObject(i);

                                String teamName = team.getString("strTeam");
                                String teamShort = team.getString("strTeamShort");
                                String formedYear = team.getString("intFormedYear");
                                String stadium = team.getString("strStadium");
                                String location = team.getString("strLocation");
                                String website = team.getString("strWebsite");

                                // new entity w properties
                                ClubEntity club = new ClubEntity();
                                club.teamName = teamName;
                                club.teamShort = teamShort;
                                club.formedYear = formedYear;
                                club.stadium = stadium;
                                club.location = location;
                                club.website = website;
                                club.leagueId = leagueId;

                                clubsList.add(club);

                                clubsInfo.append("Team Name: ").append(teamName).append("\n")
                                        .append("Short Name: ").append(teamShort).append("\n")
                                        .append("Formed Year: ").append(formedYear).append("\n")
                                        .append("Stadium: ").append(stadium).append("\n")
                                        .append("Location: ").append(location).append("\n")
                                        .append("Website: ").append(website).append("\n\n");
                            }

                            clubLeagueText.setText(clubsInfo.toString());

                        } else {
                            clubLeagueText.setText("No teams found for this league.");
                        }
                    } catch (Exception e) {
                        clubLeagueText.setText("Problem with finding teams. Try again.");
                    }
                },
                error -> {
                    clubLeagueText.setText("Problem with finding teams. Try again.");
                }
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