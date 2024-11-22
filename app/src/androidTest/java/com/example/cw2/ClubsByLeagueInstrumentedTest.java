package com.example.cw2;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ClubsByLeagueInstrumentedTest {

    @Test
    public void testInputIsProcessed() {
        try (ActivityScenario<ClubsByLeague_Activity> scenario = ActivityScenario.launch(ClubsByLeague_Activity.class)) {
            scenario.onActivity(activity -> {
                String leagueName = "English Premier League";

                // process input
                activity.getClubsByLeague(leagueName);

                // make sure the club list is updated
                assertNotNull(activity.clubsList);
            });
        }
    }

    @Test
    public void testSavingAndRetrieving() {
        try (ActivityScenario<ClubsByLeague_Activity> scenario = ActivityScenario.launch(ClubsByLeague_Activity.class)) {
            scenario.onActivity(activity -> {
                // simulate list of clubs
                List<ClubEntity> mockClubs = new ArrayList<>();
                ClubEntity mockClub = new ClubEntity();
                mockClub.idTeam = "12345";
                mockClub.strTeam = "Test FC";
                mockClubs.add(mockClub);

                // save clubs to db
                activity.saveClubs(mockClubs);

                // simulate retrieving clubs from db
                List<ClubEntity> retrievedClubs = activity.clubsEntityList;

                // makes sure that saved and retrieved clubs are same
                assertEquals(mockClubs.size(), retrievedClubs.size());
            });
        }
    }
    }
