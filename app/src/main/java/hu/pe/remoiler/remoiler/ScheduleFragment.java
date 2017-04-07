package hu.pe.remoiler.remoiler;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import hu.pe.remoiler.remoiler.data.ScheduleContract.ScheduleEntry;
import hu.pe.remoiler.remoiler.data.ScheduleDbHelper;

public class ScheduleFragment extends Fragment implements LoaderManager.LoaderCallbacks<Integer> {

    private static final String LOG_TAG = ScheduleFragment.class.getSimpleName();

    View rootView;
    //ScheduleAdapter scheduleAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Root view that will be edited & returned at the end
        rootView = inflater.inflate(R.layout.fragment_schedule, container, false);

        // Setting OnClickListener on the Floating button that will direct to the Schedule Editor
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ScheduleEditor.class);
                startActivity(intent);
            }
        });

        //populateList();

        //displayDatabaseInfo();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void populateList() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        ScheduleDbHelper mDbHelper = new ScheduleDbHelper(getActivity());
        Log.i(LOG_TAG, "mDbHelper created");

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Log.i(LOG_TAG, "SqLiteDatabase created");

        // Columns to select
        String[] projection = {
                ScheduleEntry._ID,
                ScheduleEntry.COLUMN_SCHEDULE_START_TIME,
                ScheduleEntry.COLUMN_SCHEDULE_END_TIME,
                ScheduleEntry.COLUMN_SCHEDULE_RETURNS,
                ScheduleEntry.COLUMN_SCHEDULE_ACTIVE
        };

        // Perform this raw SQL query "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.
        Cursor cursor = db.query(
                ScheduleEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null,
                null);


        ListView listView = (ListView) rootView.findViewById(R.id.schedule_list_view);
        ScheduleAdapter scheduleAdapter = new ScheduleAdapter(getActivity(), cursor);
        listView.setAdapter(scheduleAdapter);
        Log.i(LOG_TAG, "Adapter has been set.");

        //cursor.close();
    }

    @Override
    public Loader<Integer> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Integer> loader, Integer data) {

    }

    @Override
    public void onLoaderReset(Loader<Integer> loader) {

    }

    // Temporary database test
    /*private void displayDatabaseInfo() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        ScheduleDbHelper mDbHelper = new ScheduleDbHelper(getActivity());

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Columns to select
        String[] projection = {
                ScheduleEntry._ID,
                ScheduleEntry.COLUMN_SCHEDULE_START_TIME,
                ScheduleEntry.COLUMN_SCHEDULE_END_TIME,
                ScheduleEntry.COLUMN_SCHEDULE_RETURNS,
                ScheduleEntry.COLUMN_SCHEDULE_ACTIVE
        };

        // Perform this raw SQL query "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.
        Cursor cursor = db.query(
                ScheduleEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null,
                null);

        TextView displayView = (TextView) rootView.findViewById(R.id.temp_tv);

        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).
            displayView.setText("There are " + cursor.getCount() + " scheduled events in the database.\n\n");
            displayView.append(ScheduleEntry._ID + " - " +
                    ScheduleEntry.COLUMN_SCHEDULE_START_TIME + " - " +
                    ScheduleEntry.COLUMN_SCHEDULE_END_TIME + " - " +
                    ScheduleEntry.COLUMN_SCHEDULE_RETURNS + " - " +
                    ScheduleEntry.COLUMN_SCHEDULE_ACTIVE + "\n");

            // Get indexes based on projection
            int idColumnIndex = cursor.getColumnIndex(ScheduleEntry._ID);
            int starttimeColumnIndex = cursor.getColumnIndex(ScheduleEntry.COLUMN_SCHEDULE_START_TIME);
            int breedColumnIndex = cursor.getColumnIndex(ScheduleEntry.COLUMN_SCHEDULE_END_TIME);
            int returnsColumnIndex = cursor.getColumnIndex(ScheduleEntry.COLUMN_SCHEDULE_RETURNS);
            int activeColumnIndex = cursor.getColumnIndex(ScheduleEntry.COLUMN_SCHEDULE_ACTIVE);

            // Run through rows and append the data
            while (cursor.moveToNext()) {
                int currentID = cursor.getInt(idColumnIndex);
                String currentStarttime = cursor.getString(starttimeColumnIndex);
                String currentEndtime = cursor.getString(breedColumnIndex);
                int currentReturns = cursor.getInt(returnsColumnIndex);
                int currentActive = cursor.getInt(activeColumnIndex);

                displayView.append("\n" +
                        currentID + " - " +
                        currentStarttime + " - " +
                        currentEndtime + " - " +
                        currentReturns + " - " +
                        currentActive);
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }*/



}
