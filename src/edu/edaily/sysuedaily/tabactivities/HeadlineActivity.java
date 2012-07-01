package edu.edaily.sysuedaily.tabactivities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
import android.os.Handler;
import android.os.Parcelable;
import android.provider.BaseColumns;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import edu.edaily.sysuedaily.DetailActivity;
import edu.edaily.sysuedaily.R;
import edu.edaily.sysuedaily.utils.Constant;
import edu.edaily.sysuedaily.utils.NewsDBHelper;

public class HeadlineActivity extends Activity {
	
	ListView list;
	Cursor cursor;
	
	SQLiteDatabase newsdb;
	FrameLayout header;
	ImageView iv1, iv2, iv3;
	ViewPager pager;
	TextView pageTitle;
	
	int currentItem = 0;
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			pager.setCurrentItem(currentItem);// 切换当前显示的图片
		};
	};
	
	boolean[] picstat;
	boolean[] spicstat;
	
	Bitmap[] headpic;
	Bitmap[] spic;
	
	ArrayList<ImageView> headImgViews;
	ArrayList<View> dots;
	
	private ScheduledExecutorService scheduledExecutorService;
	
	private static final String[] FROM = {NewsDBHelper.C_TITLE, NewsDBHelper.C_SHORT_DESCRIPTION};
	private static final int[] TO = {R.id.textview_activity_headline_list_content_title, R.id.textview_activity_headline_list_content_short};
	
	ArrayList<HashMap<String, Object>> headcontent;
	
	String[] testArray = {"今日岭南学院教育改革会召开",
			"2月29日系“女性表白日” ",
			"AV女优进课堂 合适否？",
			"谷歌社交网站G+难以提高用户活跃度",
			"珠海宝镜湾岩画中的巫舞"};
	String[] textArray = {"        6月19日下午，岭南学院本科教育改革动员大会在岭南MBA中心大楼召开。" +
			"我校校长许宁生、校长助理李文军，岭南学院院长徐信忠、书记张文彪，以及岭南学院全体教职员工、各项目主任、近40名学生代表参加了此次大会。" +
			"\n        主管本科教学的陆军副院长介绍了岭南学院本科教育改革方案，详细阐述了改革的原因、理念及岭南学院本科项目教育目标，" +
			"介绍了通过博雅教育与专业教育的整合、多维的教学评估和外部考官制度内外相结合的质量保障体系、学生指导方案、海外实习与社会实践等翔实的举措，" +
			"培养适应社会需要的卓越人才。陆军副院长说，在本次本科教育改革的酝酿、思考、讨论和形成的改革方案过程中，学院始终牢记本科教育的核心——学生，" +
			"学生的未来必将影响社会乃至人类的未来。正因为如此，教育改革必须极其慎重，必须是渐进、分阶段的，要实现改革目标还需要全体教师长期、" +
			"艰辛的努力和无私奉献，我们会以谦卑的心态不断学习、思考、改进。", "", "", "", ""};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_headline);
		
		// Init
		header = (FrameLayout) LayoutInflater.from(HeadlineActivity.this).inflate(R.layout.linearlayout_headline_slide, null);
		pageTitle = (TextView) header.findViewById(R.id.textview_headline_slide);
		pager = (ViewPager) header.findViewById(R.id.viewpager_headline_slide);
		list = (ListView) findViewById(R.id.listview_activity_headline);
		
		picstat = new boolean[3];
		spicstat = new boolean[1000];
		for (int i = 0; i < picstat.length; ++ i) picstat[i] = false;
		for (int i = 0; i < spicstat.length; ++ i) spicstat[i] = false;
		
		headpic = new Bitmap[3];
		for (int i = 0; i < headpic.length; ++ i) headpic[i] = null;
		spic = new Bitmap[1000];
		for (int i = 0; i < spic.length; ++ i) spic[i] = null;
		
		headImgViews = new ArrayList<ImageView>();
		headcontent = new ArrayList<HashMap<String, Object>>();
		dots = new ArrayList<View>();
		dots.add(header.findViewById(R.id.view_headline_slide_dot0));
		dots.add(header.findViewById(R.id.view_headline_slide_dot1));
		dots.add(header.findViewById(R.id.view_headline_slide_dot2));
		
		newsdb = new NewsDBHelper(this).getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(NewsDBHelper.C_PIC, "");
		values.put(NewsDBHelper.C_SPIC, "");
		values.put(NewsDBHelper.C_DATE, new Date().toString());
		values.put(NewsDBHelper.C_SHORT_DESCRIPTION, "DESCRIPTION");
		for (int i = 0; i < 3; ++ i) {
			values.put(NewsDBHelper.C_GLOBAL_ID, i);
			values.put(NewsDBHelper.C_TITLE, testArray[i]);
			values.put(NewsDBHelper.C_TEXT, textArray[i]);
			newsdb.insertOrThrow(NewsDBHelper.T_HEADLINE, null, values);
		}
		newsdb.close();
		
		// 构造三个头条 headcontent
		newsdb = new NewsDBHelper(this).getReadableDatabase();
		cursor = queryNews(newsdb);
		cursor.moveToFirst();
		for (int i = 0; i < 3; ++ i) {
			HashMap<String, Object> head = new HashMap<String, Object>();
			head.put(NewsDBHelper.C_TITLE, cursor.getString(cursor.getColumnIndexOrThrow(NewsDBHelper.C_TITLE)));
			head.put(NewsDBHelper.C_GLOBAL_ID, cursor.getLong(cursor.getColumnIndexOrThrow(NewsDBHelper.C_GLOBAL_ID)));
			headcontent.add(head);
			cursor.moveToNext();
		}
		OnClickListener clickImage = new OnClickListener() {

			public void onClick(View v) {
				ImageView view = (ImageView) v;
				int tag = (Integer) v.getTag();
				cursor.moveToPosition(tag);
				Intent intent = new Intent(HeadlineActivity.this, DetailActivity.class);
				intent.putExtra(Constant.NEWS_KIND, "HEADLINE");
				intent.putExtra(Constant.NEWS_TABLE, NewsDBHelper.T_HEADLINE);
				intent.putExtra(Constant.NEWS_ID, cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID)));
				intent.putExtra(Constant.NEWS_GID, cursor.getLong(cursor.getColumnIndexOrThrow(NewsDBHelper.C_GLOBAL_ID)));
				intent.putExtra(Constant.NEWS_TITLE, cursor.getString(cursor.getColumnIndexOrThrow(NewsDBHelper.C_TITLE)));
				startActivity(intent);
			}
			
		};
		// 初始化图片ImageView
		for (int i = 0; i < headpic.length; i++) {
			ImageView imageView = new ImageView(this);
			imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			imageView.setImageResource(R.drawable.headline_pic_loading);
			imageView.setTag(i);
			imageView.setOnClickListener(clickImage);
			if (headpic[i] == null)
				new FetchPic(imageView).execute((Long) headcontent.get(i).get(NewsDBHelper.C_GLOBAL_ID), (long) i);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			headImgViews.add(imageView);
		}
		pageTitle.setText((String) headcontent.get(0).get(NewsDBHelper.C_TITLE));
		pager.setAdapter(new PageAdapter());// 设置填充ViewPager页面的适配器
		// 设置一个监听器，当ViewPager中的页面改变时调用
		pager.setOnPageChangeListener(new MyPageChangeListener());
		
		
		list.addHeaderView(header);
		list.setAdapter(new CursorAdapter(this, cursor));
		list.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 == 0) return;
				
				cursor.moveToPosition(arg2 - 1);
				Intent intent = new Intent(HeadlineActivity.this, DetailActivity.class);
				intent.putExtra(Constant.NEWS_KIND, "HEADLINE");
				intent.putExtra(Constant.NEWS_TABLE, NewsDBHelper.T_HEADLINE);
				intent.putExtra(Constant.NEWS_ID, cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID)));
				intent.putExtra(Constant.NEWS_GID, cursor.getLong(cursor.getColumnIndexOrThrow(NewsDBHelper.C_GLOBAL_ID)));
				intent.putExtra(Constant.NEWS_TITLE, cursor.getString(cursor.getColumnIndexOrThrow(NewsDBHelper.C_TITLE)));
				startActivity(intent);
			}
			
		});
	}
	
	@Override
	protected void onStart() {
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		// 当Activity显示出来后，每两秒钟切换一次图片显示
		scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 5, TimeUnit.SECONDS);
		super.onStart();
	}

	@Override
	protected void onStop() {
		// 当Activity不可见的时候停止切换
		scheduledExecutorService.shutdown();
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		newsdb.close();
		super.onDestroy();
	}
	
	private class ScrollTask implements Runnable {

		public void run() {
			synchronized (pager) {
				System.out.println("currentItem: " + currentItem);
				currentItem = (currentItem + 1) % headImgViews.size();
				handler.obtainMessage().sendToTarget(); // 通过Handler切换图片
			}
		}

	}
	
	private class MyPageChangeListener implements OnPageChangeListener {
		private int oldPosition = 0;

		/**
		 * This method will be invoked when a new page becomes selected.
		 * position: Position index of the new selected page.
		 */
		public void onPageSelected(int position) {
			currentItem = position;
			pageTitle.setText((String) headcontent.get(position).get(NewsDBHelper.C_TITLE));
			dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
			dots.get(position).setBackgroundResource(R.drawable.dot_focused);
			oldPosition = position;
		}

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}
	
	private class PageAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return headcontent.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(headImgViews.get(arg1));
			return headImgViews.get(arg1);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {

		}
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
		
		Long gid;
		int listid;
		ImageView view;
		
		public FetchPic(ImageView view) {
			this.view = view;
		}
		
		@Override
		protected Bitmap doInBackground(Long... arg0) {
			gid = arg0[0];
			listid = arg0[1].intValue();
			
			Bitmap img = null;
			if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
				final String filepath = Constant.PATH_PIC + arg0[0] + ".png";
				
				if (picstat[listid])
					this.cancel(true);
				else {
					picstat[listid] = true;
					
					File p = new File(filepath);
					
					if (p.isFile()) {
						img = BitmapFactory.decodeFile(filepath);
					}
					else {
						if (!isCancelled()) {
							
						}
					}
				}
			}
			return img;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			headpic[listid] = result;
			view.setImageBitmap(result);
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
