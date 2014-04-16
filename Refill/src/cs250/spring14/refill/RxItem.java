package cs250.spring14.refill;

import java.util.Calendar;
import java.util.Date;

public class RxItem {
	
	private String name;
	private String patient;
	private String symptoms;
	private String sideEffects;
	private int dose;
	private int pillsPerDay;
	private Date start;
	private int daysBetweenRefills; //We gotta rethink how we will do this - user writes how many days
	private String pharmacy;
	private String physician;
	private String mdPhoneNumb;
	private String rxNumb;
	private Date lastrefill;
	
	public RxItem(String name, String patient, String symptoms, String sideEffects,
			int dose, int pillsPerDay, Date start, int daysBetweenRefills, String pharmacy, String physician,
			String mdPhoneNumb, String rxNumb, Date lastrefill) {
		this.name = name;
		this.patient = patient;
		this.symptoms = symptoms;
		this.sideEffects = sideEffects;
		this.dose = dose;
		this.pillsPerDay = pillsPerDay;
		this.start = start;
		this.daysBetweenRefills = daysBetweenRefills;
		this.pharmacy = pharmacy;
		this.physician = physician;
		this.mdPhoneNumb = mdPhoneNumb;
		this.rxNumb = rxNumb;
		this.lastrefill = lastrefill;
	}
	private Date getNextRefillDate() {
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
	
	public String getPatient() {
		return this.patient;
	}
	
	public String getSymptoms() {
		return this.symptoms;
	}
	
	public String getSideEffects() {
		return this.sideEffects;
	}
	
	public int getDose() {
		return this.dose;
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
	
	public String getPharmacy() {
		return this.pharmacy;
	}
	
	public String getPhysician() {
		return this.physician;
	}
	
	public String getMdPhoneNumber() {
		return this.mdPhoneNumb;
	}
	
	public String getRxNumb() {
		return this.rxNumb;
	}	
}
