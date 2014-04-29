package cs250.spring14.refill.db;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import cs250.spring14.refill.MainActivity;
import cs250.spring14.refill.core.Doctor;
import cs250.spring14.refill.core.Patient;
import cs250.spring14.refill.core.Pharmacy;
import cs250.spring14.refill.core.RxItem;

public class RxDBAdapter {

  private SQLiteDatabase db;
  private RxdbHelper dbHelper;
  private final Context context;

  private static final String DB_NAME = "Rx.db";
  private static final int DB_VERSION = 24; // Patient Str -> Patient class
  public static final String namesortKey = "refill.namesort";
  public static final String patientsortKey = "refill.patientsort";
  public static boolean shouldSortByName;
  public static boolean shouldSortByPatient;
  private static final String RX_TABLE = "Rxs";
  public static final String RX_ID = "Rx_id"; // column 0
  public static final String RX_NAME = "Rx_name"; // column 1
  public static final String RX_PT = "RX_patients"; // column 2
  public static final String RX_SYMPT = "RX_symptoms"; // column 3
  public static final String RX_SFECT = "RX_sidefects"; // column 4
  public static final String RX_DOSE = "RX_dose"; // column 5
  public static final String RX_PRD = "RX_perday"; // column 6 - # of pills
  // per day
  public static final String RX_DBR = "RX_daysBetween"; // column 7 - how
  // often it is
  // refilled - user
  // will input how
  // many days
  public static final String RX_PHRM = "RX_pharmacy"; // column 8 - pharmacy
  public static final String RX_MD = "RX_doctor"; // column 9
  public static final String RX_NMB = "RX_number"; // column 10 -
  public static final String RX_STD = "RX_startDate"; // column 11
  // Adding most recent refill as a field
  public static final String RX_LAST = "RX_last"; // column 12

  public static final String[] RX_COLS = {RX_ID, RX_NAME, RX_PT, RX_SYMPT, RX_SFECT, RX_DOSE, RX_PRD, RX_DBR, RX_PHRM,
      RX_MD, RX_NMB, RX_STD, RX_LAST};

