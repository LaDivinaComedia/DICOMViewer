package be.ac.ulb.lisa.idot.android.dicomviewer.view;

import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.espresso.action.Tap;
import android.view.View;

import be.ac.ulb.lisa.idot.android.dicomviewer.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * @author Vladyslav Vasyliev
 *         Created on 14.11.16.
 */

public class ToolViewTest {
    private ToolViewTest() {}

    public static ViewInteraction selectTool(String toolName) {
        // open navigation drawer
        onView(withId(R.id.drawer_layout)).perform(new GeneralSwipeAction(Swipe.FAST,
                new CoordinatesProvider() {
                    @Override
                    public float[] calculateCoordinates(View view) {
                        float xy[] = new float[2];
                        xy[0] += 0;
                        xy[1] += view.getHeight() / 2;
                        return xy;
                    }
                },
                GeneralLocation.CENTER_RIGHT, Press.FINGER));
        // choose ruler tool
        return onView(withText(toolName)).perform(click());
    }

    public static GeneralClickAction clickXY(final float x, final float y) {
        return new GeneralClickAction(Tap.SINGLE,
                new CoordinatesProvider() {
                    @Override
                    public float[] calculateCoordinates(View view) {
                        return new float[] { x, y };
                    }
                }, Press.THUMB);
    }

}
