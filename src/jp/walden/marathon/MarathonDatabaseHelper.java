package jp.walden.marathon;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class MarathonDatabaseHelper extends SQLiteOpenHelper {
	
//	private SQLiteDatabase marathonDB;

	public static final String RUNNER_TABLE = "runner";
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "marathon.db";
	
	// Column Names
	public static final String KEY_ID = "_id";
	public static final String KEY_NUMBER = "runner_number";
	public static final String KEY_NAME = "runner_name";
	
	// Column indexes
	public static final int ID_COLUMN = 0;
	public static final int NUMBER_COLUMN = 1;
	public static final int NAME_COLUMN = 2;

	private static final String DATABASE_CREATE =
			"CREATE TABLE " + RUNNER_TABLE + " ("
			+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ KEY_NUMBER + " INTEGER UNIQUE NOT NULL, "
			+ KEY_NAME + " TEXT NOT NULL);";
			
	public MarathonDatabaseHelper(Context context, String name,
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
