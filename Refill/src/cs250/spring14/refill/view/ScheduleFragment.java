package cs250.spring14.refill.view;

import java.text.ParseException;
import java.util.ArrayList;
import cs250.spring14.refill.MainActivity;
import cs250.spring14.refill.R;
import cs250.spring14.refill.core.RxItem;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class ScheduleFragment extends DialogFragment {
	GridView grid;
	ScheduleAdapter scheduleAdap;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().setTitle("Schedule");
		
		View rootView = inflater.inflate(R.layout.fragment_schedule, container,
				false);
		
		grid = (GridView) rootView.findViewById(R.id.gridView1);
		
		scheduleAdap = new ScheduleAdapter(rootView.getContext());
		
		grid.setAdapter(scheduleAdap);

		grid.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	// we shouldn't do anything with the first row and first column 
	        	if (position > 8 && position%8 != 0){
	        		TextView txt = (TextView) ((LinearLayout)v).getChildAt(0);
	        		if (txt.getText() == "|         |")
	        		{
	        			openScheduleRxDialog(getActivity(), v, false);
	        		}else{
	        			openScheduleRxDialog(getActivity(), v, true);
	        		}
	        		for (int i = 0; i < ((LinearLayout)v).getChildCount(); i++){
	        			TextView txt2 = (TextView) ((LinearLayout)v).getChildAt(i);
	        			Toast.makeText(getActivity(), txt2.getText(),Toast.LENGTH_SHORT).show();
	        		}        		
	        	}
	        		
	        		//Toast.makeText(getActivity(), "" + position, Toast.LENGTH_SHORT).show();
		            //v.setBackgroundColor(-13159);
	        }
	    });
		return rootView;
	}
	
	protected void openScheduleRxDialog(final Context context, final View parent, final boolean hasPrescription){
	    final Dialog dialog = new Dialog(context);
	    dialog.setTitle("Select Rx");
	    dialog.setContentView(R.layout.schedule_dialog);
	    final Spinner spinner = (Spinner) dialog.findViewById(R.id.Rx_Spinner);
	    ListView rxListView = (ListView) dialog.findViewById(R.id.Rx_list);
	    Button ok = (Button) dialog.findViewById(R.id.ok);
	    
	    if (hasPrescription)
	    {
	    	for (int i = 0; i < ((LinearLayout)parent).getChildCount(); i++){
    			TextView txt = (TextView) ((LinearLayout)parent).getChildAt(i);
    			rxListView.addvi
    			//Toast.makeText(getActivity(), txt2.getText(),Toast.LENGTH_SHORT).show();
    		}
	    }
	    
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
			    	  if (hasPrescription){
			    		  TextView txt = new TextView(getActivity());
			    		  txt.setBackgroundColor(((RxItem)spinner.getSelectedItem()).getPatient().getColor());
			    		  txt.setText(((RxItem)spinner.getSelectedItem()).getName());
			    		  txt.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
			    		  txt.setSingleLine();
			    		  txt.setEllipsize(TextUtils.TruncateAt.END);
			    		  ((LinearLayout)parent).addView(txt);
			    	  }
			    	  else{
			    		  ((LinearLayout)parent).removeAllViews();
			    		  TextView txt = new TextView(getActivity());
			    		  txt.setBackgroundColor(((RxItem)spinner.getSelectedItem()).getPatient().getColor());
			    		  txt.setText(((RxItem)spinner.getSelectedItem()).getName());
			    		  txt.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
			    		  txt.setSingleLine();
			    		  txt.setEllipsize(TextUtils.TruncateAt.END);
			    		  ((LinearLayout)parent).addView(txt);
			    	  }
			  	      dialog.dismiss();
			      }
			    });
			    dialog.show();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	    
	  }
}
