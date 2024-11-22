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
    private List<String> jerseyList = new ArrayList<>();  // List to hold jersey URLs
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jersey_search);

        // Initialize UI elements
        searchEntry = findViewById(R.id.searchEntry);
        searchBtn = findViewById(R.id.searchBtn);
        resultsListView = findViewById(R.id.resultsListView);

        // Initialize the adapter for ListView to show jersey image URLs
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2, jerseyList) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Reuse the convertView if it's not null
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_jersey, parent, false);
                }

                // Find the ImageView for displaying the jersey image
                ImageView jerseyImage = convertView.findViewById(R.id.jerseyImage);

                // Get the jersey URL at the current position
                String jerseyUrl = getItem(position);


                // Load and display the image in the ImageView
                loadImage(jerseyImage, jerseyUrl);

                return convertView;
            }
        };

        resultsListView.setAdapter(adapter);

        if (savedInstanceState != null) {
            // Restore jerseyList from savedInstanceState
            ArrayList<String> savedJerseyList = (ArrayList<String>) savedInstanceState.getSerializable("jerseyList");
            if (savedJerseyList != null) {
                jerseyList.clear();
                jerseyList.addAll(savedJerseyList);
                adapter.notifyDataSetChanged();
            }
        }

        // Set the click listener for the search button
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchEntry.getText().toString().trim();

                if (!query.isEmpty()) {
                    searchForTeam(query);  // Call method to search for the team by name
                } else {
                    Toast.makeText(JerseySearch_Activity.this, "Please enter a club name to search for jerseys", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Method to search for a team based on the input query
    private void searchForTeam(String query) {
        String eplTeamsUrl = "https://www.thesportsdb.com/api/v1/json/3/search_all_teams.php?l=English%20Premier%20League";
        RequestQueue queue = Volley.newRequestQueue(JerseySearch_Activity.this);

        // Request to fetch all EPL teams
        JsonObjectRequest teamsRequest = new JsonObjectRequest(eplTeamsUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray teamsArray = response.getJSONArray("teams");
                    boolean teamFound = false;

                    // Iterate through the list of teams to check if any matches the query
                    for (int i = 0; i < teamsArray.length(); i++) {
                        JSONObject team = teamsArray.getJSONObject(i);
                        String teamName = team.getString("strTeam");

                        if (teamName.toLowerCase().contains(query.toLowerCase())) {
                            String teamId = team.getString("idTeam");
                            teamFound = true;

                            // Now that we have the team ID, call to fetch jersey data
                            fetchJerseyImage(teamId);
                            break;
                        }
                    }

                    if (!teamFound) {
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

        // Add the request to the request queue
        queue.add(teamsRequest);
    }

    // Method to fetch the jersey image using the team's idTeam
    private void fetchJerseyImage(String teamId) {
        String jerseyUrl = "https://www.thesportsdb.com/api/v1/json/3/lookupequipment.php?id=" + teamId;
        RequestQueue queue = Volley.newRequestQueue(JerseySearch_Activity.this);

        // Request to fetch jersey data for the team
        JsonObjectRequest jerseyRequest = new JsonObjectRequest(jerseyUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray equipmentArray = response.getJSONArray("equipment");

                    if (equipmentArray.length() > 0) {
                        // Get the first jersey (or you can adjust logic to show multiple)
                        JSONObject jersey = equipmentArray.getJSONObject(0);
                        String strEquipment = jersey.getString("strEquipment"); // The jersey image URL is in strThumb

                        if (strEquipment != null && !strEquipment.isEmpty()) {
                            // Add the jersey image URL to the list and update the ListView
                            addJerseyToList(strEquipment);
                        } else {
                            Toast.makeText(JerseySearch_Activity.this, "No jersey image found", Toast.LENGTH_SHORT).show();
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

        // Add the request to the request queue
        queue.add(jerseyRequest);
    }

    // Method to update the ListView with the jersey image URL
    private void addJerseyToList(String jerseyImageUrl) {
            jerseyList.add(jerseyImageUrl);  // Add jersey image URL to the list

            runOnUiThread(() -> {

            adapter.notifyDataSetChanged();  // Notify adapter to refresh the ListView
        });
    }

    private void loadImage(ImageView imageView, String url) {
        new Thread(() -> {
            try {
                // Open connection and get the InputStream from the image URL
                InputStream inputStream = new URL(url).openStream();
                // Decode the InputStream into a Bitmap
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                // Set the Bitmap to the ImageView on the UI thread
                runOnUiThread(() -> imageView.setImageBitmap(bitmap));
            } catch (Exception e) {
                // In case of error, set a placeholder image
                runOnUiThread(() -> imageView.setImageResource(R.drawable.placeholder_img));
            }
        }).start();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the jerseyList to the instance state
        outState.putSerializable("jerseyList", new ArrayList<>(jerseyList));
    }
}