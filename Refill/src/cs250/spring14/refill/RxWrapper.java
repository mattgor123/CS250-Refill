package cs250.spring14.refill;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RxWrapper extends ArrayAdapter<RxItem> {
	private List<RxItem> items;
	
	public RxWrapper(Context context, int resource, List<RxItem> objects) {
		super(context, resource, objects);
		this.items = objects;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent){
		// assign the view we are converting to a local variable
				View v = convertView;

				// first check to see if the view is null. if so, we have to inflate it.
				// to inflate it basically means to render, or show, the view.
				if (v == null) {
					LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					v = inflater.inflate(R.layout.rxlist_schema, null);
				}

				/*
				 * Recall that the variable position is sent in as an argument to this method.
				 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
				 * iterates through the list we sent it)
				 * 
				 * Therefore, i refers to the current Item object.
				 */
				RxItem i = items.get(pos);

				if (i != null) {

					// This is how you obtain a reference to the TextViews.
					// These TextViews are created in the XML files we defined.

					TextView name = (TextView) v.findViewById(R.id.name);
					TextView details = (TextView) v.findViewById(R.id.details);
					TextView next = (TextView) v.findViewById(R.id.next);
					if (name != null){
						String str = i.getName();
						if (str.length() > 24) str = (String) str.subSequence(0, 24);
						name.setText(str);
					}
					if (details != null){
						String str = "Use: " + i.getDose() + "mg, " + i.getPillsPerDay() + "x daily";
						details.setText(str);
					}
					if (next != null) {
						String str = "Next refill: " + MainActivity.df.format(i.getNextRefillDate());
						next.setText(str);
					}
				}

				// the view must be returned to our activity
				return v;
	}
}
