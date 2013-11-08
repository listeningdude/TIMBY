package info.guardianproject.mrapp;

import info.guardianproject.mrapp.model.Project;
import info.guardianproject.mrapp.model.Scene;
import android.content.Intent;
import android.os.Bundle;

public class StoryNewActivity extends BaseActivity {

	int rid;
	int storymode;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        rid = intent.getIntExtra("rid", -1);
        storymode = intent.getIntExtra("storymode", -1);
        
        setContentView(R.layout.activity_new_story);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        launchSimpleStory("", rid, storymode, true);
  
    }
    

    private void launchSimpleStory(String pName,  int pReport, int storyMode, boolean autoCapture) {
        int clipCount = AppConstants.DEFAULT_CLIP_COUNT;
        
        Project project;
    	project = new Project (this, clipCount);
    	project.setTitle(pName);
        project.setReport(String.valueOf(pReport));
        project.save();
        
        Scene scene = new Scene(this, clipCount);
        scene.setProjectIndex(0);
        scene.setProjectId(project.getId());
        scene.save();
    
        String templateJsonPath = Project.getSimpleTemplateForMode(storyMode);
       
        project.setStoryType(storyMode);
        project.save();
        
        Intent intent = new Intent(getBaseContext(), StoryMakerCaptureActivitySimple.class);
        intent.putExtra("story_mode", storyMode);
        intent.putExtra("template_path", templateJsonPath);
        intent.putExtra("title", project.getTitle());
        intent.putExtra("pid", project.getId());
        intent.putExtra("scene", 0);
        intent.putExtra("auto_capture", autoCapture);
        
        startActivity(intent);
        finish();
    }
}
