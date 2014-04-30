package cs250.spring14.refill.core;

import java.util.Calendar;
import java.util.Date;

/**
 * The RxItem class is the Rx object
 */
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


  /**
   * RxItem constructor
   * 
   * @param name
   * @param patient
   * @param symptoms
   * @param sideEffects
   * @param dose
   * @param pillsPerDay
   * @param start
   * @param daysBetweenRefills
   * @param pharmacy
   * @param doc
   * @param rxNumb
   * @param lastrefill
   */
  public RxItem(String name, Patient patient, String symptoms, String sideEffects, double dose, int pillsPerDay,
      Date start, int daysBetweenRefills, Pharmacy pharmacy, Doctor doc, String rxNumb, Date lastrefill) {
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

  /**
   * Determines the next refill date by adding the last refill date by the amount of days between
   * each refill
   * 
   * @return the next refill date as a Date type
   */
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

  /**
   * Gets the RxItem's name
   * 
   * @return the RxItem's name
   */
  public String getName() {
    return this.name;
  }

  /**
   * Gets the RxItem's patient as a Patient object
   * 
   * @return the RxItem's patient
   */
  public Patient getPatient() {
    return this.patient;
  }

  /**
   * @return the Rx Item's symptoms
   */
  public String getSymptoms() {
    return this.symptoms;
  }

  /**
   * @return the Rx Item's side effects
   */
  public String getSideEffects() {
    return this.sideEffects;
  }

  /**
   * @return the RxItem's dose
   */
  public double getDose() {
    return (double) Math.round(this.dose * 1000) / 1000;
  }

  /**
   * @return how frequently the medicine is to be taken daily
   */
  public int getPillsPerDay() {
    return this.pillsPerDay;
  }

  /**
   * @return the RxItem's start date
   */
  public Date getStartDate() {
    return this.start;
  }

  /**
   * @return the RxItem's last refill date
   */
  public Date getLastRefill() {
    return this.lastrefill;
  }

  /**
   * @return amount of days between each time the RxItem must be refilled
   */
  public int getDaysBetweenRefills() {
    return this.daysBetweenRefills;
  }

  /**
   * @return the RxItem's pharmacy
   */
  public Pharmacy getPharmacy() {
    return this.pharmacy;
  }

  /**
   * @return the RxItem's prescribing doctor object
   */
  public Doctor getDoc() {
    return this.doc;
  }

  /**
   * @return the string representation of the RxItem's prescribing doctor
   */
  public String getDocString() {
    return Doctor.makeStringFromDoc(this.doc);
  }

  /**
   * @return the string representation of the RxItem's pharmacy
   */
  public String getPhString() {
    return Pharmacy.makeStringFromPharm(this.pharmacy);
  }

  /**
   * @return the string representation of the RxItem's patient object
   */
  public String getPatientString() {
    return Patient.makeStringFromPatient(this.patient);
  }

  /**
   * @return the RxItem's Rx Number
   */
  public String getRxNumb() {
    return this.rxNumb;
  }

  /**
   * Sets the RxItem's id according to its location in the RX Database
   * 
   * @param id the RxItem's id in the RX Database
   */
  public void setId(long id) {
    this.id = id;
  }

  /**
   * @return the Rx Item's id according to its location in the Rx Database
   */
  public long getId() {
    return this.id;
  }

  /**
   * Determines if the user made changes to the RxItem object in the view dialog
   * 
   * @param name
   * @param patient
   * @param symptoms
   * @param sideEffects
   * @param dose
   * @param pillsPerDay
   * @param start
   * @param daysBetweenRefills
   * @param pharmacy
   * @param doc
   * @param rxNumb
   * @param lastrefill
   * @return true if the user didnt make any changes, false otherwise
   */
  public boolean shouldUpdateRx(String name, String patient, String symp, String sideEffects, double dose, int ppd,
      int dbr, String pharm, String doc, String rxnumb) {
    return ((!getName().equals(name)) || (!getPatientString().equals(patient)) || (!getSymptoms().equals(symp))
        || (!getSideEffects().equals(sideEffects)) || !(getDose() == dose) || !(getPillsPerDay() == ppd)
        || !(getDaysBetweenRefills() == dbr) || (!getPhString().equals(pharm)) || (!getDocString().equals(doc)) || !(getRxNumb()
          .equals(rxnumb)));
  }
}
