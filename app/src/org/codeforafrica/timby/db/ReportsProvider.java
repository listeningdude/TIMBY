package org.codeforafrica.timby.db;

import net.sqlcipher.database.SQLiteQueryBuilder;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

// FIXME rename this to SMProvier and get rid of LessonsProvider
public class ReportsProvider extends ContentProvider {  
	private StoryMakerDB mDB;
    private String mPassphrase = "foo"; //how and when do we set this??
    
    private static final String AUTHORITY = "org.codeforafrica.timby.db.ReportsProvider";
    public static final int REPORTS = 101;
    public static final int REPORT_ID = 111;
    
    public static final String REPORTS_BASE_PATH = "reports";
    public static final Uri REPORTS_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + REPORTS_BASE_PATH);

    public static final String REPORTS_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/reports";
    public static final String REPORTS_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/reports";
      
    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);
    
    static {
        sURIMatcher.addURI(AUTHORITY, REPORTS_BASE_PATH, REPORTS);
        sURIMatcher.addURI(AUTHORITY, REPORTS_BASE_PATH + "/#", REPORT_ID);
        
    }
    
    @Override
    public boolean onCreate() {
        mDB = new StoryMakerDB(getContext()); 
        return true;
    }

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}
    
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
        case REPORT_ID:
            queryBuilder.setTables(StoryMakerDB.Schema.Reports.NAME);
            queryBuilder.appendWhere(StoryMakerDB.Schema.Reports.ID + "="
                    + uri.getLastPathSegment());
            break;
        case REPORTS:
            queryBuilder.setTables(StoryMakerDB.Schema.Reports.NAME);
            break;
       
        default:
            throw new IllegalArgumentException("Unknown URI");
        }
        
        Cursor cursor = queryBuilder.query(mDB.getReadableDatabase(mPassphrase),
                projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long newId;
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
        case REPORTS:
            newId = mDB.getWritableDatabase(mPassphrase)
                .insertOrThrow(StoryMakerDB.Schema.Reports.NAME, null, values);
            getContext().getContentResolver().notifyChange(uri, null);
            return REPORTS_CONTENT_URI.buildUpon().appendPath(REPORTS_BASE_PATH).appendPath("" + newId).build();
       		default:
			throw new IllegalArgumentException("Unknown URI");
		}
	}
    
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		int count;
		String table;
		switch (uriType) {
        case REPORTS:
        case REPORT_ID:
            table = StoryMakerDB.Schema.Reports.NAME;
            break;
        	default:
			throw new IllegalArgumentException("Unknown URI");
		}
		count = mDB.getWritableDatabase(mPassphrase).delete(table, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		int count;
		String table;
		switch (uriType) {
        case REPORTS:
        case REPORT_ID:
            table = StoryMakerDB.Schema.Reports.NAME;
            break;
        
		default:
			throw new IllegalArgumentException("Unknown URI");
		}
		count = mDB.getWritableDatabase(mPassphrase).update(table, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
}
