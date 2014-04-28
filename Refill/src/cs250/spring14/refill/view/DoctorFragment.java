package cs250.spring14.refill.view;

import java.util.Calendar;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import cs250.spring14.refill.MainActivity;
import cs250.spring14.refill.R;
import cs250.spring14.refill.core.Doctor;
import cs250.spring14.refill.core.HistoryItem;

public class DoctorFragment extends DialogFragment implements RefreshableFragment {
  ListView docList;
  ArrayAdapter<Doctor> docAdap;
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

  /**
   * Method to create the Doctor's dialog, inflate the dialog and attach to the parent view
   * 
   * @param inflater
   * @param container
   * @param savedInstanceState
   * @return
   */
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    getDialog().setTitle("Doctors");

    View rootView = inflater.inflate(R.layout.fragment_doc, container, false);
    docList = (ListView) rootView.findViewById(R.id.listView1);
    docAdap = new DoctorWrapper(rootView.getContext(), 0, MainActivity.drAdapter.getAllDrs());
    docList.setAdapter(docAdap);
    docList.setLongClickable(true);
    docList.setOnItemLongClickListener(new OnItemLongClickListener() {

      @Override
      public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final Doctor doc = (Doctor) parent.getItemAtPosition(position);
        // We implement our long-press action soon.
        MainActivity.alertMessage(getActivity(), "Please select an action", "Would you like to Call or E-mail "
            + doc.getName() + "?", "Call",
        // Call
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                // Remove functionality must be added here
                if (doc != null) {
                  // Populate the Dialer with Phone #
                  getDialog().dismiss();
                  Intent intent = new Intent(Intent.ACTION_DIAL);
                  intent.setData(Uri.parse("tel:" + doc.getPhone()));
                  startActivity(intent);
                }
              }
            }, "E-mail",
            // Edit
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                if (doc != null) {
                  // Populate the E-mail intent with E-mail
                  // address
                  getDialog().dismiss();
                  Intent intent = new Intent(Intent.ACTION_SENDTO);
                  String uriText =
                      "mailto:" + Uri.encode(doc.getEmail()) + "?subject="
                          + Uri.encode("Question about a prescription") + "&body="
                          + Uri.encode("Dr. " + doc.getName() + ",\n\n");
                  intent.setData(Uri.parse(uriText));
                  startActivity(intent);
                }
              }
            });
        return true;
      }

    });
    docList.setOnItemClickListener(new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // This is where we will add functionality to edit the doctor
        final Doctor doc = (Doctor) parent.getItemAtPosition(position);
        MainActivity.alertMessage(getActivity(), "Please select an action",
            "Would you like to Remove or View/Edit details for " + doc.getName() + "?", "Remove",
            // Remove
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                // Remove functionality must be added here
                if (doc != null && MainActivity.rxAdapter.existsRxWithDoc(Doctor.makeStringFromDoc(doc))) {
                  Toast.makeText(getActivity(),
                      "Can't delete this Doctor because an Rx with this Doctor already exists!", Toast.LENGTH_SHORT)
                      .show();
                } else if (MainActivity.drAdapter.removeDr(doc.getId()) > 0) {
                  // We were able to remove the doctor
                  String message =
                      "Removed from Doctor DB on " + MainActivity.df.format(Calendar.getInstance().getTime());
                  HistoryItem his = new HistoryItem(doc.getName(), message, "DD");
                  MainActivity.hAdapter.insertHis(his);
                  Toast.makeText(getActivity(), "Deleted Doctor " + doc.getName() + " from the Doctor DB",
                      Toast.LENGTH_SHORT).show();
                  DoctorFragment.this.getmListener().onComplete(true);
                  repopulateAdapter();
                } else {
                  // Doctor has already been deleted
                  Toast
                      .makeText(
                          getActivity(),
                          "Sorry, we couldn't delete this Doctor. Are you sure you haven't edited or already removed this Doctor?",
                          Toast.LENGTH_SHORT).show();
                }
              }
            }, "View/Edit",
            // Edit
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                if (doc != null) {
                  Doctor.openEditDoctorDialog(getActivity(), doc, DoctorFragment.this);
                }
              }
            });
      }

    });
    return rootView;
  }

  // We will manually call this to ensure the Prescriptions view is always
  // current
  @Override
  public void onResume() {
    super.onResume();
    repopulateAdapter();
  }

  /**
   * Method to repopulate the Doctor's array adapter with changes produced in the edit dialog
   */
  @Override
  public void repopulateAdapter() {
    if (docAdap != null) {
      docAdap.clear();
      docAdap.addAll(MainActivity.drAdapter.getAllDrs());
    }
  }
}
