/**
 * Select Adapter class
 *
 * @author Dongyang Yao
 *         Hua Deng
 *         Xi Zhang
 *         Lulu Zhao
 */

package com.example.dyyao.mapchat;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

public class SelectAdapter extends ArrayAdapter<Friend> {
    private final List<Friend> list;
    private final Activity context;

    /**
     * Constructor
     * @param context
     * @param list
     */
    public SelectAdapter(Activity context, List<Friend> list){
        super(context, R.layout.list_preview_row, list);
        this.context = context;
        this.list = list;
    }

    static class ViewHolder {
        protected TextView text;
        protected RadioButton select;
    }

    /**
     * Override the getView method
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
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
