package org.codeforafrica.timby.server;



import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.codeforafrica.timby.*;
import org.codeforafrica.timby.login.*;
import org.holoeverywhere.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
 
public class LoginActivity extends Activity{
	
	Button btnLogin;
	Button btnLinkToRegister;
	EditText inputUsername;
	EditText inputPassword;
	TextView loginErrorMsg;
	Button btnLessons;

	// JSON Response node names
	private static String KEY_SUCCESS = "status";
	private static String KEY_ERROR = "error";
	private static String KEY_ERROR_MSG = "message";

	private static String KEY_USER_ID = "user_id";
	private static String KEY_TOKEN = "token";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	    StrictMode.setThreadPolicy(policy);
	    
		setContentView(R.layout.activity_login);

		// Importing all assets like buttons, text fields
		inputUsername = (EditText) findViewById(R.id.loginUsername);
		inputPassword = (EditText) findViewById(R.id.loginPassword);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
		loginErrorMsg = (TextView) findViewById(R.id.login_error);
		btnLessons = (Button)findViewById(R.id.link_to_skip);
		// Login button Click Event
		btnLogin.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				String username = inputUsername.getText().toString();
				String password = inputPassword.getText().toString();
				//check if password and username filled
				if((username.equals(""))||(password.equals(""))){
					Toast.makeText(getBaseContext(), "You need to provide both username and password!", Toast.LENGTH_LONG).show();
				}else{
					UserFunctions userFunction = new UserFunctions();
					Log.d("Button", "Login");
					JSONObject json = userFunction.loginUser(username, password);
	
					// check for login response
					try {
						if (json.getString(KEY_SUCCESS)!=null) {
							String res = json.getString(KEY_SUCCESS); 
							if(res.equals("OK")){
								// user successfully logged in
								// Store user details in SQLite Database
								DatabaseHandler db = new DatabaseHandler(getApplicationContext());
								JSONObject json_user = json.getJSONObject("message");
								
								// Clear all previous data in database
								userFunction.logoutUser(getApplicationContext());
								db.addUser(json_user.getString(KEY_USER_ID), json_user.getString(KEY_TOKEN));						
								
								Toast.makeText(getBaseContext(), "Login Successfull!", Toast.LENGTH_LONG).show();
								
								// Launch Dashboard Screen
								Intent dashboard = new Intent(getApplicationContext(), HomeActivity.class);
								
								// Close all views before launching Dashboard
								dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(dashboard);
								
								// Close Login Screen
								finish();
							}else{
								// Error in login
								loginErrorMsg.setText(json.getString(KEY_ERROR_MSG));
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
		//Link to Lessions
		btnLessons.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent lessons = new Intent(getApplicationContext(), LessonsActivity.class);
				startActivity(lessons);
				
			}
		});
		
		// Link to Register Screen
		btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(),
						RegisterActivity.class);
				startActivity(i);
				finish();
			}
		});
	}
}
