package org.codeforafrica.timby.model;

import java.util.ArrayList;
import java.util.Collections;

import org.codeforafrica.timby.StoryMakerApp;
import org.codeforafrica.timby.db.ProjectsProvider;
import org.codeforafrica.timby.db.StoryMakerDB;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class Project {
	final private String TAG = "Project";
    protected Context context;
    protected int id;
    protected String title;
    protected String report;
    protected String date;
    protected int object_id;
    protected String sequence_id; 
    protected String thumbnailPath;
    protected int storyType;
    protected String templatePath;
    
    public final static int STORY_TYPE_VIDEO = 0;
    public final static int STORY_TYPE_AUDIO = 1;
    public final static int STORY_TYPE_PHOTO = 2;
    public final static int STORY_TYPE_ESSAY = 3;
    
    // event, breaking-news, issue, feature. match category tag on server
    public final static String STORY_TEMPLATE_TYPE_EVENT = "event";
    public final static String STORY_TEMPLATE_TYPE_BREAKINGNEWS = "breaking-news";
    public final static String STORY_TEMPLATE_TYPE_ISSUE = "issue";
    public final static String STORY_TEMPLATE_TYPE_FEATURE = "feature";
    
    public String getTemplateTag ()
    {
    	String path = getTemplatePath();
    	
    	if (path != null)
    	{
    		if (path.contains("event"))
    		{
    			return STORY_TEMPLATE_TYPE_EVENT;
    		}
    		else if (path.contains("issue"))
    		{
    			return STORY_TEMPLATE_TYPE_ISSUE;
    		}
    		else if (path.contains("profile"))
    		{
    			return STORY_TEMPLATE_TYPE_FEATURE;
    		}
    		else if (path.contains("news"))
    		{
    			return STORY_TEMPLATE_TYPE_BREAKINGNEWS;
    		}
    	}
    	
    	return null;
    }
    
    public int mSceneCount = -1;
    
    public Project(Context context, int sceneCount) {
        this.context = context;
        mSceneCount = sceneCount;
    }

    public Project(Context context, int id, String title, String report, String thumbnailPath, int storyType, String date, String templatePath, int object_id, String sequence_id) {
        super();
        this.context = context;
        this.id = id;
        this.title = title;
        this.report = report;
        this.date = date;
        this.thumbnailPath = thumbnailPath;
        this.storyType = storyType;
        this.templatePath = templatePath;
        this.object_id = object_id;
        this.sequence_id = sequence_id;
    }

    public Project(Context context, Cursor cursor) {
        // FIXME use column id's directly to optimize this one schema stabilizes
        this(
                context,
                cursor.getInt(cursor
                        .getColumnIndex(StoryMakerDB.Schema.Projects.ID)),
                cursor.getString(cursor
                        .getColumnIndex(StoryMakerDB.Schema.Projects.COL_TITLE)),
                cursor.getString(cursor
                         .getColumnIndex(StoryMakerDB.Schema.Projects.COL_REPORT_ID)),
                cursor.getString(cursor
                        .getColumnIndex(StoryMakerDB.Schema.Projects.COL_THUMBNAIL_PATH)),
                cursor.getInt(cursor
                        .getColumnIndex(StoryMakerDB.Schema.Projects.COL_STORY_TYPE)),
                cursor.getString(cursor
                        .getColumnIndex(StoryMakerDB.Schema.Projects.COL_DATE)),
                cursor.getString(cursor
                        .getColumnIndex(StoryMakerDB.Schema.Projects.COL_TEMPLATE_PATH)),
                cursor.getInt(cursor
                        .getColumnIndex(StoryMakerDB.Schema.Projects.COL_OBJECT_ID)),
                cursor.getString(cursor
                        .getColumnIndex(StoryMakerDB.Schema.Projects.COL_SEQUENCE_ID)));

        calculateMaxSceneCount();

    }
    
    private void calculateMaxSceneCount ()
    {
        Cursor cursor = getScenesAsCursor();
        
        int projectIndex = 0;
        
        mSceneCount = cursor.getCount();
        
// FIXME CLEANUP --- not sure why this was calculated this way, but for now I am just using count
//        if (cursor.moveToFirst()) {
//            do {
//                Scene scene = new Scene(context, cursor);
//                projectIndex = Math.max(projectIndex, scene.getProjectIndex());
//            } while (cursor.moveToNext());
//        }
        
//        mSceneCount = projectIndex + 1; //size is one higher than max index
        
        cursor.close();
        
    }

    /***** Table level static methods *****/

    public static Cursor getAsCursor(Context context, int id) {
        String selection = StoryMakerDB.Schema.Projects.ID + "=?";
        String[] selectionArgs = new String[] { "" + id };
        return context.getContentResolver().query(
                ProjectsProvider.PROJECTS_CONTENT_URI, null, selection,
                selectionArgs, null);
    }

    public static Project get(Context context, int id) {
        Cursor cursor = Project.getAsCursor(context, id);
        Project project = null;
        
        if (cursor.moveToFirst()) {
            project = new Project(context, cursor);
           
        } 
        
        cursor.close();
        return project;
    }

    public static Cursor getAllAsCursor(Context context, int rid) {
    	String selection = StoryMakerDB.Schema.Projects.COL_REPORT_ID + "=?";
        String[] selectionArgs = new String[] { "" + rid };
        return context.getContentResolver().query(
                ProjectsProvider.PROJECTS_CONTENT_URI, null, selection,
                selectionArgs, null);
        /*
        return context.getContentResolver().query(
                ProjectsProvider.PROJECTS_CONTENT_URI, null, null, null, null);
                */
    }

    public static ArrayList<Project> getAllAsList(Context context, int rid) {
        ArrayList<Project> projects = new ArrayList<Project>();
        Cursor cursor = getAllAsCursor(context, rid);
        if (cursor.moveToFirst()) {
            do {
                projects.add(new Project(context, cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return projects;
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
        values.put(StoryMakerDB.Schema.Projects.COL_REPORT_ID, report);
        values.put(StoryMakerDB.Schema.Projects.COL_THUMBNAIL_PATH, thumbnailPath);
        values.put(StoryMakerDB.Schema.Projects.COL_STORY_TYPE, storyType);
        values.put(StoryMakerDB.Schema.Projects.COL_DATE, date);
        values.put(StoryMakerDB.Schema.Projects.COL_TEMPLATE_PATH, templatePath);
        values.put(StoryMakerDB.Schema.Projects.COL_OBJECT_ID, object_id);
        values.put(StoryMakerDB.Schema.Projects.COL_SEQUENCE_ID, sequence_id);
        
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

    public ArrayList<Scene> getScenesAsList() {
        Cursor cursor = getScenesAsCursor();
        
        ArrayList<Scene> scenes = new ArrayList<Scene>(mSceneCount);
        
        for (int i = 0; i < mSceneCount; i++)
            scenes.add(null);
        
        if (cursor.moveToFirst()) {
            do {
                Scene scene = new Scene(context, cursor);
                scenes.set(scene.getProjectIndex(), scene);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return scenes;
    }

    public Scene[] getScenesAsArray() {
        ArrayList<Scene> scenes = getScenesAsList();
        return scenes.toArray(new Scene[] {});
    }

    public Cursor getScenesAsCursor() {
        String selection = "project_id=?";
        String[] selectionArgs = new String[] { "" + getId() };
        String orderBy = "project_index";
        return context.getContentResolver().query(
                ProjectsProvider.SCENES_CONTENT_URI, null, selection,
                selectionArgs, orderBy);
    }
    
    public ArrayList<Media> getMediaAsList() {
        ArrayList<Media> mediaList = null;
        mediaList = new ArrayList<Media>();
        for (Scene s : getScenesAsArray()) {
            mediaList.addAll(s.getMediaAsList());
        }
        return mediaList;
    }
    
    public String[] getMediaAsPathArray() {
        ArrayList<Media> mediaList = getMediaAsList();

        // purge nulls
        mediaList.removeAll(Collections.singleton(null));
        
        String[] pathArray = new String[mediaList.size()];
        for (int i = 0 ; i < mediaList.size() ; i++) {
            pathArray[i] = mediaList.get(i).getPath(); // how this makes me long for python
        }
        return pathArray;
    }

    /**
     * @param media insert this scene into the projects scene list at index 
     */
    public void setScene(int projectIndex, Scene scene) {
        scene.setProjectIndex(projectIndex);
        scene.setProjectId(getId());
        scene.save();
        
        mSceneCount = Math.max((projectIndex+1), mSceneCount);
    }
    
    
    public boolean isTemplateStory() {
        return (templatePath != null) && !templatePath.equals(""); 
    }
    /***** getters and setters *****/

    /**
     * @return the id
     */
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
   
    public String getReport() {
        return report;
    }
    /**
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }
    public void setReport(String report) {
        this.report = report;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    /**
     * @return the thumbnailPath
     */
    public String getThumbnailPath() {
        return thumbnailPath;
    }

    /**
     * @param thumbnailPath
     *            the thumbnailPath to set
     */
    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public int getStoryType() {
        return storyType;
    }

    public void setStoryType(int storyType) {
        this.storyType = storyType;
    }

    public String getTemplatePath() {
        return templatePath;
    }
    
    public void setTemplatePath(String template) {
        this.templatePath = template;
    }
    public void setObjectID(int objectid) {
        this.object_id = objectid;
    }
    public void setSequenceId(String sequence) {
        this.sequence_id = sequence;
    }
    public int getObjectID() {
        return object_id;
    }
    public String getSequenceId() {
        return sequence_id;
    }
    public static String getSimpleTemplateForMode (int storyMode)
    {
    	 String lang = StoryMakerApp.getCurrentLocale().getLanguage();

         String templateJsonPath = "story/templates/" + lang + "/simple/";
         
         switch (storyMode)
         {
         
         case Project.STORY_TYPE_VIDEO:
             templateJsonPath += "video_simple.json";
             break;
         case Project.STORY_TYPE_AUDIO:
             templateJsonPath += "audio_simple.json";
             break;
         case Project.STORY_TYPE_PHOTO:
             templateJsonPath += "photo_simple.json";
             break;
         case Project.STORY_TYPE_ESSAY:
             templateJsonPath += "essay_simple.json";
             break;
             
         }
         
         return templateJsonPath;
    }
    
}
