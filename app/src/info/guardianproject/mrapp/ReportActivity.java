package info.guardianproject.mrapp;

import org.holoeverywhere.widget.Spinner;
import org.holoeverywhere.widget.Toast;

import info.guardianproject.mrapp.R;
import info.guardianproject.mrapp.model.GPSTracker;
import info.guardianproject.mrapp.model.Project;
import info.guardianproject.mrapp.model.Report;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

public class ReportActivity extends BaseActivity {

	private RadioGroup rGroup;
	private TextView txtNewStoryDesc;
	private EditText editTextStoryName;
	private Spinner spinnerSector;
	private Spinner spinnerIssue;
	private EditText editTextDesc;
	private EditText editTextEntity;
	int rid;
	String title;
	String issue;
	String sector;
	String description;
	String location;
	String entity;
	Button done;
	ImageView setLocation;
	Button view;
	int story_mode;
	TextView gpsInfo;
	GPSTracker gpsT; 
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
        rGroup = (RadioGroup)findViewById(R.id.radioGroupStoryType);
        
        done = (Button)findViewById(R.id.done);
        view = (Button)findViewById(R.id.view);
        setLocation = (ImageView)findViewById(R.id.setLocation);
        gpsInfo = (TextView)findViewById(R.id.textViewLocation);
        
        Intent i = getIntent();
        rid = i.getIntExtra("rid", -1);
        if(rid!=-1){
        
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
            gpsInfo.setText(location);
            
            view.setVisibility(View.VISIBLE);
            done.setText("Update");
        }

        //Button actions
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
				
		    	else if (checkedId == R.id.radioStoryType3)
                {
                        //essay

                            txtNewStoryDesc.setText(R.string.template_essay_desc);
                        
                }
				story_mode = 2;
				if (formValid()) {
					launchProject(editTextStoryName.getText().toString(), spinnerIssue.getSelectedItem().toString(),spinnerSector.getSelectedItem().toString(),editTextEntity.getText().toString(),editTextDesc.getText().toString(),gpsInfo.getText().toString(), false);		
				}
			}
        	
        });
        
        done.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
            	
            	if (formValid()) {
					launchProject(editTextStoryName.getText().toString(), spinnerIssue.getSelectedItem().toString(),spinnerSector.getSelectedItem().toString(),editTextEntity.getText().toString(),editTextDesc.getText().toString(),gpsInfo.getText().toString(), true);		
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
		setLocation.setOnClickListener(new OnClickListener(){
			@Override
            public void onClick(View v) {		
				gpsT = new GPSTracker(ReportActivity.this); 
				  
		          // check if GPS enabled 
		          if(gpsT.canGetLocation()){ 
		  
		              double latitude = gpsT.getLatitude(); 
		              double longitude = gpsT.getLongitude(); 
		  
		              // \n is for new line 
		              gpsInfo.setText(latitude+", "+longitude); 
		             /* GeoPoint myGeoPoint = new GeoPoint( 
		                    (int)(latitude*1000000), 
		                    (int)(longitude*1000000)); 
		            	CenterLocatio(myGeoPoint); */
		          }else{ 
		              // can't get location 
		              // GPS or Network is not enabled 
		              // Ask user to enable GPS/network in settings 
		              gpsT.showSettingsAlert(); 
		          } 
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
    		

    private void launchProject(String title, String pIssue, String pSector, String pEntity, String pDesc, String pLocation, boolean update) {
        Report report;
        if(rid==-1){
        	report = new Report (this, 0, title, pIssue, pSector, pEntity, pDesc, pLocation);
        	
        }else{
        	report = Report.get(this, rid);
        	report.setTitle(title);
        	report.setDescription(pDesc);
        	report.setEntity(pEntity);
        	report.setIssue(pIssue);
        	report.setSector(pSector);
        	report.setLocation(pLocation);
        }
        report.save();
        
        rid = report.getId();
        
        if(update == false){
	        Intent intent = new Intent(getBaseContext(), StoryNewActivity.class);
	        intent.putExtra("storymode", getSelectedStoryMode());
	        intent.putExtra("rid", report.getId());
	        startActivity(intent);
        }else{
        	Toast.makeText(getBaseContext(), String.valueOf(rid)+" Updated successfully!", Toast.LENGTH_LONG).show();
        }
         
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
