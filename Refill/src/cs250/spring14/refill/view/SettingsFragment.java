package cs250.spring14.refill.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import cs250.spring14.refill.LoginActivity;
import cs250.spring14.refill.MainActivity;
import cs250.spring14.refill.R;
import cs250.spring14.refill.db.HistoryDBAdapter;
import cs250.spring14.refill.db.RxDBAdapter;
import cs250.spring14.refill.notify.RxNotificationManager;

/**
 * The DialogFragment that we will show upon clicking on the Settings icon in the Overflow menu
 */
public class SettingsFragment extends DialogFragment {
  /**
   * The method to create the view for the SettingsFragment based on XML & SharedPreferences
   */
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    getDialog().setTitle("Settings");

    View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
    final EditText histCount = (EditText) rootView.findViewById(R.id.historyCount);
    histCount.setText("" + HistoryDBAdapter.histCount);
    final CheckBox loginBox = (CheckBox) rootView.findViewById(R.id.shouldLogin);
    final EditText numDays = (EditText) rootView.findViewById(R.id.numdays);
    final CheckBox nameSortBox = (CheckBox) rootView.findViewById(R.id.sortbyname);
    final CheckBox ptSortBox = (CheckBox) rootView.findViewById(R.id.sortbypatient);
    final SharedPreferences prefs = MainActivity.getInstance().getSharedPreferences("refill", Context.MODE_PRIVATE);
    int oldNum = prefs.getInt(RxNotificationManager.countKey, 5);
    numDays.setText("" + oldNum);

    final boolean loginChecked = prefs.getBoolean(LoginActivity.nextKey, true);
    final boolean nameSortChecked = prefs.getBoolean(RxDBAdapter.namesortKey, false);
    final boolean ptSortChecked = prefs.getBoolean(RxDBAdapter.patientsortKey, false);
    loginBox.setChecked(loginChecked);
    nameSortBox.setChecked(nameSortChecked);
    ptSortBox.setChecked(ptSortChecked);
    Button ok = (Button) rootView.findViewById(R.id.ok);
    ok.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        // See if we should update shared preference for nextKey
        if (loginBox.isChecked() != loginChecked) {
          prefs.edit().putBoolean(LoginActivity.nextKey, loginBox.isChecked()).commit();
        }
        // See if we should update shared preferences for name sort
        if (nameSortBox.isChecked() != nameSortChecked) {
          prefs.edit().putBoolean(RxDBAdapter.namesortKey, nameSortBox.isChecked()).commit();
        }
        // See if we should update shared preferences for patient
        if (ptSortBox.isChecked() != ptSortChecked) {
          prefs.edit().putBoolean(RxDBAdapter.patientsortKey, ptSortBox.isChecked()).commit();
        }
        // See if we should give our little warning toast
        if (ptSortBox.isChecked() && nameSortBox.isChecked() && !(ptSortChecked && nameSortChecked)) {
          Toast
              .makeText(
                  getActivity(),
                  "Since you've chosen to sort by both, Prescriptions will be organized by PATIENT and then sorted by NAME",
                  Toast.LENGTH_LONG).show();
        }
        // See if we should update shraed preferences for numDays
        int newNum =
            (numDays.getText().toString().trim().length() == 0) ? 0 : Integer.valueOf(numDays.getText().toString()
                .trim());
        if (newNum < 1) {
          Toast.makeText(getActivity(), "Must be at least 1 day in advance!", Toast.LENGTH_SHORT).show();
          return;
        } else if (newNum != RxNotificationManager.numDays) {
          prefs.edit().putInt(RxNotificationManager.countKey, newNum).commit();
        }
        // See if we should update shared preferences for hist count
        int newHist =
            (histCount.getText().toString().trim().length() == 0) ? 0 : Integer.valueOf(histCount.getText().toString()
                .trim());
        if (newHist < 10) {
          Toast.makeText(getActivity(), "Please show at least 10 history items", Toast.LENGTH_SHORT).show();
          return;
        } else if (newHist != HistoryDBAdapter.histCount) {
          prefs.edit().putInt(HistoryDBAdapter.histKey, newHist).commit();
        }
        getDialog().dismiss();
      }

    });
    return rootView;
  }
}
