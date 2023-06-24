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
}
