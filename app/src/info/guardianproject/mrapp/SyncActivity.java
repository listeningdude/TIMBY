package info.guardianproject.mrapp;

import java.util.ArrayList;
import java.util.concurrent.Executor;

import org.holoeverywhere.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import info.guardianproject.mrapp.login.UserFunctions;
import info.guardianproject.mrapp.model.Entity;
import info.guardianproject.mrapp.model.Media;
import info.guardianproject.mrapp.model.Project;
import info.guardianproject.mrapp.model.Report;
import info.guardianproject.mrapp.server.LoginPreferencesActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SyncActivity extends BaseActivity{
	String token;
	String user_id;
	private static String KEY_SUCCESS = "status";
	private static String KEY_ID = "id";
	ProgressBar pDialog;
	String lat;
	String lon;
	String title;
	String issue;
	String sector;
	String description;
	String date;
	String pdate;
	String entity;
	String serverID;
	String entityName;
	int esequence;
	int rid;
	int entityid;
	/*String ppath;
 	String ptype;
 	String ptitle;
 	String pid;
 	String psequence;
 	String preportid;*/
 	Button done;
 	TextView log;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sync);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#9E3B33")));
	      
		done = (Button)findViewById(R.id.close);
		
		log = (TextView)findViewById(R.id.logTextView);
		
		log.append("Initializing log...\n");
		
		pDialog = (ProgressBar)findViewById(R.id.progressBar1);
		
		new checkToken().execute();
		//if token not valid, redirect to login
		
		//loop through reports
			
		//if report is not uploaded yet
			//create reports
			//upload files
		//else
			//if changes made
				//update project
		//pDialog.dismiss();
			
			done.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();
				}
			});
	}
	
	public void loopReports(){
		ArrayList<Report> mListReports;
		mListReports = Report.getAllAsList(this);
		for (int i = 0; i < mListReports.size(); i++) {
			Report report = mListReports.get(i);
			String location = report.getLocation();
			if(location.contains(", ")){
				String[] locations = location.split(", ");
				lat = locations[0];
				lon = locations[1];
			}else{
				lat = "0";
				lon = "0";
			}
			title = report.getTitle();
			issue = report.getIssue();
			entity = report.getEntity();
			sector = report.getSector();
			date = report.getDate();
			rid = report.getId();
			description = report.getDescription();
			serverID = report.getServerId();
			if(serverID.equals("0")){
				//report is not created yet, create and grab server id
				new createReport().execute();
			}else{
				//update report
				//get report servid
				new updateReport().execute();
			}
		}
	}
	public void uploadEntities(int rid, int serverid){
		serverID = String.valueOf(serverid);
		/*
		Report report = Report.get(this, rid);
		String entity = report.getEntity();
		String[] list1 = entity.split("\\s*,\\s*");
        for(String file: list1) {
        	entityName = file;
        	esequence = 1;
        	new createEntity().execute();
        }
        */
		ArrayList<Entity> mListEntities;
		mListEntities = Entity.getAllAsList(this, rid);
	 	for (int j = 0; j < mListEntities.size(); j++) {
	 		Entity entity = mListEntities.get(j);
	 		entityName = entity.getEntity();
	 		entityid = entity.getId();
        	esequence = j;
        	new createEntity().execute(); 		
	 	}
	}
	public void uploadMedia(int rid, int serverid){
		ArrayList<Project> mListProjects;
		mListProjects = Project.getAllAsList(this, rid);
	 	for (int j = 0; j < mListProjects.size(); j++) {
	 		Project project = mListProjects.get(j);
	 		
	 		Media[] mediaList = project.getScenesAsArray()[0].getMediaAsArray();
	 		Media media = mediaList[0];
		 	
	 		String ppath = media.getPath();
	 		
		 	String ptype = media.getMimeType();
		 	
		 	String optype = "video";
		 	if(ptype.contains("image")){
		 		optype = "image";
		 	}else if(ptype.contains("video")){
		 		optype = "video";
		 	}else if(ptype.contains("audio")){
		 		optype = "video";
		 	}
		 	String ptitle = project.getTitle();
		 	String pdate = project.getDate();
		 	String pid = String.valueOf(project.getId());
		 	String psequence = String.valueOf(j+1);
		 	String preportid = String.valueOf(serverid);
		 	//new createObject().execute();		
		 	MyTaskParams params = new MyTaskParams(ppath, ptype, optype, ptitle, pdate, pid, psequence, preportid);
		 	createObject myTask = new createObject();
		 	myTask.execute(params);	
	 	}
	}
	public void updateMedia(int rid, int serverid){
		ArrayList<Project> mListProjects;
		mListProjects = Project.getAllAsList(this, rid);
	 	for (int j = 0; j < mListProjects.size(); j++) {
	 		Project project = mListProjects.get(j);
	 		
	 		Media[] mediaList = project.getScenesAsArray()[0].getMediaAsArray();
	 		
	 		Media media = mediaList[0];
	 		
	 		String ppath = media.getPath();
	 		
	 		String ptype = media.getMimeType();
		 	
		 	String optype = "video";
		 	if(ptype.contains("image")){
		 		optype = "image";
		 	}else if(ptype.contains("video")){
		 		optype = "video";
		 	}else if(ptype.contains("audio")){
		 		optype = "video";
		 	}
		 	
		 	String ptitle = project.getTitle();
		 	String pdate = project.getDate();
		 	String pid = String.valueOf(project.getId());
		 	String psequence = String.valueOf(j+1);
		 	String preportid = String.valueOf(serverid);
		 	
		 	//new updateObject().execute();	
		 	
		 	MyTaskParams params = new MyTaskParams(ppath, ptype, optype, ptitle, pdate, pid, psequence, preportid);
		 	updateObject myTask = new updateObject();
		 	myTask.execute(params);		 	
		 }
	}
	private static class MyTaskParams {
	    String ppath;
	    String ptype;
	    String optype;
	    String ptitle;
	    String pdate;
	    String pid;
	    String psequence;
	    String preportid;
	    MyTaskParams(String ppath, String ptype, String optype, String ptitle, String pdate, String pid, String psequence,String preportid) {
	        this.ppath = ppath;
	        this.pdate = pdate;
	        this.ptype = ptype;
	        this.optype = optype;
	        this.pid = pid;
	        this.pdate=pdate;
	        this.psequence=psequence;
	        this.preportid=preportid;
	        this.ptitle=ptitle;
	    }
	}
	class updateObject extends AsyncTask<MyTaskParams, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //log.append("---Updating media: \n");
            //pDialog.setVisibility(View.VISIBLE); 
        }
        protected String doInBackground(MyTaskParams... params) {
        	String ppath = params[0].ppath;
        	String optype = params[0].optype;
        	String ptype = params[0].ptype;
    	    String ptitle = params[0].ptitle;;
    	    String pdate = params[0].pdate;;
    	    String pid = params[0].pid;;
    	    String psequence = params[0].psequence;;
    	    String preportid = params[0].preportid;;
    	    
        	UserFunctions userFunction = new UserFunctions();
        	Log.d("j", String.valueOf(ppath));
			JSONObject json = userFunction.updateObject(token, user_id, ptitle, psequence, preportid, ptype, optype, pid, pdate, ppath);
		
			try {
				String res = json.getString(KEY_SUCCESS); 
					if(res.equals("OK")){
						//JSONObject json_report = json.getJSONObject("message");
						//String objectid = json_report.getString(KEY_ID);
						
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
            // pDialog.dismiss();
        	log.append("---Updated media object \n");
        	// pDialog.setVisibility(View.GONE);
        }
	}
	class createObject extends AsyncTask<MyTaskParams, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //log.append("---Updating media: \n");
            //pDialog.setVisibility(View.VISIBLE); 
        }
        protected String doInBackground(MyTaskParams... params) {
        	String ppath = params[0].ppath;
        	String optype = params[0].optype;
        	String ptype = params[0].ptype;
    	    String ptitle = params[0].ptitle;;
    	    String pdate = params[0].pdate;;
    	    String pid = params[0].pid;;
    	    String psequence = params[0].psequence;;
    	    String preportid = params[0].preportid;;
    	    
    	    Log.d("optype", optype);
    	    Log.d("ptype", ptype);
        	
    	    UserFunctions userFunction = new UserFunctions();
			JSONObject json = userFunction.newObject(token, user_id, ptitle, psequence, preportid, ptype, optype, pid, pdate, ppath);
			
			try {
				String res = json.getString(KEY_SUCCESS); 
					if(res.equals("OK")){
						//JSONObject json_report = json.getJSONObject("message");
						//String objectid = json_report.getString(KEY_ID);
						
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
           // pDialog.dismiss();
        	log.append("---Uploaded media object\n");
        	//pDialog.setVisibility(View.GONE);
        }
	}
	class createReport extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setVisibility(View.VISIBLE); 
        }
        
        protected String doInBackground(String... args) {
        	UserFunctions userFunction = new UserFunctions();
			JSONObject json = userFunction.newReport(token, user_id, title, issue, sector, entity, lat, lon, date, description);
			Log.d("Values passed", "Token: "+token+" user id: "+user_id+" title "+title+" issue "+issue+" sector "+sector+" entity "+entity+" lat "+lat+" lon "+lon+" date "+date+" description "+description);
			try {
				String res = json.getString(KEY_SUCCESS); 
					if(res.equals("OK")){
						JSONArray json_report = json.getJSONArray("message");//json.getJSONObject("message");
						JSONObject serverid = json_report.getJSONObject(0);//json_report.getString(KEY_ID);
						String srid = String.valueOf(serverid.getString(KEY_ID));
						//Update report with server id 
						Report report = Report.get(getApplicationContext(), rid);
						report.setServerId(srid);
						report.save();
						
						//Upload corresponding media files :O
						uploadEntities(report.getId(), Integer.parseInt(srid));
						uploadMedia(report.getId(), Integer.parseInt(srid));
					}else{
						//Some error message. Not sure what yet.
						
					}
					
				}catch(JSONException e){
					e.printStackTrace();
				}
			//Add to log
			
        	return null;
        }
        

        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
           // pDialog.dismiss();
        	log.append("Creating Report: "+title+"\n");
        	//pDialog.setVisibility(View.GONE);
        }
	}
	class updateReport extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setVisibility(View.VISIBLE); 
        }
        
        protected String doInBackground(String... args) {
        	UserFunctions userFunction = new UserFunctions();
			JSONObject json = userFunction.updateReport(token, user_id, title, issue, sector, entity, lat, lon, date, description, serverID);
			
			try {
				String res = json.getString(KEY_SUCCESS); 
					if(res.equals("OK")){
						JSONArray json_report = json.getJSONArray("message");//json.getJSONObject("message");
						//Update objects
						updateMedia(rid, Integer.parseInt(serverID));
					}else{
						//Some error message. Not sure what yet.
					}
				}catch(JSONException e){
					e.printStackTrace();
				}
			//Add to log
        	return null;
        }
        

        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
           // pDialog.dismiss();
        	log.append("Updating Report: "+title+"\n");
        	//pDialog.setVisibility(View.GONE);
        }
	}
	class checkToken extends AsyncTask<String, String, String> {
		 
  
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setVisibility(View.VISIBLE);
            
        }
        protected String doInBackground(String... args) {
	
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	        token = settings.getString("token",null);
	        user_id = settings.getString("user_id",null);
	        
	        if(token==null){
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
           // pDialog.dismiss();
        	//pDialog.setVisibility(View.GONE);
            loopReports();
        }
	}
	class createEntity extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setVisibility(View.VISIBLE); 
        }
        
        protected String doInBackground(String... args) {
        	UserFunctions userFunction = new UserFunctions();
			JSONObject json = userFunction.newEntity(entityName, serverID, token, user_id, date, String.valueOf(esequence));
			Log.d("Values passed", "Entity: "+entityName+" ServerID : "+serverID);
			try {
				String res = json.getString(KEY_SUCCESS); 
					if(res.equals("OK")){
						JSONArray json_report = json.getJSONArray("message");//json.getJSONObject("message");
						JSONObject details = json_report.getJSONObject(0);//json_report.getString(KEY_ID);
						String object_id = String.valueOf(details.getString("object_id"));
						String sequence_id = String.valueOf(details.getString("sequence_id"));
						
						//Update entity with object id and sequence id
						Entity entity = Entity.get(getApplicationContext(), entityid);
						entity.setObjectID(object_id);
						entity.setSequenceId(sequence_id);
						entity.save();
					}else{
						//Some error message. Not sure what yet.
						
					}
					
				}catch(JSONException e){
					e.printStackTrace();
				}
			//Add to log
			
        	return null;
        }
        
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
           // pDialog.dismiss();
        	log.append("--Creating Entity: "+entityName+"\n");
        	//pDialog.setVisibility(View.GONE);
        }
	}
}
