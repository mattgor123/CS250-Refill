package cs250.spring14.refill.view;

import java.util.Calendar;

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
import cs250.spring14.refill.core.HistoryItem;
import cs250.spring14.refill.core.Pharmacy;

public class PharmacyFragment extends DialogFragment implements
		RefreshableFragment {
	ListView phList;
	ArrayAdapter<Pharmacy> phAdap;

	/**
	 * Method to create the Pharmacy's dialog, inflate the dialog and attach to
	 * the parent view
	 * 
	 * @param inflater
	 * @param container
	 * @param savedInstanceState
	 * @return
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().setTitle("Pharmacies");

		View rootView = inflater.inflate(R.layout.fragment_pharm, container,
				false);
		phList = (ListView) rootView.findViewById(R.id.listView1);
		phAdap = new PharmacyWrapper(rootView.getContext(), 0,
				MainActivity.phAdapter.getAllPhs());
		phList.setAdapter(phAdap);
		phList.setLongClickable(true);
		phList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				final Pharmacy ph = (Pharmacy) parent
						.getItemAtPosition(position);
				// We implement our long-press action soon.
				MainActivity.alertMessage(getActivity(),
						"Please select an action",
						"Would you like to Call or E-mail " + ph.getName()
								+ "?", "Call",
						// Call
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// Remove functionality must be added here
								if (ph != null) {
									// Populate the Dialer with Phone #
									getDialog().dismiss();
									Intent intent = new Intent(
											Intent.ACTION_DIAL);
									intent.setData(Uri.parse("tel:"
											+ ph.getPhone()));
									startActivity(intent);
								}
							}
						}, "E-mail",
						// Edit
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (ph != null) {
									// Populate the E-mail intent with E-mail
									// address
									Toast.makeText(getActivity(),
											"Emailing " + ph.getName(),
											Toast.LENGTH_SHORT).show();
									Intent intent = new Intent(
											Intent.ACTION_SENDTO);
									String uriText = "mailto:"
											+ Uri.encode(ph.getEmail())
											+ "?subject="
											+ Uri.encode("Question about a prescription")
											+ "&body="
											+ Uri.encode("Dear " + ph.getName()
													+ ",\n\n");
									intent.setData(Uri.parse(uriText));
									startActivity(intent);
								}
							}
						});
				return true;
			}
		});
		phList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// This is where we will add functionality to edit the Rx
				final Pharmacy ph = (Pharmacy) parent
						.getItemAtPosition(position);
				MainActivity.alertMessage(getActivity(),
						"Please select an action",
						"Would you like to Remove or View/Edit details for "
								+ ph.getName() + "?", "Remove",
						// Remove
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (ph != null
										&& MainActivity.rxAdapter.existsRxWithPharm(Pharmacy
												.makeStringFromPharm(ph))) {
									Toast.makeText(
											getActivity(),
											"Can't delete this Pharmacy because an Rx with this Pharmacy already exists!",
											Toast.LENGTH_SHORT).show();
								} else if (MainActivity.phAdapter.removePh(ph
										.getId()) > 0) {
									// We were able to remove the doctor
									String message = "Removed from Pharmacy DB on "
											+ MainActivity.df.format(Calendar
													.getInstance().getTime());
									HistoryItem his = new HistoryItem(ph
											.getName(), message, "PD");
									MainActivity.hAdapter.insertHis(his);
									Toast.makeText(
											getActivity(),
											"Deleted Pharmacy " + ph.getName()
													+ " from the Pharmacy DB",
											Toast.LENGTH_SHORT).show();
									repopulateAdapter();
								} else {
									// Doctor has already been deleted
									Toast.makeText(
											getActivity(),
											"Sorry, we couldn't delete this Pharmacy. Are you sure you haven't edited or already removed this Pharmacy?",
											Toast.LENGTH_SHORT).show();
								}
							}
						}, "View/Edit",
						// Edit
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (ph != null) {
									Pharmacy.openEditPharmacyDialog(
											getActivity(), ph,
											PharmacyFragment.this);
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
	 * Method to repopulate the array adapter with changes produced in the edit
	 * dialog
	 */
	@Override
	public void repopulateAdapter() {
		phAdap.clear();
		phAdap.addAll(MainActivity.phAdapter.getAllPhs());
	}
}