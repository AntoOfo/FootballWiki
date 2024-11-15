package com.example.cw2;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LeagueDao {

    // insert one league  // ignore if any exists based off id
    @Insert(onConflict = OnConflictStrategy.IGNORE) void addLeague(LeagueEntity league);

    // insert multiple leagues / ignore if any already exist
    @Insert(onConflict = OnConflictStrategy.IGNORE) void addLeagues(List<LeagueEntity> leagues);


    // get all leagues from db
    @Query("SELECT * FROM LeagueTable")
    List<LeagueEntity> getAllLeagues();

    // retrieve league by id
    @Query("SELECT * FROM LeagueTable WHERE idLeague = :id")
    LeagueEntity getLeagueById(int id);
}
