package com.icebear.speechnote.itemadapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.icebear.speechnote.NoteConst;
import com.icebear.speechnote.R;

import java.util.ArrayList;
import java.util.List;

public class SpinerPiorityAdapter extends ArrayAdapter<String> {
    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<String> items;
    private int mResource;

    public SpinerPiorityAdapter(Context context, int resource, ArrayList objects) {
        super(context, resource, objects);

        mContext = context;
        mInflater = LayoutInflater.from(context);
        mResource = resource;
        items = objects;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent) {
        final View view = mInflater.inflate(mResource, parent, false);

        ImageView piotype = (ImageView) view.findViewById(R.id.pio_type);
        if (position == NoteConst.DEFAULT_CATE_AND_PIO){
            piotype.setVisibility(View.GONE);
        } else {
            piotype.setImageResource(R.drawable.category_important);
        }


        TextView cate = (TextView) view.findViewById(R.id.piority);

        cate.setText( items.get(position));
        

        return view;
    }
}
