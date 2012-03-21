package jp.walden.marathon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.URI;
import java.util.ArrayList;

public class httpRunningRecord {
	private URI uri;
	BufferedReader in = null;
	String NL;
	
	public httpRunningRecord(URI uri) {
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
	
	public ArrayList<RunningRecord> extract() {
        StringBuffer sb = new StringBuffer("");
        String line = "";
		// TODO Auto-generated method stub
        try {
			while ((line = in.readLine()) != null) {
			    sb.append(line + NL);
			}
	        in.close();
		} catch (IOException e) {
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
		return null;
	}
}

