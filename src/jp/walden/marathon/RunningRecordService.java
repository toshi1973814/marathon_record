package jp.walden.marathon;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.Service;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class RunningRecordService extends Service {

	private ContentResolver cr;
	private Resources res;

	private ArrayList<ContentProviderOperation> ops;
	private static final String TAG = "RunningRecordService";
//	private static final String QUERY_RESULT_FORMAT = "yyyy-MM-dd";

	private int runnerNumber;
	private SimpleDateFormat queryResultFormatter = new SimpleDateFormat(Runners.QUERY_RESULT_FORMAT);
	private String dateNow = "";
	private String dateFormat = "";
	private String runnerIdString = "";
	private String runnerName = "";
	private ArrayList<ParcelableRunner> parcelablerunnerArrayList = new ArrayList<ParcelableRunner>();
	
	private Context context = null;
	private String toastMessage = "";
	public Handler toastHandler = null;
	private int currentYear = -1;
	private int currentMonth = -1;
	private Date firstDateOfCurrentMonth;

	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
		toastHandler = new Handler();

	}

	public void setToastMessage(String toastMessage) {
		this.toastMessage = toastMessage;
	}

	public Runnable toastRunnable = new Runnable() {public void run() {
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, toastMessage, duration);
		toast.show();
	}};
	
	@Override
	public void onStart(Intent intent, int startId) {
		if(intent.getExtras().containsKey("runnerId")) {
			getExtraFromIntent(intent);
			updateRunningRecords();
		}
	}
