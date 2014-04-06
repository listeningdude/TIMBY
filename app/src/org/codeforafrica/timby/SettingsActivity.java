package org.codeforafrica.timby;

import org.holoeverywhere.widget.Toast;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SettingsActivity extends Activity implements OnClickListener{
    private Dialog dialog;
    EditText eTHA;
    EditText eTAPI;
    EditText eTEncryptionKey;
    EditText eTVid;
    EditText eTUnlockCode;
    CheckBox checkDeleteExport;
    CheckBox checkDeleteSync;
    CheckBox checkEncrypt;
    Button btnSave;
    
    EditText eTAPIKey;
    EditText eTUsername;
    EditText eTPassword;
    
    public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_settings);
    	checkEncrypt = (CheckBox)findViewById(R.id.checkEncrypt);
    	checkDeleteSync = (CheckBox)findViewById(R.id.checkDeleteSync);
    	checkDeleteExport = (CheckBox)findViewById(R.id.checkDeleteExport);
    	btnSave = (Button)findViewById(R.id.btnSave);
    	
    	eTVid = (EditText)findViewById(R.id.eTVid);
    	eTEncryptionKey = (EditText)findViewById(R.id.eTEncryptionKey);
    	eTAPI = (EditText)findViewById(R.id.eTAPI);
    	eTHA = (EditText)findViewById(R.id.eTHA);
    	eTUnlockCode = (EditText)findViewById(R.id.eTUnlockCode);
    	
    	eTAPIKey = (EditText)findViewById(R.id.eTAPIKey);
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
    	
	    dialog.setContentView(R.layout.dialog_settings_login);
	    dialog.findViewById(R.id.btnLogin).setOnClickListener(SettingsActivity.this);
	    dialog.setTitle("Unlock settings screen");
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
         edit.putString("unlock_code", eTUnlockCode.getText().toString());    	
         edit.putString("username", eTUsername.getText().toString());
         edit.putString("password", eTPassword.getText().toString());
         edit.putString("api_key", eTAPIKey.getText().toString());
         
         String maxL = eTVid.getText().toString();
         int imaxL = Integer.parseInt(maxL);
         imaxL = (int)(imaxL*60);
         
         edit.putString("maximum_video_length", String.valueOf(imaxL));
         
         
         edit.putString("encryption_key", eTEncryptionKey.getText().toString());
         edit.putString("api_base_url", eTAPI.getText().toString());
         edit.putString("hockey_app_id", eTHA.getText().toString());
         edit.commit();       
    	Toast.makeText(getApplicationContext(), "Settings have been saved!", Toast.LENGTH_LONG).show();
		finish();
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
    	
    	String password = prefs.getString("password",null);
    	eTPassword.setText(password);
    	
    	String api_key = prefs.getString("api_key",null);
    	eTAPIKey.setText(api_key);
    	
    	String maximum_video_length = prefs.getString("maximum_video_length",null);
    	
    	String maxL = maximum_video_length;
        int imaxL = Integer.parseInt(maxL);
        imaxL = (int)(imaxL/60);
        
    	eTVid.setText(imaxL);
    	
    	String encryption_key = prefs.getString("encryption_key",null);
    	eTEncryptionKey.setText(encryption_key);
    	
    	String api_base_url = prefs.getString("api_base_url",null);
    	eTAPI.setText(api_base_url);
    	
    	String hockey_app_id = prefs.getString("hockey_app_id",null);
    	eTHA.setText(hockey_app_id);
    	
    	String unlock_code = prefs.getString("unlock_code",null);
    	eTUnlockCode.setText(unlock_code);
    	
    }
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btnLogin:
        	dialog.dismiss();
        	EditText cE = (EditText)dialog.findViewById(R.id.code);
        	String code = cE.getText().toString(); 
        	
        	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        	String unlockCode = prefs.getString("unlock_code", DefaultsActivity.unlock_code);
        	
        	if(!code.equals(unlockCode)){
        		 Toast.makeText(getApplicationContext(), "incorrect code", Toast.LENGTH_LONG).show();
        		 finish();
        	}else{
        		 showSettings();
        	}
            break;

        }
    }
}
