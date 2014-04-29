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
import cs250.spring14.refill.core.ScheduleItem;

public class ScheduleDBAdapter {

  private SQLiteDatabase db;
  private SchDBHelper dbHelper;
  private final Context context;

  private static final String DB_NAME = "Sch.db";
  private static final int DB_VERSION = 1; // All Doctors must have unique
  // name

  private static final String SC_TABLE = "Scs";
  public static final String SC_ID = "Sc_id"; // column 0
  public static final String SC_NAME = "Sc_name"; // column 1
  public static final String SC_POS = "Sc_email"; // column 2

  public static final String[] SC_COLS = {SC_ID, SC_NAME, SC_POS};

  /**
   * Constructor given a Context for this DBAdapter
   * 
   * @param ctx
   */
  public ScheduleDBAdapter(Context ctx) {
    context = ctx;
    dbHelper = new SchDBHelper(context, DB_NAME, null, DB_VERSION);
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
   * Method to insert a Schedule into the database
   * 
   * @param dr The Schedule to insert
   * @return the row # of the Schedule DB to which we just inserted
   */
  public long insertSch(ScheduleItem sch) {
    open();
    // create a new row of values to insert
    ContentValues cvalues = new ContentValues();
    // assign values for each col
    cvalues.put(SC_NAME, sch.getName());
    cvalues.put(SC_POS, sch.getPos());
    long row = db.insert(SC_TABLE, null, cvalues);
    return row;
  }

  /**
   * Method to remove a ScheduleItem from the DB given Name
   * 
   * @param name the ScheduleItem's name
   * @return true if success, false otherwise
   */
  public boolean removeSchByName(String name) {
    open();
    return db.delete(SC_TABLE, SC_NAME + "= ?", new String[] {name}) > 0;
  }


  /**
   * Method to get a ScheduleItem given a name
   * 
   * @param name the ScheduleItem's name
   * @return the requested ScheduleItem object
   * @throws SQLException
   */
  public ScheduleItem getSchByName(String name) throws SQLException {
    open();
    Cursor c =
        db.query(true, SC_TABLE, SC_COLS, SC_NAME + "=?", new String[] {String.valueOf(name)}, null, null, null, null);
    if ((c.getCount() == 0) || !c.moveToFirst()) {
      return null;
    } else if ((c.getCount() > 1)) {
      return null;
    } else {
      ScheduleItem result = new ScheduleItem(c.getString(1), // name
          Integer.valueOf(c.getString(2)));
      // Set the ID to the row in the DB
      result.setId(c.getInt(0));
      return result;
    }
  }

  /**
   * Method to get ScheduleItems given position
   * 
   * @param pos the ScheduleItems' position
   * @return an ArrayList<ScheduleItem> with all ScheduleItems of given pos
   * @throws SQLException
   */
  public ArrayList<ScheduleItem> getScshByPos(int pos) throws SQLException {
    open();
    ArrayList<ScheduleItem> schs = new ArrayList<ScheduleItem>();
    Cursor c =
        db.query(true, SC_TABLE, SC_COLS, SC_POS + "=?", new String[] {String.valueOf(pos)}, null, null, null, null);
    if (c.moveToFirst()) {
      do {
        ScheduleItem result = new ScheduleItem(c.getString(1), // name
            Integer.valueOf(c.getString(2)));
        // Set the ID to the row in the DB
        result.setId(c.getInt(0));
        schs.add(result);
      } while (c.moveToNext());
    }
    return schs;
  }

  /**
   * Method to remove a ScheduleItem from the DB
   * 
   * @param ri the row to remove
   * @return the # of affected rows
   */
  public int removeDr(long ri) {
    open();
    return db.delete(SC_TABLE, SC_ID + " = ?", new String[] {String.valueOf(ri)});
  }

  /**
   * Method to get a cursor for all the ScheduleItem
   * 
   * @return the Cursor
   */
  public Cursor getAllSchCursor() {
    open();
    return db.query(SC_TABLE, SC_COLS, null, null, null, null, null);
  }

  /**
   * Method to get the # of rows in the db
   * 
   * @return the Cursor
   */
  public int getSize() {
    open();
    Cursor c = db.query(SC_TABLE, SC_COLS, null, null, null, null, null);
    c.moveToFirst();
    return c.getCount();
  }

  /**
   * Method to get all the ScheduleItems from the DB
   * 
   * @return an ArrayList<ScheduleItem> with all the ScheduleItems
   */
  public ArrayList<ScheduleItem> getAllSchs() {
    open();
    ArrayList<ScheduleItem> schs = new ArrayList<ScheduleItem>();
    Cursor c = this.getAllSchCursor();
    if (c.moveToFirst())
      do {
        ScheduleItem result = new ScheduleItem(c.getString(1), // name
            Integer.valueOf(c.getString(2)));
        // Set the ID to the row in the DB
        result.setId(c.getInt(0));
        schs.add(result);
      } while (c.moveToNext());
    return schs;
  }

  /**
   * Method to return the cursor for a Doctor given a specific row
   * 
   * @param ri the row
   * @return the Cursor
   * @throws SQLException
   */
  public Cursor getSchCursor(long ri) throws SQLException {
    open();
    Cursor result = db.query(true, SC_TABLE, SC_COLS, SC_ID + "=" + ri, null, null, null, null, null);
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
  private static class SchDBHelper extends SQLiteOpenHelper {

    // SQL statement to create a new database
    private static final String DB_CREATE = "CREATE TABLE " + SC_TABLE + " (" + SC_ID
        + " INTEGER PRIMARY KEY AUTOINCREMENT," + SC_NAME + " TEXT," + SC_POS + " INTEGER);";

    public SchDBHelper(Context context, String name, CursorFactory fct, int version) {
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
      Log.w("Schdb", "upgrading from version " + oldVersion + " to " + newVersion + ", destroying old data");
      // drop old table if it exists, create new one
      // better to migrate existing data into new table
      adb.execSQL("DROP TABLE IF EXISTS " + SC_TABLE);
      onCreate(adb);
    }
  } // SchDBHelper class
}
