package jp.walden.marathon;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Intent;
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
	  
	private static final int MENU_MAIN_ADD_RUNNER = Menu.FIRST;
	private static final int MENU_MAIN_PREFERENCES = Menu.FIRST+1;
	private static final int DIALOG_ADD_RUNNER = 1;
	private static final int REQUEST_CODE_ADD_RUNNER = 1;
	private static final int REQUEST_CODE_PREFERENCES = 2;
	private static final int REQUEST_CODE_RUNNING_RECORD_1KM = 3;
	private static final int REQUEST_CODE_RUNNING_RECORD_3KM = 4;
	private static final int REQUEST_CODE_RUNNING_RECORD_5KM = 5;
	private static final int REQUEST_CODE_RUNNING_RECORD_10KM = 6;
	private static final int REQUEST_CODE_RUNNING_RECORD_20KM = 7;
	protected Button add_runner;
	  
	// Define the new menu item identifiers 
	static final private int SELECT_1KM = Menu.FIRST;
	static final private int SELECT_3KM = Menu.FIRST + 1;
	static final private int SELECT_5KM = Menu.FIRST + 2;
	static final private int SELECT_10KM = Menu.FIRST + 3;
	static final private int SELECT_20KM = Menu.FIRST + 4;
	static final private int REMOVE_RUNNER = Menu.FIRST + 5;
	
//	ArrayAdapter<Runner> aa;
//	ArrayList<Runner> runners = new ArrayList<Runner>();
	SimpleCursorAdapter mAdapter;
	Cursor cursor;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ContentResolver cr = getContentResolver();
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
        add_runner.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				showAddRunnerForm();
				return true;
			}
		});
        // ランナーのレコードを確認し、なければ入力フォームへのリンクを表示する
	    if(0 == cursor.getCount()) {
    	  add_runner.setVisibility(android.view.View.VISIBLE);
      	  add_runner.setText(R.string.message_navigation_to_add_runner);
	    }

//	    aa = new ArrayAdapter<Runner>(getApplicationContext(), android.R.layout.simple_list_item_1, runners);
//        listView.setAdapter(aa);
        registerForContextMenu(this.getListView());
//        loadRunnersFromProvider();
    }

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(MENU_GROUP_CONTEXT, SELECT_1KM, Menu.NONE, R.string.menu_main_context_select_1km);
		menu.add(MENU_GROUP_CONTEXT, SELECT_3KM, Menu.NONE, R.string.menu_main_context_select_3km);
		menu.add(MENU_GROUP_CONTEXT, SELECT_5KM, Menu.NONE, R.string.menu_main_context_select_5km);
		menu.add(MENU_GROUP_CONTEXT, SELECT_10KM, Menu.NONE, R.string.menu_main_context_select_10km);
		menu.add(MENU_GROUP_CONTEXT, SELECT_20KM, Menu.NONE, R.string.menu_main_context_select_20km);
		menu.add(MENU_GROUP_CONTEXT, REMOVE_RUNNER, Menu.NONE, R.string.menu_main_context_delete_runner);
	}
//	
//	@Override
//	public void onContentChanged() {
//		// 
//		super.onContentChanged();
//        // ランナーのレコードを確認し、なければ入力フォームへのリンクを表示する
//		if(cursor != null) {
//		    if(0 == cursor.getCount()) {
//		    	  add_runner.setVisibility(android.view.View.VISIBLE);
//		      	  add_runner.setText(R.string.message_navigation_to_add_runner);
//			    }
//		}
//	}

