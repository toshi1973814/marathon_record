package jp.walden.marathon;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Runners extends Activity {
	  
	private static final int MENU_MAIN_ADD_RUNNER = Menu.FIRST;
	private static final int MENU_MAIN_PREFERENCES = Menu.FIRST+1;
	private static final int DIALOG_ADD_RUNNER = 1;
	private static final int REQUEST_CODE_ADD_RUNNER = 1;
	private static final int REQUEST_CODE_PREFERENCES = 2;
	
	ArrayAdapter<Runner> aa;
	ArrayList<Runner> runners = new ArrayList<Runner>();

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // ランナーのレコードを確認し、なければ入力フォームを表示する
	    if(0 == runners.size()) {
	    	showAddRunnerForm();
	    }
        
        getRunners();
//        String[] items = {"red", "blue","green"};
        ListView listView = (ListView) findViewById(R.id.runners);
        aa = new ArrayAdapter<Runner>(getApplicationContext(), android.R.layout.simple_list_item_1, runners);
        listView.setAdapter(aa);
    }

    public void showAddRunnerForm() {
        Intent i = new Intent(this, AddRunner.class);
        startActivityForResult(i, REQUEST_CODE_ADD_RUNNER);
    }
    public void getRunners() {
    	runners.add(new Runner(1,"テスト太郎"));
    	runners.add(new Runner(2,"次郎"));
    	runners.add(new Runner(3,"サブロー"));
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
//	          intent.putExtra("runnerNumberString",runnerNumber.getText().toString());
//	          intent.putExtra("runnerNameString",runnerName.getText().toString());
	    	  Integer runnerNumber = Integer.parseInt(extras.getString("runnerNumberString"));
	    	  String runnerNameString = extras.getString("runnerNameString");
	    	  runners.add(new Runner(runnerNumber,runnerNameString));
	    	  aa.notifyDataSetChanged();
	      }
	  }
	
}