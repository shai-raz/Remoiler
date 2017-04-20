package hu.pe.remoiler.remoiler;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.NumberPicker;

import java.util.Locale;

import cn.carbswang.android.numberpickerview.library.NumberPickerView;

public class ScheduleEditor extends AppCompatActivity {

    NumberPicker hourPicker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_editor);

        hourPicker = (NumberPicker) findViewById(R.id.hourPicker);

        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(24);
        hourPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format(Locale.getDefault(), "%02d", i);
            }
        });

        // TODO: add minute picker
        // TODO: add returns (days of week)
        // TODO: visual polish
        // TODO: insert everything to the "schedule" table

    }

}
