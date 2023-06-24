package cmg.cnsim.engine.transaction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/** 
 * A list containing various transactions. Can be used as a block or other needed grouping (e.g. pool).
 * @author Sotirios Liaskos for the Conceptual Modeling Group @ York University
 */
public class TransactionGroup implements ITxContainer {

	private ArrayList<Transaction> group;
	protected int groupID;
	protected float totalValue;
	protected float totalSize;
	
	
	//
    //
    // Constructors
    //
    //
	@Deprecated
	public void ________________Constructors() {}
	
	
    /**
     * Plain constructor, simply initializes the internal data structure.
     */
    public TransactionGroup() {
        group = new ArrayList<>();
    }

    /**
     * Accepts an already created ArrayList of transactions. Calculates the total value and size in the group.   
     * @param initial An already created ArrayList of transactions
     */
    public TransactionGroup(ArrayList<Transaction> initial) {
        group = initial;
        for (Transaction t: initial) {
        	totalValue += t.getValue();
        	totalSize += t.getSize();
        }
    }

    /**
     * Loads a transaction group from a text file. Each line in the file is a separate transaction. Each transaction is a comma separated string containing the following information: Transaction ID, Time Created, Total Value, Total Size, First Arrival NodeID. Transaction ID must run from <tt>1</tt> to <tt>n</tt> strictly increasing by 1 at each step (error otherwise). Time must not decrease as transactions IDs increase.
     *  
     * @param fileName A name to the text file containing the transactions.
     * @param hasHeader Whether the file has a header. 
     * @throws Exception Formatting error.
     */
    public TransactionGroup(String fileName, boolean hasHeader) throws Exception {
    	this();
    	
    	String l = "";
    	String delimiter = ",";
    	
    	int tCount = 1;
    	long lastTime = 0;
    	int id;
    	long time;
    	float value;
    	float size;        	
    	int nodeID;
    	
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        while ((l = br.readLine())!=null) {
        	if (hasHeader) {
        		hasHeader = false;
        	} else {
	        	String[] t = l.split(delimiter);
	        	id = Integer.parseInt(t[0]);
	        	if (id != tCount) throw new Exception("Error in workload file: transaction IDs must start from 1 and strictly increase by 1.");
	        	tCount++;
	        	time = Long.parseLong(t[1]);
	        	if (time < lastTime) throw new Exception("Error in workload file: time must not decrease as transaction IDs increase.");
	        	lastTime = time;
	        	value = Float.parseFloat(t[2]);
	        	size = Float.parseFloat(t[3]);        	
	        	nodeID = Integer.parseInt(t[4]);

	        	this.addTransaction(new Transaction(id,time,value,size,nodeID));       	
        	}
        }
        br.close();
    }
        
    
    //
    //
    // C o n t e n t    E d i t i n g
    //
    //
	@Deprecated
    public void ___________ContentEditing() {}
    
    
    /**
     * Replace transaction group with a new one.
     * @param initial An array list of <tt>Transaction</tt> objects, to replace the existing one.
     */
    public void updateTransactionGroup(ArrayList<Transaction> initial) {
        group = initial;
        for (Transaction t: initial) {
        	totalValue += t.getValue();
        	totalSize += t.getSize();
        }
    }
        

	/**
	 * See {@linkplain ITxContainer#addTransaction(Transaction)}.
	 */
	@Override
	public void addTransaction(Transaction t) {
		group.add(t);
		totalSize += t.getSize();
		totalValue += t.getValue();
	}
	
	/**
	 * See {@linkplain ITxContainer#removeTxFromContainer(Transaction)}.
	 */
	@Override
    public void removeTxFromContainer(Transaction t) {
        group.remove(t);
		totalSize -= t.getSize();
		totalValue -= t.getValue();
    }
    
	/**
	 * See {@linkplain ITxContainer#removeNextTx()}.
	 */
	@Override
    public Transaction removeNextTx(){
    	Transaction t = group.remove(0);
		totalSize -= t.getSize();
		totalValue -= t.getValue();
        return t;
    }

	
	/**
	 * See {@linkplain ITxContainer#extractGroup(TransactionGroup)}.
	 */
    @Override
    public void extractGroup(TransactionGroup g) {
    	for (Transaction t: g.getGroup()) {
    		this.removeTxFromContainer(t);
    	}
    }
    
    //
    //
    // E x a m i n e   C o n t e n t
    //
    //
	@Deprecated
    public void _____________ExamineContent() {}

	/**
	 * See {@linkplain ITxContainer#contains(Transaction)}.
	 */
    @Override
  	public boolean contains(Transaction t) {
		boolean found = false;
		for (Transaction r: group) {
			if (r.getID() == t.getID()) {found = true;}
		}
		return found;
	}  

