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
import org.codeforafrica.timby.PrivateCredentials;
import org.json.JSONArray;
import org.json.JSONObject;


import redstone.xmlrpc.util.Base64;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class UserFunctions {
	
	private JSONParser jsonParser;
	private static String loginURL = PrivateCredentials.API_PATH+"/login";
	private static String logoutURL = PrivateCredentials.API_PATH+"/logout";
	private static String sectorsURL = PrivateCredentials.API_PATH+"/getsectors";
	private static String categoriesURL = PrivateCredentials.API_PATH+"/getcategories";
	private static String entitiesURL = PrivateCredentials.API_PATH+"/getentities";
	private static String tokenCheckURL = PrivateCredentials.API_PATH+"/tokencheck";
	private static String createreportURL = PrivateCredentials.API_PATH+"/createreport";	
	private static String updatereportURL = PrivateCredentials.API_PATH+"/updatereport";	
	private static String insertobjectURL = PrivateCredentials.API_PATH+"/insertobject";
	private static String updateobjectURL = PrivateCredentials.API_PATH+"/updateobject";
	private static String registerURL = "";
	private static String api_key = PrivateCredentials.API_KEY;
	private static String[] api_keys = PrivateCredentials.API_KEYS;
	
	// constructor
	public UserFunctions(){
		jsonParser = new JSONParser();
	}
	
	public JSONObject loginUser(String username, String password){
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("user_name", username));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("key", getAPIKey_un(username)));
		JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
		// return json
		 Log.e("api key", getAPIKey_un(username));
		return json;
	}
	public JSONObject logoutUser(String username, String password){
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("user_name", username));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("key", api_key));
		JSONObject json = jsonParser.getJSONFromUrl(logoutURL, params);
		// return json
		// Log.e("JSON", json.toString());
		return json;
	}
	public JSONObject checkTokenValidity(String user_id, String token){
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("user_id", user_id));
		params.add(new BasicNameValuePair("token", token));
		JSONObject json = jsonParser.getJSONFromUrl(tokenCheckURL, params);
		// return json
		// Log.e("JSON", json.toString());
		return json;
	}
	
	public JSONObject registerUser(String username, String password){
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", password));
		
		// getting JSON Object
		JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
		// return json
		return json;
	}
	public JSONObject newReport(String token, String user_id, String title,
		String issue, String sector, String entity, String lat, String lon,	String date, String description) {
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
	
		params.add(new BasicNameValuePair("key", getAPIKey_id(user_id)));

		// getting JSON Object
		JSONObject json = jsonParser.getJSONFromUrl(createreportURL, params);
		// return json
		return json;
	}
	public JSONArray getSectors(String token, String user_id){
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("user_id", user_id));
		params.add(new BasicNameValuePair("token", token));
		params.add(new BasicNameValuePair("key", getAPIKey_id(user_id)));
		JSONArray json = jsonParser.getJSONArrayFromURL(sectorsURL, params);
		Log.d("details passed", "user_id:"+user_id+" token:"+token+" key:"+getAPIKey_id(user_id));
		return json;
	}
	public JSONArray getCategories(String token, String user_id){
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("user_id", user_id));
		params.add(new BasicNameValuePair("token", token));
		params.add(new BasicNameValuePair("key", getAPIKey_id(user_id)));
		JSONArray json = jsonParser.getJSONArrayFromURL(categoriesURL, params);
		return json;
	}
	public JSONObject updateReport(String token, String user_id, String title,
			String issue, String sector, String entity, String lat, String lon,	String date, String description, String serverID) {
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
		
			params.add(new BasicNameValuePair("key", getAPIKey_id(user_id)));
			
			params.add(new BasicNameValuePair("report_id", serverID));

			// getting JSON Object
			JSONObject json = jsonParser.getJSONFromUrl(updatereportURL, params);
			// return json
			return json;
		}
	public JSONObject updateObject(String token, String user_id, String ptitle, String psequence, String preportid, String ptype, String optype, String pid, String pdate, String path) {
		// TODO Auto-generated method stub
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("token", token));
		params.add(new BasicNameValuePair("user_id", user_id));
		params.add(new BasicNameValuePair("title", ptitle));
		params.add(new BasicNameValuePair("sequence", psequence));
		params.add(new BasicNameValuePair("report_id", preportid));
		params.add(new BasicNameValuePair("object_type", optype));
		params.add(new BasicNameValuePair("key", getAPIKey_id(user_id)));
		params.add(new BasicNameValuePair("object_id", pid));
		params.add(new BasicNameValuePair("report_date", pdate));
		params.add(new BasicNameValuePair("narrative", "(empty)"));
		// getting JSON Object
		JSONObject json = jsonParser.getJSONFromUrl(updateobjectURL, params);
		// return json
		return json;
	}	
		//add media
	public JSONObject newObject(String token, String user_id, String ptitle, String psequence, String preportid, String ptype, String optype, String pid, String pdate, String path) {
			//add media
			MultipartEntity mpEntity = new MultipartEntity();
			try{
				
				ContentBody content = new FileBody(new File(path), ptype);
				Log.d("What's null?", String.valueOf(content)+"userid"+user_id+"ptitle"+ptitle+"psequence"+psequence+"preportid"+preportid+"ptype"+ptype+"optype"+optype+api_key+pdate);
				
				mpEntity.addPart("userfile", content);
				mpEntity.addPart("token", new StringBody(token));
				mpEntity.addPart("user_id", new StringBody(user_id));
				mpEntity.addPart("title", new StringBody(ptitle));
				mpEntity.addPart("sequence", new StringBody(psequence));
				mpEntity.addPart("report_id", new StringBody(preportid));
				mpEntity.addPart("object_type", new StringBody(optype));
				mpEntity.addPart("key", new StringBody( getAPIKey_id(user_id)));
				//mpEntity.addPart("object_id", new StringBody(pid));
				mpEntity.addPart("report_date", new StringBody(pdate));
				mpEntity.addPart("narrative", new StringBody("(empty)"));
			} catch (IOException e) {
	            Log.e("mpEntity Error", e.getMessage(), e);

	        }
			// getting JSON Object
			JSONObject json = jsonParser.getJSONFromUrl_Object(insertobjectURL, mpEntity);
			
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

	public JSONObject newEntity(String entityName, String serverID, String token, String user_id, String pdate, String psequence) {
		// TODO Auto-generated method stub
		// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("token", token));
				params.add(new BasicNameValuePair("user_id", user_id));
				params.add(new BasicNameValuePair("title", entityName));
				params.add(new BasicNameValuePair("sequence", psequence));
				params.add(new BasicNameValuePair("report_id", serverID));
				params.add(new BasicNameValuePair("object_type", "entity"));
				params.add(new BasicNameValuePair("key", getAPIKey_id(user_id)));
				params.add(new BasicNameValuePair("report_date", pdate));
				params.add(new BasicNameValuePair("narrative", "(empty)"));
				// getting JSON Object
				JSONObject json = jsonParser.getJSONFromUrl(insertobjectURL, params);
				// return json
				return json;
	}

	public JSONObject updateEntity(String entityName, String serverID,
			String token, String user_id, int entity_object_id) {
			//Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("token", token));
			params.add(new BasicNameValuePair("user_id", user_id));
			params.add(new BasicNameValuePair("title", entityName));
			params.add(new BasicNameValuePair("report_id", serverID));
			params.add(new BasicNameValuePair("object_type", "entity"));
			params.add(new BasicNameValuePair("object_id", String.valueOf(entity_object_id)));
			params.add(new BasicNameValuePair("key", getAPIKey_id(user_id)));
			params.add(new BasicNameValuePair("narrative", "(empty)"));
			// getting JSON Object
			JSONObject json = jsonParser.getJSONFromUrl(updateobjectURL, params);
			// return json
			return json;
	}

	public JSONArray getEntities(String token, String user_id) {
		// TODO Auto-generated method stub
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("user_id", user_id));
		params.add(new BasicNameValuePair("token", token));
		params.add(new BasicNameValuePair("key", getAPIKey_id(user_id)));
		JSONArray json = jsonParser.getJSONArrayFromURL(entitiesURL, params);
		return json;

	}

	public final static String getAPIKey_un(String username){
		int userInd = Arrays.asList(PrivateCredentials.USERNAMES).indexOf(username);
		String key = PrivateCredentials.API_KEYS[userInd];
		return key;
	}
	public final static String getAPIKey_id(String userid){
		int user_id = Integer.parseInt(userid)-1;
		String key = PrivateCredentials.API_KEYS[user_id];
		return key;
	}
}