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

public class PharmacyDBAdapter {
	
	private SQLiteDatabase db;
	private PhDBHelper dbHelper;
	private final Context context;
	
	private static final String DB_NAME = "Ph.db";
    private static final int DB_VERSION = 4;
    
    private static final String PH_TABLE = "phs";
    public static final String PH_ID = "Ph_id";   // column 0
    public static final String PH_NAME = "Ph_name"; //column 1
    public static final String PH_EMAIL = "Ph_email"; //column 2
    public static final String PH_PHONE = "Ph_phone"; //column 3
    public static final String PH_STR = "Ph_street"; //column 4
    
    public static final String[] PH_COLS = {PH_ID, PH_NAME, PH_EMAIL, PH_PHONE, PH_STR};
    
    /**
     * Constructor given a Context for this DBAdapter
     * @param ctx
     */
    public PharmacyDBAdapter(Context ctx) {
        context = ctx;
        dbHelper = new PhDBHelper(context, DB_NAME, null, DB_VERSION);
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
     * Method to insert a Pharmacy into the database
     * @param ph the Pharmacy to insert
     * @return the row # of the Doctor DB to which we just inserted
     */
    public long insertPh(Pharmacy ph) {
        // create a new row of values to insert
        ContentValues cvalues = new ContentValues();
        // assign values for each col
        cvalues.put(PH_NAME, ph.getName());
        cvalues.put(PH_EMAIL, ph.getEmail());
        cvalues.put(PH_PHONE, ph.getPhone());
        cvalues.put(PH_STR, ph.getStreetAddress());
        long row = db.insert(PH_TABLE, null, cvalues);
        return row;
    }
    
    /**
     * Method to update a Pharmacy
     * @param ri the row in the DB where this Pharmacy lives
     * @param name the Pharmacy's name
     * @param email the Pharmacy's email
     * @param phone the Pharmacy's phone
     * @param street the Pharamcy's street address
     * @return true if update successful; false otherwise
     */
    public boolean updatePh(long ri, String name, String email, String phone, String street) {
		ContentValues cvalues = new ContentValues();
		cvalues.put(PH_NAME, name);
        cvalues.put(PH_EMAIL, email);
        cvalues.put(PH_PHONE, phone);
        cvalues.put(PH_STR, street);
        return db.update(PH_TABLE, cvalues, PH_ID + " = ?", new String[] {String.valueOf(ri)}) > 0;
	}
    /**
     * Method to remove a Pharmacy from the DB
     * @param ri the row to remove
     * @return the # of affected rows
     */
    public int removeDr(long ri)
    {
    	db = dbHelper.getWritableDatabase();
    	return db.delete(PH_TABLE, PH_ID + " = ?",
    		new String[] { String.valueOf(ri) });
    }
    /**
     * Method to get a cursor for all the Pharmacies
     * @return the Cursor
     */
    public Cursor getAllPhsCursor() {
        return db.query(PH_TABLE, PH_COLS, null, null, null, null, null);
    }
    /**
     * Method to get all the Pharmacies from the DB
     * @return an ArrayList<Pharmacy> with all the Doctors
     */
    public ArrayList<Pharmacy> getAllDrs() {
    	ArrayList<Pharmacy> phs = new ArrayList<Pharmacy>();
    	Cursor c = this.getAllPhsCursor();
    	if (c.moveToFirst())
    		do {
    			Pharmacy result = new Pharmacy(
    					c.getString(1), //name
    					c.getString(2), //email
    					c.getString(3), //phone
    					c.getString(4) //street
    			);
    			//Set the ID to the row in the DB
    			result.setId(c.getInt(0));
    			phs.add(result);
    		} while (c.moveToNext());
		return phs;
    }
    /**
     * Method to return the cursor for a Pharmacy given a specific row
     * @param ri the row
     * @return the Cursor
     * @throws SQLException
     */
    public Cursor getPhCursor(long ri) throws SQLException {
        Cursor result = db.query(true, PH_TABLE, PH_COLS, PH_ID+"="+ri, null, null, null, null, null);
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
    private static class PhDBHelper extends SQLiteOpenHelper {
    	 
		// SQL statement to create a new database
        private static final String DB_CREATE = "CREATE TABLE " + PH_TABLE
                + " (" + PH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + PH_NAME + " TEXT,"
                + PH_EMAIL + " TEXT," + PH_PHONE + " TEXT, " + PH_STR + " TEXT);";
 
        public PhDBHelper(Context context, String name, CursorFactory fct, int version) {
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
            Log.w("Phdb", "upgrading from version " + oldVersion + " to "
                    + newVersion + ", destroying old data");
            // drop old table if it exists, create new one
            // better to migrate existing data into new table
            adb.execSQL("DROP TABLE IF EXISTS " + PH_TABLE);
            onCreate(adb);
        }
    } // PhDBHelper class
}
