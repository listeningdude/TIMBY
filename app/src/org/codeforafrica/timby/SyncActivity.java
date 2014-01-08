package org.codeforafrica.timby;

import java.util.ArrayList;
import java.util.concurrent.Executor;

import org.holoeverywhere.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.codeforafrica.timby.R;
import org.codeforafrica.timby.login.UserFunctions;
import org.codeforafrica.timby.model.Entity;
import org.codeforafrica.timby.model.Media;
import org.codeforafrica.timby.model.Project;
import org.codeforafrica.timby.model.Report;
import org.codeforafrica.timby.server.LoginPreferencesActivity;

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
	/*String lat;
	String lon;
	String title;
	String issue;
	String sector;
	String description;
	String date;
	String pdate;
	String entity;
	*/
	/*String serverID;
	String entityName;
	int esequence;
	int rid;
	int entityid;
	int entity_object_id;
*/
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
			
			String lat = "0";
			String lon = "0";
			
			if(location.contains(", ")){
				String[] locations = location.split(", ");
				lat = locations[0];
				lon = locations[1];
			}
						
			String title = report.getTitle();
			
			int issue_int;
			if(Integer.parseInt(report.getIssue())==0){
				issue_int=1;
			}else{
				issue_int = Integer.parseInt(report.getIssue());
			}
			
			int sector_int;
			if(Integer.parseInt(report.getSector())==0){
				sector_int=1;
			}else{
				sector_int = Integer.parseInt(report.getSector());
			}
			
			String issue = String.valueOf(issue_int);
			String sector = String.valueOf(sector_int);
			
			String entity = report.getEntity();
			
			String date = report.getDate();
			int rid = report.getId();
			String description = report.getDescription();
			int serverID = Integer.parseInt(report.getServerId());
			if(serverID==0){
				//report is not created yet, create and grab server id
				//new createReport().execute();
				ReportTaskParams params = new ReportTaskParams(rid, serverID, title, issue, sector, entity, lat, lon, date, description);
			 	createReport myTask = new createReport();
			 	myTask.execute(params);		 
			}else{
				//update report
				//get report servid
				//new updateReport().execute();
				ReportTaskParams params = new ReportTaskParams(rid, serverID, title, issue, sector, entity, lat, lon, date, description);
			 	updateReport myTask = new updateReport();
			 	myTask.execute(params);		 
			}
		}
	}
	public void uploadEntities(int rid, int serverid){
		String serverID = String.valueOf(serverid);
		ArrayList<Entity> mListEntities;
		mListEntities = Entity.getAllAsList(this, rid);
	 	for (int j = 0; j < mListEntities.size(); j++) {
	 		Entity entity = mListEntities.get(j);
	 	    String entityName = entity.getEntity();
	 		int entityid = entity.getId();
	 		String entitydate = entity.getDate();
        	int esequence = j+1;
        	String report = entity.getReport();
        	int entity_object_id = entity.getObjectID();
        	//new createEntity().execute();
        	
        	EntityTaskParams params = new EntityTaskParams(entity_object_id, report, entityName, entityid, entitydate, esequence);
		 	createEntity myTask = new createEntity();
		 	myTask.execute(params);	
	 	}
	}
	public void updateEntities(int rid, int serverid){
		String serverID = String.valueOf(serverid);
		ArrayList<Entity> mListEntities;
		mListEntities = Entity.getAllAsList(this, rid);
	 	for (int j = 0; j < mListEntities.size(); j++) {
	 		Entity entity = mListEntities.get(j);
	 		String entityName = entity.getEntity();
		 	int entityid = entity.getId();
		 	String entitydate = entity.getDate();
	        int esequence = j+1;
	        String report = entity.getReport();
	        int entity_object_id = entity.getObjectID();
        	//new updateEntity().execute(); 	
        	EntityTaskParams params = new EntityTaskParams(entity_object_id, report, entityName, entityid, entitydate, esequence);
		 	updateEntity myTask = new updateEntity();
		 	myTask.execute(params);	
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
	private static class EntityTaskParams {
	    String report;
	    String entityName;
	    int entityid;
	    String entitydate;
	    int esequence;
	    int entity_object_id;
	    
	    EntityTaskParams(int entity_object_id, String report, String entityName, int entityid, String entitydate, int esequence) {
	        this.report = report;
	        this.entityName= entityName;
	        this.entityid = entityid;
	        this.entitydate = entitydate;
	        this.esequence = esequence;
	        this.entity_object_id = entity_object_id;
	    }
	}
	
	private static class ReportTaskParams {
	    String title;
	    String issue;
	    String sector;
	    String entity;
	    String lat;
	    String lon;
	    String date;
	    String description;
	    int rid;
	    int serverID;
	    
	    ReportTaskParams(int rid, int serverID, String title, String issue, String sector, String entity, String lat, String lon, String date, String description) {
	        this.title = title;
	        this.issue = issue;
	        this.sector = sector;
	        this.entity = entity;
	        this.lat = lat;
	        this.lon=lon;
	        this.date=date;
	        this.description=description;
	        this.rid = rid;
	        this.serverID=serverID;
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
			
			
        	return ppath;
        }
       
        protected void onPostExecute(String ppath) {
            // dismiss the dialog after getting all products
            // pDialog.dismiss();
        	log.append("Updated media object "+ppath+"\n");
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
			
			
        	return ppath;
        }
        protected void onPostExecute(String ppath) {
            // dismiss the dialog after getting all products
           // pDialog.dismiss();
        	log.append("Uploaded media object "+ppath+"\n");
        	//pDialog.setVisibility(View.GONE);
        }
	}
	class createReport extends AsyncTask<ReportTaskParams, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setVisibility(View.VISIBLE); 
        }
        
        protected String doInBackground(ReportTaskParams... params) {
        	String title = params[0].title;
        	String issue = params[0].issue;
        	String sector= params[0].sector;
    	    String entity = params[0].entity;;
    	    String lat = params[0].lat;;
    	    String lon = params[0].lon;;
    	    String date = params[0].date;;
    	    String description = params[0].description;;
    	    int rid = params[0].rid;
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
			
			return title;
        }
        

        protected void onPostExecute(String title) {
            // dismiss the dialog after getting all products
           // pDialog.dismiss();
        	log.append("Created report "+title+"\n");
        	//pDialog.setVisibility(View.GONE);
        }
	}
	class updateReport extends AsyncTask<ReportTaskParams, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setVisibility(View.VISIBLE); 
        }
        
        protected String doInBackground(ReportTaskParams... params) {
        	String title = params[0].title;
        	String issue = params[0].issue;
        	String sector= params[0].sector;
    	    String entity = params[0].entity;;
    	    String lat = params[0].lat;;
    	    String lon = params[0].lon;;
    	    String date = params[0].date;;
    	    String description = params[0].description;
    	    int rid = params[0].rid;
    	    int serverID = params[0].serverID;
        	UserFunctions userFunction = new UserFunctions();
			JSONObject json = userFunction.updateReport(token, user_id, title, issue, sector, entity, lat, lon, date, description, String.valueOf(serverID));
			
			try {
				String res = json.getString(KEY_SUCCESS); 
					if(res.equals("OK")){
						JSONArray json_report = json.getJSONArray("message");//json.getJSONObject("message");
						//Update objects
						updateEntities(rid, serverID);
						updateMedia(rid, serverID);
					}else{
						//Some error message. Not sure what yet.
					}
				}catch(JSONException e){
					e.printStackTrace();
				}
			//Add to log
        	return title;
        }
        

        protected void onPostExecute(String title) {
            // dismiss the dialog after getting all products
           // pDialog.dismiss();
        	log.append("Updated report "+title+"\n");
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
	class createEntity extends AsyncTask<EntityTaskParams, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setVisibility(View.VISIBLE); 
        }
        
        protected String doInBackground(EntityTaskParams... params) {
        	String entityName = params[0].entityName;
        	String serverID = params[0].report;
        	String date= params[0].entitydate;
    	    int esequence = params[0].esequence;
    	    int entityid = params[0].entityid;
    	   
    	    
        	UserFunctions userFunction = new UserFunctions();
			JSONObject json = userFunction.newEntity(entityName, serverID, token, user_id, date, String.valueOf(esequence));
			Log.d("Values passed", "Entity: "+entityName+" ServerID : "+serverID+" Date: "+date+" Sequence: "+String.valueOf(esequence));
			try {
				String res = json.getString(KEY_SUCCESS); 
					if(res.equals("OK")){
						JSONArray json_report = json.getJSONArray("message");//json.getJSONObject("message");
						JSONObject details = json_report.getJSONObject(0);//json_report.getString(KEY_ID);
						int object_id = Integer.parseInt(details.getString("object_id"));
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
			
			return entityName;
        }
        
        protected void onPostExecute(String entityName) {
            // dismiss the dialog after getting all products
           // pDialog.dismiss();
        	log.append("Created entity "+entityName+"\n");
        	//pDialog.setVisibility(View.GONE);
        }
	}
	class updateEntity extends AsyncTask<EntityTaskParams, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setVisibility(View.VISIBLE); 
        }
        
        protected String doInBackground(EntityTaskParams... params) {
        	String entityName = params[0].entityName;
        	String serverID = params[0].report;
        	String date= params[0].entitydate;
    	    int esequence = params[0].esequence;
    	    int entityid = params[0].entityid;
    	    int entity_object_id = params[0].entity_object_id;
        	UserFunctions userFunction = new UserFunctions();
			JSONObject json = userFunction.updateEntity(entityName, serverID, token, user_id, entity_object_id);
			Log.d("Values passed", "Entity: "+entityName+" ServerID : "+serverID);
			try {
				String res = json.getString(KEY_SUCCESS); 
					if(res.equals("OK")){
						
					}else{
						//Some error message. Not sure what yet.
						
					}
					
				}catch(JSONException e){
					e.printStackTrace();
				}
			//Add to log
			
        	return entityName;
        }
        
        protected void onPostExecute(String entityName) {
            // dismiss the dialog after getting all products
           // pDialog.dismiss();
        	log.append("Updated entity "+entityName+"\n");
        	//pDialog.setVisibility(View.GONE);
        }
	}
}
