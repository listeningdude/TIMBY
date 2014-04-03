package org.codeforafrica.timby.login;



import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.codeforafrica.timby.AppConstants;
import org.json.JSONArray;
import org.json.JSONObject;


import redstone.xmlrpc.util.Base64;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class UserFunctions {
	
	private JSONParser jsonParser;
	private static String loginURL = "/login";
	private static String logoutURL = "/logout";
	private static String sectorsURL = "/getsectors";
	private static String categoriesURL = "/getcategories";
	private static String entitiesURL = "/getentities";
	private static String tokenCheckURL = "/tokencheck";
	private static String createreportURL = "/createreport";	
	private static String updatereportURL = "/updatereport";	
	private static String insertobjectURL = "/insertobject";
	private static String updateobjectURL = "/updateobject";
	private static String registerURL = "";
	
	// constructor
	public UserFunctions(){
		jsonParser = new JSONParser();
	}
	
	public JSONObject loginUser(String username, String password, Context ctx){
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("user_name", username));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("key", getAPIKey(ctx)));
		JSONObject json = jsonParser.getJSONFromUrl(getPath(loginURL,ctx), params);
		// return json
		 Log.e("api key", getAPIKey(ctx));
		return json;
	}
	public JSONObject logoutUser(String username, String password, Context ctx){
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("user_name", username));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("key", getAPIKey(ctx)));
		JSONObject json = jsonParser.getJSONFromUrl(getPath(logoutURL,ctx), params);
		// return json
		// Log.e("JSON", json.toString());
		return json;
	}
	public JSONObject checkTokenValidity(String user_id, String token, Context ctx){
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("user_id", user_id));
		params.add(new BasicNameValuePair("token", token));
		JSONObject json = jsonParser.getJSONFromUrl(getPath(tokenCheckURL,ctx), params);
		// return json
		// Log.e("JSON", json.toString());
		return json;
	}
	
	public JSONObject registerUser(String username, String password, Context ctx){
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", password));
		
		// getting JSON Object
		JSONObject json = jsonParser.getJSONFromUrl(getPath(registerURL,ctx), params);
		// return json
		return json;
	}
	public JSONObject newReport(String token, String user_id, String title,
		String issue, String sector, String entity, String lat, String lon,	String date, String description, Context ctx) {
		// TODO Auto-generated method stub
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("token", token));
	
		params.add(new BasicNameValuePair("user_id", user_id));

		params.add(new BasicNameValuePair("title", title));
	
		params.add(new BasicNameValuePair("category", issue));
	
		params.add(new BasicNameValuePair("sector", sector));
	
	
		params.add(new BasicNameValuePair("description", description));
		
		//params.add(new BasicNameValuePair("company", entity));
		
		params.add(new BasicNameValuePair("report_date", date));
	
		params.add(new BasicNameValuePair("lat", lat));

		params.add(new BasicNameValuePair("long", lon));
	
		params.add(new BasicNameValuePair("key", getAPIKey(ctx)));
		
		params.add(new BasicNameValuePair("imei", getIMEI(ctx)));
		// getting JSON Object
		JSONObject json = jsonParser.getJSONFromUrl(getPath(createreportURL,ctx), params);
		// return json
		return json;
	}
	public String getIMEI(Context context){

	    TelephonyManager mngr = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE); 
	    String imei = mngr.getDeviceId();
	    return imei;
	}

	public JSONObject getSectors(String token, String user_id, Context ctx){
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("user_id", user_id));
		params.add(new BasicNameValuePair("token", token));
		params.add(new BasicNameValuePair("key", getAPIKey(ctx)));
		JSONObject json = jsonParser.getJSONFromUrl(getPath(sectorsURL,ctx), params);
		Log.d("details passed", "user_id:"+user_id+" token:"+token+" key:"+getAPIKey(ctx));
		return json;
	}
	public JSONObject getCategories(String token, String user_id, Context ctx){
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("user_id", user_id));
		params.add(new BasicNameValuePair("token", token));
		params.add(new BasicNameValuePair("key", getAPIKey(ctx)));
		JSONObject json = jsonParser.getJSONFromUrl(getPath(categoriesURL,ctx), params);
		return json;
	}
	public JSONObject updateReport(String token, String user_id, String title,
			String issue, String sector, String entity, String lat, String lon,	String date, String description, String serverID, Context ctx) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("token", token));
		
			params.add(new BasicNameValuePair("user_id", user_id));

			params.add(new BasicNameValuePair("title", title));
		
			params.add(new BasicNameValuePair("category", issue));
		
			params.add(new BasicNameValuePair("sector", sector));
		
			params.add(new BasicNameValuePair("description", description));
			
			//params.add(new BasicNameValuePair("company", entity));
			
			params.add(new BasicNameValuePair("report_date", date));
		
			params.add(new BasicNameValuePair("lat", lat));

			params.add(new BasicNameValuePair("long", lon));
		
			params.add(new BasicNameValuePair("key", getAPIKey(ctx)));
			
			params.add(new BasicNameValuePair("report_id", serverID));
			
			params.add(new BasicNameValuePair("imei", getIMEI(ctx)));
			// getting JSON Object
			JSONObject json = jsonParser.getJSONFromUrl(getPath(updatereportURL,ctx), params);
			// return json
			return json;
		}
	public JSONObject updateObject(String token, String user_id, String ptitle, String psequence, String preportid, String ptype, String optype, String pid, String pdate, String path, Context ctx) {
		// TODO Auto-generated method stub
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("token", token));
		params.add(new BasicNameValuePair("user_id", user_id));
		params.add(new BasicNameValuePair("title", ptitle));
		params.add(new BasicNameValuePair("sequence", psequence));
		params.add(new BasicNameValuePair("report_id", preportid));
		params.add(new BasicNameValuePair("object_type", optype));
		params.add(new BasicNameValuePair("key", getAPIKey(ctx)));
		params.add(new BasicNameValuePair("object_id", pid));
		params.add(new BasicNameValuePair("report_date", pdate));
		params.add(new BasicNameValuePair("narrative", "(empty)"));
		// getting JSON Object
		JSONObject json = jsonParser.getJSONFromUrl(getPath(updateobjectURL,ctx), params);
		// return json
		return json;
	}	
		//add media
	public JSONObject newObject(String token, String user_id, String ptitle, String psequence, String preportid, String ptype, String optype, String pid, String pdate, String path, Context ctx) {
			//add media
			MultipartEntity mpEntity = new MultipartEntity();
			try{				
				ContentBody content = new FileBody(new File(path), ptype);
				//Log.d("What's null?", String.valueOf(content)+"userid"+user_id+"ptitle"+ptitle+"psequence"+psequence+"preportid"+preportid+"ptype"+ptype+"optype"+optype+api_key+pdate);
				mpEntity.addPart("userfile", content);
				mpEntity.addPart("token", new StringBody(token));
				mpEntity.addPart("user_id", new StringBody(user_id));
				mpEntity.addPart("title", new StringBody(ptitle));
				mpEntity.addPart("sequence", new StringBody(psequence));
				mpEntity.addPart("report_id", new StringBody(preportid));
				mpEntity.addPart("object_type", new StringBody(optype));
				mpEntity.addPart("key", new StringBody( getAPIKey(ctx)));
				//mpEntity.addPart("object_id", new StringBody(pid));
				mpEntity.addPart("report_date", new StringBody(pdate));
				mpEntity.addPart("narrative", new StringBody("(empty)"));
			} catch (IOException e) {
	            Log.e("mpEntity Error", e.getMessage(), e);

	        }
			// getting JSON Object
			JSONObject json = jsonParser.getJSONFromUrl_Object(getPath(insertobjectURL,ctx), mpEntity);
			
			// return json
			return json;
		}
	/**
	 * Function get Login status
	 * */
	public boolean isUserLoggedIn(Context context){
		DatabaseHandler db = new DatabaseHandler(context);
		int count = db.getRowCount();
		if(count > 0){
			// user logged in
			return true;
		}
		return false;
	}
	
	/**
	 * Function to logout user
	 * Reset Database
	 * */
	public boolean logoutUser(Context context){
		DatabaseHandler db = new DatabaseHandler(context);
		db.resetTables();
		return true;
	}

	public JSONObject newEntity(String entityName, String serverID, String token, String user_id, String pdate, String psequence, Context ctx) {
		// TODO Auto-generated method stub
		// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("token", token));
				params.add(new BasicNameValuePair("user_id", user_id));
				params.add(new BasicNameValuePair("title", entityName));
				params.add(new BasicNameValuePair("sequence", psequence));
				params.add(new BasicNameValuePair("report_id", serverID));
				params.add(new BasicNameValuePair("object_type", "entity"));
				params.add(new BasicNameValuePair("key", getAPIKey(ctx)));
				params.add(new BasicNameValuePair("report_date", pdate));
				params.add(new BasicNameValuePair("narrative", "(empty)"));
				// getting JSON Object
				JSONObject json = jsonParser.getJSONFromUrl(getPath(insertobjectURL,ctx), params);
				// return json
				return json;
	}

	public JSONObject updateEntity(String entityName, String serverID,
			String token, String user_id, int entity_object_id, Context ctx) {
			//Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("token", token));
			params.add(new BasicNameValuePair("user_id", user_id));
			params.add(new BasicNameValuePair("title", entityName));
			params.add(new BasicNameValuePair("report_id", serverID));
			params.add(new BasicNameValuePair("object_type", "entity"));
			params.add(new BasicNameValuePair("object_id", String.valueOf(entity_object_id)));
			params.add(new BasicNameValuePair("key", getAPIKey(ctx)));
			params.add(new BasicNameValuePair("narrative", "(empty)"));
			// getting JSON Object
			JSONObject json = jsonParser.getJSONFromUrl(getPath(updateobjectURL, ctx), params);
			// return json
			return json;
	}

	public JSONObject getEntities(String token, String user_id, Context ctx) {
		// TODO Auto-generated method stub
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("user_id", user_id));
		params.add(new BasicNameValuePair("token", token));
		params.add(new BasicNameValuePair("key", getAPIKey(ctx)));
		JSONObject json = jsonParser.getJSONFromUrl(getPath(entitiesURL,ctx), params);
		return json;

	}

	private String getPath(String originalPath, Context ctx) {
		// TODO Auto-generated method stub
   	 	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
    	String api_base_url = prefs.getString("api_base_url",null);
		String newPath = api_base_url+originalPath;
		return newPath;
	}

	public final static String getAPIKey(Context ctx){
   	 	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
    	String api_key = prefs.getString("api_key",null);
		return api_key;
	}
}