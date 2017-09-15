package mobilesecurity.mobileone;

import android.provider.BaseColumns;

class RecordsContract {

    private RecordsContract() {}

    static class RecordEntry implements BaseColumns {
        static final String TABLE_NAME = "record";
        static final String COLUMN_NAME_NAME = "name";
        static final String COLUMN_NAME_DATE = "date";
        static final String COLUMN_NAME_TIME = "time";
        static final String COLUMN_NAME_GPS_LONG = "long";
        static final String COLUMN_NAME_GPS_LAT = "lat";
        static final String COLUMN_NAME_GPS_ALT = "alt";
    }
    static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + RecordEntry.TABLE_NAME + " (" +
                    RecordEntry._ID + " INTEGER PRIMARY KEY," +
                    RecordEntry.COLUMN_NAME_NAME + " TEXT," +
                    RecordEntry.COLUMN_NAME_DATE + " TEXT," +
                    RecordEntry.COLUMN_NAME_TIME + " INT," +
                    RecordEntry.COLUMN_NAME_GPS_LONG + " DOUBLE," +
                    RecordEntry.COLUMN_NAME_GPS_LAT + " DOUBLE," +
                    RecordEntry.COLUMN_NAME_GPS_ALT + " DOUBLE)";

    static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + RecordEntry.TABLE_NAME;
}
