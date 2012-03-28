package jp.walden.marathon;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;

public class Runners extends ListActivity {

	private static final int MENU_GROUP_MAIN = 0;
	private static final int MENU_GROUP_CONTEXT = 1;
	  
//	private static final int MENU_MAIN_ADD_RUNNER = Menu.FIRST;
	private static final int MENU_MAIN_PREFERENCES = Menu.FIRST;
//	private static final int DIALOG_ADD_RUNNER = 1;
	private static final int REQUEST_CODE_ADD_RUNNER = 1;
	private static final int REQUEST_CODE_PREFERENCES = 2;
//	private static final int REQUEST_CODE_UPDATE_RECORD = 3;
	private static final int REQUEST_CODE_RUNNING_RECORD_1KM = 3;
	private static final int REQUEST_CODE_RUNNING_RECORD_3KM = 4;
	private static final int REQUEST_CODE_RUNNING_RECORD_5KM = 5;
	private static final int REQUEST_CODE_RUNNING_RECORD_10KM = 6;
	private static final int REQUEST_CODE_RUNNING_RECORD_20KM = 7;
	protected Button add_runner;
	  
	// Define the new menu item identifiers 
	static final private int UPDATE_RECORD = Menu.FIRST;
	static final private int SELECT_1KM = Menu.FIRST + 1;
	static final private int SELECT_3KM = Menu.FIRST + 2;
	static final private int SELECT_5KM = Menu.FIRST + 3;
	static final private int SELECT_10KM = Menu.FIRST + 4;
	static final private int SELECT_20KM = Menu.FIRST + 5;
	static final private int REMOVE_RUNNER = Menu.FIRST + 6;

	private static final String QUERY_RESULT_FORMAT = "yyyy-MM-dd";

//	ArrayAdapter<Runner> aa;
//	ArrayList<Runner> runners = new ArrayList<Runner>();
	SimpleCursorAdapter mAdapter;
//	Cursor cursor;
	Integer monthsToGetData;
	
