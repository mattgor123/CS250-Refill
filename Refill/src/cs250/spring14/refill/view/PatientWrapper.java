package cs250.spring14.refill.view;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import cs250.spring14.refill.R;
import cs250.spring14.refill.core.Patient;

/**
 * Extension of ArrayAdapter<Patient> used by the PatientFragment to get view for each patient
 */
public class PatientWrapper extends ArrayAdapter<Patient> {
  private List<Patient> items;

  /**
   * Constructor given a context, resource, and a List<Patient> (passed by PatientFragment)
   * 
   * @param context The context given by the PatientFragment
   * @param resource The resource (never actually used)
   * @param objects The List<Patient> objects passed by the PatientFragment, gotten from the
   *        MainActivity's PatientDBAdapter
   */
  public PatientWrapper(Context context, int resource, List<Patient> objects) {
    super(context, resource, objects);
    this.items = objects;
  }

  /**
   * Method to get the specific view for the Patient at the given position. Used to set the View's
   * background to the Patient's color and TextView to Patient's name.
   */
  @Override
  public View getView(int pos, View convertView, ViewGroup parent) {
    // assign the view we are converting to a local variable
    View v = convertView;
    // first check to see if the view is null. if so, we have to inflate it.
    // to inflate it basically means to render, or show, the view.
    if (v == null) {
      LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      v = inflater.inflate(R.layout.patient_schema, null);
    }

    /*
     * Recall that the variable position is sent in as an argument to this method. The variable
     * simply refers to the position of the current object in the list. (The ArrayAdapter iterates
     * through the list we sent it)
     * 
     * Therefore, i refers to the current Item object.
     */
    Patient i = items.get(pos);
    v.setVisibility(View.VISIBLE);
    TextView name = (TextView) v.findViewById(R.id.name);
    if (i.getId() == 1) {
      // This is our dummy Pharmacy
      name.setVisibility(View.GONE);
      v.setVisibility(View.GONE);
      return v;
    } else {
      name.setVisibility(View.VISIBLE);
      v.setVisibility(View.VISIBLE);
      String str = i.getName();
      if (name != null) {
        name.setText(str);
      }
      v.setBackgroundColor(i.getColor());
    }
    // the view must be returned to our activity
    return v;
  }
}
