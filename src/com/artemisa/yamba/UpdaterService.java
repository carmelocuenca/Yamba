package com.artemisa.yamba;

import java.util.List;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

public class UpdaterService extends Service {
	private static final String TAG = "UpdaterService";
	private static final int DELAY = 60000; // a minute
	private boolean runFlag = false;
	private Updater updater;
	private YambaApplication yamba;
	private DBHelper dbHelper;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		yamba = (YambaApplication) getApplication();
		dbHelper = new DBHelper(yamba);
		updater = new Updater();

		Log.d(TAG, "onCreated");

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		runFlag = false;
		updater.interrupt();
		updater = null;
		Log.d(TAG, "onDestroyed");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		super.onStartCommand(intent, flags, startId);

		runFlag = true;
		updater.start();

		Log.d(TAG, "onStarted");
		return START_STICKY;
	}

	/*
	 * Threads tha permos the actual update from the online service
	 */
	private class Updater extends Thread {
		List<Twitter.Status> timeline;

		private Updater() {
			super("UpdaterService-Updater");
			// TODO Auto-generated constructor stub
		}

		@Override
		public void run() {
			UpdaterService updaterService = UpdaterService.this;
			while (updaterService.runFlag) {
				Log.d(TAG, "Updater running");
				try {
					try {
						// Get the timeline from the cloud
						timeline = updaterService.yamba.getTwitter()
								.getFriendsTimeline();
					} catch (TwitterException e) {
						Log.e(TAG, "Failed to connect to twitter service", e);
					}

					// Open the database for writing
					SQLiteDatabase db = dbHelper.getWritableDatabase();

					// Loop over the timeline and print it out
					ContentValues values = new ContentValues();
					for (Twitter.Status status : timeline) {
						// Insert into database
						values.clear();
						values.put(DBHelper.C_ID, status.id);
						values.put(DBHelper.C_CREATED_AT,
								status.createdAt.getTime());
						values.put(DBHelper.C_SOURCE, status.source);
						values.put(DBHelper.C_TEXT, status.text);
						values.put(DBHelper.C_USER, status.user.name);
						try {
							db.insertOrThrow(DBHelper.TABLE, null, values);
							Log.d(TAG, String.format("%s: %s",
									status.user.name, status.text));
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							Log.e(TAG, e.toString());
							e.printStackTrace();
						}

					}
					db.close();

					Log.d(TAG, "Updater ran");
					Thread.sleep(DELAY);
				} catch (InterruptedException e) {
					updaterService.runFlag = false;
				}
			}
		}

	}

}
