package cs250.spring14.refill.core;

import android.graphics.Color;

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
}
