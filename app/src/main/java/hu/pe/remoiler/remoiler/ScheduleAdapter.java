package hu.pe.remoiler.remoiler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.TextView;

import hu.pe.remoiler.remoiler.data.BoilerContract;
import hu.pe.remoiler.remoiler.data.RemoilerDbHelper;
import hu.pe.remoiler.remoiler.data.ScheduleContract.ScheduleEntry;


public class ScheduleAdapter extends CursorAdapter {

    final private static String LOG_TAG = ScheduleAdapter.class.getSimpleName();

    Context mContext;

    ScheduleAdapter(Context context, Cursor c) {
        super(context, c, 0);
        mContext = context;
    }

    // Set the layout we'll be using as the item view
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.schedule_item, parent, false);
    }

    // Bind all the views into the layout
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        Log.i(LOG_TAG, "bindView()");
        // Declare all widgets in the layout
        TextView startTimeTv = (TextView) view.findViewById(R.id.schedule_start_time);
        TextView endTimeTv = (TextView) view.findViewById(R.id.schedule_end_time);
        SwitchCompat activeSwitch = (SwitchCompat) view.findViewById(R.id.schedule_active_switch);
        TextView daysInWeekTv = (TextView) view.findViewById(R.id.schedule_days_in_week);

        // Extract from Cursor
        final long scheduleID = cursor.getLong(cursor.getColumnIndex(ScheduleEntry._ID));
        final int boilerID = cursor.getInt(cursor.getColumnIndex(ScheduleEntry.COLUMN_SCHEDULE_BOILER_ID));
        int startTimeCursor = cursor.getInt(cursor.getColumnIndex(ScheduleEntry.COLUMN_SCHEDULE_START_TIME));
        int endTimeCursor = cursor.getInt(cursor.getColumnIndex(ScheduleEntry.COLUMN_SCHEDULE_END_TIME));
        int active = cursor.getInt(cursor.getColumnIndex(ScheduleEntry.COLUMN_SCHEDULE_ACTIVE));
        String daysInWeekCursor = cursor.getString(cursor.getColumnIndex(ScheduleEntry.COLUMN_SCHEDULE_RETURNS));

        // Redefine extracted information
        String startTime = minutesInDayToTime(startTimeCursor);
        String endTime = minutesInDayToTime(endTimeCursor);

        // Create an array of days
        daysInWeekCursor = daysInWeekCursor.replace("[","");
        daysInWeekCursor = daysInWeekCursor.replace("]","");
        daysInWeekCursor = daysInWeekCursor.replaceAll("\\s+","");
        String[] daysInWeekStringArray = daysInWeekCursor.split(",");

        // Convert array to int array
        int[] daysInWeek = new int[7];

        for (int i = 0; i < 7; i++) {
            daysInWeek[i] = Integer.parseInt(daysInWeekStringArray[i]);
        }

        // Populate fields with extracted info
        startTimeTv.setText(startTime);
        endTimeTv.setText(endTime);
        activeSwitch.setChecked(active == 1);
        daysInWeekTv.setText(intArrayDaysToString(daysInWeek));

        view.setFocusable(false);

        activeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                RemoilerDbHelper dbHelper = new RemoilerDbHelper(context);
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put(ScheduleEntry.COLUMN_SCHEDULE_ACTIVE, isChecked);

                db.update(ScheduleEntry.TABLE_NAME,
                        values,
                        ScheduleEntry.COLUMN_SCHEDULE_BOILER_ID + "=?" +
                                " AND " + ScheduleEntry._ID + "=?",
                        new String[] { String.valueOf(boilerID), String.valueOf(scheduleID) }
                );

                db.close();

                /*
                // TODO: update active with server
                Consider moving this db query to ScheduleFragment and then call it from here.
                */
            }
        });
    }

    /**
     * Converts minutes in day to Hours:Minutes
     * @param time Minutes in day
     * @return String in format of Hours:Minutes
     */
    private String minutesInDayToTime(int time) {
        String hours = String.valueOf(time/60);
        String minutes = String.valueOf(time%60);

        if (hours.length() == 1)
            hours = "0" + hours;
        if (minutes.length() == 1)
            minutes = "0" + minutes;

        return hours + ":" + minutes;
    }

    /**
     *
     * @param daysInWeek Week int array with 1s and 0s
     * @return String of active days and their names
     */
    private String intArrayDaysToString (int[] daysInWeek) {
        String daysInWeekString = "";

        for (int i = 0; i < 7; i++) {
            if (daysInWeek[i] == 1)
                daysInWeekString += intToDayInWeek(i) + " ";
        }

        return daysInWeekString;
    }

    // TODO: check if dynamic string works
    private String intToDayInWeek (int day) {
        switch (day) {
            case 0:
                return mContext.getString(R.string.schedule_sunday);
            case 1:
                return mContext.getString(R.string.schedule_monday);
            case 2:
                return mContext.getString(R.string.schedule_tuesday);
            case 3:
                return mContext.getString(R.string.schedule_wednesday);
            case 4:
                return mContext.getString(R.string.schedule_thursday);
            case 5:
                return mContext.getString(R.string.schedule_friday);
            case 6:
                return mContext.getString(R.string.schedule_saturday);
        }
        return "";
    }
}
