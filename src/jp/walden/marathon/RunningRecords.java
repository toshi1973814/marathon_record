package jp.walden.marathon;

//import java.net.URI;
//import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
//import android.content.OperationApplicationException;
//import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
//import android.os.RemoteException;
//import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

public class RunningRecords extends Activity {

	private ContentResolver cr;
	private Resources res;
	private ArrayList<RunningRecord> runningRecords = new ArrayList<RunningRecord>();
//	private ArrayAdapter<RunningRecord> aa;
	private RunningRecordAdapter aa;

	private static final String QUERY_RESULT_FORMAT = "yyyy-MM-dd";

	private int runnerNumber;
	private SimpleDateFormat queryResultFormatter = new SimpleDateFormat(QUERY_RESULT_FORMAT);
	private int distance;
//	private String date;
//	private String dateFormat;
	private String runnerIdString;
	
	ArrayList<ContentProviderOperation> ops;
	String runnerName = "";
//	private static final String TAG = "RunningRecords";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
//		String lastRunningRecordDateString = "";
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.running_record);
		TextView runningRecordPageHeader = (TextView)findViewById(R.id.running_record_page_header);
		ListView runningRecordListView = (ListView)findViewById(R.id.running_record);
//		aa = new ArrayAdapter<RunningRecord>(getApplicationContext(), android.R.layout.simple_list_item_1, runningRecords);

		aa = new RunningRecordAdapter(getApplicationContext(), R.layout.running_record_entry, runningRecords);
		runningRecordListView.setAdapter(aa);

        res = getResources();
		
		getExtraFromIntent();

		// Intentから受け取ったidをキーにランナー情報をdbから取得
		Cursor cursor = getCursorFromRunnerProvider();
        cursor.moveToFirst();
		runnerNumber = cursor.getInt(RunnerProvider.NUMBER_COLUMN);
		
		// ページヘッダを設定
		setPageHeader(cursor, runningRecordPageHeader);
		cursor.close();
