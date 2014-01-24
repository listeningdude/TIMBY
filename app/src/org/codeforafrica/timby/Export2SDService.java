package org.codeforafrica.timby;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.crypto.Cipher;

import org.codeforafrica.timby.Export2SD.export2SD;
import org.codeforafrica.timby.media.Encryption;
import org.codeforafrica.timby.model.Media;
import org.codeforafrica.timby.model.Project;
import org.codeforafrica.timby.model.Report;
import org.holoeverywhere.widget.Toast;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class Export2SDService extends Service {
	private ArrayList<Report> mListReports;
	private ArrayList<Project> mListProjects;
	String data = "";
	String ext;
	int BUFFER = 2048;
	
	@Override
    public void onCreate() {
          super.onCreate();
          showNotification("Exporting to SD...");
          new export2SD().execute();
	}
	private void showNotification(String message) {
   	 CharSequence text = message;
   	 Notification notification = new Notification(R.drawable.timby_hold_icon, text, System.currentTimeMillis());
   	 PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
   	                new Intent(this, HomeActivity.class), 0);
   	notification.setLatestEventInfo(this, "Export to SD",
   	      text, contentIntent);
   	NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		nm.notify("service started", 1, notification);
		}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	class export2SD extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}
		protected String doInBackground(String... args) {
			
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	        String user_id = settings.getString("user_id",null); 
			
			ext = String.valueOf(Environment.getExternalStorageDirectory());
		 	ext += "/"+AppConstants.TAG;
			//Begin "XML" file
		 	data += "<?xml version='1.0' encoding='UTF-8'?>\n";
		 	data += "<reports>\n";
			 mListReports = Report.getAllAsList(getApplicationContext());
			 for (int i = 0; i < mListReports.size(); i++) {
				 	//check if report actually exists
				 	if(mListReports.get(i)!=null){
					 	
					 	data += "<report>\n";
					 	//data += "<user_id>"+user_id+"</user_id>";
					 	Report report = mListReports.get(i);
					 	data += "<id>"+String.valueOf(report.getId())+"</id>\n";
					 	data += "<report_title>"+report.getTitle()+"</report_title>\n";
					 	
					 	String issue = report.getIssue();
					 	if(issue.equals("0")){
					 		issue = "1";
					 	}
					 	data += "<category>"+issue+"</category>\n";
					 	
					 	String category = report.getSector();
					 	if(category.equals("0")){
					 		category = "1";
					 	}
					 	data += "<sector>"+category+"</sector>\n";
					 	
					 	data += "<entity>"+report.getEntity()+"</entity>\n";
					 	data += "<location>"+report.getLocation()+"</location>\n";
					 	data += "<report_date>"+report.getDate()+"</report_date>\n";
					 	data += "<description>"+report.getDescription()+"</description>\n";
					 	data += "<report_objects>\n";
					 	mListProjects = Project.getAllAsList(getApplicationContext(), report.getId());
					 	for (int j = 0; j < mListProjects.size(); j++) {
					 		Project project = mListProjects.get(j);
						 	data += "<object>\n";
						 	data += "<object_id>"+project.getId()+"</object_id>\n";
						 	data += "<object_title>"+project.getTitle()+"</object_title>\n";
						 	
						 	Media[] mediaList = project.getScenesAsArray()[0].getMediaAsArray();
						 	for (Media media: mediaList){
						 		String path = media.getPath();
						 		
						 		String file = path;
						 		
						 		//Decrypt file
						 		Cipher cipher;
								try {
									cipher = Encryption.createCipher(Cipher.DECRYPT_MODE);
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
								
						 		/*						 		
						 		Intent startMyService= new Intent(Export2SDService.this, EncryptionService.class);
						        startMyService.putExtra("filepath", file);
						        startMyService.putExtra("mode", Cipher.DECRYPT_MODE);
						        startService(startMyService);*/
						        
								//if(media.getMimeType().contains("video")){
								//	copyfile(file, ext+"/"+report.getId()+"/vid"+String.valueOf(j)+".mp4");
								//	data += "<object_media>/"+report.getId()+"/vid"+String.valueOf(j)+".mp4</object_media>\n";
								//}else{
								path = path.replace(ext, "");
							 	data += "<object_media>"+path+"</object_media>\n";
								//}
						 		data += "<object_type>"+media.getMimeType()+"</object_type>\n";
						 	}
							data += "</object>\n";
					 	}
					 	data += "</report_objects>\n";
					 	data += "</report>\n";
					 	
				 	}
				}
			 data += "</reports>";
			 
			 writeToFile(data);
			 
			//Delete old file
			 File zipFile = new File(String.valueOf(getSD())+"/timby.zip");
			 zipFile.delete();
			//Now create new zip
			zipFileAtPath(ext, String.valueOf(getSD())+"/timby.zip");
			
			//encrypt zip file
			String file = String.valueOf(getSD())+"/timby.zip";
			
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
			
			//Re-encrypt everything
			reEncrypt_everything();
			
			return null;
		}
		
	protected void onPostExecute(String file_url) {
			
			showNotification("Exported Successfully!");
			endExporting();
		}
	}
	public void reEncrypt_everything(){
		mListReports = Report.getAllAsList(getApplicationContext());
		 for (int i = 0; i < mListReports.size(); i++) {
			 	if(mListReports.get(i)!=null){
				 	Report report = mListReports.get(i);
				 	mListProjects = Project.getAllAsList(getApplicationContext(), report.getId());
				 	for (int j = 0; j < mListProjects.size(); j++) {
				 		Project project = mListProjects.get(j);
					 	Media[] mediaList = project.getScenesAsArray()[0].getMediaAsArray();
					 	for (Media media: mediaList){
					 		String path = media.getPath();
					 		/*
					 		Intent startMyService= new Intent(Export2SDService.this, EncryptionService.class);
					        startMyService.putExtra("filepath", path);
					        startMyService.putExtra("mode", Cipher.ENCRYPT_MODE);
					        startService(startMyService);
					        */
					 		String file = path;
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
					 	}
				 	}
			 	}
		 }
		 //encrypt xml
		 String file = Environment.getExternalStorageDirectory()+"/"+AppConstants.TAG+"/db.xml";
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
	}
      public void endExporting(){
    	  this.stopSelf();
      }
	public static void writeToFile(final String fileContents) {
		try {
	            FileWriter out = new FileWriter(new File(Environment.getExternalStorageDirectory(), AppConstants.TAG+"/db.xml"));
	            out.write(fileContents);
	            out.close();
        	}catch (IOException e){
        		Log.d("Write Error!", e.getLocalizedMessage());
        	}
			/*
			//Encrypt xml
			String file = Environment.getExternalStorageDirectory()+"/"+AppConstants.TAG+"/db.xml";
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
			*/
    }

	public boolean zipFileAtPath(String sourcePath, String toLocation) {
	    // ArrayList<String> contentList = new ArrayList<String>();
	    File sourceFile = new File(sourcePath);
	    try {
	        BufferedInputStream origin = null;
	        FileOutputStream dest = new FileOutputStream(toLocation);
	        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
	                dest));
	        if (sourceFile.isDirectory()) {
	            zipSubFolder(out, sourceFile, sourceFile.getParent().length());
	        }else {
	            byte data[] = new byte[BUFFER];
	            FileInputStream fi = new FileInputStream(sourcePath);
	            origin = new BufferedInputStream(fi, BUFFER);
	            ZipEntry entry = new ZipEntry(getLastPathComponent(sourcePath));
	            out.putNextEntry(entry);
	            int count;
	            while ((count = origin.read(data, 0, BUFFER)) != -1) {
	                out.write(data, 0, count);
	            }
	        }
	        out.close();
	    }catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	    return true;
	}

	private void zipSubFolder(ZipOutputStream out, File folder,
	        int basePathLength) throws IOException {
	    File[] fileList = folder.listFiles();
	    BufferedInputStream origin = null;
	    for (File file : fileList) {
	        if (file.isDirectory()) {
	            zipSubFolder(out, file, basePathLength);
	        } else {
	            byte data[] = new byte[BUFFER];
	            String unmodifiedFilePath = file.getPath();
	            String relativePath = unmodifiedFilePath
	                    .substring(basePathLength);
	            Log.i("ZIP SUBFOLDER", "Relative Path : " + relativePath);
	            FileInputStream fi = new FileInputStream(unmodifiedFilePath);
	            origin = new BufferedInputStream(fi, BUFFER);
	            ZipEntry entry = new ZipEntry(relativePath);
	            out.putNextEntry(entry);
	            int count;
	            while ((count = origin.read(data, 0, BUFFER)) != -1) {
	                out.write(data, 0, count);
	            }
	            origin.close();
	        }
	    }
	}

	public String getLastPathComponent(String filePath) {
	    String[] segments = filePath.split("/");
	    String lastPathComponent = segments[segments.length - 1];
	    return lastPathComponent;
	}
	
	public File getSD(){
		File extStorage = null;
		
		if((new File("/mnt/external_sd/")).exists()){
			extStorage = new File("/mnt/external_sd/");
		}else if((new File("/mnt/extSdCard/")).exists()){
			extStorage = new File("/mnt/extSdCard/");
		}else{
			extStorage = Environment.getExternalStorageDirectory();
		}
		
		return extStorage;
	}
}