	Cursor cursor;
	ContentResolver cr;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        cr = getContentResolver();
        cursor = cr.query(RunnerProvider.RUNNER_URI, null, null, null, null);
        startManagingCursor(cursor);
        String[] columns = new String[] {
        		RunnerProvider.KEY_NAME,
        		RunnerProvider.KEY_NUMBER
        };
        int[] to = new int[] {
        		R.id.name_entry,
        		R.id.number_entry
        };
        mAdapter = new SimpleCursorAdapter(this, R.layout.runner_list_entry, cursor, columns, to);
        this.setListAdapter(mAdapter);
        
//        getRunners();
//        String[] items = {"red", "blue","green"};
//        ListView listView = (ListView) findViewById(R.id.runners);
	    add_runner = (Button)findViewById(R.id.add_runner);
//        add_runner.setOnTouchListener(new OnTouchListener() {
//			
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				showAddRunnerForm();
//				return true;
//			}
//		});
        // ランナーのレコードを確認し、なければ入力フォームへのリンクを表示する
//	    if(0 == cursor.getCount()) {
//    	  add_runner.setVisibility(android.view.View.VISIBLE);
////      	  add_runner.setText(R.string.message_navigation_to_add_runner);
//	    }

//	    aa = new ArrayAdapter<Runner>(getApplicationContext(), android.R.layout.simple_list_item_1, runners);
//        listView.setAdapter(aa);
        registerForContextMenu(this.getListView());
//        updateFromPreferences();
//        loadRunnersFromProvider();
    }
    
    @Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(MENU_GROUP_CONTEXT, UPDATE_RECORD, Menu.NONE, R.string.menu_main_context_update_record);
		menu.add(MENU_GROUP_CONTEXT, SELECT_1KM, Menu.NONE, R.string.menu_main_context_select_1km);
		menu.add(MENU_GROUP_CONTEXT, SELECT_3KM, Menu.NONE, R.string.menu_main_context_select_3km);
		menu.add(MENU_GROUP_CONTEXT, SELECT_5KM, Menu.NONE, R.string.menu_main_context_select_5km);
		menu.add(MENU_GROUP_CONTEXT, SELECT_10KM, Menu.NONE, R.string.menu_main_context_select_10km);
		menu.add(MENU_GROUP_CONTEXT, SELECT_20KM, Menu.NONE, R.string.menu_main_context_select_20km);
		menu.add(MENU_GROUP_CONTEXT, REMOVE_RUNNER, Menu.NONE, R.string.menu_main_context_delete_runner);
	}

	public void updateAllRunnerRecords(View v) {
		// 全員分の記録を更新するためのIntentをRunnerRecordServiceに送る
    }

	public void showAddRunnerForm(View v) {
		showAddRunnerForm();
    }

	public void showAddRunnerForm() {
        Intent i = new Intent(this, AddRunner.class);
        startActivityForResult(i, REQUEST_CODE_ADD_RUNNER);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//	    menu.add(MENU_GROUP_MAIN, MENU_MAIN_ADD_RUNNER, Menu.NONE, R.string.menu_main_add_runner);
	    menu.add(MENU_GROUP_MAIN, MENU_MAIN_PREFERENCES, Menu.NONE, R.string.menu_main_preferences);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
	    switch (item.getItemId()) {
//	      case (MENU_MAIN_ADD_RUNNER): {
//	    	  showAddRunnerForm();
//	    	  return true;
//	      }
	      case (MENU_MAIN_PREFERENCES): {
	          Intent i = new Intent(this, Preferences.class);
	          startActivityForResult(i, REQUEST_CODE_PREFERENCES);
	          return true;
	      }
	      default: return false;
	    }
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		super.onContextItemSelected(item);
  		AdapterView.AdapterContextMenuInfo info = 
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
  		
  		int itemId = item.getItemId();
  		if(itemId == REMOVE_RUNNER) {
	    	  removeRunner(String.valueOf(info.id));
	    	  return true;
  		}

  		Intent i;
        int requestCode = 0;
  		
  		// データ更新
  		if(itemId == UPDATE_RECORD) {
  			// 現在日付を取得
  	  		Calendar currentDate = Calendar.getInstance();
//  	  		String dateFormat = "yyyy-MM-dd";
  	  		SimpleDateFormat formatter= 
  	  				new SimpleDateFormat(QUERY_RESULT_FORMAT);
  	  		String dateNow = formatter.format(currentDate.getTime());
  	  		
  	  		String where = MarathonDatabaseHelper.KEY_ID + "=" + String.valueOf(info.id);
  	  		cursor = cr.query(RunnerProvider.RUNNER_URI, null, where, null, null);
  	  		cursor.moveToFirst();
  	  		String runnerName = cursor.getString(MarathonDatabaseHelper.NAME_COLUMN);
  	  		
	  		i = new Intent(this, RunningRecordService.class);
	        i.putExtra("runnerId",info.id);
	        i.putExtra("runnerName",runnerName);
			i.putExtra("date",dateNow);
			i.putExtra("dateFormat",QUERY_RESULT_FORMAT);
//			requestCode = REQUEST_CODE_UPDATE_RECORD;
			startService(i);
	    	return true;
		} else {
	  		i = new Intent(this, RunningRecords.class);
	        i.putExtra("runnerId",info.id);
		    switch (itemId) {
		      case (SELECT_1KM): {
		          i.putExtra("distance",1);
		          requestCode = REQUEST_CODE_RUNNING_RECORD_1KM;
		          break;
		      }
		      case (SELECT_3KM): {
		          i.putExtra("distance",3);
		          requestCode = REQUEST_CODE_RUNNING_RECORD_3KM;
		          break;
		      }
		      case (SELECT_5KM): {
		          i.putExtra("distance",5);
		          requestCode = REQUEST_CODE_RUNNING_RECORD_5KM;
		          break;
		      }
		      case (SELECT_10KM): {
		          i.putExtra("distance",10);
		          requestCode = REQUEST_CODE_RUNNING_RECORD_10KM;
		          break;
		      }
		      case (SELECT_20KM): {
		          i.putExtra("distance",20);
		          requestCode = REQUEST_CODE_RUNNING_RECORD_20KM;
		          break;
		      }
		      default: return false;
		    } 
	        startActivityForResult(i, requestCode);
		}

  	  return true;
	}
	  
	  private void removeRunner(String id) {
//		  ContentResolver cr = getContentResolver();
		  String where = RunnerProvider.KEY_ID + " = " + id;
		  cr.delete(RunnerProvider.RUNNER_URI, where, null);
	}

	@Override
	  public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);

	    if (requestCode == REQUEST_CODE_PREFERENCES)
	      if (resultCode == Activity.RESULT_OK) {
//	    	  updateFromPreferences();
	    	  
//	    	  Bundle extras = data.getExtras();
//	    	  Integer runnerNumber = Integer.parseInt(extras.getString("runnerNumberString"));
//	    	  String runnerNameString = extras.getString("runnerNameString");
//	    	  addRunnerToArray(new Runner(runnerNumber,runnerNameString));
//	    	  runners.add(new Runner(runnerNumber,runnerNameString));
//	    	  aa.notifyDataSetChanged();
//	    	  loadRunnersFromProvider();
//	    	  add_runner.setVisibility(android.view.View.GONE);
	      }
	  }

	public void refreshRunningRecord() {
		startService(new Intent(this, RunningRecordService.class));
	}
//	private void updateFromPreferences() {
//		SharedPreferences prefs = getSharedPreferences(Preferences.USER_PREFERENCE, Activity.MODE_PRIVATE);
//		int monthsToGetDataIndex = prefs.getInt(Preferences.PREF_MONTHS_TO_GET_DATA, Preferences.PREF_MONTHS_TO_GET_DATA_DEFAULT);
//	    Resources r = getResources();
//	    // Get the option values from the arrays.
//	    String[] monthsToGetDataArray = r.getStringArray(R.array.preference_months_to_get);
//	    String monthsToGetDataString = monthsToGetDataArray[monthsToGetDataIndex];
//	    if(!monthsToGetDataString.equals(getString(R.string.array_item_all))) {
//	    	monthsToGetData = Integer.valueOf(monthsToGetDataString);
//	    } else {
//	    	monthsToGetData = 0;
//	    }
//	}
	
}