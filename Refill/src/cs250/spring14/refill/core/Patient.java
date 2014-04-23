package cs250.spring14.refill.core;

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

	public static String makeStringFromPatient(Patient p) {
		return p.name + " :: " + p.color;
	}
	
	public static Patient makePatientFromString(String string) {
		String[] tokens = string.split(" :: ");
		if (tokens.length != 2) {
			// Something got screwed up
			return null;
		} else {
			return new Patient(tokens[0], Integer.valueOf(tokens[1]));
		}
	}
}
