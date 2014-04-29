package cs250.spring14.refill.view;

import java.util.ArrayList;
import java.util.List;

import cs250.spring14.refill.MainActivity;
import cs250.spring14.refill.R;
import cs250.spring14.refill.core.ScheduleItem;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ScheduleWrapper extends ArrayAdapter<ScheduleItem>{

	private Context mContext;
	private String[] days = new String[]{"", "| Sun |", "|   M  |", "|   T   |", "|   W  |", "|  Th  |", "|   F   |", "|  Sat |"};
	private String[] hours = new String[]{"", " 6AM ", " 7AM ", " 8AM ", " 9AM ", "10AM", "11AM", "12PM",
			" 1PM", " 2PM", " 3PM", " 4PM", " 5PM", " 6PM", " 7PM", " 8PM", " 9PM", "10PM"};
	List<ScheduleItem> list;
    
    public ScheduleWrapper (Context c, int resource, List<ScheduleItem> objects)
    {
    	super(c, resource, objects);
    	this.mContext = c;
    	this.list = objects;
    }

	@Override
	public int getCount() {
		return days.length * hours.length;
	}

	@Override
	public ScheduleItem getItem(int position) {
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
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.schedule_schema, parent, false);
			
		}else{
			// invalidating the current layout to avoid scroll issues
			v = null; 
			
			// inflating again to render the view
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.schedule_schema, parent, false);
		}
		
		ArrayList<ScheduleItem> schItems = MainActivity.scAdapter.getScshByPos(position);
		
		v.setVisibility(View.VISIBLE);
		layout = (LinearLayout) v.findViewById(R.id.schedule_layout);
		
		if (!schItems.isEmpty())
		{
			for(ScheduleItem s : schItems)
			{
				TextView txt = new TextView(getContext());
				txt.setBackgroundColor(s.getColor());
				if (schItems.size() > 1)
					txt.setText("*" + s.getName());
				else
					txt.setText(s.getName());
		  		txt.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		  		txt.setSingleLine();
		  		txt.setEllipsize(TextUtils.TruncateAt.END);
		  		layout.addView(txt);
			}
		}else{
			// filling the text for the first row (days)
			if (position != 0 && position < 8){
				TextView dayText = new TextView(mContext);
				dayText.setText(days[position]);
				dayText.setLines(1);
				layout.addView(dayText);
				
			// filling the text for the first column (times)
			}else if ((position/8) < 18 && (position%8 == 0) ){
				TextView timeText = new TextView(mContext);
				timeText.setText(hours[position/8]);
				timeText.setLines(1);
				layout.addView(timeText);
				
			// cell to show the meds that should be taken that time
			}else
			{
				TextView txt = new TextView(mContext);
				txt.setText("|         |");
				txt.setLines(1);
				layout.addView(txt);
			}
		}
		return v;
	}

}
