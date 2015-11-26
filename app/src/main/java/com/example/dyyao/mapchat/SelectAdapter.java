package com.example.dyyao.mapchat;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by mercypp on 11/25/15.
 */
public class SelectAdapter extends ArrayAdapter<myFriend> {
    private final List<myFriend> list;
    private final Activity context;

    public SelectAdapter(Activity context, List<myFriend> list){
        super(context, R.layout.list_preview_row, list);
        this.context = context;
        this.list = list;
    }
    static class ViewHolder {
        protected TextView text;
        protected RadioButton select;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view ;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.list_preview_row, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) view.findViewById(R.id.labelText);
            viewHolder.text.setText(list.get(position).getName());
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }
        return view;
    }
}
