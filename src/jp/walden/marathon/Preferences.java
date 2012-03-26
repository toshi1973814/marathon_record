package jp.walden.marathon;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class Preferences extends Activity {

	Spinner monthsToGetDataSpinner ;
	SharedPreferences prefs;
	public static final String USER_PREFERENCE = "USER_PREFERENCES";
	public static final String PREF_MONTHS_TO_GET_DATA = "PREF_MONTHS_TO_GET_DATA";
	public static final Integer PREF_MONTHS_TO_GET_DATA_DEFAULT = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preferences);
		monthsToGetDataSpinner = (Spinner)findViewById(R.id.months_to_get_data);
		populateSpinners();
		
		prefs = getSharedPreferences(USER_PREFERENCE, Activity.MODE_PRIVATE);
		updateUIFromPreferences();
		Button okButton = (Button) findViewById(R.id.button_edit);
	    okButton.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View view) {
	          savePreferences();
	          Preferences.this.setResult(RESULT_OK);
	          finish();
	        }
	      });

	    Button cancelButton = (Button) findViewById(R.id.button_cancel);
	    cancelButton.setOnClickListener(new View.OnClickListener() {
	      public void onClick(View view) {
	        Preferences.this.setResult(RESULT_CANCELED);
	        finish();
	      }
	    });
	}

	protected void savePreferences() {
		int monthsToGetDataIndex = monthsToGetDataSpinner.getSelectedItemPosition();
		Editor editor = prefs.edit();
	    editor.putInt(PREF_MONTHS_TO_GET_DATA, monthsToGetDataIndex);
	    editor.commit();
		
	}

	private void updateUIFromPreferences() {
		int monthsToGetDataIndex = prefs.getInt(PREF_MONTHS_TO_GET_DATA, PREF_MONTHS_TO_GET_DATA_DEFAULT);
		monthsToGetDataSpinner.setSelection(monthsToGetDataIndex);
	}

	private void populateSpinners() {
	    ArrayAdapter<CharSequence> fAdapter;
	    fAdapter = ArrayAdapter.createFromResource(this, R.array.preference_months_to_get,
	                                               android.R.layout.simple_spinner_item);
	    fAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
	    monthsToGetDataSpinner.setAdapter(fAdapter);
	}

}