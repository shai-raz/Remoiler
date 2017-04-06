package hu.pe.remoiler.remoiler;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class BoilerAdapter extends CursorAdapter {

    public BoilerAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.boiler_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Declare Widgets
        TextView boilerNameTv = (TextView) view.findViewById(R.id.boiler_name);

        // Extract from the cursor
        String boilerName = cursor.getString(cursor.getColumnIndex("name"));

        // Populate widgets with cursor's data
        boilerNameTv.setText(boilerName);

    }
}
