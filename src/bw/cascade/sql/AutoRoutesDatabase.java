package bw.cascade.sql;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import bw.cascade.misc.BitmapEntry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
/***
 * handles operations on a SQLite database that holds match information
 * @author Brandon 
 *
 */
public class AutoRoutesDatabase extends SQLiteOpenHelper{

	private Context context;
	
	private static final int VERSION = 4;
	private static final String DATABASE_NAME = "matchDB";
	private static final String DATABASE_TABLE = "matches";
	//private static final String MATCH_NUMBER_COL = "MatchNum";
	private static final String ID = "Id";
	private static final String AUTO_ROUTE_NAME_COL = "AutoRouteName";
	private static final String NOTES_COL = "Notes";
	private static final String AUTO_BITMAP_PATH_COL = "AutonomousBitmapFilePath";
	
	//note: booleans are stored as integers 0/1
	private String CREATE_DATABASE_STATEMENT = 
			"create table if not exists " + DATABASE_TABLE + 
			"("+ AUTO_ROUTE_NAME_COL + " TEXT PRIMARY KEY,"
			+ NOTES_COL + " TEXT,"
			+ AUTO_BITMAP_PATH_COL + " TEXT" +")";
	
	public AutoRoutesDatabase(Context context) {
		super(context, DATABASE_NAME, null, VERSION);
		this.context = context;
	}

	
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(CREATE_DATABASE_STATEMENT);
	}
	
	@Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(db);
    }
	
	public ArrayList<BitmapEntry> getBitmaps()
	{
		ArrayList<BitmapEntry> bitmaps = new ArrayList<BitmapEntry>();
		String query = "SELECT * FROM "+ DATABASE_TABLE;
		SQLiteDatabase d = this.getWritableDatabase();
		Cursor cursor = d.rawQuery(query, null);
		if(cursor.moveToFirst())
		{
			do
			{
				//retrieve information for each match
				BitmapEntry b = new BitmapEntry();

				b.setName(cursor.getString(0));
				b.setNotes(cursor.getString(1));
				
				if(!cursor.isNull(2))
				{
					Bitmap autoRoute = retrieveBitmapFromInternalStorage(cursor.getString(2));
					b.setBitmap(autoRoute);
				}
				
				
				bitmaps.add(b);
			}while(cursor.moveToNext());
		}
		
		return bitmaps;
	}
	
	
	public void deleteEntry(BitmapEntry entry)
	{
		deleteEntry(entry.getName());
	}
	
	
	public void deleteEntry(String entryName)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		String bitmapFilePath = getEntryBitmapFilePath(entryName);
		if(bitmapFilePath != null)
			context.deleteFile(bitmapFilePath);
		db.delete(DATABASE_TABLE, AUTO_ROUTE_NAME_COL + " = ?", new String[]{entryName});
		db.close();
	}
	
	
	
	public void addRouteEntry(BitmapEntry b)
	{
		ContentValues entryValues = new ContentValues();
		entryValues.put(AUTO_ROUTE_NAME_COL, b.getName());
		entryValues.put(NOTES_COL,b.getNotes());
		
		//alternate way: save bitmap to internal storage, and add file path to SQLite
		String path = saveBitmapToInternalStorage(b);
		if(path == null)
		{
			Log.i("abc", "No bitmap saved");
		}
		else
		{
			entryValues.put(AUTO_BITMAP_PATH_COL, path);
		}
		
		SQLiteDatabase d = this.getWritableDatabase();
		d.insert(DATABASE_TABLE, null, entryValues);
		d.close();
		
		
	}
	
	private String getEntryBitmapFilePath(String name)
	{
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(DATABASE_TABLE, null , AUTO_ROUTE_NAME_COL + " = ?", new String[]{name}, null, null, null);
		if(cursor.moveToFirst())
			return cursor.getString(1);
		else
			return null;
	}

	/**
	 * 
	 * @param m match whose bitmap is to be saved
	 * @return file name in internal storage, null if there was an error or no bitmap
	 */
	private String saveBitmapToInternalStorage(BitmapEntry b)
	{
		FileOutputStream outputStream;
		String fileName = b.getName();
		Bitmap bitmap = b.getBitmap();
		if(bitmap == null)
			return null;
		
		try {
			outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
			outputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Log.i("abc", "Exception: "+e.getMessage());
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i("abc", "Exception: "+e.getMessage());
			e.printStackTrace();
			return null;
		}
		
		return fileName;
	}
	
	/***
	 * 
	 * @param fileName name of the file in internal storage
	 * @return decoded bitmap, null if no bitmap found or there was an error
	 */
	private Bitmap retrieveBitmapFromInternalStorage(String fileName)
	{
		FileInputStream inputStream;
		try {
			inputStream = context.openFileInput(fileName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Log.i("abc", "Exception: "+e.getMessage());
			e.printStackTrace();
			return null;
		}
		Bitmap b = BitmapFactory.decodeStream(inputStream);
		return b;
	}
	
}