	/**
	 * See {@linkplain ITxContainer#contains(int)}.
	 */
  	@Override
  	public boolean contains(int txID) {
		boolean found = false;
		for (Transaction r: group) {
			if (r.getID() == txID) {found = true;}
		}
		return found;
	}  
     
	/**
	 * Check if the group overlaps with another transaction group
	 * @param p The <tt>TransactionGroup</tt> in question.
	 * @return <tt>true</tt> of there is at least one transaction in <tt>p</tt> that is contained in the group, <tt>false</tt>, otherwise.
	 */
	public boolean overlapsWithbyObj(TransactionGroup p) {
		boolean result = false;
		for (Transaction t : p.getGroup()) {
			if (group.contains(t)) {
				result = true;
				break;
			}
		}
		return (result);
	}
	
	/**
	 * As {@link TransactionGroup#overlapsWithbyObj(TransactionGroup)} but criterion that is used is transaction ID.
	 * @param g The {@link TransactionGroup} object in question.
	 * @return <tt>true</tt> of there is at least one transaction in <tt>g</tt> that is contained in the group, <tt>false</tt>, otherwise.
	 */
	public boolean overlapsWith(TransactionGroup g) {
		boolean found = false;
		for (Transaction r: group) {
			for (Transaction t: g.getContent()) {
				if (t.getID() == r.getID()) {found = true;return found;}
			}
		}
		return found;
	}
	
	/**
	 * Retrieves a TransactionGroup containing the top N transactions based on a given size limit and comparator.
	 *
	 * @param sizeLimit The maximum cumulative size in bytes of transactions allowed in the the result.
	 * @param comp The comparator used to sort the transactions.
	 * @return A {@link TransactionGroup} object containing the top N transactions that do not exceed sizeLimit based on given comparator.
	 */
	public TransactionGroup getTopN(float sizeLimit, Comparator<Transaction> comp) {
		
		ArrayList<Transaction> result = new ArrayList<Transaction>();
		Collections.sort(group, comp);
		
		int i=0;
		float sum = 0;
		while ( (sum <= sizeLimit) && (i < group.size()) ) {
			sum += group.get(i).getSize();
			result.add(group.get(i));
			i++;
		}
		if (sum > sizeLimit) { //Last one was exceeding the limit.
			result.remove(i-1);
		}
		return (new TransactionGroup(result));
	}
	
	
	//
    //
    // Extracing Information
    //
    //
	@Deprecated
  	public void ______________ExtractingInfo() {}
  	
  	

    //
    // Getting Information about the Pool
    //
    

	/**
	 * See {@linkplain ITxContainer#getID()}.
	 */
	@Override
	public int getID() {
		return groupID;
	}

	/**
	 * See {@linkplain ITxContainer#getCount()}.
	 */
	@Override
    public int getCount() {
    	return (group.size());
    }
	
	/**
	 * See {@linkplain ITxContainer#getSize()}.
	 */
	@Override
	public float getSize() {
		return totalSize;
	}

	/**
	 * See {@linkplain ITxContainer#getValue()}.
	 */
	@Override
	public float getValue() {
		return totalValue;
	}

	/**
	 * See {@linkplain ITxContainer#getContent()}.
	 */
	@Override
	public Transaction[] getContent() {
		return  group.toArray(new Transaction[group.size()]);
	}

	/**
	 * Return the ArrayList of transactions in the group
	 * @return An <tt>ArrayList</tt> of <tt>Transaction</tt> objects representing the transactions in the group.
	 */
	public ArrayList<Transaction> getGroup(){
		return (group);
	}
	
	/**
	 * Get the transaction of the group at index <tt>index</tt>. Does not check if index exists.
	 * @param index The index from <tt>0</tt> to <tt>n-1</tt>
	 * @return A reference to the <tt>Transaction</tt> object.
	 */
	public Transaction getTransaction(int index) {
		return group.get(index);
	}
	

  	
  	
  	
    //
    //
    // Printing the Group
    //
    //
	@Deprecated
	public void ______________PrintingGroup() {}
  	
	
	
    /**
	 * See {@linkplain ITxContainer#printIDs(String)}.
     */
    @Override
    public String printIDs(String sep) {
    	String s = "{";
    	for(Transaction t:group) {
    		s += t.getID() + sep;
    	}
    	if (s.length()>1) 
    		s = s.substring(0, s.length()-1) + "}";
    	else 
    		s = s + "}";
    	return (s);
    }
    
    /**
     * Generates a debug printout of the Transaction IDs in the pool.
     *
     * @return A string containing the IDs of the Transactions in the pool, separated by commas.
     */
    public String debugPrintPoolTx(){
        String s = "";
        for (Transaction t : group){
            s = s + t.getID() + ", ";
        }
        return (s);
    }
	
}
