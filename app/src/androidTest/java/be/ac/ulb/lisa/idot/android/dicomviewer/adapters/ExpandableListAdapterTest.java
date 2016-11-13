package be.ac.ulb.lisa.idot.android.dicomviewer.adapters;

import android.app.Instrumentation;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import be.ac.ulb.lisa.idot.android.dicomviewer.DICOMViewer;
import be.ac.ulb.lisa.idot.android.dicomviewer.R;
import be.ac.ulb.lisa.idot.android.dicomviewer.adapters.ExpandableListAdapter;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author Vladyslav Vasyliev
 *         Created on 10.11.2016
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ExpandableListAdapterTest {
    private Instrumentation mInstrumentation;

    private ExpandableListAdapter mAdapter;
    private HashMap<String, List<String>> mListDataChild;
    private List<String> mListDataHeader;

    @Before
    public void setUp() {
        mInstrumentation = InstrumentationRegistry.getInstrumentation();
        prepareData(mInstrumentation.getTargetContext().getResources());
    }

    private void prepareData(Resources resources) {
        String[] headers = resources.getStringArray(R.array.drawer_items);
        String[] child = resources.getStringArray(R.array.child_items);
        mListDataHeader = new ArrayList<>();
        Collections.addAll(mListDataHeader, headers);
        List<String> children = Arrays.asList(child);
        List<String> empty = new ArrayList<>();
        mListDataChild = new HashMap<>();
        for (int i = 0; i < mListDataHeader.size(); i++) {
            if (mListDataHeader.get(i).toLowerCase().equals("presets"))
                mListDataChild.put(mListDataHeader.get(i), children);
            else
                mListDataChild.put(mListDataHeader.get(i), empty);
        }
        mAdapter = new ExpandableListAdapter(
                mInstrumentation.getTargetContext(),
                mListDataHeader,
                mListDataChild);
    }

    @Test
    public void getGroup() {
        for (int i = 0; i < mListDataHeader.size(); ++i)
            assertEquals(mListDataHeader.get(i), mAdapter.getGroup(i));
    }

    @Test
    public void isChildSelectable() {
        for (int i = 0; i < mListDataHeader.size(); ++i)
            assertEquals(mListDataChild.get(mListDataHeader.get(i)).size() > 0,
                    mAdapter.isChildSelectable(i, 0));
    }

    @Test
    public void getGroupView() {
        View groupView;
        ImageView imageView;
        TextView textView;
        for (int i = 0; i < mListDataHeader.size(); ++i) {
            groupView = mAdapter.getGroupView(i, false, null, null);
            imageView = (ImageView) groupView.findViewById(R.id.expandableListIndicator);
            textView = (TextView) groupView.findViewById(R.id.lblListHeader);
            assertNotNull(imageView);
            assertNotNull(textView);
            assertEquals(mListDataHeader.get(i), textView.getText());
        }
    }

    @Test
    public void getChildView(){
        View childView;
        TextView textView;
        String string;
        List<String> stringList;
        for (int i = 0; i < mListDataHeader.size(); ++i) {
            string = mListDataHeader.get(i);
            stringList = mListDataChild.get(string);
            for (int j = 0; j < stringList.size(); ++j) {
                childView = mAdapter.getChildView(i, j, false, null, null);
                textView = (TextView) childView.findViewById(R.id.lblListItem);
                assertEquals(stringList.get(j), textView.getText());
            }
        }
    }
}