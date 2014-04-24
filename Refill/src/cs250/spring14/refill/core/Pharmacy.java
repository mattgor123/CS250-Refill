package cs250.spring14.refill.core;

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
	
	public static boolean shouldUpdatePharm(Pharmacy ph, String name, String email,
			String phone, String address) {
		return ((!ph.getName().equals(name)) || (!ph.getEmail().equals(email))
				|| (!ph.getPhone().equals(phone)) || (!ph.getStreetAddress()
				.equals(address)));
	}

}