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

/**
 * The Pharmacy class is the pharmacy object associated with a Rx. An Rx can not be added unless it
 * has a pharmacy.
 */
public class Pharmacy {

  private String name;
  private String email;
  private String phone;
  private String streetAddress;
  private long id;
  public static final String DEFAULT_ADDRESS = "No Address Entered";

  /**
   * Constructor for Pharmacy's given a name, e-mail, and phone
   * 
   * @param n the pharmacy's name
   * @param e the pharmacy's email
   * @param p the pharmacy's phone number
   * @param s the pharmacy's address
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
  @Override
  public String toString() {
    if (!streetAddress.isEmpty()) {
      return this.name + " <" + streetAddress + ">";
    } else {
      return this.name;
    }
  }

  /**
   * @return the name
   */
  public String getName() {
    return this.name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the email
   */
  public String getEmail() {
    return this.email;
  }

  /**
   * @param email the email to set
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * @return the streetAddress
   */
  public String getStreetAddress() {
    return this.streetAddress;
  }

  /**
   * @param streetAddress the streetAddress to set
   */
  public void setStreetAddress(String streetAddress) {
    this.streetAddress = streetAddress;
  }

  /**
   * @return the phone
   */
  public String getPhone() {
    return this.phone;
  }

  /**
   * @param phone the phone to set
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

  /**
   * Sets the pharmacy's id
   * 
   * @param id the pharmacy's id
   */
  public void setId(long id) {
    this.id = id;
  }

  /**
   * Helper method to open edit pharmacy dialog
   * 
   * @param context the context for the dialog
   * @param ph the pharmacy to edit
   */
  public static void openEditPharmacyDialog(final Context context, final Pharmacy ph, final RefreshableFragment fr) {
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
    name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
    email.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    phone.setInputType(InputType.TYPE_CLASS_NUMBER);
    address.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
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
        addressStr = (addressStr.length() == 0) ? DEFAULT_ADDRESS : addressStr;
        if (nameStr.length() == 0) {
          Toast.makeText(context, "Please ensure you've entered a valid name", Toast.LENGTH_SHORT).show();
        } else if (!MainActivity.isValidEmail(emailStr)) {
          Toast.makeText(context, "Please ensure you've entered a valid email", Toast.LENGTH_SHORT).show();
        } else if (!MainActivity.isValidPhone(phoneStr)) {
          Toast.makeText(context, "Please ensure you've entered a valid phone", Toast.LENGTH_SHORT).show();
        } else if (shouldUpdatePharm(ph, nameStr, emailStr, phoneStr, addressStr)) {
          if (MainActivity.phAdapter.updatePh(ph.getId(), nameStr, emailStr, phoneStr, addressStr)) {
            // We successfully updated the Doctor
            String oldPharm = makeStringFromPharm(ph);
            ph.setName(nameStr);
            ph.setEmail(emailStr);
            ph.setPhone(phoneStr);
            ph.setStreetAddress(addressStr);
            String newPharm = makeStringFromPharm(ph);
            MainActivity.rxAdapter.updateAllRxWithPharmacy(oldPharm, newPharm);
            String message = "Updated in Pharmacy DB on " + MainActivity.df.format(Calendar.getInstance().getTime());
            HistoryItem his = new HistoryItem(nameStr, message, "P");
            MainActivity.hAdapter.insertHis(his);
            // hisAdap.add(his);
            // hisAdap.notifyDataSetChanged();
            fr.repopulateAdapter();
            fr.getmListener().onComplete(true);
            d.dismiss();
          } else {
            // We didn't actually successfully update the Doctor
            String message =
                "Something went wrong updating your Pharmacy. Perhaps a Pharmacy with the name already exists?";
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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
   * Generates a string representation from a Pharmacy object
   * 
   * @param ph the Pharmacy object
   * @return the string representation of the pharmacy
   */
  public static String makeStringFromPharm(Pharmacy ph) {
    return ph.getName() + " :: " + ph.getEmail() + " :: " + ph.getPhone() + " :: " + ph.getStreetAddress();
  }

  /**
   * Generate a Pharmacy object from a string representation of a pharmacy
   * 
   * @param string the string representation of a pharmacy
   * @return the Pharmacy object created from the string
   */
  public static Pharmacy makePharmFromString(String string) {
    String[] tokens = string.split(" :: ");
    if (tokens.length != 4) {
      // Something got screwed up
      return null;
    } else {
      return new Pharmacy(tokens[0], tokens[1], tokens[2], tokens[3]);
    }
  }

  /**
   * Determines if the user made changes to a pharmacy while viewing the doctor's information
   * 
   * @param ph the pharmacy object
   * @param name the pharmacy's name
   * @param email the pharmacy's email
   * @param phone the pharmacy's phone number
   * @param address the pharmacy's address
   * @return true if user didnt make changes, otherwise false
   */
  public static boolean shouldUpdatePharm(Pharmacy ph, String name, String email, String phone, String address) {
    return ((!ph.getName().equals(name)) || (!ph.getEmail().equals(email)) || (!ph.getPhone().equals(phone)) || (!ph
        .getStreetAddress().equals(address)));
  }

}