//		
//		// データベースから最新のレコードを取得
//		Cursor lastRunningRecordCursor = getLastRunningRecordCursor();
//		
//  		try {
//
//  			// 最新の日付を現在日付と比較
//  			
//  			// 現在の日付を取得
//  	  		SimpleDateFormat formatter= 
//  	  				new SimpleDateFormat(dateFormat);
//			Date currentDate = formatter.parse(date);
//			int currentYear = currentDate.getYear();
//			int currentMonth = currentDate.getMonth();
//			Date firstDateOfCurrentMonth = new Date(currentYear, currentMonth, 1, 0, 0, 0);
//
//  	  		queryResultFormatter= 
//  	  				new SimpleDateFormat(QUERY_RESULT_FORMAT);
//
//  	  		Date lastRunningRecordDate;
//			if(0 < lastRunningRecordCursor.getCount()) {
//  				lastRunningRecordCursor.moveToFirst();
//  				lastRunningRecordDateString = lastRunningRecordCursor.getString(RunningRecordProvider.RUNNING_RECORD_DATE_COLUMN);
//  				lastRunningRecordDate = queryResultFormatter.parse(lastRunningRecordDateString);
//  			} else {
//  				// レコードがない場合は、デフォルトの最大過去月 + 1をlastRunningRecordDateStringとみなす
//  				// TODO デフォルトの最大過去月を設定画面で設定できるようにする
//  				Integer maxPastMonth = getMonthsToGetData();
////  				SharedPreferences prefs = getSharedPreferences(Preferences.USER_PREFERENCE, Activity.MODE_PRIVATE);
////  				int monthsToGetDataIndex = prefs.getInt(PREF_MONTHS_TO_GET_DATA, PREF_MONTHS_TO_GET_DATA_DEFAULT);
////  				int maxPastMonth = 6;
//  				lastRunningRecordDate = new Date(currentYear, currentMonth - (maxPastMonth + 1) , 1, 0, 0, 0);
//  			}
//			
//			//　最新の日付を取得
//			int lastRunningRecordYear = lastRunningRecordDate.getYear();
//			int lastRunningRecordMonth = lastRunningRecordDate.getMonth();
//			Date firstDateOfLastRunningRecordMonth = new Date(lastRunningRecordYear, lastRunningRecordMonth, 1, 0, 0, 0);
//			
//			// 最新の日付が現在月より小さい場合
//			if(firstDateOfLastRunningRecordMonth.before(firstDateOfCurrentMonth)) {
//				
//				// 最新のレコードの翌月分を取得するため、Dateの月を加算
//				int intYear = firstDateOfLastRunningRecordMonth.getYear();
//				int intMonth = firstDateOfLastRunningRecordMonth.getMonth();
//				Date firstDateOfForLoopMonth = new Date(intYear, intMonth + 1, 1, 0, 0, 0);
//				
////				// Dateの月を加算した日付データを再取得
////				int forLoopYear = firstDateOfForLoopMonth.getYear();
////				int forLoopMonth = firstDateOfForLoopMonth.getMonth();
////				Calendar calendar = new GregorianCalendar();
////				calendar.setTime(firstDateOfForLoopMonth);
////				String forLoopYearUrlString = String.valueOf(calendar.get(Calendar.YEAR));
////				String forLoopMonthUrlString = String.valueOf(calendar.get(Calendar.MONTH));
//				
//				if(firstDateOfForLoopMonth.before(firstDateOfCurrentMonth)) {
//					
////					String runningRecordUrl = "";
////					URI httpUri;
////					do {
////						int forLoopYear = firstDateOfForLoopMonth.getYear();
////						int forLoopMonth = firstDateOfForLoopMonth.getMonth();
////						String forLoopYearUrlString = String.valueOf(forLoopYear + 1900);
////						String forLoopMonthUrlString = String.format("%02d", forLoopMonth + 1);
////
////						// URLを生成
////						runningRecordUrl = 
////								String.format(res.getString(R.string.running_record_url_pattern),
////										forLoopYearUrlString, forLoopYearUrlString + forLoopMonthUrlString);
////						httpUri = new URI(runningRecordUrl);
////						HttpRunningRecord httpRunningRecord = new HttpRunningRecord(httpUri);
////						
////						// TODO トランザクションで囲む
////						ops = new ArrayList<ContentProviderOperation>();
////
////				        httpRunningRecord.extract(String.valueOf(runnerNumber), this);
////
////				        // ひと月分の解析が完了したので、トランザクションを確定
////				        try {
////				        	// TODO 該当データがある場合は、applyBatchを実行
////				        	//　そうでない場合は、空レコードを作成して挿入
////							cr.applyBatch("jp.walden.provider.running_record", ops);
////						} catch (RemoteException e) {
////							e.printStackTrace();
////						} catch (OperationApplicationException e) {
////							e.printStackTrace();
////						}
////						
////						// 取得したら、比較対象月を加算
////						forLoopYear = firstDateOfForLoopMonth.getYear();
////						forLoopMonth = firstDateOfForLoopMonth.getMonth();
////						firstDateOfForLoopMonth = new Date(forLoopYear, forLoopMonth + 1, 1, 0, 0, 0);
//////						calendar.setTime(firstDateOfForLoopMonth);
//////						forLoopYearUrlString = String.valueOf(calendar.get(Calendar.YEAR));
//////						forLoopMonthUrlString = String.valueOf(calendar.get(Calendar.MONTH));
//////						forLoopYearUrlString = String.valueOf(forLoopYear + 1900);
//////						forLoopMonthUrlString = String.format("%02d", forLoopMonth + 1);
////					} while (firstDateOfForLoopMonth.before(firstDateOfCurrentMonth));
//				}
//			}
			
			// 画面表示のための記録を取得
			Cursor runningRecordsCursor = getRunningRecordsCursor();

		    if (runningRecordsCursor.moveToFirst()) {
		        do { 
		        	addRunningRecordToArray(runningRecordsCursor);
		        } while(runningRecordsCursor.moveToNext());
		      }
//		    runningRecordsCursor.close();

//		} catch (ParseException e1) {
//			e1.printStackTrace();
////		} catch (URISyntaxException e) {
////			e.printStackTrace();
//		}
	}
