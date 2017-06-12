package hu.pe.remoiler.remoiler.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import hu.pe.remoiler.remoiler.data.BoilerContract.BoilerEntry;
import hu.pe.remoiler.remoiler.data.ScheduleContract.ScheduleEntry;


public class RemoilerDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = RemoilerDbHelper.class.getSimpleName();

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "remoiler";

    private static final String SQL_CREATE_SCHEDULE_ENTRIES = "CREATE TABLE " + ScheduleEntry.TABLE_NAME + "("
            + ScheduleEntry._ID + " INTEGER PRIMARY KEY,"
            + ScheduleEntry.COLUMN_SCHEDULE_BOILER_ID + " INTEGER,"
            + ScheduleEntry.COLUMN_SCHEDULE_START_TIME + " INTEGER DEFAULT 0,"
            + ScheduleEntry.COLUMN_SCHEDULE_END_TIME + " INTEGER DEFAULT 0,"
            + ScheduleEntry.COLUMN_SCHEDULE_RETURNS + " VARCHAR(15) DEFAULT [0,0,0,0,0,0,0],"
            + ScheduleEntry.COLUMN_SCHEDULE_ACTIVE + " INTEGER DEFAULT 1);";

    private static final String SQL_DELETE_SCHEDULE_ENTRIES = "DELETE TABLE IF EXISTS " + ScheduleEntry.TABLE_NAME;

    private static final String SQL_CREATE_BOILER_ENTRIES = "CREATE TABLE " + BoilerEntry.TABLE_NAME + "("
            + BoilerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + BoilerEntry.COLUMN_BOILER_NAME + " TEXT,"
            + BoilerEntry.COLUMN_BOILER_KEY + " TEXT);";

    private static final String SQL_DELETE_BOILER_ENTRIES = "DELETE TABLE IF EXISTS " + BoilerEntry.TABLE_NAME;

    public RemoilerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_BOILER_ENTRIES);
        db.execSQL(SQL_CREATE_SCHEDULE_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_SCHEDULE_ENTRIES);
        db.execSQL(SQL_DELETE_BOILER_ENTRIES);
        onCreate(db);
    }
}
