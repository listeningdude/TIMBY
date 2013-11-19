package info.guardianproject.mrapp;

import java.io.IOException;
import java.util.ArrayList;

import org.holoeverywhere.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import info.guardianproject.mrapp.login.UserFunctions;
import info.guardianproject.mrapp.model.Report;
import info.guardianproject.mrapp.server.LoginPreferencesActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ProgressBar;

public class SyncActivity extends BaseActivity{
	String token;
	String user_id;
	private static String KEY_SUCCESS = "status";
	private static String KEY_ID = "id";
	ProgressDialog pDialog;
	String lat;
	String lon;
	String title;
	String issue;
	String sector;
	String description;
	String date;
	String entity;
	int rid;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sync);
		//if token not valid, redirect to login
			new checkToken().execute();
		//loop through reports
			
		//if report is not uploaded yet
			//create reports
			//upload files
		//else
			//if changes made
				//update project
	}
	public void loopReports(){
		ArrayList<Report> mListReports;
		mListReports = Report.getAllAsList(this);
		for (int i = 0; i < mListReports.size(); i++) {
			Report report = mListReports.get(i);
			if(report.getServerId().equals("0")){
				//report is not created yet, create and grab server id
				String location = report.getLocation();
				
				if(location.toLowerCase().contains(", ")){
					String[] locations = location.split(", ");
					lat = locations[0];
					lon = locations[1];
				}else{
					lat = "";
					lon = "";
				}
				title = report.getTitle();
				issue = report.getIssue();
				sector = report.getSector();
				date = report.getDate();
				rid = report.getId();
				new createReport().execute();
			}else{
				//update report
				
			}
		}
		
	}
	class createReport extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SyncActivity.this); 
            pDialog.setMessage("Creating report..."); 
            pDialog.setIndeterminate(false); 
            pDialog.setCancelable(false); 
            pDialog.show(); 
        }
        
        protected String doInBackground(String... args) {
        	UserFunctions userFunction = new UserFunctions();
			JSONObject json = userFunction.newReport(token, user_id, title, issue, sector, entity, lat, lon, date);
			try {
				String res = json.getString(KEY_SUCCESS); 
					if(res.equals("OK")){
						JSONObject json_report = json.getJSONObject("message");
						String serverid = json_report.getString(KEY_ID);
						//Update report with server id 
						Report report = Report.get(getApplicationContext(), rid);
						report.setServerId(serverid);
						//Upload corresponding media files :O
						
					}else{
						//Some error message. Not sure what yet.
						
					}
				}catch(JSONException e){
					e.printStackTrace();
				}
        	return null;
        }
        

        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            
        }
	}
	class checkToken extends AsyncTask<String, String, String> {
		 
  
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SyncActivity.this); 
            pDialog.setMessage("Checking token..."); 
            pDialog.setIndeterminate(false); 
            pDialog.setCancelable(false); 
            pDialog.show(); 
        }
 

        protected String doInBackground(String... args) {
	
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	        token = settings.getString("token",null);
	        user_id = settings.getString("user_id",null);
	        
	        if(token==null){
	        	pDialog.dismiss();
	        	Intent login = new Intent(getApplicationContext(), LoginPreferencesActivity.class);
				login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(login);
	        }else{
		        UserFunctions userFunction = new UserFunctions();
		        JSONObject json = userFunction.checkTokenValidity(user_id, token);
		        
		        try{
		        	String res = json.getString(KEY_SUCCESS); 
					if(res.equals("OK")){
						
					}else{
						pDialog.dismiss();
						Toast.makeText(getApplicationContext(), "Token expired! Login and try again.", Toast.LENGTH_LONG).show();
						Intent login = new Intent(getApplicationContext(), LoginPreferencesActivity.class);
						login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(login);
					}
		        }catch (JSONException e) {
					e.printStackTrace();
				}
	        }
	        return null;
        }
        

        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            
            loopReports();
        }
	}
}
