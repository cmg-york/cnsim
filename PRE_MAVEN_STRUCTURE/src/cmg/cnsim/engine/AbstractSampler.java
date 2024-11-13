package cmg.cnsim.engine;
/**
 * A Sampler is an object that supplies parameters to the simulator. Implementations decide how the parameters are sampled. 
 * E.g. a Standard Sampler offers parameters sampled from a Gaussian distribution 
 * and a Deterministic sampler, samples based on predifined intervals or data.    
 * 
 * @author Sotirios Liaskos for the Enterprise Systems Group @ York University
 * 
 */

public abstract class AbstractSampler  {

    protected float txArrivalIntervalRate; 
    protected float txSizeMean;
    protected float txSizeSD;
    protected float txValueMean;
    protected float txValueSD;
    protected float nodeHashPowerMean;
    protected float nodeHashPowerSD;
    protected float nodeElectricPowerMean;
    protected float nodeElectricPowerSD;
    protected float nodeElectricCostMean;
    protected float nodeElectricCostSD;
    protected float netThroughputMean;
    protected float netThroughputSD;    
    protected double currentDifficulty;
    protected long randomSeed;

    public AbstractSampler() {}

    
    /**
     * Constructs a new AbstractSampler object with the given parameters. Parameters define means and standard deviations of the distribution to be used.
     *
     * @param txArrivalIntervalRate The arrival interval rate of transactions (tx/sec).
     * @param txSizeMean            The mean transaction size (bytes).
     * @param txSizeSD              The standard deviation of transaction size (bytes).
     * @param txValueMean           The mean transaction value (local tokens).
     * @param txValueSD             The standard deviation of transaction value (local tokens).
     * @param nodeHashPowerMean     The mean hash power of a node (hashes/second).
     * @param nodeHashPowerSD       The standard deviation of node hash power (hashes/second).
     * @param nodeElectricPowerMean The mean electric power consumption of a node (Watts).
     * @param nodeElectricPowerSD   The standard deviation of node electric power consumption (Watts).
     * @param nodeElectricCostMean  The mean electric cost of a node (real currency per kWh).
     * @param nodeElectricCostSD    The standard deviation of node electric cost (real currency per kWh).
     * @param netThroughputMean     The mean network throughput (bps).
     * @param netThroughputSD       The standard deviation of network throughput (bps).
     * @param difficulty            The current difficulty level ([Search Space] / [Success Space].
     * @throws ArithmeticException if any of the provided values are less than 0.
     */
    public AbstractSampler(float txArrivalIntervalRate, float txSizeMean, float txSizeSD,
            float txValueMean, float txValueSD,
            float nodeHashPowerMean, float nodeHashPowerSD,
            float nodeElectricPowerMean, float nodeElectricPowerSD,
            float nodeElectricCostMean, float nodeElectricCostSD,
            float netThroughputMean, float netThroughputSD,
            double difficulty) {
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
        if(nodeHashPowerMean < 0)
    		throw new ArithmeticException("Node Hash Power Mean < 0");
        this.nodeHashPowerMean = nodeHashPowerMean;
        if(nodeHashPowerSD < 0)
    		throw new ArithmeticException("Node Hash Power Standard Deviation < 0");
        this.nodeHashPowerSD = nodeHashPowerSD;
        if(nodeElectricPowerMean < 0)
    		throw new ArithmeticException("Node Electric Power Mean < 0");
        this.nodeElectricPowerMean = nodeElectricPowerMean;
        if(nodeElectricPowerSD < 0)
    		throw new ArithmeticException("Node Electric Power Standard Deviation < 0");
        this.nodeElectricPowerSD = nodeElectricPowerSD;
        if(nodeElectricCostMean < 0)
    		throw new ArithmeticException("Node Electric Cost Mean < 0");
        this.nodeElectricCostMean = nodeElectricCostMean;
        if(nodeElectricCostSD < 0)
    		throw new ArithmeticException("Node Electric Cost Standard Deviation < 0");
        this.nodeElectricCostSD = nodeElectricCostSD;
        if(netThroughputMean < 0)
    		throw new ArithmeticException("Network Throughput Mean < 0");
        this.netThroughputMean = netThroughputMean;
        if(netThroughputSD < 0)
    		throw new ArithmeticException("Network Throughput Standard Deviation < 0");
        this.netThroughputSD = netThroughputSD;
        
        this.currentDifficulty = difficulty;
    }
  
