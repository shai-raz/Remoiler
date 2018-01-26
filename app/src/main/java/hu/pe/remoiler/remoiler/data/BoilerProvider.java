package hu.pe.remoiler.remoiler.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import hu.pe.remoiler.remoiler.data.BoilerContract.BoilerEntry;

public class BoilerProvider extends ContentProvider {
    /** URI matcher code for the content URI for the boiler table */
    private static final int BOILER = 200;

    /** URI matcher code for the content URI for a single 'boiler' in the boiler table */
    private static final int BOILER_ID = 201;

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

        sUriMatcher.addURI(BoilerContract.CONTENT_AUTHORITY, BoilerContract.PATH_BOILER, BOILER);
        sUriMatcher.addURI(BoilerContract.CONTENT_AUTHORITY, BoilerContract.PATH_BOILER + "/#", BOILER_ID);
    }

    // Tag for the log messages
    public static final String LOG_TAG = BoilerProvider.class.getSimpleName();

    /**
     * Database helper object.
     */
    private RemoilerDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new RemoilerDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        // Create a readable db object
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOILER:
                cursor = db.query(BoilerEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case BOILER_ID:
                selection = BoilerEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = db.query(BoilerEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query, unknown URI: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOILER:
                return insertBoiler(uri, contentValues);

            default:
                throw new IllegalArgumentException("Cannot insert, unknown URI: " + uri);
        }
    }

    public Uri insertBoiler(Uri uri, ContentValues values) {
        // Create a writable db object
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Insert the new boiler
        long id = db.insert(BoilerEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the boiler Content Uri
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row)
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOILER:
                return updateBoiler(uri, contentValues, selection, selectionArgs);

            case BOILER_ID:
                selection = BoilerEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateBoiler(uri, contentValues, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Cannot insert, unknown URI: " + uri);
        }
    }

    public int updateBoiler(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Create a writable db object
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = db.update(BoilerEntry.TABLE_NAME, values, selection, selectionArgs);

        // If more than 1 rows were updates...
        if (rowsUpdated != 0) {
            // Notify all listeners that the data has changed for the pet Content URI
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Returns the number of database rows affected by the update statement
        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOILER:
                // Delete all rows that match the selection and selection args
                rowsDeleted = db.delete(BoilerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOILER_ID:
                // Delete a single row given by the ID in the URI
                selection = BoilerEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                rowsDeleted = db.delete(BoilerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Cannot insert, unknown URI: " + uri);
        }

        // If more than 1 rows were deleted...
        if (rowsDeleted != 0) {
            // Notify all listeners that the data has changed for the pet Content URI
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return how many rows got deleted
        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOILER:
                return BoilerEntry.CONTENT_LIST_TYPE;
            case BOILER_ID:
                return BoilerEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
