package hu.pe.remoiler.remoiler;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import hu.pe.remoiler.remoiler.data.BoilerContract.BoilerEntry;
import hu.pe.remoiler.remoiler.data.RemoilerDbHelper;

public class MainActivity extends AppCompatActivity {

    final static private String LOG_TAG = MainActivity.class.getSimpleName();

    private ListView listView;
    private BoilerAdapter boilerAdapter;
    private int nr = 0;
    private boolean isActionMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        populateList();
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                if (checked) {
                    nr++;
                    boilerAdapter.setNewSelection(position, checked);
                } else {
                    nr--;
                    boilerAdapter.removeSelection(position);
                }
                mode.setTitle(nr + " selected");
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                nr = 0;
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.menu_main_list, menu);
                boilerAdapter.disableOnClick();
                isActionMode = true;
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.main_menu_delete:
                        nr = 0;
                        boilerAdapter.clearSelection();
                        mode.finish();
                        break;

                    case R.id.main_menu_edit:
                        nr = 0;
                        boilerAdapter.clearSelection();
                        mode.finish();
                        break;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                nr = 0;
                boilerAdapter.clearSelection();
                boilerAdapter.enableOnClick();
                isActionMode = false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(LOG_TAG, "OnItemClick() " + nr);
                if (isActionMode) {
                    if (nr != 0) {
                        listView.setItemChecked(position, !boilerAdapter.isPositionChecked(position));
                    }
                } else {

                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(LOG_TAG, "OnItemLongClick() " + nr);
                if (nr == 0) {
                    listView.setItemChecked(position, !boilerAdapter.isPositionChecked(position));
                    return true;
                }
                return false;

            }
        });

        // Setting OnClickListener on the Floating button that will direct to the Boiler Editor
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BoilerEditor.class);
                startActivity(intent);
            }
        });


        // TODO: finish Context Menu ACTION MODE

    }

   /* private void onListItemSelect(int position) {
        boilerAdapter.toggleSelection(position);//Toggle the selection
        boolean hasCheckedItems = boilerAdapter.getSelectedCount() > 0;//Check if any items are already selected or not
        if (hasCheckedItems && mActionMode == null)
            // there are some selected items, start the actionMode
            mActionMode = (this.startSupportActionMode(new Toolbar_ActionMode_Callback(this, null, boilerAdapter, item_models, true));
        else if (!hasCheckedItems && mActionMode != null)
            // there no selected items, finish the actionMode
            mActionMode.finish();
        if (mActionMode != null)
            //set action mode title on item selection
            mActionMode.setTitle(String.valueOf(boilerAdapter
                    .getSelectedCount()) + " selected");
    }*/

    //Set action mode null after use

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
        listView = (ListView) findViewById(R.id.main_list_view);
        boilerAdapter = new BoilerAdapter(this, cursor);
        listView.setAdapter(boilerAdapter);


        // Register the ListView for the Context Menu (shows when item is selected)

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
