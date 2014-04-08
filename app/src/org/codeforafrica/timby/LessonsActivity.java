package org.codeforafrica.timby;

import java.io.File;
import java.util.ArrayList;



//import org.codeforafrica.timby.VideoTutorials.VideosArrayAdapter;
import org.codeforafrica.timby.lessons.LessonListView;
import org.codeforafrica.timby.lessons.WebViewSetupJB;
import org.codeforafrica.timby.ui.MyCard;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.Toast;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.PluginState;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardUI;

public class LessonsActivity extends BaseActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * sections. We use a {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will
     * keep every loaded fragment in memory. If this becomes too memory intensive, it may be best
     * to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    LessonListView mListView;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
             
        super.onCreate(savedInstanceState);
           
        setContentView(R.layout.activity_lessons);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
    	mListView = new LessonListView(this, this);
        
        LessonSectionFragment fLessons = new LessonSectionFragment();
        fLessons.setListView(mListView);
        
        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),fLessons);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        // When swiping between different sections, select the corresponding tab.
        // We can also use ActionBar.Tab#select() to do this if we have a reference to the
        // Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
        
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_lessons, menu);
        
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

	        	boolean handled = mListView.handleBack();
	        	
	        	if (!handled)
	        		NavUtils.navigateUpFromSameTask(this);
	        	
                return true;
            case R.id.menu_update:
                updateLessons();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
   
    @Override
	protected void onActivityResult(int reqCode, int resCode, Intent intent) {
		
		if (resCode == RESULT_OK)
		{
				if (reqCode == 1)
				{
					//time to update the lesson list
					mListView.refreshList();
				}
		}
    }
    
    public ProgressDialog mProgressLoading;
    void DeleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                DeleteRecursive(child);

        fileOrDirectory.delete();
    }
    
    public void updateLessons ()
    {
    	final Dialog dialog = new Dialog(LessonsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_update_lessons);
        dialog.findViewById(R.id.button_videos).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				File tutorials = new File(Environment.getExternalStorageDirectory(), "TIMBY_Tutorials");
				DeleteRecursive(tutorials);
				
			    startService(new Intent(LessonsActivity.this,VideoTutorialsService.class));
	            Toast.makeText(getApplicationContext(), "Updating lessons...", Toast.LENGTH_LONG).show();
				dialog.dismiss();
				finish();
			}
        	
        });
        dialog.findViewById(R.id.button_lessons).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mProgressLoading = ProgressDialog.show(getApplicationContext(), getString(R.string.title_lessons),getString(R.string.downloading_lessons_from_server_),true,true);
		        
		    	StoryMakerApp.getLessonManager().updateLessonsFromRemote();
				dialog.dismiss();
			}
		});
        	
        dialog.show();
		
    }

    public void updateLessonProgress (String msg)
    {
    	if (mProgressLoading != null && mProgressLoading.isShowing())
    		mProgressLoading.setMessage(msg);
    	
    }
    
    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

    	private LessonSectionFragment fLessons;
    	
        public SectionsPagerAdapter(FragmentManager fm,LessonSectionFragment lessonFragment ) {
            super(fm);
            
            fLessons = lessonFragment;
        }

        @Override
        public Fragment getItem(int i) {
        	Fragment fragment = null;
        	
        	if(i == 0){
        		fragment = new VideosSectionFragment();
	            Bundle args = new Bundle();
	            args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, i + 1);
	            fragment.setArguments(args);
        	}
        	else if (i == 1)
        	{
        		fragment = fLessons;
 	            
        	}
        	else
        	{
	            fragment = new DummySectionFragment();
	            Bundle args = new Bundle();
	            args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, i + 1);
	            fragment.setArguments(args);
        	}
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
            	case 0: return getString(R.string.title_lessons_videos).toUpperCase();
                case 1: return getString(R.string.title_lessons_lessons).toUpperCase();
                case 2: return getString(R.string.title_lessons_glossary).toUpperCase();
            }
            return null;
        }
    }
    public static class VideosSectionFragment extends Fragment{
		
    	ListView mListView;
    	File mFileExternDir;
    	//private ArrayList<Project> mListProjects;
    	private VideosArrayAdapter vaProjects;
    	ArrayList<String> mListProjects = new ArrayList<String>();
    	
    	// Progress Dialog
    	public static final int progress_bar_type = 0;

    	// File url to download
    	@Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.activity_video_tutorials, container, false);
            mFileExternDir = new File(Environment.getExternalStorageDirectory(), "TIMBY_Tutorials");
            mListView = (ListView)view.findViewById(R.id.videoslist);
            initListView(mListView); 
            return view;
        }
    	public void initListView (ListView list) {
    		
    	    list.setOnItemClickListener(new OnItemClickListener ()
    	    {
    			@Override
    			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    				String vidPath = "/TIMBY_Tutorials/"+mListProjects.get(position);
    				
    				Intent intent = new Intent(Intent.ACTION_VIEW);
    				File sdCard = Environment.getExternalStorageDirectory();
    				File file = new File(sdCard, vidPath);
    				intent.setDataAndType(Uri.fromFile(file), "video/*");
    				startActivity(intent);
    			}
    	    	
    	    });
    	    
    	   refreshProjects();
    	    
    	}
    	public void refreshProjects ()
    	{
    		File file[] = mFileExternDir.listFiles();
    		for (int i=0; i < file.length; i++)
    		{
    			if(!file[i].getName().equals("thumbs")){
    				mListProjects.add(file[i].getName());
    			}
    		}
    	    vaProjects = new VideosArrayAdapter(getActivity().getApplicationContext(), R.layout.list_video_row, mListProjects);
    		mListView.setAdapter(vaProjects);
    	}
    	
    	
    	class VideosArrayAdapter extends ArrayAdapter {
    		
    		Context context; 
    	    int layoutResourceId;    
    	    ArrayList<String> projects;
    	    
    	    public VideosArrayAdapter(Context context, int layoutResourceId,ArrayList<String> mListProjects) {
    	        super(context, layoutResourceId, mListProjects);        
    	        
    	        this.layoutResourceId = layoutResourceId;
    	        this.context = context;
    	        this.projects = mListProjects;
    	    }
    	    
    	    @Override
    	    public View getView(int position, View convertView, ViewGroup parent) {
    	        View row = convertView;
    	        
    	        TextView tv;
    	        
    	        if(row == null)
    	        {
    	            LayoutInflater inflater = getLayoutInflater();
    	            row = inflater.inflate(layoutResourceId, parent, false);
    	        }
    	        
    	        tv = (TextView)row.findViewById(R.id.title);
    	        
    	                
    	        tv.setText("");   
    	        
    	        
    	        tv = (TextView)row.findViewById(R.id.description);
    	        
    	        ImageView ivType = (ImageView)row.findViewById(R.id.cardIcon);
    	        ImageView ivIcon = (ImageView)row.findViewById(R.id.imageView1);
    	        
    	        
    	        String[] thumbParts = projects.get(position).split("mp4");
    			String thumbName = thumbParts[0]+"jpg";
    			
    	        String imagePath = Environment.getExternalStorageDirectory().toString()+"/TIMBY_Tutorials/thumbs/"+thumbName;
    	        
    	        
    	        Bitmap bmp = BitmapFactory.decodeFile(imagePath);
    			
    	        if (bmp!= null){
    				ivIcon.setImageBitmap(bmp);
    			}
    	        String[] iPc = projects.get(position).split(".mp4");
    	        
    	        tv.setText(iPc[0]);
    	        
    	        //ivType.setImageDrawable(getContext().getResources().getDrawable(R.drawable.btn_toggle_ic_list_video));
    	    	ivType.setVisibility(View.GONE);
    	    	
    	        return row;
    	    	}
    		}
    }
    /**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     */
    public static class DummySectionFragment extends Fragment {
        public DummySectionFragment() {
        }

        public static final String ARG_SECTION_NUMBER = "section_number";

        private WebView mWebView;
        private String mUrl = "file:///android_asset/glossary/glossary.html";
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        	
        	mWebView = new WebView(getActivity());
        	mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        	mWebView.getSettings().setJavaScriptEnabled(true);
        	mWebView.getSettings().setPluginState(PluginState.ON);
        	mWebView.getSettings().setAllowFileAccess(true);
        	if (Build.VERSION.SDK_INT >= 16)
        	{
        		new WebViewSetupJB(mWebView);
        	}
        	
        	mWebView.setWebChromeClient(new WebChromeClient ()
        	{

				@Override
				public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
					
					Log.w(AppConstants.TAG,"web console: " + consoleMessage.lineNumber() + ": " + consoleMessage.message());
					return super.onConsoleMessage(consoleMessage);
				}

				
        		
        	});
        	
        	mWebView.setWebViewClient(new WebViewClient ()
        	{

        		
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {

					boolean handled = false;
					
					
					
					return handled;// super.shouldOverrideUrlLoading(view, url);
				}

				@Override
				public void onReceivedError(WebView view, int errorCode,
						String description, String failingUrl) {
					
					Log.e(AppConstants.TAG,"web error occured for " + failingUrl + "; " + errorCode + "=" + description);
					//super.onReceivedError(view, errorCode, description, failingUrl);
				}
				
				
        		
        	});
        	
        	mWebView.loadUrl(mUrl); 
        	
            return mWebView;
        }
    }
    

    /**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     */
    public static class LessonSectionFragment extends Fragment {
    	
    	private LessonListView mListView = null;
    	public static final String ARG_SECTION_NUMBER = "section_number";
		

        public void setListView(LessonListView listView) {
        	
        	mListView = listView;
        }
        
        public LessonListView getListView ()
        {
        	return mListView;
        	
        }
        
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
           
            return mListView;
        }
    }
    
	
    @Override
    public void onBackPressed() {
    	
    	boolean handled = mListView.handleBack();
    	
    	if (!handled)
    		NavUtils.navigateUpFromSameTask(this);
    }
    
	 @Override
	    public boolean onKeyDown(int keyCode, KeyEvent event)  {
	        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ECLAIR
	                && keyCode == KeyEvent.KEYCODE_BACK
	                && event.getRepeatCount() == 0) {
	           
	        	boolean handled = mListView.handleBack();
	        	
	        	if (!handled)
	        		NavUtils.navigateUpFromSameTask(this);
	        	
	        	return handled;
	        	
	        }

	        return super.onKeyDown(keyCode, event);
	    }

}
