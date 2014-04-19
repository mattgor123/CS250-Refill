package cs250.spring14.refill;


import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;


public class DoctorDBAdapter {
	
	private SQLiteDatabase db;
	private DrDBHelper dbHelper;
	private final Context context;
	
	private static final String DB_NAME = "Dr.db";
    private static final int DB_VERSION = 4;
    
    private static final String DR_TABLE = "Drs";
    public static final String DR_ID = "Dr_id";   // column 0
    public static final String DR_NAME = "Dr_name"; //column 1
    public static final String DR_EMAIL = "Dr_email"; //column 2
    public static final String DR_PHONE = "Dr_phone"; //column 3
    
    public static final String[] DR_COLS = {DR_ID, DR_NAME, DR_EMAIL, DR_PHONE};
    
    /**
     * Constructor given a Context for this DBAdapter
     * @param ctx
     */
    public DoctorDBAdapter(Context ctx) {
        context = ctx;
        dbHelper = new DrDBHelper(context, DB_NAME, null, DB_VERSION);
    }
    
    /**
     * Method to open the database
     * @throws SQLiteException
     */
    public void open() throws SQLiteException {
    	try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLiteException ex) {
            db = dbHelper.getReadableDatabase();
        }
    }
 
    /**
     * Method to close the database
     */
    public void close() {
        db.close();
    }
    
    /**
     * Method to insert a Doctor into the database
     * @param dr The Doctor to insert
     * @return the row # of the Doctor DB to which we just inserted
     */
    public long insertDr(Doctor dr) {
        // create a new row of values to insert
        ContentValues cvalues = new ContentValues();
        // assign values for each col
        cvalues.put(DR_NAME, dr.getName());
        cvalues.put(DR_EMAIL, dr.getEmail());
        cvalues.put(DR_PHONE, dr.getPhone());
        long row = db.insert(DR_TABLE, null, cvalues);
        return row;
    }
    
    /**
     * Method to update a Doctor
     * @param ri the row in the DB where this Doctor lives
     * @param name the Doctor's name
     * @param email the Doctor's email
     * @param phone the Doctor's phone
     * @return true if update successful; false otherwise
     */
    public boolean updateDr(long ri, String name, String email, String phone) {
		ContentValues cvalues = new ContentValues();
		cvalues.put(DR_NAME, name);
        cvalues.put(DR_EMAIL, email);
        cvalues.put(DR_PHONE, phone);
        return db.update(DR_TABLE, cvalues, DR_ID + " = ?", new String[] {String.valueOf(ri)}) > 0;
	}
    /**
     * Method to remove a Doctor from the DB
     * @param ri the row to remove
     * @return the # of affected rows
     */
    public int removeDr(long ri)
    {
    	db = dbHelper.getWritableDatabase();
    	return db.delete(DR_TABLE, DR_ID + " = ?",
    		new String[] { String.valueOf(ri) });
    }
    /**
     * Method to get a cursor for all the Doctors
     * @return the Cursor
     */
    public Cursor getAllDrsCursor() {
        return db.query(DR_TABLE, DR_COLS, null, null, null, null, null);
    }
    /**
     * Method to get all the Doctors from the DB
     * @return an ArrayList<Doctor> with all the Doctors
     */
    public ArrayList<Doctor> getAllDrs() {
    	ArrayList<Doctor> drs = new ArrayList<Doctor>();
    	Cursor c = this.getAllDrsCursor();
    	if (c.moveToFirst())
    		do {
    			Doctor result = new Doctor(
    					c.getString(1), //name
    					c.getString(2), //email
    					c.getString(3) //phone
    			);
    			//Set the ID to the row in the DB
    			result.setId(c.getInt(0));
    			drs.add(result);
    		} while (c.moveToNext());
		return drs;
    }
    /**
     * Method to return the cursor for a Doctor given a specific row
     * @param ri the row
     * @return the Cursor
     * @throws SQLException
     */
    public Cursor getDrCursor(long ri) throws SQLException {
        Cursor result = db.query(true, DR_TABLE, DR_COLS, DR_ID+"="+ri, null, null, null, null, null);
        if ((result.getCount() == 0) || !result.moveToFirst()) {
            throw new SQLException("No Dr items found for row: " + ri);
        }
        return result;
    }
    
    /**
     * 
     * Static inner helper DBHelper class
     * 
     */
    private static class DrDBHelper extends SQLiteOpenHelper {
    	 
		// SQL statement to create a new database
        private static final String DB_CREATE = "CREATE TABLE " + DR_TABLE
                + " (" + DR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + DR_NAME + " TEXT,"
                + DR_EMAIL + " TEXT," + DR_PHONE + " TEXT);";
 
        public DrDBHelper(Context context, String name, CursorFactory fct, int version) {
            super(context, name, fct, version);
        }
 
        @Override
        public void onCreate(SQLiteDatabase adb) {
            // TODO Auto-generated method stub
            adb.execSQL(DB_CREATE);
        }
 
        @Override
        public void onUpgrade(SQLiteDatabase adb, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            Log.w("Drdb", "upgrading from version " + oldVersion + " to "
                    + newVersion + ", destroying old data");
            // drop old table if it exists, create new one
            // better to migrate existing data into new table
            adb.execSQL("DROP TABLE IF EXISTS " + DR_TABLE);
            onCreate(adb);
        }
    } // DrDBHelper class
}
