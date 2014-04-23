package cs250.spring14.refill.view;

import java.util.List;
import java.util.Locale;

import cs250.spring14.refill.MainActivity;
import cs250.spring14.refill.R;
import cs250.spring14.refill.core.RxItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RxWrapper extends ArrayAdapter<RxItem> {
	private List<RxItem> items;

	public RxWrapper(Context context, int resource, List<RxItem> objects) {
		super(context, resource, objects);
		this.items = objects;
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
			v = inflater.inflate(R.layout.rxlist_schema, null);
		}

		/*
		 * Recall that the variable position is sent in as an argument to this
		 * method. The variable simply refers to the position of the current
		 * object in the list. (The ArrayAdapter iterates through the list we
		 * sent it)
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
			ImageView iv = (ImageView) v.findViewById(R.id.pill_ico);
			String nstr = i.getName();
			if (name != null) {
				String str = nstr;
				if (str.length() > 16)
					str = (String) str.subSequence(0, 14) + "...";
				name.setText(str);
			}
			if (details != null) {
				String str = "Use: " + i.getDose() + "mg, "
						+ i.getPillsPerDay() + "x daily";
				if (str.length() > 26)
					str = (String) str.subSequence(0, 23) + "...";
				details.setText(str);
			}
			if (next != null) {
				String str = "Next refill: "
						+ MainActivity.df.format(i.getNextRefillDate());
				if (str.length() > 28)
					str = (String) str.subSequence(0, 25) + "...";
				next.setText(str);
			}
			if (iv != null) {
				// Here we handle finding the image from the name
				iv.setImageResource(getIconFromString(nstr
						.toLowerCase(Locale.US)));
			}
		}
		//Set the view's color based on the RxItem Patient's color
		v.setBackgroundColor(i.getPatient().getColor());
		// the view must be returned to our activity
		return v;
	}

	/**
	 * Helper method to get the resource for a pill given its name
	 * 
	 * @param name
	 * @return the int of the resource created in the R file
	 */
	public int getIconFromString(String name) {
		switch (name) {
		case "adderall":
			return R.drawable.adderall;
		case "albuterol":
			return R.drawable.albuterol;
		case "ambien":
			return R.drawable.ambien;
		case "aspirin":
			return R.drawable.aspirin;
		case "atenolol":
			return R.drawable.atenolol;
		case "cymbalta":
			return R.drawable.cymbalta;
		case "darvocet":
			return R.drawable.darvocet;
		case "diovan":
			return R.drawable.diovan;
		case "effexor":
			return R.drawable.effexor;
		case "hydrocodone":
			return R.drawable.hydrocodone;
		case "lexapro":
			return R.drawable.lexapro;
		case "lipitor":
			return R.drawable.lipitor;
		case "lisinopril":
			return R.drawable.lisinopril;
		case "lyrica":
			return R.drawable.lyrica;
		case "metformin":
			return R.drawable.metformin;
		case "morphine":
			return R.drawable.morphine;
		case "naproxen":
			return R.drawable.naproxen;
		case "neurontin":
			return R.drawable.neurontin;
		case "nexium":
			return R.drawable.nexium;
		case "norvasc":
			return R.drawable.norvasc;
		case "oxycontin":
			return R.drawable.oxycontin;
		case "paxil":
			return R.drawable.paxil;
		case "percocet":
			return R.drawable.percocet;
		case "phentermine":
			return R.drawable.phentermine;
		case "prednisone":
			return R.drawable.prednisone;
		case "protonix":
			return R.drawable.protonix;
		case "seroquel":
			return R.drawable.seroquel;
		case "soma":
			return R.drawable.soma;
		case "synthroid":
			return R.drawable.synthroid;
		case "toprol":
			return R.drawable.toprol;
		case "tramadol":
			return R.drawable.tramadol;
		case "tylenol":
			return R.drawable.tylenol;
		case "ultram":
			return R.drawable.ultram;
		case "valium":
			return R.drawable.valium;
		case "vicodin":
			return R.drawable.vicodin;
		case "wellbutrin":
			return R.drawable.wellbutrin;
		case "xanax":
			return R.drawable.xanax;
		case "zestril":
			return R.drawable.zestril;
		case "zocor":
			return R.drawable.zocor;
		case "zoloft":
			return R.drawable.zoloft;
		default:
			return R.drawable.default_pill;
		}
	}
}
