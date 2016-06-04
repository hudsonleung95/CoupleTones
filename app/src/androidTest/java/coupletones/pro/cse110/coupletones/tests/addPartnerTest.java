package coupletones.pro.cse110.coupletones.tests;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import coupletones.pro.cse110.coupletones.AddActivity;

/**
 * Created by Andy on 6/3/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class addPartnerTest {
    @Rule
    public ActivityTestRule<AddActivity> addActivityRule = new ActivityTestRule<>(AddActivity.class);

    @Test
    public void test_addPartner() {
//        AddActivity addActivity = addActivityRule.launchActivity(new Intent());
//        onView(withId(R.id.add_et_selfid)).perform(typeText("andylin@ucsd.edu"), closeSoftKeyboard());
//        onView(withId(R.id.add_et_input)).perform(typeText("test1@test.com"), closeSoftKeyboard());
//        onView(withId(R.id.add_btn_chkid)).perform(click());
//        //DataStorage dataStorage = addActivity.getDS();
//        //onData(allOf(is(instanceOf(List.class)), hasItem("LAX  ")))
//        onData(hasToString(startsWith("LAX"))).check(matches(isDisplayed()));
//        /*ArrayList<LatLng> historyList;
//        historyList = dataStorage.getPartnerLatLngList();
//        Log.d("HELLO", String.valueOf(historyList.size()));
//        for (int i = 0; i < historyList.size(); i++) {
//            Log.d("LIST", historyList.get(i).toString());
//        }*/



    }
    /*AddActivity addActivity;
    GoogleMap gMap;
    String TAG;
    DataStorage dataStorage;
    //flag to wait for thread to finish
    boolean changedFlag = false;

    public addPartnerTest() {
        super(AddActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        TAG = "setUp";
        Log.d(TAG, "STARTING setUp!!!!!!!!!!!!!!!!!");
        super.setUp();
        addActivity = getActivity();
        //gMap = addActivity.getMap();
        changedFlag = false;
    }

    public void test_addPartner() {
        TAG = "test_addPartner";
        Log.d(TAG, "STARTING viewPartnerHistory!!!!!!!!!!!!!!!!!");
        addActivity.setSelfId("andylin@ucsd.edu");
        addActivity.setPartnerId("test1@test.com");
        Button btn = (Button)addActivity.findViewById(R.id.add_btn_chkid);
        addActivity.addPartner(btn);
        dataStorage = addActivity.getDS();
        assertEquals(dataStorage.getPartnerId(), "test1@test.com");
    }
    */


}
