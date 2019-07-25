package nexaplayer.mkv.mpg.flv.wmv.service;

import android.app.Service;
import android.content.Intent;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

import nexaplayer.mkv.mpg.flv.wmv.listeners.callbacks.SuccessCallback;
import nexaplayer.mkv.mpg.flv.wmv.task.FindAllSupoortedFilesAsyncTask;

/**
 * Created by pbuhsoft on 07/02/2018.
 */

public class InitService extends Service {
	public static final String ACTION_OPERATION_START = "1";
	public static final String ACTION_UPDATE_PROGRESS = "2";
	public static final String ACTION_OPERATION_DONE = "4";
	private FileObserver observer;
	private Handler handler;
	private Runnable updateProgress = new Runnable() {
		@Override
		public void run() {
			try {
				Intent broadcast = new Intent();
				broadcast.setAction(InitService.ACTION_UPDATE_PROGRESS);
				sendBroadcast(broadcast);
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				handler.postDelayed(this, 100);
			}
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		startTask();
		return START_STICKY;
	}

	public void startTask() {
		handler = new Handler();
		Intent broadcast = new Intent();
		broadcast.setAction(InitService.ACTION_OPERATION_START);
		sendBroadcast(broadcast);
		new FindAllSupoortedFilesAsyncTask(this, new SuccessCallback() {
			@Override
			public void onSuccess() {
				handler.removeCallbacks(updateProgress);
				Intent broadcast = new Intent();
				broadcast.setAction(InitService.ACTION_OPERATION_DONE);
				sendBroadcast(broadcast);
				stopSelf();
			}
		}).execute();


	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
