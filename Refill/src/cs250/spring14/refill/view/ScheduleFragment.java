package cs250.spring14.refill.view;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import cs250.spring14.refill.MainActivity;
import cs250.spring14.refill.R;
import cs250.spring14.refill.core.Doctor;
import cs250.spring14.refill.core.HistoryItem;
import cs250.spring14.refill.core.Patient;
import cs250.spring14.refill.core.RxItem;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class ScheduleFragment extends DialogFragment {
	
	GridView calendar;
	ScheduleAdapter scheduleAdap;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().setTitle("Schedule");
		
		View rootView = inflater.inflate(R.layout.fragment_schedule, container,
				false);
		
		calendar = (GridView) rootView.findViewById(R.id.gridView1);
		
		scheduleAdap = new ScheduleAdapter(rootView.getContext());
		
		calendar.setAdapter(scheduleAdap);

		calendar.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	// we shouldn't do anything with the first row and first column 
	        	if (position > 8 && position%8 != 0){
	        		openScheduleRxDialog(getActivity(), v);
	        		//Toast.makeText(getActivity(), "" + position, Toast.LENGTH_SHORT).show();
		            //v.setBackgroundColor(-13159);
	        	}
	        }
	    });

		
		return rootView;
	}
	
	protected void openScheduleRxDialog(final Context context, final View parent){
	    final Dialog dialog = new Dialog(context);
	    int color;
	    dialog.setTitle("Please add a patient");
	    dialog.setContentView(R.layout.schedule_dialog);
	    final Spinner spinner = (Spinner) dialog.findViewById(R.id.Rx_Spinner);
	    Button ok = (Button) dialog.findViewById(R.id.ok);
	    ArrayList<RxItem> rxList;
		try {
			rxList = MainActivity.rxAdapter.getAllRxs();
			final ArrayAdapter<RxItem> aa =
			        new ArrayAdapter<RxItem>(getActivity(), android.R.layout.simple_spinner_item, rxList);
			    
			    spinner.setAdapter(aa);
			    spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			        @Override
			        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			          RxItem rx = (RxItem) spinner.getSelectedItem();
			          if (rx.getId() == 1) {
			            // We hit our Dummy Doctor, do nothing.
			          } else {
			            //color = rx.getPatient().getColor();
			            //return color;
			            //dialog.dismiss();
			          }
			        }

			        @Override
			        public void onNothingSelected(AdapterView<?> parent) {
			          // Nothing selected; do nothing
			        }

			      });
			    ok.setOnClickListener(new OnClickListener() {
			      @Override
			      public void onClick(View v) {
			    	  if (spinner.getSelectedItem() != null){
			    		  parent.setBackgroundColor(((RxItem)spinner.getSelectedItem()).getPatient().getColor());
			    		  TextView txt = new TextView(getActivity());
			    		  txt.setText(((RxItem)spinner.getSelectedItem()).getName().substring(0, 2));
			    		  ((LinearLayout)parent).addView(txt);
			  	          dialog.dismiss();
			    	  }
			      }
			    });
			    dialog.show();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	    
	  }
}
