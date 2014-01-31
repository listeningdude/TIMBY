package org.codeforafrica.timby;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.Cipher;

import org.codeforafrica.timby.media.Encryption;
import org.codeforafrica.timby.model.Media;
import org.codeforafrica.timby.model.Report;
import org.json.JSONArray;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class EncryptionBackground extends Service {
	String message;
	String file;
	Media media;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		Timer timer = new Timer();
        TimerTask updateProfile = new mainTask();
        timer.scheduleAtFixedRate(updateProfile, 0, 1000);
        
	}
	private class mainTask extends TimerTask
    { 
        public void run() 
        {
        	 new Thread(new Runnable() {
				public void run() {
		        	if(!isServiceRunning()){
		        		String filepath = null;
		        		//Find first file
		                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		                JSONArray jsonArray2 = null;
		                JSONArray jsonArray3 = new JSONArray();
		                try {
		                    jsonArray2 = new JSONArray(prefs.getString("eQ", "[]"));
			        		//Log.d("running", "not running"+jsonArray2.length());

		                    if(jsonArray2.length()>0){
			                    filepath = jsonArray2.getString(0);
			                  //Remove from list
			                    
			                    for (int i = 0; i < jsonArray2.length(); i++) {
			                        if(i!=0){
			                        	jsonArray3.put(jsonArray2.getString(i));
			                        }
			                   }
		                    }
		                } catch (Exception e) {
		                    e.printStackTrace();
		                }
		                
		                /*
		                
		                */
		                if(jsonArray2.length()>0){
		                	
		                	
		                		Editor editor = prefs.edit();
		                		editor.putString("eQ", jsonArray3.toString());
				                editor.commit();
		                	
			                
			                
		                	
		                	Intent startMyService= new Intent(getApplicationContext(), EncryptionService.class);
			                startMyService.putExtra("filepath", filepath);
			                startMyService.putExtra("mode", Cipher.ENCRYPT_MODE);
			                startService(startMyService);
		                }
		        	}
		        	
		             
                 }}).start();
        	
        }
    }   
	
	private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (EncryptionService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
	@Override
	public void onDestroy() {
		super.onDestroy();
		//Toast.makeText(this, "Service destroyed ...", Toast.LENGTH_LONG).show();
	}
}
