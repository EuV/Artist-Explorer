package ru.yandex.academy.euv.artistexplorer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.assertion.ViewAssertions.matches;

/**
 * UI test running on a device.
 * Opens an artist details and returns to the list of artists.
 */
@RunWith(AndroidJUnit4.class)
public class BasicBehaviorTest {

    @Rule
    public ActivityTestRule<MainActivity> activity = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void openArtistDetailsAndReturn() {
        onView(withId(R.id.artist_recycler_view))
                .perform(click());

        onView(withId(R.id.text_artist_description))
                .check(matches(isDisplayed()))
                .perform(pressBack());

        onView(withId(R.id.artist_recycler_view))
                .check(matches(isDisplayed()));
    }
}
