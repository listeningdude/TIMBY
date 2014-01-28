package org.codeforafrica.timby;

import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

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
         			//Toast.makeText(context.getApplicationContext(), "Running timer ...", Toast.LENGTH_LONG).show();
            	 	//check if encryptservice is running
            	 	//if running do nothing
            	 		//else get one unecrypted media and encrypt
	            	 if(!isServiceRunning()){
	            		 context.getApplicationContext().startService(new Intent(context.getApplicationContext(),EncryptionService.class));
		             }	
	             }
	         }).start();
    }
    private boolean isServiceRunning() {
	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		       
	        String encryption_running = settings.getString("encryption_running",null);
	        Log.d("running", "running"+encryption_running);
	        if (encryption_running == null){
	        	return false;
	        }else if(encryption_running.equals("end")){
	        	return false;
	        }else{
	        	return true;
	        }
      
    }
}
