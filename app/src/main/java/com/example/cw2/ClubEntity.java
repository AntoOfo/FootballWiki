package com.example.cw2;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ClubTable")
public class ClubEntity {
    @PrimaryKey(autoGenerate = true)
    public int id; // Auto-generated primary key

    @ColumnInfo(name = "idTeam")
    public String idTeam;

    @ColumnInfo(name = "strTeam")
    public String strTeam;

    @ColumnInfo(name = "strTeamShort")
    public String strTeamShort;

    @ColumnInfo(name = "strTeamAlternate")
    public String strTeamAlternate;

    @ColumnInfo(name = "intFormedYear")
    public String intFormedYear;

    @ColumnInfo(name = "strLeague")
    public String strLeague;

    @ColumnInfo(name = "idLeague")
    public String idLeague;

    @ColumnInfo(name = "strStadium")
    public String strStadium;

    @ColumnInfo(name = "strKeywords")
    public String strKeywords;

    @ColumnInfo(name = "strLocation")
    public String strLocation;

    @ColumnInfo(name = "intStadiumCapacity")
    public String intStadiumCapacity;

    @ColumnInfo(name = "strWebsite")
    public String strWebsite;

    @ColumnInfo(name = "strLogo")
    public String strLogo;



}
