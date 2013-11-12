package info.guardianproject.mrapp;

import org.holoeverywhere.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import info.guardianproject.mrapp.login.UserFunctions;
import info.guardianproject.mrapp.server.LoginPreferencesActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class SyncActivity extends BaseActivity{
	String token;
	String user_id;
	private static String KEY_SUCCESS = "status";
	private static String KEY_ERROR_MSG = "message";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//if token not valid, redirect to login
			checkToken();		
		//loop through projects
		//if project is not uploaded yet
			//create projects
			//upload files
		//else
			//if changes made
				//update project
	}
	public void checkToken(){
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        token = settings.getString("token",null);
        user_id = settings.getString("user_id",null);
        
        if(token==null){
        	Intent login = new Intent(getApplicationContext(), LoginPreferencesActivity.class);
			login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(login);
        }
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
}
