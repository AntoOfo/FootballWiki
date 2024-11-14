package com.example.cw2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addBtn = (Button) findViewById(R.id.addBtn);
        Button searchLeagueBtn = (Button) findViewById(R.id.searchLeagueBtn);
        Button searchClubBtn = (Button) findViewById(R.id.searchClubBtn);
    }
}