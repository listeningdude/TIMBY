package org.codeforafrica.timby;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;

import org.codeforafrica.timby.VideoTutorials.VideosArrayAdapter;
import org.ffmpeg.android.MediaUtils;
import org.holoeverywhere.widget.Toast;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

public class VideoTutorialsService extends Service 
{
	File mFileExternDir;

	private static String file_url = "https://www.dropbox.com/s/2hxu8svr7fdiega/compressed.zip?dl=1";

	 public void onCreate() {
     super.onCreate();
     //showNotification("Downloading tutorials...");
	    //check if folder is created
	    //if not create
	      //Download tutorials
	    //list files
	    	//on click open
         
     
	    boolean access_denied = false;
	    
	    mFileExternDir = new File(Environment.getExternalStorageDirectory(), "TIMBY_Tutorials");
	    if (!mFileExternDir.exists()) {
	        if (!mFileExternDir.mkdirs()) {
	            Log.e("TIMBY_Tutorials: ", "Problem creating file!");
	            Toast.makeText(getApplicationContext(), "Problem creating folder", Toast.LENGTH_LONG).show();
	            access_denied = true;
	        }
	        File thumbsDir = new File(Environment.getExternalStorageDirectory(), "TIMBY_Tutorials/thumbs");
	        if(!thumbsDir.mkdirs()){
	        	Log.e("TIMBY_Tutorials: ", "Problem thumbnails folder!");
	        }
	        
	    }
	    
	    File[] contents = mFileExternDir.listFiles();
	    if((contents.length<2)&&(access_denied==false)){
	    	new DownloadFileFromURL().execute(file_url); 	
	    }
    
}
private void showNotification(String message) {
   	 CharSequence text = message;
   	 Notification notification = new Notification(R.drawable.timby_hold_icon, text, System.currentTimeMillis());
   	 PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
   	                new Intent(this, HomeActivity.class), 0);
   	notification.setLatestEventInfo(this, "Lessons",
      text, contentIntent);
   	NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
	nm.notify("service started", 1, notification);
}
@Override
public IBinder onBind(Intent intent) {
	// TODO Auto-generated method stub
	return null;
}

/**
 * Background Async Task to download file
 * */
class DownloadFileFromURL extends AsyncTask<String, String, String> {

    /**
     * Before starting background thread Show Progress Bar Dialog
     * */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
       showNotification("Downloading...");
    }

    /**
     * Downloading file in background thread
     * */
    @Override
    protected String doInBackground(String... f_url) {
        int count;
        try {
            URL url = new URL(f_url[0]);
            URLConnection conection = url.openConnection();
            conection.connect();

            // this will be useful so that you can show a tipical 0-100%
            // progress bar
            int lenghtOfFile = conection.getContentLength();

            // download the file
            InputStream input = new BufferedInputStream(url.openStream(),
                    8192);

            // Output stream
            OutputStream output = new FileOutputStream(Environment
                    .getExternalStorageDirectory().toString()
                    + "/TIMBY_Tutorials/download.zip");

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                
                //FIXME: download progress bar causes error
                
                //int progress = (int) ((total * 100) / lenghtOfFile);
                //showProgressNotification(progress);
                
                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }

        return null;
    }
    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        //showProgressNotification(Integer.parseInt(values[0]));
        int update = Integer.parseInt(values[0]);
        //if(update%5 == 0){
        showProgressNotification(update);
        //}
        
    }
    @Override
    protected void onPostExecute(String file_url) {      
       new unzip().execute();
    }
}

private void showProgressNotification(final int incr){
	final NotificationManager mNotifyManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
	final Builder mBuilder = new NotificationCompat.Builder(this);
	mBuilder.setContentTitle("Tutorials download")
	    .setContentText("Download in progress")
	    .setSmallIcon(R.drawable.timby_hold_icon);
	// Start a lengthy operation in a background thread
	new Thread(
	    new Runnable() {
	        @Override
	        public void run() {
	         mBuilder.setProgress(100, (int) incr, false);
	         // Displays the progress bar for the first time.
	         mNotifyManager.notify(0, mBuilder.build());
	 	     mBuilder.setContentText("Download in progress " + String.valueOf(incr) + "%");

	         
	         // When the loop is finished, updates the notification
	         if(incr==100){
		         mBuilder.setContentText("Download complete")
		         // Removes the progress bar
		                  .setProgress(0,0,false);
		         mNotifyManager.notify(1, mBuilder.build());
	         }
	         
	        }
	    }
	// Starts the thread by calling the run() method in its Runnable
	).start();
}

class unzip extends AsyncTask<String, String, String> {
	@Override
	protected void onPreExecute() {
	        super.onPreExecute();
	   showNotification("Unzipping...");
	}
	@Override
	protected String doInBackground(String... arg0) {
		
		final String zipFile = Environment.getExternalStorageDirectory() + "/TIMBY_Tutorials/download.zip"; 
    	String unzipLocation = Environment.getExternalStorageDirectory() + "/TIMBY_Tutorials/"; 
    	 
    	Decompress d = new Decompress(zipFile, unzipLocation); 
    	d.unzip(); 
    	        
    	//Delete zip file to save memory
    	(new File(zipFile)).delete();
    	
		return null;
	}
	@Override
    protected void onPostExecute(String file_url) {		
		new generate_thumbs().execute();
	}
}

class generate_thumbs extends AsyncTask<String, String, String> {
	@Override
	protected void onPreExecute() {
	   super.onPreExecute();
	   showNotification("Generating thumbnails...");
	}
	@Override
	protected String doInBackground(String... arg0) {
		File file[] = mFileExternDir.listFiles();

		for (int i=0; i < file.length; i++)
		{
			String filepath = file[i].getPath();
			if (filepath.endsWith(".mp4")){
				String[] thumbParts = file[i].getName().split("mp4");
				String thumbName = thumbParts[0]+"jpg";
				try {
						Bitmap videoThumb = ThumbnailUtils.createVideoThumbnail(filepath,  MediaStore.Images.Thumbnails.MINI_KIND);
					    //Bitmap videoThumb = MediaUtils.getVideoFrame(new File(filepath).getCanonicalPath(), -1);
						String filename = Environment.getExternalStorageDirectory()+"/TIMBY_Tutorials/thumbs/"+thumbName;
			            FileOutputStream out = new FileOutputStream(filename);
			            videoThumb.compress(Bitmap.CompressFormat.JPEG, 50, out);
			            out.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		}		
		return null;
	}
	@Override
    protected void onPostExecute(String file_url) {		
		showNotification("Complete!");
		endService();
	}
}
public static String fileToMD5(String filePath) {
    InputStream inputStream = null;
    try {
        inputStream = new FileInputStream(filePath);
        byte[] buffer = new byte[1024];
        MessageDigest digest = MessageDigest.getInstance("MD5");
        int numRead = 0;
        while (numRead != -1) {
            numRead = inputStream.read(buffer);
            if (numRead > 0)
                digest.update(buffer, 0, numRead);
        }
        byte [] md5Bytes = digest.digest();
        return convertHashToString(md5Bytes);
    } catch (Exception e) {
        return null;
    } finally {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (Exception e) { }
        }
    }
}

private static String convertHashToString(byte[] md5Bytes) {
    String returnVal = "";
    for (int i = 0; i < md5Bytes.length; i++) {
        returnVal += Integer.toString(( md5Bytes[i] & 0xff ) + 0x100, 16).substring(1);
    }
    return returnVal.toUpperCase();
}
	public void endService(){
		this.stopSelf();
	}
}
