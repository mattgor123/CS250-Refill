package cs250.spring14.refill;

import java.text.ParseException;
import java.util.Calendar;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import cs250.spring14.refill.core.Doctor;
import cs250.spring14.refill.core.HistoryItem;
import cs250.spring14.refill.core.Pharmacy;
import cs250.spring14.refill.core.RxItem;
import cs250.spring14.refill.view.RefreshableFragment;
import cs250.spring14.refill.view.RxWrapper;

public class RxFragment extends Fragment implements RefreshableFragment {
  ListView rxList;
  ArrayAdapter<RxItem> rxAdap;
  private RefreshableFragment.OnCompleteListener mListener;

  @Override
  public RefreshableFragment.OnCompleteListener getmListener() {
    return mListener;
  }

  @Override
  public void setmListener(RefreshableFragment.OnCompleteListener mListener) {
    this.mListener = mListener;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    setRetainInstance(true);
    try {
      this.setmListener((RefreshableFragment.OnCompleteListener) activity);
    } catch (final ClassCastException e) {
      throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    setRetainInstance(true);
    View rootView = inflater.inflate(R.layout.fragment_rx, container, false);
    rxList = (ListView) rootView.findViewById(R.id.listView1);
    try {
      rxAdap = new RxWrapper(rootView.getContext(), 0, MainActivity.rxAdapter.getAllRxs());
    } catch (ParseException e) {
      e.printStackTrace();
    }
    rxList.setAdapter(rxAdap);
    rxList.setOnItemClickListener(new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // This is where we will add functionality to edit the Rx
        final RxItem rx = (RxItem) parent.getItemAtPosition(position);
        MainActivity.alertMessage(getActivity(), "Please select an action",
            "Would you like to Remove or View/Edit details for " + rx.getName() + "?", "Remove",
            // Remove
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                // Remove functionality must be added here
                MainActivity.rxAdapter.removeRx(rx.getId());
                String msg =
                    "Removed from Prescriptions DB on " + MainActivity.df.format(Calendar.getInstance().getTime());
                MainActivity.hAdapter.insertHis(new HistoryItem(rx.getName(), msg, "R"));
                // phAdap.notifyDataSetChanged();
                repopulateAdapter();
              }
            }, "View/Edit",
            // Edit
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                // Edit functionality must be added here

                MainActivity.getInstance().openAddOrEditRxDialog(MainActivity.getInstance(), rx);
                // Toast.makeText(getActivity(),
                // "You selected to edit Rx " + rx.getName(),
                // Toast.LENGTH_SHORT)
                // .show();
              }
            });
      }

    });
    rxList.setLongClickable(true);
    rxList.setOnItemLongClickListener(new OnItemLongClickListener() {

      @Override
      public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final RxItem rx = (RxItem) parent.getItemAtPosition(position);
        // First, ask if we want to contact Pharmacy or Doctor
        MainActivity.alertMessage(getActivity(), "Who should we contact?", "Would you like to contact Doctor: "
            + rx.getDoc().getName() + " or Pharmacy: " + rx.getPharmacy().getName() + "?", "Doctor",
        // Doctor
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                final Doctor doc = rx.getDoc();
                // Open up the Doctor alertMessage
                MainActivity.alertMessage(getActivity(), "Please select an action", "Would you like to Call or E-mail "
                    + doc.getName() + "?", "Call",
                // Call
                    new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                        if (doc != null) {
                          // Populate the Dialer with
                          // Phone #
                          Intent intent = new Intent(Intent.ACTION_DIAL);
                          intent.setData(Uri.parse("tel:" + doc.getPhone()));
                          startActivity(intent);
                        }
                      }
                    }, "E-mail",
                    // Email
                    new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                        if (doc != null) {
                          // Populate the E-mail
                          // intent with E-mail
                          // address
                          Intent intent = new Intent(Intent.ACTION_SENDTO);
                          String uriText;
                          if (rx.getRxNumb().equals(MainActivity.DEFAULT_RX_NUMBER)) {
                            uriText =
                                "mailto:" + Uri.encode(doc.getEmail()) + "?subject="
                                    + Uri.encode("Question about my " + rx.getName() + " Prescription") + "&body="
                                    + Uri.encode("Dr. " + doc.getName() + ",\n\n");
                          } else {
                            uriText =
                                "mailto:" + Uri.encode(doc.getEmail()) + "?subject="
                                    + Uri.encode("Question about Rx " + rx.getName() + "-#" + rx.getRxNumb())
                                    + "&body=" + Uri.encode("Dr. " + doc.getName() + ",\n\n");
                          }
                          intent.setData(Uri.parse(uriText));
                          startActivity(intent);
                        }
                      }
                    });

              }
            }, "Pharmacy",
            // Open up the Pharmacy alertMessage
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                final Pharmacy ph = rx.getPharmacy();
                MainActivity.alertMessage(getActivity(), "Please select an action", "Would you like to Call or E-mail "
                    + ph.getName() + "?", "Call",
                // Call
                    new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                        // Remove functionality must be
                        // added here
                        if (ph != null) {
                          // Populate the Dialer with
                          // Phone #
                          Intent intent = new Intent(Intent.ACTION_DIAL);
                          intent.setData(Uri.parse("tel:" + ph.getPhone()));
                          startActivity(intent);
                        }
                      }
                    }, "E-mail",
                    // Edit
                    new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                        if (ph != null) {
                          // Populate the E-mail
                          // intent with E-mail
                          // address
                          Toast.makeText(getActivity(), "Emailing " + ph.getName(), Toast.LENGTH_SHORT).show();
                          Intent intent = new Intent(Intent.ACTION_SENDTO);
                          String uriText =
                              "mailto:" + Uri.encode(ph.getEmail()) + "?subject="
                                  + Uri.encode("Question about Rx " + rx.getName() + "-#" + rx.getRxNumb()) + "&body="
                                  + Uri.encode("Dear " + ph.getName() + ",\n\n");
                          intent.setData(Uri.parse(uriText));
                          startActivity(intent);
                        }
                      }
                    });
              }
            });

        return true;
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

  @Override
  public void repopulateAdapter() {
    if (rxAdap != null) {
      rxAdap.clear();
      try {
        rxAdap.addAll(MainActivity.rxAdapter.getAllRxs());
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }
  }
}