//    private void refreshRunners() {
//    	URL url;
//    	try {
//    		String runnerFeed = getString(R.string.runner_feed);
//    		url = new URL(runnerFeed);
//    		
//    		URLConnection connection;
//    		connection = url.openConnection();
//    		
//    		HttpURLConnection httpConnection = (HttpURLConnection)connection;
//    		int responseCode = httpConnection.getResponseCode();
//    		if(responseCode == HttpURLConnection.HTTP_OK) {
//    			InputStream in = httpConnection.getInputStream();
//    			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//    			DocumentBuilder db = dbf.newDocumentBuilder();
//    			
//    			Document dom = db.parse(in);
//    			Element docEle = dom.getDocumentElement();
//    			runners.clear();
//    			
//    			NodeList nl = docEle.getElementsByTagName("entry");
//    			Element
//    		}
//    	}
//    }
//    private void loadRunnersFromProvider() {
//    	runners.clear();
//        ContentResolver cr = getContentResolver();
//        
//        Cursor c = cr.query(RunnerProvider.RUNNER_URI, null, null, null, null);
//        if (c.moveToFirst()) {
//        	do {
//        		int runnerNumber = c.getInt(RunnerProvider.NUMBER_COLUMN);
//        		String runnerName = c.getString(RunnerProvider.NAME_COLUMN);
//        		Runner runner = new Runner(runnerNumber,runnerName);
//        		addRunnerToArray(runner);
//        	} while(c.moveToNext());
//        }
//    }
//
//    private void addRunnerToArray(Runner runner) {
//		// 
//    	runners.add(runner);
//    	aa.notifyDataSetChanged();
//		
//	}

	protected void showAddRunnerForm() {
        Intent i = new Intent(this, AddRunner.class);
        startActivityForResult(i, REQUEST_CODE_ADD_RUNNER);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    menu.add(MENU_GROUP_MAIN, MENU_MAIN_ADD_RUNNER, Menu.NONE, R.string.menu_main_add_runner);
	    menu.add(MENU_GROUP_MAIN, MENU_MAIN_PREFERENCES, Menu.NONE, R.string.menu_main_preferences);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
	    switch (item.getItemId()) {
	      case (MENU_MAIN_ADD_RUNNER): {
	    	  showAddRunnerForm();
	    	  return true;
	      }
	      case (MENU_MAIN_PREFERENCES): {
//	          Intent i = new Intent(this, Preferences.class);
//	          startActivityForResult(i, REQUEST_CODE_PREFERENCES);
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

	    switch (item.getItemId()) {
	      case (REMOVE_RUNNER): {
//	  		info.getId();
	    	  removeRunner(String.valueOf(info.id));
	    	  return true;
	      }
	      case (SELECT_5KM): {
	          Intent i = new Intent(this, RunningRecords.class);
	          i.putExtra("runnerId",info.id);
	          startActivityForResult(i, REQUEST_CODE_RUNNING_RECORD_5KM);
	    	  return true;
	      }
	      default: return false;
	    } 
	}

//	@Override
//	public boolean onMenuItemSelected(int featureId, MenuItem item) {
//
//		// 
//	    super.onOptionsItemSelected(item);
//        
//	    if(MENU_GROUP_MAIN == item.getGroupId()) {
//		    switch (item.getItemId()) {
//		      case (MENU_MAIN_ADD_RUNNER): {
//		    	  showAddRunnerForm();
//		    	  return true;
//		      }
//		      case (MENU_MAIN_PREFERENCES): {
////		          Intent i = new Intent(this, Preferences.class);
////		          startActivityForResult(i, REQUEST_CODE_PREFERENCES);
//		          return true;
//		      }
//		      default: return false;
//		    } 
//	    } else if (MENU_GROUP_CONTEXT == item.getGroupId()) {
//		    switch (item.getItemId()) {
//		      case (REMOVE_RUNNER): {
//		    	  removeRunner(item);
//		    	  return true;
//		      }
//		      default: return false;
//		    } 
//	    }
//	    return false;
//	}
	  
	  private void removeRunner(String id) {
		  ContentResolver cr = getContentResolver();
		  String where = RunnerProvider.KEY_ID + " = " + id;
		  cr.delete(RunnerProvider.RUNNER_URI, where, null);
	}

	@Override
	  public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);

	    if (requestCode == REQUEST_CODE_ADD_RUNNER)
	      if (resultCode == Activity.RESULT_OK) {
//	    	  Bundle extras = data.getExtras();
//	    	  Integer runnerNumber = Integer.parseInt(extras.getString("runnerNumberString"));
//	    	  String runnerNameString = extras.getString("runnerNameString");
//	    	  addRunnerToArray(new Runner(runnerNumber,runnerNameString));
//	    	  runners.add(new Runner(runnerNumber,runnerNameString));
//	    	  aa.notifyDataSetChanged();
//	    	  loadRunnersFromProvider();
	    	  add_runner.setVisibility(android.view.View.GONE);
	      }
	  }
	
}