package edu.edaily.sysuedaily;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class PrefsActivity extends PreferenceActivity {

	Preference weiboLogin;
	
	SharedPreferences prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.activity_prefs);
		
		weiboLogin = this.findPreference("prefs_user_weibo_profile");
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		String access_token = prefs.getString("access_token", null);
		if (access_token == null) {
			weiboLogin.setTitle("UNbound Weibo");
		}
		else {
			weiboLogin.setTitle("@" + prefs.getString("user_name", null));
		}
	}
}
