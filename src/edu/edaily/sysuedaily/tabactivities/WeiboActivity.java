package edu.edaily.sysuedaily.tabactivities;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.weibo.net.AccessToken;
import com.weibo.net.DialogError;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboDialogListener;
import com.weibo.net.WeiboException;
import com.weibo.net.WeiboParameters;

import edu.edaily.sysuedaily.R;
import edu.edaily.sysuedaily.utils.Constant;

public class WeiboActivity extends Activity {
	
	ListView list;
	SharedPreferences prefs;
	
	String access_token;
	String expires_in;
	
	Weibo weibo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weibo);
		
		list = (ListView) findViewById(R.id.listview_activity_weibo);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		access_token = prefs.getString("access_token", null);
		
		weibo = Weibo.getInstance();
		weibo.setupConsumerConfig(Constant.CONSUMER_KEY, Constant.CONSUMER_SECRET);
		if (access_token == null) {
			weibo.setRedirectUrl("https://api.weibo.com/oauth2/default.html");
			weibo.authorize(WeiboActivity.this, new AuthDialogListener());
		}
		else {
			AccessToken accessToken = new AccessToken(access_token, Constant.CONSUMER_SECRET);
			expires_in = prefs.getString("expires_in", null);
			accessToken.setExpiresIn(expires_in);
			Weibo.getInstance().setAccessToken(accessToken);
		}
		refreshList();
	}
	
	void refreshList() {
		String url = Weibo.SERVER + "statuses/user_timeline.json";
        WeiboParameters bundle = new WeiboParameters();
        bundle.add("source", Weibo.getAppKey());
        bundle.add("uid", "1899452321");
        
        try {
			String rlt = weibo.request(this, url, bundle, "GET", weibo.getAccessToken());
			JSONObject ret = new JSONObject(rlt);
			JSONArray xx = (JSONArray) ret.get("statuses");
			Log.d(Constant.DEBUG_TAB, xx.length() + "");
			
			ArrayList<HashMap<String, Object>> array = new ArrayList<HashMap<String, Object>>();
			for (int i = 0; i < xx.length(); ++ i) {
				JSONObject obj = xx.getJSONObject(i);
				HashMap<String, Object> hashmap = new HashMap<String, Object>();
				hashmap.put("text", obj.get("text"));
				array.add(hashmap);
			}
			
			SimpleAdapter adapter = new SimpleAdapter(this, 
					array, 
					android.R.layout.simple_list_item_1,
					new String[] {"text"},
					new int[] {android.R.id.text1});
			list.setAdapter(adapter);
			
		} catch (WeiboException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	class AuthDialogListener implements WeiboDialogListener {

		public void onComplete(Bundle values) {
			access_token = values.getString("access_token");
			expires_in = values.getString("expires_in");
			prefs.edit().putString("access_token", access_token).putString("expires_in", expires_in).commit();
			AccessToken accessToken = new AccessToken(access_token, Constant.CONSUMER_SECRET);
			accessToken.setExpiresIn(expires_in);
			Weibo.getInstance().setAccessToken(accessToken);
			
		}

		public void onError(DialogError e) {
			Toast.makeText(getApplicationContext(),
					"Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
		}

		public void onCancel() {
			Toast.makeText(getApplicationContext(), "Auth cancel",
					Toast.LENGTH_LONG).show();
		}

		public void onWeiboException(WeiboException e) {
			Toast.makeText(getApplicationContext(),
					"Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
					.show();
		}

	}

}
