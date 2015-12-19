/**
 * Friend list adapter
 *
 * @author Dongyang Yao
 *         Hua Deng
 *         Xi Zhang
 *         Lulu Zhao
 */

package com.example.dyyao.mapchat;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class FriendAdapter extends ArrayAdapter<Friend> {

    // Friend list
    private final List<Friend> list;
    private final Activity context;

    /**
     * Constructor
     * @param context
     * @param list
     */
    public FriendAdapter(Activity context, List<Friend> list) {
        super(context, R.layout.list_row, list);
        this.context = context;
        this.list = list;
    }

    static class ViewHolder {
        protected ImageView icon;
        protected TextView text;
    }

    /**
     * Return the view for friend list
     * @param position
     * @param v
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder viewHolder;
        LayoutInflater inflator = context.getLayoutInflater();
        v = inflator.inflate(R.layout.list_row, null);
        v.setBackgroundColor(Color.WHITE);
        viewHolder = new ViewHolder();
        viewHolder.text = (TextView) v.findViewById(R.id.label);
        viewHolder.text.setText(list.get(position).getName());
        if (list.get(position).isSelected()) {
            v.setBackgroundResource(R.color.colorPrimaryDark);
        }
        viewHolder.icon = (ImageView) v.findViewById(R.id.icon);
        viewHolder.icon.setImageResource(list.get(position).getImage());

        v.setTag(viewHolder);

        return v;
    }

}
