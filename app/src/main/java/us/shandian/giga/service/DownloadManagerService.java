package us.shandian.giga.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import us.shandian.giga.R;
import us.shandian.giga.get.DownloadManager;
import us.shandian.giga.ui.main.MainActivity;
import static us.shandian.giga.BuildConfig.DEBUG;

public class DownloadManagerService extends Service
{
	private static final String TAG = DownloadManagerService.class.getSimpleName();
	
	private DMBinder mBinder;
	private DownloadManager mManager;

	@Override
	public void onCreate() {
		super.onCreate();
		
		if (DEBUG) {
			Log.d(TAG, "onCreate");
		}
		
		mBinder = new DMBinder();
		if (mManager == null) {

			if (DEBUG) {
				Log.d(TAG, "mManager == null");
			}

			mManager = new DownloadManager(this, "/storage/sdcard0/GigaGet");
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (DEBUG) {
			Log.d(TAG, "Starting");
		}
		Intent i = new Intent();
		i.setAction(Intent.ACTION_MAIN);
		i.setClass(this, MainActivity.class);
		Notification n = new Notification();
		n.setLatestEventInfo(this, getString(R.string.msg_running),
			getString(R.string.msg_running_detail), PendingIntent.getActivity(this, 0, i, 0));
		startForeground(10011, n);
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if (DEBUG) {
			Log.d(TAG, "Destroying");
		}
		
		for (int i = 0; i < mManager.getCount(); i++) {
			mManager.pauseMission(i);
		}
		
		stopForeground(true);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	// Wrapper of DownloadManager
	public class DMBinder extends Binder {
		// Do not start missions from outside
		public DownloadManager getDownloadManager() {
			return mManager;
		}
		
		public int startMission(final String url, final String name, final int threads) {
			return mManager.startMission(url, name, threads);
		}
		
		public void resumeMission(final int id) {
			mManager.resumeMission(id);
		}
		
	}

}
