package cs250.spring14.refill.core;

import cs250.spring14.refill.R;

/**
 * The HistoryItem class is the history object that appears in the history tab
 */
public class HistoryItem {

  /**
   * HistoryType enum is used so that we can get an icon resource, i.e. it determine which type of
   * picture to get.
   */
  public enum HistoryType {
    P("P"), // pharm
    PD("PD"), // pharm delete
    D("D"), // doctor
    DD("DD"), // doctor delete
    R("R"), // prescription
    PA("PA"), // patient
    PAD("PAD"), // patient deleted
    U("U"); // User Created

    private String type;

    /**
     * 
     * @param s the type of object that the HistoryType is
     */
    private HistoryType(String s) {
      this.type = s;
    }

    /**
     * 
     * @return the type of object that the HistoryType is
     */
    public String getType() {
      return this.type;
    }
  }

  private String owner;
  private String message;
  private HistoryType h;
  private long id;

  /**
   * Constructor for a HistoryItem given an owner and a message
   * 
   * @param o the owner
   * @param m the message
   */
  public HistoryItem(String o, String m, String h) {
    this.setOwner(o);
    this.setMessage(m);
    this.setH(HistoryType.valueOf(h));
  }

  /**
   * @return the History type's message
   */
  public String getMessage() {
    return this.message;
  }

  /**
   * @param message the message to set
   */
  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public String toString() {
    return this.owner + ": " + this.message;
  }

  /**
   * @return the owner a reference to the creating object
   */
  public String getOwner() {
    return this.owner;
  }

  /**
   * @param owner the owner to set
   */
  public void setOwner(String owner) {
    this.owner = owner;
  }

  /**
   * 
   * @return the HistoryType's ID from the DB
   */
  public long getId() {
    return this.id;
  }

  /**
   * Sets the HistoryType's id from the DB
   * 
   * @param id the id to be set
   */
  public void setId(long id) {
    this.id = id;

  }

  /**
   * This uses a switch statement to determine what Icon Resource to get
   * 
   * @return the resource picture
   */
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
      case PAD:
        return R.drawable.patient;
      default:
        return R.drawable.default_pill_his;
    }
  }

  /**
   * 
   * @return the HistoryItem's HistoryType
   */
  public HistoryType getH() {
    return h;
  }

  /**
   * Sets the HistoryItem's HistoryType
   * 
   * @param h the HistroyItem's new HistoryType
   */
  public void setH(HistoryType h) {
    this.h = h;
  }
}
