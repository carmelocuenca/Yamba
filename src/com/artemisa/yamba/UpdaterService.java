package com.artemisa.yamba;

import winterwell.jtwitter.TwitterException;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class UpdaterService extends Service {
	private static final String TAG = "UpdaterService";
	private static final int DELAY = 60000; // a minute
	private boolean runFlag = false;
	private Updater updater;

	

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
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

		Log.d(TAG, "onStarted before");
		runFlag = true;
		try {
			updater.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "onStartCommand()", e);
			e.printStackTrace();
		}

		Log.d(TAG, "onStarted");
		return START_STICKY;
	}

	/*
	 * Threads tha permos the actual update from the online service
	 */
	private class Updater extends Thread {

		public Updater() {
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
						YambaApplication yamba = (YambaApplication) updaterService.getApplication();
						int newUpdates = yamba.fetchStatusUpdates();
						if (newUpdates>0){
							Log.d(TAG, "We have a new status");
						}
						Thread.sleep(DELAY);
					} catch (TwitterException e) {
						Log.e(TAG, "Failed to connect to twitter service", e);
					}

					
				} catch (InterruptedException e) {
					updaterService.runFlag = false;
				}
			}
		}

	}

}
