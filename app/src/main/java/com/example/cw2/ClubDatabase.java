package com.example.cw2;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {ClubEntity.class}, version = 1)
public abstract class ClubDatabase extends RoomDatabase {
    public abstract ClubDao clubDao();
}
