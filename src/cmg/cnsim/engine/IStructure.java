package cmg.cnsim.engine;

/**
 * Interface for finality structure. E.g. a blockchain or a DAG.
 *  
 * @author Sotirios Liaskos for the Conceptual Modeling Group @ York University
 */
public interface IStructure {
	/**
	 * Print the structure for human-readable presentation as a string. 
	 * @return An array of string presenting the structure. 
	 */
	String[] printStructure();
	
	
	/**
	 * Checks if a transaction is part of the structure.
	 * @param txID The ID of the transaction.
	 * @return {@code true} if the transaction is part of the structure and {@code false} if it is not 
	 */
	boolean transactionInStructure(long txID);
}
