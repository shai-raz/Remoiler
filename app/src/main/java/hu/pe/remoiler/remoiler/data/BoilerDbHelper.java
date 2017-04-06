package hu.pe.remoiler.remoiler.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import hu.pe.remoiler.remoiler.data.BoilerContract.BoilerEntry;

public class BoilerDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "remoiler";

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + BoilerEntry.TABLE_NAME + "("
            + BoilerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + BoilerEntry.COLUMN_BOILER_NAME + " TEXT,"
            + BoilerEntry.COLUMN_BOILER_KEY + " TEXT);";

    private static final String SQL_DELETE_ENTRIES = "DELETE TABLE IF EXISTS " + BoilerEntry.TABLE_NAME;

    public BoilerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
