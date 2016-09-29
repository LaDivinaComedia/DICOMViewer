package be.ac.ulb.lisa.idot.android.dicomviewer;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DICOMViewer extends Activity
        implements DrawerFragment.NavigationDrawerCallbacks {
    private static final String FILE_NAME = "FILE_NAME";
    private static final String META_VISIBILITY = "META_VISIBILITY";

    // Fragment managing the behaviors, interactions and presentation of the navigation drawer
    private DrawerFragment mDrawerFragment;
    private DICOMFragment mDICOMFragment;
    // Used to store the last screen title. For use in {@link #restoreActionBar()}
    private CharSequence mTitle;

    private String mFileName;
    private int mMetadataVisibility;


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
        if (savedInstanceState != null) {
            mFileName = savedInstanceState.getString(FILE_NAME);
            mMetadataVisibility = savedInstanceState.getInt(META_VISIBILITY);
        } else {
            // Get the intent
            Intent intent = getIntent();
            if (intent != null) {
                Bundle extras = intent.getExtras();
                mFileName = extras == null ? null : extras.getString("DICOMFileName");
            }
        }
        // update the main content by replacing fragments
        mDICOMFragment = DICOMFragment.newInstance(mFileName, mMetadataVisibility);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, mDICOMFragment)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mFileName != null)
            outState.putString(FILE_NAME, mFileName);
        outState.putInt(META_VISIBILITY, mDICOMFragment.getMetadataVisibility());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mFileName = savedInstanceState.getString(FILE_NAME);
            mMetadataVisibility = savedInstanceState.getInt(META_VISIBILITY);
            mDICOMFragment.setMetadataVisibility(mMetadataVisibility);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDICOMFragment = null;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (mDICOMFragment == null)
            return;
        switch (position) {
            case 0: // Ruler
                break;
            case 1: // Protractor
                break;
            case 2: // Area
                break;
            case 3: // Metadata
                int visibility = mDICOMFragment.getMetadataVisibility();
                if (visibility == View.VISIBLE)
                    mDICOMFragment.setMetadataVisibility(View.INVISIBLE);
                else
                    mDICOMFragment.setMetadataVisibility(View.VISIBLE);
                break;
            case 4: // Settings
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

}