  /**
   * Constructor given a Context for this DBAdapter
   * 
   * @param ctx
   */
  public RxDBAdapter(Context ctx) {
    context = ctx;
    dbHelper = new RxdbHelper(context, DB_NAME, null, DB_VERSION);
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
   * Method to insert a new Rx to the database
   * 
   * @param rx the Rx to be inserted
   * @return the row # of the Rx DB to which we just inserted
   */
  public long insertRx(RxItem rx) {
    open();
    // create a new row of values to insert
    ContentValues cvalues = new ContentValues();
    // assign values for each col
    cvalues.put(RX_NAME, rx.getName());
    cvalues.put(RX_PT, rx.getPatientString());
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

  /**
   * Method to check if exists Rx's for a given Patient
   * 
   * @param patStr the string representation of a Patient
   * @return true if exists, false otherwise
   * @throws SQLException
   */
  public boolean existsRxWithPat(String patStr) throws SQLException {
    open();
    Cursor c =
        db.query(true, RX_TABLE, RX_COLS, RX_PT + "=?", new String[] {String.valueOf(patStr)}, null, null, null, null);
    if ((c.getCount() == 0) || !c.moveToFirst()) {
      return false;
    } else {
      return true;
    }
  }

  /**
   * Method to check if exists Rx's for a given Doctor
   * 
   * @param docStr the string representation of a Doctor
   * @return true if exists, false otherwise
   * @throws SQLException
   */
  public boolean existsRxWithDoc(String docStr) throws SQLException {
    open();
    Cursor c =
        db.query(true, RX_TABLE, RX_COLS, RX_MD + "=?", new String[] {String.valueOf(docStr)}, null, null, null, null);
    if ((c.getCount() == 0) || !c.moveToFirst()) {
      return false;
    } else {
      return true;
    }
  }

  /**
   * Method to check if exists Rx's for a given Pharmacy
   * 
   * @param phStr the string representation of a Pharmacy
   * @return true if exists, false otherwise
   * @throws SQLException
   */
  public boolean existsRxWithPharm(String phStr) throws SQLException {
    open();
    Cursor c =
        db.query(true, RX_TABLE, RX_COLS, RX_PHRM + "=?", new String[] {String.valueOf(phStr)}, null, null, null, null);
    if ((c.getCount() == 0) || !c.moveToFirst()) {
      return false;
    } else {
      return true;
    }
  }

  /**
   * Method to update all Rx's from a patient to another
   * 
   * @param oldPatient the old patient's string representation
   * @param newPatient the new patient's string representation
   * @return true if update successful, false otherwise
   */
  public boolean updateAllRxWithPatient(String oldPatient, String newPatient) {
    open();
    ContentValues cvalues = new ContentValues();
    cvalues.put(RX_PT, newPatient);
    return db.update(RX_TABLE, cvalues, RX_PT + " = ?", new String[] {oldPatient}) > 0;
  }

  /**
   * Method to update all Rx's from a Doctor to another
   * 
   * @param oldDoctor the old doctor's string representation
   * @param newDoctor the new doctor's string representation
   * @return true if update successful, false otherwise
   */
  public boolean updateAllRxWithDoctor(String oldDoctor, String newDoctor) {
    open();
    ContentValues cvalues = new ContentValues();
    cvalues.put(RX_MD, newDoctor);
    return db.update(RX_TABLE, cvalues, RX_MD + " = ?", new String[] {oldDoctor}) > 0;
  }

  /**
   * Method to update all Rx's from a pharmacy to another
   * 
   * @param oldPharm the old pharmacy's string representation
   * @param newPharm the new pharmacy's string representation
   * @return true if update successful, false otherwise
   */
  public boolean updateAllRxWithPharmacy(String oldPharm, String newPharm) {
    open();
    ContentValues cvalues = new ContentValues();
    cvalues.put(RX_PHRM, newPharm);
    return db.update(RX_TABLE, cvalues, RX_PHRM + " = ?", new String[] {oldPharm}) > 0;
  }

  /**
   * Method to update a Rx
   * 
   * @param ri the Rx ID
   * @param name the Rx name
   * @param patient the Rx patient
   * @param symptoms the Rx's symptoms
   * @param sideEffects the Rx's side effects
   * @param dose the Rx's dosage
   * @param pillsPerDay the Rx's amount of pills per day
   * @param start the Rx's start date
   * @param daysBetweenRefills the Rx's number of days between refills
   * @param pharmacy the Rx's pharmacy
   * @param physician the Rx's doctor
   * @param rxNumb the Rx's number
   * @param lastrefill the Rx's last refill date
   * @return true if update successful, false otherwise
   */

  public boolean updateRx(long ri, String name, String patient, String symptoms, String sideEffects, double dose,
      int pillsPerDay, Date start, int daysBetweenRefills, String pharmacy, String physician, String rxNumb,
      Date lastrefill) {
    open();
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

  /**
   * Method to update an Rx's LastRefillDate
   * 
   * @param ri the row to update
   * @param newDate the new LastRefillDate
   * @return true if update successful, false otherwise
   */
  public boolean updateRxRefillDate(long ri, Date newDate) {
    open();
    ContentValues cvalues = new ContentValues();
    cvalues.put(RX_LAST, MainActivity.df.format(newDate));
    return db.update(RX_TABLE, cvalues, RX_ID + " = ?", new String[] {String.valueOf(ri)}) > 0;
  }

  /**
   * Method to remove a Rx from the database given a ri
   * 
   * @param ri the Rx's ID (row to be removed)
   * @return the # of affected rows
   */
  public int removeRx(long ri) {
    open();
    return db.delete(RX_TABLE, RX_ID + " = ?", new String[] {String.valueOf(ri)});
  }

  /**
   * Method to get a cursor for all the Rx
   * 
   * @return the cursor
   */
  public Cursor getAllRxsCursor() {
    open();
    if (shouldSortByPatient) {
      if (shouldSortByName) {
        // Sort by both (Patient first)
        return db.query(RX_TABLE, RX_COLS, null, null, null, null, RX_PT + ", " + RX_NAME + " ASC");
      } else {
        // Sort by Patient (not name)
        return db.query(RX_TABLE, RX_COLS, null, null, null, null, RX_PT + " ASC");
      }
    } else if (shouldSortByName) {
      // Sort by Name (not patient)
      return db.query(RX_TABLE, RX_COLS, null, null, null, null, RX_NAME + " ASC");
    } else {
      // No sorting
      return db.query(RX_TABLE, RX_COLS, null, null, null, null, null);
    }
  }

  /**
   * Method to get an RxItem given a row
   * 
   * @param ri the row of the RxItem
   * @return the RxItem in the row
   */
  public RxItem getRxFromRow(long ri) {
    open();
    Cursor c = this.getRxCursor(ri);
    if (c.moveToFirst()) {
      RxItem result = null;
      try {
        result = new RxItem(c.getString(1), // name
            Patient.makePatientFromString(c.getString(2)), // patient
            c.getString(3), // symptoms
            c.getString(4), // sideEffects
            c.getFloat(5), // dose
            c.getInt(6), // pillsPerDay
            MainActivity.df.parse(c.getString(11)), // startDate
            c.getInt(7), // daysBetween
            Pharmacy.makePharmFromString(c.getString(8)), // pharmacy
            Doctor.makeDocFromString(c.getString(9)), // physician
            c.getString(10), // RX Number
            MainActivity.df.parse(c.getString(12)) // last refill
            );
        result.setId(c.getInt(0));
        return result;
      } catch (ParseException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return null;
  }

  /**
   * Method to get all Rx's from the database
   * 
   * @return an ArrayList<RxItem> with all the Rx's
   * @throws ParseException
   */
  public ArrayList<RxItem> getAllRxs() throws ParseException {
    open();
    ArrayList<RxItem> rxs = new ArrayList<RxItem>();
    Cursor c = this.getAllRxsCursor();
    if (c.moveToFirst())
      do {
        RxItem result = new RxItem(c.getString(1), // name
            Patient.makePatientFromString(c.getString(2)), // patient
            c.getString(3), // symptoms
            c.getString(4), // sideEffects
            c.getFloat(5), // dose
            c.getInt(6), // pillsPerDay
            MainActivity.df.parse(c.getString(11)), // startDate
            c.getInt(7), // daysBetween
            Pharmacy.makePharmFromString(c.getString(8)), // pharmacy
            Doctor.makeDocFromString(c.getString(9)), // physician
            c.getString(10), // RX Number
            MainActivity.df.parse(c.getString(12)) // last refill
            );
        result.setId(c.getInt(0));
        rxs.add(result);
      } while (c.moveToNext());
    return rxs;
  }

  /**
   * Method to get the size of the DB
   * 
   * @return the size of the DB
   */
  public int getSize() {
    open();
    Cursor c = db.query(RX_TABLE, RX_COLS, null, null, null, null, null);
    c.moveToFirst();
    return c.getCount();
  }

  /**
   * Method to get the cursor for a Rx given a RxId (row number)
   * 
   * @param ri the row number of the Rx
   * @return the cursor
   * @throws SQLException
   */
  public Cursor getRxCursor(long ri) throws SQLException {
    open();
    Cursor result = db.query(true, RX_TABLE, RX_COLS, RX_ID + "=" + ri, null, null, null, null, null);
    if ((result.getCount() == 0) || !result.moveToFirst()) {
      throw new SQLException("No Rx items found for row: " + ri);
    }
    return result;
  }

  /**
   * 
   * Static inner helper DBHelper class
   * 
   */
  private static class RxdbHelper extends SQLiteOpenHelper {

    // SQL statement to create a new database
    private static final String DB_CREATE = "CREATE TABLE " + RX_TABLE + " (" + RX_ID
        + " INTEGER PRIMARY KEY AUTOINCREMENT," + RX_NAME + " TEXT," + RX_PT + " TEXT," + RX_SYMPT + " TEXT,"
        + RX_SFECT + " TEXT," + RX_DOSE + " REAL," + RX_PRD + " INTEGER," + RX_DBR + " INTEGER, " + RX_PHRM + " TEXT,"
        + RX_MD + " TEXT, " + RX_NMB + " TEXT," + RX_STD + " TEXT, " + RX_LAST + " TEXT);";

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
      Log.w("Rxdb", "upgrading from version " + oldVersion + " to " + newVersion + ", destroying old data");
      // drop old table if it exists, create new one
      // better to migrate existing data into new table
      adb.execSQL("DROP TABLE IF EXISTS " + RX_TABLE);
      onCreate(adb);
    }
  } // RxdbHelper class
}
