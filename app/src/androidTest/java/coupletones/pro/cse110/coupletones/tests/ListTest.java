package coupletones.pro.cse110.coupletones.tests;

import android.content.Intent;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;

import coupletones.pro.cse110.coupletones.AddActivity;
import coupletones.pro.cse110.coupletones.HistoryActivity;
import coupletones.pro.cse110.coupletones.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
/**
 * Created by Koji Kameda on 6/3/2016.
 */
public class ListTest {

    @Rule
    public ActivityTestRule<HistoryActivity> hActivityRule
            = new ActivityTestRule<>(HistoryActivity.class);

    // Partner ID: test1@test.com
    @Test
    // Scenario 1: Given that the user has added a partner,
    // and the partner visits/has visited one or more favorite locations
    public void testLaunchHistoryActivity() {

        // logs which Intents have fired
        Intents.init();
        hActivityRule.launchActivity(new Intent());

        // checks that HistoryActivity is opened
        intended(hasComponent(HistoryActivity.class.getName()));
        Intents.release();
    }

    @Test
    // PartnerID: kojikameda23@gmail.com
    // Scenario 2: Given that the user has added a partner
    // And the partner has not visited any of his/her favorite location
    public void testNoFavLocs() {
        // logs which Intents have fired
        Intents.init();
        //hActivityRule.launchActivity(new Intent());

        // open drawer
        onView(withId(R.id.drawer_settings_layout)).perform(actionOpenDrawer());

        // checks that HistoryActivity is opened
        //intended(hasComponent(HistoryActivity.class.getName()));

        // select partner settings
        onView(withText("Partner Settings")).perform(click());

        // replace partner ID
        onView(withId(R.id.add_et_input)).perform(replaceText("kojikameda23@gmail.com"));
        onView(withId(R.id.add_btn_chkid)).perform(click());

        intended(hasComponent(AddActivity.class.getName()));
        Intents.release();
    }

    @Test
    // PartnerID: none
    // Scenario 3: Given that the user does not have a partner added,
    // When the user opens the application
    public void testNoPartner() {

        // logs which Intents have fired
        Intents.init();
        //hActivityRule.launchActivity(new Intent());

        // open drawer
        onView(withId(R.id.drawer_settings_layout)).perform(actionOpenDrawer());

        // select partner settings
        onView(withText("Partner Settings")).perform(click());

        // replace partner ID
        onView(withId(R.id.add_et_input)).perform(clearText());
        onView(withId(R.id.add_btn_chkid)).perform(click());

        intended(hasComponent(AddActivity.class.getName()));
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
