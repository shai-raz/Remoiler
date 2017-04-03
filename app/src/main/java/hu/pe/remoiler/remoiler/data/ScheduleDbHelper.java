package hu.pe.remoiler.remoiler.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import hu.pe.remoiler.remoiler.data.ScheduleContract.ScheduleEntry;

/**
 * Created by Shai on 03/04/2017.
 */

public class ScheduleDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "remoiler";

    private static final String SQL_CREATE_ENTERIES = "CREATE TABLE" + ScheduleEntry.TABLE_NAME + "("
            + ScheduleEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + ScheduleEntry.COLUMN_SCHEDULE_START_TIME + " INTEGER DEFAULT 0,"
            + ScheduleEntry.COLUMN_SCHEDULE_END_TIME + " INTEGER DEFAULT 0,"
            + ScheduleEntry.COLUMN_SCHEDULE_RETURNS + " VARCHAR 15 DEFAULT [0,0,0,0,0,0,0]"
            + ScheduleEntry.COLUMN_SCHEDULE_ACTIVE + " INTEGER DEFAULT 1);";

    private static final String SQL_DELETE_ENTRIES = "DELETE TABLE IF EXISTS " + ScheduleEntry.TABLE_NAME;

    public ScheduleDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTERIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
