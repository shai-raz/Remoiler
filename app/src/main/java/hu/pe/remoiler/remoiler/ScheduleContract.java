package hu.pe.remoiler.remoiler;


import android.net.Uri;
import android.provider.BaseColumns;

public final class ScheduleContract {

    // Uncallable Constructor
    private ScheduleContract() {}

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "hu.pe.remoiler.remoiler";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.pets/pets/ is a valid path for
     * looking at pet data. content://com.example.android.pets/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String SCHEDULE_PETS = "schedule";

    /**
     * Inner class that defines constant values for the pets database table.
     * Each entry in the table represents a single pet.
     */

    private static abstract class ScheduleEntry implements BaseColumns {
        /** The content URI to access the pet data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, SCHEDULE_PETS);

        /** Name of database table for pets */
        public final static String TABLE_NAME = "schedule";

        /**
         * Unique ID number for the pet (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Boiler's Start time
         *
         * TYPE: INTEGER
         */
        public final static String COLUMN_SCHEDULE_START_TIME = "start_time";

        /**
         * Boiler's End Time
         *
         * TYPE: INTEGER
         */
        public final static String COLUMN_SCHEDULE_END_TIME = "end_time";

        /**
         * Determines when will the event return.
         *
         // TODO: Figure out how to use it, wither as an integer, an array, or maybe create a whole different table for that.
         */
        public final static String COLUMN_SCHEDULE_RETURNS = "returns";

        /**
         * Determines if the event is active or not.
         *
         * TYPE: INTEGER
         */
        public final static String COLUMN_SCHEDULE_RETURN = "active";

    }
}
