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
import cs250.spring14.refill.core.Patient;

/**
 * Database adapter to work with and create our database of Patients Statically referenced in
 * MainActivity & also takes a context as constructor for new DBAdapter
 */

public class PatientDBAdapter {

  private SQLiteDatabase db;
  private PaDBHelper dbHelper;
  private final Context context;

  private static final String DB_NAME = "Pa.db";
  private static final int DB_VERSION = 7; // Require unique color

  private static final String PA_TABLE = "Pats";
  public static final String PA_ID = "Pa_id"; // column 0
  public static final String PA_NAME = "Pa_name"; // column 1
  public static final String PA_COLOR = "Pa_color"; // column 2

  public static final String[] PA_COLS = {PA_ID, PA_NAME, PA_COLOR};

  /**
   * Constructor given a Context for this DBAdapter
   * 
   * @param ctx
   */
  public PatientDBAdapter(Context ctx) {
    context = ctx;
    dbHelper = new PaDBHelper(context, DB_NAME, null, DB_VERSION);
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
   * Method to insert a Doctor into the database
   * 
   * @param pa The Patient to insert
   * @return the row # of the Patient DB to which we just inserted
   */
  public long insertPa(Patient pa) {
    open();
    // create a new row of values to insert
    ContentValues cvalues = new ContentValues();
    // assign values for each col
    cvalues.put(PA_NAME, pa.getName());
    cvalues.put(PA_COLOR, pa.getColor());
    long row = db.insert(PA_TABLE, null, cvalues);
    return row;
  }

  /**
   * Method to remove a Patient from the DB given Name
   * 
   * @param name the Patient's name
   * @return true if success, false otherwise
   */
  public boolean removePaByName(String name) {
    open();
    return db.delete(PA_TABLE, PA_NAME + "= ?", new String[] {name}) > 0;
  }

  /**
   * Method to update a Patient given a ri
   * 
   * @param ri the row in the DB where this Patient lives
   * @param name the Patient's name
   * @param color the Patient's color
   * @return true if update successful; false otherwise
   */
  public boolean updatePa(long ri, String name, int color) {
    open();
    Patient pat = getPatientByName(name);
    if (pat != null) {
      // there already is a doc with this name; make sure he has the same
      // ID
      if (pat.getId() != ri)
        return false;
    }
    ContentValues cvalues = new ContentValues();
    cvalues.put(PA_NAME, name);
    cvalues.put(PA_COLOR, color);
    return db.update(PA_TABLE, cvalues, PA_ID + " = ?", new String[] {String.valueOf(ri)}) > 0;
  }

  /**
   * Method to get the # of rows in the db
   * 
   * @return the Cursor
   */
  public int getSize() {
    open();
    Cursor c = db.query(PA_TABLE, PA_COLS, null, null, null, null, null);
    c.moveToFirst();
    return c.getCount();
  }

  /**
   * Method to get a Patient given Name
   * 
   * @param name the Patient's name
   * @return the requested Patient object
   * @throws SQLException
   */
  public Patient getPatientByName(String name) throws SQLException {
    open();
    Cursor c =
        db.query(true, PA_TABLE, PA_COLS, PA_NAME + "=?", new String[] {String.valueOf(name)}, null, null, null, null);
    if ((c.getCount() == 0) || !c.moveToFirst()) {
      return null;
    } else if ((c.getCount() > 1)) {
      return null;
    } else {
      Patient result = new Patient(c.getString(1), // name
          Integer.valueOf(c.getString(2)) // color
          );
      // Set the ID to the row in the DB
      result.setId(c.getInt(0));
      return result;
    }
  }

  /**
   * Method to update a Patient given Name
   * 
   * @param pname the Patient's name
   * @param name the Patient's new name
   * @param color the Patient's new color
   * @return true if update successful; false otherwise
   */
  public boolean updatePatientByName(String pname, String name, int color) {
    open();
    if (pname != name && getPatientByName(name) != null) {
      return false;
    } else {
      ContentValues cvalues = new ContentValues();
      cvalues.put(PA_NAME, name);
      cvalues.put(PA_COLOR, color);
      return db.update(PA_TABLE, cvalues, PA_NAME + " = ?", new String[] {String.valueOf(pname)}) > 0;
    }
  }

  /**
   * Method to remove a Patient from the DB
   * 
   * @param ri the row to remove
   * @return the # of affected rows
   */
  public int removePatient(long ri) {
    open();
    return db.delete(PA_TABLE, PA_ID + " = ?", new String[] {String.valueOf(ri)});
  }

  /**
   * Method to get a cursor for all the Patients
   * 
   * @return the Cursor
   */
  public Cursor getAllPatsCursor() {
    open();
    return db.query(PA_TABLE, PA_COLS, null, null, null, null, null);
  }

  /**
   * Method to get all the Patients from the DB
   * 
   * @return an ArrayList<Patient> with all the Doctors
   */
  public ArrayList<Patient> getAllPats() {
    open();
    ArrayList<Patient> pats = new ArrayList<Patient>();
    Cursor c = this.getAllPatsCursor();
    if (c.moveToFirst())
      do {
        Patient result = new Patient(c.getString(1), // name
            Integer.valueOf(c.getString(2)) // phone
            );
        // Set the ID to the row in the DB
        result.setId(c.getInt(0));
        pats.add(result);
      } while (c.moveToNext());
    return pats;
  }

  /**
   * Method to determine if a patient of a given color already exists
   * 
   * @param color the color to check for
   * @return true if exists, false otherwise
   * @throws SQLException
   */
  public boolean existsPatWithColor(String colorStr) throws SQLException {
    open();
    Cursor c =
        db.query(true, PA_TABLE, PA_COLS, PA_COLOR + "=?", new String[] {String.valueOf(colorStr)}, null, null, null,
            null);
    if ((c.getCount() == 0) || !c.moveToFirst()) {
      return false;
    } else {
      return true;
    }
  }

  /**
   * Method to return the cursor for a Patient given a specific row
   * 
   * @param ri the row
   * @return the Cursor
   * @throws SQLException
   */
  public Cursor getPatCursor(long ri) throws SQLException {
    open();
    Cursor result = db.query(true, PA_TABLE, PA_COLS, PA_ID + "=" + ri, null, null, null, null, null);
    if ((result.getCount() == 0) || !result.moveToFirst()) {
      throw new SQLException("No Patient items found for row: " + ri);
    }
    return result;
  }

  /**
   * 
   * Static inner helper DBHelper class
   * 
   */
  private static class PaDBHelper extends SQLiteOpenHelper {

    // SQL statement to create a new database
    private static final String DB_CREATE = "CREATE TABLE " + PA_TABLE + " (" + PA_ID
        + " INTEGER PRIMARY KEY AUTOINCREMENT," + PA_NAME + " TEXT UNIQUE," + PA_COLOR + " INTEGER);";

    public PaDBHelper(Context context, String name, CursorFactory fct, int version) {
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
      Log.w("Patdb", "upgrading from version " + oldVersion + " to " + newVersion + ", destroying old data");
      // drop old table if it exists, create new one
      // better to migrate existing data into new table
      adb.execSQL("DROP TABLE IF EXISTS " + PA_TABLE);
      onCreate(adb);
    }
  } // PaDBHelper class
}
