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
    CheckBox checkDeleteExport;
    CheckBox checkDeleteSync;
    CheckBox checkEncrypt;
    Button btnSave;
    
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

	    loginDialog();
	    
	    btnSave.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
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
    	
    	String maximum_video_length = prefs.getString("maximum_video_length",null);
    	eTVid.setText(maximum_video_length);
    	
    	String encryption_key = prefs.getString("encryption_key",null);
    	eTEncryptionKey.setText(encryption_key);
    	
    	String api_base_url = prefs.getString("api_base_url",null);
    	eTAPI.setText(api_base_url);
    	
    	String hockey_app_id = prefs.getString("hockey_app_id",null);
    	eTHA.setText(hockey_app_id);
    	
    }
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btnLogin:
        	dialog.dismiss();
        	EditText cE = (EditText)dialog.findViewById(R.id.code);
        	String code = cE.getText().toString(); 
        	if(!code.equals("5565")){
        		 Toast.makeText(getApplicationContext(), "incorrect code", Toast.LENGTH_LONG).show();
        		 finish();
        	}else{
        		 
        		 showSettings();
        	}
            break;

        }
    }
}
