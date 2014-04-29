package cs250.spring14.refill.view;

import java.util.Calendar;
import java.util.Date;

import cs250.spring14.refill.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ScheduleAdapter extends BaseAdapter{

	private Context mContext;
	private Calendar cal;
	private String[] days = new String[]{"", "Sun", " M ", " T ", " W ", " Th", " F ", "Sat"};
	private String[] hours = new String[]{"", " 6AM", " 7AM", " 8AM", " 9AM", "10AM", "11AM", "12PM",
			" 1PM", " 2PM", " 3PM", " 4PM", " 5PM", " 6PM", " 7PM", " 8PM", " 9PM", "10PM"};
	
    public ScheduleAdapter(Context c) {
        mContext = c;
        cal = Calendar.getInstance();
    }

	@Override
	public int getCount() {
		return days.length * hours.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = convertView;
		LinearLayout layout;
		// first check to see if the view is null. if so, we have to inflate it.
		// to inflate it basically means to render, or show, the view.
		if (v == null) { 
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.schedule_schema, parent, false);
			
		}else{
			// invalidating the current layout to avoid scroll issues
			v = null; 
			
			// inflating again to render the view
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.schedule_schema, parent, false);
		}
		// filling the text for the first row (days)
		if (position != 0 && position < 8){
			v.setVisibility(View.VISIBLE);
			layout = (LinearLayout) v.findViewById(R.id.schedule_layout);
			TextView dayText = new TextView(mContext);
			dayText.setText(days[position]);
			dayText.setLines(1);
			layout.addView(dayText);
			
		// filling the text for the first column (times)
		}else if ((position/8) < 18 && (position%8 == 0) ){
			v.setVisibility(View.VISIBLE);
			layout = (LinearLayout) v.findViewById(R.id.schedule_layout);
			TextView timeText = new TextView(mContext);
			timeText.setText(hours[position/8]);
			timeText.setLines(1);
			layout.addView(timeText);
			
		// cell to show the meds that should be taken that time
		}else
		{
			v.setVisibility(View.VISIBLE);
			layout = (LinearLayout) v.findViewById(R.id.schedule_layout);
			TextView txt = new TextView(mContext);
			txt.setText("|      |");
			txt.setLines(1);
			layout.addView(txt);
		}
		return v;
	}

}
