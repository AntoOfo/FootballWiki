package com.example.cw2;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ClubDao {
    @Insert void insert(ClubEntity club);

    @Insert void insertAll(List<ClubEntity> clubs);
    @Query("SELECT* FROM ClubTable")
    List<ClubEntity> getAllClubs();

    // clubs name, short and location case insensitive
    @Query("SELECT * FROM ClubTable WHERE " +
            "LOWER(strTeam) LIKE '%' || LOWER(:query) || '%' OR " +
            "LOWER(strTeamShort) LIKE '%' || LOWER(:query) || '%' OR " +
            "LOWER(strLocation) LIKE '%' || LOWER(:query) || '%'")
    List<ClubEntity> searchClubsByName(String query);

}
