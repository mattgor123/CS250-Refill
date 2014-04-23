package cs250.spring14.refill.core;

public class Doctor {

	private String name;
	private String email;
	private String phone;
	private long id;

	/**
	 * Constructor for Doctor given a name, e-mail, and phone
	 * 
	 * @param n
	 * @param e
	 * @param p
	 */
	public Doctor(String n, String e, String p) {
		this.setName(n);
		this.setEmail(e);
		this.setPhone(p);
	}

	/**
	 * @return the doctor, formatted as a string
	 */
	public String toString() {
		return name + " <" + phone + ">";
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
	
	public static String makeStringFromDoc(Doctor dr) {
		return dr.getName() + " :: " + dr.getEmail() + " :: " + dr.getPhone();
	}
	
	public static Doctor makeDocFromString(String string) {
		String[] tokens = string.split(" :: ");
		if (tokens.length != 3) {
			// Something got screwed up
			return null;
		} else {
			return new Doctor(tokens[0], tokens[1], tokens[2]);
		}
	}

}
