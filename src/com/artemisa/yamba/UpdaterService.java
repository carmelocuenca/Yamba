package com.artemisa.yamba;

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

		runFlag = true;
		updater.start();

		Log.d(TAG, "onStarted");
		return START_STICKY;
	}

	/*
	 * Threads tha permos the actual update from the online service
	 */
	private class Updater extends Thread {

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
					// Some work goes here ...
					Log.d(TAG, "Updater ran");
					Thread.sleep(DELAY);
				} catch (InterruptedException e) {
					updaterService.runFlag = false;
				}
			}
		}

	}

}
