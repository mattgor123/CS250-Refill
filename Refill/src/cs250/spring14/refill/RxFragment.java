package cs250.spring14.refill;

import java.text.ParseException;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import android.widget.AdapterView.OnItemClickListener;

public class RxFragment extends Fragment {
	ListView rxList;
	ArrayAdapter<RxItem> rxAdap;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_rx, container, false);
        rxList = (ListView) rootView.findViewById(R.id.listView1);
        try {
			rxAdap = new RxWrapper(rootView.getContext(),0, MainActivity.rxAdapter.getAllRxs());
		} catch (ParseException e) {
			e.printStackTrace();
		}
        rxList.setAdapter(rxAdap);
        rxList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//This is where we will add functionality to edit the Rx
				final RxItem rx = (RxItem) parent.getItemAtPosition(position);
				alertMessage("Please select an action", "Would you like to Remove or View/Edit details for " + rx.getName() + "?", "Remove", 
						//Remove
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								//Remove functionality must be added here
								MainActivity.rxAdapter.removeRx(rx.getId());
								onResume();
							}
						}, 
						"View/Edit",
						//Edit
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								//Edit functionality must be added here
								
								MainActivity.getInstance().openAddDialog(MainActivity.getInstance(), rx);
								 //Toast.makeText(getActivity(),
											//"You selected to edit Rx " + rx.getName(), Toast.LENGTH_SHORT)
											//.show();
							}
						});
			}
        	
        });
        rxAdap.notifyDataSetChanged();
        return rootView;
    }
	
	/**
	 * Helper method to create AlertDialogs given a title, message, positive OnClickListner, and negative OnClickListener
	 * Deprecation warnings suppressed because the code still works on all versions of Android we tested
	 * @param title the alert dialog's desired title
	 * @param message the alert dialog's desired message
	 * @param b2Text the alert dialog's b2 text
	 * @param b2OCL the alert dialog's positive OnClickListener, b2
	 * @param b1Text the alert dialog's b1 text
	 * @param b1OCL the alert dialog's negative OnClickListener, b1
	 * Code adapted from Matt's ARK AlertDialog
	 */
	@SuppressWarnings("deprecation")
	public void alertMessage(String title, String message, String b2Text, DialogInterface.OnClickListener b2OCL, String b1Text, DialogInterface.OnClickListener b1OCL) {
		AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		alertDialog.setButton2(b2Text, b2OCL);
		alertDialog.setButton(b1Text, b1OCL);				
		alertDialog.show();
	}
	
	//We will manually call this to ensure the Prescriptions view is always current
	@Override
	public void onResume() {
		super.onResume();
		rxAdap.clear();
		try {
			rxAdap.addAll(MainActivity.rxAdapter.getAllRxs());
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	
}
