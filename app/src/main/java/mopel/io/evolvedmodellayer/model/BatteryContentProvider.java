package mopel.io.evolvedmodellayer.model;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import org.threeten.bp.LocalDateTime;

import java.util.Arrays;
import java.util.HashSet;

import mopel.io.BatteryModel;

public class BatteryContentProvider extends ContentProvider {
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/batteries";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/battery";
    // used for the UriMacher
    private static final int Batteries = 10;
    private static final String AUTHORITY = "io.amosbake.battery";
    private static final String BASE_PATH = Battery.TABLE_NAME;
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, Batteries);
    }

    private DbHelper db;

    public BatteryContentProvider() {
    }

    @Override
    public boolean onCreate() {
        db = DbHelper.getInstance(getContext());
        return false;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = db.getWritableDatabase();
        long id ;
        switch (uriType) {
            case Batteries:
                id = sqlDB.insert(Battery.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        // Uisng SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // check if the caller has requested a column which does not exists
        checkColumns(projection);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case Batteries:
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        SQLiteDatabase database = db.getReadableDatabase();
        queryBuilder.setTables(Battery.TABLE_NAME);
        Cursor cursor =
                queryBuilder.query(database, projection, selection, selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void checkColumns(String[] projection) {
        String[] available = {
                BatteryModel.ID, BatteryModel.HEALTH, BatteryModel.CREATED_AT
        };
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }

    public static class DbHelper extends SQLiteOpenHelper {
        private static final int VERSION = 1;
        private static final String DB_NAME = "powersaver";

        private static DbHelper instance;

        public DbHelper(Context context) {
            super(context, DB_NAME, null, VERSION);
        }

        public static DbHelper getInstance(Context context) {
            if (instance == null) {
                instance = new DbHelper(context);
            }
            return instance;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(Battery.CREATE_TABLE);
            BatteryModel.Insert_battery insertBattery =
                    new BatteryModel.Insert_battery(db, Battery.BATTERY_FACTORY);
            insertBattery.bind(100, LocalDateTime.of(2017, 1, 10, 5, 30));
            insertBattery.program.executeInsert();
            insertBattery.bind(90, LocalDateTime.of(2017, 1, 10, 6, 30));
            insertBattery.program.executeInsert();
            insertBattery.bind(70, LocalDateTime.of(2017, 1, 10, 7, 30));
            insertBattery.program.executeInsert();
            insertBattery.bind(50, LocalDateTime.of(2017, 1, 10, 8, 30));
            insertBattery.program.executeInsert();
            insertBattery.bind(30, LocalDateTime.of(2017, 1, 10, 9, 30));
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
