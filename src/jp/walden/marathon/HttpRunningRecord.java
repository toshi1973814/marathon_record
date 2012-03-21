package jp.walden.marathon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.sql.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpRunningRecord {
	private URI uri;
	BufferedReader in = null;
	String NL;
	
	public HttpRunningRecord(URI uri) {
		super();
		this.uri = uri;
		initialize();
	}
	protected void initialize() {
		try {
	        HttpClient client = new DefaultHttpClient();
	        HttpGet request = new HttpGet();
	        request.setURI(uri);
	        HttpResponse response = client.execute(request);
	        in = new BufferedReader
	        (new InputStreamReader(response.getEntity().getContent()));
	        NL = System.getProperty("line.separator");
		} catch (IOException e) {
			e.printStackTrace();
		}
//	        while ((line = in.readLine()) != null) {
//	            sb.append(line + NL);
//	        }
//	        in.close();
//	        String pageContent = sb.toString();
//	        return pageContent;
//	    }
	}
	
	public void extract(String runnerNumber) {
//	public ArrayList<RunningRecord> extract(String runnerNumber) {
        StringBuffer sb = new StringBuffer("");
        String line = "";
        boolean buffering;
        int ranking = 0;
        String time = "";
        
		// TODO Auto-generated method stub
        try {
            int counter = 0;
            buffering = false;
			Pattern p1 = Pattern.compile("<(tr|TR).+>");
			Pattern p2 = Pattern.compile("</(tr|TR)>");
			Pattern p3 = Pattern.compile("<td.+>(" + runnerNumber + ")</td>");
			Pattern p4 = Pattern.compile("<td.+>(\\d+)</td>");
			Pattern p5 = Pattern.compile("<td.+>(\\d+\\:\\d+.+)</td>");
			while ((line = in.readLine()) != null) {
				//test
//				int res = 0;
//				Pattern p = Pattern.compile("html");
//				Matcher m = p.matcher(line);
//				while (m.find()) { // Find each match in turn; String can't do this.
//					res = 1;
//				 }
				
				// <tr></tr>のコンテンツをsbに格納
				Matcher m1 = p1.matcher(line);
				if (m1.find()) {
					buffering = true;
				}
//				m1.reset();
				
				Matcher m2 = p2.matcher(line);
				if (m2.find()) {
					buffering = false;
					//　<tr></tr>の中に番号が含まれていた場合、順位と記録を抽出
					sb.append(line + NL);
					String trContent = sb.toString();
					String[] trContentArray = trContent.split(NL);
//					Pattern p3 = Pattern.compile("<td.+>" + runnerNumber + "</td>");
					String lineToSearch = trContentArray[2];
					Matcher m3 = p3.matcher(lineToSearch);
					if(m3.find()) {
						String matched = m3.group(1);
						// 順位を抽出
						Matcher m4 = p4.matcher(trContentArray[1]);
						if(m4.find()) {
							ranking = Integer.valueOf(m4.group(1));
						}
//						m4.reset();
						// タイムを抽出
						for(String text : trContentArray) {
							Matcher m5 = p5.matcher(text);
							if(m5.find()) {
								time = m5.group(1);
								break;
							}
//							m5.reset();
						}
						Date date = new Date(2012,3,21);
						Integer distance = 5;
						RunningRecord record = new RunningRecord(Integer.valueOf(runnerNumber), date, distance, ranking, time);
					}
//					m3.reset();
					sb.delete(0, sb.length());
				}
//				m2.reset();
				if(buffering) {
					sb.append(line + NL);
				}
				counter++;
			}
	        in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
	        if (in != null) {
	            try {
	                in.close();
	                } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
		}
//		return null;
	}
}

