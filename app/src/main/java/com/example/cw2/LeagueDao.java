package com.example.cw2;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LeagueDao {

    // insert one league
    @Insert void addLeague(LeagueEntity league);

    // insert multiple leagues
    @Insert void addLeagues(List<LeagueEntity> leagues);

    // get all leagues from db
    @Query("SELECT * FROM LeagueTable")
    List<LeagueEntity> getAllLeagues();

    // retrieve league by id
    @Query("SELECT * FROM LeagueTable WHERE idLeague = :id")
    LeagueEntity getLeagueById(int id);
}
