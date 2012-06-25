package edu.edaily.sysuedaily;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import edu.edaily.sysuedaily.utils.Constant;

public class DetailActivity extends Activity {
	
	Button back, send;
	TextView kind, title, date, text;
	EditText comment;
	ImageView pic;
	
	long gid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		
		back = (Button) findViewById(R.id.button_activity_detail_back);
		kind = (TextView) findViewById(R.id.textview_activity_detail_kind);
		title = (TextView) findViewById(R.id.textview_activity_detail_title);
		date = (TextView) findViewById(R.id.textview_activity_detail_datefrom);
		text = (TextView) findViewById(R.id.textview_activity_detail_text);
		pic = (ImageView) findViewById(R.id.imageview_activity_detail_pic);
		comment = (EditText) findViewById(R.id.edittext_activity_detail_comment);
		
		Intent intent = getIntent();
		kind.setText(intent.getStringExtra(Constant.NEWS_KIND));
		title.setText(intent.getStringExtra(Constant.NEWS_TITLE));
		text.setText(intent.getStringExtra(Constant.NEWS_TEXT));
		gid = intent.getLongExtra(Constant.NEWS_GID, 0);
		
		back.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				DetailActivity.this.finish();
				
			}
			
		});
		
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
		imm.hideSoftInputFromWindow(comment.getWindowToken(), 0);
		
		new FetchPic(pic).execute(gid);
	}
	
	
	class FetchPic extends AsyncTask<Long, Void, Bitmap> {
		ImageView simage;
		public FetchPic(ImageView v) {
			simage = v;
		}
		
		@Override
		protected Bitmap doInBackground(Long... arg0) {
			Bitmap img = null;
			if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
				String filepath = Constant.PATH_PIC + arg0[0];
				
				if (new File(filepath + ".lock").isFile())
					this.cancel(true);
				else {
					File lockfile = new File(filepath + ".lock");
					try {
						lockfile.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					File p = new File(filepath + ".png");
					
					if (p.isFile()) {
						img = BitmapFactory.decodeFile(filepath + ".png");
					}
					else {
						if (!isCancelled()) {
							
						}
					}
					
					lockfile.delete();
				}
			}
			return img;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (result != null)
				simage.setImageBitmap(result);
			super.onPostExecute(result);
		}
	}
}
