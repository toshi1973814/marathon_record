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

	public static final Uri RUNNER_URI = Uri.parse("content://jp.walden.provider.marathon/runners");
	private static final int RUNNERS = 1;
	private static final int RUNNER_ID = 2;
	private static final UriMatcher uriMatcher;
	
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI("jp.walden.provider.marathon", "runners", RUNNERS);
		uriMatcher.addURI("jp.walden.provider.marathon", "runners/#", RUNNER_ID);
	}
	
	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		Context context = getContext();
		runnerDatabaseHelper dbHelper;
		dbHelper = new runnerDatabaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
		runnerDB = dbHelper.getWritableDatabase();
		return (runnerDB == null) ? false : true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sort) {
		// TODO Auto-generated method stub
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(RUNNER_TABLE);
		switch(uriMatcher.match(uri)) {
		case RUNNER_ID: qb.appendWhere(KEY_ID + "=" + uri.getPathSegments().get(1));
			break;
		default: break;
		}
		
		String orderBy;
		if (TextUtils.isEmpty(sort)) {
			orderBy = KEY_NAME;
		} else {
			orderBy = sort;
		}
		
		Cursor c = qb.query(runnerDB, projection, selection, selectionArgs, null, null, orderBy);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		switch (uriMatcher.match(uri)) {
			case RUNNERS: return "vnd.android.cursor.dir/vnd.walden.marathon.runner";
			case RUNNER_ID: return "vnd.android.cursor.dir/vnd.walden.marathon.runner";
			default: throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	@Override
	public Uri insert(Uri _uri, ContentValues values) {
		// TODO Auto-generated method stub
		long rowID = runnerDB.insert(RUNNER_TABLE, "dummy", values);
		if(rowID > 0) {
			Uri uri = ContentUris.withAppendedId(RUNNER_URI, rowID);
			getContext().getContentResolver().notifyChange(uri, null);
			return uri;
		}
		throw new SQLException("Fail to insert row into " + _uri);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private SQLiteDatabase runnerDB;

	private static final String RUNNER_TABLE = "runner";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "runner.db";
	
	// Column Names
	public static final String KEY_ID = "_id";
	public static final String KEY_NUMBER = "runner_number";
	public static final String KEY_NAME = "runner_name";
	
	// Column indexes
	public static final int NUMBER_COLUMN = 1;
	public static final int NAME_COLUMN = 2;
	
	public class runnerDatabaseHelper extends SQLiteOpenHelper {

		private static final String DATABASE_CREATE =
				"CREATE TABLE " + RUNNER_TABLE + " ("
				+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ KEY_NUMBER + " INTEGER, "
				+ KEY_NAME + " TEXT);";
				
		public runnerDatabaseHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(DATABASE_CREATE);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub

		}

	}
}
