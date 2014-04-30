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
 * The Doctor class is the doctor object associated with a Rx. An Rx can not be added unless it has
 * a Doctor.
 */
public class Doctor {

  private String name;
  private String email;
  private String phone;
  private long id;

  /**
   * Constructor for Doctor given a name, e-mail, and phone
   * 
   * @param n - the doctor's inputted name
   * @param e - the doctor's inputted email
   * @param p - the doctor's inputted phone number
   */
  public Doctor(String n, String e, String p) {
    this.setName(n);
    this.setEmail(e);
    this.setPhone(p);
  }

  /**
   * Used in the select doctor at the spinner
   * 
   * @return the doctor, formatted as a string
   */
  @Override
  public String toString() {
    if (!phone.isEmpty()) {
      return this.name + " <" + this.phone + ">";
    } else
      return this.name;
  }

  /**
   * @return the doctor's name
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
   * @return the doctor's email
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
   * @return the doctor's phone number
   */
  public String getPhone() {
    return this.phone;
  }

  /**
   * @param phone the phone number to set
   */
  public void setPhone(String phone) {
    this.phone = phone;
  }

  /**
   * @return the doctor's ID from the DB
   */
  public long getId() {
    return this.id;
  }

  /**
   * 
   * @param id set the doctor's ID from the DB
   */
  public void setId(long id) {
    this.id = id;
  }

  /**
   * Helper method to open edit doctor dialog
   * 
   * @param context the context for the dialog
   * @param dr the doctor to edit
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
    name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
    email.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
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
          Toast.makeText(context, "Please ensure you've entered a valid name", Toast.LENGTH_SHORT).show();
        } else if (!MainActivity.isValidEmail(emailStr)) {
          Toast.makeText(context, "Please ensure you've entered a valid email", Toast.LENGTH_SHORT).show();
        } else if (!MainActivity.isValidPhone(phoneStr)) {
          Toast.makeText(context, "Please ensure you've entered a valid phone", Toast.LENGTH_SHORT).show();
        } else if (shouldUpdateDr(dr, nameStr, emailStr, phoneStr)) {
          if (MainActivity.drAdapter.updateDr(dr.getId(), nameStr, emailStr, phoneStr)) {
            // We successfully updated the Doctor
            String oldStr = makeStringFromDoc(dr);
            dr.setName(nameStr);
            dr.setEmail(emailStr);
            dr.setPhone(phoneStr);
            String newStr = makeStringFromDoc(dr);
            MainActivity.rxAdapter.updateAllRxWithDoctor(oldStr, newStr);
            String message = "Updated in Doctors DB on " + MainActivity.df.format(Calendar.getInstance().getTime());
            HistoryItem his = new HistoryItem(nameStr, message, "D");
            MainActivity.hAdapter.insertHis(his);
            d.dismiss();
            fr.repopulateAdapter();
            fr.getmListener().onComplete(true);
          } else {
            // We didn't actually successfully update the Doctor
            String message =
                "Something went wrong updating your Doctor. Perhaps a Doctor with the name already exists?";
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
          }
        } else {
          // Nothing to update
          d.dismiss();
        }
      }
    });
    d.show();
  }

  /**
   * RxDatabase store the Rx's doctor information as string, therefore this method returns the
   * doctor object as a string
   * 
   * @param dr the doctor object
   * @returnthe doctor as a string
   */
  public static String makeStringFromDoc(Doctor dr) {
    return dr.getName() + " :: " + dr.getEmail() + " :: " + dr.getPhone();
  }

  /**
   * Since RxDatabase store doctors as string, this method is needed to get a doctor object from an
   * RxItem
   * 
   * @param string the doctor as a string
   * @return
   */
  public static Doctor makeDocFromString(String string) {
    String[] tokens = string.split(" :: ");
    if (tokens.length != 3) {
      // Something got screwed up
      return null;
    } else {
      return new Doctor(tokens[0], tokens[1], tokens[2]);
    }
  }

  /**
   * Determines if the user made changes to a doctor while viewing the doctor's information
   * 
   * @param dr the doctor object
   * @param name the doctor's name
   * @param email the doctor's email
   * @param phone the doctor's phone number
   * @return true if user didnt make changes, otherwise false
   */
  public static boolean shouldUpdateDr(Doctor dr, String name, String email, String phone) {
    return ((!dr.getName().equals(name)) || (!dr.getEmail().equals(email)) || (!dr.getPhone().equals(phone)));
  }

}
