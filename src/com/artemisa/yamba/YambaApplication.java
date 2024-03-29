package com.artemisa.yamba;

import java.util.List;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.Twitter.Status;
import android.app.Application;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.Log;

public class YambaApplication extends Application implements
		OnSharedPreferenceChangeListener {
	private static final String TAG = YambaApplication.class.getSimpleName();
	private Twitter twitter;
	private SharedPreferences prefs;

	private StatusData statusData;

	public StatusData getStatusData() {
		return statusData;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);

		statusData = new StatusData(this);
		Log.i(TAG, "onCreated");
	}
	
	// Connects to the online service and puts the latest statuses into DB.
	// Returns the count of new statuses
	public synchronized int fetchStatusUpdates() { //
		Log.d(TAG, "Fetching status updates");
		if (getTwitter() == null) {
			Log.d(TAG, "Twitter connection info not initialized");
			return 0;
		}
		try {
			List<Status> statusUpdates = getTwitter().getFriendsTimeline();
			long latestStatusCreatedAtTime = this.getStatusData()
					.getLatestStatusCreatedAtTime();
			int count = 0;
			ContentValues values = new ContentValues();
			for (Status status : statusUpdates) {
				values.put(StatusData.C_ID, status.getId());
				long createdAt = status.getCreatedAt().getTime();
				values.put(StatusData.C_CREATED_AT, createdAt);
				values.put(StatusData.C_TEXT, status.getText());
				values.put(StatusData.C_USER, status.getUser().getName());
				Log.d(TAG, "Got update with id " + status.getId() + ". Saving");
				this.getStatusData().insertOrIgnore(values);
				if (latestStatusCreatedAtTime < createdAt) {
					count++;
				}
			}
			Log.d(TAG, count > 0 ? "Got " + count + " status updates"
					: "No new status updates");
			return count;
		} catch (RuntimeException e) {
			Log.e(TAG, "Failed to fetch status updates", e);
			return 0;
		}
	}

	

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
		Log.i(TAG, "onTerminated");
	}

	public synchronized Twitter getTwitter() {
		if (twitter == null) {
			String username, password, apiRoot;
			username = prefs.getString("username", "");
			password = prefs.getString("password", "");
			apiRoot = prefs.getString("apiRoot",
					"http://yamba.marakana.com/api");
			// Connect to twitter.com
			twitter = new Twitter(username, password);
			twitter.setAPIRootUrl(apiRoot);
		}
		return twitter;
	}

	@Override
	public synchronized void onSharedPreferenceChanged(
			SharedPreferences sharedPreferences, String key) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onSharedPreferenceChanged()");
		twitter = null;

	}

}
