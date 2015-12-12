package com.example.dyyao.mapchat;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.graphics.Color.WHITE;

public class myFriendAdapter extends ArrayAdapter<myFriend> {

    private final List<myFriend> list;
    private final Activity context;

    public myFriendAdapter(Activity context, List<myFriend> list){
        super(context, R.layout.list_row, list);
        this.context = context;
        this.list = list;
    }

    static class ViewHolder {
        protected ImageView icon;
        protected TextView text;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder viewHolder;
        LayoutInflater inflator = context.getLayoutInflater();
        v = inflator.inflate(R.layout.list_row, null);
        v.setBackgroundColor(Color.WHITE);
        viewHolder = new ViewHolder();
        viewHolder.text = (TextView) v.findViewById(R.id.label);
        viewHolder.text.setText(list.get(position).getName());
        if(list.get(position).isSelected()){
            v.setBackgroundResource(R.color.colorPrimaryDark);
        }
        viewHolder.icon = (ImageView) v.findViewById(R.id.icon);
        viewHolder.icon.setImageResource(list.get(position).getImage());

        v.setTag(viewHolder);

        return v;
    }

}
