package org.codeforafrica.timby.server;


import java.util.Arrays;
import java.util.Iterator;

import org.codeforafrica.timby.BaseActivity;
import org.codeforafrica.timby.DefaultsActivity;
import org.codeforafrica.timby.HomeActivity;
import org.codeforafrica.timby.LessonsActivity;
import org.codeforafrica.timby.R;
import org.codeforafrica.timby.ReportActivity;
import org.codeforafrica.timby.ReportsActivity;
import org.codeforafrica.timby.SettingsActivity;
import org.codeforafrica.timby.login.UserFunctions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
 
public class LoginPreferencesActivity extends BaseActivity implements Runnable 
{
        
        private ImageView viewLogo;
        private TextView txtStatus;
        private EditText txtUser;
        private EditText txtPass;
        private CheckBox showPassword;
    	private static String KEY_SUCCESS = "status";
    	private static String KEY_ERROR_MSG = "message";

    	private static String KEY_USER_ID = "user_id";
    	private static String KEY_TOKEN = "token";
    	
    	// Connection detector class
        ConnectionDetector cd;
        //flag for Internet connection status
        Boolean isInternetPresent = false;
        
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setting default screen to login.xml
        setContentView(R.layout.activity_login);
        
        viewLogo = (ImageView)findViewById(R.id.logo);
        txtStatus = (TextView)findViewById(R.id.login_error);
        txtUser = (EditText)findViewById(R.id.loginUsername);
        txtPass = (EditText)findViewById(R.id.loginPassword);
        showPassword = (CheckBox)findViewById(R.id.showPassword);
        getCreds();
        
