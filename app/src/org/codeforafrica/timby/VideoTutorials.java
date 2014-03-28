package org.codeforafrica.timby;

import java.io.File;
import java.util.ArrayList;

import org.holoeverywhere.app.AlertDialog;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.util.Log;
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
    setContentView(R.layout.activity_video_tutorials);
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    mFileExternDir = new File(Environment.getExternalStorageDirectory(), "TIMBY_Tutorials");

    mListView = (ListView)findViewById(R.id.videoslist);
    initListView(mListView);    
}

public void initListView (ListView list) {
	
    list.setOnItemClickListener(new OnItemClickListener ()
    {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			String vidPath = "/TIMBY_Tutorials/"+mListProjects.get(position);
			
			Intent intent = new Intent(Intent.ACTION_VIEW);
			File sdCard = Environment.getExternalStorageDirectory();
			File file = new File(sdCard, vidPath);
			intent.setDataAndType(Uri.fromFile(file), "video/*");
			startActivity(intent);
		}
    	
    });
    
   refreshProjects();
    
}

public void refreshProjects ()
{
	File file[] = mFileExternDir.listFiles();
	for (int i=0; i < file.length; i++)
	{
		if(!file[i].getName().equals("thumbs")){
			mListProjects.add(file[i].getName());
		}
	}
    vaProjects = new VideosArrayAdapter(VideoTutorials.this, R.layout.list_video_row, mListProjects);
	mListView.setAdapter(vaProjects);
}
@Override
public boolean onCreateOptionsMenu(Menu menu) {
    getSupportMenuInflater().inflate(R.menu.activity_lessons, menu);
    
    return true;
}


@Override
public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
       
        case R.id.menu_update:
            updateLessons();
            return true;
    }
    return super.onOptionsItemSelected(item);
}

public void updateLessons(){
	//check if updated
	 //true:delete everything
		//start video service
	//false:give notification
	
	File tutorials = new File(Environment.getExternalStorageDirectory(), "TIMBY_Tutorials");
	DeleteRecursive(tutorials);
	
    startService(new Intent(VideoTutorials.this,VideoTutorialsService.class));
    
   //Intent i = new Intent(getApplicationContext(),HomeActivity.class);
	//i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	//startActivity(i);
	
	finish();
}

void DeleteRecursive(File fileOrDirectory) {
    if (fileOrDirectory.isDirectory())
        for (File child : fileOrDirectory.listFiles())
            DeleteRecursive(child);

    fileOrDirectory.delete();
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
        
        
        String[] thumbParts = projects.get(position).split("mp4");
		String thumbName = thumbParts[0]+"jpg";
		
        String imagePath = Environment.getExternalStorageDirectory().toString()+"/TIMBY_Tutorials/thumbs/"+thumbName;
        
        
        Bitmap bmp = BitmapFactory.decodeFile(imagePath);
		
        if (bmp!= null){
			ivIcon.setImageBitmap(bmp);
		}
        //String[] iPc = projects.get(position).split("-");
        
        //tv.setText(iPc[0]);
        tv.setText(projects.get(position));
        
        //ivType.setImageDrawable(getContext().getResources().getDrawable(R.drawable.btn_toggle_ic_list_video));
    	ivType.setVisibility(View.GONE);
    	
        return row;
    	}
	}
}
