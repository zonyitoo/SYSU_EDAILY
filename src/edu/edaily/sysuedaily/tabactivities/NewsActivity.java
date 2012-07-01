package edu.edaily.sysuedaily.tabactivities;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import edu.edaily.sysuedaily.R;

public class NewsActivity extends TabActivity {
	
	TabHost host;
	TabSpec headline, campus, lecture, job, vision, more;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news);
		
		host = getTabHost();
		
		headline = host.newTabSpec("HEADLINE").setIndicator("HEADLINE").setContent(new Intent(this, HeadlineActivity.class));
		host.addTab(headline);
		campus = host.newTabSpec("CAMPUS").setIndicator("CAMPUS").setContent(new Intent(this, CampusActivity.class));
		host.addTab(campus);
		lecture = host.newTabSpec("LECTURE").setIndicator("LECTURE").setContent(new Intent(this, LectureActivity.class));
		host.addTab(lecture);
		job = host.newTabSpec("JOB").setIndicator("JOB").setContent(new Intent(this, JobActivity.class));
		host.addTab(job);
		vision = host.newTabSpec("VISION").setIndicator("VISION").setContent(new Intent(this, VisionActivity.class));
		host.addTab(vision);
		more = host.newTabSpec("MORE").setIndicator("MORE").setContent(new Intent(this, MoreActivity.class));
		host.addTab(more);
		
		
	}
	
}
