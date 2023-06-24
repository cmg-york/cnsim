package cmg.cnsim.engine.transaction;

/**
 * Defines the main methods that a transaction containing type must include. Examples of ITxCointainers include transaction pools and blocks. 
 * 
 * @author Sotirios Liaskos Enterprise Systems Group (ESG) @ York University
 */
public interface ITxContainer {
	
	/**
	 * Return the ID of the container
	 * @return The ID of the container
	 */
	public int getID();
			
	/**
	 * Total number of transactions in the container.
	 * @return The number of transactions.
	 */
	public int getCount();
	
	/**
	 * Total size in bytes contained in the container.
	 * TODO: Are these bytes?
	 * @return The number of bytes in teh container.
	 */
	public float getSize();
	
	/**
	 * Total transaction value contained in the container.
	 * @return The total value of all transctions in the container.
	 */
	public float getValue();

	/**
	 * Return the contents of the container
	 * @return An array of <tt>Transaction</tt> objects.
	 */
	public Transaction[] getContent();
		
	/**
	 * Returns <tt>true</tt> if <tt>Transaction t</tt> is contained in the container.
	 * @param t The Transaction object in question
	 * @return <tt>true</tt> if <tt>t</tt> is in the container, <tt>false</tt> otherwise.
	 */
	public boolean contains(Transaction t);
	
	/**
	 * Returns <tt>true</tt> if transaction with id txID is contained in the container.
	 * @param txID The transaction ID in question
	 * @return <tt>true</tt> if <tt>t</tt> is in the container, <tt>false</tt> otherwise.
	 */
	public boolean contains(int txID);
	
	
    /**
     * Removes transactions from the group that are also contained in g.
     * @param g The <tt>TransactionGroup</tt> whose transactions are to be removed from the current group.
     */
    public void extractGroup(TransactionGroup g);
    
	/**
	 * Adds a transaction to the container
	 * @param t The <tt>Transaction</tt> object to be added.
	 */
	public void addTransaction(Transaction t);
	
	/**
	 * Removes transaction from the container
	 * @param t The <tt>Transaction</tt> object to be removed.
	 */
	public void removeTxFromContainer(Transaction t);
	
	/**
	 * Remove the first transaction from the container and return a reference to the transaction just removed. Total size and value is updated.
	 * @return A reference to the transaction removed.
	 */
	public Transaction removeNextTx();
	
	
	/**
	 * Print the IDs of each transaction in the container.
	 * @param sep The separator between the IDs, e.g., ",".  
	 * @return A string with the list of IDs.
	 */
	public String printIDs(String sep);

}
