package com.icebear.speechnote.itemadapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.icebear.speechnote.R;
import com.icebear.speechnote.model.Category;

import java.util.ArrayList;

public class SpinerCategoryAdapter extends ArrayAdapter<String> {
    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<Category> items;
    private int mResource;

    public SpinerCategoryAdapter(Context context, int resource,ArrayList objects) {
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
    private View createItemView(int position, View convertView, ViewGroup parent){
        final View view = mInflater.inflate(mResource, parent, false);
        TextView cate = (TextView) view.findViewById(R.id.cate);
        ImageView notebook = (ImageView) view.findViewById(R.id.notebook);
        if (position == 0){
            notebook.setVisibility(View.GONE);
        } else {
            notebook.setVisibility(View.VISIBLE);
        }

        Category category = items.get(position);
        cate.setText(category.getCategory());

        return view;
    }

    public int getCategoryPosition(int categoryid){
        int position = 0;

        for (int i = 1; i < items.size(); i++){
            if (categoryid == items.get(i).getId()){
                position = i;
            }
        }

        return position;
    }
}
