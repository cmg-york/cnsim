package cmg.cnsim.engine;


public abstract class AbstractNodeSampler implements IMultiSowable {
    
	protected Sampler sampler;
	
	protected float nodeHashPowerMean;
    protected float nodeHashPowerSD;
    protected float nodeElectricPowerMean;
    protected float nodeElectricPowerSD;
    protected float nodeElectricCostMean;
    protected float nodeElectricCostSD;
    protected double currentDifficulty;
    
    
    public AbstractNodeSampler() {
   		super();
 		LoadConfig();
    }
    
    
    /**
     * Constructs a new AbstractSampler object with the given parameters. Parameters define means and standard deviations of the distribution to be used.
     *
     * @param nodeHashPowerMean     The mean hash power of a node (hashes/second).
     * @param nodeHashPowerSD       The standard deviation of node hash power (hashes/second).
     * @param nodeElectricPowerMean The mean electric power consumption of a node (Watts).
     * @param nodeElectricPowerSD   The standard deviation of node electric power consumption (Watts).
     * @param nodeElectricCostMean  The mean electric cost of a node (real currency per kWh).
     * @param nodeElectricCostSD    The standard deviation of node electric cost (real currency per kWh).
     * @param difficulty            The current difficulty level ([Search Space] / [Success Space].
     * @throws ArithmeticException if any of the provided values are less than 0.
     */
    public AbstractNodeSampler(
            float nodeHashPowerMean, float nodeHashPowerSD,
            float nodeElectricPowerMean, float nodeElectricPowerSD,
            float nodeElectricCostMean, float nodeElectricCostSD,
            double difficulty,
            Sampler sampler) {
    	super();
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
       
        this.currentDifficulty = difficulty;
        
        this.sampler  = sampler;
    }
    
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
	
	
	public Sampler getSampler() {
		return sampler;
	}

	public void setSampler(Sampler sampler) {
		this.sampler = sampler;
	}

	
	public abstract void updateSeed();
    
    /**
     * Load configuration using Config object.
     */
    public void LoadConfig() {
        this.setNodeHashPowerMean(Config.getPropertyFloat("pow.hashPowerMean"));
        this.setNodeHashPowerSD(Config.getPropertyFloat("pow.hashPowerSD"));
        this.setNodeElectricPowerMean(Config.getPropertyFloat("node.electricPowerMean"));
        this.setNodeElectricPowerSD(Config.getPropertyFloat("node.electricPowerSD"));
        this.setNodeElectricCostMean(Config.getPropertyFloat("node.electricCostMean"));
        this.setNodeElectricCostSD(Config.getPropertyFloat("node.electricCostSD"));
        this.setCurrentDifficulty(Config.getPropertyDouble("pow.difficulty"));
    }
    
	
    /**
     * Get a sample of a time interval it takes for a node with hash power `hashPower` to mine a block. 
     * @param hashPower The hash power of the node (Giga-Hashes/sec)
     * @return A time interval in milliseconds.
     * @author Sotirios Liaskos
     */
    public abstract long getNextMiningInterval(double hashPower);
	
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

}
