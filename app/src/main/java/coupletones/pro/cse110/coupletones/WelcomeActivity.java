package coupletones.pro.cse110.coupletones;

import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;
import android.animation.ArgbEvaluator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * The WelcomeActivity displays the tutorial to the user at the first run and when the help option
 * is selected in the settings sidebar
 */

public class WelcomeActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private int page = 0;
    private ImageButton nextBtn;
    private Button skipBtn, finishBtn;
    private ImageView[] indicators;
    private ImageView in0, in1, in2, in3, in4, in5;
    private int numScreens = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //Set images and buttons
        nextBtn = (ImageButton) findViewById(R.id.intro_btn_next);
        skipBtn = (Button) findViewById(R.id.intro_btn_skip);
        finishBtn = (Button) findViewById(R.id.intro_btn_finish);
        in0 = (ImageView) findViewById(R.id.intro_indicator_0);
        in1 = (ImageView) findViewById(R.id.intro_indicator_1);
        in2 = (ImageView) findViewById(R.id.intro_indicator_2);
        in3 = (ImageView) findViewById(R.id.intro_indicator_3);
        in4 = (ImageView) findViewById(R.id.intro_indicator_4);
        in5 = (ImageView) findViewById(R.id.intro_indicator_5);
        indicators = new ImageView[] {in0, in1, in2, in3, in4, in5};

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(page);
        updateIndicators(page);

        final int color1 = Color.parseColor("#FF8A65");
        final int color2 = Color.parseColor("#AED581");
        final int color3 = Color.parseColor("#B0BEC5");
        final int color4 = Color.parseColor("#80CBC4");
        final int color5 = Color.parseColor("#7986CB");
        final int color6 = Color.parseColor("#9CCC65");

        final int[] colors = new int[]{color1, color2, color3, color4, color5, color6};
        final ArgbEvaluator eval = new ArgbEvaluator();

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int update = (Integer) eval.evaluate(positionOffset, colors[position],
                        colors[position == (numScreens - 1)? position : position + 1]);
                mViewPager.setBackgroundColor(update);
            }

            @Override
            public void onPageSelected(int position) {
                page = position;
                updateIndicators(page);

                //Update the color when each page is selected
                switch (position) {
                    case 0:
                        mViewPager.setBackgroundColor(color1);
                        break;
                    case 1:
                        mViewPager.setBackgroundColor(color2);
                        break;
                    case 2:
                        mViewPager.setBackgroundColor(color3);
                        break;
                    case 3:
                        mViewPager.setBackgroundColor(color4);
                        break;
                    case 4:
                        mViewPager.setBackgroundColor(color5);
                        break;
                    case 5:
                        mViewPager.setBackgroundColor(color6);
                        break;
                }

                //update the bottom page indicators
                nextBtn.setVisibility(position == numScreens - 1 ? View.GONE : View.VISIBLE);
                finishBtn.setVisibility(position == numScreens - 1 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        nextBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                page++;
                mViewPager.setCurrentItem(page, true);
            }
        });

        finishBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
            }
        });

        skipBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
            }
        });

    }

    /**
     * Update the bottom images to indicate the current page
     * @param position - the page currently viewed
     */
    void updateIndicators(int position) {
        for (int i = 0; i < indicators.length; i++) {
            indicators[i].setBackgroundResource(
                    i == position ? R.drawable.indicator_selected : R.drawable.indicator_unselected
            );
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        int[] titles = new int[] {R.string.tutorial_0_title, R.string.tutorial_1_title,
                R.string.tutorial_2_title, R.string.tutorial_3_title, R.string.tutorial_4_title,
                R.string.tutorial_5_title};
        int[] descriptions = new int[] {R.string.tutorial_0_desc,
                R.string.tutorial_1_desc,
                R.string.tutorial_2_desc,
                R.string.tutorial_3_desc,
                R.string.tutorial_4_desc,
                R.string.tutorial_5_desc};
        int[] images = new int[] {R.drawable.ic_people_outline_black_24dp,
                R.drawable.ic_person_add_black_24dp, R.drawable.ic_add_location_black_24dp,
                R.drawable.ic_mode_edit_black_24dp, R.drawable.ic_directions_walk_black_24dp,
                R.drawable.ic_format_list_bulleted_black_24dp};

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        /**
         * Set the image, title, and description appropriate when each page is loaded
         * @param inflater
         * @param container
         * @param savedInstanceState
         * @return
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_welcome, container, false);

            //Set the appropriate title
            TextView titleView = (TextView) rootView.findViewById(R.id.section_title);
            titleView.setText(titles[getArguments().getInt(ARG_SECTION_NUMBER) - 1]);

            //Set the appropriate description
            TextView descView = (TextView) rootView.findViewById(R.id.section_desc);
            descView.setText(descriptions[getArguments().getInt(ARG_SECTION_NUMBER) - 1]);

            //Set the appropriate image
            ImageView img = (ImageView) rootView.findViewById(R.id.section_img);
            img.setBackgroundResource(images[getArguments().getInt(ARG_SECTION_NUMBER) - 1]);
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 6 total pages.
            return numScreens;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }
    }
}
