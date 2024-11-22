package com.example.cw2;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class JerseySearch_Activity extends AppCompatActivity {

    private EditText searchEntry;
    private Button searchBtn;
    private ListView resultsListView;
    private List<String> jerseyList = new ArrayList<>();  // List to hold jersey urls
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jersey_search);

        // Initialize UI elements
        searchEntry = findViewById(R.id.searchEntry);
        searchBtn = findViewById(R.id.searchBtn);
        resultsListView = findViewById(R.id.resultsListView);

        // adapter for listview to hold jerseys
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2, jerseyList) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // reuse the convertView if not null, from online source
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_jersey, parent, false);
                }

                // finds imageview for displaying the jersey image
                ImageView jerseyImage = convertView.findViewById(R.id.jerseyImage);

                // get jersey url at the current position
                String jerseyUrl = getItem(position);


                // load the image in the imageview
                loadImage(jerseyImage, jerseyUrl);

                return convertView;
            }
        };

        // set adapter to listview
        resultsListView.setAdapter(adapter);

        // restore list on rotation
        if (savedInstanceState != null) {
            ArrayList<String> savedJerseyList = (ArrayList<String>) savedInstanceState.getSerializable("jerseyList");
            if (savedJerseyList != null) {
                jerseyList.clear();
                jerseyList.addAll(savedJerseyList);
                adapter.notifyDataSetChanged();
            }
        }

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchEntry.getText().toString().trim();

                if (!query.isEmpty()) {
                    searchForTeam(query);  // call method to search for the team name
                } else {
                    Toast.makeText(JerseySearch_Activity.this, "Please enter a club name to search for jerseys", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // method to search for a team from input
    private void searchForTeam(String query) {
        String eplTeamsUrl = "https://www.thesportsdb.com/api/v1/json/3/search_all_teams.php?l=English%20Premier%20League";
        RequestQueue queue = Volley.newRequestQueue(JerseySearch_Activity.this);

        // request to get EPL teams
        JsonObjectRequest teamsRequest = new JsonObjectRequest(eplTeamsUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray teamsArray = response.getJSONArray("teams");
                    boolean teamFound = false;

                    // go through list to check for matches
                    for (int i = 0; i < teamsArray.length(); i++) {
                        JSONObject team = teamsArray.getJSONObject(i);
                        String teamName = team.getString("strTeam");

                        if (teamName.toLowerCase().contains(query.toLowerCase())) {
                            String teamId = team.getString("idTeam");
                            teamFound = true;

                            // grab jersey image for team
                            getJerseyImage(teamId);
                            break;
                        }
                    }

                    if (!teamFound) {    // if no teams found
                        Toast.makeText(JerseySearch_Activity.this, "No teams found matching your search", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(JerseySearch_Activity.this, "Error parsing team data", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(JerseySearch_Activity.this, "Error fetching teams data", Toast.LENGTH_SHORT).show();
            }
        });

        // add request to request queue
        queue.add(teamsRequest);
    }

    // grab jersey image using idTeam
    private void getJerseyImage(String teamId) {
        String jerseyUrl = "https://www.thesportsdb.com/api/v1/json/3/lookupequipment.php?id=" + teamId;
        RequestQueue queue = Volley.newRequestQueue(JerseySearch_Activity.this);

        // request to get jersey data
        JsonObjectRequest jerseyRequest = new JsonObjectRequest(jerseyUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray equipmentArray = response.getJSONArray("equipment");

                    if (equipmentArray.length() > 0) {
                        // go through each jersey to check its season
                        for (int i = 0; i < equipmentArray.length(); i++) {
                            JSONObject jersey = equipmentArray.getJSONObject(i);
                            String strSeason = jersey.getString("strSeason");

                            // check if season is either 2024-2025 or 2023-2024
                            if (strSeason.equals("2024-2025") || strSeason.equals("2023-2024")) {
                                String strEquipment = jersey.getString("strEquipment");

                                if (strEquipment != null && !strEquipment.isEmpty()) {
                                    // add jersey image url to list and update
                                    addJerseyToList(strEquipment);
                                } else {
                                    Toast.makeText(JerseySearch_Activity.this, "No jersey image found", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    } else {
                        Toast.makeText(JerseySearch_Activity.this, "No jersey data found for this team", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(JerseySearch_Activity.this, "Error fetching jersey data", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(JerseySearch_Activity.this, "Error fetching jersey data", Toast.LENGTH_SHORT).show();
            }
        });

        // add request to the request queue
        queue.add(jerseyRequest);
    }

    // update listview with the jersey image URL
    private void addJerseyToList(String jerseyImageUrl) {
            jerseyList.add(jerseyImageUrl);  // add jersey image url to list

            runOnUiThread(() -> {

            adapter.notifyDataSetChanged();  // refresh the listview
        });
    }

    private void loadImage(ImageView imageView, String url) {
        new Thread(() -> {
            try {
                // get the inputstream from the image url
                InputStream inputStream = new URL(url).openStream();
                // decode it into a bitmap
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                // set bitmap to the imageview on ui thread
                runOnUiThread(() -> imageView.setImageBitmap(bitmap));
            } catch (Exception e) {
                runOnUiThread(() -> imageView.setImageResource(R.drawable.placeholder_img));
            }
        }).start();
    }

    // save the jerseyList to the instance state on rotation
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("jerseyList", new ArrayList<>(jerseyList));
    }
}