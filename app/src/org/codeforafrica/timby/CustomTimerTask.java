package org.codeforafrica.timby;

import java.util.TimerTask;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;

public class CustomTimerTask extends TimerTask {


   
    private Context context;
	// Write Custom Constructor to pass Context
    public CustomTimerTask(Context con) {
        this.context = con;
    }
    @Override
    public void run() {
        // TODO Auto-generated method stub
    	 new Thread(new Runnable() {
             public void run() {
            	 	//check if encryptservice is running
            	 	//if running do nothing
            	 		//else get one unecrypted media and encrypt
	            	 if(!isServiceRunning(EncryptAllMediaService.class)){
	            		 context.getApplicationContext().startService(new Intent(context.getApplicationContext(),EncryptionService.class));
		             }	
	             }
	         }).start();
    }
    private boolean isServiceRunning(Class<?> cls) {
        ActivityManager manager = (ActivityManager)context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (cls.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
