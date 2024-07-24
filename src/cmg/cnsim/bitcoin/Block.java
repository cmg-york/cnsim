package cmg.cnsim.bitcoin;

import cmg.cnsim.engine.Simulation;
import cmg.cnsim.engine.node.Node;
import cmg.cnsim.engine.transaction.Transaction;
import cmg.cnsim.engine.transaction.TransactionGroup;

import java.util.ArrayList;
import java.util.Objects;

/**
 * 
 * The Block class represents a block in a blockchain. It extends the TransactionGroup class.
 *
 * @author Sotirios Liaskos for the Enterprise Systems Group @ York University
 *
 */
public class Block extends TransactionGroup implements Cloneable {
	
	// Generation of next unique ID for new blocks.
	public static int currID = 1;
	


	public static int getCurrID() {
		return currID;
	}


	public static void setCurrID(int currID) {
		Block.currID = currID;
	}


	public static int getNextID() {
		return(currID++);
	}
	
	//Holds information for reporting purposes
	public Context contx;
	
	//The parent in blockchain (if any)
	protected TransactionGroup parent = null;
	//The height in blockchain (if in one)
	protected int height = 0;

	
	//The times the block was validated
	private long simTime_validation = -1;
	private long sysTime_validation = -1;
	
	//The id of the node that validated the block
	private int validationNodeID = -1;
	
	//The node currently in possession of the block
	private int currentNodeID = -1;
	
	//The difficulty under which validation took place
	private double validationDifficulty = -1;
	
	//The cycles dedicated for the validation of the block
	private double validationCycles = -1;
	
	//The last event that happened to the block
	private String lastBlockEvent = "-1";
	
	



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
    	
    	simTime_validation = simTime;
    	sysTime_validation = sysTime;
    	validationNodeID = nodeID;
    	currentNodeID = nodeID;
    	
    	validationDifficulty = difficulty;
    	validationCycles = cycles;
    	
    	//Deprecated
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

	
	
	//
	//
	//
	// GETTERS AND SETTERS for Block information.
	//
	//
	
	
	
	

	public int getCurrentNodeID() {
		return currentNodeID;
	}


	public void setCurrentNodeID(int currentNodeID) {
		this.currentNodeID = currentNodeID;
	}


	public long getSimTime_validation() {
		return simTime_validation;
	}


	public long getSysTime_validation() {
		return sysTime_validation;
	}


	public int getValidationNodeID() {
		return validationNodeID;
	}


	public double getValidationDifficulty() {
		return validationDifficulty;
	}


	public double getValidationCycles() {
		return validationCycles;
	}
	
	
	public void setSimTime_validation(long simTime_validation) {
		this.simTime_validation = simTime_validation;
	}


	public void setSysTime_validation(long sysTime_validation) {
		this.sysTime_validation = sysTime_validation;
	}


	public void setValidationDifficulty(double validationDifficulty) {
		this.validationDifficulty = validationDifficulty;
	}


	public void setValidationCycles(double validationCycles) {
		this.validationCycles = validationCycles;
	}

	
	public String getLastBlockEvent() {
		return lastBlockEvent;
	}

	public void setLastBlockEvent(String lastBlockEvent) {
		this.lastBlockEvent = lastBlockEvent;
	}
	
	// clone() method overriding using @Overirde
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Block block = (Block) other;
        return height == block.height
                && simTime_validation == block.simTime_validation
                && sysTime_validation == block.sysTime_validation
                && validationNodeID == block.validationNodeID
                && currentNodeID == block.currentNodeID
                && Double.compare(validationDifficulty, block.validationDifficulty) == 0
                && Double.compare(validationCycles, block.validationCycles) == 0
                && Objects.equals(contx, block.contx)
                && Objects.equals(parent, block.parent)
                && Objects.equals(lastBlockEvent, block.lastBlockEvent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contx, parent, height, simTime_validation, sysTime_validation, validationNodeID, currentNodeID, validationDifficulty, validationCycles, lastBlockEvent);
    }
}
