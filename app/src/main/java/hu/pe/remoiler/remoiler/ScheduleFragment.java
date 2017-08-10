package hu.pe.remoiler.remoiler;

import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import cn.pedant.SweetAlert.SweetAlertDialog;
import hu.pe.remoiler.remoiler.data.BoilerContract.BoilerEntry;
import hu.pe.remoiler.remoiler.data.ScheduleContract.ScheduleEntry;
import hu.pe.remoiler.remoiler.data.RemoilerDbHelper;

public class ScheduleFragment extends Fragment implements LoaderManager.LoaderCallbacks<Integer> {

    private static final String LOG_TAG = ScheduleFragment.class.getSimpleName();

    Boolean _isSeen = false; // Is the fragment been seen by the user yet?

    View rootView;
    //ScheduleAdapter scheduleAdapter;

    private int mBoilerID;
    ListView mListView;
    ScheduleAdapter scheduleAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBoilerID = getActivity().getIntent().getIntExtra("boilerID", 0);

        // Root view that will be edited & returned at the end
        rootView = inflater.inflate(R.layout.fragment_schedule, container, false);

        // Setting OnClickListener on the Floating button that will direct to the Schedule Editor
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ScheduleEditor.class);
                intent.putExtra("mBoilerID", mBoilerID);
                startActivity(intent);
            }
        });

        // Get schedules from server
        //getSchedulesFromServer();

        // Fill ListView with the schedules
        populateList();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ScheduleEditor.class);

                long scheduleID = mListView.getAdapter().getItemId(position);
                Cursor adapterCursor = (Cursor) mListView.getAdapter().getItem(position);
                Log.i(LOG_TAG, "schedule ID:" + String.valueOf(scheduleID));
                intent.putExtra("scheduleID", scheduleID);
                intent.putExtra("mBoilerID", adapterCursor.getInt(adapterCursor.getColumnIndex(ScheduleEntry.COLUMN_SCHEDULE_BOILER_ID)));

                Cursor cursor = (Cursor) mListView.getAdapter().getItem(position);
                int startTime = cursor.getInt(cursor.getColumnIndex(ScheduleEntry.COLUMN_SCHEDULE_START_TIME));
                int endTime = cursor.getInt(cursor.getColumnIndex(ScheduleEntry.COLUMN_SCHEDULE_END_TIME));
                String returns = cursor.getString(cursor.getColumnIndex(ScheduleEntry.COLUMN_SCHEDULE_RETURNS));

                intent.putExtra("startTime", startTime);
                intent.putExtra("endTime", endTime);
                intent.putExtra("returns", returns);

                startActivity(intent);

            }
        });

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && !_isSeen ) {
            _isSeen = true;
            getSchedulesFromServer();
            // Code executes ONLY THE FIRST TIME fragment is viewed.
        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void getSchedulesFromServer() {
        RemoilerDbHelper dbHelper = new RemoilerDbHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = { BoilerEntry.COLUMN_BOILER_KEY };
        String[] selectionArgs = { String.valueOf(mBoilerID) };

        // Getting the boiler's key
        Cursor boilerCursor = db.query(
                BoilerEntry.TABLE_NAME,
                projection,
                BoilerEntry._ID + "=?",
                selectionArgs,
                null,
                null,
                null);

        Log.i(LOG_TAG, "boilerId: " + mBoilerID + ",CURSOR QUERY: " + DatabaseUtils.dumpCursorToString(boilerCursor));

        String boilerKey = null;
        Log.i(LOG_TAG, "boilerCursor: " + boilerCursor);
        if (boilerCursor != null && boilerCursor.moveToFirst()) {
            boilerKey = boilerCursor.getString(boilerCursor.getColumnIndex(BoilerEntry.COLUMN_BOILER_KEY));
            boilerCursor.close();
        }
        db.close();

        class GetSchedulesFromServerTask extends AsyncTask<String, Void, String> {

            private SweetAlertDialog loadingSchedulesDialog;

            @Override
            protected void onPreExecute() {
                loadingSchedulesDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE)
                        .setTitleText(getString(R.string.schedule_loading_from_server));
                loadingSchedulesDialog.setCancelable(false);
                loadingSchedulesDialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                URL queryUrl = ServerQueries.createURL(ServerQueries.PATH_GET_SCHEDULES, params[0]);
                Log.i(LOG_TAG, "params: " + params);

                try {
                    String response  = NetworkUtils.getStringFromURL2(queryUrl);
                    Log.i(LOG_TAG, "json schedule response: " + response);
                    return response;

                } catch (IOException e) {
                    Log.e(LOG_TAG, "ERROR CONNECTING TO SERVER");
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String response) {
                loadingSchedulesDialog.dismiss();
                if (response == null) { // If there was an error getting schedules from the server (null and not "" - I guess)
                    SweetAlertDialog errorDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(getString(R.string.schedule_error_something_went_wrong))
                            .setContentText(getString(R.string.schedule_error_couldnt_reach_server));
                    /*errorDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            //ScheduleEditor.this.finish();
                        }
                    });*/
                    errorDialog.show();

                } else { // Update the db with schedules from server
                    JSONArray jsonResponse = null;
                    try {
                        jsonResponse = new JSONArray(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JSONObject schedule;
                    for (int i = 0; i < jsonResponse.length(); i++) {

                        try {
                            schedule = jsonResponse.getJSONObject(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    /*RemoilerDbHelper dbHelper = new RemoilerDbHelper(getActivity());
                    SQLiteDatabase db = dbHelper.getReadableDatabase();*/
                }
            }
        }

        GetSchedulesFromServerTask scheduleServerTask = new GetSchedulesFromServerTask();
        scheduleServerTask.execute(boilerKey);
    }

    private void populateList() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        RemoilerDbHelper mDbHelper = new RemoilerDbHelper(getActivity());
        //Log.i(LOG_TAG, "mDbHelper created");

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        //Log.i(LOG_TAG, "SqLiteDatabase created");

        // Columns to select
        String[] projection = {
                ScheduleEntry._ID,
                ScheduleEntry.COLUMN_SCHEDULE_BOILER_ID,
                ScheduleEntry.COLUMN_SCHEDULE_START_TIME,
                ScheduleEntry.COLUMN_SCHEDULE_END_TIME,
                ScheduleEntry.COLUMN_SCHEDULE_RETURNS,
                ScheduleEntry.COLUMN_SCHEDULE_ACTIVE
        };

        // Getting the schedules for the adapter
        Cursor scheduleCursor = db.query(
                ScheduleEntry.TABLE_NAME,
                projection,
                ScheduleEntry.COLUMN_SCHEDULE_BOILER_ID + "=?",
                new String[] { String.valueOf(mBoilerID) },
                null,
                null,
                null,
                null);

        mListView = (ListView) rootView.findViewById(R.id.schedule_list_view);
        scheduleAdapter = new ScheduleAdapter(getActivity(), scheduleCursor);
        mListView.setAdapter(scheduleAdapter);
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
        RemoilerDbHelper mDbHelper = new RemoilerDbHelper(getActivity());

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
