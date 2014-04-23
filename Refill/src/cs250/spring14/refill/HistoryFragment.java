package cs250.spring14.refill;

import java.util.Calendar;

import cs250.spring14.refill.core.Doctor;
import cs250.spring14.refill.core.HistoryItem;
import cs250.spring14.refill.core.Pharmacy;
import cs250.spring14.refill.core.HistoryItem.HistoryType;
import cs250.spring14.refill.view.HistoryWrapper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class HistoryFragment extends Fragment implements RefreshableFragment {
	ListView historyList;
	ArrayAdapter<HistoryItem> hisAdap;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_his, container,
				false);
		historyList = (ListView) rootView.findViewById(R.id.listView1);
		hisAdap = new HistoryWrapper(rootView.getContext(), 0,
				MainActivity.hAdapter.getAllHis());
		historyList.setAdapter(hisAdap);
		historyList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// This is where we will add functionality to edit the
				// Dr./Pharmacy
				final HistoryItem hi = (HistoryItem) parent
						.getItemAtPosition(position);
				// Doctor
				if (hi.getH() == HistoryType.D) {
					MainActivity.alertMessage(getActivity(),
							"Please select an action",
							"Would you like to Remove or View/Edit details for Dr. "
									+ hi.getOwner() + "?", "Remove",
							// Remove
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									if (MainActivity.drAdapter
											.removeDrByName(hi.getOwner())) {
										// We were able to remove the pharmacy
										String message = "Removed from Doctor DB on "
												+ MainActivity.df
														.format(Calendar
																.getInstance()
																.getTime());
										HistoryItem his = new HistoryItem(hi
												.getOwner(), message,
												"DD");
										MainActivity.hAdapter
												.insertHis(his);
										//hisAdap.add(his);
										//hisAdap.notifyDataSetChanged();
										repopulateAdapter();
									} else {
										// Doctor has already been deleted
										Toast.makeText(
												getActivity(),
												"Sorry, we couldn't delete this Doctor. Are you sure you haven't edited or already removed this Doctor?",
												Toast.LENGTH_SHORT).show();
									}
								}
							}, "View/Edit",
							// Edit
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Doctor dr = MainActivity.drAdapter
											.getDocByName(hi.getOwner());
									if (dr != null) {
										openEditDoctorDialog(getActivity(), dr);
									} else {
										Toast.makeText(
												getActivity(),
												"Sorry,  we couldn't edit this Doctor. Are you sure you haven't removed this Doctor or edited their name?",
												Toast.LENGTH_SHORT).show();
									}
								}
							});
				}
				// Pharmacy
				else if (hi.getH() == HistoryType.P) {
					MainActivity.alertMessage(getActivity(),
							"Please select an action",
							"Would you like to Remove or View/Edit details for Pharmacy "
									+ hi.getOwner() + "?", "Remove",
							// Remove
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									if (MainActivity.phAdapter
											.removePhByName(hi.getOwner())) {
										// We were able to remove the pharmacy
										String message = "Removed from Pharmacy DB on "
												+ MainActivity.df
														.format(Calendar
																.getInstance()
																.getTime());
										HistoryItem his = new HistoryItem(hi
												.getOwner(), message,
												"PD");
										MainActivity.hAdapter
												.insertHis(his);
										//hisAdap.add(his);
										//hisAdap.notifyDataSetChanged();
										repopulateAdapter();
									} else {
										// Pharmacy has already been deleted
										Toast.makeText(
												getActivity(),
												"Sorry, we couldn't delete this pharmacy. Are you sure you haven't edited or already removed this Pharmacy?",
												Toast.LENGTH_SHORT).show();
									}
								}
							}, "View/Edit",
							// Edit
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Pharmacy ph = MainActivity.phAdapter
											.getPharmByName(hi.getOwner());
									if (ph != null) {
										openEditPharmacyDialog(getActivity(),
												ph);
									} else {
										Toast.makeText(
												getActivity(),
												"Sorry,  we couldn't edit this pharmacy. Are you sure you haven't removed this Pharmacy or edited its name?",
												Toast.LENGTH_SHORT).show();
									}
								}
							});
				}

			}

		});
		return rootView;
	}

	/**
	 * Helper method to open edit pharmacy dialog
	 * 
	 * @param context
	 *            the context for the dialog
	 * @param ph
	 *            the pharmacy to edit
	 */
	protected void openEditPharmacyDialog(final Context context,
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
					Toast.makeText(getActivity(),
							"Please ensure you've entered a valid name",
							Toast.LENGTH_SHORT).show();
				} else if (!MainActivity.isValidEmail(emailStr)) {
					Toast.makeText(getActivity(),
							"Please ensure you've entered a valid email",
							Toast.LENGTH_SHORT).show();
				} else if (!MainActivity.isValidPhone(phoneStr)) {
					Toast.makeText(getActivity(),
							"Please ensure you've entered a valid phone",
							Toast.LENGTH_SHORT).show();
				} else if (!MainActivity.isValidStreet(addressStr)) {
					Toast.makeText(
							getActivity(),
							"Please ensure you've entered a valid street address",
							Toast.LENGTH_SHORT).show();
				} else if (shouldUpdatePharm(ph, nameStr, emailStr, phoneStr,
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
						repopulateAdapter();
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

	/**
	 * Helper method to open edit doctor dialog
	 * 
	 * @param context
	 *            the context for the dialog
	 * @param dr
	 *            the doctor to edit
	 */
	protected void openEditDoctorDialog(final Context context, final Doctor dr) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(1);
		// TODO Auto-generated method stub
		final EditText name = new EditText(context);
		final EditText email = new EditText(context);
		final EditText phone = new EditText(context);
		Button add = new Button(context);
		add.setText("Update This Doctor/Exit Dialog");
		name.setSingleLine();
		email.setSingleLine();
		phone.setSingleLine();
		name.setHint("    Name (must be unique): ");
		email.setHint("    Email: ");
		phone.setHint("    Phone: ");
		name.setText(dr.getName());
		email.setText(dr.getEmail());
		phone.setText(dr.getPhone());
		name.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
		email.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		phone.setInputType(InputType.TYPE_CLASS_NUMBER);
		layout.addView(name);
		layout.addView(email);
		layout.addView(phone);
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
				if (!MainActivity.isValidName(nameStr)) {
					Toast.makeText(getActivity(),
							"Please ensure you've entered a valid name",
							Toast.LENGTH_SHORT).show();
				} else if (!MainActivity.isValidEmail(emailStr)) {
					Toast.makeText(getActivity(),
							"Please ensure you've entered a valid email",
							Toast.LENGTH_SHORT).show();
				} else if (!MainActivity.isValidPhone(phoneStr)) {
					Toast.makeText(getActivity(),
							"Please ensure you've entered a valid phone",
							Toast.LENGTH_SHORT).show();
				} else if (shouldUpdateDr(dr, nameStr, emailStr, phoneStr)) {
					if (MainActivity.drAdapter.updateDr(dr.getId(), nameStr,
							emailStr, phoneStr)) {
						// We successfully updated the Doctor
						String oldStr = Doctor.makeStringFromDoc(dr);
						dr.setName(nameStr);
						dr.setEmail(emailStr);
						dr.setPhone(phoneStr);
						String newStr = Doctor.makeStringFromDoc(dr);
						MainActivity.rxAdapter.updateAllRxWithDoctor(oldStr,
								newStr);
						String message = "Updated in Doctors DB on "
								+ MainActivity.df.format(Calendar.getInstance()
										.getTime());
						HistoryItem his = new HistoryItem(
								nameStr, message, "D");
						MainActivity.hAdapter.insertHis(his);
						//hisAdap.add(his);
						//hisAdap.notifyDataSetChanged();
						repopulateAdapter();
						d.dismiss();
						//onResume();
					} else {
						// We didn't actually successfully update the Doctor
						String message = "Something went wrong updating your Doctor. Perhaps a Doctor with the name already exists?";
						Toast.makeText(context, message, Toast.LENGTH_SHORT)
								.show();
					}
				} else {
					// Nothing to update
					d.dismiss();
				}
			}
		});
		d.show();
	}

	private boolean shouldUpdateDr(Doctor dr, String name, String email,
			String phone) {
		return ((!dr.getName().equals(name)) || (!dr.getEmail().equals(email)) || (!dr
				.getPhone().equals(phone)));
	}

	private boolean shouldUpdatePharm(Pharmacy ph, String name, String email,
			String phone, String address) {
		return ((!ph.getName().equals(name)) || (!ph.getEmail().equals(email))
				|| (!ph.getPhone().equals(phone)) || (!ph.getStreetAddress()
				.equals(address)));
	}

	@Override
	public void onResume() {
		super.onResume();
		repopulateAdapter();
	}

	@Override
	public void repopulateAdapter() {
		hisAdap.clear();
		hisAdap.addAll(MainActivity.hAdapter.getAllHis());
	}

}
