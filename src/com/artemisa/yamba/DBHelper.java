package com.artemisa.yamba;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	static final String TAG = DBHelper.class.getSimpleName();
	static final String DB_NAME = "timeline.db";
	static final int DB_VERSION = 1; //
	static final String TABLE = "timeline";
	static final String C_ID = "id";
	static final String C_CREATED_AT = "created_at";
	static final String C_SOURCE = "source";
	static final String C_TEXT = "txt";
	static final String C_USER = "user";

	public DBHelper(Context context) {
		// TODO Auto-generated constructor stub

		super(context, DB_NAME, null, DB_VERSION);
	}

	// Called only once, first time the DB is created
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = "create table " + TABLE + " (" + C_ID
				+ " int primary key, " + C_CREATED_AT + " int, " + C_SOURCE + " text, " + C_USER
				+ " text, " + C_TEXT + " text)";
		db.execSQL(sql);
		Log.d(TAG, "onCreated sql:" + sql);
	}

	// Called whenever newVersion != to oldVersion
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		// Typically do ALTER TABLE statements, but...we're just in development,
		// so:
		db.execSQL("drop table if exists " + TABLE); // drops the old database
		Log.d(TAG, "onUpdated");
		onCreate(db); // run onCreate to get new database
	}

}
