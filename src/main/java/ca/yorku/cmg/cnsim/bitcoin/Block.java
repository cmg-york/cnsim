package ca.yorku.cmg.cnsim.bitcoin;

import ca.yorku.cmg.cnsim.engine.node.Node;
import ca.yorku.cmg.cnsim.engine.transaction.Transaction;
import ca.yorku.cmg.cnsim.engine.transaction.TransactionGroup;

import java.util.List;
import java.util.Objects;

/**
 * The Block class represents a block in a blockchain. It extends the TransactionGroup class.
 *
 * @author Sotirios Liaskos for the Enterprise Systems Group @ York University
 */
public class Block extends TransactionGroup implements Cloneable {

    // =========================
    // ID Generation & Management
    // =========================

    private static int currID = 1;

    public static int getNextID() {
        return currID++;
    }

    // ========================
    // Fields for Reporting & Blockchain Structure
    // =========================

    // Parent in blockchain (if any)
    protected TransactionGroup parent = null;

    // Height in blockchain (if in one)
    protected int height = 0;


    // Times the block was validated
    private long simTime_validation = -1;
    private long sysTime_validation = -1;

    // ID of the node that validated the block
    private int validationNodeID = -1;

    // Node currently in possession of the block
    private int currentNodeID = -1;

    // Difficulty under which validation took place
    private double validationDifficulty = -1;

    // Cycles dedicated for the validation of the block
    private double validationCycles = -1;

    // Last event that happened to the block
    private String lastBlockEvent = "-1";


    public Context context;

    /**
     * Contains information about the lifecycle of the block.
     */
    public static class Context {
        public long simTime;
        public long sysTime;
        public int nodeID;
        public String blockEvt;
        public double difficulty;
        public double cycles;
    }

    // =========================
    // Constructors
    // =========================

    /**
     * Constructs a new {@link Block} object with the next available ID and an empty {@link Context}.
     */
    public Block() {
        context = new Context();
        groupID = getNextID();
    }

    /**
     * Constructs a new {@link Block} object with the next available ID and an initial lis of {@link Transaction}
     * objects and an empty context.
     *
     * @param initial The initial list of {@link Transaction} objects.
     */
    public Block(List<Transaction> initial) {
        super(initial);
        context = new Context();
        groupID = getNextID();
    }

    // =========================
    // Utility Methods
    // =========================

    /**
     * Updates {@linkplain Block} with information pertaining to its validation. Used in response to a validation event.
     *
     * @param newTransList The list of {@link Transaction} objects that are validated.
     * @param simTime      Simulation time at which the validation event occurred.
     * @param sysTime      Real time at which the validation event occurred.
     * @param nodeID       ID of the {@link Node} in which validation took place.
     * @param eventType    Textual description of the type of event (for logging).
     *                     TODO: link to difficulty explanation.
     * @param difficulty   Difficulty under which validation took place.
     *                     TODO: check if this is correct.
     * @param cycles       The number of cycles (hashes) expended for the validation.
     */
    public void validateBlock(
            TransactionGroup newTransList,
            long simTime,
            long sysTime,
            int nodeID,
            String eventType,
            double difficulty,
            double cycles
    ) {
        super.updateTransactionGroup(newTransList.getTransactions());
//    	groupID = getID();

        simTime_validation = simTime;
        sysTime_validation = sysTime;
        validationNodeID = nodeID;
        currentNodeID = nodeID;

        validationDifficulty = difficulty;
        validationCycles = cycles;

        // Deprecated
        context = new Context();
        context.simTime = simTime;
        context.sysTime = sysTime;
        context.nodeID = nodeID;
        context.blockEvt = eventType;
        context.difficulty = difficulty;
        context.cycles = cycles;
    }

    /**
     * Checks if the {@linkplain Block} has a parent.
     *
     * @return {@code true} if the block has a parent, {@code false} otherwise.
     */
    public boolean hasParent() {
        return parent != null;
    }

    // =========================
    // Overridden Methods
    // =========================

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
                && Objects.equals(context, block.context)
                && Objects.equals(parent, block.parent)
                && Objects.equals(lastBlockEvent, block.lastBlockEvent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(context, parent, height, simTime_validation, sysTime_validation, validationNodeID, currentNodeID, validationDifficulty, validationCycles, lastBlockEvent);
    }

    // =========================
    // Getters & Setters
    // =========================

    public static int getCurrID() {
        return currID;
    }

    public static void setCurrID(int currID) {
        Block.currID = currID;
    }
    
	/**
	 * Resets the next available ID to 1. To be used for moving to the next experiment.
	 * @author Sotirios Liaskos
	 */
    public static void resetCurrID() {
        currID = 1;
    }

    
    
    
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public TransactionGroup getParent() {
        return parent;
    }

    public void setParent(TransactionGroup parent) {
        this.parent = parent;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public long getSimTime_validation() {
        return simTime_validation;
    }

    public void setSimTime_validation(long simTime_validation) {
        this.simTime_validation = simTime_validation;
    }

    public long getSysTime_validation() {
        return sysTime_validation;
    }

    public void setSysTime_validation(long sysTime_validation) {
        this.sysTime_validation = sysTime_validation;
    }

    public int getValidationNodeID() {
        return validationNodeID;
    }

    public void setValidationNodeID(int validationNodeID) {
        this.validationNodeID = validationNodeID;
    }

    public int getCurrentNodeID() {
        return currentNodeID;
    }

    public void setCurrentNodeID(int currentNodeID) {
        this.currentNodeID = currentNodeID;
    }

    public double getValidationDifficulty() {
        return validationDifficulty;
    }

    public void setValidationDifficulty(double validationDifficulty) {
        this.validationDifficulty = validationDifficulty;
    }

    public double getValidationCycles() {
        return validationCycles;
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

}