//
//	private void showToast(String msg) {
//		int duration = Toast.LENGTH_SHORT;
//		Toast toast = Toast.makeText(context, msg, duration);
//		toast.show();
//    }

	private void updateRunningRecords() {
		Thread updateThread = new Thread(null, backgroundUpdate, "update_runningRecord");  
		updateThread.start();        
	}

	private Runnable backgroundUpdate = new Runnable() {
		public void run() {
			doUpdateRunningRecord();
		}
	};
	
	private void updateRunningRecordOfEachRunner(ParcelableRunner pRunner) {
		
		String lastRunningRecordDateString = "";

		// ParcelableRunnerからrunnerIdString,runnerNameをセット
		long runnerId = pRunner.getRunnerId();
		runnerIdString = String.valueOf(runnerId);
		runnerName = pRunner.getRunnerName();
		
		// idからランナーナンバーを抽出
		Cursor cursor = getCursorFromRunnerProvider();
		cursor.moveToFirst();
		runnerNumber = cursor.getInt(RunnerProvider.NUMBER_COLUMN);
		Cursor lastRunningRecordCursor = getLastRunningRecordCursor();
		
		try {

  			// 最新の日付を現在日付と比較

//  	  		queryResultFormatter= 
//  	  				new SimpleDateFormat(QUERY_RESULT_FORMAT);

  	  		Date lastRunningRecordDate;
			if(0 < lastRunningRecordCursor.getCount()) {
  				lastRunningRecordCursor.moveToFirst();
  				lastRunningRecordDateString = lastRunningRecordCursor.getString(RunningRecordProvider.RUNNING_RECORD_DATE_COLUMN);
  				lastRunningRecordDate = queryResultFormatter.parse(lastRunningRecordDateString);
  			} else {
  				// レコードがない場合は、デフォルトの最大過去月 + 1をlastRunningRecordDateStringとみなす
  				Integer maxPastMonth = getMonthsToGetData();
  				lastRunningRecordDate = new Date(currentYear, currentMonth - (maxPastMonth + 1) , 1, 0, 0, 0);
  			}
			
			//　最新の日付を取得
			int lastRunningRecordYear = lastRunningRecordDate.getYear();
			int lastRunningRecordMonth = lastRunningRecordDate.getMonth();
			Date firstDateOfLastRunningRecordMonth = new Date(lastRunningRecordYear, lastRunningRecordMonth, 1, 0, 0, 0);
			
			// 最新の日付が現在月より小さい場合
			if(firstDateOfLastRunningRecordMonth.before(firstDateOfCurrentMonth)) {
				
				// 最新のレコードの翌月分を取得するため、Dateの月を加算
				int intYear = firstDateOfLastRunningRecordMonth.getYear();
				int intMonth = firstDateOfLastRunningRecordMonth.getMonth();
				Date firstDateOfForLoopMonth = new Date(intYear, intMonth + 1, 1, 0, 0, 0);
				
				if(firstDateOfForLoopMonth.before(firstDateOfCurrentMonth)) {

					res = getResources();
					String runningRecordUrl = "";
					URI httpUri;
					do {
						int forLoopYear = firstDateOfForLoopMonth.getYear();
						int forLoopMonth = firstDateOfForLoopMonth.getMonth();
						String forLoopYearUrlString = String.valueOf(forLoopYear + 1900);
						String forLoopMonthUrlString = String.format("%02d", forLoopMonth + 1);
			
						// URLを生成
						runningRecordUrl = 
								String.format(res.getString(R.string.running_record_url_pattern),
										forLoopYearUrlString, forLoopYearUrlString + forLoopMonthUrlString);
						httpUri = new URI(runningRecordUrl);
						HttpRunningRecord httpRunningRecord = new HttpRunningRecord(httpUri);
						
						// TODO トランザクションで囲む
						ops = new ArrayList<ContentProviderOperation>();
			
				        httpRunningRecord.extract(String.valueOf(runnerNumber), this);
			
				        // ひと月分の解析が完了したので、トランザクションを確定
				        try {
							cr.applyBatch("jp.walden.provider.running_record", ops);
						} catch (RemoteException e) {
							e.printStackTrace();
						} catch (OperationApplicationException e) {
							e.printStackTrace();
						}
						
						// 取得したら、比較対象月を加算
						forLoopYear = firstDateOfForLoopMonth.getYear();
						forLoopMonth = firstDateOfForLoopMonth.getMonth();
						firstDateOfForLoopMonth = new Date(forLoopYear, forLoopMonth + 1, 1, 0, 0, 0);
					} while (firstDateOfForLoopMonth.before(firstDateOfCurrentMonth));
				}
			}

		toastMessage = context.getString(R.string.running_record_toast_end_update);
		toastHandler.post(toastRunnable);
//		String endMsg = context.getString(R.string.running_record_toast_end_update);
//		showToast(endMsg);

		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			lastRunningRecordCursor.close();
			cursor.close();
		}
		
	}

	private void doUpdateRunningRecord() {
//		(Date firstDateOfForLoopMonth, int runnerNumber, Date firstDateOfCurrentMonth) {
		toastMessage = context.getString(R.string.running_record_toast_start_update);
		toastHandler.post(toastRunnable);
//		showToast(startMsg);

			// 現在の日付を取得
	  	SimpleDateFormat formatter= 
	  			new SimpleDateFormat(dateFormat);
		Date currentDate = null;
		try {
			currentDate = formatter.parse(dateNow);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		currentYear = currentDate.getYear();
		currentMonth = currentDate.getMonth();
		firstDateOfCurrentMonth = new Date(currentYear, currentMonth, 1, 0, 0, 0);
		cr = getContentResolver();

		// ArrayList<ParcelableRunner>分だけ処理をループする
		for (ParcelableRunner pRunner : parcelablerunnerArrayList) {
			updateRunningRecordOfEachRunner(pRunner);
		}

		toastMessage = context.getString(R.string.running_record_toast_end_update);
		toastHandler.post(toastRunnable);

//		// idからランナーナンバーを抽出
//		Cursor cursor = getCursorFromRunnerProvider();
//		cursor.moveToFirst();
//		runnerNumber = cursor.getInt(RunnerProvider.NUMBER_COLUMN);
//		Cursor lastRunningRecordCursor = getLastRunningRecordCursor();
//		
//		try {
//
//  			// 最新の日付を現在日付と比較
//
////  	  		queryResultFormatter= 
////  	  				new SimpleDateFormat(QUERY_RESULT_FORMAT);
//
//  	  		Date lastRunningRecordDate;
//			if(0 < lastRunningRecordCursor.getCount()) {
//  				lastRunningRecordCursor.moveToFirst();
//  				lastRunningRecordDateString = lastRunningRecordCursor.getString(RunningRecordProvider.RUNNING_RECORD_DATE_COLUMN);
//  				lastRunningRecordDate = queryResultFormatter.parse(lastRunningRecordDateString);
//  			} else {
//  				// レコードがない場合は、デフォルトの最大過去月 + 1をlastRunningRecordDateStringとみなす
//  				Integer maxPastMonth = getMonthsToGetData();
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
//				if(firstDateOfForLoopMonth.before(firstDateOfCurrentMonth)) {
//
//					res = getResources();
//					String runningRecordUrl = "";
//					URI httpUri;
//					do {
//						int forLoopYear = firstDateOfForLoopMonth.getYear();
//						int forLoopMonth = firstDateOfForLoopMonth.getMonth();
//						String forLoopYearUrlString = String.valueOf(forLoopYear + 1900);
//						String forLoopMonthUrlString = String.format("%02d", forLoopMonth + 1);
//			
//						// URLを生成
//						runningRecordUrl = 
//								String.format(res.getString(R.string.running_record_url_pattern),
//										forLoopYearUrlString, forLoopYearUrlString + forLoopMonthUrlString);
//						httpUri = new URI(runningRecordUrl);
//						HttpRunningRecord httpRunningRecord = new HttpRunningRecord(httpUri);
//						
//						// TODO トランザクションで囲む
//						ops = new ArrayList<ContentProviderOperation>();
//			
//				        httpRunningRecord.extract(String.valueOf(runnerNumber), this);
//			
//				        // ひと月分の解析が完了したので、トランザクションを確定
//				        try {
//							cr.applyBatch("jp.walden.provider.running_record", ops);
//						} catch (RemoteException e) {
//							e.printStackTrace();
//						} catch (OperationApplicationException e) {
//							e.printStackTrace();
//						}
//						
//						// 取得したら、比較対象月を加算
//						forLoopYear = firstDateOfForLoopMonth.getYear();
//						forLoopMonth = firstDateOfForLoopMonth.getMonth();
//						firstDateOfForLoopMonth = new Date(forLoopYear, forLoopMonth + 1, 1, 0, 0, 0);
//					} while (firstDateOfForLoopMonth.before(firstDateOfCurrentMonth));
//				}
//			}
//
//		toastMessage = context.getString(R.string.running_record_toast_end_update);
//		toastHandler.post(toastRunnable);
////		String endMsg = context.getString(R.string.running_record_toast_end_update);
////		showToast(endMsg);
//
//		} catch (ParseException e1) {
//			e1.printStackTrace();
//		} catch (URISyntaxException e) {
//			e.printStackTrace();
//		} finally {
//			lastRunningRecordCursor.close();
//			cursor.close();
//		}
	}

	private Cursor getLastRunningRecordCursor() {
		StringBuffer sb = new StringBuffer();
		sb.append
		(RunningRecordProvider.RUNNING_RECORD_KEY_NUMBER + "=" + String.valueOf(runnerNumber));
		String where = sb.toString();
		String orderBy = MarathonDatabaseHelper.RUNNING_RECORD_KEY_DATE + " DESC";
		Cursor lastRunningRecordCursor = cr.query(RunningRecordProvider.RUNNING_RECORD_URI, null, where, null, orderBy);
//		startManagingCursor(lastRunningRecordCursor);
		return lastRunningRecordCursor;
	}

	private Integer getMonthsToGetData() {
		SharedPreferences prefs = getSharedPreferences(Preferences.USER_PREFERENCE, Activity.MODE_PRIVATE);
		int monthsToGetDataIndex = prefs.getInt(Preferences.PREF_MONTHS_TO_GET_DATA, Preferences.PREF_MONTHS_TO_GET_DATA_DEFAULT);
	    Resources r = getResources();
	    // Get the option values from the arrays.
	    String[] monthsToGetDataArray = r.getStringArray(R.array.preference_months_to_get);
	    String monthsToGetDataString = monthsToGetDataArray[monthsToGetDataIndex];
		Integer monthsToGetData;
	    if(!monthsToGetDataString.equals(getString(R.string.array_item_all))) {
	    	monthsToGetData = Integer.valueOf(monthsToGetDataString);
	    } else {
	    	monthsToGetData = 0;
	    }
		return monthsToGetData;
	}

	private Cursor getCursorFromRunnerProvider() {
        Uri uri = Uri.parse(RunnerProvider.RUNNER_URI + "/" + runnerIdString);
        Cursor cursor = cr.query(uri, null, null, null, null);
//        startManagingCursor(cursor);
        return cursor;
	}

	private void getExtraFromIntent(Intent intent) {
		
		parcelablerunnerArrayList = intent.getParcelableArrayListExtra(Runners.INTENT_KEYS_RUNNER_LIST);
		dateNow = intent.getStringExtra(Runners.INTENT_KEYS_DATE);
		dateFormat = intent.getStringExtra(Runners.INTENT_KEYS_DATE_FORMAT);
		
//		long runnerId = intent.getExtras().getLong("runnerId");
////		distance = intent.getExtras().getInt("distance");
//		date = intent.getExtras().getString("date");
//		dateFormat = intent.getExtras().getString("dateFormat");
//	    runnerIdString = String.valueOf(runnerId);
//	    runnerName = intent.getExtras().getString("runnerName");
	}

	public void addRunningRecord(RunningRecord runningRecord) {
    	
    	ops.add(ContentProviderOperation.newInsert(RunningRecordProvider.RUNNING_RECORD_URI)
    		.withValue(RunningRecordProvider.RUNNING_RECORD_KEY_NUMBER, runningRecord.getRunnerNumber())
			.withValue(RunningRecordProvider.RUNNING_RECORD_KEY_DATE, runningRecord.getDate().toString())
			.withValue(RunningRecordProvider.RUNNING_RECORD_KEY_DISTANCE, runningRecord.getDistance())
			.withValue(RunningRecordProvider.RUNNING_RECORD_KEY_RANKING, runningRecord.getRanking())
			.withValue(RunningRecordProvider.RUNNING_RECORD_KEY_TOTAL, runningRecord.getTotal())
			.withValue(RunningRecordProvider.RUNNING_RECORD_KEY_TIME, runningRecord.getTime())
			.withValue(RunningRecordProvider.RUNNING_RECORD_KEY_LINE, runningRecord.getLine())
			.withValue(RunningRecordProvider.RUNNING_RECORD_KEY_CREATED_AT, runningRecord.getCreated_at().toString())
			.build());
		Log.v(TAG, "ops.add "
			+ RunningRecordProvider.RUNNING_RECORD_KEY_NUMBER + "=>" + runningRecord.getRunnerNumber() + "\n"
			+ RunningRecordProvider.RUNNING_RECORD_KEY_DATE + "=>" + runningRecord.getDate().toString() + "\n"
			+ RunningRecordProvider.RUNNING_RECORD_KEY_DISTANCE + "=>" + runningRecord.getDistance() + "\n"
			+ RunningRecordProvider.RUNNING_RECORD_KEY_RANKING + "=>" + runningRecord.getRanking() + "\n"
			+ RunningRecordProvider.RUNNING_RECORD_KEY_TOTAL + "=>" + runningRecord.getTotal() + "\n"
			+ RunningRecordProvider.RUNNING_RECORD_KEY_TIME + "=>" + runningRecord.getTime() + "\n"
			+ RunningRecordProvider.RUNNING_RECORD_KEY_LINE + "=>" + runningRecord.getLine() + "\n"
			+ RunningRecordProvider.RUNNING_RECORD_KEY_CREATED_AT + "=>" + runningRecord.getCreated_at().toString()
		);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return the runnerName
	 */
	public String getRunnerName() {
		return runnerName;
	}

}
