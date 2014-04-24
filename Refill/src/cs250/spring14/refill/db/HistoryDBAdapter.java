package cs250.spring14.refill.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import cs250.spring14.refill.core.HistoryItem;

public class HistoryDBAdapter {

	private SQLiteDatabase db;
	private HisDBHelper dbHelper;
	private final Context context;
	public static final String histKey = "refill.hist";
	public static int histCount;

	private static final String DB_NAME = "His.db";
	private static final int DB_VERSION = 16;

	private static final String H_TABLE = "His";
	public static final String H_ID = "H_id"; // column 0
	public static final String H_OWN = "H_own"; // column 1
	public static final String H_MSG = "H_msg"; // column 2
	public static final String H_HT = "H_ht"; // column 3

	public static final String[] H_COLS = { H_ID, H_OWN, H_MSG, H_HT };

	/**
	 * Constructor given a Context for this DBAdapter
	 * 
	 * @param ctx
	 */
	public HistoryDBAdapter(Context ctx) {
		context = ctx;
		dbHelper = new HisDBHelper(context, DB_NAME, null, DB_VERSION);
	}

	/**
	 * Method to open the database
	 * 
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
	 * Method to insert a HistoryItem into the database
	 * 
	 * @param h
	 *            the HistoryItem to insert
	 * @return the row # of the History DB to which we just inserted
	 */
	public long insertHis(HistoryItem h) {
		open();
		// create a new row of values to insert
		ContentValues cvalues = new ContentValues();
		// assign values for each col
		cvalues.put(H_OWN, h.getOwner());
		cvalues.put(H_MSG, h.getMessage());
		cvalues.put(H_HT, h.getH().getType());
		long row = db.insert(H_TABLE, null, cvalues);
		return row;
	}

	/**
	 * Method to update a HistoryItem
	 * 
	 * @param ri
	 *            the row in the DB where this HistoryItem lives
	 * @param own
	 *            the HistoryItem's owner
	 * @param msg
	 *            the HistoryItem's msg
	 * @param ht
	 *            the HistoryItem's HistoryType
	 * @return true if update successful; false otherwise
	 */
	public boolean updateHis(long ri, String own, String msg, String h) {
		ContentValues cvalues = new ContentValues();
		cvalues.put(H_OWN, own);
		cvalues.put(H_MSG, msg);
		cvalues.put(H_HT, h);
		return db.update(H_TABLE, cvalues, H_ID + " = ?",
				new String[] { String.valueOf(ri) }) > 0;
	}

	/**
	 * Method to remove a HistoryItem from the DB
	 * 
	 * @param ri
	 *            the row to remove
	 * @return the # of affected rows
	 */
	public int removeHis(long ri) {
		db = dbHelper.getWritableDatabase();
		return db.delete(H_TABLE, H_ID + " = ?",
				new String[] { String.valueOf(ri) });
	}

	/**
	 * Method to get a cursor for all the HistoryItems Sorted in descending
	 * order for display purposes
	 * 
	 * @return the Cursor
	 */
	public Cursor getAllHisCursor() {
		return db
				.query(H_TABLE, H_COLS, null, null, null, null, H_ID + " DESC");
	}

	/**
	 * Method to get all the HistoryItems from the DB
	 * 
	 * @return an ArrayList<HistoryItem> with all the HistoryItems
	 */
	public ArrayList<HistoryItem> getAllHis() {
		ArrayList<HistoryItem> his = new ArrayList<HistoryItem>();
		Cursor c = this.getAllHisCursor();
		if (c.moveToFirst())
			do {
				HistoryItem result = new HistoryItem(c.getString(1), // owner
						c.getString(2), // message
						c.getString(3));
				// Set the ID to the row in the DB
				result.setId(c.getInt(0));
				his.add(result);
			} while (c.moveToNext());
		return his;
	}
	
	/**
	 * Method to get the right number of history items from the DB
	 * 
	 * @return an ArrayList<HistoryItem> with no more than histCount HistoryItems
	 */
	public ArrayList<HistoryItem> getHisForDisplay() {
		ArrayList<HistoryItem> his = new ArrayList<HistoryItem>();
		Cursor c = this.getAllHisCursor();
		if (c.moveToFirst())
			do {
				HistoryItem result = new HistoryItem(c.getString(1), // owner
						c.getString(2), // message
						c.getString(3)); // type
				// Set the ID to the row in the DB
				result.setId(c.getInt(0));
				his.add(result);
			} while (c.getPosition() < histCount-1 && c.moveToNext());
		return his;
	}

	/**
	 * Method to return the cursor for a HistoryItem given a specific row
	 * 
	 * @param ri
	 *            the row
	 * @return the Cursor
	 * @throws SQLException
	 */
	public Cursor getHisCursor(long ri) throws SQLException {
		Cursor result = db.query(true, H_TABLE, H_COLS, H_ID + "=" + ri, null,
				null, null, null, null);
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
	private static class HisDBHelper extends SQLiteOpenHelper {

		// SQL statement to create a new database
		private static final String DB_CREATE = "CREATE TABLE " + H_TABLE
				+ " (" + H_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + H_OWN
				+ " TEXT," + H_MSG + " TEXT, " + H_HT + " TEXT);";

		public HisDBHelper(Context context, String name, CursorFactory fct,
				int version) {
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
			Log.w("Hisdb", "upgrading from version " + oldVersion + " to "
					+ newVersion + ", destroying old data");
			// drop old table if it exists, create new one
			// better to migrate existing data into new table
			adb.execSQL("DROP TABLE IF EXISTS " + H_TABLE);
			onCreate(adb);
		}
	} // HisDBHelper class
}
