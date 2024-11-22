package com.example.cw2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class JerseySearchInstrumentedTest {

    @Test
    public void testInputIsProcessed() {
        try (ActivityScenario<JerseySearch_Activity> scenario = ActivityScenario.launch(JerseySearch_Activity.class)) {
            scenario.onActivity(activity -> {
                // simulate user input
                String query = "Manchester United";

                // do search
                activity.searchForTeam(query);

                // Make sure the jerseyList is updated
                assertNotNull("Jersey list shouldnt be empty", activity.jerseyList);
            });
        }
    }

    @Test
    public void testSavingAndRetrieving() {
        try (ActivityScenario<JerseySearch_Activity> scenario = ActivityScenario.launch(JerseySearch_Activity.class)) {
            scenario.onActivity(activity -> {
                // simulate adding jersey url to the list
                List<String> mockJerseyList = new ArrayList<>();
                String mockJerseyUrl = "https://example.com/jersey.jpg";
                mockJerseyList.add(mockJerseyUrl);

                // add jerseys to the activity's list
                activity.jerseyList.clear();
                activity.jerseyList.addAll(mockJerseyList);

            });
        }
    }
}
