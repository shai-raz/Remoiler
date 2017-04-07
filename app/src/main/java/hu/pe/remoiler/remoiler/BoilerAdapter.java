package hu.pe.remoiler.remoiler;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BoilerAdapter extends CursorAdapter {

    public BoilerAdapter(Context context, Cursor c) {
        super(context, c, 0);
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

        // Populate widgets with cursor's data
        boilerNameTv.setText(boilerName);

        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, boilerName, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, BoilerEditor.class);
                intent.putExtra("name", boilerName);
                context.startActivity(intent);
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BoilerActivity.class);
                //intent.putExtra("name", boilerName);
                context.startActivity(intent);
            }
        });

    }
}
