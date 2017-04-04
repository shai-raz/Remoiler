package hu.pe.remoiler.remoiler;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Switch;
import android.widget.TextView;


public class ScheduleAdapter extends CursorAdapter {

    public ScheduleAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
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
        Switch activeSwitch = (Switch) view.findViewById(R.id.schedule_active_switch);
        TextView daysInWeekTv = (TextView) view.findViewById(R.id.schedule_days_in_week);

        // Extract from Cursor
        int startTimeCursor = cursor.getInt(cursor.getColumnIndex("start_time"));
        int endTimeCursor = cursor.getInt(cursor.getColumnIndex("end_time"));
        int active = cursor.getInt(cursor.getColumnIndex("active"));
        String daysInWeekCursor = cursor.getString(cursor.getColumnIndex("returns"));

        // Redefine extracted information
        int startTimeHour = startTimeCursor/60;
        int startTimeMin = startTimeCursor%60;
        String startTime = startTimeHour + ":" + startTimeMin;

        int endTimeHour = endTimeCursor/60;
        int endTimeMin = endTimeCursor%60;
        String endTime = endTimeHour + ":" + endTimeMin;

        // Create an array of days
        daysInWeekCursor = daysInWeekCursor.replace("[","");
        daysInWeekCursor = daysInWeekCursor.replace("]","");
        String[] daysInWeekStringArray = daysInWeekCursor.split(",");

        // Convert array to int array
        int[] daysInWeek = new int[8];

        for (int i = 0; i < 8; i++) {
            daysInWeek[i] = Integer.parseInt(daysInWeekStringArray[i]);
        }

        // Populate fields with extracted info
        startTimeTv.setText(startTime);
        endTimeTv.setText(endTime);
        activeSwitch.setChecked(active == 1);
        daysInWeekTv.setText(intArrayDaysToString(daysInWeek));
    }

    /**
     *
     * @param daysInWeek Week int array with 1s and 0s
     * @return String of active days and their names
     */
    private String intArrayDaysToString (int[] daysInWeek) {
        String daysInWeekString = "";

        for (int i = 0; i < 8; i++) {
            switch (daysInWeek[i]) {
                case 1:
                    daysInWeekString += intToDayInWeek(i) + " ";
            }
        }

        return daysInWeekString;
    }
    // TODO: Make dynamic string
    private String intToDayInWeek (int day) {
        switch (day) {
            case 1:
                return "Sunday";
            case 2:
                return "Monday";
            case 3:
                return "Tuesday";
            case 4:
                return "Wednesday";
            case 5:
                return "Thursday";
            case 6:
                return "Friday";
            case 7:
                return "Saturday";
        }
        return "";
    }
}
