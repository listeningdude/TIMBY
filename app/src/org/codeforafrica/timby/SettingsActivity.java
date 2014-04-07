package org.codeforafrica.timby;

import org.codeforafrica.timby.login.UserFunctions;
import org.codeforafrica.timby.model.Project;
import org.codeforafrica.timby.server.ConnectionDetector;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SettingsActivity extends BaseActivity implements OnClickListener{
    private Dialog dialog;
	private static String KEY_SUCCESS = "status";

    EditText eTVid;
    
    CheckBox checkDeleteExport;
    CheckBox checkDeleteSync;
    CheckBox checkEncrypt;
    Button btnSave;

    EditText eTUsername;
    EditText eTPassword;
    
    String old_password;
    String old_username;
    
    
 // Connection detector class
    ConnectionDetector cd;
    //flag for Internet connection status
    Boolean isInternetPresent = false;
    private ProgressDialog pDialog;
    public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_settings);
    	// creating connection detector class instance
        cd = new ConnectionDetector(getApplicationContext());
        
		//get Internet status
        isInternetPresent = cd.isConnectingToInternet();
                
    	checkEncrypt = (CheckBox)findViewById(R.id.checkEncrypt);
    	checkDeleteSync = (CheckBox)findViewById(R.id.checkDeleteSync);
    	checkDeleteExport = (CheckBox)findViewById(R.id.checkDeleteExport);
    	btnSave = (Button)findViewById(R.id.btnSave);
    	
    	eTVid = (EditText)findViewById(R.id.eTVid);
    	
    	eTUsername = (EditText)findViewById(R.id.eTUsername);
    	eTPassword = (EditText)findViewById(R.id.eTPassword);
    	
	    loginDialog();
	    
	    btnSave.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveSettings();
			}
	    });
    }
    
    public void loginDialog(){
    	dialog = new Dialog(SettingsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    dialog.setContentView(R.layout.dialog_settings_login);
	    dialog.findViewById(R.id.btnLogin).setOnClickListener(SettingsActivity.this);
	    dialog.setCancelable(false);
	    dialog.show();
    }
    public void saveSettings(){
    	 SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
         Editor edit = settings.edit(); 
        
         if(checkEncrypt.isChecked()){
             edit.putString("encrypt_zip_files", "1");
         }else{
        	 edit.putString("encrypt_zip_files", "0");
         }
         if(checkDeleteSync.isChecked()){
             edit.putString("delete_after_sync", "1");
         }else{
        	 edit.putString("delete_after_sync", "0");
         }
         if(checkDeleteExport.isChecked()){
             edit.putString("delete_after_export", "1");
         }else{
        	 edit.putString("delete_after_export", "0");
         }   	
         
         
         String maxL = eTVid.getText().toString();
         int imaxL = Integer.parseInt(maxL);
         imaxL = (int)(imaxL*60);
         
         edit.putString("maximum_video_length", String.valueOf(imaxL));
         

         edit.commit();       
    	
         Toast.makeText(getApplicationContext(), "Settings have been saved!", Toast.LENGTH_LONG).show();
		
    	
    	check_profile_change();
    }
    public void check_profile_change(){
    	if((!eTUsername.getText().toString().equals(old_username))||
    			(!eTPassword.getText().toString().equals(old_password))){
    		//Request password change
    		if(!isInternetPresent){
	        	Toast.makeText(getApplicationContext(), "Could not update username/password change. Check your connection!", Toast.LENGTH_LONG).show();
    		}else{
        		new profile_changes().execute();    		
    		}
    	}
    }
    class profile_changes extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SettingsActivity.this);
            pDialog.setMessage("Updating user profile...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        protected String doInBackground(String... args) {
        	String success = "0";
            //send token, user_id, username, password
        	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
 	        String token = settings.getString("token",null);
 	        String user_id = settings.getString("user_id",null);
 	        String username = eTUsername.getText().toString();
 	        String password = eTPassword.getText().toString();
 	        UserFunctions userFunction = new UserFunctions();
 	        JSONObject json = userFunction.updateUser(token, user_id, username, password, getApplicationContext());
 	       try {
				String res = json.getString(KEY_SUCCESS); 
				if(res.equals("OK")){
					//successfull
			        Editor edit = settings.edit(); 
					edit.putString("username", eTUsername.getText().toString());
			        edit.putString("password", eTPassword.getText().toString());
			        edit.commit(); 
			        success = "1";
			        
				}else{
					//not successfull
					success = "0";
				}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
 	        		
        	return success;
        }

        protected void onPostExecute(String success) {
        	pDialog.dismiss();
        	if(success.equals("1")){
        		Toast.makeText(getApplicationContext(), "Profile edited successfully!", Toast.LENGTH_LONG).show();
        	}else{
        		Toast.makeText(getApplicationContext(), "Profile edit failed!", Toast.LENGTH_LONG).show();
        	}
        }
    }
    public void showSettings(){
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				
    	String encrypt_zip_files = prefs.getString("encrypt_zip_files",null);
    	if(encrypt_zip_files.equals("1")){
    		checkEncrypt.setChecked(true);
    	}
    	
    	String delete_after_sync = prefs.getString("delete_after_sync",null);
    	if(delete_after_sync.equals("1")){
    		checkDeleteSync.setChecked(true);
    	}
    	
    	String delete_after_export = prefs.getString("delete_after_export",null);
    	if(delete_after_export.equals("1")){
    		checkDeleteExport.setChecked(true);
    	}
    	
    	String username = prefs.getString("username",null);
    	eTUsername.setText(username);
    	old_username = username;
    	
    	String password = prefs.getString("password",null);
    	eTPassword.setText(password);
    	old_password = password;
  	
    	String maximum_video_length = prefs.getString("maximum_video_length",null);
    	
    	String maxL = maximum_video_length;
        int imaxL = Integer.parseInt(maxL);
        imaxL = (int)(imaxL/60);
        
    	eTVid.setText(imaxL);
    	
    }
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btnLogin:
        	dialog.dismiss();
        	EditText cE = (EditText)dialog.findViewById(R.id.code);
        	String code = cE.getText().toString(); 
        	
        	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        	String unlockCode = prefs.getString("password", DefaultsActivity.password);
        	
        	if(!code.equals(unlockCode)){
        		 Toast.makeText(getApplicationContext(), "incorrect password", Toast.LENGTH_LONG).show();
        		 finish();
        	}else{
        		 showSettings();
        	}
            break;

        }
    }
}
