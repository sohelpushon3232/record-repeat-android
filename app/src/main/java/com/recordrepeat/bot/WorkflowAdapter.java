package com.recordrepeat.bot;

import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;


public class WorkflowAdapter extends ArrayAdapter<String> {


    public WorkflowAdapter(
            Context context,
            String[] names
    ){

        super(
                context,
                android.R.layout.simple_list_item_1,
                names
        );

    }


    @Override
    public View getView(
            int position,
            View convertView,
            ViewGroup parent
    ){

        View view =
                super.getView(
                        position,
                        convertView,
                        parent
                );


        TextView text =
                view.findViewById(
                        android.R.id.text1
                );


        text.setText(
                getItem(position)
        );


        return view;

    }

}
