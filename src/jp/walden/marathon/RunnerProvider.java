package jp.walden.marathon;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class RunnerProvider extends ContentProvider {

	public static final Uri RUNNER_URI = Uri.parse("content://jp.walden.provider.runner/runners");
	private static final int RUNNERS = 1;
	private static final int RUNNER_ID = 2;
	private static final UriMatcher uriMatcher;
	private SQLiteDatabase marathonDB;
	
	// Column Names
	public static final String KEY_ID = MarathonDatabaseHelper.KEY_ID;
	public static final String KEY_NUMBER= MarathonDatabaseHelper.KEY_NUMBER;
	public static final String KEY_NAME= MarathonDatabaseHelper.KEY_NAME;
	
	// Column indexes
	public static final int ID_COLUMN = MarathonDatabaseHelper.ID_COLUMN;
	public static final int NUMBER_COLUMN = MarathonDatabaseHelper.NUMBER_COLUMN;
	public static final int NAME_COLUMN = MarathonDatabaseHelper.NAME_COLUMN;
	
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI("jp.walden.provider.runner", "runners", RUNNERS);
		uriMatcher.addURI("jp.walden.provider.runner", "runners/#", RUNNER_ID);
	}
	
	@Override
	public boolean onCreate() {
		Context context = getContext();
		MarathonDatabaseHelper dbHelper;
		dbHelper = new MarathonDatabaseHelper(context, MarathonDatabaseHelper.DATABASE_NAME, null, MarathonDatabaseHelper.DATABASE_VERSION);
		marathonDB = dbHelper.getWritableDatabase();
		return (marathonDB == null) ? false : true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sort) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(MarathonDatabaseHelper.RUNNER_TABLE);
		switch(uriMatcher.match(uri)) {
		case RUNNER_ID: qb.appendWhere(MarathonDatabaseHelper.KEY_ID + "=" + uri.getPathSegments().get(1));
			break;
		default: break;
		}
		
		String orderBy;
		if (TextUtils.isEmpty(sort)) {
			orderBy = MarathonDatabaseHelper.KEY_NUMBER;
		} else {
			orderBy = sort;
		}
		
		Cursor c = qb.query(marathonDB, projection, selection, selectionArgs, null, null, orderBy);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
			case RUNNERS: return "vnd.android.cursor.dir/vnd.walden.marathon.runner";
			case RUNNER_ID: return "vnd.android.cursor.dir/vnd.walden.marathon.runner";
			default: throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	@Override
	public Uri insert(Uri _uri, ContentValues values) {
		long rowID = marathonDB.insert(MarathonDatabaseHelper.RUNNER_TABLE, "dummy", values);
		if(rowID > 0) {
			Uri uri = ContentUris.withAppendedId(RUNNER_URI, rowID);
			getContext().getContentResolver().notifyChange(uri, null);
			return uri;
		}
		throw new SQLException("Fail to insert row into " + _uri);
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		int count;
		switch(uriMatcher.match(uri)) {
		case RUNNERS:
			count = marathonDB.delete(MarathonDatabaseHelper.RUNNER_TABLE, where, whereArgs);
			break;
		case RUNNER_ID:
			String segment = uri.getPathSegments().get(1);
			count = marathonDB.delete(MarathonDatabaseHelper.RUNNER_TABLE, MarathonDatabaseHelper.KEY_ID + "=" + segment 
					+ (!TextUtils.isEmpty(where) ? " AND ("
					+ where + ")" : "")
					, whereArgs);
			break;
		default: throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}
	
//	private SQLiteDatabase marathonDB;
//
//	private static final String RUNNER_TABLE = "runner";
//	private static final int DATABASE_VERSION = 1;
//	private static final String DATABASE_NAME = "runner.db";
//	
//	// Column Names
//	public static final String KEY_ID = "_id";
//	public static final String KEY_NUMBER = "runner_number";
//	public static final String KEY_NAME = "runner_name";
//	
//	// Column indexes
//	public static final int ID_COLUMN = 0;
//	public static final int NUMBER_COLUMN = 1;
//	public static final int NAME_COLUMN = 2;
//	
//	public class runnerDatabaseHelper extends SQLiteOpenHelper {
//
//		private static final String DATABASE_CREATE =
//				"CREATE TABLE " + RUNNER_TABLE + " ("
//				+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
//				+ KEY_NUMBER + " INTEGER UNIQUE NOT NULL, "
//				+ KEY_NAME + " TEXT NOT NULL);";
//				
//		public runnerDatabaseHelper(Context context, String name,
//				CursorFactory factory, int version) {
//			super(context, name, factory, version);
//			// 
//		}
//
//		@Override
//		public void onCreate(SQLiteDatabase db) {
//			// 
//			db.execSQL(DATABASE_CREATE);
//
//		}
//
//		@Override
//		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//			// 
//
//		}
//
//	}
}
