package org.codeforafrica.timby;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import org.holoeverywhere.widget.*;
import org.json.JSONArray;

import org.codeforafrica.timby.R;
import org.codeforafrica.timby.model.Entity;
import org.codeforafrica.timby.model.GPSTracker;
import org.codeforafrica.timby.model.Project;
import org.codeforafrica.timby.model.Report;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;


import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;

import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;


public class ReportActivity extends BaseActivity implements OnClickListener,
OnItemLongClickListener{

	//private RadioGroup rGroup;
	//private TextView txtNewStoryDesc;
	private EditText editTextStoryName;
	private Spinner spinnerSector;
	private Spinner spinnerIssue;
	private EditText editTextDesc;
	int rid;
	String title;
	String issue;
	String sector;
	String description;
	String location;
	String entity;
	Button done;
	Button addEntity;
	String[] allEntities;
	ListView entitiesLV;
    
	ImageView setLocation;
	ImageView view;
	int story_mode;
	TextView gpsInfo;
	GPSTracker gpsT; 
	
	RelativeLayout images;
	RelativeLayout video;
	RelativeLayout audio;
	RelativeLayout gallery;
	int resultMode;
	
    private ArrayList<String> datasource;
    private MyAdapter adapter;
    private Dialog dialog;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_new_story);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        ViewGroup actionBar = (ViewGroup) getWindow().getDecorView().findViewById(getResources().getIdentifier("action_bar", "id", "android"));
        View v = actionBar.getChildAt(0);
        ActionBar.LayoutParams p = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        p.gravity= Gravity.CENTER;
        v.setLayoutParams(p);
        
        TextView title2 = (TextView) getWindow().getDecorView().findViewById(getResources().getIdentifier("action_bar_title", "id", "android"));
        title2.setTextColor(Color.parseColor("#7d4199"));
        
        //
        images = (RelativeLayout)findViewById(R.id.images);
        video = (RelativeLayout)findViewById(R.id.video);
        audio = (RelativeLayout)findViewById(R.id.audio);
        gallery = (RelativeLayout)findViewById(R.id.gallery);
        
        images.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO Auto-generated method stub
				story_mode = 2;
				resultMode = Project.STORY_TYPE_PHOTO;
				launchProject(editTextStoryName.getText().toString(), spinnerIssue.getSelectedItemPosition(),spinnerSector.getSelectedItemPosition(),datasource.toString(),editTextDesc.getText().toString(),gpsInfo.getText().toString(), false);		
				
			}
		});
        video.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO Auto-generated method stub
				story_mode = 2;
				resultMode = Project.STORY_TYPE_VIDEO;
				launchProject(editTextStoryName.getText().toString(), spinnerIssue.getSelectedItemPosition(),spinnerSector.getSelectedItemPosition(),datasource.toString(),editTextDesc.getText().toString(),gpsInfo.getText().toString(), false);		

			}
		});
        audio.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO Auto-generated method stub
				story_mode = 2;
				resultMode = Project.STORY_TYPE_AUDIO;
				launchProject(editTextStoryName.getText().toString(), spinnerIssue.getSelectedItemPosition(),spinnerSector.getSelectedItemPosition(),datasource.toString(),editTextDesc.getText().toString(),gpsInfo.getText().toString(), false);		
			
			}
		});
        
        gallery.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO Auto-generated method stub
				Intent p = new Intent(getBaseContext(), ProjectsActivity.class);
            	p.putExtra("rid", rid);
            	p.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            	startActivity(p);
			}
		});
        
        
        //txtNewStoryDesc = (TextView)findViewById(R.id.txtNewStoryDesc);
        editTextStoryName = (EditText)findViewById(R.id.editTextStoryName);
       
        addEntity = (Button)findViewById(R.id.AddEntity);
        entitiesLV = (ListView)findViewById(R.id.EntitiesList);
        
        spinnerSector = (Spinner)findViewById(R.id.spinnerSector);
        setSectors();        
        
        spinnerIssue = (Spinner)findViewById(R.id.spinnerIssue);
        setCategories();                
        
        editTextDesc = (EditText)findViewById(R.id.editTextDescription);
        
        //rGroup = (RadioGroup)findViewById(R.id.radioGroupStoryType);
        
        done = (Button)findViewById(R.id.done);
        //view = (ImageView)findViewById(R.id.view);
        setLocation = (ImageView)findViewById(R.id.imageView4);
        gpsInfo = (TextView)findViewById(R.id.textViewLocation);
        
        Intent i = getIntent();
        rid = i.getIntExtra("rid", -1);
        
        
      //entity
        datasource = new ArrayList<String>();
        adapter = new MyAdapter();
        
        entitiesLV.setAdapter(adapter);
        entitiesLV.setOnItemLongClickListener(this);
        
        setEntities();
        addEntity.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
            	 dialog = new Dialog(ReportActivity.this);
                 dialog.setContentView(R.layout.dialog_entities);
                 //Entities autocomplete 
                 
                 ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                         android.R.layout.simple_dropdown_item_1line, allEntities);
                 AutoCompleteTextView textView = (AutoCompleteTextView)dialog.findViewById(R.id.edit_box);
                 textView.setAdapter(adapter);
                dialog.findViewById(R.id.button_cancel).setOnClickListener(
                        ReportActivity.this);
                dialog.findViewById(R.id.button_ok).setOnClickListener(
                        ReportActivity.this);
                dialog.setTitle("Add Entity");
                dialog.show();
            }
        });
        
        if(rid!=-1){
        	getSupportActionBar().setTitle("Edit Report");
        	title = i.getStringExtra("title");
            sector = i.getStringExtra("sector");
            issue = i.getStringExtra("issue");
            entity = i.getStringExtra("entity");
            description = i.getStringExtra("description");
            location = i.getStringExtra("location");
            if(location.equals("0, 0")){
        		location = "Location not set";
        	}
            editTextStoryName.setText(title);
            spinnerSector.setSelection(Integer.parseInt(sector));
           
            spinnerIssue.setSelection(Integer.parseInt(issue));
            //setSelectedItem(spinnerSector, sector);
           //setSelectedItem(spinnerIssue, issue);
            editTextDesc.setText(description);
            
            /*
            String[] list1 = entity.split("\\s*,\\s*");
            for(String file: list1) {
                datasource.add(file);
            }
            
            //datasource = Arrays.asList(entity.split("\\s*,\\s*"));
            
            */
            
            ArrayList<Entity> mListEntities;
    		mListEntities = Entity.getAllAsList(this, rid);
    	 	for (int j = 0; j < mListEntities.size(); j++) {
    	 		Entity entity = mListEntities.get(j);
    	 		datasource.add(entity.getEntity());
    	 		
    	 	}
    	 	entitiesLV.setAdapter(adapter);
            
            gpsInfo.setText(location);
    
            done.setText("Update");
        }else{
        	getSupportActionBar().setTitle("Add Report");
        }
        /*
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
				//if (formValid()) {
					launchProject(editTextStoryName.getText().toString(), spinnerIssue.getSelectedItemPosition(),spinnerSector.getSelectedItemPosition(),datasource.toString(),editTextDesc.getText().toString(),gpsInfo.getText().toString(), false);		
				//}else{
					//rGroup.clearCheck();
				//}
				
			}
			
        });
        */
        
        done.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
            	
            	if (formValid()) {
					launchProject(editTextStoryName.getText().toString(), spinnerIssue.getSelectedItemPosition(),spinnerSector.getSelectedItemPosition(),datasource.toString(),editTextDesc.getText().toString(),gpsInfo.getText().toString(), true);		
            	}
            	
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
    public void setEntities(){
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	try {
    	    JSONArray jsonArray2 = new JSONArray(prefs.getString("entities", "[]"));
    	    
			for(int i=0;i<jsonArray2.length();i++)
			{
				allEntities[i]=jsonArray2.getString(i);
			}
			
    	}catch (Exception e) {
    	    e.printStackTrace();
    	}
	}
    
    public void setCategories(){
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	try {
    	    JSONArray jsonArray2 = new JSONArray(prefs.getString("categories", "[]"));
    	    ArrayList<String> list=new ArrayList<String>();
    	    list.add("Select Category");
			for(int i=0;i<jsonArray2.length();i++)
			{
				list.add(jsonArray2.getString(i));
			}
			
			ArrayAdapter<String> spinnerMenu = new ArrayAdapter<String>(getApplicationContext(),  android.R.layout.simple_list_item_1, list);
			spinnerIssue.setAdapter(spinnerMenu);
			
    	}catch (Exception e) {
    	    e.printStackTrace();
    	}
	}
    public void setSectors(){
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	try {
    	    JSONArray jsonArray2 = new JSONArray(prefs.getString("sectors", "[]"));
    	    ArrayList<String> list=new ArrayList<String>();
    	    list.add("Select Sector");
			for(int i=0;i<jsonArray2.length();i++)
			{
				list.add(jsonArray2.getString(i));
			}
			ArrayAdapter<String> spinnerMenu = new ArrayAdapter<String>(getApplicationContext(),  android.R.layout.simple_list_item_1, list);
			spinnerSector.setAdapter(spinnerMenu);
    	} catch (Exception e) {
	    	    e.printStackTrace();
	    	}
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
    	/*String pName = editTextStoryName.getText().toString();
    	
    	
    	if (pName == null || pName.length() == 0)
    	{
    		Toast.makeText(this, R.string.you_must_enter_a_project_name, Toast.LENGTH_SHORT).show();
    		return false;
    	}else if(spinnerSector.getSelectedItemPosition()==0){
    		Toast.makeText(this, "You must select a sector", Toast.LENGTH_SHORT).show();
    		return false;
    	}
    	else if(gpsInfo.getText().toString().equals("Location not set")){
    		Toast.makeText(this, "You must set location", Toast.LENGTH_SHORT).show();
    		return false;
    	}
    	else
    	{
    		return true;
    	}*/
    	return true;
    }
    /*
    private int getSelectedStoryMode ()
    {
    	   int checkedId = rGroup.getCheckedRadioButtonId();
    	   int resultMode = -1;
    	   
    	   switch (checkedId)
    	   {
    	   case R.id.radioStoryType0:
    		   resultMode = Project.STORY_TYPE_VIDEO;
    		   ((RadioButton)findViewById(R.id.radioStoryType0)).setChecked(false);
    		   break;
    	   case R.id.radioStoryType1:
    		   resultMode = Project.STORY_TYPE_PHOTO;
    		   ((RadioButton)findViewById(R.id.radioStoryType1)).setChecked(false);
    		   break;
    		   
    	   case R.id.radioStoryType2:
    		   resultMode = Project.STORY_TYPE_AUDIO;
    		   ((RadioButton)findViewById(R.id.radioStoryType2)).setChecked(false);
    		   break;
    		  	   
    	   case R.id.radioStoryType3:
    		   resultMode = Project.STORY_TYPE_ESSAY;
    		   ((RadioButton)findViewById(R.id.radioStoryType3)).setChecked(false);
    		   break;
    		   
    	   }
    	   
    	   return resultMode;
    }
    */		

    private void launchProject(String title, int pIssue, int pSector, String pEntity, String pDesc, String pLocation, boolean update) {
    	
    	
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	String currentdate = dateFormat.format(new Date());
    	
    	if(pLocation.equals("Location not set")){
    		pLocation = "0, 0";
    	}
    	if (title == null || title.length() == 0)
    	{
    		title = "Captured at "+currentdate;
    	}
    	
    	//pIssue = pIssue+1;
    	//pSector = pSector+1;
    	
    	pEntity = pEntity.replace("[", "");
    	pEntity = pEntity.replace("]", "");
    	
    	Report report;
        if(rid==-1){
        	
        	report = new Report (this, 0, title, String.valueOf(pSector), String.valueOf(pIssue), pEntity, pDesc, pLocation, "0", currentdate);
         }else{
        	report = Report.get(this, rid);
        	report.setTitle(title);
        	report.setDescription(pDesc);
        	report.setEntity(pEntity);
        	report.setIssue(String.valueOf(pIssue));
        	report.setSector(String.valueOf(pSector));
        	report.setLocation(pLocation);
        }
        report.save();
        
        rid = report.getId();
        String report_date = report.getDate();
        
        //save entities
        for(int i=0; i<datasource.size();i++){
        	String e_t = datasource.get(i);
        	
        	Entity entity;
        	entity = new Entity(this, 0, "", "", 0, "", "");
        	entity.setEntity(e_t);
        	entity.setReport(String.valueOf(rid));
        	entity.setDate(report_date);
        	entity.save();
        }
                
        if(update == false){
	        Intent intent = new Intent(getBaseContext(), StoryNewActivity.class);
	        intent.putExtra("storymode", resultMode);
	        intent.putExtra("rid", report.getId());
	        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        startActivity(intent);
        }else{
        	Toast.makeText(getBaseContext(), String.valueOf(rid)+" Updated successfully!", Toast.LENGTH_LONG).show();
        	Intent i = new Intent(getApplicationContext(), ReportsActivity.class);
        	i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	startActivity(i);
        	finish();        	
        }
         
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                Intent i = new Intent(getBaseContext(), ReportsActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    
    //entities
    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return datasource.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return datasource.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) convertView;
            if (null == view) {
                view = new TextView(ReportActivity.this);
                view.setPadding(10, 10, 10, 10);
            }
            view.setText(datasource.get(position));
            return view;
        }
    }

    @SuppressLint("NewApi")
	@Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.button_cancel:
            dialog.dismiss();
            break;

        case R.id.button_ok:
            String text = ((AutoCompleteTextView) dialog.findViewById(R.id.edit_box))
                    .getText().toString();
            if (null != text && 0 != text.compareTo("")) {
                datasource.add(text);
                
                int minHeight = datasource.size()*50;
                LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, minHeight);
                entitiesLV.setLayoutParams(params);
                                
                dialog.dismiss();
                adapter.notifyDataSetChanged();
            }

            break;
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> listView, View view,
            int position, long column) {
    	int minHeight = datasource.size()*50;
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, minHeight);
        entitiesLV.setLayoutParams(params);
        
        datasource.remove(position);
        adapter.notifyDataSetChanged();
        return true;
    }
}
