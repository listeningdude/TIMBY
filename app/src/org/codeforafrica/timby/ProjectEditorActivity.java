package org.codeforafrica.timby;

import org.codeforafrica.timby.R;

import org.holoeverywhere.widget.Spinner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class ProjectEditorActivity extends BaseActivity{
	RadioGroup rGroup;
	EditText editTextStoryName;
	Spinner spinnerSector;
	Spinner spinnerIssue;
	EditText editTextDesc;
	EditText editTextEntity;
	TextView textViewLocation; 
	
	String title;
	String issue;
	String sector;
	String description;
	String location;
	String entity;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_story);
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        editTextStoryName = (EditText)findViewById(R.id.editTextStoryName);
        spinnerSector = (Spinner)findViewById(R.id.spinnerSector);
        spinnerIssue = (Spinner)findViewById(R.id.spinnerIssue);
        editTextDesc = (EditText)findViewById(R.id.editTextDescription);
        editTextEntity = (EditText)findViewById(R.id.editTextEntity);
        textViewLocation = (TextView)findViewById(R.id.textViewLocation);
        rGroup = (RadioGroup)findViewById(R.id.radioGroupStoryType);
                
        Intent i = getIntent();
        
        title = i.getStringExtra("title");
        sector = i.getStringExtra("sector");
        issue = i.getStringExtra("issue");
        entity = i.getStringExtra("entity");
        description = i.getStringExtra("description");
        location = i.getStringExtra("location");
        
        editTextStoryName.setText(title);
        setSelectedItem(spinnerSector, sector);
        setSelectedItem(spinnerIssue, issue);
        //spinnerIssue.setText(issue);
        editTextDesc.setText(description);
        editTextEntity.setText(entity);
        textViewLocation.setText(location);
        
        
        
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
}
