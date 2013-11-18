package info.guardianproject.mrapp;

import info.guardianproject.mrapp.model.Media;
import info.guardianproject.mrapp.model.Project;
import info.guardianproject.mrapp.model.Report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.logging.Logger;

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
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		ext = String.valueOf(Environment.getExternalStorageDirectory());
	 	ext += "/"+AppConstants.TAG+"/";
		//Begin "XML" file
		 data += "<?xml version='1.0' encoding='UTF-8'?>";
		 data += "<reports>";
		 mListReports = Report.getAllAsList(this);
		 for (int i = 0; i < mListReports.size(); i++) {
			 	data += "<report>";
			 	Report report = mListReports.get(i);
			 	data += "<id>"+String.valueOf(report.getId())+"</id>";
			 	data += "<title>"+report.getTitle()+"</title>";
			 	data += "<issue>"+report.getIssue()+"</issue>";
			 	data += "<sector>"+report.getSector()+"</sector>";
			 	data += "<entity>"+report.getEntity()+"</entity>";
			 	data += "<description>"+report.getDescription()+"</description>";
			 	data += "<location>"+report.getLocation()+"</location>";
			 	
			 	mListProjects = Project.getAllAsList(this, report.getId());
			 	for (int j = 0; j < mListProjects.size(); j++) {
			 		Project project = mListProjects.get(j);
				 	data += "<project>";
				 	data += "<pid>"+project.getId()+"</pid>";
				 	data += "<ptitle>"+project.getTitle()+"</ptitle>";
				 	
				 	Media[] mediaList = project.getScenesAsArray()[0].getMediaAsArray();
				 	for (Media media: mediaList){
				 		String path = media.getPath();
				 		path = path.replace(ext, "");
				 		data += "<pmedia>"+path+"</pmedia>";
				 		data += "<ptype>"+media.getMimeType()+"</ptype>";
				 	}
					data += "</project>";
			 	}
			 	data += "</report>";
			}
		 data += "</reports>";
		writeToFile(data);
		//Now zip it!
		
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
            //Log.d("Write Error!", e.getLocalizedMessage());
        }
    }


}
