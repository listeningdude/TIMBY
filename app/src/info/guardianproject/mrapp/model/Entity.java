package info.guardianproject.mrapp.model;

import java.util.ArrayList;
import java.util.Collections;

import info.guardianproject.mrapp.StoryMakerApp;
import info.guardianproject.mrapp.db.EntitiesProvider;
import info.guardianproject.mrapp.db.StoryMakerDB;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class Entity {
	final private String TAG = "Entity";
    protected Context context;
    protected int id;
    protected String report_id;
    protected String entity;
    protected int object_id;
    protected String sequence_id; 
    protected String date;

    public Entity(Context context, int id, String report, String entity, int object_id, String sequence_id, String date) {
        super();
        this.context = context;
        this.id = id;
        this.report_id = report;
        this.entity = entity;
        this.object_id = object_id;
        this.sequence_id = sequence_id;
        this.date = date;
    }

    public Entity(Context context, Cursor cursor) {
        // FIXME use column id's directly to optimize this one schema stabilizes
        this(
                context,
                cursor.getInt(cursor
                        .getColumnIndex(StoryMakerDB.Schema.Entities.ID)),
                cursor.getString(cursor
                         .getColumnIndex(StoryMakerDB.Schema.Entities.COL_REPORT_ID)),
                cursor.getString(cursor
                        .getColumnIndex(StoryMakerDB.Schema.Entities.COL_ENTITY)),
                cursor.getInt(cursor
                        .getColumnIndex(StoryMakerDB.Schema.Entities.COL_OBJECT_ID)),
                cursor.getString(cursor
                        .getColumnIndex(StoryMakerDB.Schema.Entities.COL_SEQUENCE_ID)),
                cursor.getString(cursor
                        .getColumnIndex(StoryMakerDB.Schema.Entities.COL_DATE)));

       // calculateMaxSceneCount();

    }
    
    
    public static Cursor getAsCursor(Context context, int id) {
        String selection = StoryMakerDB.Schema.Entities.ID + "=?";
        String[] selectionArgs = new String[] { "" + id };
        return context.getContentResolver().query(
                EntitiesProvider.ENTITIES_CONTENT_URI, null, selection,
                selectionArgs, null);
    }

    public static Entity get(Context context, int id) {
        Cursor cursor = Entity.getAsCursor(context, id);
        Entity entity = null;
        
        if (cursor.moveToFirst()) {
            entity = new Entity(context, cursor);
           
        } 
        
        cursor.close();
        return entity;
    }

    public static Cursor getAllAsCursor(Context context, int rid) {
    	String selection = StoryMakerDB.Schema.Entities.COL_REPORT_ID + "=?";
        String[] selectionArgs = new String[] { "" + rid };
        return context.getContentResolver().query(
                EntitiesProvider.ENTITIES_CONTENT_URI, null, selection,
                selectionArgs, null);
        /*
        return context.getContentResolver().query(
                EntitiesProvider.PROJECTS_CONTENT_URI, null, null, null, null);
                */
    }

    public static ArrayList<Entity> getAllAsList(Context context, int rid) {
        ArrayList<Entity> entities = new ArrayList<Entity>();
        Cursor cursor = getAllAsCursor(context, rid);
        if (cursor.moveToFirst()) {
            do {
                entities.add(new Entity(context, cursor));
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        return entities;
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
        values.put(StoryMakerDB.Schema.Entities.COL_REPORT_ID, report_id);
        values.put(StoryMakerDB.Schema.Entities.COL_ENTITY, entity);
        values.put(StoryMakerDB.Schema.Entities.COL_OBJECT_ID, object_id);
        values.put(StoryMakerDB.Schema.Entities.COL_SEQUENCE_ID, sequence_id);
        values.put(StoryMakerDB.Schema.Entities.COL_DATE, date);
        
        return values;
    }
    private void insert() {
        ContentValues values = getValues();
        Uri uri = context.getContentResolver().insert(
                EntitiesProvider.ENTITIES_CONTENT_URI, values);
        String lastSegment = uri.getLastPathSegment();
        int newId = Integer.parseInt(lastSegment);
        this.setId(newId);
    }
    
    private void update() {
    	Uri uri = EntitiesProvider.ENTITIES_CONTENT_URI.buildUpon().appendPath("" + id).build();
        String selection = StoryMakerDB.Schema.Entities.ID + "=?";
        String[] selectionArgs = new String[] { "" + id };
    	ContentValues values = getValues();
        int count = context.getContentResolver().update(
                uri, values, selection, selectionArgs);
        // FIXME make sure 1 row updated
    }
    
    public void delete() {
    	Uri uri = EntitiesProvider.ENTITIES_CONTENT_URI.buildUpon().appendPath("" + id).build();
        String selection = StoryMakerDB.Schema.Entities.ID + "=?";
        String[] selectionArgs = new String[] { "" + id };
        int count = context.getContentResolver().delete(
                uri, selection, selectionArgs);
        Log.d(TAG, "deleted entity: " + id + ", rows deleted: " + count);
        // FIXME make sure 1 row updated
        
        //TODO should we also delete all media files associated with this entity?
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

   
    public String getReport() {
        return report_id;
    }
    public String getEntity() {
        return entity;
    }
    public int getObjectID() {
        return object_id;
    }
    public String getSequenceId() {
        return sequence_id;
    }
    public String getDate() {
        return date;
    }

    /**
     * @param title
     *            the title to set
     */
    public void setReport(String report) {
        this.report_id = report;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setObjectID(int objectid) {
        this.object_id = objectid;
    }
    public void setSequenceId(String sequence) {
        this.sequence_id = sequence;
    }

	
}
