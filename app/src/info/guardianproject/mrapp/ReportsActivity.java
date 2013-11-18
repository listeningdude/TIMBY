package info.guardianproject.mrapp;

import info.guardianproject.mrapp.model.Media;
import info.guardianproject.mrapp.model.Project;
import info.guardianproject.mrapp.model.Report;
import info.guardianproject.mrapp.model.Report;

import java.util.ArrayList;

import org.holoeverywhere.app.AlertDialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

public class ReportsActivity extends BaseActivity {
	ListView mListView;
	private ArrayList<Report> mListReports;
	private ReportArrayAdapter aaReports;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);
        // action bar stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#9E3B33")));
        
        mListView = (ListView)findViewById(R.id.projectslist);
        initListView(mListView);
    }
    @Override
	protected void onResume() {
		super.onResume();
		refreshReports();
	}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_reports, menu);
        return true;
    }
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
         case android.R.id.home:

	        	NavUtils.navigateUpFromSameTask(this);
	        	
             return true;
         case R.id.menu_new_project:
 		
			 startActivity(new Intent(this, ReportActivity.class));

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
		if (changed){
			startActivity(new Intent(this,ReportsActivity.class));
			
			finish();
			
		}
	}

	
    public void initListView (ListView list) {
    
    	list.setOnItemLongClickListener(new OnItemLongClickListener(){

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            final Report report = mListReports.get(arg2);
            AlertDialog.Builder builder = new AlertDialog.Builder(ReportsActivity.this);
            builder.setMessage(R.string.delete_project_)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteReport (report);
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
			
				Report report = mListReports.get(position);
				Intent intent = null;
				intent = new Intent(ReportsActivity.this, ReportActivity.class);
				intent.putExtra("title",report.getTitle());
				intent.putExtra("issue", report.getIssue());
                intent.putExtra("sector", report.getSector());
                intent.putExtra("description", report.getDescription());
                intent.putExtra("entity", report.getEntity());
                intent.putExtra("location", report.getLocation());
                intent.putExtra("rid", report.getId());
		        startActivity(intent);
			}
        	
        });
       
        
       refreshReports();
        
    }
    
    public void refreshReports ()
    {
    	
    	 mListReports = Report.getAllAsList(this);
         aaReports = new ReportArrayAdapter(this, R.layout.list_project_row, mListReports);
         
         mListView.setAdapter(aaReports);
    }
    
    private void deleteReport (Report report)
    {
    	mListReports.remove(report);
        aaReports.notifyDataSetChanged();
        
    	report.delete();
    	
    	//should we delete report folders here too?
    }
    
    class ReportArrayAdapter extends ArrayAdapter {
    	
    	Context context; 
        int layoutResourceId;    
        ArrayList<Report> reports;
        
        public ReportArrayAdapter(Context context, int layoutResourceId,ArrayList<Report> reports) {
            super(context, layoutResourceId, reports);        
            
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.reports = reports;
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
            
            Report report = reports.get(position);
            int totalprojects = Project.getAllAsList(((Activity)context), report.getId()).size();
            tv.setText(report.getTitle());       
            
            tv = (TextView)row.findViewById(R.id.description);
            
        	tv.setText(String.valueOf(totalprojects)+" files(s)");   	
            
            return row;
        }
        
    }


   
    
    
}
