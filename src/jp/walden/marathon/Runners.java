package jp.walden.marathon;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class Runners extends Activity {
	  
	private static final int MENU_MAIN_ADD_RUNNER = Menu.FIRST;
	private static final int MENU_MAIN_PREFERENCES = Menu.FIRST+1;
	private static final int DIALOG_ADD_RUNNER = 1;
	private static final int REQUEST_CODE_ADD_RUNNER = 1;
	private static final int REQUEST_CODE_PREFERENCES = 2;
	protected Button add_runner;
	
	ArrayAdapter<Runner> aa;
	ArrayList<Runner> runners = new ArrayList<Runner>();

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
//        getRunners();
//        String[] items = {"red", "blue","green"};
        ListView listView = (ListView) findViewById(R.id.runners);
	    add_runner = (Button)findViewById(R.id.add_runner);
        add_runner.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				showAddRunnerForm();
				return true;
			}
		});
        // ランナーのレコードを確認し、なければ入力フォームへのリンクを表示する
	    if(0 == runners.size()) {
    	  add_runner.setVisibility(android.view.View.VISIBLE);
      	  add_runner.setText(R.string.message_navigation_to_add_runner);
	    }

	    aa = new ArrayAdapter<Runner>(getApplicationContext(), android.R.layout.simple_list_item_1, runners);
        listView.setAdapter(aa);
        loadRunnersFromProvider();
    }

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
    private void loadRunnersFromProvider() {
    	runners.clear();
        ContentResolver cr = getContentResolver();
        
        Cursor c = cr.query(RunnerProvider.RUNNER_URI, null, null, null, null);
        if (c.moveToFirst()) {
        	do {
        		int runnerNumber = c.getInt(RunnerProvider.NUMBER_COLUMN);
        		String runnerName = c.getString(RunnerProvider.NAME_COLUMN);
        		Runner runner = new Runner(runnerNumber,runnerName);
        		addRunnerToArray(runner);
        	} while(c.moveToNext());
        }
    }

    private void addRunnerToArray(Runner runner) {
		// TODO Auto-generated method stub
    	runners.add(runner);
    	aa.notifyDataSetChanged();
		
	}

	protected void showAddRunnerForm() {
        Intent i = new Intent(this, AddRunner.class);
        startActivityForResult(i, REQUEST_CODE_ADD_RUNNER);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
	    menu.add(0, MENU_MAIN_ADD_RUNNER, Menu.NONE, R.string.menu_main_add_runner);
	    menu.add(0, MENU_MAIN_PREFERENCES, Menu.NONE, R.string.menu_main_preferences);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		// TODO Auto-generated method stub
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
	    } 
	    return false;
	}
	  
	  @Override
	  public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);

	    if (requestCode == REQUEST_CODE_ADD_RUNNER)
	      if (resultCode == Activity.RESULT_OK) {
	    	  Bundle extras = data.getExtras();
	    	  Integer runnerNumber = Integer.parseInt(extras.getString("runnerNumberString"));
	    	  String runnerNameString = extras.getString("runnerNameString");
	    	  addRunnerToArray(new Runner(runnerNumber,runnerNameString));
//	    	  runners.add(new Runner(runnerNumber,runnerNameString));
//	    	  aa.notifyDataSetChanged();
	    	  add_runner.setVisibility(android.view.View.GONE);
	      }
	  }
	
}