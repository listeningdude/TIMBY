package info.guardianproject.mrapp;

import info.guardianproject.mrapp.db.StoryMakerDB;

import java.io.File;
import java.io.FileWriter;

import net.sqlcipher.database.SQLiteDatabase;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

public class ExportDBFiles extends Activity{
/*	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		exportTablesToCSV();
	}

	private void exportTablesToCSV() {
		 File dbFile=getDatabasePath("yourDBname.sqlite");
	        StoryMakerDB dbhelper = new StoryMakerDB(getApplicationContext());
	         File exportDir = new File(Environment.getExternalStorageDirectory(), "");        
	        if (!exportDir.exists()) 
	        {
	            exportDir.mkdirs();
	        }

	        File file = new File(exportDir, "csvname.csv");
	        try 
	        {
	            file.createNewFile();                
	            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
	            SQLiteDatabase db = dbhelper.getReadableDatabase();
	            Cursor curCSV = db.rawQuery("SELECT * FROM TableName",null);
	            csvWrite.writeNext(curCSV.getColumnNames());
	            while(curCSV.moveToNext())
	            {
	               //Which column you want to export
	                String arrStr[] ={curCSV.getString(0),curCSV.getString(1), curCSV.getString(2)};
	                csvWrite.writeNext(arrStr);
	            }
	            csvWrite.close();
	            curCSV.close();
	        }
	        catch(Exception sqlEx)
	        {
	            Log.e("ExportDB", sqlEx.getMessage(), sqlEx);
	        }
	}
*/
}
