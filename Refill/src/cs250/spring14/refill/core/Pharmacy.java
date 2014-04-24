package cs250.spring14.refill.core;

import java.util.Calendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import cs250.spring14.refill.MainActivity;
import cs250.spring14.refill.view.RefreshableFragment;

public class Pharmacy {

	private String name;
	private String email;
	private String phone;
	private String streetAddress;
	private long id;

	/**
	 * Constructor for Doctor given a name, e-mail, and phone
	 * 
	 * @param n
	 * @param e
	 * @param p
	 * @param s
	 */
	public Pharmacy(String n, String e, String p, String s) {
		this.setName(n);
		this.setEmail(e);
		this.setPhone(p);
		this.setStreetAddress(s);
	}

	/**
	 * @return the pharmacy, formatted as a string
	 */
	public String toString() {
		return name + " <" + streetAddress + ">";
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the streetAddress
	 */
	public String getStreetAddress() {
		return streetAddress;
	}

	/**
	 * @param streetAddress
	 *            the streetAddress to set
	 */
	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone
	 *            the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the ID
	 */
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Helper method to open edit pharmacy dialog
	 * 
	 * @param context
	 *            the context for the dialog
	 * @param ph
	 *            the pharmacy to edit
	 */
	public static void openEditPharmacyDialog(final Context context,
			final Pharmacy ph, final RefreshableFragment fr) {
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
				if (nameStr.length() == 0) {
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
				} else if (shouldUpdatePharm(ph, nameStr, emailStr, phoneStr,
						addressStr)) {
					if (MainActivity.phAdapter.updatePh(ph.getId(), nameStr,
							emailStr, phoneStr, addressStr)) {
						// We successfully updated the Doctor
						String oldPharm = makeStringFromPharm(ph);
						ph.setName(nameStr);
						ph.setEmail(emailStr);
						ph.setPhone(phoneStr);
						ph.setStreetAddress(addressStr);
						String newPharm = makeStringFromPharm(ph);
						MainActivity.rxAdapter.updateAllRxWithPharmacy(
								oldPharm, newPharm);
						String message = "Updated in Pharmacy DB on "
								+ MainActivity.df.format(Calendar.getInstance()
										.getTime());
						HistoryItem his = new HistoryItem(nameStr, message, "P");
						MainActivity.hAdapter.insertHis(his);
						// hisAdap.add(his);
						// hisAdap.notifyDataSetChanged();
						fr.repopulateAdapter();
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

	public static String makeStringFromPharm(Pharmacy ph) {
		return ph.getName() + " :: " + ph.getEmail() + " :: " + ph.getPhone()
				+ " :: " + ph.getStreetAddress();
	}

	public static Pharmacy makePharmFromString(String string) {
		String[] tokens = string.split(" :: ");
		if (tokens.length != 4) {
			// Something got screwed up
			return null;
		} else {
			return new Pharmacy(tokens[0], tokens[1], tokens[2], tokens[3]);
		}
	}

	public static boolean shouldUpdatePharm(Pharmacy ph, String name,
			String email, String phone, String address) {
		return ((!ph.getName().equals(name)) || (!ph.getEmail().equals(email))
				|| (!ph.getPhone().equals(phone)) || (!ph.getStreetAddress()
				.equals(address)));
	}

}
