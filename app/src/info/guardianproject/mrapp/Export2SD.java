package info.guardianproject.mrapp;

import info.guardianproject.mrapp.model.Media;
import info.guardianproject.mrapp.model.Project;
import info.guardianproject.mrapp.model.Report;

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

import org.holoeverywhere.widget.Toast;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

public class Export2SD extends Activity{
	private ArrayList<Report> mListReports;
	private ArrayList<Project> mListProjects;
	String data = "";
	String ext;
	int BUFFER = 2048;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		ext = String.valueOf(Environment.getExternalStorageDirectory());
	 	ext += "/"+AppConstants.TAG;
		//Begin "XML" file
		 data += "<?xml version='1.0' encoding='UTF-8'?>\n";
		 data += "<reports>\n";
		 mListReports = Report.getAllAsList(this);
		 for (int i = 0; i < mListReports.size(); i++) {
			 	data += "<report>\n";
			 	Report report = mListReports.get(i);
			 	data += "<id>"+String.valueOf(report.getId())+"</id>\n";
			 	data += "<title>"+report.getTitle()+"</title>\n";
			 	data += "<issue>"+report.getIssue()+"</issue>\n";
			 	data += "<sector>"+report.getSector()+"</sector>\n";
			 	data += "<entity>"+report.getEntity()+"</entity>\n";
			 	data += "<description>"+report.getDescription()+"</description>\n";
			 	data += "<location>"+report.getLocation()+"</location>\n";
			 	
			 	mListProjects = Project.getAllAsList(this, report.getId());
			 	for (int j = 0; j < mListProjects.size(); j++) {
			 		Project project = mListProjects.get(j);
				 	data += "<story>\n";
				 	data += "<sid>"+project.getId()+"</sid>\n";
				 	data += "<stitle>"+project.getTitle()+"</stitle>\n";
				 	
				 	Media[] mediaList = project.getScenesAsArray()[0].getMediaAsArray();
				 	for (Media media: mediaList){
				 		String path = media.getPath();
				 		path = path.replace(ext, "");
				 		data += "<smedia>"+path+"</smedia>\n";
				 		data += "<stype>"+media.getMimeType()+"</stype>\n";
				 	}
					data += "</story>\n";
			 	}
			 	data += "</report>\n";
			}
		 data += "</reports>";
		writeToFile(data);
		//Now zip it!
		zipFileAtPath(ext, String.valueOf(Environment.getExternalStorageDirectory())+"/timby.zip");
		//Toast and end
		Toast.makeText(getBaseContext(), "Exported Successfully!", Toast.LENGTH_LONG).show();
		finish();
	}
	public static void writeToFile(final String fileContents) {
		try {
            FileWriter out = new FileWriter(new File(Environment.getExternalStorageDirectory(), AppConstants.TAG+"/db.xml"));
            out.write(fileContents);
            out.close();
        } catch (IOException e) {
            Log.d("Write Error!", e.getLocalizedMessage());
        }
         //Encrypt db.xml
		 
         Cipher cipher;
         String file = Environment.getExternalStorageDirectory()+"/"+AppConstants.TAG+"/db.xml";
 		
		 try {
			cipher = createCipher(Cipher.ENCRYPT_MODE);
			applyCipher(file, file+"_", cipher);
		} catch (Exception e) {
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
	static Cipher createCipher(int mode) throws Exception {
        PBEKeySpec keySpec = new PBEKeySpec("test".toCharArray());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(keySpec);
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update("input".getBytes());
        byte[] digest = md.digest();
        byte[] salt = new byte[8];
        for (int i = 0; i < 8; ++i)
          salt[i] = digest[i];
        PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 20);
        Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
        cipher.init(mode, key, paramSpec);
        return cipher;
      }

      static void applyCipher(String inFile, String outFile, Cipher cipher) throws Exception {
        CipherInputStream in = new CipherInputStream(new FileInputStream(inFile), cipher);
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outFile));
        int BUFFER_SIZE = 8;
        byte[] buffer = new byte[BUFFER_SIZE];
        int numRead = 0;
        do {
          numRead = in.read(buffer);
          if (numRead > 0)
            out.write(buffer, 0, numRead);
        } while (numRead == 8);
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
	        } else {
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
	    } catch (Exception e) {
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
