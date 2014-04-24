package cs250.spring14.refill.view;

import java.util.Calendar;

import cs250.spring14.refill.MainActivity;
import cs250.spring14.refill.R;
import cs250.spring14.refill.core.HistoryItem;
import cs250.spring14.refill.core.Doctor;
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

public class DoctorFragment extends DialogFragment implements RefreshableFragment {
	ListView docList;
	ArrayAdapter<Doctor> docAdap;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().setTitle("Doctors");

		View rootView = inflater.inflate(R.layout.fragment_doc, container,
				false);
		docList = (ListView) rootView.findViewById(R.id.listView1);
		docAdap = new DoctorWrapper(rootView.getContext(), 0,
				MainActivity.drAdapter.getAllDrs());
		docList.setAdapter(docAdap);
		docList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// This is where we will add functionality to edit the Rx
				final Doctor doc = (Doctor) parent.getItemAtPosition(position);
				MainActivity.alertMessage(getActivity(),
						"Please select an action",
						"Would you like to Remove or View/Edit details for "
								+ doc.getName() + "?", "Remove",
						// Remove
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// Remove functionality must be added here
								if (doc != null
										&& MainActivity.rxAdapter.existsRxWithDoc(Doctor
												.makeStringFromDoc(doc))) {
									Toast.makeText(
											getActivity(),
											"Can't delete this Doctor because an Rx with this Doctor already exists!",
											Toast.LENGTH_SHORT).show();
								} else if (MainActivity.drAdapter.removeDr(doc
										.getId()) > 0) {
									// We were able to remove the doctor
									String message = "Removed from Doctor DB on "
											+ MainActivity.df.format(Calendar
													.getInstance().getTime());
									HistoryItem his = new HistoryItem(doc
											.getName(), message, "DD");
									MainActivity.hAdapter.insertHis(his);
									Toast.makeText(
											getActivity(),
											"Deleted Doctor " + doc.getName()
													+ " from the Doctor DB",
											Toast.LENGTH_SHORT).show();
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
								if (doc != null) {
									openEditDoctorDialog(getActivity(), doc,DoctorFragment.this);
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

	@Override
	public void repopulateAdapter() {
		docAdap.clear();
		docAdap.addAll(MainActivity.drAdapter.getAllDrs());
	}
	
	/**
	 * Helper method to open edit doctor dialog
	 * 
	 * @param context
	 *            the context for the dialog
	 * @param dr
	 *            the doctor to edit
	 */
	public static void openEditDoctorDialog(final Context context, final Doctor dr, final RefreshableFragment fr) {
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
				} else if (Doctor.shouldUpdateDr(dr, nameStr, emailStr, phoneStr)) {
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
						d.dismiss();
						fr.repopulateAdapter();
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
}
