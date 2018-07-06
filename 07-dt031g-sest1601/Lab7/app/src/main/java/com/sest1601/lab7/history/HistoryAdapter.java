package com.sest1601.lab7.history;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sest1601.lab7.R;
import com.sest1601.lab7.database.HistoryEntity;
import java.util.List;

public class HistoryAdapter extends ArrayAdapter<HistoryEntity> {
    public HistoryAdapter(Context context, List<HistoryEntity> historydata) {
        super(context, 0, historydata);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        HistoryEntity entity = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.historyitem, parent, false);
        }
        // Lookup view for data population
        TextView listNumber = convertView.findViewById(R.id.listNumber);
        TextView listDate = convertView.findViewById(R.id.listDate);
        TextView listCoordinates = convertView.findViewById(R.id.listCoordinates);
        // Populate the data into the template view using the data object
//
        listNumber.setText(entity.getNumber());
        listDate.setText(entity.getDate());
        listCoordinates.setText(String.valueOf(entity.getLat()) + ", " + String.valueOf(entity.getLng()) );

        // Return the completed view to render on screen
        return convertView;
    }
}



