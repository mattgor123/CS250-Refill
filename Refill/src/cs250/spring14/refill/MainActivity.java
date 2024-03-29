package cs250.spring14.refill;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;
import cs250.spring14.refill.core.Doctor;
import cs250.spring14.refill.core.HistoryItem;
import cs250.spring14.refill.core.Patient;
import cs250.spring14.refill.core.Pharmacy;
import cs250.spring14.refill.core.RxItem;
import cs250.spring14.refill.db.DoctorDBAdapter;
import cs250.spring14.refill.db.HistoryDBAdapter;
import cs250.spring14.refill.db.PatientDBAdapter;
import cs250.spring14.refill.db.PharmacyDBAdapter;
import cs250.spring14.refill.db.RxDBAdapter;
import cs250.spring14.refill.db.ScheduleDBAdapter;
import cs250.spring14.refill.notify.RxNotificationManager;
import cs250.spring14.refill.view.DoctorFragment;
import cs250.spring14.refill.view.PatientFragment;
import cs250.spring14.refill.view.PharmacyFragment;
import cs250.spring14.refill.view.RefreshableFragment;
import cs250.spring14.refill.view.ScheduleFragment;
import cs250.spring14.refill.view.SettingsFragment;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener,
    RefreshableFragment.OnCompleteListener, OnSharedPreferenceChangeListener {

  /**
   * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
   * sections. We use a {@link FragmentPagerAdapter} derivative, which will keep every loaded
   * fragment in memory. If this becomes too memory intensive, it may be best to switch to a
   * {@link android.support.v4.app.FragmentStatePagerAdapter}.
   */
  SectionsPagerAdapter mSectionsPagerAdapter;

  /**
   * The {@link ViewPager} that will host the section contents.
   */
  ViewPager mViewPager;
  public static final DateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
  private final String[] tabs = new String[] {"Prescriptions", "History"};
  private Fragment[] frags;
  public static RxDBAdapter rxAdapter;
  public static DoctorDBAdapter drAdapter;
  public static PharmacyDBAdapter phAdapter;
  public static HistoryDBAdapter hAdapter;
  public static PatientDBAdapter paAdapter;
  public static ScheduleDBAdapter scAdapter;
  public static int currFrag;
  private boolean shouldLogin;
  private Menu menu;
  protected static final int LOGIN = 3;
  public static String DEFAULT_RX_NUMBER = "12345";
  private static MainActivity _instance;

  /**
   * Method that creates our app.
   * 
   * @param savedInstanceState a Bundle containing a previous saved state of our app
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Get whether the log-in screen should be displayed from SharedPrefs
    SharedPreferences prefs = this.getSharedPreferences("refill", Context.MODE_PRIVATE);
    prefs.registerOnSharedPreferenceChangeListener(this);
    shouldLogin = prefs.getBoolean(LoginActivity.nextKey, true);
    HistoryDBAdapter.histCount = prefs.getInt(HistoryDBAdapter.histKey, 100);
    if (shouldLogin) {
      // Show the log-in screen
      Intent login = new Intent(this, LoginActivity.class);
      startActivityForResult(login, LOGIN);
    }
    setContentView(R.layout.activity_main);
    // Set up the fragments
    RxFragment rxFrag = new RxFragment();
    rxFrag.setRetainInstance(true);
    HistoryFragment hisFrag = new HistoryFragment();
    hisFrag.setRetainInstance(true);
    frags = new Fragment[] {rxFrag, hisFrag};
    // Set up the action bar.
    final ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(false);
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

    // Create the adapter that will return a fragment for each of the three
    // primary sections of the activity.
    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

    // Set up the ViewPager with the sections adapter.
    mViewPager = (ViewPager) findViewById(R.id.pager);
    mViewPager.setAdapter(mSectionsPagerAdapter);

    // When swiping between different sections, select the corresponding
    // tab. We can also use ActionBar.Tab#select() to do this if we have
    // a reference to the Tab.
    mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
      @Override
      public void onPageSelected(int position) {
        actionBar.setSelectedNavigationItem(position);
        MainActivity.currFrag = position;
        RefreshableFragment f = (RefreshableFragment) frags[currFrag];
        if (f != null) {
          // Should never be null, but just in case...
          f.repopulateAdapter();
        }
      }
    });

    // For each of the sections in the app, add a tab to the action bar.
    for (String tName : tabs) {
      // Create a tab with text corresponding to the page title defined by
      // the adapter. Also specify this Activity object, which implements
      // the TabListener interface, as the callback (listener) for when
      // this tab is selected.
      actionBar.addTab(actionBar.newTab().setText(tName).setTabListener(this));
    }

    // Rx adapter stuff
    rxAdapter = new RxDBAdapter(this);
    RxDBAdapter.shouldSortByName = prefs.getBoolean(RxDBAdapter.namesortKey, false);
    RxDBAdapter.shouldSortByPatient = prefs.getBoolean(RxDBAdapter.patientsortKey, false);
    rxAdapter.open();
    // Dr adapter stuff
    drAdapter = new DoctorDBAdapter(this);
    drAdapter.open();
    if (drAdapter.getSize() == 0) {
      // Dummy doctor in first spot for the spinner
      Doctor adding = new Doctor("Select a Doctor or Add One", "", "");
      adding.setId(drAdapter.insertDr(adding));
    }
    // Ph adapter stuff
    phAdapter = new PharmacyDBAdapter(this);
    phAdapter.open();
    if (phAdapter.getSize() == 0) {
      // Dummy pharmacy in first spot for the spinner
      Pharmacy adding = new Pharmacy("Select a Pharmacy or Add One", "", "", "");
      adding.setId(phAdapter.insertPh(adding));
    }
    // History adapter stuff
    hAdapter = new HistoryDBAdapter(this);
    HistoryDBAdapter.histCount = prefs.getInt(HistoryDBAdapter.histKey, 100);
    hAdapter.open();
    // Patient adapter stuff
    paAdapter = new PatientDBAdapter(this);
    paAdapter.open();
    if (paAdapter.getSize() == 0) {
      Patient adding = new Patient("Select a Patient!", 0);
      adding.setId(paAdapter.insertPa(adding));
    }
    // ScheduleItem adapter stuff
    scAdapter = new ScheduleDBAdapter(this);
    scAdapter.open();
    _instance = this;
  }

  /**
   * Method to create the action bar menus and inflate it with items.
   * 
   * @param menu the action bar
   */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    this.menu = menu;
    return true;
  }

  /**
   * Method to handle a click in a action bar item.
   * 
   * @param item the action bar item clicked
   * 
   * @return true if the action bar item is matched in the switch statement (should always return
   *         true)
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    switch (item.getItemId()) {
      case R.id.action_cal:
        if (rxAdapter.getSize() == 0) {
          Toast.makeText(this, "You should add a Prescription before opening schedule!", Toast.LENGTH_SHORT).show();
        } else {
          ScheduleFragment scheduleFrag = new ScheduleFragment();
          scheduleFrag.show(getSupportFragmentManager(), "ScheduleFragment");
        }
        return true;
      case R.id.action_add:
        openCustomDialog(this);
        return true;
      case R.id.action_doc:
        if (drAdapter.getSize() <= 1) {
          Toast.makeText(this, "You haven't added any Doctors yet!", Toast.LENGTH_SHORT).show();
        } else {
          DoctorFragment docFrag = new DoctorFragment();
          docFrag.show(getSupportFragmentManager(), "DoctorFragment");
        }
        return true;
      case R.id.action_pharm:
        if (phAdapter.getSize() <= 1) {
          Toast.makeText(this, "You haven't added any Pharmacies yet!", Toast.LENGTH_SHORT).show();
        } else {
          PharmacyFragment pharmFrag = new PharmacyFragment();
          pharmFrag.show(getSupportFragmentManager(), "PharmacyFragment");
        }
        return true;
      case R.id.action_patient:
        if (paAdapter.getSize() <= 1) {
          Toast.makeText(this, "You haven't added any Patients yet!", Toast.LENGTH_SHORT).show();
        } else {
          PatientFragment patientFrag = new PatientFragment();
          patientFrag.show(getSupportFragmentManager(), "PatientFragment");
        }
        return true;
      case R.id.action_settings:
        SettingsFragment settingFrag = new SettingsFragment();
        settingFrag.show(getSupportFragmentManager(), "SettingsFragment");
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  /**
   * Method to override standard menu button behavior to make it open our overflow menu.
   * 
   * @param keyCode The key
   * @param event The event
   * @return true if opened menu, false otherwise
   */
  @Override
  public boolean onKeyUp(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_MENU) {
      openOptionsMenu();
      menu.performIdentifierAction(R.id.action_overflow, 0);
      return true;
    }
    return super.onKeyUp(keyCode, event);
  }

  /**
   * Method to get the result from the login activity.
   * 
   * @param requestCode
   * @param resultCode
   * @param data
   */
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
      case LOGIN: {
        if (resultCode == Activity.RESULT_OK) {
          // We logged in successfully!
          String email = data.getStringExtra(LoginActivity.RESULT_STRING);
          Toast.makeText(this, "Logging in with e-mail: " + email, Toast.LENGTH_SHORT).show();
          break;
        } else if (resultCode == Activity.RESULT_FIRST_USER) {
          String email = data.getStringExtra(LoginActivity.RESULT_STRING);
          Toast.makeText(this, "First time logging in with e-mail: " + email, Toast.LENGTH_SHORT).show();
          // Initializing daily notification stuff
          // Notification stuff
          // Starting Alarm Service for the refill Notification
          Intent alarmIntent = new Intent(this, RxNotificationManager.class);
          alarmIntent.setAction(RxNotificationManager.SEND_NOTIFICATION);
          PendingIntent pi = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
          AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
          am.cancel(pi);
          Calendar calendar = Calendar.getInstance();
          calendar.set(Calendar.HOUR_OF_DAY, 12);
          calendar.set(Calendar.MINUTE, 0);
          am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + 86400000, AlarmManager.INTERVAL_DAY, pi);
          break;
        } else if (resultCode == LoginActivity.KILLED) {
          // We hit the back button from LoginActivity
          finish();
        }
      }

    }
  }

  /**
   * Override method to onStart(). Gets executed when the app starts.
   */
  @Override
  public void onStart() {
    super.onStart();
    rxAdapter.open();
    drAdapter.open();
    phAdapter.open();
    hAdapter.open();
    paAdapter.open();
    scAdapter.open();
    // Make sure the fragments are still initialized, haven't been destroyed
    if (frags[0] == null || frags[1] == null) {
      RxFragment rxFrag = new RxFragment();
      rxFrag.setRetainInstance(true);
      frags[0] = rxFrag;
      HistoryFragment hisFrag = new HistoryFragment();
      hisFrag.setRetainInstance(true);
      frags[1] = hisFrag;
    }
  }

  /**
   * Override method to onStop(). Gets executed when the app stops.
   */
  @Override
  public void onStop() {
    super.onStop();
    rxAdapter.close();
    drAdapter.close();
    phAdapter.close();
    hAdapter.close();
    paAdapter.close();
    scAdapter.close();
  }

  /**
   * Method to handle the selection of a tab.
   * 
   * @param tab the selected tab
   * @param fragmentTransaction
   */
  @Override
  public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    // When the given tab is selected, switch to the corresponding page in
    // the ViewPager.
    mViewPager.setCurrentItem(tab.getPosition());
  }

  /**
   * Method to handle the deselection of a tab (doesn't actually do anything).
   */
  @Override
  public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}

  /**
   * Method to check if a int is valid within a EditText. This method is used to validate the data
   * filled in our dialogs.
   * 
   * @param et the EditText being evaluated
   * @return true if int is valid, false otherwise
   */
  public static boolean isValidInt(EditText et) {
    String str = et.getText().toString().trim();
    if (str.length() > 0) {
      return Integer.valueOf(str) > 0;
    } else
      return false;
  }

  /**
   * Method to check if a double is valid within a EditText. This method is used to validate the
   * data filled in our dialogs.
   * 
   * @param et the EditText being evaluated
   * @return true if double is valid, false otherwise
   */
  public static boolean isValidDouble(EditText et) {
    String str = et.getText().toString().trim();
    if (str.length() > 0 && !str.equals(".")) {
      return Double.valueOf(str) > 0;
    } else
      return false;
  }

  /**
   * Method to check if a name is valid within a EditText. This method is used to validate the data
   * filled in our dialogs.
   * 
   * @param et the EditText being evaluated
   * @return true if the name is valid, false otherwise
   */
  public static boolean isValidName(String str) {
    if (str.length() > 2) {
      String[] name = str.split(" ");
      if (name.length < 2) {
        // We have not entered a First & Last name
        return false;
      }
      // Very basic; just seeing if we have at least 1 space. Could do
      // more validation later.
      else
        return true;
    }
    return false;
  }

  /**
   * Method to check if a phone is valid within a EditText. This method is used to validate the data
   * filled in our dialogs.
   * 
   * @param et the EditText being evaluated
   * @return true if the phone is valid, false otherwise
   */
  public static boolean isValidPhone(String str) {
    if (str.length() >= 10) {
      return android.util.Patterns.PHONE.matcher(str).matches();
    } else
      return false;
  }

  /**
   * Method to check if a email is valid within a EditText. This method is used to validate the data
   * filled in our dialogs.
   * 
   * @param et the EditText being evaluated
   * @return true if the email is valid, false otherwise
   */
  public static boolean isValidEmail(String str) {
    if (str.length() >= 5) {
      return android.util.Patterns.EMAIL_ADDRESS.matcher(str).matches();
    } else
      return false;
  }

  /**
   * Method to check if a street is valid within a EditText. This method is used to validate the
   * data filled in our dialogs.
   * 
   * @param et the EditText being evaluated
   * @return true if the street is valid, false otherwise
   */
  public static boolean isValidStreet(String str) {
    if (str.length() > 0) {
      String[] address = str.split(" ");
      if (address.length < 2) {
        // We've entered an invalid address;
        // Must be at least of the form # STREETNAME
        return false;
      }
      if (address[0].matches("\\d+")) {
        return true;
      }
    }
    return false;
  }

  /**
   * Method to handle the reselection of a tab (doesn't actually do anything).
   */
  @Override
  public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}

  /**
   * Method to provide static access to MainActivity from other classes.
   * 
   * @return the current MainActivity instance
   */
  public static MainActivity getInstance() {
    return _instance;
  }

  /**
   * Helper method to open dialog to add a doctor WITHOUT the selection spinner.
   * 
   * @param context the context for the dialog
   */
  protected void openAddDoctorDialog(final Context context) {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    LinearLayout layout = new LinearLayout(this);
    layout.setOrientation(1);
    // TODO Auto-generated method stub
    final EditText name = new EditText(this);
    final EditText email = new EditText(this);
    final EditText phone = new EditText(this);
    Button add = new Button(this);
    add.setText("Add a New Doctor");
    name.setSingleLine();
    email.setSingleLine();
    phone.setSingleLine();
    name.setHint("    Name (must be unique): ");
    email.setHint("    Email: ");
    phone.setHint("    Phone: ");
    name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
    email.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    phone.setInputType(InputType.TYPE_CLASS_NUMBER);
    layout.addView(name);
    layout.addView(email);
    layout.addView(phone);
    layout.addView(add);
    builder.setView(layout);
    final Dialog d = builder.create();
    add.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        // Basic input checking; will get better with time
        String nameStr = name.getText().toString().trim();
        String emailStr = email.getText().toString().trim();
        String phoneStr = phone.getText().toString().trim();
        if (!isValidName(nameStr)) {
          Toast.makeText(getApplicationContext(), "Please ensure you've entered a valid name", Toast.LENGTH_SHORT)
              .show();
        } else if (!isValidEmail(emailStr)) {
          Toast.makeText(getApplicationContext(), "Please ensure you've entered a valid email", Toast.LENGTH_SHORT)
              .show();
        } else if (!isValidPhone(phoneStr)) {
          Toast.makeText(getApplicationContext(), "Please ensure you've entered a valid phone", Toast.LENGTH_SHORT)
              .show();
        } else {
          // We are good to add our Doctor
          Doctor newDoc = new Doctor(nameStr, emailStr, phoneStr);
          newDoc.setId(drAdapter.insertDr(new Doctor(nameStr, emailStr, phoneStr)));
          // ID will be 0 if the insert returned 0 aka didn't add to
          // the DB
          if (newDoc.getId() > 0) {
            // We added the Doctor
            String message = "Added to Doctors DB on " + df.format(Calendar.getInstance().getTime());
            hAdapter.insertHis(new HistoryItem(nameStr, message, "D"));
            d.dismiss();
            currFrag = 1;
            MainActivity.getInstance().mViewPager.setCurrentItem(currFrag);
            RefreshableFragment f = (RefreshableFragment) frags[currFrag];
            if (f != null) {
              // Should never be null, but just in case...
              f.repopulateAdapter();
            }
          } else {
            // We didn't actually add the Doctor; ID = 0
            String message = "All Doctors must have unique names. Please try again.";
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
          }
        }
      }
    });
    d.show();
  }

  /**
   * Helper method to open dialog to add a patient WITHOUT the selection spinner.
   * 
   * @param context the context for the dialog
   */
  protected void openAddPatientDialog(final Context context) {
    final Dialog dialog = new Dialog(context);
    dialog.setTitle("Please add a patient");
    dialog.setContentView(R.layout.patient_dialog);
    final EditText et = (EditText) dialog.findViewById(R.id.patient);
    final RadioGroup colors = (RadioGroup) dialog.findViewById(R.id.colors);
    Button ok = (Button) dialog.findViewById(R.id.ok);
    ok.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        // Step 1) Make sure we have a valid name
        String str = et.getText().toString().trim();
        if (str.length() == 0) {
          Toast.makeText(context, "Please ensure you've entered a valid patient name", Toast.LENGTH_SHORT).show();
        }
        // Step 2) Make sure patient doesn't already exist
        else if (paAdapter.getPatientByName(str) != null) {
          Toast.makeText(context, "A patient with this name already exists!", Toast.LENGTH_SHORT).show();
        }
        // Step 3) Make sure no patient with same color already exists
        else {
          int color = colors.getCheckedRadioButtonId();
          String message;
          int newColor;
          switch (color) {
            case R.id.w:
              newColor = Color.WHITE;
              break;
            case R.id.lb:
              newColor = Color.parseColor("#94FFFF");
              break;
            case R.id.lo:
              newColor = Color.parseColor("#FFCC99");
              break;
            case R.id.ly:
              newColor = Color.parseColor("#FFFF99");
              break;
            case R.id.lg:
              newColor = Color.parseColor("#85FF85");
              break;
            case R.id.lpu:
              newColor = Color.parseColor("#CCCCFF");
              break;
            case R.id.lpi:
              newColor = Color.parseColor("#FFCCFF");
              break;
            default:
              // We really shouldn't be here
              return;
          }
          if (paAdapter.existsPatWithColor(String.valueOf(newColor))) {
            Toast.makeText(context, "A patient with this color already exists!", Toast.LENGTH_SHORT).show();
            return;
          } else {
            paAdapter.insertPa(new Patient(str, newColor));
            message = "Added to Patients DB on " + df.format(Calendar.getInstance().getTime());
            hAdapter.insertHis(new HistoryItem(str, message, "PA"));
            dialog.dismiss();
            currFrag = 1;
            MainActivity.getInstance().mViewPager.setCurrentItem(currFrag);
            RefreshableFragment f = (RefreshableFragment) frags[currFrag];
            if (f != null) {
              // Should never be null, but just in case...
              f.repopulateAdapter();
            }
          }
        }
      }
    });
    dialog.show();
  }

  /**
   * Helper method to make the doctor dialog from the adding Rx This has the spinner to make a
   * selection amongst doctors.
   * 
   * @param context the context for the dialog
   * @param v the calling view (to set the text to the selected doctor)
   */
  protected void openDoctorSelectDialog(final Context context, final View v) {
    ArrayList<Doctor> drs = drAdapter.getAllDrs();
    if (drs.size() == 0) {
      // Dummy doctor in first spot for the spinner
      Doctor adding = new Doctor("Select a Doctor or Add One", "", "");
      adding.setId(drAdapter.insertDr(adding));
      drs.add(adding);
    }
    final ArrayAdapter<Doctor> aa =
        new ArrayAdapter<Doctor>(getApplicationContext(), android.R.layout.simple_spinner_item, drs);
    final Spinner spinner = new Spinner(getApplicationContext());
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    LinearLayout layout = new LinearLayout(this);
    layout.setOrientation(1);
    // TODO Auto-generated method stub
    final EditText name = new EditText(this);
    final EditText email = new EditText(this);
    final EditText phone = new EditText(this);
    Button add = new Button(this);
    add.setText("Add a New Doctor");
    add.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        // Basic input checking; will get better with time
        String nameStr = name.getText().toString().trim();
        String emailStr = email.getText().toString().trim();
        String phoneStr = phone.getText().toString().trim();
        if (!isValidName(nameStr)) {
          Toast.makeText(getApplicationContext(), "Please ensure you've entered a valid name", Toast.LENGTH_SHORT)
              .show();
        } else if (!isValidEmail(emailStr)) {
          Toast.makeText(getApplicationContext(), "Please ensure you've entered a valid email", Toast.LENGTH_SHORT)
              .show();
        } else if (!isValidPhone(phoneStr)) {
          Toast.makeText(getApplicationContext(), "Please ensure you've entered a valid phone", Toast.LENGTH_SHORT)
              .show();
        } else {
          // We are good to add our Doctor
          Doctor newDoc = new Doctor(nameStr, emailStr, phoneStr);
          newDoc.setId(drAdapter.insertDr(new Doctor(nameStr, emailStr, phoneStr)));
          if (newDoc.getId() > 0) {
            // We added our Doctor
            String message = "Added to Doctors DB on " + df.format(Calendar.getInstance().getTime());
            hAdapter.insertHis(new HistoryItem(nameStr, message, "D"));
            // Better way to do it, but just wanted to get
            // functionality
            aa.clear();
            aa.addAll(drAdapter.getAllDrs());
            name.setText("");
            email.setText("");
            phone.setText("");
            spinner.setSelection(aa.getCount());
            // frags[currFrag].onResume();
          } else {
            String message = "Couldn't add your Doctor. Please ensure each Doctor's name is unique.";
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
          }
        }
      }

    });
    name.setSingleLine();
    email.setSingleLine();
    phone.setSingleLine();
    name.setHint("    Name (must be unique): ");
    email.setHint("    Email: ");
    phone.setHint("    Phone: ");
    name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
    email.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    phone.setInputType(InputType.TYPE_CLASS_NUMBER);
    layout.addView(name);
    layout.addView(email);
    layout.addView(phone);
    layout.addView(add);
    builder.setView(layout);
    final Dialog d = builder.create();
    spinner.setAdapter(aa);
    spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Doctor dr = (Doctor) spinner.getSelectedItem();
        if (dr.getId() == 1) {
          // We hit our Dummy Doctor, do nothing.
        } else {
          EditText et = (EditText) v;
          et.setText(Doctor.makeStringFromDoc(dr));
          d.dismiss();
        }

      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
        // Nothing selected; do nothing
      }

    });
    layout.addView(spinner);
    d.show();
  }

  /**
   * Helper method to open dialog to add a pharmacy WITHOUT the selection spinner.
   * 
   * @param context the context for the dialog
   */
  protected void openAddPharmacyDialog(final Context context) {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    LinearLayout layout = new LinearLayout(this);
    layout.setOrientation(1);
    final EditText name = new EditText(this);
    final EditText email = new EditText(this);
    final EditText phone = new EditText(this);
    final EditText street = new EditText(this);
    Button add = new Button(this);
    add.setText("Add a New Pharmacy");
    name.setSingleLine();
    email.setSingleLine();
    phone.setSingleLine();
    street.setSingleLine();
    name.setHint("    Unique Name (CVS Boston): ");
    email.setHint("    Email For Refill: ");
    phone.setHint("    Phone: ");
    street.setHint("    Street (123 Oak): ");
    name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
    email.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    phone.setInputType(InputType.TYPE_CLASS_NUMBER);
    street.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
    layout.addView(name);
    layout.addView(email);
    layout.addView(phone);
    layout.addView(street);
    layout.addView(add);
    builder.setView(layout);
    final Dialog d = builder.create();
    add.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        // Basic input checking; will get better with time
        String nameStr = name.getText().toString().trim();
        String emailStr = email.getText().toString().trim();
        String phoneStr = phone.getText().toString().trim();
        String streetStr = street.getText().toString().trim();
        streetStr = (streetStr.length() == 0) ? Pharmacy.DEFAULT_ADDRESS : streetStr;
        if (nameStr.length() == 0) {
          Toast.makeText(getApplicationContext(), "Please ensure you've entered a valid name", Toast.LENGTH_SHORT)
              .show();
        } else if (!isValidEmail(emailStr)) {
          Toast.makeText(getApplicationContext(), "Please ensure you've entered a valid email", Toast.LENGTH_SHORT)
              .show();
        } else if (!isValidPhone(phoneStr)) {
          Toast.makeText(getApplicationContext(), "Please ensure you've entered a valid phone", Toast.LENGTH_SHORT)
              .show();
        }
        else {
          // We are good to add our pharmacy
          Pharmacy newPh = new Pharmacy(nameStr, emailStr, phoneStr, streetStr);
          newPh.setId(phAdapter.insertPh(new Pharmacy(nameStr, emailStr, phoneStr, streetStr)));
          if (newPh.getId() > 0) {
            String message = "Added to Pharmacy DB on " + df.format(Calendar.getInstance().getTime());
            hAdapter.insertHis(new HistoryItem(nameStr, message, "P"));
            d.dismiss();
            currFrag = 1;
            MainActivity.getInstance().mViewPager.setCurrentItem(currFrag);
            RefreshableFragment f = (RefreshableFragment) frags[currFrag];
            if (f != null) {
              // Should never be null, but just in case...
              f.repopulateAdapter();
            }
          } else {
            String message = "Couldn't add your Pharmacy to the DB. Please ensure all Pharmacy names are unique";
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
          }
        }
      }
    });
    d.show();
  }

  /**
   * Helper method to make the patient dialog from the adding Rx This has the spinner to make a
   * selection amongst doctors.
   * 
   * @param context the context for the dialog
   * @param v the calling view (to set the text to the selected patient)
   */
  protected void openPatientSelectDialog(final Context context, final View v) {
    ArrayList<Patient> pats = paAdapter.getAllPats();
    if (pats.size() <= 1) {
      Toast.makeText(context, "Please add a patient; this must be done before adding an Rx", Toast.LENGTH_SHORT).show();
    } else {
      final ArrayAdapter<Patient> aa =
          new ArrayAdapter<Patient>(getApplicationContext(), android.R.layout.simple_spinner_item, pats);
      final Spinner spinner = new Spinner(getApplicationContext());
      AlertDialog.Builder builder = new AlertDialog.Builder(context);
      builder.setView(spinner);
      final Dialog d = builder.create();
      // Making the dialog appear at the top rather than center; looks
      // better imo
      d.getWindow().setGravity(Gravity.TOP);
      spinner.setAdapter(aa);
      spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
          Patient pa = (Patient) spinner.getSelectedItem();
          if (pa.getId() == 1) {
            // We hit our Dummy Pharmacy, do nothing.
          } else {
            EditText et = (EditText) v;
            et.setText(Patient.makeStringFromPatient(pa));
            d.dismiss();
          }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
          // Nothing selected; do nothing
        }

      });
      d.show();
    }
  }

  /**
   * Helper method to make the select a pharmacy or add one.
   * 
   * @param context the context for the dialog
   * @param v the calling view (EditText in this case)
   */
  protected void openPharmacySelectDialog(final Context context, final View v) {
    ArrayList<Pharmacy> phs = phAdapter.getAllPhs();
    if (phs.size() == 0) {
      // Dummy pharmacy in first spot for the spinner
      Pharmacy adding = new Pharmacy("Select a Pharmacy or Add One", "", "", "");
      adding.setId(phAdapter.insertPh(adding));
      phs.add(adding);
    }
    final ArrayAdapter<Pharmacy> aa =
        new ArrayAdapter<Pharmacy>(getApplicationContext(), android.R.layout.simple_spinner_item, phs);
    final Spinner spinner = new Spinner(getApplicationContext());
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    LinearLayout layout = new LinearLayout(this);
    layout.setOrientation(1);

    final EditText name = new EditText(this);
    final EditText email = new EditText(this);
    final EditText phone = new EditText(this);
    final EditText street = new EditText(this);
    Button add = new Button(this);
    add.setText("Add a New Pharmacy");
    add.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        // Basic input checking; will get better with time
        String nameStr = name.getText().toString().trim();
        String emailStr = email.getText().toString().trim();
        String phoneStr = phone.getText().toString().trim();
        String streetStr = street.getText().toString().trim();
        streetStr = (streetStr.length() == 0) ? Pharmacy.DEFAULT_ADDRESS : streetStr;
        if (nameStr.length() == 0) {
          Toast.makeText(getApplicationContext(), "Please ensure you've entered a valid name", Toast.LENGTH_SHORT)
              .show();
        } else if (!isValidEmail(emailStr)) {
          Toast.makeText(getApplicationContext(), "Please ensure you've entered a valid email", Toast.LENGTH_SHORT)
              .show();
        } else if (!isValidPhone(phoneStr)) {
          Toast.makeText(getApplicationContext(), "Please ensure you've entered a valid phone", Toast.LENGTH_SHORT)
              .show();
        } else {
          // We are good to add our pharmacy
          Pharmacy newPh = new Pharmacy(nameStr, emailStr, phoneStr, streetStr);
          newPh.setId(phAdapter.insertPh(new Pharmacy(nameStr, emailStr, phoneStr, streetStr)));
          if (newPh.getId() > 0) {
            String message = "Added to Pharmacy DB on " + df.format(Calendar.getInstance().getTime());
            hAdapter.insertHis(new HistoryItem(nameStr, message, "P"));
            // Better way to do it, but just wanted to get
            // functionality
            aa.clear();
            aa.addAll(phAdapter.getAllPhs());
            name.setText("");
            email.setText("");
            phone.setText("");
            street.setText("");
            spinner.setSelection(aa.getCount());
            // frags[currFrag].onResume();
          } else {
            String message = "Couldn't add your Pharmacy. Please ensure all Pharmacys have unique name.";
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
          }
        }
      }

    });
    name.setSingleLine();
    email.setSingleLine();
    phone.setSingleLine();
    street.setSingleLine();
    name.setHint("    Unique Name (CVS Boston): ");
    email.setHint("    Email For Refill: ");
    phone.setHint("    Phone: ");
    street.setHint("    Street (123 Oak): ");
    name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
    email.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    phone.setInputType(InputType.TYPE_CLASS_NUMBER);
    street.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
    layout.addView(name);
    layout.addView(email);
    layout.addView(phone);
    layout.addView(street);
    layout.addView(add);
    builder.setView(layout);
    final Dialog d = builder.create();
    spinner.setAdapter(aa);
    spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Pharmacy ph = (Pharmacy) spinner.getSelectedItem();
        if (ph.getId() == 1) {
          // We hit our Dummy Pharmacy, do nothing.
        } else {
          EditText et = (EditText) v;
          et.setText(Pharmacy.makeStringFromPharm(ph));
          d.dismiss();
        }

      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
        // Nothing selected; do nothing
      }

    });
    layout.addView(spinner);
    d.show();
  }

  /**
   * Method to open a dialog from which the user can choose to add a new prescription, doctor,
   * pharmacy or patient.
   * 
   * @param context the context for the dialog
   */
  public void openCustomDialog(final Context context) {
    final Dialog dialog = new Dialog(context);
    dialog.setContentView(R.layout.select_dialog);
    dialog.setTitle("Please choose what to add");
    LinearLayout pill = (LinearLayout) dialog.findViewById(R.id.ll1);
    LinearLayout doc = (LinearLayout) dialog.findViewById(R.id.ll2);
    LinearLayout pharm = (LinearLayout) dialog.findViewById(R.id.ll3);
    LinearLayout patient = (LinearLayout) dialog.findViewById(R.id.ll4);
    pill.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (paAdapter.getSize() <= 1) {
          Toast.makeText(context, "Please add a patient first!", Toast.LENGTH_SHORT).show();
        } else {
          openAddOrEditRxDialog(context, null);
          dialog.dismiss();
        }
      }
    });

    doc.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        openAddDoctorDialog(context);
        dialog.dismiss();
      }
    });
    pharm.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        openAddPharmacyDialog(context);
        dialog.dismiss();
      }
    });
    patient.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (paAdapter.getSize() == 8) {
          Toast.makeText(context,
              "We're sorry, you can't add another patient - there are no colors left. Please remove a patient first.",
              Toast.LENGTH_SHORT).show();
        } else {
          openAddPatientDialog(context);
          dialog.dismiss();
        }

      }
    });
    dialog.show();
  }

  /**
   * Used to create the Dialog box which appears when one hits the + button.
   * 
   * @param context the application context where the dialog should be displayed
   * @param rx the RxItem we are editing if we are editing, or null if we are adding
   */
  public void openAddOrEditRxDialog(final Context context, final RxItem rx) {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    // Now I'm making the scrollview with a linear layout for this badboy
    // (easier to do programatically than in XML)
    ScrollView sv = new ScrollView(this);
    LayoutParams svlp =
        new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            android.view.ViewGroup.LayoutParams.MATCH_PARENT);
    sv.setLayoutParams(svlp);
    LinearLayout layout = new LinearLayout(this);
    layout.setOrientation(1);
    layout.setVerticalScrollBarEnabled(true);
    layout.setHorizontalScrollBarEnabled(false);
    final EditText nameET = new EditText(this);
    final EditText patientET = new EditText(this);
    final EditText sympET = new EditText(this);
    final EditText sideEffectsET = new EditText(this);
    final EditText doseET = new EditText(this);
    final EditText ppdET = new EditText(this);
    final EditText startET = new EditText(this);
    final EditText dbrET = new EditText(this);
    final EditText pharmET = new EditText(this);
    final EditText physET = new EditText(this);
    final EditText rxnumbET = new EditText(this);
    nameET.setHint("   Prescription Name: ");
    patientET.setHint("   Patient: ");
    sympET.setHint("   For treating:  ");
    sideEffectsET.setHint("   Side effects: ");
    doseET.setHint("   Dose: (mg) ");
    ppdET.setHint("   Pills Per Day: ");
    startET.setHint("   Start Date (Click to Pick): ");
    dbrET.setHint("   Days Between Refills: ");
    pharmET.setHint("   Pharmacy (Click to Pick/Add): ");
    physET.setHint("   Doctor (Click to Pick/Add): ");
    rxnumbET.setHint("   RX Number (Optional): ");
    nameET.setSingleLine();
    // To avoid having to deal with keyboard input when you want to pick
    // patient
    patientET.setFocusable(false);
    patientET.setFocusableInTouchMode(false);
    patientET.setSingleLine();
    patientET.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        openPatientSelectDialog(context, v);
      }
    });
    sympET.setSingleLine();
    sideEffectsET.setSingleLine();
    doseET.setSingleLine();
    ppdET.setSingleLine();
    physET.setSingleLine();
    // To avoid having to deal with keyboard popping up when you want to
    // pick date
    startET.setFocusable(false);
    startET.setFocusableInTouchMode(false);
    // To avoid having to deal with keyboard popping up when you want to
    // pick the Dr
    physET.setFocusable(false);
    physET.setFocusableInTouchMode(false);
    // To avoid having to deal with keyboard popping up when you want to
    // pick the Ph
    pharmET.setFocusable(false);
    pharmET.setFocusableInTouchMode(false);
    // Get the calendar for the datepicker listener
    final Calendar myCalendar = Calendar.getInstance();
    // Code adapted from
    // http://stackoverflow.com/questions/14933330/datepicker-how-to-popup-datepicker-when-click-on-edittext
    /**
     * This is the OnDateSetListener for our startET EditText
     */
    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

      @Override
      public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // TODO Auto-generated method stub
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, monthOfYear);
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        startET.setText(df.format(myCalendar.getTime()));
      }
    };
    /**
     * This is where we actually launch the onDateSetListener
     */
    startET.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        if (startET.getText().toString().trim().length() >= 0)
          try {
            myCalendar.setTime(df.parse(startET.getText().toString()));
          } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        else {
          // myCalendar was already set to the right time, proceed
        }
        new DatePickerDialog(MainActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
      }
    });
    /**
     * Now I'm going to make the Spinner for the Doctor selection
     */
    physET.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        openDoctorSelectDialog(context, v);
      }
    });
    dbrET.setSingleLine();
    pharmET.setSingleLine();
    pharmET.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        openPharmacySelectDialog(context, v);
      }
    });
    rxnumbET.setSingleLine();
    nameET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
    physET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
    rxnumbET.setInputType(InputType.TYPE_CLASS_NUMBER);
    doseET.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    dbrET.setInputType(InputType.TYPE_CLASS_NUMBER);
    ppdET.setInputType(InputType.TYPE_CLASS_NUMBER);

    // This means we are editing an existing Rx
    if (rx != null) {
      nameET.setText(rx.getName());
      patientET.setText(rx.getPatientString());
      sympET.setText(rx.getSymptoms());
      sideEffectsET.setText(rx.getSideEffects());
      doseET.setText(Double.toString(rx.getDose()));
      ppdET.setText(Integer.toString(rx.getPillsPerDay()));
      startET.setText(df.format(rx.getStartDate()));
      dbrET.setText(Integer.toString(rx.getDaysBetweenRefills()));
      pharmET.setText(rx.getPhString());
      physET.setText(rx.getDocString());
      rxnumbET.setText(rx.getRxNumb());
    }

    layout.addView(nameET);
    layout.addView(patientET);
    layout.addView(sympET);
    layout.addView(sideEffectsET);
    layout.addView(doseET);
    layout.addView(ppdET);
    layout.addView(startET);
    layout.addView(dbrET);
    layout.addView(pharmET);
    layout.addView(physET);
    layout.addView(rxnumbET);
    sv.setFillViewport(true);
    sv.setVerticalScrollBarEnabled(true);
    // Set the ScrollView to contain this LinearLayout (I didn't wanna do it
    // all in XML; stackOverflow suggested doing it in code sooo...)
    sv.addView(layout);
    // Set the builder's View to the ScrollView
    builder.setView(sv);
    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int id) {
        // Adios amigos!
        return;
      }
    });
    builder.setPositiveButton("OK", null);
    // This is to make it so hitting OK on an invalid input doesn't close
    // the dialog!
    final AlertDialog dialog = builder.create();
    // Code adapted from
    // http://stackoverflow.com/questions/2620444/how-to-prevent-a-dialog-from-closing-when-a-button-is-clicked
    dialog.setOnShowListener(new DialogInterface.OnShowListener() {

      @Override
      public void onShow(DialogInterface d) {

        Button b = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        b.setOnClickListener(new View.OnClickListener() {
          // The code to add an entry can be found here
          @Override
          public void onClick(View view) {
            // Perform our checks; for now, just that they are
            // non-empty (we can and will expand on this shortly)
            if (nameET.getText().toString().trim().length() == 0) {
              Toast.makeText(getApplicationContext(), "Please ensure you have a valid name", Toast.LENGTH_SHORT).show();
            } else if (patientET.getText().toString().trim().length() == 0) {
              Toast.makeText(getApplicationContext(), "Please ensure you've entered a valid patient",
                  Toast.LENGTH_SHORT).show();
            } else if (sympET.getText().toString().trim().length() == 0) {
              Toast
                  .makeText(getApplicationContext(), "Please ensure you've entered valid symptoms", Toast.LENGTH_SHORT)
                  .show();
            } else if (sideEffectsET.getText().toString().trim().length() == 0) {
              Toast.makeText(getApplicationContext(), "Please ensure you've entered valid side effects",
                  Toast.LENGTH_SHORT).show();
            } else if (!isValidDouble(doseET)) {
              Toast.makeText(getApplicationContext(), "Please ensure you've entered a valid dose in mg",
                  Toast.LENGTH_SHORT).show();
            } else if (!isValidInt(ppdET)) {
              Toast.makeText(getApplicationContext(), "Please ensure you've entered a valid # of pills per day",
                  Toast.LENGTH_SHORT).show();
            } else if (startET.getText().toString().trim().length() == 0) {
              Toast.makeText(getApplicationContext(), "Please ensure you've entered a valid start date",
                  Toast.LENGTH_SHORT).show();
            } else if (!isValidInt(dbrET)) {
              Toast.makeText(getApplicationContext(), "Please ensure you've entered valid days between refills",
                  Toast.LENGTH_SHORT).show();
            } else if (pharmET.getText().toString().trim().length() == 0) {
              Toast.makeText(getApplicationContext(), "Please ensure you've entered a valid pharmacy",
                  Toast.LENGTH_SHORT).show();
            } else if (physET.getText().toString().trim().length() == 0) {
              Toast.makeText(getApplicationContext(), "Please ensure you've entered a valid physician",
                  Toast.LENGTH_SHORT).show();
            } else {
              // None of our inputs are empty;
              // We insert a new RxItem into the database
              // See if we should use the value in the ET or
              // default value
              if (rxnumbET.getText().toString().trim().length() == 0) {
                // Dummy value for now (not enough time to make
                // it actually optional)
                rxnumbET.setText(DEFAULT_RX_NUMBER);
              }
              try {
                Date lastRefillDate = null;
                String name = nameET.getText().toString();// name
                String patient = patientET.getText().toString();// patient
                String symp = sympET.getText().toString(); // symptoms
                String sideEffects = sideEffectsET.getText().toString();// side effects
                double dose = Double.parseDouble(doseET.getText().toString());// dose
                int ppd = Integer.parseInt(ppdET.getText().toString()); // pills per day
                Date start = df.parse(startET.getText().toString());// start date
                int dbr = Integer.parseInt(dbrET.getText().toString()); // day between refills
                String pharm = pharmET.getText().toString(); // pharmacy
                String doc = physET.getText().toString();
                String rxnumb = rxnumbET.getText().toString();
                // This means we were editing
                if (rx != null) {
                  // Need to see if we should change
                  // lastRefillDate
                  boolean dateChanged;
                  if (!start.equals(rx.getStartDate())) {
                    // This means the date has been updated;
                    // must update last Refill date to match
                    Calendar myCal = Calendar.getInstance();
                    myCal.setTime(start);
                    myCal.add(Calendar.DAY_OF_YEAR, rx.getDaysBetweenRefills());
                    // If we changed the date but it shouldn't impact next refill
                    if (rx.getNextRefillDate().compareTo(myCal.getTime()) > 0) {
                      Toast
                          .makeText(
                              getApplicationContext(),
                              "Start date has been updated, but your Last Refill date cannot be moved backwards. If you made a mistake before Refilling, please remove and re-add this Rx.",
                              Toast.LENGTH_LONG).show();
                      dateChanged = true;
                      lastRefillDate = rx.getLastRefill();
                    } else {
                      dateChanged = true;
                      lastRefillDate = start;
                    }
                  } else {
                    // We have not updated the date, so we
                    // must keep the old last refill date
                    lastRefillDate = rx.getLastRefill();
                    dateChanged = false;
                  }
                  // Check if we should update
                  if (rx.shouldUpdateRx(name, patient, symp, sideEffects, dose, ppd, dbr, pharm, doc, rxnumb)
                      || dateChanged) {
                    rxAdapter.updateRx(rx.getId(), name, patient, symp, sideEffects, dose, ppd, start, dbr, pharm, doc,
                        rxnumb, lastRefillDate);
                    scAdapter.updateAllSchWithPatientAndName(rx.getPatient().getColor(), Patient
                        .getColorIntFromPatientString(patient), rx.getName(), name);
                    String message = "Updated in Prescriptions DB on " + df.format(Calendar.getInstance().getTime());
                    hAdapter.insertHis(new HistoryItem(name, message, "R"));
                  } else {
                    // We hit OK but didn't want to update;
                    // don't do anything (or add to history)
                  }
                }
                // This means we we were inserting a new one
                else {
                  lastRefillDate = start;
                  rxAdapter.insertRx(new RxItem(name, Patient.makePatientFromString(patient), symp, sideEffects, dose,
                      ppd, start, dbr, Pharmacy.makePharmFromString(pharm), Doctor.makeDocFromString(doc), rxnumb,
                      lastRefillDate));
                  String message = "Added to Prescriptions DB on " + df.format(Calendar.getInstance().getTime());
                  hAdapter.insertHis(new HistoryItem(name, message, "R"));
                  // Switch to the Prescriptions tab if we've
                  // added a prescription
                  MainActivity.getInstance().mViewPager.setCurrentItem(0);

                }
                // update the view
                currFrag = 0;
                MainActivity.getInstance().mViewPager.setCurrentItem(currFrag);
                RefreshableFragment f = (RefreshableFragment) frags[currFrag];
                if (f != null) {
                  // Should never be null, but just in case...
                  f.repopulateAdapter();
                }
              } catch (NumberFormatException e) {
                Toast.makeText(getApplicationContext(), "Sorry,  your Rx couldn't be added. Please check all fields.",
                    Toast.LENGTH_SHORT).show();
                e.printStackTrace();
              } catch (ParseException e) {
                Toast.makeText(getApplicationContext(), "Sorry,  your Rx couldn't be added. Please check all fields.",
                    Toast.LENGTH_SHORT).show();
                e.printStackTrace();
              }
              dialog.dismiss();
            }
          }
        });
      }
    });
    dialog.show();
  }

  /**
   * Helper method to create AlertDialogs given a title, message, positive OnClickListner, and
   * negative OnClickListener Deprecation warnings suppressed because the code still works on all
   * versions of Android we tested
   * 
   * @param title the alert dialog's desired title
   * @param message the alert dialog's desired message
   * @param b2Text the alert dialog's b2 text
   * @param b2OCL the alert dialog's positive OnClickListener, b2
   * @param b1Text the alert dialog's b1 text
   * @param b1OCL the alert dialog's negative OnClickListener, b1 Code adapted from Matt's ARK
   *        AlertDialog
   */
  @SuppressWarnings("deprecation")
  public static void alertMessage(Context context, String title, String message, String b2Text,
      DialogInterface.OnClickListener b2OCL, String b1Text, DialogInterface.OnClickListener b1OCL) {
    AlertDialog alertDialog = new AlertDialog.Builder(context).create();
    alertDialog.setTitle(title);
    alertDialog.setMessage(message);
    alertDialog.setButton2(b2Text, b2OCL);
    alertDialog.setButton(b1Text, b1OCL);
    alertDialog.show();
  }

  /**
   * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the
   * sections/tabs/pages.
   */
  public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
      super(fm);
    }

    /**
     * Method to get a fragment for a given position in a page.
     * 
     * @param position the requested fragment position
     * @return a fragment for the given position
     */
    @Override
    public Fragment getItem(int position) {
      // getItem is called to instantiate the fragment for the given page.
      // Return a PlaceholderFragment (defined as a static inner class
      // below).
      if (position <= 1)
        return frags[position];
      return null;
    }

    /**
     * Method to get the count of tabs.
     * 
     * @return the number of tabs
     */
    @Override
    public int getCount() {
      // Show 2 total pages.
      return 2;
    }
  }

  /**
   * Method to refresh the fragment which is just completed.
   * 
   * @see RefreshableFragment.OnCompleteListener
   */
  @Override
  public void onComplete(boolean b) {
    if (b) {
      RefreshableFragment f = (RefreshableFragment) frags[currFrag];
      if (f != null) {
        // Should never be null, but just in case...
        f.repopulateAdapter();
      }
    }
  }

  /**
   * Method to implement OnSharedPreferenceChangeListener. Used to refresh the appropriate fragment
   * and make fragments reflect settings changes.
   * 
   * @param sharedPreferences the app's shared preferences
   * @param key the shared preference that just changed
   */
  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    if (key.equals(HistoryDBAdapter.histKey)) {
      HistoryDBAdapter.histCount = sharedPreferences.getInt(key, 100);
      RefreshableFragment f = (RefreshableFragment) frags[currFrag];
      if (f != null) {
        f.repopulateAdapter();
      }
    } else if (key.equals(RxNotificationManager.countKey)) {
      RxNotificationManager.numDays = sharedPreferences.getInt(key, 5);
    } else if (key.equals(RxDBAdapter.namesortKey)) {
      RxDBAdapter.shouldSortByName = sharedPreferences.getBoolean(key, false);
      RefreshableFragment f = (RefreshableFragment) frags[currFrag];
      if (f != null) {
        f.repopulateAdapter();
      }
    } else if (key.equals(RxDBAdapter.patientsortKey)) {
      RxDBAdapter.shouldSortByPatient = sharedPreferences.getBoolean(key, false);
      RefreshableFragment f = (RefreshableFragment) frags[currFrag];
      if (f != null) {
        f.repopulateAdapter();
      }
    }
  }

}
