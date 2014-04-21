package cs250.spring14.refill;

import java.util.List;

import cs250.spring14.refill.HistoryItem.HistoryType;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HistoryWrapper extends ArrayAdapter<HistoryItem> {
	private List<HistoryItem> items;
	public HistoryWrapper(Context context, int resource, List<HistoryItem> objects) {
		super(context, resource, objects);
		this.items = objects;
	}

	//We don't want to be able to click on a HistoryItem
	@Override
	public boolean isEnabled(int position) {
		return super.isEnabled(position) && 
				(items.get(position).getH() == HistoryType.D ||
				items.get(position).getH() == HistoryType.P);
	}
	
	@Override
	public View getView(int pos, View convertView, ViewGroup parent){
		// assign the view we are converting to a local variable
				View v = convertView;
				// first check to see if the view is null. if so, we have to inflate it.
				// to inflate it basically means to render, or show, the view.
				if (v == null) {
					LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					v = inflater.inflate(R.layout.hislist_schema, null);
				}

				/*
				 * Recall that the variable position is sent in as an argument to this method.
				 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
				 * iterates through the list we sent it)
				 * 
				 * Therefore, i refers to the current Item object.
				 */
				HistoryItem i = items.get(pos);

				if (i != null) {

					// This is how you obtain a reference to the TextViews.
					// These TextViews are created in the XML files we defined.

					TextView own = (TextView) v.findViewById(R.id.owner);
					TextView msg = (TextView) v.findViewById(R.id.msg);
					ImageView iv = (ImageView) v.findViewById(R.id.his_ico);
					String nstr = i.getOwner();
					if (own != null){
						String str = nstr;
						if (str.length() > 16) str = (String) str.subSequence(0, 18) + "...";
						own.setText(str);
					}
					if (msg != null){
						String str = i.getMessage();
						if (str.length() > 39) str = (String) str.subSequence(0, 36) + "...";
						msg.setText(str);
					}
					if (iv != null) {
						//Here we handle finding the image from the name
						iv.setImageResource(i.getIconResource());
					}
				}
				// the view must be returned to our activity
				return v;
	}
}