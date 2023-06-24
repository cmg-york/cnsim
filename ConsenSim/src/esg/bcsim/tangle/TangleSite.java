package esg.bcsim.tangle;

import cmg.cnsim.engine.transaction.ITxContainer;
import cmg.cnsim.engine.transaction.Transaction;
import cmg.cnsim.engine.transaction.TransactionGroup;

public class TangleSite implements ITxContainer {

	Transaction[] trans = new Transaction[1];
	int[] parents = new int[2];

	public TangleSite(Transaction t, int[] parents) {
		trans[0] = t;
		this.parents = parents;
	}

	public TangleSite(Transaction t) {
		trans[0] = t;
	}
	
	public void setParents(int[] parents) {
		this.parents = parents;
	}

	public int[] getParents() {
		return(this.parents);
	}
	
	@Override
	public Transaction[] getContent() {
		return (trans);
	}

	@Override
	public int getCount() {
		return 1;
	}

	@Override
	public int getID() {
		return trans[0].getID();
	}

	@Override
	public float getSize() {
		return trans[0].getSize();
	}

	@Override
	public void addTransaction(Transaction t) {
		// Not needed here?
		
	}

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
