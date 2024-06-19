package cmg.cnsim.bitcoin;

import cmg.cnsim.engine.node.Node;
import cmg.cnsim.engine.transaction.Transaction;
import cmg.cnsim.engine.transaction.TransactionGroup;

import java.util.ArrayList;

/**
 * 
 * The Block class represents a block in a blockchain. It extends the TransactionGroup class.
 *
 * @author Sotirios Liaskos for the Enterprise Systems Group @ York University
 *
 */
public class Block extends TransactionGroup {
	
	// Generation of next unique ID for new blocks.
	public static int currID = 1;
	public static int getNextID() {
		return(currID++);
	}
	
	//Holds information for reporting purposes
	public Context contx;
	
	//The parent in blockchain (if any)
	protected TransactionGroup parent = null;
	//The height in blockchain (if in one)
	protected int height = 0;

	
	
	/**
	 * Information about the lifecycle of the block.
	 */
	public class Context {
		public long simTime;
		public long sysTime;
		public int nodeID;
		public String blockEvt;
		public double difficulty;
		public double cycles;
	}
	

    /**
     * Returns the information context of the block.
     *
     * @return The information context of the block.
     */
	public Context getContext(){
		return(this.contx);
	}
	
	
	/**
	 * @deprecated
	 */
	public void ____________Constructors() {}
	
    /**
     * Constructs a new {@link Block} object with the next available ID and an empty {@link Context}.
     */
	public Block(){
		contx = new Context();
		groupID = getNextID();
	}
	
    /**
     * Constructs a new {@link Block} object with the next available ID and an initial list 
     * of {@link Transaction} objects and an empty context. 
     * @param initial The initial list of {@link Transaction} objects.
     */
    public Block(ArrayList<Transaction> initial) {
    	super(initial);
		contx = new Context();
    	groupID = getNextID();
    }
	
    
    /**
     * Updates {@linkplain Block} with information pertaining to its validation.
     * Used in response to a validation event. 
     * @param newTransList The list of {@link Transaction} objects that are validated. 
     * @param simTime Simulation time at which the validation event occurred.
     * @param sysTime Real time at which the validation event occurred.
     * @param nodeID ID of the {@link Node} in which validation took place.
     * @param eventType Textual description of the type of event (for logging).
     * TODO: link to difficulty explanation.
     * @param difficulty Difficulty under which validation took place. 
     * TODO: check if this is correct.
     * @param cycles The number of cycles (hashes) expended for the validation. 
     */
    public void validateBlock(ArrayList<Transaction> newTransList,
    		long simTime,
    		long sysTime,
    		int nodeID,
    		String eventType,
    		double difficulty,
    		double cycles
    		) {
    	super.updateTransactionGroup(newTransList);
//    	groupID = getID();
    	contx = new Context();
		contx.simTime = simTime;
		contx.sysTime = sysTime;
		contx.nodeID = nodeID;
		contx.blockEvt = eventType;
		contx.difficulty = difficulty;
		contx.cycles = cycles;
    }
    
  
	//
	//
	// HEIGHT in BLOCKCHAIN
	//
	//
    /**
     * @deprecated
     */
	public void _________HeightAndParents() {}
	
	
	/**
	 * Returns the height of the {@linkplain Block} in a blockchain.
	 * @return The height of the {@linkplain Block} in a blockchain.
	 */
	public int getHeight() {
		return(height);
	}

	/**
	 * Sets the height of the {@linkplain Block} in a blockchain.
	 * @param height The height of the {@linkplain Block} in a blockchain.
	 */
	public void setHeight(int height) {
		this.height = height; 
	}
	
	
	//
	//
	// PARENTHOOD
	//
	//
    /**
     * Returns the parent {@linkplain TransactionGroup} of the {@linkplain Block}.
     *
     * @return The parent parent {@linkplain TransactionGroup} of the {@linkplain Block}.
     */
	public TransactionGroup getParent() {
		return(parent);
	}
	
	
    /**
     * Sets the parent of the {@linkplain Block}.
     *
     * @param parent The {@linkplain TransactionGroup} to be set as parent.
     */
	public void setParent(TransactionGroup parent) {
		this.parent = parent;
	}

	
    /**
     * Checks if the {@linkplain Block} has a parent.
     *
     * @return {@code true} if the block has a parent, {@code false} otherwise.
     */
	public boolean hasParent() {
		return (parent != null);
	}


//	public void addTransaction(Transaction transaction) {
//		super.addTransaction(transaction);
//	}

}
