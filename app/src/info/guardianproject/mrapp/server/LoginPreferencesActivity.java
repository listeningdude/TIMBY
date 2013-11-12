package info.guardianproject.mrapp.server;


import info.guardianproject.mrapp.BaseActivity;
import info.guardianproject.mrapp.HomeActivity;
import info.guardianproject.mrapp.LessonsActivity;
import info.guardianproject.mrapp.R;
import info.guardianproject.mrapp.login.UserFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
    	private static String KEY_SUCCESS = "status";
    	private static String KEY_ERROR_MSG = "message";

    	private static String KEY_USER_ID = "user_id";
    	private static String KEY_TOKEN = "token";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setting default screen to login.xml
        setContentView(R.layout.activity_login);
        
        viewLogo = (ImageView)findViewById(R.id.logo);
        txtStatus = (TextView)findViewById(R.id.login_error);
        txtUser = (EditText)findViewById(R.id.loginUsername);
        txtPass = (EditText)findViewById(R.id.loginPassword);
        
        getCreds();
        
        getSupportActionBar().hide();
        
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
            txtStatus.setText("Connecting to server...");
            
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
        
         if (password != null)
                txtPass.setText(password);
        
    }
    
    public void run ()
    {
            String username = txtUser.getText().toString();
            String password = txtPass.getText().toString();
            UserFunctions userFunction = new UserFunctions();
			
			JSONObject json = userFunction.loginUser(username, password);

			// check for login response
			try {
				if (json.getString(KEY_SUCCESS)!=null) {
					String res = json.getString(KEY_SUCCESS); 
					if(res.equals("OK")){
						// user successfully logged in
						// Store user details in SQLite Database
						// DatabaseHandler db = new DatabaseHandler(getApplicationContext());
						JSONObject json_user = json.getJSONObject("message");
						
						// Clear all previous data in database
						//userFunction.logoutUser(getApplicationContext());
						//db.addUser(json_user.getString(KEY_USER_ID), json_user.getString(KEY_TOKEN));						
						
						saveCreds(json_user.getString(KEY_USER_ID), json_user.getString(KEY_TOKEN), username, password);
						//Toast.makeText(getBaseContext(), "Login Successfull!", Toast.LENGTH_LONG).show();
						
						// Launch Dashboard Screen
						Intent dashboard = new Intent(getApplicationContext(), HomeActivity.class);
						
						// Close all views before launching Dashboard
						dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(dashboard);
						
						// Close Login Screen
						finish();
					}else{
						// Error in login
						txtStatus.setText(json.getString(KEY_ERROR_MSG));
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
            //for now just save to keep it simple
            /*saveCreds(username, password);
            
            try {
                        StoryMakerApp.getServerManager().connect(username, password);

                        mHandler.sendEmptyMessage(0);
                 
                } catch (Exception e) {
                        
                        Message msgErr= mHandler.obtainMessage(1);
                        msgErr.getData().putString("err",e.getLocalizedMessage());
                        mHandler.sendMessage(msgErr);
                        Log.e(AppConstants.TAG,"login err",e);
                }*/
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
            //Toast.makeText(this, "Login failed: " + err, Toast.LENGTH_LONG).show();
    }
    
    private void loginSuccess ()
    {
            finish();
    }
}