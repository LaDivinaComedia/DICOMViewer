package be.ac.ulb.lisa.idot.android.dicomviewer.adapters;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import be.ac.ulb.lisa.idot.android.dicomviewer.R;

/**
 * @author Vladyslav Vasyliev
 *         Created on 21.09.2016
 */
public class PairArrayAdapter extends ArrayAdapter<Pair<String, String>> {
    protected Context mContext;
    protected int mMainTextId;
    protected int mSubTextId;
    protected int mLayoutId;
    protected List<Pair<String, String>> mValues = new ArrayList<>();

    public PairArrayAdapter(Context context, int resource, int mainTextId, int subTextId) {
        super(context, resource);
        mContext = context;
        mMainTextId = mainTextId;
        mSubTextId = subTextId;
        mLayoutId = resource;
    }

    public PairArrayAdapter(Context context, int resource, List<Pair<String, String>> objects,
                            int mainTextId, int subTextId) {
        super(context, resource, objects);
        mContext = context;
        mValues.addAll(objects);
        mMainTextId = mainTextId;
        mSubTextId = subTextId;
        mLayoutId = resource;
    }

    @Override
    public void add(Pair<String, String> object) {
        mValues.add(object);
        notifyDataSetChanged();
    }

    @Override
    public void addAll(Collection<? extends Pair<String, String>> collection) {
        mValues.addAll(collection);
        notifyDataSetChanged();
    }

    @Override
    public void insert(Pair<String, String> object, int index) {
        mValues.add(index, object);
    }

    @Override
    public void remove(Pair<String, String> object) {
        mValues.remove(object);
    }

    @Override
    public int getCount() {
        return mValues.size();
    }

    @Override
    public void clear() {
        mValues.clear();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(mLayoutId, parent, false);
        TextView subTextView = (TextView) rowView.findViewById(mSubTextId);
        TextView mainTextView = (TextView) rowView.findViewById(mMainTextId);
        Pair<String, String> pair = mValues.get(position);
        subTextView.setText(pair.first);
        mainTextView.setText(pair.second);
        return rowView;
    }
}
