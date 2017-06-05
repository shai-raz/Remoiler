package hu.pe.remoiler.remoiler;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import hu.pe.remoiler.remoiler.data.BoilerContract.BoilerEntry;
import hu.pe.remoiler.remoiler.data.RemoilerDbHelper;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

    final static private String LOG_TAG = MainActivity.class.getSimpleName();

    private ListView listView;
    private BoilerAdapter boilerAdapter;
    private int nr = 0;
    private boolean isActionMode = false;
    private LoaderManager loaderManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //updateAndroidSecurityProvider(this);

        loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(1, null, this);

        populateList();
        listView.setEmptyView(findViewById(R.id.main_empty_view));
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

                // Trigger onPrepareActionMode to hide/show Edit icon
                mode.invalidate();

                // Show how many are selected
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
                if (nr > 1) {
                    menu.findItem(R.id.main_menu_edit).setVisible(false);
                    return true;
                } else if (nr == 1) {
                    menu.findItem(R.id.main_menu_edit).setVisible(true);
                    return true;
                }
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                Set<Integer> checked;
                Object[] checkedPosArr;
                Integer checkedPos;
                Cursor cursor;

                switch (item.getItemId()) {
                    case R.id.main_menu_delete: // deleting boilers
                        RemoilerDbHelper mDbHelper = new RemoilerDbHelper(MainActivity.this);
                        SQLiteDatabase db = mDbHelper.getWritableDatabase();

                        checked = boilerAdapter.getCurrentCheckedPosition();

                        for (Integer currentPos : checked) {
                            //Log.i(LOG_TAG, "current pos: " + currentPos);
                            cursor = (Cursor) listView.getAdapter().getItem(currentPos);
                            int currentID = cursor.getInt(cursor.getColumnIndex(BoilerEntry._ID));

                            // delete single row
                            db.delete(BoilerEntry.TABLE_NAME, BoilerEntry._ID + "=?", new String[] { String.valueOf(currentID) });
                        }

                        boilerAdapter.notifyDataSetChanged();

                        nr = 0;
                        boilerAdapter.clearSelection();
                        mode.finish();
                        break;

                    case R.id.main_menu_edit: // editing a single boiler
                        checked =  boilerAdapter.getCurrentCheckedPosition();
                        checkedPos = checked.iterator().next();
                        cursor = (Cursor) listView.getAdapter().getItem(checkedPos);

                        String boilerName = cursor.getString(cursor.getColumnIndex(BoilerEntry.COLUMN_BOILER_NAME));
                        int boilerID = cursor.getInt(cursor.getColumnIndex(BoilerEntry._ID));
                        String boilerKey = cursor.getString(cursor.getColumnIndex(BoilerEntry.COLUMN_BOILER_KEY));

                        Intent intent = new Intent(MainActivity.this, BoilerEditor.class);
                        intent.putExtra("boilerID", boilerID);
                        intent.putExtra("boilerName", boilerName);
                        intent.putExtra("boilerKey", boilerKey);
                        startActivity(intent);
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
                /*Cursor cursor = (Cursor) listView.getAdapter().getItem(position);
                Log.i(LOG_TAG, "boilerName: " + cursor.getString(cursor.getColumnIndex(BoilerEntry.COLUMN_BOILER_NAME)));*/
                if (isActionMode) { // If we're on Context Menu ActionMode, then clicking on an item won't intent to BoilerActivity, but will select the clicked.
                    if (nr != 0) {
                        listView.setItemChecked(position, !boilerAdapter.isPositionChecked(position));
                    }
                } else { // Clicking on item when ActionMode is off will intent to BoilerActivity
                    // Get boiler ID from Adapter for current view
                    int boilerID = (int) listView.getAdapter().getItemId(position);
                    Log.i(LOG_TAG, "boilerID: " + String.valueOf(boilerID));

                    Intent intent = new Intent(MainActivity.this, BoilerActivity.class);
                    intent.putExtra("boilerID", boilerID);
                    startActivity(intent);
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

        //db.close();


        // Register the ListView for the Context Menu (shows when item is selected)

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

    /*
    Loader methods
     */
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Log.i(LOG_TAG, "onCreateLoader()");
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        Log.i(LOG_TAG, "onLoadFinished()");
        boilerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader loader) {
        Log.i(LOG_TAG, "onLoaderReset()");
        boilerAdapter.notifyDataSetChanged();
        loaderManager.destroyLoader(1);
        loaderManager.initLoader(1, null, this);
    }

    private void updateAndroidSecurityProvider(Activity callingActivity) {
        try {
            ProviderInstaller.installIfNeeded(this);
        } catch (GooglePlayServicesRepairableException e) {
            // Thrown when Google Play Services is not installed, up-to-date, or enabled
            // Show dialog to allow users to install, update, or otherwise enable Google Play services.
            GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), callingActivity, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e("SecurityException", "Google Play Services not available.");
        }
    }

}
