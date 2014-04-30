package cs250.spring14.refill.core;
/**
 * The ScheduleItem class is the object that represents an Rx
 * in the calendar view
 */
public class ScheduleItem {

  private long id;
  private String name;
  private int pos;
  private int color;

  /**
   * The ScheduleItem Constructor
   * 
   * @param name the ScheduleItem's name
   * @param pos the ScheduleItem's position in the calendar view
   * @param the SheduleItem's color seen in the calendar view
   */
  public ScheduleItem(String name, int pos, int color) {
    this.setName(name);
    this.setPos(pos);
    this.setColor(color);
  }

  /**
   * @return the ScheduleItem's id based on its position in the Schedule DB
   */
  public long getId() {
    return id;
  }

  /** Sets the ScheduleItem's id
   * 
   * @param id the ShceduleItem's id based on its position in the Schedule DB
   */
  public void setId(long id) {
    this.id = id;
  }

  /**
   * @return the ScheduleItem name
   */
  public String getName() {
    return name;
  }

  /** Sets the ScheduleItem's name
   * 
   * @param name the ShceduleItem's name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the ScheduleItem's position in the calendar view
   */
  public int getPos() {
    return pos;
  }

  /** Sets the ScheduleItem's position in the calendar view
   * 
   * @param pos the ShceduleItem's position in the calendar view
   */
  public void setPos(int pos) {
    this.pos = pos;
  }

  /**
   * @return the ScheduleItem's color in the calendar view
   */
  public int getColor() {
    return color;
  }

  /** Sets the ScheduleItem's color in the calendar view
   * 
   * @param color the ShceduleItem's color in the calendar view
   */
  public void setColor(int color) {
    this.color = color;
  }
}
