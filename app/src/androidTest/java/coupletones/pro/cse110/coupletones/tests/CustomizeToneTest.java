package coupletones.pro.cse110.coupletones.tests;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import coupletones.pro.cse110.coupletones.AddActivity;
import coupletones.pro.cse110.coupletones.HistoryActivity;
import coupletones.pro.cse110.coupletones.ParseClient;
import coupletones.pro.cse110.coupletones.PartnerListActivity;
import coupletones.pro.cse110.coupletones.R;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;

import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;

/**
 * Created by Koji Kameda on 6/3/2016.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CustomizeToneTest {
    ParseClient pctest;
    private Context context;
    HistoryActivity platest;

    @Rule
    public ActivityTestRule<HistoryActivity> hActivityRule
            = new ActivityTestRule<>(HistoryActivity.class);

    // Given that the user has added a partner
    // And the user may be notified with an arrival or departure notification

    @Test
    // Tests that HistoryActivity launches upon opening after partner is added
    public void testLaunchHistoryActivity() {

        // ActivityTestRule<HistoryActivity> hActivityRule
        //         = new ActivityTestRule<>(HistoryActivity.class);

        // logs which Intents have fired
        Intents.init();

        // open drawer
        onView(withId(R.id.drawer_settings_layout)).perform(actionOpenDrawer());


        // select partner settings
        onView(withText("Partner Settings")).perform(click());

        // replace partner ID
        onView(withId(R.id.add_et_input)).perform(replaceText("test1@test.com"));
        onView(withId(R.id.add_btn_chkid)).perform(click());

        // open drawer
        onView(withId(R.id.drawer_settings_layout)).perform(actionOpenDrawer());

        // select Partner's Fav Loc List
        onView(withText("Partner's Fav Loc List")).perform(click());

        // checks that PartnerListActivity is opened
        intended(hasComponent(AddActivity.class.getName()));

        // click list item
        onData(anything()).inAdapterView(withId(R.id.partnerlistView)).atPosition(0).perform(click());

        // click set arrival audible
        onView(withId(R.id.set_audible_arrival_tone)).perform(click());

        // click an audible tone
        onView(withId(R.id.audible_tone_1)).perform(click());

        // select ok
        onView(withId(android.R.id.button1)).perform(click());

        // set departure tone
        onView(withId(R.id.set_audible_departure_tone)).perform(click());
        onView(withId(R.id.audible_tone_1)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());

        // set arrival vibe
        onView(withId(R.id.set_vibe_arrival_tone)).perform(click());
        onView(withId(R.id.vibe_tone_1)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());

        // set departure vibe
        onView(withId(R.id.set_vibe_departure_tone)).perform(click());
        onView(withId(R.id.vibe_tone_1)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());

        // close window
        onView(withId(android.R.id.button1)).perform(click());

        // click next list item
        onData(anything()).inAdapterView(withId(R.id.partnerlistView)).atPosition(1).perform(click());

        // click set arrival audible
        onView(withId(R.id.set_audible_arrival_tone)).perform(click());

        // click an audible tone
        onView(withId(R.id.audible_tone_1)).perform(click());

        // select ok
        onView(withId(android.R.id.button1)).perform(click());

        // set departure tone
        onView(withId(R.id.set_audible_departure_tone)).perform(click());
        onView(withId(R.id.audible_tone_1)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());

        // set arrival vibe
        onView(withId(R.id.set_vibe_arrival_tone)).perform(click());
        onView(withId(R.id.vibe_tone_1)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());

        // set departure vibe
        onView(withId(R.id.set_vibe_departure_tone)).perform(click());
        onView(withId(R.id.vibe_tone_1)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());

        Intents.release();

    }


    // private methods to open and close drawers - provided by Valera Zakharov of Google
    // https://groups.google.com/forum/m/#!topic/android-test-kit-discuss/bmLQUlcI5U4

    private static ViewAction actionOpenDrawer() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(DrawerLayout.class);
            }

            @Override
            public String getDescription() {
                return "open drawer";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((DrawerLayout) view).openDrawer(GravityCompat.START);
            }
        };
    }
    private static ViewAction actionCloseDrawer() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(DrawerLayout.class);
            }

            @Override
            public String getDescription() {
                return "close drawer";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((DrawerLayout) view).closeDrawer(GravityCompat.START);
            }
        };
    }

}