//
//	private Integer getMonthsToGetData() {
//		SharedPreferences prefs = getSharedPreferences(Preferences.USER_PREFERENCE, Activity.MODE_PRIVATE);
//		int monthsToGetDataIndex = prefs.getInt(Preferences.PREF_MONTHS_TO_GET_DATA, Preferences.PREF_MONTHS_TO_GET_DATA_DEFAULT);
//	    Resources r = getResources();
//	    // Get the option values from the arrays.
//	    String[] monthsToGetDataArray = r.getStringArray(R.array.preference_months_to_get);
//	    String monthsToGetDataString = monthsToGetDataArray[monthsToGetDataIndex];
//		Integer monthsToGetData;
//	    if(!monthsToGetDataString.equals(getString(R.string.array_item_all))) {
//	    	monthsToGetData = Integer.valueOf(monthsToGetDataString);
//	    } else {
//	    	monthsToGetData = 0;
//	    }
//		return monthsToGetData;
//	}

	private void getExtraFromIntent() {
		long runnerId = getIntent().getExtras().getLong("runnerId");
		distance = getIntent().getExtras().getInt("distance");
//		date = getIntent().getExtras().getString("date");
//		dateFormat = getIntent().getExtras().getString("dateFormat");
        runnerIdString = String.valueOf(runnerId);
	}
	

	private Cursor getCursorFromRunnerProvider() {
        Uri uri = Uri.parse(RunnerProvider.RUNNER_URI + "/" + runnerIdString);
        cr = getContentResolver();
        Cursor cursor = cr.query(uri, null, null, null, null);
        startManagingCursor(cursor);
        return cursor;
	}

	private void setPageHeader(Cursor cursor, TextView runningRecordPageHeader) {
		runnerName = cursor.getString(RunnerProvider.NAME_COLUMN);
		String title = String.format(res.getString(R.string.running_record_page_header), runnerName);
		runningRecordPageHeader.setText(title);
	}
	
	public String getRunnerName() {
		return runnerName;
	}
