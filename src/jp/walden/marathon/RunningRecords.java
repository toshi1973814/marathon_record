package jp.walden.marathon;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class RunningRecords extends Activity {

	Resources res;
	ArrayList<RunningRecord> runningRecords = new ArrayList<RunningRecord>();
	ArrayAdapter<RunningRecord> aa;
	int runnerNumber;
	String queryResultFormat = "yyyy-MM-dd";
	SimpleDateFormat queryResultFormatter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		String lastRunningRecordDateString = "";
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.running_record);
		ListView runningRecordListView = (ListView)findViewById(R.id.running_record);
		TextView runningRecordPageHeader = (TextView)findViewById(R.id.running_record_page_header);
		aa = new ArrayAdapter<RunningRecord>(getApplicationContext(), android.R.layout.simple_list_item_1, runningRecords);
		runningRecordListView.setAdapter(aa);
		
		long runnerId = getIntent().getExtras().getLong("runnerId");
		int distance = getIntent().getExtras().getInt("distance");
		String date = getIntent().getExtras().getString("date");
		String dateFormat = getIntent().getExtras().getString("dateFormat");
        ContentResolver cr = getContentResolver();
        String runnerIdString = String.valueOf(runnerId);
        Uri uri = Uri.parse(RunnerProvider.RUNNER_URI + "/" + runnerIdString);
        Cursor cursor = cr.query(uri, null, null, null, null);
        startManagingCursor(cursor);
        cursor.moveToFirst();
		runnerNumber = cursor.getInt(RunnerProvider.NUMBER_COLUMN);
		String runnerName = cursor.getString(RunnerProvider.NAME_COLUMN);
		res = getResources();
		String title = String.format(res.getString(R.string.running_record_page_header), runnerName);
		runningRecordPageHeader.setText(title);
		
		// データベースから最新のレコードを取得
		StringBuffer sb = new StringBuffer();
		sb.append
		(RunningRecordProvider.RUNNING_RECORD_KEY_NUMBER + "=" + String.valueOf(runnerNumber));
