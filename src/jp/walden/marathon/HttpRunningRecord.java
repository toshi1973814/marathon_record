package jp.walden.marathon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.sql.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

//import android.content.ContentResolver;
//import android.widget.ArrayAdapter;

public class HttpRunningRecord {
	private URI uri;
	BufferedReader in = null;
	String NL;
	private static final String TAG = "HttpRunningRecord";
	
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
	        (new InputStreamReader(response.getEntity().getContent(), "Shift_JIS"));
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
	
	public void extract
	(String runnerNumber, RunningRecords rr) {
//	(String runnerNumber, ArrayList<RunningRecord> runningRecords,
//			ArrayAdapter<RunningRecord> aa, RunningRecords rr) {
//	public void extract(String runnerNumber) {
//	public ArrayList<RunningRecord> extract(String runnerNumber) {
        StringBuffer sb = new StringBuffer("");
//        String rawline = "";
        String line = "";
        boolean buffering = false;
        Integer ranking = 0;
        String time = "";
        String distance = "";
        Integer runnersTotal = 0;
        HashMap<String, Integer> distanceAndTotal = new HashMap<String, Integer>();
        Integer total = 0;
        Date runningDate = null;
        
        try {
            int counter = 0;
//            buffering = false;
            Pattern pDate = Pattern.compile("(\\d{4})\\.(\\d{1,2})\\.(\\d{1,2})");
            Pattern pDistance = Pattern.compile("(\\d+)\\s*(ｋｍ|ｋm)");
            Pattern pDistanceOption = Pattern.compile("(\\d+)\\s*(ｋｍ|ｋm)\\s*（(.+)）");
            Pattern pRunnersTotal = Pattern.compile("<td.+>\\s*(\\d+)\\s*</td>");
//            Pattern pDistanceMarker = Pattern.compile("\\*{5}");
			Pattern pTrStart = Pattern.compile("<(tr|TR).+>");
			Pattern pTrEnd = Pattern.compile("</(tr|TR)>");
			Pattern pRunnerNumber = Pattern.compile("<td.+>(" + runnerNumber + ")</td>");
			Pattern pRanking = Pattern.compile("<td.+>\\s*(\\d+)\\s*</td>");
			Pattern pTime = Pattern.compile("<td.+>\\s*(\\d+\\:\\d+.+)\\s*</td>");
//			while ((rawline = in.readLine()) != null) {
			while ((line = in.readLine()) != null) {

				// 文字コードを変換
//				byte[] byteLine = rawline.getBytes("Shift_JIS");
//				line = new String(byteLine,"Shift_JIS"); 
				
				// <tr></tr>のコンテンツをsbに格納
				Matcher mTrStart = pTrStart.matcher(line);
				if (mTrStart.find()) {
					buffering = true;
				}
				
				Matcher mTrEnd = pTrEnd.matcher(line);
				if (mTrEnd.find()) {

					buffering = false;
					sb.append(line + NL);
					String trContent = sb.toString();
					String[] trContentArray = trContent.split(NL);
					// 日付を抽出
					Matcher mDate = pDate.matcher(trContent);
					if(mDate.find()) {
						Integer matchedYear = Integer.valueOf(mDate.group(1));
						Integer matchedMonth = Integer.valueOf(mDate.group(2));
						Integer matchedDay = Integer.valueOf(mDate.group(3));
						runningDate = new Date(matchedYear-1900,matchedMonth,matchedDay);
					}
					
					// 距離を抽出
					Matcher mDistance = pDistance.matcher(trContent);
//					Matcher mDistanceMarker = pDistanceMarker.matcher(trContent);
//					if(mDistance.find() && mDistanceMarker.find()) {
					if(mDistance.find()) {
						Matcher mDistanceOption = pDistanceOption.matcher(trContent);
						distance = mDistance.group(1);
						if(distance.equals("１")) {
							distance = "1";
						} else if(distance.equals("３")) {
							distance = "3";
						} else if(distance.equals("５")) {
							distance = "5";
						} else if(distance.equals("１０")) {
							distance = "10";
						} else if(distance.equals("２０")) {
							distance = "20";
						}
						distance = distance + "km";
						
						if(mDistanceOption.find()) {
							String Option = mDistanceOption.group(3);
							if(Option.equals("Ａ")) {
								Option = "A";
							} else if(Option.equals("Ｂ")) {
								Option = "B";
							} else if(Option.equals("Ｃ")) {
								Option = "C";
							}
							distance = distance + " (" + Option + ")";
						}

						Matcher mRunnersTotal = pRunnersTotal.matcher(trContentArray[5]);
						if(mRunnersTotal.find()) {
							runnersTotal = Integer.valueOf(mRunnersTotal.group(1));
							distanceAndTotal.put(distance, runnersTotal); 
						}
					}
					
					//　<tr></tr>の中にナンバーが含まれていた場合、順位と記録を抽出
					String lineToSearch = trContentArray[2];
					Matcher mRunnerNumber = pRunnerNumber.matcher(lineToSearch);
					if(mRunnerNumber.find()) {
//						String matched = mRunnerNumber.group(1);
						// 順位を抽出
						Matcher mRanking = pRanking.matcher(trContentArray[1]);
						if(mRanking.find()) {
							ranking = Integer.valueOf(mRanking.group(1));
						}
						// タイムを抽出
						for(String text : trContentArray) {
							Matcher mTime = pTime.matcher(text);
							if(mTime.find()) {
								time = mTime.group(1);
								break;
							}
						}
						total = distanceAndTotal.get(distance);
//						total = (distanceAndTotal.get(distance) != null) ? distanceAndTotal.get(distance) : null;
						// TODO 仮の日付
//						Date date = new Date(2012-1900,3,21);
						RunningRecord record = new RunningRecord(Integer.valueOf(runnerNumber), runningDate, distance, ranking, total, time);
//						runningRecords.add(record);
//						aa.notifyDataSetChanged();
						rr.addRunningRecord(record);
					}
					sb.delete(0, sb.length());
				}
				if(buffering) {
					sb.append(line + NL);
				}
				counter++;
				if((counter % 1000) == 0) {
					Log.v(TAG, "counter=" + counter + " on " + uri.toString());
				}
			}
	        in.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
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

