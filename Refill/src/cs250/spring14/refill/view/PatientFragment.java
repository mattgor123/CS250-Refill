package cs250.spring14.refill.view;

import java.util.Calendar;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import cs250.spring14.refill.MainActivity;
import cs250.spring14.refill.R;
import cs250.spring14.refill.core.HistoryItem;
import cs250.spring14.refill.core.Patient;

/**
 * DialogFragment that opens onClick of the Patient in the Overflow menu
 * 
 */
public class PatientFragment extends DialogFragment implements RefreshableFragment {
  ListView patList;
  ArrayAdapter<Patient> patAdap;
  private RefreshableFragment.OnCompleteListener mListener;

  /**
   * Method to get the OnCompleteListener specified by the RefreshableFragment Used to determine if
   * anything needs to be performed (ie: refresh the views)
   * 
   * @see RefreshableFragment.OnCompleteListener
   */
  @Override
  public RefreshableFragment.OnCompleteListener getmListener() {
    return mListener;
  }

  /**
   * Method to set the OnCompleteListener
   */
  @Override
  public void setmListener(RefreshableFragment.OnCompleteListener mListener) {
    this.mListener = mListener;
  }

  /**
   * Method to attach the fragment to the activity Used to attach the onCompleteListener to the
   * Fragment
   */
  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      this.setmListener((RefreshableFragment.OnCompleteListener) activity);
    } catch (final ClassCastException e) {
      throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
    }
  }

  /**
   * Method to create the view used by this DialogFragment
   * 
   * @param inflater The LayoutInflater to use
   * @param container The ViewGroup to which this DialogFragment belongs
   * @param savedInstanceState the Bundle used to retain instances (not really used)
   */
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    getDialog().setTitle("Select a Patient!");
    View rootView = inflater.inflate(R.layout.fragment_pat, container, false);
    patList = (ListView) rootView.findViewById(R.id.listView1);
    patAdap = new PatientWrapper(rootView.getContext(), 0, MainActivity.paAdapter.getAllPats());
    patList.setAdapter(patAdap);
    patList.setOnItemClickListener(new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // This is where we will add functionality to edit the Rx
        final Patient pat = (Patient) parent.getItemAtPosition(position);
        MainActivity.alertMessage(getActivity(), "Please select an action",
            "Would you like to Remove or View/Edit details for " + pat.getName() + "?", "Remove",
            // Remove
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                // Remove functionality must be added here
                if (pat != null && MainActivity.rxAdapter.existsRxWithPat(Patient.makeStringFromPatient(pat))) {
                  Toast.makeText(getActivity(),
                      "Can't delete this Patient because an Rx with this Patient already exists!", Toast.LENGTH_SHORT)
                      .show();
                } else if (MainActivity.paAdapter.removePatient(pat.getId()) > 0) {
                  // We were able to remove the patient
                  String message =
                      "Removed from Patient DB on " + MainActivity.df.format(Calendar.getInstance().getTime());
                  HistoryItem his = new HistoryItem(pat.getName(), message, "PAD");
                  MainActivity.hAdapter.insertHis(his);
                  Toast.makeText(getActivity(), "Deleted Patient " + pat.getName() + " from the Patient DB",
                      Toast.LENGTH_SHORT).show();
                  repopulateAdapter();
                  PatientFragment.this.getmListener().onComplete(true);
                } else {
                  // Doctor has already been deleted
                  Toast
                      .makeText(
                          getActivity(),
                          "Sorry, we couldn't delete this Patient. Are you sure you haven't edited or already removed this Doctor?",
                          Toast.LENGTH_SHORT).show();
                }
              }
            }, "View/Edit",
            // Edit
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                if (pat != null) {
                  Patient.openEditPatientDialog(getActivity(), pat, PatientFragment.this);
                }
              }
            });
      }

    });
    return rootView;
  }

  /**
   * Method to repopulate the adapter every time the fragment's onResume is called
   */
  @Override
  public void onResume() {
    super.onResume();
    repopulateAdapter();
  }

  /**
   * Method to clear and then re-add all of the items that should be displayed Note: This method is
   * not very efficient, but since we are not working with more than a few hundred objects at a
   * time, it should not cause performance issues.
   */
  @Override
  public void repopulateAdapter() {
    if (patAdap != null) {
      patAdap.clear();
      patAdap.addAll(MainActivity.paAdapter.getAllPats());
    }
  }
}
