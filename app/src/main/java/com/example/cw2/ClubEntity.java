package com.example.cw2;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ClubTable")
public class ClubEntity {
    @PrimaryKey(autoGenerate = true)
    public int id; // Auto-generated primary key

    public String teamName;
    public String teamShort;
    public String formedYear;
    public String stadium;
    public String location;
    public String website;
    public String leagueId;

}
