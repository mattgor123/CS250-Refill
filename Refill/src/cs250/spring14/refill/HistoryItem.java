package cs250.spring14.refill;

public class HistoryItem {

	private String owner;
	private String message;
	private long id;
	
	/**
	 * Constructor for a HistoryItem given an owner and a message
	 * @param o the owner
	 * @param m the message
	 */
	public HistoryItem(String o, String m) {
		this.setOwner(o);
		this.setMessage(m);
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
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
	 * @param owner the owner to set
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
}
