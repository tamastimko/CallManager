package com.example.tamas.callmanager;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Tamas on 2015.02.28..
 */

public class SettingsAdapter extends BaseAdapter{

    private List<Beallit> items;

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
           Beallit beallit = items.get(i);
           if(view == null)
           {
               view = View.inflate(viewGroup.getContext(),R.layout.settings_list, null);
           }
        TextView titleTextView = (TextView) view.findViewById(R.id.title);
            titleTextView.setText(beallit.getTitle());

        TextView leadTextView = (TextView) view.findViewById(R.id.lead);

        return view;

    }
}
