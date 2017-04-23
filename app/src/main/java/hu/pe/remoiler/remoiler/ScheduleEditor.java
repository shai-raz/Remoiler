package hu.pe.remoiler.remoiler;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.util.Locale;

import hu.pe.remoiler.remoiler.data.RemoilerDbHelper;
import hu.pe.remoiler.remoiler.data.ScheduleContract.ScheduleEntry;

public class ScheduleEditor extends AppCompatActivity {

    NumberPicker startTimeHourPicker;
    NumberPicker startTimeMinutePicker;
    NumberPicker endTimeHourPicker;
    NumberPicker endTimeMinutePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_editor);

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
                //saveSchedule();
                Toast.makeText(this, "Save clicked", Toast.LENGTH_SHORT).show();
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveSchedule() {
        RemoilerDbHelper dbHelper = new RemoilerDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put(ScheduleEntry.COLUMN_SCHEDULE_START_TIME, );
        // TODO: Save schedule to database
        // TODO: add option to EDIT existing schedule
    }

}
