package jp.walden.marathon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddRunner extends Activity {

	TextView errorMessage;
	EditText runnerNumber;
	EditText runnerName;
	Button buttonEdit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.add_runner);
	    errorMessage = (TextView)findViewById(R.id.error_message);
	    runnerNumber = (EditText)findViewById(R.id.runner_number);
	    runnerName = (EditText)findViewById(R.id.runner_name);
	    buttonEdit = (Button)findViewById(R.id.button_edit);
	    buttonEdit.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View view) {
	          try {
		          Integer.parseInt(runnerNumber.getText().toString());
	          } catch (NumberFormatException e) {
	        	  errorMessage.setVisibility(android.view.View.VISIBLE);
	        	  errorMessage.setText(R.string.error_message_runner_number_wrong_format);
	        	  return;
	          }
	          Intent intent = new Intent();
	          intent.putExtra("runnerNumberString",runnerNumber.getText().toString());
	          intent.putExtra("runnerNameString",runnerName.getText().toString());
	          AddRunner.this.setResult(RESULT_OK, intent);
	          finish();
	        }
	      });
	}

}
