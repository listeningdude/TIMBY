package org.codeforafrica.timby;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.codeforafrica.timby.R;
import org.codeforafrica.timby.model.Project;
import org.codeforafrica.timby.model.Report;
import org.codeforafrica.timby.model.Scene;

import com.google.analytics.tracking.android.EasyTracker;

import android.content.Intent;
import android.os.Bundle;

public class StoryNewActivity extends BaseActivity {
	int rid;
	int storymode;
	int quickstory;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        rid = intent.getIntExtra("rid", -1);
        storymode = intent.getIntExtra("storymode", -1);
        
        quickstory = intent.getIntExtra("quickstory", 0);
        
        setContentView(R.layout.activity_new_story);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        launchSimpleStory("", rid, storymode, true, quickstory);  
    }
    private int createReport() {
      	
      	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      	String currentdate = dateFormat.format(new Date());
      	
      	String pLocation = "0, 0";
      	
      	String title = "Captured at "+currentdate;
      	
      	Report report = new Report (getApplicationContext(), 0, title, "0", "0", "", "", pLocation, "0", currentdate);
          
        report.save();
          
        rid = report.getId();
        
        return rid;
          
        }
    private void launchSimpleStory(String pName,  int pReport, int storyMode, boolean autoCapture, int quickstory) {
        
    	if(rid == -1){
        	pReport  = createReport();
        }
    	
    	int clipCount = AppConstants.DEFAULT_CLIP_COUNT;
        
        Project project;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	String currentdate = dateFormat.format(new Date());
    	
    	project = new Project (this, clipCount);
    	
    	project.setTitle(pName);
    	project.setDate(currentdate);
        project.setReport(String.valueOf(pReport));
        project.save();
        
        Scene scene = new Scene(this, clipCount);
        scene.setProjectIndex(0);
        scene.setProjectId(project.getId());
        scene.save();
        
        String templateJsonPath = Project.getSimpleTemplateForMode(storyMode);
        
        project.setStoryType(storyMode);
        project.save();
        
        Intent intent = new Intent(getBaseContext(), SceneEditorActivity.class);
        intent.putExtra("story_mode", storyMode);
        intent.putExtra("template_path", templateJsonPath);
        intent.putExtra("title", project.getTitle());
        intent.putExtra("pid", project.getId());
        intent.putExtra("scene", 0);
        intent.putExtra("quickstory", quickstory);
        intent.putExtra("auto_capture", autoCapture);
        startActivity(intent);
        finish();
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