package cs250.spring14.refill.core;

public class ScheduleItem {

  private long id;
  private String name;
  private int pos;
  private int color;

  public ScheduleItem(String name, int pos, int color) {
    this.setName(name);
    this.setPos(pos);
    this.setColor(color);
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getPos() {
    return pos;
  }

  public void setPos(int pos) {
    this.pos = pos;
  }

  public int getColor() {
    return color;
  }

  public void setColor(int color) {
    this.color = color;
  }
}