//
//	private Cursor getLastRunningRecordCursor() {
//		StringBuffer sb = new StringBuffer();
//		sb.append
//		(RunningRecordProvider.RUNNING_RECORD_KEY_NUMBER + "=" + String.valueOf(runnerNumber));
//		String where = sb.toString();
//		String orderBy = MarathonDatabaseHelper.RUNNING_RECORD_KEY_DATE + " DESC";
//		Cursor lastRunningRecordCursor = cr.query(RunningRecordProvider.RUNNING_RECORD_URI, null, where, null, orderBy);
//		startManagingCursor(lastRunningRecordCursor);
//		return lastRunningRecordCursor;
//	}

	private Cursor getRunningRecordsCursor() {
		StringBuffer sb = new StringBuffer();
		sb.delete(0, sb.length());
		sb.append
		(RunningRecordProvider.RUNNING_RECORD_KEY_NUMBER + "=" + String.valueOf(runnerNumber));
		sb.append
		(" and " + RunningRecordProvider.RUNNING_RECORD_KEY_DISTANCE + " LIKE '%" + String.valueOf(distance) + "km%'");
//		sb.append
//		(" and " + RunningRecordProvider.RUNNING_RECORD_KEY_DISTANCE + " REGEXP '" + String.valueOf(distance) + "km'");
		String where = sb.toString();
		String orderBy = MarathonDatabaseHelper.RUNNING_RECORD_KEY_DATE + " DESC";
        cr = getContentResolver();
		Cursor runningRecordsCursor = cr.query(RunningRecordProvider.RUNNING_RECORD_URI, null, where, null, orderBy);
		startManagingCursor(runningRecordsCursor);
		return runningRecordsCursor;
	}


	private void addRunningRecordToArray(Cursor runningRecordsCursor) {
		String dateString = runningRecordsCursor.getString(RunningRecordProvider.RUNNING_RECORD_DATE_COLUMN);
		Date date = null;
		try {
			date = queryResultFormatter.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String distance = runningRecordsCursor.getString(RunningRecordProvider.RUNNING_RECORD_DISTANCE_COLUMN);
      	Integer ranking = runningRecordsCursor.getInt(RunningRecordProvider.RUNNING_RECORD_RANKING_COLUMN);
      	Integer total = runningRecordsCursor.getInt(RunningRecordProvider.RUNNING_RECORD_TOTAL_COLUMN);
      	String time = runningRecordsCursor.getString(RunningRecordProvider.RUNNING_RECORD_TIME_COLUMN);

      	RunningRecord runningRecord =
      		new RunningRecord(Integer.valueOf(runnerNumber), date, distance, ranking, total, time);
      	runningRecords.add(runningRecord);
		aa.notifyDataSetChanged();
		
	}

//	public void addRunningRecord(RunningRecord runningRecord) {
////    	ContentResolver cr = getContentResolver();
////    	ContentValues values = new ContentValues();
////    	values.put(RunningRecordProvider.RUNNING_RECORD_KEY_NUMBER, runningRecord.getRunnerNumber());
////    	values.put(RunningRecordProvider.RUNNING_RECORD_KEY_DATE, runningRecord.getDate().toString());
////    	values.put(RunningRecordProvider.RUNNING_RECORD_KEY_DISTANCE, runningRecord.getDistance());
////    	values.put(RunningRecordProvider.RUNNING_RECORD_KEY_RANKING, runningRecord.getRanking());
////    	values.put(RunningRecordProvider.RUNNING_RECORD_KEY_TOTAL, runningRecord.getTotal());
////    	values.put(RunningRecordProvider.RUNNING_RECORD_KEY_TIME, runningRecord.getTime());
////    	cr.insert(RunningRecordProvider.RUNNING_RECORD_URI, values);
////    	ContentResolver cr = getContentResolver();
////    	ContentValues values = new ContentValues();
////    	values.put(RunningRecordProvider.RUNNING_RECORD_KEY_NUMBER, runningRecord.getRunnerNumber());
////    	values.put(RunningRecordProvider.RUNNING_RECORD_KEY_DATE, runningRecord.getDate().toString());
////    	values.put(RunningRecordProvider.RUNNING_RECORD_KEY_DISTANCE, runningRecord.getDistance());
////    	values.put(RunningRecordProvider.RUNNING_RECORD_KEY_RANKING, runningRecord.getRanking());
////    	values.put(RunningRecordProvider.RUNNING_RECORD_KEY_TOTAL, runningRecord.getTotal());
////    	values.put(RunningRecordProvider.RUNNING_RECORD_KEY_TIME, runningRecord.getTime());
////    	cr.insert(RunningRecordProvider.RUNNING_RECORD_URI, values);
//    	
//    	ops.add(ContentProviderOperation.newInsert(RunningRecordProvider.RUNNING_RECORD_URI)
//    		.withValue(RunningRecordProvider.RUNNING_RECORD_KEY_NUMBER, runningRecord.getRunnerNumber())
//			.withValue(RunningRecordProvider.RUNNING_RECORD_KEY_DATE, runningRecord.getDate().toString())
//			.withValue(RunningRecordProvider.RUNNING_RECORD_KEY_DISTANCE, runningRecord.getDistance())
//			.withValue(RunningRecordProvider.RUNNING_RECORD_KEY_RANKING, runningRecord.getRanking())
//			.withValue(RunningRecordProvider.RUNNING_RECORD_KEY_TOTAL, runningRecord.getTotal())
//			.withValue(RunningRecordProvider.RUNNING_RECORD_KEY_TIME, runningRecord.getTime())
//			.withValue(RunningRecordProvider.RUNNING_RECORD_KEY_LINE, runningRecord.getLine())
//			.withValue(RunningRecordProvider.RUNNING_RECORD_KEY_CREATED_AT, runningRecord.getCreated_at().toString())
//			.build());
//		Log.v(TAG, "ops.add "
//			+ RunningRecordProvider.RUNNING_RECORD_KEY_NUMBER + "=>" + runningRecord.getRunnerNumber() + "\n"
//			+ RunningRecordProvider.RUNNING_RECORD_KEY_DATE + "=>" + runningRecord.getDate().toString() + "\n"
//			+ RunningRecordProvider.RUNNING_RECORD_KEY_DISTANCE + "=>" + runningRecord.getDistance() + "\n"
//			+ RunningRecordProvider.RUNNING_RECORD_KEY_RANKING + "=>" + runningRecord.getRanking() + "\n"
//			+ RunningRecordProvider.RUNNING_RECORD_KEY_TOTAL + "=>" + runningRecord.getTotal() + "\n"
//			+ RunningRecordProvider.RUNNING_RECORD_KEY_TIME + "=>" + runningRecord.getTime() + "\n"
//			+ RunningRecordProvider.RUNNING_RECORD_KEY_LINE + "=>" + runningRecord.getLine() + "\n"
//			+ RunningRecordProvider.RUNNING_RECORD_KEY_CREATED_AT + "=>" + runningRecord.getCreated_at().toString()
//		);
//	}
}
