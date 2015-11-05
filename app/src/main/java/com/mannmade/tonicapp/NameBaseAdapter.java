package com.mannmade.tonicapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by Mannb3ast on 11/4/2015.
 */
public class NameBaseAdapter extends BaseAdapter {
    ArrayList<LinkedHashMap<String, String>> listPassed;
    Context context;

    //had to make sure I grabbed the context of the activity in the overloaded constructor
    NameBaseAdapter(Context context, ArrayList<LinkedHashMap<String, String>> listPassed){
        this.listPassed = listPassed;
        this.context = context;
    }

    static class ViewHolder{
        TextView firstNameView;
        TextView lastNameView;
    }

    @Override
    public int getCount() {
        return listPassed.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //ViewHolder Pattern Baby
        ViewHolder viewHolder;
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.name_list_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.firstNameView = (TextView) convertView.findViewById(R.id.first_name_item);
            viewHolder.lastNameView = (TextView) convertView.findViewById(R.id.last_name_item);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //only did check for null as a value (Could have been more thorough if I wanted
        if( (listPassed.get(position).get("first_name").equalsIgnoreCase("null")) && (listPassed.get(position).get("last_name").equalsIgnoreCase("null")) ){
            viewHolder.firstNameView.setVisibility(View.GONE);
            viewHolder.lastNameView.setVisibility(View.GONE);
        }else if(listPassed.get(position).get("first_name").equalsIgnoreCase("null")){
            //Show only last name
            viewHolder.firstNameView.setVisibility(View.GONE);
            viewHolder.lastNameView.setText(listPassed.get(position).get("last_name"));
        }else if(listPassed.get(position).get("last_name").equalsIgnoreCase("null")) {
            //Show only first name
            viewHolder.firstNameView.setText(listPassed.get(position).get("first_name"));
            viewHolder.lastNameView.setVisibility(View.GONE);
        }else{
            viewHolder.firstNameView.setText(listPassed.get(position).get("first_name"));
            viewHolder.lastNameView.setText(listPassed.get(position).get("last_name"));
        }

        return convertView;
    }
}
