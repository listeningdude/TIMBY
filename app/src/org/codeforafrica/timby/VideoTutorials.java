package org.codeforafrica.timby;

import java.io.File;
import java.util.ArrayList;

import org.holoeverywhere.app.AlertDialog;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
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

public class VideoTutorials extends BaseActivity 
{
	ListView mListView;
	File mFileExternDir;
	//private ArrayList<Project> mListProjects;
	private VideosArrayAdapter vaProjects;
	ArrayList<String> mListProjects = new ArrayList<String>();
	
	// Progress Dialog
	public static final int progress_bar_type = 0;

	// File url to download

public void onCreate(Bundle savedInstanceState) 
{
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_projects);
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    mFileExternDir = new File(Environment.getExternalStorageDirectory(), "TIMBY_Tutorials");

    mListView = (ListView)findViewById(R.id.projectslist);
    initListView(mListView);    
}

public void initListView (ListView list) {
    
	list.setOnItemLongClickListener(new OnItemLongClickListener(){

        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            
            
           // final Project project = mListProjects.get(arg2);


        AlertDialog.Builder builder = new AlertDialog.Builder(VideoTutorials.this);
        builder.setMessage(R.string.delete_project_)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        
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
		
			//Project project = mListProjects.get(position);
			//Intent intent = null;
		}
    	
    });
    
   refreshProjects();
    
}

public void refreshProjects ()
{
	File file[] = mFileExternDir.listFiles();
	for (int i=0; i < file.length; i++)
	{
		mListProjects.add(file[i].getName());
	}
    vaProjects = new VideosArrayAdapter(VideoTutorials.this, R.layout.list_project_row, mListProjects);
	mListView.setAdapter(vaProjects);
}

class VideosArrayAdapter extends ArrayAdapter {
	
	Context context; 
    int layoutResourceId;    
    ArrayList<String> projects;
    
    public VideosArrayAdapter(Context context, int layoutResourceId,ArrayList<String> mListProjects) {
        super(context, layoutResourceId, mListProjects);        
        
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.projects = mListProjects;
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
        
                
        tv.setText("");       
        
        tv = (TextView)row.findViewById(R.id.description);
        
        ImageView ivType = (ImageView)row.findViewById(R.id.cardIcon);
        ImageView ivIcon = (ImageView)row.findViewById(R.id.imageView1);
        
        Bitmap bmp = null;
		
        if (bmp!= null){
			ivIcon.setImageBitmap(bmp);
		}
        
        tv.setText(projects.get(position));
        
        ivType.setImageDrawable(getContext().getResources().getDrawable(R.drawable.btn_toggle_ic_list_video));
    	ivType.setVisibility(View.GONE);
    	
        return row;
    	}
	}
}
