package org.codeforafrica.timby;

import org.codeforafrica.timby.R;
import org.codeforafrica.timby.model.Media;
import org.codeforafrica.timby.model.Project;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.widget.Toast;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.actionbarsherlock.view.MenuItem;

public class ProjectsActivity extends BaseActivity {


	ListView mListView;

	private ArrayList<Project> mListProjects;
	private ProjectArrayAdapter aaProjects;
	int rid;
	ProgressDialog pDialog;
	getThumbnail get_thumbnail=null;
    @Override
    @SuppressLint("NewApi")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent i = getIntent();
        rid = i.getIntExtra("rid", -1);
        setContentView(R.layout.activity_projects);
        
        // action bar stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        
        //TextView title2 = (TextView) getWindow().getDecorView().findViewById(getResources().getIdentifier("action_bar_title", "id", "android"));
        //title2.setTextColor(getResources().getColor(R.color.soft_purple));
	     
        
        mListView = (ListView)findViewById(R.id.projectslist);
        /*pDialog = new ProgressDialog(ProjectsActivity.this);
		pDialog.setMessage("Getting thumbnails...");
		pDialog.setCancelable(false);
		pDialog.show(); 
        */
        File mThumbsDir = new File(Environment.getExternalStorageDirectory(), AppConstants.TAG+"/decrypts");
	    if (!mThumbsDir.exists()) {
	        if (!mThumbsDir.mkdirs()) {
	            Log.e("TIMBY: ", "Problem creating thumbnails folder");
	        }
	    }else{
	    	DeleteRecursive(mThumbsDir);
	    }
        initListView(mListView);
        //new showList().execute();

        Toast.makeText(getApplicationContext(), "Thumbnails might take a while to display", Toast.LENGTH_LONG).show();
       
        int delay = 3000; // delay for 1 sec. 
		int period = 3000; // repeat every 10 sec. 
		final Timer timer = new Timer(); 
		timer.scheduleAtFixedRate(new TimerTask(){ 
		        public void run() 
		        { 
		        	if(checkTasks()<1){
		        		runOnUiThread(new Runnable() 
		        		{
		        		   public void run() 
		        		   {
		        			  //pDialog.dismiss();
		        			  timer.cancel();
		        			  //finish();
		        		      //Toast.makeText(getApplicationContext(), "Something", Toast.LENGTH_SHORT).show();    
		        		   }
		        		}); 
		             }
		        } 
		    }, delay, period); 
    }
    void DeleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                DeleteRecursive(child);

        fileOrDirectory.delete();
    }
    public int checkTasks(){
		int tasks = 0;
		if((get_thumbnail!=null)){
			if(get_thumbnail.getStatus() == AsyncTask.Status.RUNNING){
				tasks++;
			}
		}
		if(tasks==0){
	        File mThumbsDir = new File(Environment.getExternalStorageDirectory(), AppConstants.TAG+"/decrypts");

			DeleteRecursive(mThumbsDir);
		};
		Log.d("Tasks", String.valueOf(tasks));
		return tasks;
	}
    class showList extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
		}
		protected String doInBackground(String... args) {
			
			mListProjects = Project.getAllAsList(ProjectsActivity.this, rid);
	         aaProjects = new ProjectArrayAdapter(ProjectsActivity.this, 
	           	   R.layout.list_project_row, mListProjects);
	         
			return null;
		}
	protected void onPostExecute(String file_url) {
			mListView.setAdapter(aaProjects);
				
		}
	}
    
   /* @Override
	public void onResume() {
		super.onResume();
		refreshProjects();

	}*/
/*
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
     
     }
 		
     return super.onOptionsItemSelected(item);
  
	}
    
	*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                //Intent i = new Intent(getBaseContext(), HomeActivity.class);
                //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //startActivity(i);
                finish();
            return true;
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
                /*

            AlertDialog.Builder builder = new AlertDialog.Builder(ProjectsActivity.this);
            builder.setMessage(R.string.delete_project_)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteProject (project);
                        }
                        
                    })
                    .setNegativeButton(R.string.no, null).show();
                    */
                final Dialog dialog = new Dialog(ProjectsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_delete);
                dialog.findViewById(R.id.button_ok).setOnClickListener(new OnClickListener(){

    				@Override
    				public void onClick(View v) {
    					// TODO Auto-generated method stub
    					deleteProject (project);
    					dialog.dismiss();
    				}
                	
                });
                dialog.findViewById(R.id.button_cancel).setOnClickListener(new OnClickListener(){

    				@Override
    				public void onClick(View v) {
    					// TODO Auto-generated method stub
    					dialog.dismiss();
    				}
    			});
                	
                dialog.show();
                    return false;
            }
    	    
    	});
    	
        list.setOnItemClickListener(new OnItemClickListener ()
        {

			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			
				Project project = mListProjects.get(position);
				Intent intent = new Intent(ProjectsActivity.this, ProjectActivity.class);
			    intent.putExtra("pid", project.getId());
			    startActivity(intent);
			    refreshProjects();
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
			
        }
        protected String doInBackground(GetThumbnailParams... params) {
        	Context context = params[0].context;
        	final Media media = params[0].media;
        	final Project project = params[0].project;
        	final ImageView ivIcon = params[0].ivIcon;
        	final Bitmap bmp = Media.getThumbnail(ProjectsActivity.this,media,project);
        	
            ProjectsActivity.this.runOnUiThread(new Runnable() 
                  {
                       public void run() 
                       {	
                    	   //pDialog.show();
                    	   
               				if (bmp != null)
               					ivIcon.setImageBitmap(bmp);
               				/*
               				String file = Environment.getExternalStorageDirectory()+"/"+AppConstants.TAG+"/thumbs/"+media.getId()+".jpg";
            		 		Cipher cipher;
            				try {
            					cipher = Encryption.createCipher(Cipher.ENCRYPT_MODE);
            					Encryption.applyCipher(file, file+"_", cipher);
            				}catch (Exception e) {
            					// TODO Auto-generated catch block
            					Log.e("Encryption error", e.getLocalizedMessage());
            					e.printStackTrace();
            				}
            				//Then delete original file
            				File oldfile = new File(file);
            				oldfile.delete();
            				
            				//Then remove _ on encrypted file
            				File newfile = new File(file+"_");
            				newfile.renameTo(new File(file));
               				*/
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
            			get_thumbnail = new getThumbnail();
            		 	get_thumbnail.execute(params);	
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
