package org.codeforafrica.timby;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.codeforafrica.timby.VideoTutorials.VideosArrayAdapter;
import org.ffmpeg.android.MediaUtils;
import org.holoeverywhere.widget.Toast;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class VideoTutorialsService extends Service 
{
	File mFileExternDir;

	private static String file_url = "https://www.dropbox.com/s/fwctfwwdlth9ln1/tutorials.zip?dl=1";

	 public void onCreate() {
         super.onCreate();
         showNotification("Downloading tutorials...");
    //check if folder is created
    //if not create
      //Download tutorials
    //list files
    	//on click open
    boolean access_denied = false;
    
    mFileExternDir = new File(Environment.getExternalStorageDirectory(), "TIMBY_Tutorials");
    if (!mFileExternDir.exists()) {
        if (!mFileExternDir.mkdirs()) {
            Log.e("TIMBY_Tutorials: ", "Problem creating file");
            Toast.makeText(getApplicationContext(), "Problem creating folder", Toast.LENGTH_LONG).show();
            access_denied = true;
        }
    }
    
    File[] contents = mFileExternDir.listFiles();
    if((contents.length==0)&&(access_denied==false)){
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
    protected void onPostExecute(String file_url) {      
       new unzip().execute();
    }
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
			String filenum = file[i].toString();
			try {
				Bitmap videoThumb = MediaUtils.getVideoFrame(new File(filepath).getCanonicalPath(), -1);
				String filename = Environment.getExternalStorageDirectory()+"/TIMBY_Tutorials/thumbs/"+filenum+".jpg";
	            FileOutputStream out = new FileOutputStream(filename);
	            videoThumb.compress(Bitmap.CompressFormat.JPEG, 50, out);
	            out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		return null;
	}
	@Override
    protected void onPostExecute(String file_url) {		
		showNotification("Complete!");
	}
}
}
