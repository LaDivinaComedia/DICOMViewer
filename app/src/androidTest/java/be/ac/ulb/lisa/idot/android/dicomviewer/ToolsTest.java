package be.ac.ulb.lisa.idot.android.dicomviewer;

import android.content.Intent;
import android.graphics.PointF;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.ListView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import be.ac.ulb.lisa.idot.android.dicomviewer.view.AreaView;
import be.ac.ulb.lisa.idot.android.dicomviewer.view.Calculus;
import be.ac.ulb.lisa.idot.android.dicomviewer.view.ProtractorView;
import be.ac.ulb.lisa.idot.android.dicomviewer.view.RulerView;
import be.ac.ulb.lisa.idot.android.dicomviewer.view.ToolViewTest;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.*;

/**
 * @author Vladyslav Vasyliev
 *         Created on 14.11.16.
 */
@RunWith(AndroidJUnit4.class)
public class ToolsTest {
    Intent mIntent;

    @Rule
    public ActivityTestRule<DICOMViewer> activityRule = new ActivityTestRule<>(DICOMViewer.class);

    @Before
    public void setUp() {
        mIntent = new Intent();
        mIntent.putExtra("DICOMFileName", "/storage/emulated/0/DICOMs/IM-0001-0004.dcm");
    }

    @Test
    public void checkRuler() throws Exception {
        DICOMViewer activity;
        RulerView rulerView;
        float scale;
        float[] ps;
        activity = activityRule.launchActivity(mIntent);
        rulerView = (RulerView) activity.findViewById(R.id.ruler_view);
        // check whether the tool is visible
        ToolViewTest.selectTool("Ruler");
        assertEquals(View.VISIBLE, activity.findViewById(R.id.ruler_view).getVisibility());
        // check if the tool calculates the distance correctly
        ps = rulerView.getPixelSpacing();
        scale = rulerView.getScaleFactor();
        onView(withId(R.id.ruler_view)).perform(ToolViewTest.clickXY(350, 678));
        onView(withId(R.id.ruler_view)).perform(ToolViewTest.clickXY(800, 400));
        assertEquals(Math.pow(Math.pow((800 - 350) * ps[0], 2)
                        + Math.pow((400 - 678) * ps[1], 2), 0.5) / scale,
                rulerView.getDistance(), 0.001);
    }

    @Test
    public void checkArea() {
        DICOMViewer activity;
        AreaView areaView;
        float scale;
        float[] ps;
        activity = activityRule.launchActivity(mIntent);
        areaView = (AreaView) activity.findViewById(R.id.area_view);
        // check whether the tool is visible
        ToolViewTest.selectTool("Area");
        assertEquals(View.VISIBLE, activity.findViewById(R.id.area_view).getVisibility());
        //
        ps = areaView.getPixelSpacing();
        scale = areaView.getScaleFactor();
        // how to do that???
    }

    @Test
    public void checkProtractor() {
        DICOMViewer activity;
        ProtractorView protractorView;
        float[] ps;
        activity = activityRule.launchActivity(mIntent);
        protractorView = (ProtractorView) activity.findViewById(R.id.protractor_view);
        // check whether the tool is visible
        ToolViewTest.selectTool("Protractor");
        assertEquals(View.VISIBLE, activity.findViewById(R.id.protractor_view).getVisibility());
        // verify the value of the angle
        onView(withId(R.id.protractor_view)).perform(ToolViewTest.clickXY(300, 300));
        onView(withId(R.id.protractor_view)).perform(ToolViewTest.clickXY(800, 400));
        onView(withId(R.id.protractor_view)).perform(ToolViewTest.clickXY(400, 400));
        ps = protractorView.getPixelSpacing();
        assertEquals(Calculus.getRealAngle(new PointF[]{
                        new PointF(300, 300),
                        new PointF(800, 400),
                        new PointF(400, 400)}, ps[0], ps[1]),
                protractorView.getAngle(), 0.001);
    }

    @Test
    public void checkMetadata() {
        DICOMViewer activity;
        ListView listView;
        float scale;
        float[] ps;
        activity = activityRule.launchActivity(mIntent);
        listView = (ListView) activity.findViewById(R.id.list_metadata);
        // check whether the tool is visible
        ToolViewTest.selectTool("Metadata");
        assertEquals(View.VISIBLE, activity.findViewById(R.id.list_metadata).getVisibility());
        onView(allOf(withText("BRAINIX"), hasSibling(withText("Name")))).check(matches(isDisplayed()));
    }

}