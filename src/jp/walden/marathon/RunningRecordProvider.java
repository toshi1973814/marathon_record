package jp.walden.marathon;

import java.util.ArrayList;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class RunningRecordProvider extends ContentProvider {

	public static final Uri RUNNING_RECORD_URI = Uri.parse("content://jp.walden.provider.running_record/running_records");
	private static final int RUNNING_RECORDS = 1;
	private static final int RUNNING_RECORD_ID = 2;
	private static final UriMatcher uriMatcher;
	private SQLiteDatabase marathonDB;
	
	// Column Names;
	public static final String RUNNING_RECORD_KEY_ID = MarathonDatabaseHelper.RUNNING_RECORD_KEY_ID;
	public static final String RUNNING_RECORD_KEY_NUMBER = MarathonDatabaseHelper.RUNNING_RECORD_KEY_NUMBER;
	public static final String RUNNING_RECORD_KEY_DATE = MarathonDatabaseHelper.RUNNING_RECORD_KEY_DATE;
	public static final String RUNNING_RECORD_KEY_DISTANCE = MarathonDatabaseHelper.RUNNING_RECORD_KEY_DISTANCE;
	public static final String RUNNING_RECORD_KEY_RANKING = MarathonDatabaseHelper.RUNNING_RECORD_KEY_RANKING;
	public static final String RUNNING_RECORD_KEY_TOTAL = MarathonDatabaseHelper.RUNNING_RECORD_KEY_TOTAL;
	public static final String RUNNING_RECORD_KEY_TIME = MarathonDatabaseHelper.RUNNING_RECORD_KEY_TIME;
	public static final String RUNNING_RECORD_KEY_LINE = MarathonDatabaseHelper.RUNNING_RECORD_KEY_LINE;
	public static final String RUNNING_RECORD_KEY_CREATED_AT = MarathonDatabaseHelper.RUNNING_RECORD_KEY_CREATED_AT;

	// Column indexes;
	public static final int RUNNING_RECORD_ID_COLUMN = MarathonDatabaseHelper.RUNNING_RECORD_ID_COLUMN;
	public static final int RUNNING_RECORD_NUMBER_COLUMN = MarathonDatabaseHelper.RUNNING_RECORD_NUMBER_COLUMN;
	public static final int RUNNING_RECORD_DATE_COLUMN = MarathonDatabaseHelper.RUNNING_RECORD_DATE_COLUMN;
	public static final int RUNNING_RECORD_DISTANCE_COLUMN = MarathonDatabaseHelper.RUNNING_RECORD_DISTANCE_COLUMN;
	public static final int RUNNING_RECORD_RANKING_COLUMN = MarathonDatabaseHelper.RUNNING_RECORD_RANKING_COLUMN;
	public static final int RUNNING_RECORD_TOTAL_COLUMN = MarathonDatabaseHelper.RUNNING_RECORD_TOTAL_COLUMN;
	public static final int RUNNING_RECORD_TIME_COLUMN = MarathonDatabaseHelper.RUNNING_RECORD_TIME_COLUMN;
	public static final int RUNNING_RECORD_LINE_COLUMN = MarathonDatabaseHelper.RUNNING_RECORD_LINE_COLUMN;
	public static final int RUNNING_RECORD_CREATED_AT_COLUMN = MarathonDatabaseHelper.RUNNING_RECORD_CREATED_AT_COLUMN;
	
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI("jp.walden.provider.running_record", "running_records", RUNNING_RECORDS);
		uriMatcher.addURI("jp.walden.provider.running_record", "running_records/#", RUNNING_RECORD_ID);
	}

	private static final String TAG = "RunnerProvider";

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
		qb.setTables(MarathonDatabaseHelper.RUNNING_RECORD_TABLE);
		switch(uriMatcher.match(uri)) {
		case RUNNING_RECORD_ID: qb.appendWhere(MarathonDatabaseHelper.RUNNING_RECORD_KEY_ID + "=" + uri.getPathSegments().get(1));
			break;
		default: break;
		}
		
		String orderBy;
		if (TextUtils.isEmpty(sort)) {
			orderBy = MarathonDatabaseHelper.RUNNING_RECORD_DATE_COLUMN + " DESC";
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
		case RUNNING_RECORDS: return "vnd.android.cursor.dir/vnd.walden.marathon.running_record";
		case RUNNING_RECORD_ID: return "vnd.android.cursor.dir/vnd.walden.marathon.running_record";
		default: throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	@Override
	public Uri insert(Uri _uri, ContentValues values) {
		long rowID = marathonDB.insert(MarathonDatabaseHelper.RUNNING_RECORD_TABLE, "dummy", values);
		if(rowID > 0) {
			Uri uri = ContentUris.withAppendedId(RUNNING_RECORD_URI, rowID);
			getContext().getContentResolver().notifyChange(uri, null);
			return uri;
		}
		throw new SQLException("Fail to insert row into " + _uri);
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		int count;
		switch(uriMatcher.match(uri)) {
		case RUNNING_RECORDS:
			count = marathonDB.delete(MarathonDatabaseHelper.RUNNING_RECORD_TABLE, where, whereArgs);
			break;
		case RUNNING_RECORD_ID:
			String segment = uri.getPathSegments().get(1);
			count = marathonDB.delete(MarathonDatabaseHelper.RUNNING_RECORD_TABLE, MarathonDatabaseHelper.KEY_ID + "=" + segment 
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

	@Override
	public ContentProviderResult[] applyBatch(
			ArrayList<ContentProviderOperation> operations)
			throws OperationApplicationException {
		marathonDB.beginTransaction();
		try {
			ContentProviderResult[] cpr = super.applyBatch(operations);
			marathonDB.setTransactionSuccessful();
			return cpr;
		} catch (OperationApplicationException e) {
			e.printStackTrace();
			Log.v(TAG, "failed to insert runner_records.");
			throw e;
		} finally {
			marathonDB.endTransaction();
		}
	}

}
