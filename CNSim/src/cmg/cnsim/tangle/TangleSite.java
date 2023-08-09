package cmg.cnsim.tangle;

import cmg.cnsim.engine.transaction.ITxContainer;
import cmg.cnsim.engine.transaction.Transaction;
import cmg.cnsim.engine.transaction.TransactionGroup;

/**
 * 
 * 
 * A {@link TangleSite} is a representation of a Tangle 1.0 transaction that has (a) been validated, (b) attached to the DAG (when completed).
 * It is itself a {@link ITxContainer} object.
 * @author Sotirios Liaskos for the Conceptual Modeling Group @ York University
 * 
 */
public class TangleSite implements ITxContainer {

	Transaction[] trans = new Transaction[1];
	int[] parents = new int[2];

	/**
	 * Create a new {@link TangleSite} based on a {@link Transaction} object and pointers to the two parents.
	 * @param t The {@link Transaction} object to be contained in the site.
	 * @param parents An array of integers containing the unique IDs of the transactions that the site will point at.
	 * NOTE: SIte IDs are the same as the contained transaction IDs. 
	 */
	public TangleSite(Transaction t, int[] parents) {
		this(t);
		this.parents = parents;
	}

	/**
	 * As {@link TangleSite#TangleSite(Transaction, int[])} but without specifying the parents (yet). 
	 * @param t The transaction of which to create the site.
	 */
	public TangleSite(Transaction t) {
		trans[0] = t;
	}
	
	/**
	 * Set the parents of the transaction. 
	 * @param parents  An array of integers containing the unique IDs of the transactions that the site will point at.
	 * NOTE: SIte IDs are the same as the contained transaction IDs.
	 * @author Sotirios Liaskos
	 */
	public void setParents(int[] parents) {
		this.parents = parents;
	}

	/**
	 * Get the parents of a transaction.
	 * @return An array of integers containing the unique IDs of the transactions that the site will point at.
	 * NOTE: SIte IDs are the same as the contained transaction IDs.
	 * @author Sotirios Liaskos
	 */
	public int[] getParents() {
		return(this.parents);
	}
	
	/**
	 * Get the transaction contained in the site.
	 */
	@Override
	public Transaction[] getContent() {
		return (trans);
	}

	/**
	 * Get the size of the site -- it is always one.
	 */
	@Override
	public int getCount() {
		return 1;
	}

	/**
	 * The ID of the site is the same as the ID of the transaction it contains.
	 */
	@Override
	public int getID() {
		return trans[0].getID();
	}

	/**
	 * The size of the site is equal to the size of the transaction.
	 */
	@Override
	public float getSize() {
		return trans[0].getSize();
	}


	/**
	 * The value of the site is equal to the value of the transaction.
	 */
	@Override
	public float getValue() {
		return trans[0].getValue();
	}

	
	//
	//
	// Baggage from the interface 
	//
	//
	
	@Override
	public void addTransaction(Transaction t) {
		// TODO Auto-generated method stub
	}

	
	@Override
	public void removeTxFromContainer(Transaction t) {
		// TODO Auto-generated method stub
	}

	@Override
	public Transaction removeNextTx() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void extractGroup(TransactionGroup g) {
		// TODO Auto-generated method stub
		
	}
	
    @Override
  	public boolean contains(Transaction t) {
		boolean found = false;
		for (Transaction r: trans) {
			if (r.getID() == t.getID()) {found = true;}
		}
		return found;
	}  
	
    
  	@Override
  	public boolean contains(int txID) {
		boolean found = false;
		for (Transaction r: trans) {
			if (r.getID() == txID) {found = true;}
		}
		return found;
	}  

    
	
	@Override
	public String printIDs(String sep) {
		// TODO Auto-generated method stub
		return null;
	}


}
