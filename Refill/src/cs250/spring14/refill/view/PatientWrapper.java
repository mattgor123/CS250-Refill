package cs250.spring14.refill.view;

import java.util.List;

import cs250.spring14.refill.R;
import cs250.spring14.refill.core.Patient;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PatientWrapper extends ArrayAdapter<Patient> {
	private List<Patient> items;

	public PatientWrapper(Context context, int resource, List<Patient> objects) {
		super(context, resource, objects);
		this.items = objects;
	}

	// We don't want to be able to click on a HistoryItem
	@Override
	public boolean isEnabled(int position) {
		return super.isEnabled(position);
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		// assign the view we are converting to a local variable
		View v = convertView;
		// first check to see if the view is null. if so, we have to inflate it.
		// to inflate it basically means to render, or show, the view.
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.patient_schema, null);
		}

		/*
		 * Recall that the variable position is sent in as an argument to this
		 * method. The variable simply refers to the position of the current
		 * object in the list. (The ArrayAdapter iterates through the list we
		 * sent it)
		 * 
		 * Therefore, i refers to the current Item object.
		 */
		Patient i = items.get(pos);

		TextView name = (TextView) v.findViewById(R.id.name);
			// This is how you obtain a reference to the TextViews.
			// These TextViews are created in the XML files we defined.
			String nstr = i.getName();
			if (name != null) {
				String str = nstr;
				name.setText(str);
			}
			v.setBackgroundColor(i.getColor());
		// the view must be returned to our activity
		return v;
	}
}