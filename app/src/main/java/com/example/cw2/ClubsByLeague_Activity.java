package com.example.cw2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
    private ListView clubsListView;

    // lists to hold club details and entities
    List<String> clubsList = new ArrayList<>();
    List<ClubEntity> clubsEntityList = new ArrayList<>();  // for saving to db
    private ArrayAdapter<String> adapter;  // for displaying in list

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clubs_by_league);

        enterLeagueText = findViewById(R.id.enterLeagueText);
        retrieveBtn = findViewById(R.id.retrieveBtn);
        saveClubsBtn = findViewById(R.id.saveClubsBtn);
        clubsListView = findViewById(R.id.clubsListView);

        // adapter setup for listview
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, clubsList);

        clubsListView.setAdapter(adapter);

        // restore list on rotation
        if (savedInstanceState != null) {
            clubsList = savedInstanceState.getStringArrayList("clubsList");
            if (clubsList != null) {
                adapter.clear();
                adapter.addAll(clubsList);
                adapter.notifyDataSetChanged();
            }
        }

        retrieveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the league name by the user
                String leagueName = enterLeagueText.getText().toString().trim();

                if (!leagueName.isEmpty()) {
                    getClubsByLeague(leagueName);   // call method to get clubs from web
                } else {
                    Toast.makeText(ClubsByLeague_Activity.this, "Please enter a league name!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        saveClubsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!clubsList.isEmpty()) {
                    saveClubs(clubsEntityList);  // save the clubs to room db
                } else {
                    Toast.makeText(ClubsByLeague_Activity.this, "No clubs to save.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // saves the clubsList to the instance state on rotation
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList("clubsList", new ArrayList<>(clubsList));
    }

    // get teams based on league name
    void getClubsByLeague(String leagueName) {
        // format spaces with %20 for url
        String formattedLeagueName = leagueName.replace(" ", "%20");
        String url = "https://www.thesportsdb.com/api/v1/json/3/search_all_teams.php?l=" + formattedLeagueName;

        RequestQueue queue = Volley.newRequestQueue(this);

        // get request to get club data from api
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.has("teams")) {        // check if teams array is in response
                            JSONArray teamsArray = response.getJSONArray("teams");

                            clubsList.clear(); // clears list to avoid duplicates
                            clubsEntityList.clear();  // clears entitys list too

                            // go through each team and get details
                            for (int i = 0; i < teamsArray.length(); i++) {
                                JSONObject team = teamsArray.getJSONObject(i);

                                // optString so it doesnt crash whole list and has a fallback
                                String idTeam = team.optString("idTeam", "N/A");
                                String name = team.optString("strTeam", "N/A");
                                String teamShort = team.optString("strTeamShort", "N/A");
                                String alternate = team.optString("strTeamAlternate", "N/A");
                                String formedYear = team.optString("intFormedYear", "N/A");
                                String strLeague = team.optString("strLeague", "N/A");
                                String idLeague = team.optString("idLeague", "N/A");
                                String stadium = team.optString("strStadium", "N/A");
                                String keywords = team.optString("strKeywords", "N/A");
                                String strLocation = team.optString("strLocation", "N/A");
                                String stadiumCapacity = team.optString("intStadiumCapacity", "N/A");
                                String website = team.optString("strWebsite", "N/A");
                                String strLogo = team.optString("strLogo", "N/A");

                                // creates new ClubEntity and add it to the list for db
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
                                club.strLocation = strLocation;
                                club.intStadiumCapacity = stadiumCapacity;
                                club.strWebsite = website;
                                club.strLogo = strLogo;

                                clubsEntityList.add(club);

                                String clubDetails = "ID: " + idTeam + "\n" +
                                        "Name: " + name + "\n" +
                                        "Short Name: " + teamShort + "\n" +
                                        "Alternate Names: " + alternate + "\n" +
                                        "Formed Year: " + formedYear + "\n" +
                                        "League: " + strLeague + "\n" +
                                        "League ID: " + idLeague + "\n" +
                                        "Stadium: " + stadium + "\n" +
                                        "Keywords: " + keywords + "\n" +
                                        "Stadium Location: " + strLocation + "\n" +
                                        "Stadium Capacity: " + stadiumCapacity + "\n" +
                                        "Website: " + website + "\n" +
                                        "Logo URL: " + strLogo;

                                clubsList.add(clubDetails);
                            }

                            // refresh list adapter
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(ClubsByLeague_Activity.this, "No teams found in this league!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(ClubsByLeague_Activity.this, "Error occurred. Try again...", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(ClubsByLeague_Activity.this, "Error occurred. Try again...", Toast.LENGTH_SHORT).show();
                }

        );

        queue.add(jsonObjectRequest);  // add request to volley queue
    }

    void saveClubs(List<ClubEntity> clubs) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // build db instance
                ClubDatabase db = Room.databaseBuilder(getApplicationContext(),
                        ClubDatabase.class, "club-database").build();

                db.clubDao().insertAll(clubs);
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