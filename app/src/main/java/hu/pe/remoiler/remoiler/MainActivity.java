package hu.pe.remoiler.remoiler;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import hu.pe.remoiler.remoiler.data.BoilerContract.BoilerEntry;
import hu.pe.remoiler.remoiler.data.RemoilerDbHelper;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Setting OnClickListener on the Floating button that will direct to the Boiler Editor
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BoilerEditor.class);
                startActivity(intent);
            }
        });

        populateList();
    }

    private void populateList() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        RemoilerDbHelper mDbHelper = new RemoilerDbHelper(this);

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Columns to select
        String[] projection = {
                BoilerEntry._ID,
                BoilerEntry.COLUMN_BOILER_NAME,
                BoilerEntry.COLUMN_BOILER_KEY
        };

        // Perform this raw SQL query "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.
        Cursor cursor = db.query(
                BoilerEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null,
                null);

        // Define list&adapter and set adapter on list.
        ListView listView = (ListView) findViewById(R.id.main_list_view);
        BoilerAdapter boilerAdapter = new BoilerAdapter(this, cursor);
        listView.setAdapter(boilerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
                deleteAllEntries();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Insert dummy boiler
    private void insertDummyData() {
        RemoilerDbHelper mDbHelper = new RemoilerDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BoilerEntry.COLUMN_BOILER_NAME, "Test");
        values.put(BoilerEntry.COLUMN_BOILER_KEY, "raz");

        long newRowId = db.insert(BoilerEntry.TABLE_NAME, null, values);

        if (newRowId != -1)
            Toast.makeText(this, "Added Dummy Data! ID: " + newRowId, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Problem adding dummy data", Toast.LENGTH_SHORT).show();

        db.close();
    }

    // Delete all entries
    private void deleteAllEntries() {
        RemoilerDbHelper mDbHelper = new RemoilerDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.delete(BoilerEntry.TABLE_NAME, null, null);
        Toast.makeText(this, "All boilers were deleted.", Toast.LENGTH_SHORT).show();

        db.close();
    }
}
