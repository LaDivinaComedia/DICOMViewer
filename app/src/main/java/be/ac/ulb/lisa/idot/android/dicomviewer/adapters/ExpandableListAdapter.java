package be.ac.ulb.lisa.idot.android.dicomviewer.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import be.ac.ulb.lisa.idot.android.dicomviewer.R;

/**
 * This is expandable list view which
 * Created by Victor Vanichkov on 18.10.2016.
 * @author Victor Vanichkov
 * @author Vladyslav Vasyliev
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private List<String> mListDataHeader;   // header titles
    // child data in format of <header title, child titles>
    private HashMap<String, List<String>> mListDataChild;
    private Drawable mDrawableCollapsed;    // indicator for the collapsed group item
    private Drawable mDrawableExpanded;     // indicator for the expanded group item

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData) {
        mContext = context;
        mListDataHeader = listDataHeader;
        mListDataChild = listChildData;
        // load group indicators from the resources
        Resources resources = context.getResources();
        mDrawableCollapsed = resources.getDrawable(R.drawable.expander_ic_minimized);
        mDrawableExpanded = resources.getDrawable(R.drawable.expander_ic_maximized);
        // throw an exception if one of the required resources was not loaded
        if (mDrawableCollapsed == null)
            throw new NullPointerException("Drawable 'expander_ic_minimized' was not found!");
        if (mDrawableExpanded == null)
            throw new NullPointerException("Drawable 'expander_ic_maximized' was not found!");
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mListDataChild.get(mListDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);
        }
        TextView txtListChild = (TextView) convertView.findViewById(R.id.lblListItem);
        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mListDataChild.get(mListDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mListDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return mListDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        // inflate group item
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, null);
        }
        // try to get indicator from the inflated view
        View indicator = convertView.findViewById(R.id.expandableListIndicator);
        if (indicator != null) {
            // get image itself
            ImageView indicatorImage = (ImageView) indicator;
            // if there is no child in parent group item then hide indicator image
            if (getChildrenCount(groupPosition) == 0) {
                indicatorImage.setVisibility(View.INVISIBLE);
            } else {
                // otherwise show indicator depending on its current state
                if (isExpanded)
                    indicatorImage.setImageDrawable(mDrawableExpanded);
                else
                    indicatorImage.setImageDrawable(mDrawableCollapsed);
                indicatorImage.setVisibility(View.VISIBLE);
            }
        }
        // set header text according to the position of the group header
        TextView textView = (TextView) convertView.findViewById(R.id.lblListHeader);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setText(headerTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return getChildrenCount(groupPosition) > 0;
    }
}
