package info.guardianproject.mrapp.login;

import info.guardianproject.mrapp.AppConstants;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;

public class UserFunctions {
	
	private JSONParser jsonParser;
	private static String loginURL = "https://timby.org/mobileapi/api/login";
	private static String logoutURL = "https://timby.org/mobileapi/api/logout";
	private static String tokenCheckURL = "https://timby.org/mobileapi/api/tokencheck";
	private static String createreportURL = "https://timby.org/mobileapi/api/createreport";	
	private static String registerURL = "";
	private static String api_key = AppConstants.API_KEY;

	
	// constructor
	public UserFunctions(){
		jsonParser = new JSONParser();
	}
	
	public JSONObject loginUser(String username, String password){
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("user_name", username));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("key", api_key));
		JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
		// return json
		// Log.e("JSON", json.toString());
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
		String issue, String sector, String entity, String lat, String lon,	String date) {
		// TODO Auto-generated method stub
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("token", token));
		params.add(new BasicNameValuePair("user_id", user_id));
		params.add(new BasicNameValuePair("title", title));
		params.add(new BasicNameValuePair("category", issue));
		params.add(new BasicNameValuePair("sector", sector));
		params.add(new BasicNameValuePair("company", entity));
		params.add(new BasicNameValuePair("report_date", date));
		params.add(new BasicNameValuePair("lat", lat));
		params.add(new BasicNameValuePair("long", lon));
		params.add(new BasicNameValuePair("key", AppConstants.API_KEY));
		// getting JSON Object
		JSONObject json = jsonParser.getJSONFromUrl(createreportURL, params);
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

	
	
}
