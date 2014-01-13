package org.codeforafrica.timby;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.codeforafrica.timby.SyncActivity.checkToken;
import org.codeforafrica.timby.media.Encryption;
import org.codeforafrica.timby.model.Media;
import org.codeforafrica.timby.model.Project;
import org.codeforafrica.timby.model.Report;
import org.holoeverywhere.widget.Toast;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

public class Export2SD extends Activity{
	private ArrayList<Report> mListReports;
	private ArrayList<Project> mListProjects;
	String data = "";
	String ext;
	int BUFFER = 2048;
	ProgressDialog pDialog;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.export);   
		//pDialog = (ProgressDialog)findViewById(R.id.progressBar1);
		
		new export2SD().execute();
		
		//Toast.makeText(getBaseContext(), "Exported Successfully!", Toast.LENGTH_LONG).show();
		
	}
	
	class export2SD extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Export2SD.this);
			pDialog.setMessage("Exporting to SD Card");
			pDialog.show();
		}
		protected String doInBackground(String... args) {
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	        String user_id = settings.getString("user_id",null); 
			
			ext = String.valueOf(Environment.getExternalStorageDirectory());
		 	ext += "/"+AppConstants.TAG;
			//Begin "XML" file
			 mListReports = Report.getAllAsList(getApplicationContext());
			 for (int i = 0; i < mListReports.size(); i++) {
				 	data += "<?xml version='1.0' encoding='UTF-8'?>\n";
				 	data += "<user_id>"+user_id+"</user_id>";
				 	data += "<reports>\n";
				 	data += "<report>\n";
				 	Report report = mListReports.get(i);
				 	data += "<id>"+String.valueOf(report.getId())+"</id>\n";
				 	data += "<title>"+report.getTitle()+"</title>\n";
				 	data += "<category>"+report.getIssue()+"</category>\n";
				 	data += "<sector>"+report.getSector()+"</sector>\n";
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
					 		path = path.replace(ext, "");
					 		data += "<object_media>"+path+"</object_media>\n";
					 		data += "<object_type>"+media.getMimeType()+"</object_type>\n";
					 	}
						data += "</object>\n";
				 	}
				 	data += "</report_objects>\n";
				 	data += "</report>\n";
				 	data += "</reports>";
					writeToFile(data, i);
				}
			 
			//Now zip it!
			zipFileAtPath(ext, String.valueOf(Environment.getExternalStorageDirectory())+"/timby.zip");
			//Toast and end
		
			return null;
		}
	protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			Toast.makeText(getBaseContext(), "Exported Successfully!", Toast.LENGTH_LONG).show();
			finish();
			
		}
	
	}
      
	public static void writeToFile(final String fileContents, final int reportId) {
		try {
            FileWriter out = new FileWriter(new File(Environment.getExternalStorageDirectory(), AppConstants.TAG+"/"+String.valueOf(reportId)+"/db.xml"));
            out.write(fileContents);
            out.close();
        } catch (IOException e) {
            Log.d("Write Error!", e.getLocalizedMessage());
        }
         //Encrypt db.xml
		 
        Cipher cipher;
        String file = Environment.getExternalStorageDirectory()+"/"+AppConstants.TAG+"/"+String.valueOf(reportId)+"/db.xml";
 		
		try{
			cipher = Encryption.createCipher(Cipher.ENCRYPT_MODE);
			Encryption.applyCipher(file, file+"_", cipher);
		}catch(Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Then delete original file
		File oldfile = new File(file);
		oldfile.delete();
		
		//Then remove _ on encrypted file
		File newfile = new File(file+"_");
		newfile.renameTo(new File(file));
		
		//Done!
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

}
