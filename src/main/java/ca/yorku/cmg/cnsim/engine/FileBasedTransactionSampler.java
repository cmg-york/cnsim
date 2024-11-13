package ca.yorku.cmg.cnsim.engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class FileBasedTransactionSampler extends AbstractTransactionSampler {
	String transactionsFilePath;
	AbstractTransactionSampler alternativeSampler = null;
	
    private int requiredTransactionLines = Config.getPropertyInt("workload.numTransactions");
	private float lastArrivalTime = 0;
    private Queue<Long> transactionSizes = new LinkedList<>();
    private Queue<Float> transactionFeeValues = new LinkedList<>();
    private final Queue<Long> transactionArrivalTimes = new LinkedList<>();
	

    
    public FileBasedTransactionSampler(String transactionsFilePath){
    	this.transactionsFilePath = transactionsFilePath;
    	try {
			LoadTransactionWorkload();
		} catch (Exception e) {
			//e.printStackTrace();
			System.err.println("Error loading workload: " + e.getMessage());
			System.exit(-1);
		}
    }
 
    public FileBasedTransactionSampler(String transactionsFilePath, AbstractTransactionSampler randomSampler) {
    	this.alternativeSampler = randomSampler;
    	this.transactionsFilePath = transactionsFilePath;
    	try {
			LoadTransactionWorkload();
		} catch (Exception e) {
			//e.printStackTrace();
			System.err.println("Error loading workload: " + e.getMessage());
			System.exit(-1);
		}
    }
    
    
	//Loading
	
    public void LoadTransactionWorkload() throws Exception {
    	LoadTransactionWorkload(true);
    }
    
    
	public void LoadTransactionWorkload(boolean hasHeaders) throws Exception {
		int lineCount = 0;
		String line;
		try (BufferedReader br = new BufferedReader(new FileReader(transactionsFilePath))) {
			while ((line = br.readLine()) != null) {
				lineCount++;
				String[] values = line.split(",");
				if (values.length != 4) {
					continue; // Skip lines that don't have exactly 4 values
				}
				if (hasHeaders && lineCount == 1) {
					continue; // Skip first line
				}
				try {
					transactionSizes.add(Long.parseLong(values[1].trim()));
					transactionFeeValues.add(Float.parseFloat(values[2].trim()));
					transactionArrivalTimes.add(Long.parseLong(values[3].trim()));
				} catch (NumberFormatException e) {
					System.err.println("Error parsing transaction line: " + line);
				}
			}
		} catch (IOException e) {
			System.err.println("Error loading workload: no such file or directory.");
			System.exit(-1);
			//e.printStackTrace();
		}
		if (hasHeaders) lineCount--;
		if (lineCount < requiredTransactionLines) {
			if (alternativeSampler == null) {
				throw new Exception("    The transaction file does not contain enough lines as per configuration file. Required: "
						+ requiredTransactionLines + ", Found: " + lineCount + ". Define alternative sampler for the additional intervals or update config file.");
			} else {
				System.out.println("    The transaction file does not contain enough lines as per configuration file. Required: "
						+ requiredTransactionLines + ", Found: " + lineCount + ". Additional arrrivals to be drawn from alternative sampler.");
			}
		} else if (lineCount > requiredTransactionLines) {
			System.out.println("Warning: Transaction file contains more lines than required transactions as per configuration file. Required: "
					+ requiredTransactionLines + ", Found: " + lineCount);
		}
	}
	
	


    // Override the methods to return values from the queues
    @Override
    public float getNextTransactionArrivalInterval() throws Exception {
    	float arrivalTime;
    	float interval;
    	
    	if (!transactionArrivalTimes.isEmpty()) {
    		arrivalTime = transactionArrivalTimes.poll();
    		interval = arrivalTime - lastArrivalTime;
    		lastArrivalTime = arrivalTime;
    	} else if (alternativeSampler != null) {
    		interval = alternativeSampler.getNextTransactionArrivalInterval();
    	} else {
    		throw new Exception("Transaction file has less transactions than specified in configuration file. Alternative Sampler not specified.");
    	}
        return (interval); 
    }

    @Override
    public float getNextTransactionFeeValue() throws Exception {
    	if (!transactionFeeValues.isEmpty()) {
    		return(transactionFeeValues.poll());
    	} else if (alternativeSampler != null) {
    		return(alternativeSampler.getNextTransactionFeeValue());
    	} else {
    		throw new Exception("Transaction file has less transactions than specified in configuration file. Alternative Sampler not specified.");
    	}
    }

    @Override
    public long getNextTransactionSize() throws Exception {
    	if (!transactionSizes.isEmpty()) {
    		return(transactionSizes.poll());
    	} else if (alternativeSampler != null) {
    		return(alternativeSampler.getNextTransactionSize());
    	} else {
    		throw new Exception("Transaction file has less transactions than specified in configuration file. Alternative Sampler not specified.");
    	}
    }


    /**
     * See parent. Use Uniform distribution.
     */
    @Override
    public int getRandomNum(int min, int max) {
        return(alternativeSampler.getRandomNum(min, max));
    }
    

}
