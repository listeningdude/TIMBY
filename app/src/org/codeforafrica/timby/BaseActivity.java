package org.codeforafrica.timby;
import org.codeforafrica.timby.R;
import org.codeforafrica.timby.login.UserFunctions;
import org.codeforafrica.timby.server.LoginActivity;
import org.codeforafrica.timby.server.LoginPreferencesActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;
import org.holoeverywhere.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.view.KeyEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.view.WindowManager;
import android.widget.ImageView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
//import com.google.analytics.tracking.android.EasyTracker;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.SlidingMenu.OnClosedListener;

public class BaseActivity extends Activity {

	public SlidingMenu mSlidingMenu;
	private static String KEY_SUCCESS = "status";
	public static final long DISCONNECT_TIMEOUT = 900000; // 5 min = 5 * 60 * 1000 ms

    private Handler disconnectHandler = new Handler(){
        public void handleMessage(Message msg) {
        }
    };

    private Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {
            // Perform any required operation on disconnect
        	//SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        	
        	//settings.edit().clear();
            //settings.edit().commit();	
        	
            Intent i = new Intent(getBaseContext(), LoginPreferencesActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();;
        }
    };

    public void resetDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }

    public void stopDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    @Override
    public void onUserInteraction(){
        resetDisconnectTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        resetDisconnectTimer();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopDisconnectTimer();
    }

    public void initSlidingMenu ()
    {

        mSlidingMenu = new SlidingMenu(this);
        mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        mSlidingMenu.setShadowDrawable(R.drawable.shadow);
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        
        mSlidingMenu.setBehindWidthRes(R.dimen.slidingmenu_offset);
        mSlidingMenu.setFadeDegree(0.35f);
        mSlidingMenu.setMenu(R.layout.fragment_drawer);

		mSlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		
        mSlidingMenu.setOnClosedListener(new OnClosedListener() {

            @Override
            public void onClosed() {
                mSlidingMenu.requestLayout();

            }
        });
        
        final Activity activity = this;
        
        ImageButton btnDrawerQuickCaptureVideo = (ImageButton) findViewById(R.id.btnDrawerQuickCaptureVideo);
        ImageButton btnDrawerQuickCapturePhoto = (ImageButton) findViewById(R.id.btnDrawerQuickCapturePhoto);
        ImageButton btnDrawerQuickCaptureAudio = (ImageButton) findViewById(R.id.btnDrawerQuickCaptureAudio);
        
        Button btnDrawerHome = (Button) findViewById(R.id.btnDrawerHome);
        Button btnDrawerProjects = (Button) findViewById(R.id.btnDrawerProjects);
        Button btnDrawerLessons = (Button) findViewById(R.id.btnDrawerLessons);
        Button btnDrawerAccount = (Button) findViewById(R.id.btnDrawerAccount);
        Button btnDrawerSettings = (Button) findViewById(R.id.btnDrawerSettings);
        Button btnExport = (Button) findViewById(R.id.btnExport);

        btnDrawerQuickCaptureVideo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	
            	String dateNowStr = new Date().toLocaleString();
                
            	Intent intent = new Intent(BaseActivity.this, StoryNewActivity.class);
            	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            	intent.putExtra("story_name", "Quick Story " + dateNowStr);
            	intent.putExtra("story_type", 0);
            	intent.putExtra("auto_capture", true);
                
                 activity.startActivity(intent);      	
                 Toast.makeText(BaseActivity.this, "Coming soon", Toast.LENGTH_SHORT).show();
                 }
            	
        });
        
        btnDrawerQuickCapturePhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	
            	String dateNowStr = new Date().toLocaleString();
                
            	Intent intent = new Intent(BaseActivity.this, StoryNewActivity.class);
            	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            	intent.putExtra("story_name", "Quick Story " + dateNowStr);
            	intent.putExtra("story_type", 2);
            	intent.putExtra("auto_capture", true);
                
                 activity.startActivity(intent);  
                 }
    		

        });
        
        btnDrawerQuickCaptureAudio.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	
            	String dateNowStr = new Date().toLocaleString();
                
            	Intent intent = new Intent(BaseActivity.this, StoryNewActivity.class);
            	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            	intent.putExtra("story_name", "Quick Story " + dateNowStr);
            	intent.putExtra("story_type", 1);
            	intent.putExtra("auto_capture", true);
                
                 activity.startActivity(intent);  
                 }
        });
        
        btnDrawerHome.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	
            	mSlidingMenu.showContent(true);
                
            	 Intent i = new Intent(activity, HomeActivity.class);
                 activity.startActivity(i);
            }
        });
        btnDrawerProjects.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            	mSlidingMenu.showContent(true);
            	  Intent i = new Intent(activity, ReportsActivity.class);
                  activity.startActivity(i);
            }
        });
        btnDrawerLessons.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            	mSlidingMenu.showContent(true);
            	
                Intent i = new Intent(activity, LessonsActivity.class);
                activity.startActivity(i);
            }
        });
        
        btnDrawerAccount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	// handleLogin ();
            	//SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        	  	//settings.edit().clear();
                //settings.edit().commit();	
                Intent i = new Intent(getBaseContext(), LoginPreferencesActivity.class);
                startActivity(i);
                finish();
            }
        });
         
        btnDrawerSettings.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	mSlidingMenu.showContent(true);

                Intent i = new Intent(activity, SimplePreferences.class);
                activity.startActivity(i);
            }
        });
        btnExport.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	//mSlidingMenu.showContent(true);
            	
                Intent i = new Intent(activity, Export2SD.class);
                activity.startActivity(i);
                mSlidingMenu.showContent(false);
            }
        });
        
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_actionbar_menu, menu);
        
        return true;
    }
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.home)
        {
        	Intent intent = new Intent(getBaseContext(), HomeActivity.class);
	        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        startActivity(intent);
        }
        /*if (item.getItemId() == R.id.settings)
        {	
        	   		
        	Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
	        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        startActivity(intent);
        }*/
        if (item.getItemId() == R.id.logout)
        {
        	//nullify user
        	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    		Editor editor = prefs.edit();
    		editor.putString("logged_in", "0");
        	editor.commit();
        	
        	Intent intent = new Intent(getBaseContext(), LoginPreferencesActivity.class);
	        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        startActivity(intent);
        }
       
		return true;
	}
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    
    	super.onCreate(savedInstanceState);
        
        //(new Eula(this)).show();
    }
    
    @Override
	public void onPostCreate(Bundle savedInstanceState) {
		
		super.onPostCreate(savedInstanceState);
	

        initSlidingMenu();
	}

    public void run ()
    {
    	try {
        	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        	String username = settings.getString("username",null);
            String password = settings.getString("password",null);
            //logout remotely
            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.logoutUser(username, password, getApplicationContext());
            if ((json.getString(KEY_SUCCESS)!=null)&&(json.getString(KEY_SUCCESS).equals("OK"))){
            	//logout locally 
            	settings.edit().clear();
                settings.edit().commit();	
            	//load login
            	mSlidingMenu.showContent(true);
                Intent i = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(i);
                finish();
            }else{
            	Toast.makeText(getBaseContext(), "Could not log you out. Try again!", Toast.LENGTH_LONG).show();
            }
            
		} catch (JSONException e) {
			e.printStackTrace();
		}
    }

	private void detectCoachOverlay ()
    {
        try {
        	
        	if (this.getClass().getName().contains("SceneEditorActivity"))
        	{
        		showCoachOverlay("images/coach/coach_add.png");
        	}
        	else if (this.getClass().getName().contains("OverlayCameraActivity"))
        	{
        		showCoachOverlay("images/coach/coach_camera_prep.png");
        	}
        		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     * 
	public void switchContent(final Fragment fragment) {
		mContent = fragment;
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.content_frame, fragment)
		.commit();
		Handler h = new Handler();
		h.postDelayed(new Runnable() {
			public void run() {
				getSlidingMenu().showContent();
			}
		}, 50);
	}	
**/
    
    private void showCoachOverlay (String path) throws IOException
    {
    	ImageView overlayView = new ImageView(this);
    	
    	overlayView.setOnClickListener(new OnClickListener () 
    	{

			@Override
			public void onClick(View v) {
				getWindowManager().removeView(v);
				
			}
    		
    	});
    	
    	AssetManager mngr = getAssets();
        // Create an input stream to read from the asset folder
           InputStream ins = mngr.open(path);

           // Convert the input stream into a bitmap
           Bitmap bmpCoach = BitmapFactory.decodeStream(ins);
           overlayView.setImageBitmap(bmpCoach);
           
           WindowManager.LayoutParams params = new WindowManager.LayoutParams(
	        WindowManager.LayoutParams.MATCH_PARENT,
	        WindowManager.LayoutParams.MATCH_PARENT,
	        WindowManager.LayoutParams.TYPE_APPLICATION,
	        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
	        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
    	        PixelFormat.TRANSLUCENT);

    	getWindowManager().addView(overlayView, params);
    }
}
