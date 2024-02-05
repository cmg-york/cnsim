package cmg.cnsim.engine.transaction;

import cmg.cnsim.engine.AbstractSampler;

import java.util.ArrayList;

public class TransactionWorkload extends TransactionGroup {

    private AbstractSampler sampler;
    private long timeEnd = 0;
    
    //Sampler based workload generation
    @Deprecated
    public void ___________________Sampler_Based() {}
    
    /**
     * Constructs a TransactionWorkload with the given sampler.
     * @param s The sampler used to generate transaction attributes.
     */
    public TransactionWorkload(AbstractSampler s) {
        this.sampler = s;
    }
    
    /**
     * Adds a specified number of transactions from a given start time.
     * @param num The number of transactions to add.
     * @param startTime The start time of the first transaction.
     * @throws ArithmeticException If the start time or number of transactions is less than 0.
     * @author Sotirios Liaskos, Nahid Alimohammadi
     */
    private void addTransactions(long num, long startTime){
    	if(startTime < 0)
    		throw new ArithmeticException("startTime < 0");
    	if(num < 0)
    		throw new ArithmeticException("num < 0");
        long currTime = startTime;

        for (long i = 1; i <= num; i++){
            currTime += sampler.getNextTransactionArrivalInterval();
            addTransaction(currTime);
        }
        timeEnd = currTime;
    }

    /**
     * Appends a specified number of transactions after the last transaction in the workload.
     * @param num The number of transactions to append.
     * @throws ArithmeticException If the number of transactions is less than 0.
     * @author Sotirios Liaskos, Nahid Alimohammadi
     */
    public void appendTransactions(long num){
    	if(num < 0)
    		throw new ArithmeticException("num < 0");
        addTransactions(num, timeEnd);
    }
    
    /**
     * Adds a transaction with the given current time.
     * @param currTime The current time of the transaction.
     * @author Sotirios Liaskos
     */
    public void addTransaction(long currTime){
        Transaction t;

        t = new Transaction(Transaction.getNextTxID(),
                currTime,
                sampler.getNextTransactionFeeValue(),
                sampler.getNextTransactionSize());
        t.setType(Transaction.Type.HONEST);
        addTransaction(t);
    }
    
	/**
	 * Picks a specified number of random transactions from the workload based on the given percentile value.
	 * @param transNo The number of transactions to pick.
	 * @param percentile The percentile value to determine the range for picking transactions. For example from 
	 * 100 transactions with indexes 1..100, percentile 0.25 will return samples from the first 25 transactions 
	 * (indexes 1..25)  
	 * @return An ArrayList of randomly picked transactions possibly with duplicates.
	 * @author Sotirios Liaskos
	 */
	public ArrayList<Transaction> pickRandomTransactions(int transNo,float percentile) {
		ArrayList<Transaction> rtx = new ArrayList<Transaction>();
		
		for (int i=1;i<=transNo;i++) {
			rtx.add(getTransaction(sampler.getRandomNum(0, Math.round((getCount()-1)*percentile))));
		}
		return rtx;
	}

    public ArrayList<Transaction> getAllTransactions() {
    	return getGroup();
    }
    //TODO Why did not used get group directly
	   

    //Workload generation from File
    @Deprecated
    public void ___________________File_Based() {}
    
    /**
     * See {@linkplain TransactionGroup#TransactionGroup(String, boolean)} 
     * @param fileName The workload filename to be read.
     * @param hasHeader Whether the file has a header.
     * @throws Exception Generic IO exception.
     */
    public TransactionWorkload(String fileName, boolean hasHeader) throws Exception {
    	super(fileName, hasHeader);
    }

          
}
    
