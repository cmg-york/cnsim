package cmg.cnsim.engine.message;

/**
 * For protocol co-ordination.
 * @author Sotirios Liaskos for the Conceptual Modeling Group @ York University
 * 
 */
public abstract class Message {
	public static int currID = 1;

	protected int ID;
	protected float size;
	protected long creationTime;


	/**
	 * Get the next available ID number to assign to the message.
	 * @return The next message ID number
	 */
	public static int getNextTxID() {
	    return(currID++);
	}
}
