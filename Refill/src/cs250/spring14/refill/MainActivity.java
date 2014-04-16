package cs250.spring14.refill;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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
	            openDialog(this);
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
	private void openDialog(Context context) {	
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		//Now I'm making the linear layout for this badboy (easier to do programatically than in XML)
		LinearLayout layout= new LinearLayout(this);
	    layout.setOrientation(1); 
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
	    final EditText mdPhoneET = new EditText(this);
	    final EditText rxnumbET = new EditText(this);
	    nameET.setHint("   Prescription Name: ");
	    patientET.setHint("   Patient Name: ");
	    sympET.setHint("   Symptoms: ");
	    sideEffectsET.setHint("   Side effects: ");
	    doseET.setHint("   Dose: (mg) ");
	    ppdET.setHint("   Pills Per Day: ");
	    startET.setHint("   Start Date: (mm/dd/yyyy): ");
	    dbrET.setHint("   Days Between Refills: ");
	    pharmET.setHint("   Pharmacy: ");
	    physET.setHint("   Physician: ");
	    mdPhoneET.setHint("   MD Phone Number: ");
	    rxnumbET.setHint("   RX Number: ");
	    nameET.setSingleLine();
	    patientET.setSingleLine();
	    sympET.setSingleLine();
	    sideEffectsET.setSingleLine();
	    doseET.setSingleLine();
	    ppdET.setSingleLine();
	    //To avoid having to deal with keyboard popping up when you want to pick date
	    startET.setFocusable(false);
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
	            startET.setText(MainActivity.df.format(myCalendar.getTime()));
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
	    dbrET.setSingleLine();
	    pharmET.setSingleLine();
	    physET.setSingleLine();
	    mdPhoneET.setSingleLine();
	    rxnumbET.setSingleLine();
	    mdPhoneET.setInputType(InputType.TYPE_CLASS_PHONE);
	    rxnumbET.setInputType(InputType.TYPE_CLASS_NUMBER);
	    doseET.setInputType(InputType.TYPE_CLASS_NUMBER);
	    dbrET.setInputType(InputType.TYPE_CLASS_NUMBER);
	    ppdET.setInputType(InputType.TYPE_CLASS_NUMBER);
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
	    layout.addView(mdPhoneET);
	    layout.addView(rxnumbET);
	    //Set the dialog to this linear layout (I didn't wanna do it all in XML; stackOverflow suggested doing it in code sooo...)
	    builder.setView(layout);
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
	                	else if(sympET.getText().toString().trim().length() == 0){Toast.makeText(getApplicationContext(), "Please ensure you've entered a valid symp",Toast.LENGTH_SHORT).show();}
	                	else if(sideEffectsET.getText().toString().trim().length() == 0){Toast.makeText(getApplicationContext(), "Please ensure you've entered a valid sideEffects",Toast.LENGTH_SHORT).show();}
	                	else if(doseET.getText().toString().trim().length() == 0){Toast.makeText(getApplicationContext(), "Please ensure you've entered a valid dose",Toast.LENGTH_SHORT).show();}
	                	else if(ppdET.getText().toString().trim().length() == 0){Toast.makeText(getApplicationContext(), "Please ensure you've entered a valid ppd",Toast.LENGTH_SHORT).show();}
	                	else if(startET.getText().toString().trim().length() == 0){Toast.makeText(getApplicationContext(), "Please ensure you've entered a valid start",Toast.LENGTH_SHORT).show();}
	                	else if(dbrET.getText().toString().trim().length() == 0){Toast.makeText(getApplicationContext(), "Please ensure you've entered a valid dbr",Toast.LENGTH_SHORT).show();}
	                	else if(pharmET.getText().toString().trim().length() == 0){Toast.makeText(getApplicationContext(), "Please ensure you've entered a valid pharm",Toast.LENGTH_SHORT).show();}
	                	else if(physET.getText().toString().trim().length() == 0){Toast.makeText(getApplicationContext(), "Please ensure you've entered a valid phys",Toast.LENGTH_SHORT).show();}
	                	else if(mdPhoneET.getText().toString().trim().length() == 0){Toast.makeText(getApplicationContext(), "Please ensure you've entered a valid mdPhone",Toast.LENGTH_SHORT).show();}
	                	else if(rxnumbET.getText().toString().trim().length() == 0){Toast.makeText(getApplicationContext(), "Please ensure you've entered a valid rxnumb",Toast.LENGTH_SHORT).show();}
	                	else {
	                		//None of our inputs are empty! We can begin to process the Rx and possibly even add it.
	                		Toast.makeText(getApplicationContext(), "We would add something!", Toast.LENGTH_SHORT).show();
	                		dialog.dismiss();
	                	}
	                }
	            });
	        }
	       });
		dialog.show();	
		}
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 99:
		   // set date picker as current date
		   return new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
			
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
					Toast.makeText(getApplicationContext(), "You chose y: " + year + ", m: " + monthOfYear + ", d: " + dayOfMonth, Toast.LENGTH_SHORT).show();
			}
		}, 
         	Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
		}
		return null;
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

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
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
			return frags[position];
		}

		@Override
		public int getCount() {
			// Show 2 total pages.
			return 2;
		}
	}
}
