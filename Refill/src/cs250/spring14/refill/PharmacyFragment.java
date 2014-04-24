package cs250.spring14.refill;

import java.util.Calendar;

import cs250.spring14.refill.core.HistoryItem;
import cs250.spring14.refill.core.Pharmacy;
import cs250.spring14.refill.view.PharmacyWrapper;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class PharmacyFragment extends DialogFragment implements RefreshableFragment {
	ListView phList;
	ArrayAdapter<Pharmacy> phAdap;

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
		phList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// This is where we will add functionality to edit the Rx
				final Pharmacy ph = (Pharmacy) parent.getItemAtPosition(position);
				MainActivity.alertMessage(getActivity(),
						"Please select an action",
						"Would you like to Remove or View/Edit details for "
								+ ph.getName() + "?", "Remove",
						// Remove
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// Remove functionality must be added here
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
									openEditPharmacyDialog(getActivity(), ph);
									repopulateAdapter();
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
	 * Helper method to open edit pharmacy dialog
	 * 
	 * @param context
	 *            the context for the dialog
	 * @param ph
	 *            the pharmacy to edit
	 */
	protected static void openEditPharmacyDialog(final Context context,
			final Pharmacy ph) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(1);
		// TODO Auto-generated method stub
		final EditText name = new EditText(context);
		final EditText email = new EditText(context);
		final EditText phone = new EditText(context);
		final EditText address = new EditText(context);
		Button add = new Button(context);
		add.setText("Update This Pharmacy/Exit Dialog");
		name.setSingleLine();
		email.setSingleLine();
		phone.setSingleLine();
		address.setSingleLine();
		name.setHint("    Name (must be unique): ");
		email.setHint("    Email: ");
		phone.setHint("    Phone: ");
		address.setHint("    Street (123 Oak): ");
		name.setText(ph.getName());
		email.setText(ph.getEmail());
		phone.setText(ph.getPhone());
		address.setText(ph.getStreetAddress());
		name.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
		email.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		phone.setInputType(InputType.TYPE_CLASS_NUMBER);
		address.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
		layout.addView(name);
		layout.addView(email);
		layout.addView(phone);
		layout.addView(address);
		layout.addView(add);
		builder.setView(layout);
		final Dialog d = builder.create();
		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Basic input checking; will get better with time
				String nameStr = name.getText().toString().trim();
				String emailStr = email.getText().toString().trim();
				String phoneStr = phone.getText().toString().trim();
				String addressStr = address.getText().toString().trim();
				if (!MainActivity.isValidName(nameStr)) {
					Toast.makeText(context,
							"Please ensure you've entered a valid name",
							Toast.LENGTH_SHORT).show();
				} else if (!MainActivity.isValidEmail(emailStr)) {
					Toast.makeText(context,
							"Please ensure you've entered a valid email",
							Toast.LENGTH_SHORT).show();
				} else if (!MainActivity.isValidPhone(phoneStr)) {
					Toast.makeText(context,
							"Please ensure you've entered a valid phone",
							Toast.LENGTH_SHORT).show();
				} else if (!MainActivity.isValidStreet(addressStr)) {
					Toast.makeText(
							context,
							"Please ensure you've entered a valid street address",
							Toast.LENGTH_SHORT).show();
				} else if (Pharmacy.shouldUpdatePharm(ph, nameStr, emailStr, phoneStr,
						addressStr)) {
					if (MainActivity.phAdapter.updatePh(ph.getId(), nameStr,
							emailStr, phoneStr, addressStr)) {
						// We successfully updated the Doctor
						String oldPharm = Pharmacy.makeStringFromPharm(ph);
						ph.setName(nameStr);
						ph.setEmail(emailStr);
						ph.setPhone(phoneStr);
						ph.setStreetAddress(addressStr);
						String newPharm = Pharmacy.makeStringFromPharm(ph);
						MainActivity.rxAdapter.updateAllRxWithPharmacy(
								oldPharm, newPharm);
						String message = "Updated in Pharmacy DB on "
								+ MainActivity.df.format(Calendar.getInstance()
										.getTime());
						HistoryItem his = new HistoryItem(
								nameStr, message, "P");
						MainActivity.hAdapter.insertHis(his);
						//hisAdap.add(his);
						//hisAdap.notifyDataSetChanged();
						d.dismiss();
					} else {
						// We didn't actually successfully update the Doctor
						String message = "Something went wrong updating your Pharmacy. Perhaps a Pharmacy with the name already exists?";
						Toast.makeText(context, message, Toast.LENGTH_SHORT)
								.show();
					}
				} else {
					// We shouldn't update; were just viewing
					d.dismiss();
				}
			}
		});
		d.show();
	}


	@Override
	public void repopulateAdapter() {
		phAdap.clear();
		phAdap.addAll(MainActivity.phAdapter.getAllPhs());
	}
}