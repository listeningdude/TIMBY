package org.codeforafrica.timby;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.Cipher;

import org.codeforafrica.timby.media.Encryption;
import org.codeforafrica.timby.model.Media;
import org.codeforafrica.timby.model.Report;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class EncryptAllMediaService extends Service {
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
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Editor edit = settings.edit(); 
        
        edit.putString("encryption_running", "end");
        edit.commit();
        
		//Toast.makeText(this, "Encrypt All Media ...", Toast.LENGTH_LONG).show();
		Timer timer = new Timer();
        TimerTask updateProfile = new mainTask();
        timer.scheduleAtFixedRate(updateProfile, 0, 5000);
        
	}
	private class mainTask extends TimerTask
    { 
        public void run() 
        {
        	if(isServiceRunning()){
        		Log.d("running", "running");
        	}else{
        		Log.d("running", "not running");
        		encrypt_one_media();
        	}
        }
    }   
	public void encrypt_one_media(){
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Editor edit = settings.edit(); 
        
        edit.putString("encryption_running", "begin");
        edit.commit();
        
        //ArrayList<Media> mediaList = Media.getUnEncrypted(getApplicationContext());
        ArrayList<Media> mediaList = Media.getAllAsList(getApplicationContext());
        Log.d("medialist", "medialist"+String.valueOf(mediaList.size()));
        	int j =0;
			if(mediaList.size()>0){
					for(int i=0; i<mediaList.size(); i++){
						media = mediaList.get(i);
						Log.d("encrypted", "value"+String.valueOf(media.getEncrypted()));
						if(media.getEncrypted().equals("0")){
							j++;
							Log.d("updating", "updating"+String.valueOf(media.getId()));
							file = media.getPath();
							message = "Encrypting file...";
							
							//mark file as encrypted
							Media media2 = Media.get(getApplicationContext(), media.getId());
							media2.setEncrypted("1");
							media2.save();
							
							Log.d("updated", "updated"+String.valueOf(media2.getEncrypted()));	
							encryptFile();
						}
					}
					if(j==0){
						message = "All files encrypted!";
						endEncryption();
					}
										
			}else{
				message = "No files to encrypt!";
				endEncryption();
			}
			
			showNotification(message);
	}
	public void encryptFile() {
		Cipher cipher;
		
		try {
				cipher = Encryption.createCipher(Cipher.ENCRYPT_MODE);
				Encryption.applyCipher(file, file+"_", cipher);
			}catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e("Encryption error", e.getLocalizedMessage());
				e.printStackTrace();
			}
		//Then delete original file
		File oldfile = new File(file);
		oldfile.delete();
		//Then remove _ on encrypted file
		File newfile = new File(file+"_");
		newfile.renameTo(new File(file));
		
		
		message = "Encrypted successfully!";
		showNotification(message);
		endEncryption();
	
}
	public void endEncryption(){
	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	Editor edit = settings.edit(); 
	edit.putString("encryption_running", "end");
	edit.commit();
	}
	
	private void showNotification(String message) {
		 CharSequence text = message;
		 Notification notification = new Notification(R.drawable.timby_hold_icon, text, System.currentTimeMillis());
		 PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
		                new Intent(this, HomeActivity.class), 0);
		notification.setLatestEventInfo(this, "Encryption",
		      text, contentIntent);
		NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		nm.notify("service started", 2, notification);
		}

	public boolean isServiceRunning() {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			       
        String encryption_running = settings.getString("encryption_running",null);
        
       if (encryption_running == null){
        	return false;
        }else if(encryption_running.equals("end")){
        	return false;
        }else if(encryption_running.equals("begin")){
        	return true;
        }else{
        	return false;
        }
	      
	    }
	@Override
	public void onDestroy() {
		super.onDestroy();
		//Toast.makeText(this, "Service destroyed ...", Toast.LENGTH_LONG).show();
	}
}
