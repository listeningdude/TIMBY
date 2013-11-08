package info.guardianproject.mrapp;

import java.io.File;
import java.io.IOException;

import net.micode.soundrecorder.SoundRecorder;
import info.guardianproject.mrapp.media.MediaProjectManager;
import info.guardianproject.mrapp.media.OverlayCameraActivity;
import info.guardianproject.mrapp.model.Media;
import info.guardianproject.mrapp.model.Project;
import info.guardianproject.mrapp.model.Scene;
import info.guardianproject.mrapp.model.template.Template;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.ImageView;

public class StoryMakerCaptureActivitySimple extends EditorBaseActivity{
		private int mSceneIndex = 0;
		private final static String CAPTURE_MIMETYPE_AUDIO = "audio/3gpp";
		public Fragment mFragmentTab0, mFragmentTab1, mLastTabFrag;
		public PublishFragment mPublishFragment;
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        Intent intent = getIntent();
	        int pid = intent.getIntExtra("pid", -1); //project id
	        mSceneIndex = getIntent().getIntExtra("scene", 0);
	        mProject = Project.get(getApplicationContext(), pid);
            Scene scene = null;
            if ((mSceneIndex != -1) && (mSceneIndex < mProject.getScenesAsArray().length)) {
                scene = mProject.getScenesAsArray()[mSceneIndex];
            }
            mMPM = new MediaProjectManager(this, getApplicationContext(), getIntent(), mHandlerPub, mProject, scene);
            mMPM.initProject();
            mMPM.addAllProjectMediaToEditor();
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
            setContentView(R.layout.medialist);
            openCaptureMode(0, 0);
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
	           
	        }
	        	
	        
	    }
}
