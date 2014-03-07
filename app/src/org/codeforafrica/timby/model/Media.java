package org.codeforafrica.timby.model;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.ffmpeg.android.MediaUtils;


import org.codeforafrica.timby.AppConstants;
import org.codeforafrica.timby.EncryptionService;
import org.codeforafrica.timby.R;
import org.codeforafrica.timby.db.ProjectsProvider;
import org.codeforafrica.timby.db.StoryMakerDB;
import org.codeforafrica.timby.media.Encryption;
import org.codeforafrica.timby.media.MediaProjectManager;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class Media {
	private static final String TAG = "Media";
	
    protected Context context;
    protected int id;
    protected String path;
    protected String mimeType;
    protected String clipType; // R.arrays.cliptypes
    protected int clipIndex; // which clip is this in the scene
    protected int sceneId; // foreign key to the Scene which holds this media
    protected float trimStart;
    protected float trimEnd;
    protected float duration;
    protected String encrypted;
    public final static int IMAGE_SAMPLE_SIZE = 4;
    
    public Media(Context context) {
        this.context = context;
    }

    public Media(Context context, int id, String path, String mimeType, String clipType, int clipIndex,
            int sceneId, float trimStart, float trimEnd, float duration, String encrypted) {
        super();
        this.context = context;
        this.id = id;
        this.path = path;
        this.mimeType = mimeType;
        this.clipType = clipType;
        this.clipIndex = clipIndex;
        this.sceneId = sceneId;
        this.trimStart = trimStart;
        this.trimEnd = trimEnd;
        this.duration = duration;
        this.encrypted = encrypted;
    }

    public Media(Context context, Cursor cursor) {
        // FIXME use column id's directly to optimize this one schema stabilizes
        this(
                context,
                cursor.getInt(cursor
                        .getColumnIndex(StoryMakerDB.Schema.Media.ID)),
                cursor.getString(cursor
                        .getColumnIndex(StoryMakerDB.Schema.Media.COL_PATH)),
                cursor.getString(cursor
                        .getColumnIndex(StoryMakerDB.Schema.Media.COL_MIME_TYPE)),
                cursor.getString(cursor
                        .getColumnIndex(StoryMakerDB.Schema.Media.COL_CLIP_TYPE)),
                cursor.getInt(cursor
                        .getColumnIndex(StoryMakerDB.Schema.Media.COL_CLIP_INDEX)),
                cursor.getInt(cursor
                        .getColumnIndex(StoryMakerDB.Schema.Media.COL_SCENE_ID)),
                cursor.getInt(cursor
                        .getColumnIndex(StoryMakerDB.Schema.Media.COL_TRIM_START)),
                cursor.getInt(cursor
                        .getColumnIndex(StoryMakerDB.Schema.Media.COL_TRIM_END)),
                cursor.getInt(cursor
                        .getColumnIndex(StoryMakerDB.Schema.Media.COL_DURATION)),
                cursor.getString(cursor
                        .getColumnIndex(StoryMakerDB.Schema.Media.COL_ENCRYPTED)));
    }

    /***** Table level static methods *****/

    public static Cursor getAsCursor(Context context, int id) {
        String selection = StoryMakerDB.Schema.Media.ID + "=?";
        String[] selectionArgs = new String[] { "" + id };
        return context.getContentResolver().query(
                ProjectsProvider.MEDIA_CONTENT_URI, null, selection,
                selectionArgs, null);
    }

    public static Media get(Context context, int id) {
        Cursor cursor = Media.getAsCursor(context, id);
        if (cursor.moveToFirst()) {
            return new Media(context, cursor);
        } else {
            return null;
        }
    }
    
    /***** Calculated object level methods *****/

    /** 
     * @return 0.0-1.0 percent into the clip to start play
     */
    public float getTrimmedStartPercent() {
        return (trimStart + 1) / 100F;
    }

    /** 
     * @return 0.0-1.0 percent into the clip to end play
     */
    public float getTrimmedEndPercent() {
        return (trimEnd + 1) / 100F;
    }

    /** 
     * @return milliseconds into clip trimmed clip to start playback
     */
    public int getTrimmedStartTime() {
        return Math.round(getTrimmedStartPercent() * duration);
    }

    public float getTrimmedStartTimeFloat() {
        return (getTrimmedStartPercent() * duration);
    }
    /** 
     * @return milliseconds to end of trimmed clip
     */
    public int getTrimmedEndTime() {
        return Math.round(getTrimmedEndPercent() * duration);
    }
    
    public float getTrimmedEndTimeFloat() {
        return (getTrimmedEndPercent() * duration);
    }

    /** 
     * @return milliseconds trimmed clip will last
     */
    public int getTrimmedDuration() {
        return getTrimmedEndTime() - getTrimmedStartTime();
    }
    
    /***** Object level methods *****/


    /*
     * gets media in scene at location clipIndex
     */
    public static Cursor getAsCursor(Context context, int sceneId, int clipIndex) {
        String selection = StoryMakerDB.Schema.Media.COL_SCENE_ID + "=? and " +
        		StoryMakerDB.Schema.Media.COL_CLIP_INDEX + "=?";
        String[] selectionArgs = new String[] { "" + sceneId, "" + clipIndex };
        return context.getContentResolver().query(
                ProjectsProvider.MEDIA_CONTENT_URI, null, selection,
                selectionArgs, null);
    }
    public static Cursor getUnEncryptedAsCursor(Context context) {
    	String selection = StoryMakerDB.Schema.Media.COL_ENCRYPTED + "!=?";
    	String unenc = "1";
        String[] selectionArgs = new String[] { "" + unenc };
        return context.getContentResolver().query(
                ProjectsProvider.MEDIA_CONTENT_URI, null, selection,
                selectionArgs, null);
    }
    
    public static ArrayList<Media> getUnEncrypted(Context context) {
    	Cursor cursor = getUnEncryptedAsCursor(context);
        ArrayList<Media> medias = new ArrayList<Media>();
        if (cursor.moveToFirst()) {
            do {
                medias.add(new Media(context, cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return medias;
    }
    /*
     * gets media in scene at location clipIndex
     */
    public static Media get(Context context, int sceneId, int clipIndex) {
        Cursor cursor = Media.getAsCursor(context, sceneId, clipIndex);
        if (cursor.moveToFirst()) {
            return new Media(context, cursor);
        } else {
            return null;
        }
    }

    public static Cursor getAllAsCursor(Context context) {
        return context.getContentResolver().query(
                ProjectsProvider.MEDIA_CONTENT_URI, null, null, null, null);
    }

    public static ArrayList<Media> getAllAsList(Context context) {
        ArrayList<Media> medias = new ArrayList<Media>();
        Cursor cursor = getAllAsCursor(context);
        if (cursor.moveToFirst()) {
            do {
                medias.add(new Media(context, cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return medias;
    }

    /***** Object level methods *****/
    
    public void save() {
    	Cursor cursor = getAsCursor(context, id);
    	if (cursor.getCount() == 0) {
    		cursor.close();
    		insert();
    	} else {
    		cursor.close();
    		update();    		
    	}
    	
    	
    }
    
    private ContentValues getValues() {
        ContentValues values = new ContentValues();
        values.put(StoryMakerDB.Schema.Media.COL_PATH, path);
        values.put(StoryMakerDB.Schema.Media.COL_MIME_TYPE, mimeType);
        values.put(StoryMakerDB.Schema.Media.COL_CLIP_TYPE, clipType);
        values.put(StoryMakerDB.Schema.Media.COL_CLIP_INDEX, clipIndex);
        values.put(StoryMakerDB.Schema.Media.COL_SCENE_ID, sceneId);
        values.put(StoryMakerDB.Schema.Media.COL_TRIM_START, trimStart);
        values.put(StoryMakerDB.Schema.Media.COL_TRIM_END, trimEnd);
        values.put(StoryMakerDB.Schema.Media.COL_DURATION, duration);
        values.put(StoryMakerDB.Schema.Media.COL_ENCRYPTED, encrypted);
        return values;
    }
    
    private void insert() {
    	// There can be only one!  check if a media item exists at this location already, if so purge it first.
    	Cursor cursorDupes = getAsCursor(context, sceneId, clipIndex);
    	if ((cursorDupes.getCount() > 0) && cursorDupes.moveToFirst()) {
        	// FIXME we should allow audio clips to remain so they can be mixed down with their buddies
    		do {
    			(new Media(context, cursorDupes)).delete();
    		} while (cursorDupes.moveToNext());
    	}
    	
        ContentValues values = getValues();
        Uri uri = context.getContentResolver().insert(
                ProjectsProvider.MEDIA_CONTENT_URI, values);
        String lastSegment = uri.getLastPathSegment();
        int newId = Integer.parseInt(lastSegment);
        this.setId(newId);
        
        cursorDupes.close();
    }
    
    private void update() {
    	Uri uri = ProjectsProvider.MEDIA_CONTENT_URI.buildUpon().appendPath("" + id).build();
        String selection = StoryMakerDB.Schema.Media.ID + "=?";
        String[] selectionArgs = new String[] { "" + id };
    	ContentValues values = getValues();
        int count = context.getContentResolver().update(
                uri, values, selection, selectionArgs);
        // FIXME make sure 1 row updated
    }
    
    public void delete() {
    	Uri uri = ProjectsProvider.MEDIA_CONTENT_URI.buildUpon().appendPath("" + id).build();
        String selection = StoryMakerDB.Schema.Media.ID + "=?";
        String[] selectionArgs = new String[] { "" + id };
        int count = context.getContentResolver().delete(
                uri, selection, selectionArgs);
        Log.d(TAG, "deleted media: " + id + ", rows deleted: " + count);
        // FIXME make sure 1 row updated
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
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path
     *            the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }
    
    public void setEncrypted(String encrypted) {
        this.encrypted = encrypted;
    }
    public String getEncrypted() {
        return encrypted;
    }
    /**
     * @return the mimeType
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * @param mimeType
     *            the mimeType to set
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * @return the sceneId
     */
    public int getSceneId() {
        return sceneId;
    }

    /**
     * @param sceneId
     *            the sceneId to set
     */
    public void setSceneId(int sceneId) {
        this.sceneId = sceneId;
    }

	/**
	 * @return the clipType
	 */
	public String getClipType() {
		return clipType;
	}

	/**
	 * @param clipType the clipType to set
	 */
	public void setClipType(String clipType) {
		this.clipType = clipType;
	}

    /**
     * @return the clipIndex
     */
    public int getClipIndex() {
        return clipIndex;
    }

    /**
     * @param clipIndex the clipIndex to set
     */
    public void setClipIndex(int clipIndex) {
        this.clipIndex = clipIndex;
    }

    /**
     * @return the trimStart
     */
    public float getTrimStart() {
        return trimStart;
    }

    /**
     * @param trimStart the trimStart to set
     */
    public void setTrimStart(float trimStart) {
        this.trimStart = trimStart;
    }

    /**
     * @return the trimEnd
     */
    public float getTrimEnd() {
        return trimEnd;
    }

    /**
     * @param trimEnd the trimEnd to set
     */
    public void setTrimEnd(float trimEnd) {
        this.trimEnd = trimEnd;
    }


    /**
     * @return the duration
     */
    public float getDuration() {
        return duration;
    }

    /**
     * @param duration the duration to set
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }
   
    public static Bitmap getThumbnail(Context context, Media media, Project project) 
    {
    	ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (EncryptionService.class.getName().equals(service.service.getClassName())) {
                return null;
            }
        }
        
    	
    	if (media == null)
    		return null;
    	
    	
        if (media.getMimeType() == null)
        {
            return null;
        }
        else if ((media.getMimeType().startsWith("video"))||((media.getMimeType().startsWith("image"))))
        {
            String filename = Environment.getExternalStorageDirectory()+"/"+AppConstants.TAG+"/thumbs/"+media.getId()+".jpg";
            String dest = Environment.getExternalStorageDirectory()+"/"+AppConstants.TAG+"/decrypts/"+media.getId()+".jpg";
           
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = IMAGE_SAMPLE_SIZE;

                String file = filename;
		 		Cipher cipher;
				try {
					cipher = Encryption.createCipher(Cipher.DECRYPT_MODE, context);
					Encryption.applyCipher(file, dest, cipher);
				}catch (Exception e) {
					// TODO Auto-generated catch block
					Log.e("Encryption error", e.getLocalizedMessage());
					e.printStackTrace();
				}
				 
				
            return BitmapFactory.decodeFile(dest, options);
            
        }/*
        else if (media.getMimeType().startsWith("image"))
        {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = IMAGE_SAMPLE_SIZE * 2; //images will be bigger than video or audio
            /*
          //Decrypt    
            Intent startMyService= new Intent(context, EncryptionService.class);
            startMyService.putExtra("filepath", media.getPath());
            startMyService.putExtra("mode", Cipher.DECRYPT_MODE);
            context.startService(startMyService);
            */
            /*
            String file = media.getPath();
	 		Cipher cipher;
			try {
				cipher = Encryption.createCipher(Cipher.DECRYPT_MODE);
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
			
            return BitmapFactory.decodeFile(media.getPath(), options);
        }*/
        else if (media.getMimeType().startsWith("audio"))
        {
        	 final BitmapFactory.Options options = new BitmapFactory.Options();
             options.inSampleSize = IMAGE_SAMPLE_SIZE;

             int audioId;
             
             //mod by 5 to repeat the colors in order
             switch(media.clipIndex % 5)
             {    
	             case 0:
	            	 audioId = R.drawable.cliptype_audio_signature;
	            	 break;
	             case 1:
	            	 audioId = R.drawable.cliptype_audio_ambient;
	            	 break;
	             case 2:
	            	 audioId = R.drawable.cliptype_audio_narrative;
	            	 break;
	             case 3:
	            	 audioId = R.drawable.cliptype_audio_interview;
	            	 break;
	             case 4:
	            	 audioId = R.drawable.cliptype_audio_environmental;
	            	 break;
	             default:
	            	audioId = R.drawable.thumb_audio; 
             }
             
            return BitmapFactory.decodeResource(context.getResources(), audioId,  options);
        }
        else 
        {
        	 final BitmapFactory.Options options = new BitmapFactory.Options();
             options.inSampleSize = IMAGE_SAMPLE_SIZE;
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.thumb_complete,options);
        }
    }
    
}
