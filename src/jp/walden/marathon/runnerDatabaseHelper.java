package jp.walden.marathon;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class runnerDatabaseHelper extends SQLiteOpenHelper {

	private static final String RUNNER_TABLE = "runner.db";

	// Column Names
	public static final String KEY_ID = "_id";
	public static final String KEY_RUNNER_NUMBER = "runner_number";
	public static final String KEY_NAME = "runner_name";

	private static final String DATABASE_CREATE =
			"CREATE TABLE " + RUNNER_TABLE + " ("
			+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ KEY_RUNNER_NUMBER + " INTEGER, "
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
