package info.guardianproject.mrapp;

import java.util.Date;

import org.holoeverywhere.widget.Spinner;
import org.holoeverywhere.widget.Toast;

import info.guardianproject.mrapp.R;
import info.guardianproject.mrapp.model.Project;
import info.guardianproject.mrapp.model.Report;
import info.guardianproject.mrapp.model.Scene;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class ReportActivity extends BaseActivity {

	private RadioGroup rGroup;
	private TextView txtNewStoryDesc;
	private EditText editTextStoryName;
	private Spinner spinnerSector;
	private Spinner spinnerIssue;
	private EditText editTextDesc;
	private EditText editTextEntity;
	private TextView textViewLocation; 
	int rid;
	String title;
	String issue;
	String sector;
	String description;
	String location;
	String entity;
	Button done;
	Button view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_new_story);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        txtNewStoryDesc = (TextView)findViewById(R.id.txtNewStoryDesc);
        editTextStoryName = (EditText)findViewById(R.id.editTextStoryName);
        spinnerSector = (Spinner)findViewById(R.id.spinnerSector);
        spinnerIssue = (Spinner)findViewById(R.id.spinnerIssue);
        editTextDesc = (EditText)findViewById(R.id.editTextDescription);
        editTextEntity = (EditText)findViewById(R.id.editTextEntity);
        textViewLocation = (TextView)findViewById(R.id.textViewLocation);
        rGroup = (RadioGroup)findViewById(R.id.radioGroupStoryType);
        
        done = (Button)findViewById(R.id.done);
        view = (Button)findViewById(R.id.done);
        
        Intent i = getIntent();
        if(i.getIntExtra("rid", -1)!=-1){
        	title = i.getStringExtra("title");
            sector = i.getStringExtra("sector");
            issue = i.getStringExtra("issue");
            entity = i.getStringExtra("entity");
            description = i.getStringExtra("description");
            location = i.getStringExtra("location");
            
            editTextStoryName.setText(title);
            setSelectedItem(spinnerSector, sector);
            setSelectedItem(spinnerIssue, issue);
            editTextDesc.setText(description);
            editTextEntity.setText(entity);
            textViewLocation.setText(location);
            
            view.setVisibility(View.VISIBLE);
            done.setText("Update");
        }
        
        rGroup.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
        	
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				
				if (checkedId == R.id.radioStoryType0)
		    	{
		    		//video
					txtNewStoryDesc.setText(R.string.template_video_desc);
		    		
		    	}
		    	else if (checkedId == R.id.radioStoryType1)
		    	{

		    		//photo

					txtNewStoryDesc.setText(R.string.template_photo_desc);
		    	}
		    	else if (checkedId == R.id.radioStoryType2)
		    	{

		    		//audio

					txtNewStoryDesc.setText(R.string.template_audio_desc);
		    	}
				if (formValid()) {
					launchProject(editTextStoryName.getText().toString(), spinnerIssue.getSelectedItem().toString(),spinnerSector.getSelectedItem().toString(),editTextEntity.getText().toString(),editTextDesc.getText().toString(),textViewLocation.getText().toString());		
				}
			}
        	
        });
        
        done.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
            	
            	if (formValid()) {

            	}
            	
            }
        });
		view.setOnClickListener(new OnClickListener() {
		            
		            @Override
		            public void onClick(View v) {		            	
		            	Intent p = new Intent(getBaseContext(), ProjectsActivity.class);
		            	p.putExtra("rid", rid);
		            	startActivity(p);
		            	
		            }
		        });
    }
    
    public void setSelectedItem(Spinner spinner,String string){
		int index = 0;
		for (int i = 0; i < spinner.getAdapter().getCount(); i++){
			if (spinner.getItemAtPosition(i).equals(string)){
				index = i;
			}
		}
			spinner.setSelection(index);
	}
    
    private boolean formValid ()
    {
    	String pName = editTextStoryName.getText().toString();
    	
    	
    	if (pName == null || pName.length() == 0)
    	{
    		Toast.makeText(this, R.string.you_must_enter_a_project_name, Toast.LENGTH_SHORT).show();
    		return false;
    	}
    	else
    	{
    		return true;
    	}
    }
    
    private int getSelectedStoryMode ()
    {
    	   int checkedId = rGroup.getCheckedRadioButtonId();
    	   int resultMode = -1;
    	   
    	   switch (checkedId)
    	   {
    	   case R.id.radioStoryType0:
    		   resultMode = Project.STORY_TYPE_VIDEO;
    		   break;
    	   case R.id.radioStoryType1:
    		   resultMode = Project.STORY_TYPE_PHOTO;
    		   break;
    		   
    	   case R.id.radioStoryType2:
    		   resultMode = Project.STORY_TYPE_AUDIO;
    		   break;
    		   
    	   case R.id.radioStoryType3:
    		   resultMode = Project.STORY_TYPE_ESSAY;
    		   break;
    		   
    	   }
    	   
    	   return resultMode;
    }
    		

    private void launchProject(String title, String pIssue, String pSector, String pEntity, String pDesc, String pLocation) {
        Report report;
        if(rid==-1){
        	report = new Report (this, 0, title, pIssue, pSector, pEntity, pDesc, pLocation);
        }else{
        	report = Report.get(this, rid);
        }
        report.save();
        
        Intent intent = new Intent(getBaseContext(), StoryNewActivity.class);
        intent.putExtra("storymode", getSelectedStoryMode());
        intent.putExtra("rid", report.getId());
        startActivity(intent);
         
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_new_story, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
