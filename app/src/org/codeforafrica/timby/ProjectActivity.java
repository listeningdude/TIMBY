package org.codeforafrica.timby;

import org.codeforafrica.timby.model.Media;
import org.codeforafrica.timby.model.Project;
import android.widget.Toast;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Button;

public class ProjectActivity extends BaseActivity{
	int pid;
	Button btnPublish;
	Button btnSkip;
	Project project;
	EditText desc;
	
	public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.fragment_story_publish);
	    
	    btnPublish = (Button)findViewById(R.id.btnPublish);
	   
	    btnSkip = (Button)findViewById(R.id.btnSkip);
	    btnSkip.setVisibility(View.GONE);
	    
	    desc = (EditText)findViewById(R.id.editTextDescribe);    
	    
	    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
	   
	    Intent i = getIntent();
	    pid = i.getIntExtra("pid", -1);
	    project = Project.get(getApplicationContext(), pid);
	    
	    desc.setText(project.getTitle());
	    
	    ImageView ivIcon = (ImageView)findViewById(R.id.storyThumb);
	    ivIcon.setVisibility(View.VISIBLE);
	    
	    // FIXME default to use first scene
	    Media[] mediaList = project.getScenesAsArray()[0].getMediaAsArray();
	    
	    if (mediaList != null && mediaList.length > 0)    
	    {
	    	for (Media media: mediaList)
	    		if (media != null)
	    		{
	    				
	    			Bitmap bmp = Media.getThumbnail(ProjectActivity.this,media,project);
	    			if (bmp != null)
	    				ivIcon.setImageBitmap(bmp);
	    			break;
	    		}
	    }
		
		btnPublish.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				project.setTitle(desc.getText().toString());
				project.save();
				Toast.makeText(getApplicationContext(), "Caption saved!", Toast.LENGTH_LONG).show();
				finish();
			}
		});
	}
	
}
