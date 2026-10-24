
package com.example.smartpantrymanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PantryAdapter extends ArrayAdapter<String> {

    public PantryAdapter(Context context, ArrayList<String> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;

        if (row == null) {
            row = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_pantry, parent, false);
        }

        TextView txtName = row.findViewById(R.id.txtPantryName);
        TextView txtDetails = row.findViewById(R.id.txtPantryDetails);

        String item = getItem(position);

        if (item != null) {

            if (item.contains(" — ")) {

                String[] parts = item.split(" — ", 2);

                txtName.setText(parts[0]);
                txtDetails.setText(parts[1]);

            } else {

                txtName.setText(item);
                txtDetails.setText("");
            }
        }

        return row;
    }
}

