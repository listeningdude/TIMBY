package info.guardianproject.mrapp.model;

import java.util.ArrayList;
import java.util.Collections;

import info.guardianproject.mrapp.StoryMakerApp;
import info.guardianproject.mrapp.db.ProjectsProvider;
import info.guardianproject.mrapp.db.StoryMakerDB;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class Report {
	final private String TAG = "Report";
    protected Context context;
    protected int id;
    protected String title;
    protected String _sector;
    protected String _issue;
    protected String _entity;
    protected String _description;
    protected String _location;

    
    
    
   
    public Report(Context context, int id, String title, String _sector, String _issue,String _entity, String _description, String _location) {
        super();
        this.context = context;
        this.id = id;
        this.title = title;
        this._sector = _sector;
        this._issue = _issue;
        this._entity = _entity;
        this._description = _description;
        this._location = _location;
    }

    public Report(Context context, Cursor cursor) {
        // FIXME use column id's directly to optimize this one schema stabilizes
        this(
                context,
                cursor.getInt(cursor
                        .getColumnIndex(StoryMakerDB.Schema.Projects.ID)),
                cursor.getString(cursor
                        .getColumnIndex(StoryMakerDB.Schema.Projects.COL_TITLE)),
                cursor.getString(cursor
                         .getColumnIndex(StoryMakerDB.Schema.Projects.COL_SECTOR)),
                cursor.getString(cursor
                        .getColumnIndex(StoryMakerDB.Schema.Projects.COL_ISSUE)),
                cursor.getString(cursor
                        .getColumnIndex(StoryMakerDB.Schema.Projects.COL_ENTITY)),
                cursor.getString(cursor
                        .getColumnIndex(StoryMakerDB.Schema.Projects.COL_DESCRIPTION)),
               cursor.getString(cursor
                          .getColumnIndex(StoryMakerDB.Schema.Projects.COL_LOCATION));
        
                cursor.close();

    }
    
   
    /***** Table level static methods *****/

    public static Cursor getAsCursor(Context context, int id) {
        String selection = StoryMakerDB.Schema.Reports.ID + "=?";
        String[] selectionArgs = new String[] { "" + id };
        return context.getContentResolver().query(
                ProjectsProvider.PROJECTS_CONTENT_URI, null, selection,
                selectionArgs, null);
    }

    public static Report get(Context context, int id) {
        Cursor cursor = Report.getAsCursor(context, id);
        Report project = null;
        
        if (cursor.moveToFirst()) {
            project = new Report(context, cursor);
           
        } 
        
        cursor.close();
        return project;
    }

    public static Cursor getAllAsCursor(Context context) {
        return context.getContentResolver().query(
                ProjectsProvider.PROJECTS_CONTENT_URI, null, null, null, null);
    }

    public static ArrayList<Report> getAllAsList(Context context) {
        ArrayList<Report> reports = new ArrayList<Report>();
        Cursor cursor = getAllAsCursor(context);
        if (cursor.moveToFirst()) {
            do {
                reports.add(new Report(context, cursor));
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        return reports;
    }

    /***** Object level methods *****/

    public void save() {
    	Cursor cursor = getAsCursor(context, id);
    	if (cursor.getCount() == 0) {
    		insert();
    	} else {
    		update();
    	}
    	
    	cursor.close();
    }
    
    private ContentValues getValues() {
        ContentValues values = new ContentValues();
        values.put(StoryMakerDB.Schema.Projects.COL_TITLE, title);
        values.put(StoryMakerDB.Schema.Projects.COL_SECTOR, _sector);
        values.put(StoryMakerDB.Schema.Projects.COL_ISSUE, _issue);
        values.put(StoryMakerDB.Schema.Projects.COL_ENTITY, _entity);
        values.put(StoryMakerDB.Schema.Projects.COL_DESCRIPTION, _description);
        values.put(StoryMakerDB.Schema.Projects.COL_LOCATION, _location);
        
        return values;
    }
    private void insert() {
        ContentValues values = getValues();
        Uri uri = context.getContentResolver().insert(
                ProjectsProvider.PROJECTS_CONTENT_URI, values);
        String lastSegment = uri.getLastPathSegment();
        int newId = Integer.parseInt(lastSegment);
        this.setId(newId);
    }
    
    private void update() {
    	Uri uri = ProjectsProvider.PROJECTS_CONTENT_URI.buildUpon().appendPath("" + id).build();
        String selection = StoryMakerDB.Schema.Projects.ID + "=?";
        String[] selectionArgs = new String[] { "" + id };
    	ContentValues values = getValues();
        int count = context.getContentResolver().update(
                uri, values, selection, selectionArgs);
        // FIXME make sure 1 row updated
    }
    
    public void delete() {
    	Uri uri = ProjectsProvider.PROJECTS_CONTENT_URI.buildUpon().appendPath("" + id).build();
        String selection = StoryMakerDB.Schema.Projects.ID + "=?";
        String[] selectionArgs = new String[] { "" + id };
        int count = context.getContentResolver().delete(
                uri, selection, selectionArgs);
        Log.d(TAG, "deleted project: " + id + ", rows deleted: " + count);
        // FIXME make sure 1 row updated
        
        //TODO should we also delete all media files associated with this project?
    }

   
    public int getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }
    public String getIssue() {
        return _issue;
    }
    public String getSector() {
        return _sector;
    }
    public String getEntity() {
        return _entity;
    }
    public String getDescription() {
        return _description;
    }
    public String getLocation() {
        return _location;
    }
    /**
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }
    public void setIssue(String _issue) {
        this._issue = _issue;
    }
    public void setSector(String _sector) {
        this._sector = _sector;
    }
    public void setDescription(String _description) {
        this._description = _description;
    }
    public void setEntity(String _entity) {
        this._entity = _entity;
    }
    public void setLocation(String _location) {
        this._location = _location;
    }
        
}
