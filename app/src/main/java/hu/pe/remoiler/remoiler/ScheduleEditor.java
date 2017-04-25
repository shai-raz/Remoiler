package hu.pe.remoiler.remoiler;

import android.app.ActionBar;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.NumberPicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Arrays;
import java.util.Locale;

import hu.pe.remoiler.remoiler.data.RemoilerDbHelper;
import hu.pe.remoiler.remoiler.data.ScheduleContract.ScheduleEntry;

public class ScheduleEditor extends AppCompatActivity {

    NumberPicker startTimeHourPicker;
    NumberPicker startTimeMinutePicker;
    NumberPicker endTimeHourPicker;
    NumberPicker endTimeMinutePicker;
    ToggleButton[] toggleDay = new ToggleButton[7];

    private int boilerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_editor);

        boilerID = getIntent().getIntExtra("boilerID", 0);

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

        // TODO: add returns (days of week)
        // TODO: visual polish
        // TODO: insert everything to the "schedule" table

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
                Toast.makeText(this, "Save clicked", Toast.LENGTH_SHORT).show();
                this.finish();
                return true;
            case android.R.id.home:
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Saves the created schedule into the database
     */
    private void saveSchedule() {
        RemoilerDbHelper dbHelper = new RemoilerDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        // 'Convert' the times into minutes in day.
        int startTime = (startTimeHourPicker.getValue() * 60) + startTimeMinutePicker.getValue();
        int endTime = (endTimeHourPicker.getValue() * 60) + endTimeMinutePicker.getValue();

        int[] returns = new int[7];

        // Create an int array of selected days in week (which will be inserted into the db as a String)
        for (int i = 0; i < 7; i++) {
            returns[i] = toggleDay[i].isChecked() ? 1 : 0;
        }

        values.put(ScheduleEntry.COLUMN_SCHEDULE_BOILER_ID, boilerID);
        values.put(ScheduleEntry.COLUMN_SCHEDULE_START_TIME, startTime);
        values.put(ScheduleEntry.COLUMN_SCHEDULE_END_TIME, endTime);
        values.put(ScheduleEntry.COLUMN_SCHEDULE_RETURNS, Arrays.toString(returns));

        long newRowId = db.insert(ScheduleEntry.TABLE_NAME, null, values);

        if (newRowId != -1)
            Toast.makeText(this, "Added new Boiler! ID: " + newRowId, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Problem adding new boiler.", Toast.LENGTH_SHORT).show();

        db.close();
        // TODO: Save schedule to database
        // TODO: add option to EDIT existing schedule
    }

}
