package edu.edaily.sysuedaily.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class NewsDBHelper extends SQLiteOpenHelper {

	public static final String DBNAME = "news.db";
	public static final int VERSION = 1;
	
	public static final String T_HEADLINE = "headline_table";
	public static final String T_CAMPUS = "campus_table";
	public static final String T_LECTURE = "lecture_table";
	public static final String T_JOB = "job_table";
	public static final String T_VISION = "vision_table";
	public static final String T_NEWS = "news_db";
	
	public static final String C_ID = BaseColumns._ID;
	public static final String C_TITLE = "title";
	public static final String C_GLOBAL_ID = "global_id";
	public static final String C_SHORT_DESCRIPTION = "short_description";
	public static final String C_TEXT = "text";
	public static final String C_PIC = "pic";
	public static final String C_SPIC = "s_pic";
	public static final String C_DATE = "date";
	
	public static final String[] C_COLUMNS = {
		BaseColumns._ID, C_TITLE, C_GLOBAL_ID, C_SHORT_DESCRIPTION, C_TEXT, C_PIC, C_SPIC, C_DATE
	};
	
	public static final String T_COMMENT = "comment_table";
	public static final String C_CONTENT = "content";
	public static final String C_RATE = "rate";
	
	public static final String CREATE_NEWS_DB = "CREATE TABLE " + T_NEWS;
	
	public NewsDBHelper(Context context) {
		super(context, DBNAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE * IF EXISTS");
		
	}

}
