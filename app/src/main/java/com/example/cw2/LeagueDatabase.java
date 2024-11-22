package com.example.cw2;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {LeagueEntity.class}, version = 1)
public abstract class LeagueDatabase extends RoomDatabase {

    private static final String dbname = "league_database";
    private static LeagueDatabase leagueDatabase;

    // synch method so only one db instance is made
    public static synchronized LeagueDatabase getInstance(Context context) {
        // if instance is null make new one
        if (leagueDatabase == null) {
            leagueDatabase = Room.databaseBuilder(context.getApplicationContext(),
                            LeagueDatabase.class, dbname)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return leagueDatabase;
    }

    // access the DAO
    public abstract LeagueDao leagueDao();
}
