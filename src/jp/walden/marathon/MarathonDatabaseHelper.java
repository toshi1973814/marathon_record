package jp.walden.marathon;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class MarathonDatabaseHelper extends SQLiteOpenHelper {
	
//	private SQLiteDatabase marathonDB;

	public static final String RUNNER_TABLE = "runner";
	public static final String RUNNING_RECORD_TABLE = "runner_record";
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "marathon.db";
	
	// Column Names
	public static final String KEY_ID = "_id";
	public static final String KEY_NUMBER = "runner_number";
	public static final String KEY_NAME = "runner_name";
	public static final String KEY_CREATED_AT = "runner_created_at";
	
	// Column indexes
	public static final int ID_COLUMN = 0;
	public static final int NUMBER_COLUMN = 1;
	public static final int NAME_COLUMN = 2;
	public static final int CREATED_AT_COLUMN = 3;
	
	// Column Names
	public static final String RUNNING_RECORD_KEY_ID = "_id";
	public static final String RUNNING_RECORD_KEY_NUMBER = "running_record_number";
	public static final String RUNNING_RECORD_KEY_DATE = "running_record_date";
	public static final String RUNNING_RECORD_KEY_DISTANCE = "running_record_distance";
	public static final String RUNNING_RECORD_KEY_RANKING = "running_record_ranking";
	public static final String RUNNING_RECORD_KEY_TOTAL = "running_record_total";
	public static final String RUNNING_RECORD_KEY_TIME = "running_record_time";
	public static final String RUNNING_RECORD_KEY_LINE = "running_record_line";
	public static final String RUNNING_RECORD_KEY_CREATED_AT = "running_record_created_at";

	// Column indexes
	public static final int RUNNING_RECORD_ID_COLUMN = 0;
	public static final int RUNNING_RECORD_NUMBER_COLUMN = 1;
	public static final int RUNNING_RECORD_DATE_COLUMN = 2;
	public static final int RUNNING_RECORD_DISTANCE_COLUMN = 3;
	public static final int RUNNING_RECORD_RANKING_COLUMN = 4;
	public static final int RUNNING_RECORD_TOTAL_COLUMN = 5;
	public static final int RUNNING_RECORD_TIME_COLUMN = 6;
	public static final int RUNNING_RECORD_LINE_COLUMN = 7;
	public static final int RUNNING_RECORD_CREATED_AT_COLUMN = 8;

	private static final String DATABASE_CREATE =
			"CREATE TABLE " + RUNNER_TABLE + " ("
			+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ KEY_NUMBER + " INTEGER UNIQUE NOT NULL, "
			+ KEY_NAME + " TEXT NOT NULL, "
			+ KEY_CREATED_AT + " DATE NOT NULL);";

	private static final String CREATE_RUNNING_RECORD =
			"CREATE TABLE " + RUNNING_RECORD_TABLE + " ("
			+ RUNNING_RECORD_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ RUNNING_RECORD_KEY_NUMBER + " INTEGER, "
			+ RUNNING_RECORD_KEY_DATE + " DATE NOT NULL, "
			+ RUNNING_RECORD_KEY_DISTANCE + " TEXT NOT NULL, "
			+ RUNNING_RECORD_KEY_RANKING + " INTEGER, "
			+ RUNNING_RECORD_KEY_TOTAL + " INTEGER, "
			+ RUNNING_RECORD_KEY_TIME + " TEXT, "
			+ RUNNING_RECORD_KEY_LINE + " INTEGER, "
			+ RUNNING_RECORD_KEY_CREATED_AT + " DATE NOT NULL, "
			+ "UNIQUE("
			+ RUNNING_RECORD_KEY_NUMBER + ", "
			+ RUNNING_RECORD_KEY_DATE + ", "
			+ RUNNING_RECORD_KEY_DISTANCE + ") ON CONFLICT ROLLBACK);";
			
	public MarathonDatabaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
		db.execSQL(CREATE_RUNNING_RECORD);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
