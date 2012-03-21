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
import android.widget.ListView;
import android.widget.TextView;

public class RunningRecords extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.running_record);
		ListView runningRecord = (ListView)findViewById(R.id.running_record);
		TextView runningRecordPageTitle = (TextView)findViewById(R.id.running_record_page_title);
		
		long runnerId = getIntent().getExtras().getLong("runnerId");
        ContentResolver cr = getContentResolver();
        Uri uri = Uri.parse(RunnerProvider.RUNNER_URI + "/" + String.valueOf(runnerId));
        Cursor c = cr.query(uri, null, null, null, null);
        c.moveToFirst();
		int runnerNumber = c.getInt(RunnerProvider.NUMBER_COLUMN);
		String runnerName = c.getString(RunnerProvider.NAME_COLUMN);
		Resources res = getResources();
		String title = String.format(res.getString(R.string.running_record_page_title), runnerName);
		runningRecordPageTitle.setText(title);

	    URI httpUri;
	    try {
	        String runningRecordUrl = getString(R.string.running_record_url);
	        httpUri = new URI(runningRecordUrl);
	        httpRunningRecord httpGet = new httpRunningRecord(httpUri);
	        // ランナー番号で結果を抽出
//	        ArrayList<RunningRecord> = httpGet.extract();
//	        String pageContent = httpGet.execute();
	        int i = 1;
	    } catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
