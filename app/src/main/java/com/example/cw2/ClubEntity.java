package com.example.cw2;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ClubTable")
public class ClubEntity {
    @PrimaryKey(autoGenerate = true)
    public int id; // Auto-generated primary key

    @ColumnInfo(name = "teamName")
    public String teamName;

    @ColumnInfo(name = "teamShort")
    public String teamShort;

    @ColumnInfo(name = "formedYear")
    public String formedYear;

    @ColumnInfo(name = "stadium")
    public String stadium;

    @ColumnInfo(name = "location")
    public String location;

    @ColumnInfo(name = "website")
    public String website;

    @ColumnInfo(name = "leagueId")
    public String leagueId;

    @ColumnInfo(name = "logoUrl")
    public String logoUrl;

}
