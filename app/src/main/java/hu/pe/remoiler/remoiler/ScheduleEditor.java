package hu.pe.remoiler.remoiler;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.NumberPicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import hu.pe.remoiler.remoiler.data.BoilerContract.BoilerEntry;
import hu.pe.remoiler.remoiler.data.RemoilerDbHelper;
import hu.pe.remoiler.remoiler.data.ScheduleContract.ScheduleEntry;

public class ScheduleEditor extends AppCompatActivity {

    final private static String LOG_TAG = ScheduleEntry.class.getSimpleName();

    NumberPicker startTimeHourPicker;
    NumberPicker startTimeMinutePicker;
    NumberPicker endTimeHourPicker;
    NumberPicker endTimeMinutePicker;
    ToggleButton[] toggleDay = new ToggleButton[7];

    private int mBoilerID;
    private int mStartTime;
    private int mEndTime;
    private int[] mReturns;
    private long mScheduleID;
    private boolean mEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_editor);

        mBoilerID = getIntent().getIntExtra("boilerID", 0);
        Toast.makeText(this, String.valueOf(mBoilerID), Toast.LENGTH_SHORT).show();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        startTimeHourPicker = (NumberPicker) findViewById(R.id.startTimeHourPicker);
        startTimeMinutePicker = (NumberPicker) findViewById(R.id.starTimeMinutePicker);

        endTimeHourPicker = (NumberPicker) findViewById(R.id.endTimeHourPicker);
        endTimeMinutePicker = (NumberPicker) findViewById(R.id.endTimeMinutePicker);

        startTimeHourPicker.setMinValue(0);
        startTimeHourPicker.setMaxValue(23);
        startTimeHourPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format(Locale.getDefault(), "%02d", i);
            }
        });

        startTimeMinutePicker.setMinValue(0);
        startTimeMinutePicker.setMaxValue(59);
        startTimeMinutePicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format(Locale.getDefault(), "%02d", i);
            }
        });

        endTimeHourPicker.setMinValue(0);
        endTimeHourPicker.setMaxValue(23);
        endTimeHourPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format(Locale.getDefault(), "%02d", i);
            }
        });

        endTimeMinutePicker.setMinValue(0);
        endTimeMinutePicker.setMaxValue(59);
        endTimeMinutePicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format(Locale.getDefault(), "%02d", i);
            }
        });

        toggleDay[0] = (ToggleButton) findViewById(R.id.toggleSunday);
        toggleDay[1] = (ToggleButton) findViewById(R.id.toggleMonday);
        toggleDay[2] = (ToggleButton) findViewById(R.id.toggleTuesday);
        toggleDay[3] = (ToggleButton) findViewById(R.id.toggleWednesday);
        toggleDay[4] = (ToggleButton) findViewById(R.id.toggleThursday);
        toggleDay[5] = (ToggleButton) findViewById(R.id.toggleFriday);
        toggleDay[6] = (ToggleButton) findViewById(R.id.toggleSaturday);

        mScheduleID = getIntent().getLongExtra("scheduleID", -1);
        Toast.makeText(this, "mScheduleID = " + mScheduleID, Toast.LENGTH_SHORT).show();

        // Check whether it's a new schedule or not (-1 = new)
        if (mScheduleID != -1) {
            mEditMode = true;
            mStartTime = getIntent().getIntExtra("startTime", 0);
            mEndTime = getIntent().getIntExtra("endTime", 0);
            String stringReturns = getIntent().getStringExtra("returns");

            // Create an array of days
            stringReturns = stringReturns.replace("[","");
            stringReturns = stringReturns.replace("]","");
            stringReturns = stringReturns.replaceAll("\\s+","");
            String[] returnsArray = stringReturns.split(",");

            // Convert array to int array
            int[] returns = new int[7];

            for (int i = 0; i < 7; i++) {
                returns[i] = Integer.parseInt(returnsArray[i]);
            }

            //Log.i(LOG_TAG, "returnsInt: " + Arrays.toString(mReturns));

            startTimeHourPicker.setValue(Integer.parseInt(minutesInDayToHours(mStartTime)));
            startTimeMinutePicker.setValue(Integer.parseInt(minutesInDayToMinutes(mStartTime)));
            endTimeHourPicker.setValue(Integer.parseInt(minutesInDayToHours(mEndTime)));
            endTimeMinutePicker.setValue(Integer.parseInt(minutesInDayToMinutes(mEndTime)));

            for (int i = 0; i < 7; i++) {
                if (returns[i] == 1)
                    toggleDay[i].setChecked(true);
            }

        }

        // TODO: visual polish
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_boiler_editor, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveSchedule();
                //Toast.makeText(this, "Save clicked", Toast.LENGTH_SHORT).show();
                return true;
            case android.R.id.home:
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Saves the created schedule into the server, and then calls @saveScheduleToDb
     */
    private void saveSchedule() {
        /*RemoilerDbHelper dbHelper = new RemoilerDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        // 'Convert' the times into minutes in day.
        mStartTime = (startTimeHourPicker.getValue() * 60) + startTimeMinutePicker.getValue();
        mEndTime = (endTimeHourPicker.getValue() * 60) + endTimeMinutePicker.getValue();

        mReturns = new int[7];

        // Create an int array of selected days in week (which will be inserted into the db as a String)
        for (int i = 0; i < 7; i++) {
            mReturns[i] = toggleDay[i].isChecked() ? 1 : 0;
        }

        values.put(ScheduleEntry.COLUMN_SCHEDULE_BOILER_ID, mBoilerID);
        values.put(ScheduleEntry.COLUMN_SCHEDULE_START_TIME, mStartTime);
        values.put(ScheduleEntry.COLUMN_SCHEDULE_END_TIME, mEndTime);
        values.put(ScheduleEntry.COLUMN_SCHEDULE_RETURNS, Arrays.toString(mReturns));

        long newRowId;

        if (!mEditMode) // New schedule
            newRowId = db.insert(ScheduleEntry.TABLE_NAME, null, values);
        else { // Editing
            String[] whereArgs = { String.valueOf(mScheduleID) };
            newRowId = db.update(ScheduleEntry.TABLE_NAME, values, ScheduleEntry._ID + "=?", whereArgs);
        }

        if (newRowId != -1)
            Toast.makeText(this, "Added new Boiler! ID: " + newRowId, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Problem adding new boiler.", Toast.LENGTH_SHORT).show();

        db.close();*/

        // Get the boiler key
        RemoilerDbHelper dbHelper = new RemoilerDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] columns = {BoilerEntry.COLUMN_BOILER_KEY};
        String[] selectionArgs = {ScheduleEntry.COLUMN_SCHEDULE_BOILER_ID};
        Cursor cursor = db.query(BoilerEntry.TABLE_NAME,
                columns,
                BoilerEntry._ID + "+?",
                selectionArgs,
                null,
                null,
                null);

        String boilerKey = "";
        if (cursor != null && cursor.moveToFirst()) {
            boilerKey = cursor.getString(cursor.getColumnIndex(BoilerEntry.COLUMN_BOILER_KEY));
        }

        // Get the Values from the 'form'
        // 'Convert' the times into minutes in day.
        mStartTime = (startTimeHourPicker.getValue() * 60) + startTimeMinutePicker.getValue();
        mEndTime = (endTimeHourPicker.getValue() * 60) + endTimeMinutePicker.getValue();

        mReturns = new int[7];

        // Create an int array of selected days in week (which will be inserted into the db as a String)
        for (int i = 0; i < 7; i++) {
            mReturns[i] = toggleDay[i].isChecked() ? 1 : 0;
        }

        ArrayList<String> paramsList = new ArrayList<>();
        paramsList.add("key=" + boilerKey);
        if (mEditMode)
            paramsList.add("luz_id=" + mScheduleID);
        paramsList.add("start_time=" + mStartTime);
        paramsList.add("end_time=" + mEndTime);
        paramsList.add("returns=" + Arrays.toString(mReturns));
        Log.i(LOG_TAG, "Returns: " + Arrays.toString(mReturns));


        class ScheduleTask extends AsyncTask<String, Void, String> {

            private final String LOG_TAG = ScheduleTask.class.getSimpleName();

            @Override
            protected String doInBackground(String... params) {
                URL queryUrl = ServerQueries.createURL(ServerQueries.PATH_SCHEDULE);
                Log.i(LOG_TAG, "params: " + params.toString());

                try {
                    String response = NetworkUtils.getStringFromURL3(queryUrl, params);

                    Log.i(LOG_TAG, "response: " + response);
                    return response;

                } catch (IOException e) {
                    Log.e(LOG_TAG, "ERROR CONNECTING TO SERVER");
                    e.printStackTrace();
                    return "-1";
                }
            }

            @Override
            protected void onPostExecute(String response) {
                if (response == null || response.equals("-1")) {
                    SweetAlertDialog errorDialog = new SweetAlertDialog(ScheduleEditor.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(getString(R.string.schedule_error_something_went_wrong))
                            .setContentText(getString(R.string.schedule_error_couldnt_reach_server));
                    /*errorDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    ScheduleEditor.this.finish();
                                }
                            });*/
                    errorDialog.show();
                } else { // If saving schedule to server is successful
                    Log.i(LOG_TAG, "onPostExecute(), response: " + response);
                    //long longResponse = Long.parseLong(response);
                    response = response.substring(1);
                    response = response.substring(0, response.length() - 1);
                    String[] splitResponse = response.split(","); // Server returns an array in this form: [ID, WAS REMOILER NOTICED(0/1)]
                    Long idFromServer = Long.parseLong(splitResponse[0]); // The ID the server used in the database
                    Integer isRemoilerNoticed = Integer.valueOf(splitResponse[1]); // Was remoiler noticed about the new schedule
                    saveScheduleToDb(idFromServer); // Save schedule into DB
                    ScheduleEditor.this.finish(); // Close editor activity

                    // TODO: let the user know that the Remoiler was either noticed or not (Pop a msg)
                }
            }
        }

        ScheduleTask scheduleTask = new ScheduleTask();
        String[] params = new String[paramsList.size()];
        params = paramsList.toArray(params);
        scheduleTask.execute(params);

        // TODO: change concept of Schedule IDs so it will worked sync with server ( Maybe work with serials )
        // TODO: Only add schedule if it is sent to the server as well

    }


    /**
     * Saves new/updating existing schedule into database
     * @param id New schedule - id that gets returned from the server.
     *               Existing schedule - Schedule's id from DB.
     */
    private void saveScheduleToDb(long id) {
        RemoilerDbHelper dbHelper = new RemoilerDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        /*// 'Convert' the times into minutes in day.
        mStartTime = (startTimeHourPicker.getValue() * 60) + startTimeMinutePicker.getValue();
        mEndTime = (endTimeHourPicker.getValue() * 60) + endTimeMinutePicker.getValue();

        mReturns = new int[7];

        // Create an int array of selected days in week (which will be inserted into the db as a String)
        for (int i = 0; i < 7; i++) {
            mReturns[i] = toggleDay[i].isChecked() ? 1 : 0;
        }*/

        values.put(ScheduleEntry._ID, id);
        values.put(ScheduleEntry.COLUMN_SCHEDULE_BOILER_ID, mBoilerID);
        values.put(ScheduleEntry.COLUMN_SCHEDULE_START_TIME, mStartTime);
        values.put(ScheduleEntry.COLUMN_SCHEDULE_END_TIME, mEndTime);
        values.put(ScheduleEntry.COLUMN_SCHEDULE_RETURNS, Arrays.toString(mReturns));

        long newRowId;

        if (!mEditMode) // New schedule
            newRowId = db.insert(ScheduleEntry.TABLE_NAME, null, values);
        else { // Editing
            String[] whereArgs = { String.valueOf(mScheduleID) };
            newRowId = db.update(ScheduleEntry.TABLE_NAME, values, ScheduleEntry._ID + "=?", whereArgs);
        }

        if (newRowId != -1)
            Toast.makeText(this, "Added new Boiler! ID: " + newRowId, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Problem adding new boiler.", Toast.LENGTH_SHORT).show();

        db.close();
    }

    /**
     * Gets hour out of minutes in day(24h) format
     * @param time Minutes in day
     * @return hours in hh format
     */
    private String minutesInDayToHours(int time) {
        String hours = String.valueOf(time/60);

        if (hours.length() == 1)
            hours = "0" + hours;

        return hours;
    }

    /**
     * Gets minutes out of minutes in day(24h) format
     * @param time Minutes in day
     * @return minutes in mm format
     */
    private String minutesInDayToMinutes(int time) {
        String minutes = String.valueOf(time%60);

        if (minutes.length() == 1)
            minutes = "0" + minutes;

        return minutes;
    }

}
