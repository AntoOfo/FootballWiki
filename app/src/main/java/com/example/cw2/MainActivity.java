package com.example.cw2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // declaring league db and league dao
    private LeagueDatabase leagueDatabase;
    private LeagueDao leagueDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addBtn = (Button) findViewById(R.id.addBtn);
        Button searchLeagueBtn = (Button) findViewById(R.id.searchLeagueBtn);
        Button searchClubBtn = (Button) findViewById(R.id.searchClubBtn);
        Button wideSearchBtn = (Button) findViewById(R.id.jerseySearchBtn);

        // initialise db and dao
        leagueDatabase = LeagueDatabase.getInstance(getApplicationContext());
        leagueDao = leagueDatabase.leagueDao();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Predefined leagues
                List<LeagueEntity> leagues = new ArrayList<>();
                leagues.add(new LeagueEntity(4330, "Scottish Premier League", "Soccer", "Scottish Premiership, SPFL"));
                leagues.add(new LeagueEntity(4331, "German Bundesliga", "Soccer", "Bundesliga, Fußball-Bundesliga"));
                leagues.add(new LeagueEntity(4332, "Italian Serie A", "Soccer", "Serie A"));
                leagues.add(new LeagueEntity(4334, "French Ligue 1", "Soccer", "Ligue 1 Conforama"));
                leagues.add(new LeagueEntity(4335, "Spanish La Liga", "Soccer", "LaLiga Santander, La Liga"));
                leagues.add(new LeagueEntity(4336, "Greek Superleague Greece", "Soccer", ""));
                leagues.add(new LeagueEntity(4337, "Dutch Eredivisie", "Soccer", "Eredivisie"));
                leagues.add(new LeagueEntity(4338, "Belgian Pro League", "Soccer", "Jupiler Pro League"));

                // Insert the leagues into the database on a background thread
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        leagueDao.addLeagues(leagues);  // Insert leagues
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Leagues added to the database!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();
            }
        });

        searchLeagueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ClubsByLeague_Activity.class));
            }
        });

        searchClubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SearchClubs_Activity.class));
            }
        });

        wideSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, JerseySearch_Activity.class));
            }
        });
    }
}