package be.ac.ulb.lisa.idot.android.dicomviewer.adapters;

import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import be.ac.ulb.lisa.idot.android.dicomviewer.R;

import static org.junit.Assert.assertEquals;

/**
 * @author Vladyslav Vasyliev
 *         Created on 13.11.2016
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class PairArrayAdapterTest {
    private Instrumentation mInstrumentation;

    private PairArrayAdapter mAdapter;
    private List<Pair<String, String>> mPairList;

    @Before
    public void setUp() throws Exception {
        mInstrumentation = InstrumentationRegistry.getInstrumentation();
        mAdapter = new PairArrayAdapter(mInstrumentation.getTargetContext(),
                R.layout.metadata_item,
                R.id.metadata_tag_value,
                R.id.metadata_tag_key);
        mPairList = new ArrayList<>();
        mPairList.add(new Pair<>("First 1", "Second 1"));
        mPairList.add(new Pair<>("First 2", "Second 2"));
        mPairList.add(new Pair<>("First 3", "Second 3"));
        mPairList.add(new Pair<>("First 4", "Second 4"));
        mPairList.add(new Pair<>("First 5", "Second 5"));
        mAdapter.addAll(mPairList);
    }

    @Test
    public void listOperations() throws Exception {
        assertEquals(mPairList.size(), mAdapter.getCount());
        mAdapter.clear();
        assertEquals(0, mAdapter.getCount());
    }

    @Test
    public void getView() throws Exception {
        View rowView;
        TextView subTextView;
        TextView mainTextView;
        int index = 0;
        for (Pair<String, String> p: mPairList) {
            rowView = mAdapter.getView(index++, null, null);
            mainTextView = (TextView) rowView.findViewById(R.id.metadata_tag_key);
            subTextView = (TextView) rowView.findViewById(R.id.metadata_tag_value);
            assertEquals(p.first, mainTextView.getText());
            assertEquals(p.second, subTextView.getText());
        }
    }

}