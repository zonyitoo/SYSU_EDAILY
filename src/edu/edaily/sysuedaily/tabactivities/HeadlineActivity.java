package edu.edaily.sysuedaily.tabactivities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.ViewFlipper;
import edu.edaily.sysuedaily.DetailActivity;
import edu.edaily.sysuedaily.R;
import edu.edaily.sysuedaily.utils.Constant;
import edu.edaily.sysuedaily.utils.NewsDBHelper;

public class HeadlineActivity extends Activity {
	
	ListView list;
	Cursor cursor;
	
	SQLiteDatabase newsdb;
	LinearLayout header;
	ImageView iv1, iv2, iv3;
	ViewFlipper vf;
	
	private static final String[] FROM = {NewsDBHelper.C_TITLE, NewsDBHelper.C_SHORT_DESCRIPTION};
	private static final int[] TO = {R.id.textview_activity_headline_list_content_title, R.id.textview_activity_headline_list_content_short};
	
	ArrayList<HashMap<String, Object>> headcontent;
	
	String[] testArray = {"图书馆：什么样的饮品可以带？",
			"2月29日系“女性表白日” 男性拒绝需付出代价",
			"AV女优进课堂 合适否？",
			"谷歌社交网站GOOGLE+难以提高用户活跃度",
			"珠海宝镜湾岩画中的巫舞"};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_headline);
		
		list = (ListView) findViewById(R.id.listview_activity_headline);
		
		newsdb = new NewsDBHelper(this).getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(NewsDBHelper.C_PIC, "");
		values.put(NewsDBHelper.C_SPIC, "");
		values.put(NewsDBHelper.C_DATE, new Date().toString());
		values.put(NewsDBHelper.C_SHORT_DESCRIPTION, "DESCRIPTION");
		values.put(NewsDBHelper.C_TEXT, "");
		for (int i = 0; i < 3; ++ i) {
			values.put(NewsDBHelper.C_GLOBAL_ID, i);
			values.put(NewsDBHelper.C_TITLE, testArray[i]);
			newsdb.insertOrThrow(NewsDBHelper.T_HEADLINE, null, values);
		}
		newsdb.close();
		
		newsdb = new NewsDBHelper(this).getReadableDatabase();
		cursor = queryNews(newsdb);
		
		headcontent = new ArrayList<HashMap<String, Object>>();
		cursor.moveToFirst();
		for (int i = 0; i < 3; ++ i) {
			HashMap<String, Object> head = new HashMap<String, Object>();
			head.put(NewsDBHelper.C_TITLE, cursor.getString(cursor.getColumnIndexOrThrow(NewsDBHelper.C_TITLE)));
			head.put(NewsDBHelper.C_GLOBAL_ID, cursor.getLong(cursor.getColumnIndexOrThrow(NewsDBHelper.C_GLOBAL_ID)));
			headcontent.add(head);
			cursor.moveToNext();
		}
		
		header = (LinearLayout) LayoutInflater.from(HeadlineActivity.this).inflate(R.layout.linearlayout_headline_slide, null);
		iv1 = (ImageView) header.findViewById(R.id.imageview_headline_slide_1);
		new FetchPic(iv1).execute((Long) headcontent.get(0).get(NewsDBHelper.C_GLOBAL_ID));
		iv2 = (ImageView) header.findViewById(R.id.imageview_headline_slide_2);
		new FetchPic(iv2).execute((Long) headcontent.get(1).get(NewsDBHelper.C_GLOBAL_ID));
		iv3 = (ImageView) header.findViewById(R.id.imageview_headline_slide_3);
		new FetchPic(iv3).execute((Long) headcontent.get(2).get(NewsDBHelper.C_GLOBAL_ID));
		vf = (ViewFlipper) header.findViewById(R.id.viewflipper_headline_slide);
		
		
		list.addHeaderView(header);
		list.setAdapter(new CursorAdapter(this, cursor));
		list.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 == 0) return;
				
				cursor.moveToPosition(arg2 - 1);
				Intent intent = new Intent(HeadlineActivity.this, DetailActivity.class);
				intent.putExtra(Constant.NEWS_KIND, "HEADLINE");
				intent.putExtra(Constant.NEWS_GID, cursor.getLong(cursor.getColumnIndexOrThrow(NewsDBHelper.C_GLOBAL_ID)));
				intent.putExtra(Constant.NEWS_TITLE, cursor.getString(cursor.getColumnIndexOrThrow(NewsDBHelper.C_TITLE)));
				intent.putExtra(Constant.NEWS_TEXT, cursor.getString(cursor.getColumnIndexOrThrow(NewsDBHelper.C_SHORT_DESCRIPTION)));
				startActivity(intent);
			}
			
		});
	}
	
	@Override
	protected void onDestroy() {
		newsdb.close();
		super.onDestroy();
	}

	private Cursor queryNews(SQLiteDatabase db) {
		return db.query(NewsDBHelper.T_HEADLINE, NewsDBHelper.C_COLUMNS, null, null, null, null, null);
	}
	
	class CursorAdapter extends SimpleCursorAdapter {

		public CursorAdapter(Context context, Cursor c) {
			super(context, R.layout.linearlayout_headline_activity_list_content, c, FROM, TO);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			super.bindView(view, context, cursor);
			
			LinearLayout ll = (LinearLayout) view;
			long gid = cursor.getLong(cursor.getColumnIndex(NewsDBHelper.C_GLOBAL_ID));
			new FetchSPic((ImageView) ll.findViewById(R.id.imageview_headline_list_content_spic)).execute(gid);
		}
		
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
	
	class FetchSPic extends AsyncTask<Long, Void, Bitmap> {

		ImageView simage;
		public FetchSPic(ImageView v) {
			simage = v;
		}
		
		@Override
		protected Bitmap doInBackground(Long... arg0) {
			Bitmap img = null;
			if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
				String filepath = Constant.PATH_SPIC + arg0[0];
				
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