//		sb.append
//		(" and " + RunningRecordProvider.RUNNING_RECORD_KEY_DISTANCE + "=\"" + String.valueOf(distance) + "\"");
		String where = sb.toString();
		String orderBy = MarathonDatabaseHelper.RUNNING_RECORD_KEY_DATE + " DESC";
		Cursor lastRunningRecordCursor = cr.query(RunningRecordProvider.RUNNING_RECORD_URI, null, where, null, orderBy);
		startManagingCursor(lastRunningRecordCursor);
		
  		try {

  			// 最新の日付を現在日付と比較
  			
  			// 現在の日付を取得
  	  		SimpleDateFormat formatter= 
  	  				new SimpleDateFormat(dateFormat);
			Date currentDate = formatter.parse(date);
			int currentYear = currentDate.getYear() + 1900;
			int currentMonth = currentDate.getMonth();
			Date firstDateOfCurrentMonth = new Date(currentYear, currentMonth, 1, 0, 0, 0);

			queryResultFormat = "yyyy-MM-dd";
  	  		queryResultFormatter= 
  	  				new SimpleDateFormat(queryResultFormat);

  	  		Date lastRunningRecordDate;
			if(0 < lastRunningRecordCursor.getCount()) {
  				lastRunningRecordCursor.moveToFirst();
  				lastRunningRecordDateString = lastRunningRecordCursor.getString(RunningRecordProvider.RUNNING_RECORD_DATE_COLUMN);
  				lastRunningRecordDate = queryResultFormatter.parse(lastRunningRecordDateString);
  			} else {
  				// レコードがない場合は、デフォルトの最大過去月 + 1をlastRunningRecordDateStringとみなす
  				// TODO デフォルトの最大過去月を設定画面で設定できるようにする
  				int maxPastMonth = 6;
  				lastRunningRecordDate = new Date(currentYear, currentMonth - ( maxPastMonth - 1) , 1, 0, 0, 0);
  			}
			
			//　最新の日付を取得
			int lastRunningRecordYear = lastRunningRecordDate.getYear() + 1900;
			int lastRunningRecordMonth = lastRunningRecordDate.getMonth();
			Date firstDateOfLastRunningRecordMonth = new Date(lastRunningRecordYear, lastRunningRecordMonth, 1, 0, 0, 0);
			
			// 最新の日付が現在月より小さい場合
			if(firstDateOfLastRunningRecordMonth.before(firstDateOfCurrentMonth)) {
				
				int forLoopYear = firstDateOfLastRunningRecordMonth.getYear() + 1900;
				int forLoopMonth = firstDateOfLastRunningRecordMonth.getMonth();
				Date firstDateOfForLoopMonth = new Date(forLoopYear, forLoopMonth + 1, 1, 0, 0, 0);
				
				if(firstDateOfForLoopMonth.before(firstDateOfCurrentMonth)) {
					String runningRecordUrl = "";
					URI httpUri;
					do {
						// URLを生成
						runningRecordUrl = 
								String.format(res.getString(R.string.running_record_url_pattern),
										String.valueOf(forLoopYear), String.valueOf(forLoopYear) + String.valueOf(forLoopMonth));
						httpUri = new URI(runningRecordUrl);
						HttpRunningRecord httpRunningRecord = new HttpRunningRecord(httpUri);
						
						// TODO トランザクションで囲む
						
				        httpRunningRecord.extract(String.valueOf(runnerNumber), this);
						
						// 取得したら、比較対象月を加算
						forLoopYear = firstDateOfForLoopMonth.getYear() + 1900;
						forLoopMonth = firstDateOfForLoopMonth.getMonth();
						firstDateOfForLoopMonth = new Date(forLoopYear, forLoopMonth + 1, 1, 0, 0, 0);
					} while (firstDateOfForLoopMonth.before(firstDateOfCurrentMonth));
				}
			}
			
			// dbからレコードを取得し配列に格納
			sb.delete(0, sb.length());
			sb.append
			(RunningRecordProvider.RUNNING_RECORD_KEY_NUMBER + "=" + String.valueOf(runnerNumber));
			sb.append
			(" and " + RunningRecordProvider.RUNNING_RECORD_KEY_DISTANCE + "=\"" + String.valueOf(distance) + "\"");
			where = sb.toString();
			orderBy = MarathonDatabaseHelper.RUNNING_RECORD_KEY_DATE + " DESC";
			Cursor runningRecordsCursor = cr.query(RunningRecordProvider.RUNNING_RECORD_URI, null, where, null, orderBy);
			startManagingCursor(runningRecordsCursor);
//			runningRecordsCursor.moveToFirst();
//			int runnerNumber = cursor.getInt(RunnerProvider.NUMBER_COLUMN);
//			String runnerName = cursor.getString(RunnerProvider.NAME_COLUMN);

		    if (runningRecordsCursor.moveToFirst()) {
		        do { 
		        	addRunningRecordToArray(runningRecordsCursor);
		        } while(runningRecordsCursor.moveToNext());
		      }
		    runningRecordsCursor.close();

		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//	    URI httpUri;
//	    try {
//	        String runningRecordUrl = getString(R.string.running_record_url);
//	        httpUri = new URI(runningRecordUrl);
//	        HttpRunningRecord httpRunningRecord = new HttpRunningRecord(httpUri);
////	        httpRunningRecord.extract(String.valueOf(runnerNumber));
//	        // TODO トランザクションで一つの月の処理が中途半端にならないようにする
//	        httpRunningRecord.extract
//	        	(String.valueOf(runnerNumber), runningRecords, aa, this);
//	        // ランナー番号で結果を抽出
////	        ArrayList<RunningRecord> = httpGet.extract();
////	        String pageContent = httpGet.execute();
//	        int i = 1;
//	    } catch (URISyntaxException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
////	      } catch (ParserConfigurationException e) {
////	        e.printStackTrace();
////	      } catch (SAXException e) {
////	        e.printStackTrace();
////	      }
//	      finally {
//	      }
		
	}

	private void addRunningRecordToArray(Cursor runningRecordsCursor) {
        // Extract the quake details.
//        Long datems = runningRecordsCursor.getLong(EarthquakeProvider.DATE_COLUMN);
//        String details = c.getString(EarthquakeProvider.DETAILS_COLUMN);
//        Float lat = c.getFloat(EarthquakeProvider.LATITUDE_COLUMN);
//        Float lng = c.getFloat(EarthquakeProvider.LONGITUDE_COLUMN);
//        Double mag = c.getDouble(EarthquakeProvider.MAGNITUDE_COLUMN);
//        String link = c.getString(EarthquakeProvider.LINK_COLUMN);
//
//        Location location = new Location("gps");
//        location.setLongitude(lng);
//        location.setLatitude(lat);
//
//        Date date = new Date(datems);
//
//        Quake q = new Quake(date, details, location, mag, link);
//        addQuakeToArray(q);
//		Integer runnerNumber = runningRecordsCursor.getString(RunningRecordProvider.RUNNING_RECORD_DATE_COLUMN);
		String dateString = runningRecordsCursor.getString(RunningRecordProvider.RUNNING_RECORD_DATE_COLUMN);
		Date date = null;
		try {
			date = queryResultFormatter.parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
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

	public void addRunningRecord(RunningRecord runningRecord) {
    	ContentResolver cr = getContentResolver();
    	ContentValues values = new ContentValues();
    	values.put(RunningRecordProvider.RUNNING_RECORD_KEY_NUMBER, runningRecord.getRunnerNumber());
    	values.put(RunningRecordProvider.RUNNING_RECORD_KEY_DATE, runningRecord.getDate().toString());
    	values.put(RunningRecordProvider.RUNNING_RECORD_KEY_DISTANCE, runningRecord.getDistance());
    	values.put(RunningRecordProvider.RUNNING_RECORD_KEY_RANKING, runningRecord.getRanking());
    	values.put(RunningRecordProvider.RUNNING_RECORD_KEY_TOTAL, runningRecord.getTotal());
    	values.put(RunningRecordProvider.RUNNING_RECORD_KEY_TIME, runningRecord.getTime());
    	cr.insert(RunningRecordProvider.RUNNING_RECORD_URI, values);
	}

	private void loadRunningRecordsFromProvider() {
//		runningRecords
	}
}
