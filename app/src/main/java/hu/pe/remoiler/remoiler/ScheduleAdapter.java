package hu.pe.remoiler.remoiler;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;


public class ScheduleAdapter extends CursorAdapter {

    Context mContext;

    public ScheduleAdapter(Context context, Cursor c) {
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
    public void bindView(View view, Context context, Cursor cursor) {
        // Declare all widgets in the layout
        TextView startTimeTv = (TextView) view.findViewById(R.id.schedule_start_time);
        TextView endTimeTv = (TextView) view.findViewById(R.id.schedule_end_time);
        SwitchCompat activeSwitch = (SwitchCompat) view.findViewById(R.id.schedule_active_switch);
        TextView daysInWeekTv = (TextView) view.findViewById(R.id.schedule_days_in_week);

        // Extract from Cursor
        int startTimeCursor = cursor.getInt(cursor.getColumnIndex("start_time"));
        int endTimeCursor = cursor.getInt(cursor.getColumnIndex("end_time"));
        int active = cursor.getInt(cursor.getColumnIndex("active"));
        String daysInWeekCursor = cursor.getString(cursor.getColumnIndex("returns"));

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
            switch (daysInWeek[i]) {
                case 1:
                    daysInWeekString += intToDayInWeek(i) + " ";
            }
        }

        return daysInWeekString;
    }

    // TODO: check if dynamic string works
    private String intToDayInWeek (int day) {
        switch (day) {
            case 1:
                return mContext.getString(R.string.schedule_sunday);
            case 2:
                return mContext.getString(R.string.schedule_monday);
            case 3:
                return mContext.getString(R.string.schedule_tuesday);
            case 4:
                return mContext.getString(R.string.schedule_wednesday);
            case 5:
                return mContext.getString(R.string.schedule_thursday);
            case 6:
                return mContext.getString(R.string.schedule_friday);
            case 7:
                return mContext.getString(R.string.schedule_saturday);
        }
        return "";
    }
}
