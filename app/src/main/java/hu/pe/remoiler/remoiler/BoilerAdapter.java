package hu.pe.remoiler.remoiler;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import hu.pe.remoiler.remoiler.data.BoilerContract.BoilerEntry;

public class BoilerAdapter extends CursorAdapter {

    private static final String LOG_TAG = BoilerAdapter.class.getSimpleName();

    private SparseBooleanArray mSelectedItemsIds;
    private ArrayList<?> item_modelArrayList;
    private HashMap<Integer, Boolean> mSelection = new HashMap<Integer, Boolean>();
    private Context mContext;
    private boolean isActionMode = false;


    public BoilerAdapter(Context context, Cursor c) {
        super(context, c, 0);
        mSelectedItemsIds = new SparseBooleanArray();
        mContext = context;

    }

    // Set the layout we'll be using as the item view
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.boiler_item, parent, false);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);//let the adapter handle setting up the row views
        v.setBackgroundColor(ContextCompat.getColor(mContext,android.R.color.background_light)); //default color

        if (mSelection.get(position) != null) {
            v.setBackgroundColor(ContextCompat.getColor(mContext,android.R.color.holo_blue_light));// this is a selected position so make it red
        }

        /*ListView listView = (ListView) parent.findViewById(R.id.main_list_view);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(LOG_TAG, "OnItemLongClick() " + nr);
                if (nr == 0) {
                    listView.setItemChecked(position, !boilerAdapter.isPositionChecked(position));
                    return true;
                }
                return false;
            }
        });*/
        return v;
    }

    // Bind all the views into the layout
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        // Declare Widgets
        TextView boilerNameTv = (TextView) view.findViewById(R.id.boiler_name);
        //ImageView editIcon = (ImageView) view.findViewById(R.id.boiler_edit_icon);

        // Extract from the cursor
        final String boilerName = cursor.getString(cursor.getColumnIndex(BoilerEntry.COLUMN_BOILER_NAME));
        final int boilerID = cursor.getInt(cursor.getColumnIndex(BoilerEntry._ID));
        final String boilerKey = cursor.getString(cursor.getColumnIndex(BoilerEntry.COLUMN_BOILER_KEY));

        // Populate widgets with cursor's data
        boilerNameTv.setText(boilerName);

        view.setFocusable(false);
        //view.setLongClickable(true);

        /*editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, mBoilerName, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, BoilerEditor.class);
                intent.putExtra("boilerID", boilerID);
                intent.putExtra("boilerName", boilerName);
                intent.putExtra("boilerKey", boilerKey);
                context.startActivity(intent);
            }
        });*/

        /*view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isActionMode) {
                    Intent intent = new Intent(context, BoilerActivity.class);
                    intent.putExtra("boilerID", boilerID);
                    context.startActivity(intent);
                }
            }
        });*/
    }

    public void disableOnClick() {
        isActionMode = true;
    }

    public void enableOnClick() {
        isActionMode = false;
    }

    /***
     * Methods required for do selections, remove selections, etc.
     */

    public void setNewSelection(int position, boolean value) {
        mSelection.put(position, value);
        notifyDataSetChanged();
    }

    public boolean isPositionChecked(int position) {
        Boolean result = mSelection.get(position);
        return result == null ? false : result;
    }

    public Set<Integer> getCurrentCheckedPosition() {
        return mSelection.keySet();
    }

    public void removeSelection(int position) {
        mSelection.remove(position);
        notifyDataSetChanged();
    }

    public void clearSelection() {
        mSelection = new HashMap<Integer, Boolean>();
        notifyDataSetChanged();
    }

}
