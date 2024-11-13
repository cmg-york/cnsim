package cmg.cnsim.engine;

import java.util.Random;

/**
 * 
 * @author Sotirios Liaskos for the Enterprise Systems Group @ York University
 */
public abstract class AbstractTransactionSampler implements ISowable {
	
	protected Sampler sampler;
	protected Random random;
	protected long randomSeed;
	
	protected float txArrivalIntervalRate; 
    protected float txSizeMean;
    protected float txSizeSD;
    protected float txValueMean;
    protected float txValueSD;
        
    AbstractTransactionSampler(){
   		this.random = new Random();
   		random.setSeed(randomSeed);
   		LoadConfig();
    }
    
    /**
     * Constructs a new AbstractTransactionSampler object with the given parameters. 
     * Parameters define means and standard deviations of the distribution to be used.
     *
     * @param txArrivalIntervalRate The arrival interval rate of transactions (tx/sec).
     * @param txSizeMean            The mean transaction size (bytes).
     * @param txSizeSD              The standard deviation of transaction size (bytes).
     * @param txValueMean           The mean transaction value (local tokens).
     * @param txValueSD             The standard deviation of transaction value (local tokens).
     * @throws ArithmeticException if any of the provided values are less than 0.
     */
    AbstractTransactionSampler(float txArrivalIntervalRate, float txSizeMean, float txSizeSD,
            float txValueMean, float txValueSD) {
        this.txArrivalIntervalRate = txArrivalIntervalRate;
        this.txSizeMean = txSizeMean;
        if(txSizeSD < 0)
    		throw new ArithmeticException("Transaction Size Standard Deviation < 0");
        this.txSizeSD = txSizeSD;
        if(txValueMean < 0)
    		throw new ArithmeticException("Transaction Value Mean < 0");
        this.txValueMean = txValueMean;
        if(txValueSD < 0)
    		throw new ArithmeticException("Transaction Value Standard Deviation < 0");
        this.txValueSD = txValueSD;
    }
  
        
	//
	//
	// Transactions
	//
	//
	/**
	 * @deprecated 
	 */
	public void _______________Transactions() {}

	
	/**
	 * Returns the transaction arrival rate (in the entire system, arriving in random nodes) in transactions per second (Tx/sec) 
	 * @return Transaction arrival rate in Tx/sec
	 */
	public float getTxArrivalIntervalRate() {
        return txArrivalIntervalRate;
    }

	/**
	 * Sets the transaction arrival rate (in the entire system, arriving in random nodes) in transactions per second (Tx/sec)
	 * @param txArrivalIntervalRate Transaction arrival rate in Tx/sec
	 */
	public void setTxArrivalIntervalRate(float txArrivalIntervalRate) {
    	if(txArrivalIntervalRate < 0)
    		throw new ArithmeticException("Transaction Arrival Interval Rate < 0");
        this.txArrivalIntervalRate = txArrivalIntervalRate;
    }
	
	
	/**
	 * Returns the mean transaction size in bytes 
	 * @return The mean transaction size in bytes
	 */
	public float getTxSizeMean() {
        return txSizeMean;
    }

	/**
	 * Sets the mean transaction size in bytes
	 * @param txSizeMean The mean transaction size in bytes  
	 */
	public void setTxSizeMean(float txSizeMean) {
    	if(txSizeMean < 0)
    		throw new ArithmeticException("Transaction size mean < 0");
        this.txSizeMean = txSizeMean;
    }

	/**
	 * Returns the standard deviation of transaction sizes in bytes
	 * @return The standard deviation in transaction sizes in bytes
	 */
	public float getTxSizeSD() {
        return txSizeSD;
    }

	/**
	 * Sets the standard deviation of transaction sizes in bytes
	 * @param txSizeSD The standard deviation in transaction sizes in bytes
	 */
	public void setTxSizeSD(float txSizeSD) {
    	if(txSizeSD < 0)
    		throw new ArithmeticException("Transaction Size Standard Deviation < 0");
        this.txSizeSD = txSizeSD;
    }

	/**
	 * Returns the mean transaction fee value.
	 *
	 * @return The mean transaction fee value (local tokens).
	 */
	public float getTxFeeValueMean() {
        return txValueMean;
    }
	
	/**
	 * Sets the mean transaction fee value.
	 *
	 * @param txValueMean The mean transaction fee value to be set (local tokens).
	 * @throws ArithmeticException if the provided value is less than 0.
	 */
	public void setTxFeeValueMean(float txValueMean) {
    	if(txValueMean < 0)
    		throw new ArithmeticException("Transaction Value Mean < 0");
        this.txValueMean = txValueMean;
    }
	
	/**
	 * Returns the standard deviation of transaction fee value.
	 *
	 * @return The standard deviation of transaction fee value (in local tokens).
	 */
	public float getTxFeeValueSD() {
        return txValueSD;
    }

	
	/**
	 * Sets the standard deviation of transaction fee value.
	 *
	 * @param txValueSD The standard deviation of transaction fee value to be set.
	 * @throws ArithmeticException if the provided value is less than 0.
	 */
	public void setTxFeeValueSD(float txValueSD) {
    	if(txValueSD < 0)
    		throw new ArithmeticException("Transaction Value Standard Deviation < 0");
        this.txValueSD = txValueSD;
    }
	
    public Sampler getSampler() {
		return sampler;
	}

	public void setSampler(Sampler sampler) {
		this.sampler = sampler;
	}

	public Random getRandom() {
		return random;
	}

	public void setRandom(Random random) {
		this.random = random;
	}
	
    public void setSeed(long seed) {
    	randomSeed = seed;
    	random.setSeed(seed);
    }

    /**
     * Load configuration using Config object.
     */
    public void LoadConfig() {
        this.setTxArrivalIntervalRate(Config.getPropertyFloat("workload.lambda")); //How often transactions arrive at the system.
        this.setTxSizeMean(Config.getPropertyFloat("workload.txSizeMean"));
        this.setTxSizeSD(Config.getPropertyFloat("workload.txSizeSD"));
        this.setTxFeeValueMean(Config.getPropertyFloat("workload.txFeeValueMean"));
        this.setTxFeeValueSD(Config.getPropertyFloat("workload.txFeeValueSD"));
    }
	
	//
	//
	//	Generators of Random Values
	//
	//
	//
	
	
	/**
	 * Returns the next sampled transaction interval in milliseconds.
	 * @return The next sampled transaction interval (msec).
	 * @throws Exception 
	 */
    public abstract float getNextTransactionArrivalInterval() throws Exception;

    /**
     * Get a sample transaction fee value.
     * @return Transaction fee value (local tokens).
     * @author Sotirios Liaskos
     * @throws Exception 
     */
    public abstract float getNextTransactionFeeValue() throws Exception;

	/**
	 * Returns a sample of a transaction size in bytes.
	 * @return The generated transaction size in bytes.
	 * @throws Exception 
	 */
    public abstract long getNextTransactionSize() throws Exception;

	/**
	 * Return a random number from min to max. (TODO: inclusive?)
	 * @param min The minimum. 
	 * @param max The maximum. 
	 * @return The random integer.
	 */
    public abstract int getRandomNum(int min, int max);

	
    
}
