package cs250.spring14.refill.view;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cs250.spring14.refill.R;
import cs250.spring14.refill.core.Doctor;

/**
 * Extension of ArrayAdapter<Doctor> to get the view for each item in the DoctorFragment
 */
public class DoctorWrapper extends ArrayAdapter<Doctor> {
  private List<Doctor> items;

  /**
   * Constructor given a Context, resource and a Doctor's list
   * 
   * @param context the context, passed from the calling DoctorFragment
   * @param resource always 0; not used
   * @param objects the List<Doctor> that we will be setting views for
   */
  public DoctorWrapper(Context context, int resource, List<Doctor> objects) {
    super(context, resource, objects);
    this.items = objects;
  }

  /**
   * Method to populate the view with a given Doctor position (List<Doctor> index)
   * 
   * @param pos the Doctor that will be pushed to the DoctorFragment (List<Doctor> index)
   * @param convertView the old view that will be updated
   * @param parent the parent that this view will be attached to
   * @return a View corresponding to the Doctor at the specified position.
   */
  @Override
  public View getView(int pos, View convertView, ViewGroup parent) {
    // assign the view we are converting to a local variable
    View v = convertView;
    // first check to see if the view is null. if so, we have to inflate it.
    // to inflate it basically means to render, or show, the view.
    if (v == null) {
      LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      v = inflater.inflate(R.layout.doctor_schema, null);
    }

    /*
     * Recall that the variable position is sent in as an argument to this method. The variable
     * simply refers to the position of the current object in the list. (The ArrayAdapter iterates
     * through the list we sent it)
     * 
     * Therefore, i refers to the current Item object.
     */
    Doctor i = items.get(pos);

    // This is how you obtain a reference to the TextViews.
    // These TextViews are created in the XML files we defined.
    if (i != null) {
      v.setVisibility(View.VISIBLE);
      TextView name = (TextView) v.findViewById(R.id.name);
      TextView phone = (TextView) v.findViewById(R.id.phone);
      TextView email = (TextView) v.findViewById(R.id.email);
      ImageView iv = (ImageView) v.findViewById(R.id.dr_ico);
      if (i.getId() == 1) {
        // This is our dummy Pharmacy; we don't want to display it
        name.setVisibility(View.GONE);
        phone.setVisibility(View.GONE);
        email.setVisibility(View.GONE);
        iv.setVisibility(View.GONE);
        v.setVisibility(View.GONE);
        return v;
      } else {
        // This is a real Pharmacy; we want to display it
        name.setVisibility(View.VISIBLE);
        phone.setVisibility(View.VISIBLE);
        email.setVisibility(View.VISIBLE);
        iv.setVisibility(View.VISIBLE);
        v.setVisibility(View.VISIBLE);
        String nstr = i.getName();
        if (name != null) {
          String str = nstr;
          // Truncate if too long
          if (str.length() > 17)
            str = (String) str.subSequence(0, 15) + "...";
          name.setText(str);
        }
        if (phone != null) {
          String str = i.getPhone();
          phone.setText(str);
        }
        if (email != null) {
          String str = i.getEmail();
          email.setText(str);
        }
      }
    }
    // the view must be returned to our activity
    return v;
  }
}