        getSupportActionBar().hide();
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#9E3B33")));
               
        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new OnClickListener ()
        {

                        @Override
                        public void onClick(View v) {
                        	String username = txtUser.getText().toString();
            				String password = txtPass.getText().toString();
                        	if((username.equals(""))||(password.equals(""))){
            					Toast.makeText(getBaseContext(), "You need to provide both username and password!", Toast.LENGTH_LONG).show();
            				}else{   
                                handleLogin ();
            				}
                                
                        }
                
        });
        showPassword.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(showPassword.isChecked()){
					txtPass.setInputType(InputType.TYPE_CLASS_TEXT);
		        }else{
		        	txtPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		        }
			}
		});
               
      //Link to Lessions
       Button btnLessons = (Button)findViewById(R.id.link_to_skip);
      		btnLessons.setOnClickListener(new View.OnClickListener() {
      			
      			@Override
      			public void onClick(View v) {
      				// TODO Auto-generated method stub
      				Intent lessons = new Intent(getApplicationContext(), LessonsActivity.class);
      				startActivity(lessons);
      				
      			}
      		});
    }

    private void handleLogin ()
    {
            txtStatus.setText("Attempting login...");
            
            new Thread(this).start();
    }
    
    private void saveCreds (String user_id, String token, String username, String password)
    { 
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Editor edit = settings.edit(); 
        
        
        edit.putString("user_id", user_id);
        edit.putString("token", token);
        edit.putString("username", username);
        edit.putString("password", password);
        edit.commit();
    }
    
    private void getCreds ()
    { 
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
       
        String username = settings.getString("username",null);
        String password = settings.getString("password",null);
        if (username != null)
                txtUser.setText(username);
        
         //if (password != null)
               // txtPass.setText(password);
        checkSettings();
    }
    
    public void checkSettings(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		Editor editor = prefs.edit();

    	String encrypt_zip_files = prefs.getString("encrypt_zip_files",null);
    	if(encrypt_zip_files==null){
    		editor.putString("encrypt_zip_files", DefaultsActivity.encrypt_zip_files);
    	}
    	
    	String delete_after_sync = prefs.getString("delete_after_sync",null);
    	if(delete_after_sync==null){
    		editor.putString("delete_after_sync", DefaultsActivity.delete_after_sync);
    	}
    	
    	String delete_after_export = prefs.getString("delete_after_export",null);
    	if(delete_after_export==null){
    		editor.putString("delete_after_export", DefaultsActivity.delete_after_export);
    	}
    	
    	String maximum_video_length = prefs.getString("maximum_video_length",null);
    	if(maximum_video_length==null){
    		editor.putString("maximum_video_length", DefaultsActivity.maximum_video_length);
    	}
    	
    	String encryption_key = prefs.getString("encryption_key",null);
    	if(encryption_key==null){
    		editor.putString("encryption_key", DefaultsActivity.encryption_key);
    	}
    	
    	String api_base_url = prefs.getString("api_base_url",null);
    	if(api_base_url==null){
    		editor.putString("api_base_url", DefaultsActivity.api_base_url);
    	}
    	
    	String hockey_app_id = prefs.getString("hockey_app_id",null);
    	if(hockey_app_id==null){
    		editor.putString("hockey_app_id", DefaultsActivity.hockey_app_id);
    	}
    	String username = prefs.getString("username",null);
    	if(username==null){
    		editor.putString("username", DefaultsActivity.username);
    	}
    	
    	String password = prefs.getString("password",null);
    	if(password==null){
    		editor.putString("password", DefaultsActivity.password);
    	}
    	
    	String api_key = prefs.getString("api_key",null);
    	if(api_key==null){
    		editor.putString("api_key", DefaultsActivity.api_key);
    	}
    	
    	String unlock_code = prefs.getString("unlock_code",null);
    	if(unlock_code==null){
    		editor.putString("unlock_code", DefaultsActivity.unlock_code);
    	}
		editor.commit();
    }
    public static <T> boolean contains( final T[] array, final T v ) {
        for ( final T e : array )
            if ( e == v || v != null && v.equals( e ) )
                return true;

        return false;
    }
    public void run ()
    {
	    	// creating connection detector class instance
	        cd = new ConnectionDetector(getApplicationContext());
	        
			//get Internet status
	        isInternetPresent = cd.isConnectingToInternet();
	        
	        if(!isInternetPresent){
	        	//check for details on preferences
	        	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	            
	            String username = settings.getString("username",null);
	            String password = settings.getString("password",null);
	            
	            if((username!=null)&&(password!=null)){
	            	 if((username.equals(txtUser.getText().toString()))&&(password.equals(txtPass.getText().toString()))){
		            	mHandler.sendEmptyMessage(0);
					}else{   
						Message msgErr= mHandler.obtainMessage(1);
	                    msgErr.getData().putString("err","Incorrect username and/or password!");
	                    mHandler.sendMessage(msgErr);
					}
	            }else{
	            	//Username / Password not set
	            	Toast.makeText(getApplicationContext(), "Username and/or password not set!", Toast.LENGTH_LONG).show();
	            }
	            
	        }else{
	            String username = txtUser.getText().toString();
	            String password = txtPass.getText().toString();
	            UserFunctions userFunction = new UserFunctions();
	            //find index of user
            		JSONObject json = userFunction.loginUser(username, password, getApplicationContext());
					try {
							String res = json.getString(KEY_SUCCESS); 
							if(res.equals("OK")){
								JSONObject json_user = json.getJSONObject("message");
								saveCreds(json_user.getString(KEY_USER_ID), json_user.getString(KEY_TOKEN), username, password);
								mHandler.sendEmptyMessage(0);
							}else{
								Message msgErr= mHandler.obtainMessage(1);
		                        msgErr.getData().putString("err",json.getString(KEY_ERROR_MSG));
		                        mHandler.sendMessage(msgErr);
							}
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
            	
	        }
           
    }

    private Handler mHandler = new Handler ()
    {

                @Override
                public void handleMessage(Message msg) {
                        
                        switch (msg.what)
                        {
                                case 0:
                                        loginSuccess();
                                        break;
                                case 1:
                                        loginFailed(msg.getData().getString("err"));
                                        
                                        
                                default:
                        }
                }
            
    };
    
    private void loginFailed (String err)
    {
            txtStatus.setText(err);
            
    }
    
    private void loginSuccess ()
    {
    	// creating connection detector class instance
        cd = new ConnectionDetector(getApplicationContext());
        
		//get Internet status
        isInternetPresent = cd.isConnectingToInternet();
        
        if(isInternetPresent){
	    	new getSectors().execute();
	    	new getCategories().execute();
	    	new getEntities().execute();
	    }else{
	    	getPresetSectors();
	    	getPresetCategories();
	    	getPresetEntities();
	    }
        
        //Save logged in status
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		Editor editor = prefs.edit();
		editor.putString("logged_in", "1");
    	editor.commit();
    	
        
    	Intent intent = new Intent(LoginPreferencesActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        
        finish();
    }
    public void getPresetSectors(){
    	//First check if not done already
    	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	String sectors_current = settings.getString("sectors",null);
    	if(sectors_current==null){
	    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	    	    	
			JSONArray sectors = new JSONArray();
			
	    	String[] sectors_string= getResources().getStringArray(R.array.sectors);
	    	for(int n=0; n<sectors_string.length;n++){
	    			String str2 = sectors_string[n];
					sectors.put(str2);
	    	}
	    	Editor editor = prefs.edit();
			editor.putString("sectors", sectors.toString());
			
			editor.commit();
    	}
    }
    public void getPresetCategories(){
    	//First check if not done already
    	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	String categories_current = settings.getString("categories",null);
    	if(categories_current==null){
	    	SharedPreferences prefs = PreferenceManager
			        .getDefaultSharedPreferences(getApplicationContext());
	    	    	
			JSONArray sectors = new JSONArray();
			
	    	String[] sectors_string= getResources().getStringArray(R.array.categories);
	    	for(int n=0; n<sectors_string.length;n++){
	    			String str2 = sectors_string[n];
					sectors.put(str2);
	    	}
	    	Editor editor = prefs.edit();
			editor.putString("categories", sectors.toString());
			
			editor.commit();
    	}
    }
    public void getPresetEntities(){
    	//First check if not done already
    	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	String categories_current = settings.getString("entities",null);
    	if(categories_current==null){
	    	SharedPreferences prefs = PreferenceManager
			        .getDefaultSharedPreferences(getApplicationContext());
	    	    	
			JSONArray sectors = new JSONArray();
			
	    	String[] sectors_string= getResources().getStringArray(R.array.entities);
	    	for(int n=0; n<sectors_string.length;n++){
	    			String str2 = sectors_string[n];
					sectors.put(str2);
	    	}
	    	Editor editor = prefs.edit();
			editor.putString("entities", sectors.toString());
			
			editor.commit();
    	}
    }
    class getSectors extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute(); 
        }
        protected String doInBackground(String... args) {
        	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	        String token = settings.getString("token",null);
	        String user_id = settings.getString("user_id",null);
	        
        	UserFunctions userFunction = new UserFunctions();
			JSONObject json = userFunction.getSectors(token, user_id, getApplicationContext());
			Log.d("length", String.valueOf(json.length()));
			
			JSONObject sJson  = null;
			try {
				sJson = json.getJSONObject("message");
				
			} catch (JSONException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			JSONArray jArrayObject = null;
			try {
				jArrayObject = sJson.getJSONArray("sectors");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Log.d("length3", String.valueOf(jArrayObject.length()));
			
			
			try {
				
				SharedPreferences prefs = PreferenceManager
				        .getDefaultSharedPreferences(getApplicationContext());
				JSONArray sectors = new JSONArray();
			
				for(int i=0;i<jArrayObject.length();i++){
					String str2 = jArrayObject.getString(i);
					Log.d("item", str2);
					JSONObject json_data = new JSONObject(str2);
					sectors.put(json_data.get("sector"));
				}
				
				Editor editor = prefs.edit();
				editor.putString("sectors", sectors.toString());
				
				editor.commit();
							
				}catch(JSONException e){
					e.printStackTrace();
				}
        	return null;
        }
        protected void onPostExecute(String file_url) {
            
        }
	}
    class getCategories extends AsyncTask<String, String, String> {
    	 @Override
         protected void onPreExecute() {
             super.onPreExecute(); 
         }
         protected String doInBackground(String... args) {
         	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
 	        String token = settings.getString("token",null);
 	        String user_id = settings.getString("user_id",null);
 	        
         	UserFunctions userFunction = new UserFunctions();
 			JSONObject json = userFunction.getCategories(token, user_id, getApplicationContext());
 			Log.d("length", String.valueOf(json.length()));
 			JSONObject sJson  = null;
			try {
				sJson = json.getJSONObject("message");
				
			} catch (JSONException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			JSONArray jArrayObject = null;
			try {
				jArrayObject = sJson.getJSONArray("categories");
 			} catch (JSONException e1) {
 				// TODO Auto-generated catch block
 				e1.printStackTrace();
 			}
 			Log.d("length3", String.valueOf(jArrayObject.length()));
 			
 			
 			try {
 				
 				SharedPreferences prefs = PreferenceManager
 				        .getDefaultSharedPreferences(getApplicationContext());
 				JSONArray categories = new JSONArray();
 			
 				for(int i=0;i<jArrayObject.length();i++){
 					String str2 = jArrayObject.getString(i);
 					Log.d("item", str2);
 					JSONObject json_data = new JSONObject(str2);
 					categories.put(json_data.get("category"));
 				}
 				
 				Editor editor = prefs.edit();
 				editor.putString("categories", categories.toString());
 				
 				editor.commit();
 							
 				}catch(JSONException e){
 					e.printStackTrace();
 				}
         	return null;
         }
         protected void onPostExecute(String file_url) {
             
         }
	}
    class getEntities extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {
        	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	        String token = settings.getString("token",null);
	        String user_id = settings.getString("user_id",null);
	        
        	UserFunctions userFunction = new UserFunctions();
			JSONObject json = userFunction.getEntities(token, user_id, getApplicationContext());
			Log.d("length", String.valueOf(json.length()));
			
			JSONObject sJson  = null;
			try {
				sJson = json.getJSONObject("message");
				
			} catch (JSONException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			JSONArray jArrayObject = null;
			try {
				jArrayObject = sJson.getJSONArray("entities");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Log.d("length3", String.valueOf(jArrayObject.length()));
			
			
			try {
				
				SharedPreferences prefs = PreferenceManager
				        .getDefaultSharedPreferences(getApplicationContext());
				JSONArray entities = new JSONArray();
			
				for(int i=0;i<jArrayObject.length();i++){
					String str2 = jArrayObject.getString(i);
					Log.d("item", str2);
					JSONObject json_data = new JSONObject(str2);
					entities.put(json_data.get("entity"));
					
				}
				
				Editor editor = prefs.edit();
				editor.putString("entities", entities.toString());
				
				editor.commit();
							
				}catch(JSONException e){
					e.printStackTrace();
				}
        	return null;
        }
        @Override
		protected void onPreExecute() {
		    super.onPreExecute(); 
		}
		protected void onPostExecute(String file_url) {
            
        }
	}
	 @Override
	  public void onStart() {
	    super.onStart();
	    EasyTracker.getInstance(this).activityStart(this);
	  }

	  @Override
	  public void onStop() {
	    super.onStop();
	    EasyTracker.getInstance(this).activityStop(this);
	  }
}