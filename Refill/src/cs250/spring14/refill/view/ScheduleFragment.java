package cs250.spring14.refill.view;

import java.text.ParseException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import cs250.spring14.refill.MainActivity;
import cs250.spring14.refill.R;
import cs250.spring14.refill.core.RxItem;
import cs250.spring14.refill.core.ScheduleItem;

public class ScheduleFragment extends DialogFragment implements RefreshableFragment {
  GridView grid;
  ScheduleWrapper scheduleAdap;

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
    try {
      this.setmListener((RefreshableFragment.OnCompleteListener) activity);
    } catch (final ClassCastException e) {
      throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    getDialog().setTitle("Schedule");

    View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);

    grid = (GridView) rootView.findViewById(R.id.gridView1);

    scheduleAdap = new ScheduleWrapper(rootView.getContext(), 0, MainActivity.scAdapter.getAllSchs());
    repopulateAdapter();
    grid.setAdapter(scheduleAdap);

    grid.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        // we shouldn't do anything with the first row and first column
        if (position > 8 && position % 8 != 0) {
          TextView txt = (TextView) ((LinearLayout) v).getChildAt(0);
          if (txt.getText() == "|         |") {
            openScheduleRxDialog(getActivity(), v, false, position);
          } else {
            openScheduleRxDialog(getActivity(), v, true, position);
          }
        }
      }
    });
    return rootView;
  }


  protected void openScheduleRxDialog(final Context context, final View parent, final boolean hasPrescription,
      final int position) {
    final Dialog dialog = new Dialog(context);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setContentView(R.layout.schedule_dialog);
    final LinearLayout ll = (LinearLayout) dialog.findViewById(R.id.rxs_layout);
    final Spinner spinner = (Spinner) dialog.findViewById(R.id.Rx_Spinner);
    Button add = (Button) dialog.findViewById(R.id.add);
    Button close = (Button) dialog.findViewById(R.id.close);
    Button remove = (Button) dialog.findViewById(R.id.remove);
    final ArrayList<ScheduleItem> schList = MainActivity.scAdapter.getScshByPos(position);

    if (hasPrescription) {
      for (ScheduleItem s : schList) {
        final TextView txt = new TextView(context);
        txt.setText(s.getName());
        txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        txt.setBackgroundColor(s.getColor());
        /**
         * txt.setOnClickListener(new OnClickListener() {
         * 
         * @Override public void onClick(View v) { final TextView clicked = (TextView) v; final
         *           Dialog confirm = new Dialog(context); confirm.setTitle("Confirm remove");
         *           LinearLayout ll = new LinearLayout(context);
         *           ll.setOrientation(LinearLayout.VERTICAL); TextView txt = new TextView(context);
         *           txt.setText("Are you sure you would like to remove " +
         *           clicked.getText().toString() + "?"); ll.addView(txt); LinearLayout horiz = new
         *           LinearLayout(context); horiz.setOrientation(LinearLayout.HORIZONTAL); Button ok
         *           = new Button(context); ok.setOnClickListener(new OnClickListener() {
         * @Override public void onClick(View v) { for (ScheduleItem s : schList) { if
         *           (s.getName().equals(((TextView)v).getText().toString())) { if
         *           (MainActivity.scAdapter.removeSch(s.getId()) > 0) { Toast.makeText(context,
         *           "Successfully removed " + s.getName() + " from this slot.", Toast.LENGTH_SHORT)
         *           .show(); repopulateAdapter(); confirm.dismiss(); return; } } } } }); Button no
         *           = new Button(context); no.setOnClickListener(new OnClickListener () {
         * @Override public void onClick(View v) { confirm.dismiss(); return; } });
         *           horiz.addView(ok); horiz.addView(no); ll.addView(horiz);
         *           confirm.setContentView(ll); confirm.show(); } });
         */
        ll.addView(txt);
      }
    }

    ArrayList<RxItem> rxList = null;
    try {
      rxList = MainActivity.rxAdapter.getAllRxs();
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      // e.printStackTrace();
    }
    if (rxList == null) {
      dialog.dismiss();
      return;
    }
    final ArrayAdapter<RxItem> aa =
        new ArrayAdapter<RxItem>(getActivity(), android.R.layout.simple_spinner_item, rxList);
    spinner.setAdapter(aa);
    remove.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        final RxItem r = (RxItem) spinner.getSelectedItem();
        if (!hasPrescription) {
          dialog.dismiss();
          return;
        } else {
          for (ScheduleItem s : schList) {
            if (s.getName().equals(r.getName()) && s.getColor() == r.getPatient().getColor()) {
              if (MainActivity.scAdapter.removeSch(s.getId()) > 0) {
                Toast.makeText(context, "Successfully removed " + r.getName() + " from this slot.", Toast.LENGTH_SHORT)
                    .show();
                dialog.dismiss();
                repopulateAdapter();
                return;
              }
            }
          }
          Toast.makeText(context, r.getName() + " was not removed; are you sure it's in your schedule?",
              Toast.LENGTH_SHORT).show();
          dialog.dismiss();
        }
      }
    });
    close.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        dialog.dismiss();
        return;
      }

    });
    add.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        final RxItem r = (RxItem) spinner.getSelectedItem();
        if (hasPrescription) {
          for (ScheduleItem s : schList) {
            if (s.getName().equals(r.getName()) && s.getColor() == r.getPatient().getColor()) {
              Toast.makeText(context, r.getName() + " has already been scheduled for this time slot!",
                  Toast.LENGTH_SHORT).show();
              dialog.dismiss();
              return;
            }
          }
          ScheduleItem sch = new ScheduleItem(r.getName(), position, r.getPatient().getColor());
          sch.setId(MainActivity.scAdapter.insertSch(sch));
          repopulateAdapter();
        } else {
          // removing the pipes TextView
          ((LinearLayout) parent).removeAllViews();
          ScheduleItem sch = new ScheduleItem(r.getName(), position, r.getPatient().getColor());
          sch.setId(MainActivity.scAdapter.insertSch(sch));
          repopulateAdapter();
        }
        dialog.dismiss();
      }
    });
    dialog.show();
  }

  @Override
  public void repopulateAdapter() {
    // TODO Auto-generated method stub
    if (scheduleAdap != null) {
      scheduleAdap.clear();
      scheduleAdap.addAll(MainActivity.scAdapter.getAllSchs());
    }
  }
}
