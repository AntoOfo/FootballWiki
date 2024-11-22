package com.example.cw2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

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

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
public class SearchClubs_Activity extends AppCompatActivity {

    private EditText searchEntry;
    private Button searchBtn;
    private Button searchJerseyBtn;
    private ListView resultsListView;

    List<ClubEntity> clubsList = new ArrayList<>();
    private ArrayAdapter<ClubEntity> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_clubs);

        searchEntry = findViewById(R.id.searchEntry);
        searchBtn = findViewById(R.id.searchBtn);
        resultsListView = findViewById(R.id.resultsListView);

        // adapter for club list
        adapter = new ArrayAdapter<ClubEntity>(this, 0, clubsList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // reuse the convertView if not null, from online source
                if (convertView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    convertView = inflater.inflate(R.layout.item_clubs_result, parent, false);
                }

                ImageView clubLogo = convertView.findViewById(R.id.clubLogo);
                TextView clubDetails = convertView.findViewById(R.id.clubDetails);

                // get club data and put em to ui
                ClubEntity club = clubsList.get(position);
                if (club != null) {
                    String details = "ID: " + club.idTeam + "\n" +
                            "Name: " + club.strTeam + "\n" +
                            "Short Name: " + club.strTeamShort + "\n" +
                            "Alternate Names: " + club.strTeamAlternate + "\n" +
                            "Formed Year: " + club.intFormedYear + "\n" +
                            "League: " + club.strLeague + "\n" +
                            "League ID: " + club.idLeague + "\n" +
                            "Stadium: " + club.strStadium + "\n" +
                            "Keywords: " + club.strKeywords + "\n" +
                            "Location: " + club.strLocation + "\n" +
                            "Stadium Capacity: " + club.intStadiumCapacity + "\n" +
                            "Website: " + club.strWebsite;
                    clubDetails.setText(details);
                    loadImage(clubLogo, club.strLogo);
                }

                return convertView;
            }
        };

        resultsListView.setAdapter(adapter);

        searchBtn.setOnClickListener(v -> {
            String query = searchEntry.getText().toString().trim();

            // use search method if query isnt empty
            if (!query.isEmpty()) {
                searchClubs(query);
            } else {
                Toast.makeText(this, "Please enter a search term", Toast.LENGTH_SHORT).show();
            }
        });

        // restore clubsList on rotation
        if (savedInstanceState != null) {
            ArrayList<ClubEntity> savedClubsList = (ArrayList<ClubEntity>) savedInstanceState.getSerializable("clubsList");
            if (savedClubsList != null) {
                clubsList.clear();
                clubsList.addAll(savedClubsList);
                adapter.notifyDataSetChanged();
            }
        }
    }

    // save clubs on rotation
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the clubsList to the instance state
        outState.putSerializable("clubsList", new ArrayList<>(clubsList));
    }

    // search clubs by name
    void searchClubs(String query) {
        new Thread(() -> {
            ClubDatabase db = Room.databaseBuilder(getApplicationContext(), ClubDatabase.class, "club-database").build();
            List<ClubEntity> dbResults = db.clubDao().searchClubsByName(query); // use Room DAO to search
            runOnUiThread(() -> {
                // if results found, update list+adapter
                if (!dbResults.isEmpty()) {
                    clubsList.clear();
                    clubsList.addAll(dbResults);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "Results loaded from database.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "No results found..", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();

    }


    private void loadImage(ImageView imageView, String url) {
        new Thread(() -> {
            try {
                // load image from url
                InputStream inputStream = new URL(url).openStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                runOnUiThread(() -> imageView.setImageBitmap(bitmap));
            } catch (Exception e) {
                runOnUiThread(() -> imageView.setImageResource(R.drawable.placeholder_img));
            }
        }).start();
    }
}

