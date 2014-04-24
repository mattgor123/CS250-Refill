package cs250.spring14.refill.core;

import java.util.Calendar;

import cs250.spring14.refill.MainActivity;
import cs250.spring14.refill.R;
import cs250.spring14.refill.view.RefreshableFragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

public class Patient {
	private String name;
	private int color;
	private long id; 
	
	public Patient(String name, int color) {
		this.setName(name);
		this.setColor(color);	}

	@Override
	public String toString() {
		return "<" + name + ">";
		
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public static String getColorStringFromColorInt(int color) {
		switch (color) {
		case -7012353:
			return "LightBlue";
		case -13159:
			return "LightOrange";
		case -103:
			return "LightYellow";
		case -7995515:
			return "LightGreen";
		case -3355393:
			return "LightPurple";
		case -13057:
			return "LightPink";
		default:
				return "White";
		}
	}

	public static int getColorIntFromColorString(String str) {
		switch (str) {
			case "LightBlue":
			return -7012353;

			case "LightOrange":
			return -13159;

			case "LightYellow":
			return -103;

			case "LightGreen":
			return -7995515;

			case "LightPurple":
			return -3355393;

			case "LightPink":
			return -13057;
			
			default:
				return Color.WHITE;

		}

	}
	public static String makeStringFromPatient(Patient p) {
		return p.name + " :: " + getColorStringFromColorInt(p.color);
	}
	
	public static Patient makePatientFromString(String string) {
		String[] tokens = string.split(" :: ");
		if (tokens.length != 2) {
			// Something got screwed up
			return null;
		} else {
			return new Patient(tokens[0], getColorIntFromColorString(tokens[1]));
		}
	}
	/**
	public static void openEditPatientDialog(final Context context,
			final Patient ph, final RefreshableFragment fr) {
		final Dialog dialog = new Dialog(context);
		dialog.setTitle("Editing a patient");
		dialog.setContentView(R.layout.patient_dialog);
		final EditText et = (EditText) dialog.findViewById(R.id.patient);
		et.setText(ph.getName());
		final RadioGroup colors = (RadioGroup) dialog.findViewById(R.id.colors);
		//Start the dialog with the currently selected color already selected
		switch (ph.getColor()) {
		case -7012353:
			colors.check(R.id.lb);
			break;
		case -13159:
			colors.check(R.id.lo);
			break;
		case -103:
			colors.check(R.id.ly);
			break;
		case -7995515:
			colors.check(R.id.lg);
			break;
		case -3355393:
			colors.check(R.id.lpu);
			break;
		case -13057:
			colors.check(R.id.lpi);
		default:
			colors.check(R.id.w);
		}
		Button ok = (Button) dialog.findViewById(R.id.ok);
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//Step 1) Make sure we have a valid name
				String str = et.getText().toString().trim();
				int color = colors.getCheckedRadioButtonId();
				if (str.length() == 0) {
					Toast.makeText(context, "Please ensure you've entered a valid patient name", Toast.LENGTH_SHORT).show();
				}
				//Step 2) Make sure patient doesn't already exist 
				else if (shouldUpdatePatient(ph, nameStr, emailStr, phoneStr,
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
						HistoryItem his = new HistoryItem(
								nameStr, message, "P");
						MainActivity.hAdapter.insertHis(his);
						//hisAdap.add(his);
						//hisAdap.notifyDataSetChanged();
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
				//Step 3) Check if we should update the patient!
				else {
					int color = colors.getCheckedRadioButtonId();
					String message;
					switch(color) {
					case R.id.w:
						paAdapter.insertPa(new Patient(str,Color.WHITE));
						message = "Added to Patients DB on "
								+ df.format(Calendar.getInstance().getTime());
						hAdapter.insertHis(new HistoryItem(str, message,
								"PA"));
						dialog.dismiss();
						break;						
					case R.id.lb:
						paAdapter.insertPa(new Patient(str,Color.parseColor("#94FFFF")));
						message = "Added to Patients DB on "
								+ df.format(Calendar.getInstance().getTime());
						hAdapter.insertHis(new HistoryItem(str, message,
								"PA"));
						dialog.dismiss();
						break;
					case R.id.lo:
						paAdapter.insertPa(new Patient(str,Color.parseColor("#FFCC99")));
						message = "Added to Patients DB on "
								+ df.format(Calendar.getInstance().getTime());
						hAdapter.insertHis(new HistoryItem(str, message,
								"PA"));
						dialog.dismiss();
						break;
					case R.id.ly:
						paAdapter.insertPa(new Patient(str, Color.parseColor("#FFFF99")));
						message = "Added to Patients DB on "
								+ df.format(Calendar.getInstance().getTime());
						hAdapter.insertHis(new HistoryItem(str, message,
								"PA"));
						dialog.dismiss();
						break;
					case R.id.lg:
						paAdapter.insertPa(new Patient(str, Color.parseColor("#85FF85")));
						message = "Added to Patients DB on "
								+ df.format(Calendar.getInstance().getTime());
						hAdapter.insertHis(new HistoryItem(str, message,
								"PA"));
						dialog.dismiss();
						break;
					case R.id.lpu:
						paAdapter.insertPa(new Patient(str, Color.parseColor("#CCCCFF")));
						message = "Added to Patients DB on "
								+ df.format(Calendar.getInstance().getTime());
						hAdapter.insertHis(new HistoryItem(str, message,
								"PA"));
						dialog.dismiss();
						break;
					case R.id.lpi:
						paAdapter.insertPa(new Patient(str,Color.parseColor("#FFCCFF")));
						message = "Added to Patients DB on "
								+ df.format(Calendar.getInstance().getTime());
						hAdapter.insertHis(new HistoryItem(str, message,
								"PA"));
						dialog.dismiss();
						break;
					default:
						//We really shouldn't be here
						return;
					}
					fr.
					}		
				}
			}
		});
		dialog.show();		
	}
}

	public static boolean shouldUpdatePatient(Patient pa, String name, int color) {
		return ((!pa.getName().equals(name)) || !(pa.getColor() == (color)));
	}
**/
}
