package cs250.spring14.refill.view;

import java.util.Calendar;
import java.util.Locale;

import cs250.spring14.refill.MainActivity;
import cs250.spring14.refill.R;
import cs250.spring14.refill.core.Doctor;
import cs250.spring14.refill.core.HistoryItem;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

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
	            Toast.makeText(getActivity(), "" + position, Toast.LENGTH_SHORT).show();
	        }
	    });

		
		return rootView;
	}
}
