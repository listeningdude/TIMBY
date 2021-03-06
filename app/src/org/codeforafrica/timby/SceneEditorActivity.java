
package org.codeforafrica.timby;

import org.codeforafrica.timby.R;
import org.codeforafrica.timby.media.MediaProjectManager;
import org.codeforafrica.timby.media.OverlayCameraActivity;
import org.codeforafrica.timby.model.Project;
import org.codeforafrica.timby.model.Scene;
import org.codeforafrica.timby.model.template.Clip;
import org.codeforafrica.timby.model.template.Template;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import net.micode.soundrecorder.SoundRecorder;

import org.holoeverywhere.app.AlertDialog;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class SceneEditorActivity extends EditorBaseActivity implements ActionBar.TabListener {
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
		
    protected Menu mMenu = null;
    
    //private String mTemplateJsonPath = null;
    private int mSceneIndex = 0;

    boolean mTrimMode = false;
    
    private final static String CAPTURE_MIMETYPE_AUDIO = "audio/3gpp";
    public Fragment mFragmentTab0, mFragmentTab1, mLastTabFrag;
    public PublishFragment mPublishFragment;
    public int quickstory;
    @Override
    @SuppressLint("NewApi")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        
        //mTemplateJsonPath = getIntent().getStringExtra("template_path"); 
        //mStoryMode = getIntent().getIntExtra("story_mode", Project.STORY_TYPE_VIDEO);

        int pid = intent.getIntExtra("pid", -1); //project id

        quickstory = intent.getIntExtra("quickstory", quickstory);
        mSceneIndex = getIntent().getIntExtra("scene", 0);
        //Log.e("pid", String.valueOf(pid));
        if (pid != -1)
        {
        	mProject = Project.get(getApplicationContext(), pid);
            Scene scene = null;
            if ((mSceneIndex != -1) && (mSceneIndex < mProject.getScenesAsArray().length)) {
                scene = mProject.getScenesAsArray()[mSceneIndex];
            }
            mMPM = new MediaProjectManager(this, getApplicationContext(), getIntent(), mHandlerPub, mProject, scene);
            mMPM.initProject();
            mMPM.addAllProjectMediaToEditor();
        }
        else
        {
            int clipCount = 5; // FIXME get rid of hardcoded clipCount = 5

            String title = intent.getStringExtra("title");
        
            mProject = new Project(getApplicationContext(), clipCount);
            mProject.setTitle(title);
            mProject.save();
            mMPM = new MediaProjectManager(this, getApplicationContext(), getIntent(), mHandlerPub, mProject);
            mMPM.initProject();
        }
        
        try
        {
	        if (mProject.getScenesAsList().size() > 1)
	        {
	        	mTemplate = Template.parseAsset(this, mProject.getTemplatePath(),Project.getSimpleTemplateForMode(mProject.getStoryType()));
	        	
	        }
	        else
	        {
	        	mTemplate = Template.parseAsset(this, Project.getSimpleTemplateForMode(mProject.getStoryType()));
	        	
	        }
        }
        catch (Exception e)
        {
        	Log.e(AppConstants.TAG,"could not parse templates",e);
        }
        setContentView(R.layout.activity_scene_editor_no_swipe);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        
     // action bar stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        
        
        
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayHomeAsUpEnabled(true);
        /*if (mMPM.mScene != null) {
            actionBar.setTitle(mMPM.mScene.getTitle());
            
        }*/
        	actionBar.setTitle("Add Caption");
       // 	TextView title2 = (TextView) getWindow().getDecorView().findViewById(getResources().getIdentifier("action_bar_title", "id", "android"));
       //     title2.setTextColor(getResources().getColor(R.color.soft_purple));
        // For each of the sections in the app, add a tab to the action bar.
        //	actionBar.addTab(actionBar.newTab().setText("Media List").setTabListener(this));
        // actionBar.addTab(actionBar.newTab().setText(R.string.tab_order).setTabListener(this));
      //  if (mMPM.mProject.isTemplateStory()) {
            actionBar.addTab(actionBar.newTab().setText("Add Caption").setTabListener(this));
       // } else {
        
       //     actionBar.addTab(actionBar.newTab().setText(R.string.tab_publish).setTabListener(this));
     //   }
        
        
        if (intent.hasExtra("auto_capture")
        		&& intent.getBooleanExtra("auto_capture", false))
        {
        	openCaptureMode(0, 0);
        	
        }
        
    }
    
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getSupportActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getSupportActionBar().getSelectedNavigationIndex());
    }
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

       getSupportMenuInflater().inflate(R.menu.activity_scene_editor, menu);
        mMenu = menu;
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemForward:
                int idx = getSupportActionBar().getSelectedNavigationIndex();
                if (idx < 2) {
                    getSupportActionBar().setSelectedNavigationItem(Math.min(2, idx + 1));
                } else {
                    mPublishFragment.doPublish();
                }
                return true;
            case R.id.addFromGallery:
                addMediaFromGallery();
            
                return true;
            case R.id.addNewShot:
             
                addShotToScene();
                
                return true;
            case R.id.delShot:
            	
            	deleteCurrentShot();
            	return true;
            case R.id.exportProjectFiles:
                exportProjectFiles();
            
                return true;
            case R.id.itemTrim:
                if (mFragmentTab1 != null) { 
                    ((OrderClipsFragment) mFragmentTab1).loadTrim();
                    ((OrderClipsFragment) mFragmentTab1).enableTrimMode(true);
                    startActionMode(mActionModeCallback);
                }
                return true;
                
        }
        return super.onOptionsItemSelected(item);
    }
    
    private boolean actionModelCancel = false;
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu_trim, menu);
            actionModelCancel = false;
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            mTrimMode = true;
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_cancel:
                    actionModelCancel = true;
                    mode.finish();
                    return true;
                case R.id.menu_trim_clip:
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        // this has slightly odd save logic so that I can always save exit actionmode as 
        // the checkmark button acts as a cancel but the users will treat it as an accept
        @Override
        public void onDestroyActionMode(ActionMode mode) {
//            mActionMode = null;
            ((OrderClipsFragment) mFragmentTab1).enableTrimMode(false);
            mTrimMode = false;
            if (actionModelCancel) {
                ((OrderClipsFragment) mFragmentTab1).undoSaveTrim();
            } else {
                ((OrderClipsFragment) mFragmentTab1).saveTrim();
            }
        }
    };
    */
    // FIXME move this into AddClipsFragment?
    public void addShotToScene ()
    {
        try
        {
            Clip tClip = new Clip();
            tClip.setDefaults();
            AddClipsFragment acf = ((AddClipsFragment)mFragmentTab0);
            acf.addTemplateClip(tClip);
        }
        catch (Exception e)
        {
            Log.e(AppConstants.TAG,"error adding new clip",e);
        }
    }

    public void deleteCurrentShot ()
    {
		mMPM.deleteCurrentClip();
    	
    }
    
    private void exportProjectFiles()
    {	
		try 
		{
	    	File fileProjectSrc = MediaProjectManager.getExternalProjectFolder(mMPM.mProject, mMPM.getContext());	
	    	ArrayList<File> fileList= new ArrayList<File>();
	    	String mZipFileName = buildZipFilePath(fileProjectSrc.getAbsolutePath());
	    	
	    	//if not enough space
	    	if(!mMPM.checkStorageSpace())
	        {
	    		return;
	        }
	         
	    	String[] mMediaPaths = mMPM.mProject.getMediaAsPathArray();
	    	
	    	//add videos
	    	for (String path : mMediaPaths)
	    	{
	    		fileList.add(new File(path));
	    	}
	    	
	    	//add thumbnails
	    	fileList.addAll(Arrays.asList(fileProjectSrc.listFiles()));
	    	
	    	//add database file
	    	fileList.add(getDatabasePath("sm.db4"));
	    	    	
			FileOutputStream fos = new FileOutputStream(mZipFileName);
			ZipOutputStream zos = new ZipOutputStream(fos);
			
			exportProjectFiles(zos, fileList.toArray( new File[fileList.size()]));

			zos.close();
			
			onExportProjectSuccess(mZipFileName);
		}
		catch (IOException ioe) 
		{
			Log.e(AppConstants.TAG, "Error creating zip file:", ioe);
		} 	
    }
    
    
    private void exportProjectFiles(ZipOutputStream zos, File[] fileList)
    {
    	final int BUFFER = 2048;
    	
		for (int i = 0; i < fileList.length; i++) 
		{		
			try 
			{
				byte[] data = new byte[BUFFER];

				FileInputStream fis = new FileInputStream(fileList[i]);
				zos.putNextEntry(new ZipEntry(fileList[i].getName()));
				
				int count;
				while ((count = fis.read(data, 0, BUFFER)) != -1) 
				{ 
					zos.write(data, 0, count); 
				} 

				//close steams
				zos.closeEntry();
				fis.close();

			} 
			catch (IOException ioe) 
			{
				Log.e(AppConstants.TAG, "Error creating zip file:", ioe);
			}			
		}
    }
    
    private void onExportProjectSuccess(final String zipFileName)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(R.string.export_dialog_title);
        dialogBuilder.setPositiveButton(R.string.export_dialog_share, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                
            	Intent shareIntent = new Intent();
            	shareIntent.setAction(Intent.ACTION_SEND);
            	shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(zipFileName)));
            	shareIntent.setType("*/*");
            	startActivity(shareIntent);  	
            }
        });
        dialogBuilder.setNegativeButton(R.string.export_dialog_close,
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
        dialogBuilder.show();
    }
    
    private String buildZipFilePath(String filePath)
    {
    	//create datestamp
    	Date date = new Date();
    	SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
    	
    	int index = filePath.lastIndexOf('/');
    	filePath = filePath.substring(0, index + 1);
    	
    	return String.format("%sstorymaker_project_%s_%s.zip", filePath, mMPM.mProject.getId(), dateFormat.format(date));
    }
     
    private void addMediaFromGallery()
    {
        mMPM.mMediaHelper.openGalleryChooser("*/*");
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().hide(mLastTabFrag).commit();
    }

    // protected void setupAddClipsFragment() {
    // FragmentManager fm = getSupportFragmentManager();
    //
    // try {
    // mFragmentTab0 = new SceneChooserFragment(R.layout.fragment_add_clips, fm,
    // mTemplateJsonPath);
    // } catch (IOException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // } catch (JSONException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        
    	// When the given tab is selected, show the tab contents in the
        // container
        int layout = R.layout.fragment_add_clips;
        FragmentManager fm = getSupportFragmentManager();

        if (mMenu != null) {
            mMenu.findItem(R.id.itemInfo).setVisible(false);
            mMenu.findItem(R.id.itemTrim).setVisible(false);
        }
        
        if(mLastTabFrag instanceof OrderClipsFragment)
        {
        	((OrderClipsFragment) mLastTabFrag).stopPlaybackOnTabChange();
        }
        
        if (tab.getPosition() == 2) {

            if (mMenu != null) {
                mMenu.findItem(R.id.itemForward).setEnabled(true);
            }
            layout = R.layout.fragment_add_clips;
            //layout = R.layout.fragment_media_list;	
            if (mFragmentTab0 == null)
            {
               
            //    mFragmentTab0 = new AddClipsFragment(layout, fm, mTemplate, mSceneIndex, this);
                mFragmentTab0 = new AddClipsFragment();

                Bundle args = new Bundle();
                args.putInt(AddClipsFragment.ARG_SECTION_NUMBER, tab.getPosition() + 1);
                args.putInt("layout", layout);
                args.putInt("scene",mSceneIndex);
                mFragmentTab0.setArguments(args);
             
                fm.beginTransaction()
                        .add(R.id.container, mFragmentTab0, layout + "")
                        .commit();

            } else {
                fm.beginTransaction()
                        .show(mFragmentTab0)
                        .commit();
            }
            mLastTabFrag = mFragmentTab0;

        } else if (tab.getPosition() == 1) {
            layout = R.layout.fragment_order_clips;

            if (mMenu != null) {
                mMenu.findItem(R.id.itemInfo).setVisible(true);       
                //mMenu.findItem(R.id.itemForward).setEnabled(true);
                
                //if only photos, no need to display trim option
                if(!(mMPM.mProject.getStoryType() == Project.STORY_TYPE_ESSAY || mMPM.mProject.getStoryType() == Project.STORY_TYPE_PHOTO))
                {
                	mMenu.findItem(R.id.itemTrim).setVisible(true);
                }             
            }
            
            if (mFragmentTab1 == null)
            {
                
                mFragmentTab1 = new OrderClipsFragment();

                Bundle args = new Bundle();
                args.putInt(OrderClipsFragment.ARG_SECTION_NUMBER, tab.getPosition() + 1);
                args.putInt("layout", layout);
                mFragmentTab1.setArguments(args);


                fm.beginTransaction()
                        .add(R.id.container, mFragmentTab1, layout + "")
                        .commit();

            } else {

                ((OrderClipsFragment)mFragmentTab1).loadMedia();
                
                fm.beginTransaction()
                        .show(mFragmentTab1)
                        .commit();
            }

            mLastTabFrag = mFragmentTab1;

        } else if (tab.getPosition() == 0) {
            if (mMPM.mProject.isTemplateStory()) {
                Intent intent = new Intent(getBaseContext(), StoryTemplateActivity.class);
                intent.putExtra("template_path", mProject.getTemplatePath());
                intent.putExtra("story_mode", mMPM.mProject.getStoryType());
                intent.putExtra("pid", mMPM.mProject.getId());
                intent.putExtra("title", mMPM.mProject.getTitle());
                startActivity(intent);
                finish();
            } else {
                layout = R.layout.fragment_story_publish;
                
                if (mPublishFragment == null)
                {
                	mPublishFragment = new PublishFragment();
                    Bundle args = new Bundle();
                    args.putInt(PublishFragment.ARG_SECTION_NUMBER, tab.getPosition() + 1);
                	args.putInt("layout",layout);
                	args.putInt("quickstory", quickstory);
                	mPublishFragment.setArguments(args);
                        
    
                    fm.beginTransaction()
                            .add(R.id.container, mPublishFragment, layout + "")
                            .commit();
    
                } else {
    
                    fm.beginTransaction()
                            .show(mPublishFragment)
                            .commit();
                }
    
                mLastTabFrag = mPublishFragment;
            }
        }
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    public void refreshClipPager() {
        if (mFragmentTab0 != null) {
            try {
                ((AddClipsFragment) mFragmentTab0).reloadClips();
            } 
            catch (Exception e) {
                Log.e(AppConstants.TAG, "error reloading clips", e);
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

	public void openCaptureMode (int shotType, int clipIndex)
	{
		
		if (mProject.getStoryType() == Project.STORY_TYPE_AUDIO)
		{
			Intent i = new Intent(getApplicationContext(), SoundRecorder.class);
			i.putExtra("dir", mMPM.getExternalProjectFolder(mProject, getBaseContext()));
			i.setType(CAPTURE_MIMETYPE_AUDIO);
			i.putExtra("mode", mProject.getStoryType());
			mMPM.mClipIndex = clipIndex;
			startActivityForResult(i,mProject.getStoryType());

        }
        else
        {
            Intent i = new Intent(getApplicationContext(), OverlayCameraActivity.class);
            i.putExtra("group", shotType);
            i.putExtra("mode", mProject.getStoryType());
            mMPM.mClipIndex = clipIndex;
            startActivityForResult(i, REQ_OVERLAY_CAM);
        }
    }

    private File mCapturePath;
  //function to perform the encryption before saving to the SD Card
    static void encrypt(File file) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException
    {
    	// Here you read the cleartext.
        FileInputStream fis = new FileInputStream(file);
        // This stream write the encrypted text. This 	stream will be wrapped by another stream.
        FileOutputStream fos = new FileOutputStream(file);

        // Length is 16 byte
        SecretKeySpec sks = new SecretKeySpec("BackYardSourceMap$2012".getBytes(), "AES");
        // Create cipher
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, sks);
        // Wrap the output stream
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);
        // Write bytes
        int b;
        byte[] d = new byte[8];
        while((b = fis.read(d)) != -1) {
            cos.write(d, 0, b);
        }
        // Flush and close streams.
        cos.flush();
        cos.close();
        fis.close();
    }
    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent intent) {

        if (resCode == RESULT_OK)
        {
            if (reqCode == REQ_OVERLAY_CAM)
            {
                File fileMediaFolder = mMPM.getExternalProjectFolder(mProject, getBaseContext());

                if (mProject.getStoryType() == Project.STORY_TYPE_VIDEO)
                {
                    mCapturePath = mMPM.mMediaHelper.captureVideo(fileMediaFolder);

                }
                else if (mProject.getStoryType() == Project.STORY_TYPE_PHOTO)
                {
                    mCapturePath = mMPM.mMediaHelper.capturePhoto(fileMediaFolder);
                }
                else if (mProject.getStoryType() == Project.STORY_TYPE_ESSAY)
                {
                    mCapturePath = mMPM.mMediaHelper.capturePhoto(fileMediaFolder);
                }
                	
            }
            else if (reqCode == REQ_YOUTUBE_AUTH)
            {
            	if (resCode == RESULT_OK)
            	{
            		
            		String oauthToken = intent.getStringExtra("token");
            		Log.d("OAuth","got token: " + oauthToken);
            		mPublishFragment.setYouTubeAuth(oauthToken);
            	}
            }
            else
            {
            	try
            	{
            		mMPM.handleResponse(intent, mCapturePath);

            		refreshClipPager();
            	}
            	catch (IOException e)
            	{
            		Log.e(AppConstants.TAG,"error handling capture response: " + mCapturePath,e);
            	}
            }
        }else{
        	mProject.delete();
        	finish();
        	//Log.d("rescode", String.valueOf(resCode));
        }
        
    }
}