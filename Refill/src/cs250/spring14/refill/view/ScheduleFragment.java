package cs250.spring14.refill.view;

import java.text.ParseException;
import java.util.ArrayList;
import cs250.spring14.refill.MainActivity;
import cs250.spring14.refill.R;
import cs250.spring14.refill.core.RxItem;
import cs250.spring14.refill.core.ScheduleItem;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ScheduleFragment extends DialogFragment implements RefreshableFragment{
	GridView grid;
	ScheduleWrapper scheduleAdap;
	
	private RefreshableFragment.OnCompleteListener mListener;

	  public RefreshableFragment.OnCompleteListener getmListener() {
	    return mListener;
	  }
	
	  public void setmListener(RefreshableFragment.OnCompleteListener mListener) {
	    this.mListener = mListener;
	  }
	
	  public void onAttach(Activity activity) {
	    super.onAttach(activity);
	    try {
	      this.setmListener((RefreshableFragment.OnCompleteListener) activity);
	    } catch (final ClassCastException e) {
	      throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
	    }
	  }
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().setTitle("Schedule");
		
		View rootView = inflater.inflate(R.layout.fragment_schedule, container,
				false);
		
		grid = (GridView) rootView.findViewById(R.id.gridView1);
		
		scheduleAdap = new ScheduleWrapper(rootView.getContext(), 0, MainActivity.scAdapter.getAllSchs());
		repopulateAdapter();
		grid.setAdapter(scheduleAdap);

		grid.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	// we shouldn't do anything with the first row and first column 
	        	if (position > 8 && position%8 != 0){
	        		TextView txt = (TextView) ((LinearLayout)v).getChildAt(0);
	        		if (txt.getText() == "|         |")
	        		{
	        			openScheduleRxDialog(getActivity(), v, false, position); 
	        		}else{
	        			openScheduleRxDialog(getActivity(), v, true, position);
	        		}     		
	        	}
	        }
	    });
		return rootView;
	}
	

	protected void openScheduleRxDialog(final Context context, final View parent, final boolean hasPrescription, final int position){
	    final Dialog dialog = new Dialog(context);
	    dialog.setTitle("Select Rx");
	    dialog.setContentView(R.layout.schedule_dialog);
	    final LinearLayout ll = (LinearLayout) dialog.findViewById(R.id.rxs_layout);
	    final Spinner spinner = (Spinner) dialog.findViewById(R.id.Rx_Spinner);
	    Button ok = (Button) dialog.findViewById(R.id.ok);
	    final ArrayList<ScheduleItem> schList = MainActivity.scAdapter.getScshByPos(position);
	    
	    if (hasPrescription)
	    {
	    	for(ScheduleItem s : schList)
	    	{
	    		TextView txt = new TextView (context);
	    		txt.setText(s.getName());
	    		txt.setBackgroundColor(s.getColor());
	    		ll.addView(txt);
	    	}
	    }
	    
	    ArrayList<RxItem> rxList;
		try {
			rxList = MainActivity.rxAdapter.getAllRxs();
			final ArrayAdapter<RxItem> aa =
			        new ArrayAdapter<RxItem>(getActivity(), android.R.layout.simple_spinner_item, rxList);
			    spinner.setAdapter(aa);
			    ok.setOnClickListener(new OnClickListener() {
			      @Override
			      public void onClick(View v) {
			    	  if (hasPrescription){
			    		  RxItem r = (RxItem)spinner.getSelectedItem();
			    		  for(ScheduleItem s : schList){
			    			  if (s.getName().equals(r.getName()) && s.getColor() == r.getPatient().getColor())
			    			  {
			    				  dialog.dismiss();
				    			  return; 
			    			  }
			    		  }
			    		  MainActivity.scAdapter.insertSch(new ScheduleItem(((RxItem)spinner.getSelectedItem()).getName(),position, 
			    				  ((RxItem)spinner.getSelectedItem()).getPatient().getColor()));
			    		  repopulateAdapter();
			    	  }
			    	  else{
			    		  // removing the pipes TextView
			    		  ((LinearLayout)parent).removeAllViews();
			    		  MainActivity.scAdapter.insertSch(new ScheduleItem(((RxItem)spinner.getSelectedItem()).getName(),position, 
			    				  ((RxItem)spinner.getSelectedItem()).getPatient().getColor()));
			    		  repopulateAdapter();
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
	
	@Override
	public void repopulateAdapter() {
		// TODO Auto-generated method stub
		if (scheduleAdap != null)
		{
			scheduleAdap.clear();
			scheduleAdap.addAll(MainActivity.scAdapter.getAllSchs());
		}
		
	}
}
