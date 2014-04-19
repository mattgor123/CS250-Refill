package cs250.spring14.refill;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.InputType;
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
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	protected static final DateFormat df = new SimpleDateFormat("MM/dd/yyyy",Locale.US);
	private final String[] tabs = new String[]{"Prescriptions","History"};
	private Fragment[] frags;
	public static RxDBAdapter rxAdapter;
	public static DoctorDBAdapter drAdapter;
	public static int currFrag;
	private static MainActivity _instance;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//Set up the fragments
		frags = new Fragment[]{new RxFragment(), new HistoryFragment()};
		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
						MainActivity.currFrag = position;
						frags[currFrag].onResume();
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (String tName : tabs) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(tName)
					.setTabListener(this));
		}
		
		//Rx adapter stuff
		rxAdapter = new RxDBAdapter(this);
		rxAdapter.open();
		//Dr adapter stuff
		drAdapter = new DoctorDBAdapter(this);
		drAdapter.open();
		_instance = this;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		 switch (item.getItemId()) {
	        case R.id.action_add:
	            openAddDialog(this,null);
	            return true;
	        case R.id.action_settings:
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
		 }
	}
	/**
	 * Used to create the Dialog box which appears when one hits the + button.
	 * @param context the application context where the dialog should be displayeed
	 */
	public void openAddDialog(final Context context, final RxItem rx) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		//Now I'm making the scrollview with a linear layout for this badboy (easier to do programatically than in XML)
		ScrollView sv = new ScrollView(this);
		LayoutParams svlp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        sv.setLayoutParams(svlp);
		LinearLayout layout= new LinearLayout(this);
	    layout.setOrientation(1); 
	    layout.setVerticalScrollBarEnabled(true);
	    layout.setHorizontalScrollBarEnabled(false);
	    builder.setMessage("Add a Prescription");
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
	    patientET.setHint("   Patient Name: ");
	    sympET.setHint("   For treating:  ");
	    sideEffectsET.setHint("   Side effects: ");
	    doseET.setHint("   Dose: (mg) ");
	    ppdET.setHint("   Pills Per Day: ");
	    startET.setHint("   Start Date (Click to Pick): ");
	    dbrET.setHint("   Days Between Refills: ");
	    pharmET.setHint("   Pharmacy: ");
	    physET.setHint("   Doctor (Click to Pick/Add): ");
	    rxnumbET.setHint("   RX Number: ");
	    nameET.setSingleLine();
	    patientET.setSingleLine();
	    sympET.setSingleLine();
	    sideEffectsET.setSingleLine();
	    doseET.setSingleLine();
	    ppdET.setSingleLine();
	    //To avoid having to deal with keyboard popping up when you want to pick date
	    startET.setFocusable(false);
	    startET.setFocusableInTouchMode(false);
	    //To avoid having to deal with keyboard popping up when you want to pick the Dr
	    physET.setFocusable(false);
	    physET.setFocusableInTouchMode(false);
	    //Get the calendar for the datepicker listener
	    final Calendar myCalendar = Calendar.getInstance();
	    //Code adapted from http://stackoverflow.com/questions/14933330/datepicker-how-to-popup-datepicker-when-click-on-edittext
	    /**
	     * This is the OnDateSetListener for our startET EditText
	     */
	    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

	        @Override
	        public void onDateSet(DatePicker view, int year, int monthOfYear,
	                int dayOfMonth) {
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
	                // TODO Auto-generated method stub
	                new DatePickerDialog(MainActivity.this, date, myCalendar
	                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
	                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
	            }
	        });
	    /**
	     * Now I'm going to make the Spinner for the Doctor selection
	     */
	    physET.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				makeDoctorDialog(context,v);
			}
	    });
	    dbrET.setSingleLine();
	    pharmET.setSingleLine();
	    rxnumbET.setSingleLine();
	    nameET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
	    patientET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
	    physET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
	    rxnumbET.setInputType(InputType.TYPE_CLASS_NUMBER);
	    doseET.setInputType(InputType.TYPE_CLASS_NUMBER);
	    dbrET.setInputType(InputType.TYPE_CLASS_NUMBER);
	    ppdET.setInputType(InputType.TYPE_CLASS_NUMBER);
	    
	    //This means we are editing an existing Rx
	    if (rx != null)
		{
	    	nameET.setText(rx.getName());
			patientET.setText(rx.getPatient());
			sympET.setText(rx.getSymptoms());
			sideEffectsET.setText(rx.getSideEffects());
			doseET.setText(Integer.toString(rx.getDose()));
			ppdET.setText(Integer.toString(rx.getPillsPerDay()));
		    startET.setText(df.format(rx.getStartDate()));
		    dbrET.setText(Integer.toString(rx.getDaysBetweenRefills()));
		    pharmET.setText(rx.getPharmacy());
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
        //Set the ScrollView to contain this LinearLayout (I didn't wanna do it all in XML; stackOverflow suggested doing it in code sooo...)
        sv.addView(layout);
	    //Set the builder's View to the ScrollView
        builder.setView(sv);
	    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   //Adios amigos!
	        	   return;
	           }
	       });
	    builder.setPositiveButton("OK", null);
	    //This is to make it so hitting OK on an invalid input doesn't close the dialog!
		final AlertDialog dialog = builder.create();
		//Code adapted from http://stackoverflow.com/questions/2620444/how-to-prevent-a-dialog-from-closing-when-a-button-is-clicked
		dialog.setOnShowListener(new DialogInterface.OnShowListener() {

	        @Override
	        public void onShow(DialogInterface d) {

	            Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
	            b.setOnClickListener(new View.OnClickListener() {
	            	//The code to add an entry can be found here
	                @Override
	                public void onClick(View view) {
	                	//Perform our checks; for now, just that they are non-empty (we can and will expand on this shortly)
	                	if (nameET.getText().toString().trim().length()==0) {
	 	        		   Toast.makeText(getApplicationContext(), "Please ensure you have a valid name", Toast.LENGTH_SHORT).show();
	                	}
	                	else if(patientET.getText().toString().trim().length() == 0){Toast.makeText(getApplicationContext(), "Please ensure you've entered a valid patient",Toast.LENGTH_SHORT).show();}
	                	else if(sympET.getText().toString().trim().length() == 0){Toast.makeText(getApplicationContext(), "Please ensure you've entered valid symptoms",Toast.LENGTH_SHORT).show();}
	                	else if(sideEffectsET.getText().toString().trim().length() == 0){Toast.makeText(getApplicationContext(), "Please ensure you've entered valid side effects",Toast.LENGTH_SHORT).show();}
	                	else if(doseET.getText().toString().trim().length() == 0){Toast.makeText(getApplicationContext(), "Please ensure you've entered a valid dose in mg",Toast.LENGTH_SHORT).show();}
	                	else if(ppdET.getText().toString().trim().length() == 0){Toast.makeText(getApplicationContext(), "Please ensure you've entered a valid # of pills per day",Toast.LENGTH_SHORT).show();}
	                	else if(startET.getText().toString().trim().length() == 0){Toast.makeText(getApplicationContext(), "Please ensure you've entered a valid start date",Toast.LENGTH_SHORT).show();}
	                	else if(dbrET.getText().toString().trim().length() == 0){Toast.makeText(getApplicationContext(), "Please ensure you've entered valid days between refills",Toast.LENGTH_SHORT).show();}
	                	else if(pharmET.getText().toString().trim().length() == 0){Toast.makeText(getApplicationContext(), "Please ensure you've entered a valid pharmacy",Toast.LENGTH_SHORT).show();}
	                	else if(physET.getText().toString().trim().length() == 0){Toast.makeText(getApplicationContext(), "Please ensure you've entered a valid physician",Toast.LENGTH_SHORT).show();}
	                	else if(rxnumbET.getText().toString().trim().length() == 0){Toast.makeText(getApplicationContext(), "Please ensure you've entered a valid Rx number",Toast.LENGTH_SHORT).show();}
	                	else {
	                		//None of our inputs are empty;
	                		//We insert a new RxItem into the database
	                		try {
	                			Date lastRefillDate = null;
	                			String name = nameET.getText().toString();//name
	                			String patient = patientET.getText().toString();//patient
	                			String symp = sympET.getText().toString(); //symptoms
	                			String sideEffects = sideEffectsET.getText().toString();//side effects
	                			int dose = Integer.parseInt(doseET.getText().toString());//dose
	                			int ppd = Integer.parseInt(ppdET.getText().toString()); //pills per day
	                			Date start = df.parse(startET.getText().toString());//start date
	                			int dbr = Integer.parseInt(dbrET.getText().toString()); //day between refills
	                			String pharm = pharmET.getText().toString(); //pharmacy
	                			String doc = physET.getText().toString();
	                			//For debugging purposes; we'll get rid of this later
	                			assert (doc!=null);
	                			String rxnumb = rxnumbET.getText().toString();
	                			//This means we were editing
	                			if (rx != null)
	                			{
	                				lastRefillDate = rx.getLastRefill();
	                				rxAdapter.updateRx(rx.getId(), name, patient, symp, sideEffects, dose, ppd, start, dbr, pharm, doc, rxnumb, lastRefillDate);
	                			}
	                			//This means we we were inserting a new one
	                			else {
	                				lastRefillDate = start;
	                				rxAdapter.insertRx(new RxItem(name, patient, symp, sideEffects, dose, ppd, start, dbr, pharm, makeDocFromString(doc), rxnumb, lastRefillDate));									
	                			}
								//Toast.makeText(getApplicationContext(), "Added " + nameET.getText().toString() + " to the Rx Database, now " + rxAdapter.getAllRxs().size() + " items in the DB", Toast.LENGTH_SHORT).show();
								//Manually call onResume to ensure that we update the view
								frags[currFrag].onResume();
							} catch (NumberFormatException e) {
								Toast.makeText(getApplicationContext(), "Sorry,  your Rx couldn't be added. Please check all fields.",Toast.LENGTH_SHORT).show();
								e.printStackTrace();
							} catch (ParseException e) {
								Toast.makeText(getApplicationContext(), "Sorry,  your Rx couldn't be added. Please check all fields.",Toast.LENGTH_SHORT).show();
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

	public static Doctor makeDocFromString(String string) {
		String[] tokens = string.split(" :: ");
		if(tokens.length != 3)
		{
			//Something got screwed up
			return null;
		}
		else
		{
			return new Doctor(tokens[0],tokens[1],tokens[2]);
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		rxAdapter.open();
		drAdapter.open();
	}
	@Override
	public void onStop() {
		super.onStop();
		rxAdapter.close();
		drAdapter.close();
	}
	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	protected void makeDoctorDialog(Context context,final View v) {
		ArrayList<Doctor> drs = drAdapter.getAllDrs();
		if (drs.size() == 0) {
			//Dummy doctor in first spot for the spinner
			Doctor adding = new Doctor("","","Select a Doctor or Add One");
			adding.setId(drAdapter.insertDr(adding));
			drs.add(adding);
		}
		final ArrayAdapter<Doctor> aa = new ArrayAdapter<Doctor>(getApplicationContext(),android.R.layout.simple_spinner_item, drs);
		final Spinner spinner = new Spinner(getApplicationContext());
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		LinearLayout layout= new LinearLayout(this);
	    layout.setOrientation(1); 
		// TODO Auto-generated method stub
		final EditText name = new EditText(this);
		final EditText email = new EditText(this);
		final EditText phone = new EditText(this);
		Button add = new Button(this);
		add.setText("Add a New Doctor");
		add.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				//Basic input checking; will get better with time
				String nameStr = name.getText().toString().trim();
				String emailStr = email.getText().toString().trim();
				String phoneStr = phone.getText().toString().trim();
				if(nameStr.length() == 0){Toast.makeText(getApplicationContext(), "Please ensure you've entered a valid name",Toast.LENGTH_SHORT).show();}
				if(emailStr.length() == 0){Toast.makeText(getApplicationContext(), "Please ensure you've entered a valid email",Toast.LENGTH_SHORT).show();}
				if(phoneStr.length() == 0){Toast.makeText(getApplicationContext(), "Please ensure you've entered a valid phone",Toast.LENGTH_SHORT).show();}
				else {
					//We are good to add our Doctor
					Doctor newDoc = new Doctor(nameStr,emailStr,phoneStr);
					newDoc.setId(drAdapter.insertDr(new Doctor(nameStr,emailStr,phoneStr)));
					//Better way to do it, but just wanted to get functionality
					aa.clear();
					aa.addAll(drAdapter.getAllDrs());
					name.setText("");
					email.setText("");
					phone.setText("");
					spinner.setSelection(aa.getCount());
				}
			}
			
		});
		name.setSingleLine();
		email.setSingleLine();
		phone.setSingleLine();
		name.setHint("    Name: ");
		email.setHint("    Email: ");
		phone.setHint("    Phone: ");
		name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
		email.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);		
		phone.setInputType(InputType.TYPE_CLASS_PHONE);
		layout.addView(name);
		layout.addView(email);
		layout.addView(phone);
		layout.addView(add);
		builder.setView(layout);
		final Dialog d = builder.create();
		spinner.setAdapter(aa);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent,
					View view, int position, long id) {
				Doctor dr = (Doctor) spinner.getSelectedItem();
				if (dr.getId() == 1)
				{
					//We hit our Dummy Doctor, do nothing.
				}
				else {
					EditText et = (EditText) v;
					et.setText(dr.getName() + " :: " + dr.getEmail() + " :: " + dr.getPhone());
					d.dismiss();					
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				//Nothing selected; do nothing						
			}
			
		});
		layout.addView(spinner);
		d.show();
	}
	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}
	
	public static MainActivity getInstance()
	{
		return _instance;
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			if (position <= 1)
				return frags[position];
			return null;
		}

		@Override
		public int getCount() {
			// Show 2 total pages.
			return 2;
		}
	}
}
