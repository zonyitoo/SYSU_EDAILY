package edu.edaily.sysuedaily.utils;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Base64;

public class Remote {
	public static class News {
		public JSONObject getNews(JSONObject object) throws IOException, JSONException {
			JSONObject ret = postConnect(object, Constant.SRV_URL + Constant.SYNC_NEWS);
			
			return ret;
		}
		
		public boolean getPic(String urlstr, String path) throws IOException {
			
			try {
				URL url = new URL(urlstr);
				URLConnection connection = url.openConnection();
				InputStream is = connection.getInputStream();
				byte[] bs = new byte[30720];
				int len;
				
				OutputStream os = new FileOutputStream(path);
				
				while ((len = is.read(bs)) != -1) {
					os.write(bs, 0, len);
				}
				
				os.close();
				is.close();
				
				return true;
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return false;
		}
	}
	
	
	
	private static JSONObject postConnect(JSONObject object, String serverurl) throws IOException, JSONException {
		JSONObject retobj = null;
		
		try {
			URL url = new URL(serverurl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			
			connection.connect();
			
			String postStr = Base64.encodeToString(object.toString().getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);
			
			PrintWriter pw = new PrintWriter(connection.getOutputStream());
			pw.print(postStr);
			pw.flush();
			pw.close();
			
			int respCode = connection.getResponseCode();
			
			if (respCode == Constant.SRV_RESP_SUC) {
				InputStreamReader isr = new InputStreamReader(connection.getInputStream());
				BufferedReader br = new BufferedReader(isr);
				String respMsg = br.readLine();
				
				retobj = new JSONObject((String) (new JSONTokener(respMsg).nextValue()));
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return retobj;
	}
}
