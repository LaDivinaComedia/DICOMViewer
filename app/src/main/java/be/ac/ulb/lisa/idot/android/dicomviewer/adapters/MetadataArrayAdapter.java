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
public class MetadataArrayAdapter extends ArrayAdapter<Pair<String, String>> {
    protected Context mContext;
    protected List<Pair<String, String>> mValues = new ArrayList<>();

    public MetadataArrayAdapter(Context context, int resource) {
        super(context, resource);
        mContext = context;
    }

    public MetadataArrayAdapter(Context context, int resource, List<Pair<String, String>> objects) {
        super(context, resource, objects);
        mContext = context;
    }

    @Override
    public void add(Pair<String, String> object) {
        mValues.add(object);
    }

    @Override
    public void addAll(Collection<? extends Pair<String, String>> collection) {
        mValues.addAll(collection);
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
        View rowView = inflater.inflate(R.layout.metadata_item, parent, false);
        TextView textViewKey = (TextView) rowView.findViewById(R.id.metadata_tag_key);
        TextView textViewValue = (TextView) rowView.findViewById(R.id.metadata_tag_value);
        Pair<String, String> pair = mValues.get(position);
        textViewKey.setText(pair.first);
        textViewValue.setText(pair.second);
        return rowView;
    }
}
