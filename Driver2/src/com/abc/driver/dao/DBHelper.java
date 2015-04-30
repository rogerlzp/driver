package com.abc.driver.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.abc.driver.R;

public class DBHelper extends SQLiteOpenHelper {

	static final String TAG = DBHelper.class.getSimpleName();
	private SQLiteDatabase mDatabase;
	Context mContext;
	private final int BUFFER_SIZE = 1024;
	
	 private static String DB_PATH = "/data/data/com.abc.driver/databases/"; 
	
	   public static final String DB_NAME = "city.s3db";
	    public static final String PACKAGE_NAME = "com.abc.driver";
	 //   public static final String DB_PATH = "/data"
	  //         + Environment.getDataDirectory().getAbsolutePath() + "/"+ PACKAGE_NAME;
	
	 private File file=null;
	
//	static final String DB_NAME = "cellsite.db";
	static final int DB_VERSION = 1;
	public static final String LAST_UPDATE = "LastUpdate"; // last update time
	
	public static final String TABLE_NAME1 = "TABLE1"; // TODO: modified later
	public static final String COL_KEY = "KEY1"; //TODO:added
	public static final String COL_CONTENT = "CONTENT1";// TODO: added 
	
	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.mContext = context;

		try {
			createDataBase();
		} catch (IOException ioe) {
			throw new Error("Unable to create database");
		}

	}
	
	 private SQLiteDatabase openDatabase(String dbfile) {
	        try {
	        	Log.e("cc", "open and return");
	        	file = new File(dbfile);
	            if (!file.exists()) {
	            	Log.e("cc", "file");
	            	InputStream is = mContext.getResources().openRawResource(R.raw.cellsite);
	            	if(is!=null){
	            		Log.e("cc", "is null");
	            	}else{
	            	}
	            	FileOutputStream fos = new FileOutputStream(dbfile);
	            	if(is!=null){
	            		Log.e("cc", "fosnull");
	            	}else{
	            	}
	                byte[] buffer = new byte[BUFFER_SIZE];
	                int count = 0;
	                while ((count =is.read(buffer)) > 0) {
	                    fos.write(buffer, 0, count);
	                		Log.e("cc", "while");
	                	fos.flush();
	                }
	                fos.close();
	                is.close();
	            }
	            mDatabase = SQLiteDatabase.openOrCreateDatabase(dbfile,null);
	            return mDatabase;
	        } catch (FileNotFoundException e) {
	            Log.e("cc", "File not found");
	            e.printStackTrace();
	        } catch (IOException e) {
	            Log.e("cc", "IO exception");
	            e.printStackTrace();
	        } catch (Exception e){
	        	Log.e("cc", "exception "+e.toString());
	        }
	        return null;
	    }
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CreateSystemSql = "create table " + TABLE_NAME1
				+ "(" + COL_KEY + " text primary key, " + COL_CONTENT
				+ " text)";
		try {
			db.execSQL(CreateSystemSql);
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Can use the sql syntax "ALTER TABLE .....";
		try {
			db.execSQL("drop table if exists " + TABLE_NAME1);
		} catch (SQLException e) {

			e.printStackTrace();
		}

		onCreate(db);
	}
	
	
	/**
	 * Creates a empty database on the system and rewrites it with your own
	 * database.
	 * */
	public void createDataBase() throws IOException {

		Log.d(TAG, "createDataBase");
		boolean dbExist = false;// checkDataBase();

		if (dbExist) {
			Log.d(TAG, "db does exist");
			// do nothing - database already exist
		} else {
			// By calling this method and empty database will be created into
			// the default system path
			// of your application so we are able to overwrite that
			// database with our database.
			this.getReadableDatabase();
			
			try {
				 
    			copyDataBase();
    			Log.d(TAG, "copy database");
 
    		} catch (IOException e) {
 
        		throw new Error("Error copying database");
 
        	}
		}

	}
	
	
	 /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{
 
    	//Open your local db as the input stream
    	InputStream myInput = mContext.getResources().openRawResource(R.raw.city);
    	
    //	InputStream myInput = mContext.getAssets().open(DB_NAME);
 
    	// Path to the just created empty db
    	String outFileName = DB_PATH + DB_NAME;
 
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
 
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
 
    }
 
	
	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase() {

		SQLiteDatabase checkDB = null;
		Boolean isDBExist = false;

		try {
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);
			if (checkDB != null) {
				isDBExist = true;
			}
		} catch (SQLiteException e) {
			// database does't exist yet.
		}

		if (checkDB != null) {
			checkDB.close();
		}

		return isDBExist;
	}
	
	/*
	public void openDataBase() throws SQLException {
		// Open the database
		String myPath = DB_PATH + DB_NAME;
		mDatabase = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READONLY);
	} */
	
	public void openDataBase() throws SQLException {
		// Open the database
		String myPath = DB_PATH + DB_NAME;
	//	mDatabase = SQLiteDatabase.openDatabase(myPath, null,
	///			SQLiteDatabase.OPEN_READONLY);
		
		 this.mDatabase = this.openDatabase(myPath);
	} 
	
  
	 public SQLiteDatabase getDatabase(){
	    	Log.e("cc", "getDatabase()");
	    	return this.mDatabase;
	    }
	 

	@Override
	public synchronized void close() {
		if (mDatabase != null)
			mDatabase.close();
		super.close();

	}
	
}
