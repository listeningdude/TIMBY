package org.codeforafrica.timby;

import java.io.File;
import java.util.ArrayList;

import javax.crypto.Cipher;

import org.codeforafrica.timby.Export2SDService.export2SD;
import org.codeforafrica.timby.media.Encryption;
import org.codeforafrica.timby.model.Media;
import org.codeforafrica.timby.model.Project;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class EncryptionService extends Service{
	String message;
	private Context context;
	
	// Write Custom Constructor to pass Context
    public EncryptionService(Context con) {
        this.context = con;
    }
    
	@Override
    public void onCreate() {
          super.onCreate();
          message = "Encrypting file...";
          showNotification(message);
          new encryptFile().execute();
    }

	class encryptFile extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}
		protected  String doInBackground(String... args) {
			//Get one media file to encrypt
			ArrayList<Media> mediaList = Media.getUnEncrypted(context.getApplicationContext());
			if(mediaList.size()>0){
				Media media = mediaList.get(0);
				
				String file = media.getPath();			
				
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
				
				//mark file as encrypted
				media.setEncrypted("1");
			}
			return null;
		}
		protected void onPostExecute(String file_url) {

		      	message = "Encrypted successfully!";
		        
				showNotification(message);
				endEncryption();
			}
		}
	public void endEncryption(){
		this.stopSelf();
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
			
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
