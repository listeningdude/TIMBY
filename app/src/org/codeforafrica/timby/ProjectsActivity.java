package org.codeforafrica.timby;

import org.codeforafrica.timby.R;
import org.codeforafrica.timby.media.MediaProjectManager;
import org.codeforafrica.timby.model.Media;
import org.codeforafrica.timby.model.Project;

import java.io.File;
import java.util.ArrayList;

import org.holoeverywhere.app.AlertDialog;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class ProjectsActivity extends BaseActivity {


	ListView mListView;

	private ArrayList<Project> mListProjects;
	private ProjectArrayAdapter aaProjects;
	int rid;
	ProgressDialog pDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent i = getIntent();
        rid = i.getIntExtra("rid", -1);
        setContentView(R.layout.activity_projects);
        
        // action bar stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#9E3B33")));
        
        mListView = (ListView)findViewById(R.id.projectslist);
        
        initListView(mListView);
        //new showList().execute();
    }
    
    class showList extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ProjectsActivity.this);
			pDialog.setMessage("Decrypting thumbnails...");
			pDialog.show();
		}
		protected String doInBackground(String... args) {
			mListProjects = Project.getAllAsList(ProjectsActivity.this, rid);
	         aaProjects = new ProjectArrayAdapter(ProjectsActivity.this, 
	           	   R.layout.list_project_row, mListProjects);
	         
	         mListView.setAdapter(aaProjects);
			return null;
		}
	protected void onPostExecute(String file_url) {
			pDialog.dismiss();	
		}
	}
    
   /* @Override
	public void onResume() {
		super.onResume();
		refreshProjects();

	}*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_projects, menu);
        return true;
    }


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
         case android.R.id.home:

	        	NavUtils.navigateUpFromSameTask(this);
	        	
             return true;
         /*case R.id.menu_report:
        	 Intent r = new Intent(this, ReportActivity.class);
			 startActivity(new Intent(this, StoryNewActivity.class));

             return true;*/
     }
 		
     return super.onOptionsItemSelected(item);
  
	}
    
	
	private void showPreferences ()
	{
		Intent intent = new Intent(this,SimplePreferences.class);
		this.startActivityForResult(intent, 9999);
	}

    
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		
		super.onActivityResult(arg0, arg1, arg2);
		

		boolean changed = ((StoryMakerApp)getApplication()).checkLocale();
		if (changed)
		{
			startActivity(new Intent(this,ProjectsActivity.class));
			
			finish();
			
		}
	}

	
    public void initListView (ListView list) {
    
    	list.setOnItemLongClickListener(new OnItemLongClickListener(){

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                
                
                final Project project = mListProjects.get(arg2);


            AlertDialog.Builder builder = new AlertDialog.Builder(ProjectsActivity.this);
            builder.setMessage(R.string.delete_project_)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteProject (project);
                        }
                        
                    })
                    .setNegativeButton(R.string.no, null).show();
            
            
                
                return false;
            }
    	    
    	});
    	
        list.setOnItemClickListener(new OnItemClickListener ()
        {

			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			
				Project project = mListProjects.get(position);
				Intent intent = null;

			    
				/*if (project.getScenesAsArray().length > 1) {
					
				    intent = new Intent(ProjectsActivity.this, StoryTemplateActivity.class);
				    
				    
			    }else {
    				intent = new Intent(ProjectsActivity.this, ProjectEditorActivity.class);
    		    }
				
				intent.putExtra("template_path",project.getTemplatePath());
				intent.putExtra("story_mode", project.getStoryType());
                intent.putExtra("pid", project.getId());
                intent.putExtra("title", project.getTitle());
		        startActivity(intent);*/
			}
        	
        });
        
       refreshProjects();
        
    }
    
    public void refreshProjects ()
    {
    	 new showList().execute();
    }
    
    
    private void deleteProject (Project project)
    {
    	mListProjects.remove(project);
        aaProjects.notifyDataSetChanged();
        
    	project.delete();
    	
    	//should we delete project folders here too?
    }
    
    private static class GetThumbnailParams {
    	Context context;
    	Media media;
    	Project project;
    	ImageView ivIcon;
	    
	    GetThumbnailParams(Context context, Media media, Project project,ImageView ivIcon) {
	        this.context = context;
	        this.media = media;
	        this.project = project;
	        this.ivIcon = ivIcon;
	    }
	}
    class getThumbnail extends AsyncTask<GetThumbnailParams, String, String> {

		@Override
        protected void onPreExecute() {
            super.onPreExecute();
			//pDialog = new ProgressDialog(ProjectsActivity.this);
			//pDialog.setMessage("Decrypting thumbnail...");
			//pDialog.show(); 
        }
        protected String doInBackground(GetThumbnailParams... params) {
        	Context context = params[0].context;
        	final Media media = params[0].media;
        	final Project project = params[0].project;
        	final ImageView ivIcon = params[0].ivIcon;
        	
        	
            ProjectsActivity.this.runOnUiThread(new Runnable() 
                  {
                       public void run() 
                       {
                    	   Bitmap bmp = Media.getThumbnail(ProjectsActivity.this,media,project);
               				if (bmp != null)
               					ivIcon.setImageBitmap(bmp);
               				
               				//Delete decrypted file
               				String filepath = media.getPath();
                 			String[] fileparts = filepath.split("\\.");
                 			String filename = fileparts[0];
                 			String fileext = fileparts[1];
                 			String tempFile = filename+"2."+fileext;
                 			File temp = new File(tempFile);
                 			temp.delete();
                 			//If file is video delete thumbnail as well
                 			if(media.getMimeType().contains("video")){
                 				File fileThumb = new File(MediaProjectManager.getExternalProjectFolder(project, ProjectsActivity.this), media.getId() + "2.jpg");
                 	            fileThumb.delete();
                 			}
                 			
                       }

          });
        	
        	
        	return null;
        }
       
        protected void onPostExecute(String ppath) {
            //pDialog.dismiss();
        }
	}
        
    class ProjectArrayAdapter extends ArrayAdapter {
    	
    	Context context; 
        int layoutResourceId;    
        ArrayList<Project> projects;
        
        public ProjectArrayAdapter(Context context, int layoutResourceId,ArrayList<Project> projects) {
            super(context, layoutResourceId, projects);        
            
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.projects = projects;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            
            TextView tv;
            
            if(row == null)
            {
                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);
            }
            
            tv = (TextView)row.findViewById(R.id.title);
            
            Project project = projects.get(position);
            
            tv.setText("");       
            
            tv = (TextView)row.findViewById(R.id.description);
            
            ImageView ivType = (ImageView)row.findViewById(R.id.cardIcon);
            ImageView ivIcon = (ImageView)row.findViewById(R.id.imageView1);
            
            // FIXME default to use first scene
            Media[] mediaList = project.getScenesAsArray()[0].getMediaAsArray();
            
            if (mediaList != null && mediaList.length > 0)    
            {
            	for (Media media: mediaList)
            		if (media != null)
            		{
            			GetThumbnailParams params = new GetThumbnailParams(ProjectsActivity.this,media,project, ivIcon);
            		 	getThumbnail myTask = new getThumbnail();
            		 	myTask.execute(params);	
            			//Bitmap bmp = Media.getThumbnail(ProjectsActivity.this,media,project);
            			//if (bmp != null)
            			//	ivIcon.setImageBitmap(bmp);
            			break;
            		}
            }
            int sceneCount = project.getScenesAsList().size();
            int clipCount = project.getMediaAsList().size();
            
            String projectDesc = sceneCount + " " + getContext().getString(R.string.scene_s_) + ", " + clipCount + ' ' + getContext().getString(R.string.clip_s_);

            tv.setText(project.getTitle());
            
            if (project.getStoryType() == Project.STORY_TYPE_VIDEO)
	    	{
	    		//video
	    		ivType.setImageDrawable(getContext().getResources().getDrawable(R.drawable.btn_toggle_ic_list_video));
	    	}
	    	else if (project.getStoryType() == Project.STORY_TYPE_PHOTO)
	    	{	
	    		//photo	    	
	    		ivType.setImageDrawable(getContext().getResources().getDrawable(R.drawable.btn_toggle_ic_list_photo));

	    	}
	    	else if (project.getStoryType() == Project.STORY_TYPE_AUDIO)
	    	{
	
	    		//audio	    	
	    		ivType.setImageDrawable(getContext().getResources().getDrawable(R.drawable.btn_toggle_ic_list_audio));

	    	}
	    	else if (project.getStoryType() == Project.STORY_TYPE_ESSAY)
	    	{
	    		//essay
	    		ivType.setImageDrawable(getContext().getResources().getDrawable(R.drawable.btn_toggle_ic_list_essay));
	
	    	}
            return row;
        }
        
    }

}
