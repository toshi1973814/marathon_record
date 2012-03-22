package jp.walden.marathon;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class RunningRecords extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.running_record);
		ListView runningRecordListView = (ListView)findViewById(R.id.running_record);
		TextView runningRecordPageHeader = (TextView)findViewById(R.id.running_record_page_header);
		ArrayList<RunningRecord> runningRecords = new ArrayList<RunningRecord>();
		ArrayAdapter<RunningRecord> aa = new ArrayAdapter<RunningRecord>(getApplicationContext(), android.R.layout.simple_list_item_1, runningRecords);
		runningRecordListView.setAdapter(aa);
		
		long runnerId = getIntent().getExtras().getLong("runnerId");
        ContentResolver cr = getContentResolver();
        Uri uri = Uri.parse(RunnerProvider.RUNNER_URI + "/" + String.valueOf(runnerId));
        Cursor c = cr.query(uri, null, null, null, null);
        c.moveToFirst();
		int runnerNumber = c.getInt(RunnerProvider.NUMBER_COLUMN);
		String runnerName = c.getString(RunnerProvider.NAME_COLUMN);
		Resources res = getResources();
		String title = String.format(res.getString(R.string.running_record_page_header), runnerName);
		runningRecordPageHeader.setText(title);

	    URI httpUri;
	    try {
	        String runningRecordUrl = getString(R.string.running_record_url);
	        httpUri = new URI(runningRecordUrl);
	        HttpRunningRecord httpRunningRecord = new HttpRunningRecord(httpUri);
//	        httpRunningRecord.extract(String.valueOf(runnerNumber));
	        httpRunningRecord.extract(String.valueOf(runnerNumber), runningRecords, aa);
	        // ランナー番号で結果を抽出
//	        ArrayList<RunningRecord> = httpGet.extract();
//	        String pageContent = httpGet.execute();
	        int i = 1;
	    } catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
//	      } catch (ParserConfigurationException e) {
//	        e.printStackTrace();
//	      } catch (SAXException e) {
//	        e.printStackTrace();
//	      }
	      finally {
	      }
		
	}

}
