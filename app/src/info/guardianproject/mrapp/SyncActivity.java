package info.guardianproject.mrapp;

import org.holoeverywhere.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import info.guardianproject.mrapp.login.UserFunctions;
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
	private static String KEY_ERROR_MSG = "message";
	ProgressDialog pDialog;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sync);
		//if token not valid, redirect to login
			new checkToken().execute();
		//loop through projects
		//if project is not uploaded yet
			//create projects
			//upload files
		//else
			//if changes made
				//update project
	}
	class checkToken extends AsyncTask<String, String, String> {
		 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SyncActivity.this); 
            pDialog.setMessage("Checking token..."); 
            pDialog.setIndeterminate(false); 
            pDialog.setCancelable(false); 
            pDialog.show(); 
        }
 
        /**
         * getting All products from url
         * */
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
        
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
        }
	}
}