    /**
     * Sets the seed for the random number generator
     * @param seed The seed value for the random number genarator.
     * @author Sotirios Liaskos
     */
    public abstract void setSeed(long seed);
    
	public double getCurrentDifficulty() {
        return currentDifficulty;
    }
    
	/**
	 * Sets the difficulty parameter for PoW.
	 * @param currentDifficulty The difficulty parameter ([Search Space] / [Success Space])
	 * @author Sotirios Liaskos
	 */
	public void setCurrentDifficulty(double currentDifficulty) {
        this.currentDifficulty = currentDifficulty;
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

	/**
	 * Gets the mean hash power of nodes in GH/s
	 * @return the mean hash power of nodes in GH/s
	 */
	public float getNodeHashPowerMean() {
        return nodeHashPowerMean;
    }

	/**
	 * Sets the mean node hash power.
	 *
	 * @param nodeHashPowerMean The mean node hash power to be set.
	 * @throws ArithmeticException if the provided value is less than 0.
	 */
	public void setNodeHashPowerMean(float nodeHashPowerMean) {
    	if(nodeHashPowerMean < 0)
    		throw new ArithmeticException("Node Hash Power Mean < 0");
        this.nodeHashPowerMean = nodeHashPowerMean;
    }

	/**
	 * Returns the standard deviation of node hash power.
	 *
	 * @return The standard deviation of node hash power.
	 */
	public float getNodeHashPowerSD() {
        return nodeHashPowerSD;
    }

	/**
	 * Sets the standard deviation of node hash power.
	 *
	 * @param nodeHashPowerSD The standard deviation of node hash power to be set.
	 * @throws ArithmeticException if the provided value is less than 0.
	 */
	public void setNodeHashPowerSD(float nodeHashPowerSD) {
    	if(nodeHashPowerSD < 0)
    		throw new ArithmeticException("Node Hash Power Standard Deviation < 0");
        this.nodeHashPowerSD = nodeHashPowerSD;
    }

	
	/**
	 * Returns the mean node electric power.
	 *
	 * @return The mean node electric power.
	 */
	public float getNodeElectricPowerMean() {
        return nodeElectricPowerMean;
    }

	
	/**
	 * Sets the mean node electric power.
	 *
	 * @param nodeElectricPowerMean The mean node electric power to be set.
	 * @throws ArithmeticException if the provided value is less than 0.
	 */
	public void setNodeElectricPowerMean(float nodeElectricPowerMean) {
    	if(nodeElectricPowerMean < 0)
    		throw new ArithmeticException("Node Electric Power Mean < 0");
        this.nodeElectricPowerMean = nodeElectricPowerMean;
    }
	
	/**
	 * Returns the standard deviation of node electric power.
	 *
	 * @return The standard deviation of node electric power.
	 */
	public float getNodeElectricPowerSD() {
        return nodeElectricPowerSD;
    }

	/**
	 * Sets the standard deviation of node electric power.
	 *
	 * @param nodeElectricPowerSD The standard deviation of node electric power to be set.
	 * @throws ArithmeticException if the provided value is less than 0.
	 */
	public void setNodeElectricPowerSD(float nodeElectricPowerSD) {
    	if(nodeElectricPowerSD < 0)
    		throw new ArithmeticException("Node Electric Power Standard Deviation < 0");
    	this.nodeElectricPowerSD = nodeElectricPowerSD; 
    }

	/**
	 * Returns the mean node electric cost.
	 *
	 * @return The mean node electric cost.
	 */
	public float getNodeElectricCostMean() {
        return nodeElectricCostMean;
    }
		
	/**
	 * Sets the mean node electric cost.
	 *
	 * @param nodeElectricCostMean The mean node electric cost to be set.
	 * @throws ArithmeticException if the provided value is less than 0.
	 */
	public void setNodeElectricCostMean(float nodeElectricCostMean) {
    	if(nodeElectricCostMean < 0)
    		throw new ArithmeticException("Node Electric Cost Mean < 0");
        this.nodeElectricCostMean = nodeElectricCostMean;
    }

	/**
	 * Returns the standard deviation of node electric cost.
	 *
	 * @return The standard deviation of node electric cost.
	 */
	public float getNodeElectricCostSD() {
        return nodeElectricCostSD;
    }

	/**
	 * Sets the standard deviation of node electric cost.
	 *
	 * @param nodeElectricCostSD The standard deviation of node electric cost to be set.
	 * @throws ArithmeticException if the provided value is less than 0.
	 */
	public void setNodeElectricCostSD(float nodeElectricCostSD) {
    	if(nodeElectricCostSD < 0)
    		throw new ArithmeticException("Node Electric Cost Standard Deviation < 0");
        this.nodeElectricCostSD = nodeElectricCostSD;
    }

	
	//
	//
	// Transactions
	//
	//
	/**
	 * @deprecated 
	 */
	public void _______________Network() {}

	
	
	/**
	 * Returns the mean throughput of an arbitrary node to node connection in bits per second (bps)
	 * @return The mean throughput of an arbitrary node to node connection in bits per second (bps)
	 */
	public float getNetThroughputMean() {
        return netThroughputMean;
    }

	/**
	 * Sets the mean throughput of an arbitrary node to node connection in bits per second (bps)
	 * @param netThroughputMean The mean throughput of an arbitrary node to node connection in bits per second (bps)
	 */
	public void setNetThroughputMean(float netThroughputMean) {
    	if(netThroughputMean < 0)
    		throw new ArithmeticException("Network Throughput Mean < 0");
        this.netThroughputMean = netThroughputMean;
    }

	/**
	 * Returns the standard deviation throughput of an arbitrary node to node connection in bits per second (bps)
	 * @return The standard deviation throughput of an arbitrary node to node connection in bits per second (bps)
	 */
	public float getNetThroughputSD() {
        return netThroughputSD;
    }

	/**
	 * Sets the standard deviation throughput of an arbitrary node to node connection in bits per second (bps)
	 * @param netThroughputSD The standard deviation throughput of an arbitrary node to node connection in bits per second (bps)
	 */
	public void setNetThroughputSD(float netThroughputSD) {
    	if(netThroughputSD < 0)
    		throw new ArithmeticException("Network Throughput Standard Deviation < 0");
        this.netThroughputSD = netThroughputSD;
    }

	//
	//
	//	Generators of Random Values
	//
	//
	//
	/**
	 * @deprecated 
	 */
	public void _______________Sampling() {}

	
	
	/**
	 * Returns the next sampled transaction interval in milliseconds.
	 * @return The next sampled transaction interval (msec).
	 */
    public abstract float getNextTransactionArrivalInterval();

    /**
     * Get a sample of a time interval it takes for a node with hash power `hashPower` to mine a block. 
     * @param hashPower The hash power of the node (Giga-Hashes/sec)
     * @return A time interval in milliseconds.
     * @author Sotirios Liaskos
     */
    public abstract long getNextMiningInterval(double hashPower);

    /**
     * Get a sample transaction fee value.
     * @return Transaction fee value (local tokens).
     * @author Sotirios Liaskos
     */
    public abstract float getNextTransactionFeeValue();

	/**
	 * Returns a sample of a transaction size in bytes.
	 * @return The generated transaction size in bytes.
	 */
    public abstract long getNextTransactionSize();

    /**
     * Get a sample of an nodes electric power (Watts).
     * @return A node electric power in Watts.
     * @author Sotirios Liaskos
     */
    public abstract float getNextNodeElectricPower();

    /**
     * Get a sample of a node hash power (hashes/second).
     * @return A hashpower sample (hashes/second).
     * @author Sotirios Liaskos
     */
    public abstract float getNextNodeHashPower();

    /**
     * Get a sample of electricity cost (real currency / kWh).
     * @return A sample of electricity cost (real currency / kWh)
     * * @author Sotirios Liaskos
     */
    public abstract float getNextNodeElectricityCost();
    
	/**
	 * Returns a random (or otherwise sampled) node ID (TODO: from... to...)
	 * @param nNodes The number of nodes to select from.
	 * @return The ID of a selected node.
	 */
    public abstract int getNextRandomNode(int nNodes);

	/**
	 * Returns a sample of a throughput based on set mean and SD  
	 * @return The throughput value in bits per second (bps)
	 */
    public abstract float getNextConnectionThroughput();

	/**
	 * Return a random number from min to max. (TODO: inclusive?)
	 * @param min The minimum. 
	 * @param max The maximum. 
	 * @return The random integer.
	 */
    public abstract int getRandomNum(int min, int max);

    
	/**
	 * Load parameters from a configuration source.
	 * @author Sotirios Liaskos
	 */
	public abstract void LoadConfig();
	
}
