package org.codeforafrica.timby;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class EncryptAllMediaService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	
		Timer timer = new Timer();
        TimerTask updateProfile = new CustomTimerTask(EncryptAllMediaService.this);
        timer.scheduleAtFixedRate(updateProfile, 0, 5000);
        
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//Toast.makeText(this, "Service destroyed ...", Toast.LENGTH_LONG).show();
	}
}
