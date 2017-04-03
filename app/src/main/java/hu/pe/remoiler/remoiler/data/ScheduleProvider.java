package hu.pe.remoiler.remoiler.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Created by Shai on 03/04/2017.
 */

public class ScheduleProvider extends ContentProvider {

    /** URI matcher code for the content URI for the schedule table */
    private static final int SCHEDULE = 100;

    /** URI matcher code for the content URI for a single 'timer' in the schedule table */
    private static final int SCHEDULE_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        sUriMatcher.addURI(ScheduleContract.CONTENT_AUTHORITY, ScheduleContract.PATH_SCHEDULE, SCHEDULE);
        sUriMatcher.addURI(ScheduleContract.CONTENT_AUTHORITY, ScheduleContract.PATH_SCHEDULE + "/#", SCHEDULE_ID);
    }

    // Tag for the log messages
    public static final String LOG_TAG = ScheduleProvider.class.getSimpleName();

    /**
     * Database helper object.
     */
    private ScheduleDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ScheduleDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        return null;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        return null;
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}
