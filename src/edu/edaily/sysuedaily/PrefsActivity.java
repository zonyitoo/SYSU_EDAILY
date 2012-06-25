package edu.edaily.sysuedaily;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import edu.edaily.sysuedaily.utils.Constant;

public class PrefsActivity extends PreferenceActivity {

	Preference weiboLogin;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.activity_prefs);
		
		
	}
}
