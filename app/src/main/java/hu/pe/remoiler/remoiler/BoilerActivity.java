package hu.pe.remoiler.remoiler;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import hu.pe.remoiler.remoiler.data.ScheduleContract.ScheduleEntry;
import hu.pe.remoiler.remoiler.data.RemoilerDbHelper;

public class BoilerActivity extends AppCompatActivity {

    String boilerName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boiler);

        /*if (getIntent().getExtras() != null) {
            boilerName = getIntent().getStringExtra("name");

            //nameEdit.setText(boilerName, TextView.BufferType.EDITABLE);
        }*/

        // Create the ViewPager object
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        // Create the TabAdapter (Custom FragmentPagerAdapter)
        TabAdapter tabAdapter = new TabAdapter(this, getSupportFragmentManager());

        // Set TabAdapter with the ViewPager
        viewPager.setAdapter(tabAdapter);

        // Create the TabLayout Object
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        // Set Tabs With Pages
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_schedule, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {

            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertDummyData();
                return true;

            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Temporary insert test
    private void insertDummyData() {
        RemoilerDbHelper mDbHelper = new RemoilerDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        /*
         * TODO: Create a Dbhelper/dbprovider for both tables.
         * -> because right now only the first table is being created (boiler table),
         * and schedule isn't, so it's causing an error.
         */

        ContentValues values = new ContentValues();
        values.put(ScheduleEntry.COLUMN_SCHEDULE_START_TIME, 420);
        values.put(ScheduleEntry.COLUMN_SCHEDULE_END_TIME, 650);
        values.put(ScheduleEntry.COLUMN_SCHEDULE_RETURNS, "[0,0,0,0,1,0,1]");
        values.put(ScheduleEntry.COLUMN_SCHEDULE_ACTIVE, 1);

        long newRowId = db.insert(ScheduleEntry.TABLE_NAME, null, values);

        if (newRowId != -1)
            Toast.makeText(this, "Added Dummy Data! ID: " + newRowId, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Problem adding dummy data", Toast.LENGTH_SHORT).show();

        db.close();
    }
}
