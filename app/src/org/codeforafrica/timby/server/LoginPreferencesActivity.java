package org.codeforafrica.timby.server;


import java.util.Iterator;

import org.codeforafrica.timby.BaseActivity;
import org.codeforafrica.timby.HomeActivity;
import org.codeforafrica.timby.LessonsActivity;
import org.codeforafrica.timby.R;
import org.codeforafrica.timby.ReportActivity;
import org.codeforafrica.timby.ReportsActivity;
import org.codeforafrica.timby.login.UserFunctions;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


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
	            
	            if((username.equals(txtUser.getText().toString()))&&(password.equals(txtPass.getText().toString()))){
	            	
	            	mHandler.sendEmptyMessage(0);
				}else{   
					Message msgErr= mHandler.obtainMessage(1);
                    msgErr.getData().putString("err","Incorrect username and/or password!");
                    mHandler.sendMessage(msgErr);
				}
	            
	        }else{
		        
	            String username = txtUser.getText().toString();
	            String password = txtPass.getText().toString();
	            UserFunctions userFunction = new UserFunctions();
				
				JSONObject json = userFunction.loginUser(username, password);
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
	    }
        
    	Intent intent = new Intent(LoginPreferencesActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        
        finish();
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
			JSONArray json = userFunction.getSectors(token, user_id);
			Log.d("length", String.valueOf(json.length()));
			
			String str = json.toString();
			str = str.replace("[[", "{\"items\": [");
			str = str.replace("]]", "]}");
			
			JSONObject json2 = null;
			try {
				json2 = new JSONObject(str);
			}catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Log.d("length2", String.valueOf(json2.length()));
			
			
			JSONArray jArrayObject = null;
			try {
				jArrayObject = json2.getJSONArray("items");
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
 			JSONArray json = userFunction.getCategories(token, user_id);
 			Log.d("length", String.valueOf(json.length()));
 			
 			String str = json.toString();
 			str = str.replace("[[", "{\"items\": [");
 			str = str.replace("]]", "]}");
 			
 			JSONObject json2 = null;
 			try {
 				json2 = new JSONObject(str);
 			}catch (JSONException e) {
 				// TODO Auto-generated catch block
 				e.printStackTrace();
 			}
 			//Log.d("length2", String.valueOf(json2.length()));
 			
 			
 			JSONArray jArrayObject = null;
 			try {
 				jArrayObject = json2.getJSONArray("items");
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

        @Override
        protected void onPreExecute() {
            super.onPreExecute(); 
        }
        protected String doInBackground(String... args) {
        	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	        String token = settings.getString("token",null);
	        String user_id = settings.getString("user_id",null);
	        
        	UserFunctions userFunction = new UserFunctions();
			JSONArray json = userFunction.getEntities(token, user_id);
			Log.d("length", String.valueOf(json.length()));
			
			String str = json.toString();
			str = str.replace("[[", "{\"items\": [");
			str = str.replace("]]", "]}");
			
			JSONObject json2 = null;
			try {
				json2 = new JSONObject(str);
			}catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Log.d("length2", String.valueOf(json2.length()));
			
			
			JSONArray jArrayObject = null;
			try {
				jArrayObject = json2.getJSONArray("items");
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
					sectors.put(json_data.get("entity"));
				}
				
				Editor editor = prefs.edit();
				editor.putString("entities", sectors.toString());
				
				editor.commit();
							
				}catch(JSONException e){
					e.printStackTrace();
				}
        	return null;
        }
        protected void onPostExecute(String file_url) {
            
        }
	}
}