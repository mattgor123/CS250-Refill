package cs250.spring14.refill.core;

import java.util.Calendar;
import java.util.Date;

public class RxItem {

	private String name;
	private Patient patient;
	private String symptoms;
	private String sideEffects;
	private double dose;
	private int pillsPerDay;
	private Date start;
	private int daysBetweenRefills; // We gotta rethink how we will do this -
									// user writes how many days
	private Pharmacy pharmacy;
	private Doctor doc;
	private String rxNumb;
	private Date lastrefill;
	private long id;

	public RxItem(String name, Patient patient, String symptoms,
			String sideEffects, double dose, int pillsPerDay, Date start,
			int daysBetweenRefills, Pharmacy pharmacy, Doctor doc,
			String rxNumb, Date lastrefill) {
		this.name = name;
		this.patient = patient;
		this.symptoms = symptoms;
		this.sideEffects = sideEffects;
		this.dose = dose;
		this.pillsPerDay = pillsPerDay;
		this.start = start;
		this.daysBetweenRefills = daysBetweenRefills;
		this.pharmacy = pharmacy;
		this.doc = doc;
		this.rxNumb = rxNumb;
		this.lastrefill = lastrefill;
	}

	public Date getNextRefillDate() {
		Calendar c = Calendar.getInstance();
		c.setTime(lastrefill);
		c.add(Calendar.DATE, daysBetweenRefills);
		return c.getTime();
	}

	@Override
	public String toString() {
		return this.name + ", Rx for " + this.patient;
	}

	public String getName() {
		return this.name;
	}

	public Patient getPatient() {
		return this.patient;
	}

	public String getSymptoms() {
		return this.symptoms;
	}

	public String getSideEffects() {
		return this.sideEffects;
	}

	public double getDose() {
		return (double) Math.round(this.dose*1000)/1000;
	}

	public int getPillsPerDay() {
		return this.pillsPerDay;
	}

	public Date getStartDate() {
		return this.start;
	}

	public Date getLastRefill() {
		return this.lastrefill;
	}

	public int getDaysBetweenRefills() {
		return this.daysBetweenRefills;
	}

	public Pharmacy getPharmacy() {
		return this.pharmacy;
	}

	public Doctor getDoc() {
		return this.doc;
	}

	public String getDocString() {
		return Doctor.makeStringFromDoc(this.doc);
	}

	public String getPhString() {
		return Pharmacy.makeStringFromPharm(this.pharmacy);
	}
	
	public String getPatientString() {
		return Patient.makeStringFromPatient(this.patient);
	}

	public String getRxNumb() {
		return this.rxNumb;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return this.id;
	}
}
