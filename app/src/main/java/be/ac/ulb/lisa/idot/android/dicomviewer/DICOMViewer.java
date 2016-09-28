package be.ac.ulb.lisa.idot.android.dicomviewer;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;

public class DICOMViewer extends Activity
        implements DrawerFragment.NavigationDrawerCallbacks,
        DICOMFragment.OnFragmentInteractionListener {
    private static final String FILE_NAME = "FILE_NAME";
    // Fragment managing the behaviors, interactions and presentation of the navigation drawer
    private DrawerFragment mDrawerFragment;
    // Used to store the last screen title. For use in {@link #restoreActionBar()}
    private CharSequence mTitle;
    private String mFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dicomviewer);

        mDrawerFragment = (DrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        // If the saved instance state is not null get the file name
        mFileName = null;
        if (savedInstanceState != null) {
            mFileName = savedInstanceState.getString(FILE_NAME);
        } else {
            // Get the intent
            Intent intent = getIntent();
            if (intent != null) {
                Bundle extras = intent.getExtras();
                mFileName = extras == null ? null : extras.getString("DICOMFileName");
            }
        }
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, DICOMFragment.newInstance(mFileName))
                .commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mFileName != null)
            outState.putString(FILE_NAME, mFileName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // TODO: provide interaction
        // update the main content by replacing fragments
//        FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
//                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.drawer_metadata);
                break;
            case 2:
                mTitle = getString(R.string.drawer_settings);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

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

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_dicomviewer, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((DICOMViewer) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
