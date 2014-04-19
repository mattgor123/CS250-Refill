package cs250.spring14.refill;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;


public class RxDBAdapter {
	
	private SQLiteDatabase db;
	private RxdbHelper dbHelper;
	private final Context context;
	
	private static final String DB_NAME = "Rx.db";
    private static final int DB_VERSION = 15; //Playing around with History stuff
    
    private static final String RX_TABLE = "Rxs";
    public static final String RX_ID = "Rx_id";   // column 0
    public static final String RX_NAME = "Rx_name"; //column 1
    public static final String RX_PT = "RX_patients"; //column 2
    public static final String RX_SYMPT = "RX_symptoms"; //column 3
    public static final String RX_SFECT = "RX_sidefects"; //column 4
    public static final String RX_DOSE = "RX_dose"; //column 5
    public static final String RX_PRD = "RX_perday"; //column 6 - # of pills per day
    public static final String RX_DBR = "RX_daysBetween"; //column 7 - how often it is refilled - user will input how many days
    public static final String RX_PHRM = "RX_pharmacy"; //column 8 - pharmacy
    public static final String RX_MD = "RX_doctor"; //column 9
    public static final String RX_NMB = "RX_number"; //column 10 - 
    public static final String RX_STD = "RX_startDate"; //column 11
    //Adding most recent refill as a field
    public static final String RX_LAST = "RX_last"; //column 12
    
    public static final String[] RX_COLS = {RX_ID, RX_NAME, RX_PT, RX_SYMPT, RX_SFECT, RX_DOSE, RX_PRD,
    	RX_DBR, RX_PHRM, RX_MD, RX_NMB, RX_STD, RX_LAST};
    
    
    public RxDBAdapter(Context ctx) {
        context = ctx;
        dbHelper = new RxdbHelper(context, DB_NAME, null, DB_VERSION);
    }
    
    
    public void open() throws SQLiteException {
    	try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLiteException ex) {
            db = dbHelper.getReadableDatabase();
        }
    }
 
    public void close() {
        db.close();
    }
    
    public long insertRx(RxItem rx) {
        // create a new row of values to insert
        ContentValues cvalues = new ContentValues();
        // assign values for each col
        cvalues.put(RX_NAME, rx.getName());
        cvalues.put(RX_PT, rx.getPatient());
        cvalues.put(RX_SYMPT, rx.getSymptoms());
        cvalues.put(RX_SFECT, rx.getSideEffects());
        cvalues.put(RX_DOSE, rx.getDose());
        cvalues.put(RX_PRD, rx.getPillsPerDay());
        cvalues.put(RX_DBR, rx.getDaysBetweenRefills());
        cvalues.put(RX_PHRM, rx.getPhString());
        cvalues.put(RX_MD, rx.getDocString());
        cvalues.put(RX_NMB, rx.getRxNumb());
        cvalues.put(RX_STD, MainActivity.df.format(rx.getStartDate()));
        cvalues.put(RX_LAST, MainActivity.df.format(rx.getLastRefill()));
        long row = db.insert(RX_TABLE, null, cvalues);
        rx.setId(row);
        return row;
    }
    
    public boolean updateRx(long ri, String name, String patient, String symptoms, String sideEffects,
			int dose, int pillsPerDay, Date start, int daysBetweenRefills, String pharmacy, String physician,
			String rxNumb, Date lastrefill ) {
		ContentValues cvalues = new ContentValues();
		cvalues.put(RX_NAME, name);
        cvalues.put(RX_PT, patient);
        cvalues.put(RX_SYMPT, symptoms);
        cvalues.put(RX_SFECT, sideEffects);
        cvalues.put(RX_DOSE, dose);
        cvalues.put(RX_PRD, pillsPerDay);
        cvalues.put(RX_DBR, daysBetweenRefills);
        cvalues.put(RX_PHRM, pharmacy);
        cvalues.put(RX_MD, physician);
        cvalues.put(RX_NMB, rxNumb);
        cvalues.put(RX_STD, MainActivity.df.format(start));
        cvalues.put(RX_LAST, MainActivity.df.format(lastrefill));
        return db.update(RX_TABLE, cvalues, RX_ID + " = ?", new String[] {String.valueOf(ri)}) > 0;
	}
    
    public int removeRx(long ri)
    {
    	db = dbHelper.getWritableDatabase();
    	return db.delete(RX_TABLE, RX_ID + " = ?",
    		new String[] { String.valueOf(ri) });
    }
    
    public Cursor getAllRxsCursor() {
        return db.query(RX_TABLE, RX_COLS, null, null, null, null, null);
    }
    
    public ArrayList<RxItem> getAllRxs() throws ParseException {
    	ArrayList<RxItem> rxs = new ArrayList<RxItem>();
    	Cursor c = this.getAllRxsCursor();
    	if (c.moveToFirst())
    		do {
    			RxItem result = new RxItem(
    					c.getString(1), //name
    					c.getString(2), //patient
    					c.getString(3), //symptoms
    					c.getString(4), //sideEffects
    					c.getInt(5), //dose
    					c.getInt(6), //pillsPerDay
    					MainActivity.df.parse(c.getString(11)), //startDate
    					c.getInt(7), //daysBetween
    					MainActivity.makePharmFromString(c.getString(8)), //pharmacy
    					MainActivity.makeDocFromString(c.getString(9)), //physician
    					c.getString(10), //RX Number
    					MainActivity.df.parse(c.getString(12)) //last refill
    					);
    			result.setId(c.getInt(0));
    			rxs.add(result);
    		} while (c.moveToNext());
		return rxs;
    }
     
    public Cursor getRxCursor(long ri) throws SQLException {
        Cursor result = db.query(true, RX_TABLE, RX_COLS, RX_ID+"="+ri, null, null, null, null, null);
        if ((result.getCount() == 0) || !result.moveToFirst()) {
            throw new SQLException("No Rx items found for row: " + ri);
        }
        return result;
    }
    
    private static class RxdbHelper extends SQLiteOpenHelper {
    	 
		// SQL statement to create a new database
        private static final String DB_CREATE = "CREATE TABLE " + RX_TABLE
                + " (" + RX_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + RX_NAME + " TEXT,"
                + RX_PT + " TEXT," + RX_SYMPT + " TEXT," + RX_SFECT + " TEXT," + RX_DOSE + " INTEGER," + RX_PRD + 
                " INTEGER," + RX_DBR + " INTEGER, " + RX_PHRM + " TEXT," + RX_MD + " TEXT, " + RX_NMB + " TEXT," + RX_STD + " TEXT, " + RX_LAST + " TEXT);";
 
        public RxdbHelper(Context context, String name, CursorFactory fct, int version) {
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
            Log.w("Rxdb", "upgrading from version " + oldVersion + " to "
                    + newVersion + ", destroying old data");
            // drop old table if it exists, create new one
            // better to migrate existing data into new table
            adb.execSQL("DROP TABLE IF EXISTS " + RX_TABLE);
            onCreate(adb);
        }
    } // RxdbHelper class
}
