package hu.pe.remoiler.remoiler;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import hu.pe.remoiler.remoiler.data.BoilerContract.BoilerEntry;

public class BoilerAdapter extends CursorAdapter {

    private SparseBooleanArray mSelectedItemsIds;
    private ArrayList<?> item_modelArrayList;


    public BoilerAdapter(Context context, Cursor c) {
        super(context, c, 0);
        mSelectedItemsIds = new SparseBooleanArray();

    }

    // Set the layout we'll be using as the item view
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.boiler_item, parent, false);
    }

    // Bind all the views into the layout
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Declare Widgets
        TextView boilerNameTv = (TextView) view.findViewById(R.id.boiler_name);
        ImageView editIcon = (ImageView) view.findViewById(R.id.boiler_edit_icon);

        // Extract from the cursor
        final String boilerName = cursor.getString(cursor.getColumnIndex("name"));
        final int boilerID = cursor.getInt(cursor.getColumnIndex(BoilerEntry._ID));

        // Populate widgets with cursor's data
        boilerNameTv.setText(boilerName);

        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, boilerName, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, BoilerEditor.class);
                intent.putExtra("name", boilerName);
                //intent.putExtra("id", boilerID);
                context.startActivity(intent);
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BoilerActivity.class);
                intent.putExtra("boilerID", boilerID);
                context.startActivity(intent);
            }
        });
    }

    /***
     * Methods required for do selections, remove selections, etc.
     */

    //Toggle selection methods
    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }


    //Remove selected selections
    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }


    //Put or delete selected position into SparseBooleanArray
    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);

        notifyDataSetChanged();
    }

    //Get total selected count
    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    //Return all selected ids
    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

}
