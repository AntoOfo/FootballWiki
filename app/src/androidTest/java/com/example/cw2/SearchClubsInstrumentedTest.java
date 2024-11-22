package com.example.cw2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class SearchClubsInstrumentedTest {

    @Test
    public void testInputIsProcessed() {
        try (ActivityScenario<SearchClubs_Activity> scenario = ActivityScenario.launch(SearchClubs_Activity.class)) {
            scenario.onActivity(activity -> {
                // user input
                String query = "Chelsea";

                // do the search
                activity.searchClubs(query);

                // make sure the clubsList is updated
                assertNotNull("Clubs list should not be null", activity.clubsList);

                // make sure clubsList has result
                assertTrue("Clubs list shouldn't be empty after search", activity.clubsList.size() > 0);
            });
        }
    }

    @Test
    public void testSavingAndRetrieving() {
        try (ActivityScenario<SearchClubs_Activity> scenario = ActivityScenario.launch(SearchClubs_Activity.class)) {
            scenario.onActivity(activity -> {
                // simulate adding a club entity to list
                List<ClubEntity> mockClubsList = new ArrayList<>();
                ClubEntity mockClub = new ClubEntity();
                mockClub.idTeam = "001";
                mockClub.strTeam = "Mock FC";
                mockClub.strLeague = "Mock League";
                mockClubsList.add(mockClub);

                // update activitys clubsList
                activity.clubsList.clear();
                activity.clubsList.addAll(mockClubsList);
            });
        }
    }
}
