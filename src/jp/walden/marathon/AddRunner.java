package jp.walden.marathon;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddRunner extends Activity {

	protected TextView message;
	protected EditText runnerNumber;
	protected EditText runnerName;
	protected Button buttonEdit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.add_runner);
	    message = (TextView)findViewById(R.id.message);
	    runnerNumber = (EditText)findViewById(R.id.runner_number);
	    runnerName = (EditText)findViewById(R.id.runner_name);
	    buttonEdit = (Button)findViewById(R.id.button_edit);
	    buttonEdit.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View view) {
	          int runnerNumberInt;
	          try {
		          runnerNumberInt = Integer.parseInt(runnerNumber.getText().toString());
		          String runnerNameString = runnerName.getText().toString();
		          Runner runner = new Runner(runnerNumberInt, runnerNameString);
		          addRunnerToDB(runner);
		          Intent intent = new Intent();
//		          intent.putExtra("runnerNumberString",runnerNumber.getText().toString());
//		          intent.putExtra("runnerNameString",runnerNameString);
		          AddRunner.this.setResult(RESULT_OK, intent);
		          finish();
		      // ランナー番号に数値以外の入力があった場合
	          } catch (NumberFormatException e) {
	        	  message.setVisibility(android.view.View.VISIBLE);
	        	  message.setText(R.string.error_message_runner_number_wrong_format);
	        	  return;
	          // ランナー番号のUNIQUE制約違反等のSQLエラーの場合
	          } catch (SQLException e) {
	        	  message.setVisibility(android.view.View.VISIBLE);
	        	  message.setText(R.string.error_message_failed_to_add_runner);
	          }
	        }
	      });
	}
    
    private void addRunnerToDB(Runner _runner) {
    	ContentResolver cr = getContentResolver();
    	ContentValues values = new ContentValues();
    	values.put(RunnerProvider.KEY_NUMBER, _runner.getNumber());
    	values.put(RunnerProvider.KEY_NAME, _runner.getName());
    	values.put(RunnerProvider.KEY_CREATED_AT, _runner.getCreated_at().toString());
    	cr.insert(RunnerProvider.RUNNER_URI, values);
    }

}
