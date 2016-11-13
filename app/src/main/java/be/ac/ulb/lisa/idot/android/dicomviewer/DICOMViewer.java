package be.ac.ulb.lisa.idot.android.dicomviewer;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

public class DICOMViewer extends Activity
        implements DrawerFragment.NavigationDrawerCallbacks {
    public static final String WAS_INITIALIZED = "WAS_INITIALIZED";
    public static final String DRAWER_FRAGMENT = "DRAWER_FRAGMENT";

    // Fragment managing the behaviors, interactions and presentation of the navigation drawer
    private DrawerFragment mDrawerFragment;
    private DICOMFragment mDICOMFragment;
    // Used to store the last screen title. For use in {@link #restoreActionBar()}
    private CharSequence mTitle;
    private Boolean mInitialized;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dicomviewer);
        mInitialized = false;
        if (savedInstanceState != null)
            mInitialized = savedInstanceState.getBoolean(WAS_INITIALIZED);

        mDrawerFragment = (DrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        // If the saved instance state is not null get the file name
        // Get the intent
        String fileName = null;
        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            fileName = extras == null ? null : extras.getString("DICOMFileName");
        }
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        if (!mInitialized) {
            mDICOMFragment = DICOMFragment.newInstance(fileName);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, mDICOMFragment, DRAWER_FRAGMENT)
                    .commit();
        } else
            mDICOMFragment = (DICOMFragment) fragmentManager.findFragmentByTag(DRAWER_FRAGMENT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDICOMFragment = null;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mInitialized = savedInstanceState.getBoolean(WAS_INITIALIZED);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(WAS_INITIALIZED, true);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position,int valueCenter,int valueWidth) {
        if (mDICOMFragment == null)
            return;
        switch (position) {
            case 0: // Ruler
                if (mDICOMFragment.getTool() != DICOMFragment.Tool.RULER)
                    mDICOMFragment.setTool(DICOMFragment.Tool.RULER);
                else
                    mDICOMFragment.setTool(DICOMFragment.Tool.NONE);
                break;
            case 1: // Protractor
                if (mDICOMFragment.getTool() != DICOMFragment.Tool.PROTRACTOR)
                    mDICOMFragment.setTool(DICOMFragment.Tool.PROTRACTOR);
                else
                    mDICOMFragment.setTool(DICOMFragment.Tool.NONE);
                break;
            case 2: // Area
                if (mDICOMFragment.getTool() != DICOMFragment.Tool.AREA)
                    mDICOMFragment.setTool(DICOMFragment.Tool.AREA);
                else
                    mDICOMFragment.setTool(DICOMFragment.Tool.NONE);
                break;
            case 3: // Metadata
                int visibility = mDICOMFragment.getMetadataVisibility();
                if (visibility == View.VISIBLE)
                    mDICOMFragment.setMetadataVisibility(View.INVISIBLE);
                else
                    mDICOMFragment.setMetadataVisibility(View.VISIBLE);
                break;
            case 4://Annotations
                if (mDICOMFragment.getTool() != DICOMFragment.Tool.ANNOTATIONS)
                    mDICOMFragment.setTool(DICOMFragment.Tool.ANNOTATIONS);
                else
                    mDICOMFragment.setTool(DICOMFragment.Tool.NONE);
                break;
            case 5: // Presets
                if(valueCenter!=-1){
                    mDICOMFragment.setImageCenter(valueCenter,valueWidth);
                }
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
