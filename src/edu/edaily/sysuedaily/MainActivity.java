package edu.edaily.sysuedaily;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import edu.edaily.sysuedaily.tabactivities.HelpActivity;
import edu.edaily.sysuedaily.tabactivities.IrrigationActivity;
import edu.edaily.sysuedaily.tabactivities.NewsActivity;
import edu.edaily.sysuedaily.tabactivities.TopicActivity;
import edu.edaily.sysuedaily.tabactivities.WeiboActivity;

public class MainActivity extends TabActivity {
	
	TabHost tabhost;
	TabSpec weibo, news, topic, help, irrigation;
	Button discover;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tabhost = getTabHost();
		
		news = tabhost.newTabSpec("NEWS").setIndicator("NEWS").setContent(new Intent(this, NewsActivity.class));
		tabhost.addTab(news);
		topic = tabhost.newTabSpec("TOPIC").setIndicator("TOPIC").setContent(new Intent(this, TopicActivity.class));
		tabhost.addTab(topic);
		weibo = tabhost.newTabSpec("WEIBO").setIndicator("WEIBO").setContent(new Intent(this, WeiboActivity.class));
		tabhost.addTab(weibo);
		help = tabhost.newTabSpec("HELP").setIndicator("HELP").setContent(new Intent(this, HelpActivity.class));
		tabhost.addTab(help);
		irrigation = tabhost.newTabSpec("IRRIGATION").setIndicator("IRRIGATION").setContent(new Intent(this, IrrigationActivity.class));
		tabhost.addTab(irrigation);
		
		discover = (Button) findViewById(R.id.button_activity_main_discover);
		discover.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				startActivity(new Intent(MainActivity.this, DiscoverActivity.class));
				
			}
			
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuitem_activity_main_setting:
			startActivity(new Intent(this, PrefsActivity.class));
			break;
		}
		
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main_menu, menu);
		
		return true;
	}

}
