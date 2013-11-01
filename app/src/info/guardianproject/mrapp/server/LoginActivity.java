package info.guardianproject.mrapp.server;


import info.guardianproject.mrapp.*;
import info.guardianproject.mrapp.R;
import info.guardianproject.mrapp.login.*;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
	private static String KEY_SUCCESS = "success";
	private static String KEY_ERROR = "error";
	private static String KEY_ERROR_MSG = "error_msg";
	private static String KEY_UID = "uid";
	private static String KEY_USERNAME = "username";
	private static String KEY_CREATED_AT = "created_at";

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
				UserFunctions userFunction = new UserFunctions();
				Log.d("Button", "Login");
				JSONObject json = userFunction.loginUser(username, password);

				// check for login response
				try {
					if (json.getString(KEY_SUCCESS) != null) {
						loginErrorMsg.setText("");
						String res = json.getString(KEY_SUCCESS); 
						if(Integer.parseInt(res) == 1){
							// user successfully logged in
							// Store user details in SQLite Database
							DatabaseHandler db = new DatabaseHandler(getApplicationContext());
							JSONObject json_user = json.getJSONObject("user");
							
							// Clear all previous data in database
							userFunction.logoutUser(getApplicationContext());
							db.addUser(json_user.getString(KEY_USERNAME), json.getString(KEY_UID), json_user.getString(KEY_CREATED_AT));						
							
							// Launch Dashboard Screen
							Intent dashboard = new Intent(getApplicationContext(), HomeActivity.class);
							
							// Close all views before launching Dashboard
							dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(dashboard);
							
							// Close Login Screen
							finish();
						}else{
							// Error in login
							loginErrorMsg.setText("Incorrect username/password");
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
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
