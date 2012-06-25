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
	
	private static final String CREATE_NEWS_COLUMNS = " ("
			+ C_ID					+ " INT PRIMARY KEY, "
			+ C_TITLE				+ " TEXT NOT NULL, "
			+ C_GLOBAL_ID			+ " INT NOT NULL, "
			+ C_SHORT_DESCRIPTION	+ " TEXT NOT NULL, "
			+ C_TEXT				+ " TEXT NOT NULL, "
			+ C_PIC					+ " TEXT NOT NULL, "
			+ C_SPIC				+ " TEXT NOT NULL, "
			+ C_DATE				+ " TEXT NOT NULL"
			+ ")";
	private static final String CREATE_HEADLINE = "CREATE TABLE " + T_HEADLINE + CREATE_NEWS_COLUMNS;
	private static final String CREATE_CAMPUS = "CREATE TABLE " + T_CAMPUS + CREATE_NEWS_COLUMNS;
	private static final String CREATE_LECTURE = "CREATE TABLE " + T_LECTURE + CREATE_NEWS_COLUMNS;
	private static final String CREATE_JOB = "CREATE TABLE " + T_JOB + CREATE_NEWS_COLUMNS;
	private static final String CREATE_VISION = "CREATE TABLE " + T_VISION + CREATE_NEWS_COLUMNS;
	
	public NewsDBHelper(Context context) {
		super(context, DBNAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_HEADLINE);
		db.execSQL(CREATE_CAMPUS);
		db.execSQL(CREATE_LECTURE);
		db.execSQL(CREATE_JOB);
		db.execSQL(CREATE_VISION);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE * IF EXISTS");
		
	}

}
