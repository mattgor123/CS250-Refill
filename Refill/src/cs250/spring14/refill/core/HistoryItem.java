package cs250.spring14.refill.core;

import cs250.spring14.refill.R;

public class HistoryItem {

	public enum HistoryType {
		P("P"), // pharm
		PD("PD"), // pharm delete
		D("D"), // doctor
		DD("DD"), // doctor delete
		R("R"), // prescription
		PA("PA"), //patient
		U("U"); // User Created

		private String type;

		private HistoryType(String s) {
			this.type = s;
		}

		public String getType() {
			return type;
		}
	}

	private String owner;
	private String message;
	private HistoryType h;
	private long id;

	/**
	 * Constructor for a HistoryItem given an owner and a message
	 * 
	 * @param o
	 *            the owner
	 * @param m
	 *            the message
	 */
	public HistoryItem(String o, String m, String h) {
		this.setOwner(o);
		this.setMessage(m);
		this.setH(HistoryType.valueOf(h));
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	public String toString() {
		return this.owner + ": " + this.message;
	}

	/**
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @param owner
	 *            the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;

	}

	public int getIconResource() {
		switch (this.h) {
		case U:
			return R.drawable.user;
		case D:
			return R.drawable.doctor;
		case DD:
			return R.drawable.doctor;
		case P:
			return R.drawable.pharmacy;
		case PD:
			return R.drawable.pharmacy;
		case PA:
			return R.drawable.patient;
		default:
			return R.drawable.default_pill;
		}
	}

	public HistoryType getH() {
		return h;
	}

	public void setH(HistoryType h) {
		this.h = h;
	}
}