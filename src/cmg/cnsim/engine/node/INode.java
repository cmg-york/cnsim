package cmg.cnsim.engine.node;

import cmg.cnsim.engine.IStructure;
import cmg.cnsim.engine.Simulation;
import cmg.cnsim.engine.transaction.ITxContainer;
import cmg.cnsim.engine.transaction.Transaction;

/**
 * Represents a network node.
 */
public interface INode {
	
    /**
     * Enumeration for the behavior type of node.
     */
	public enum BehaviorType {HONEST, MALICIOUS, SELFISH}

	// N O D E     P R O P E R T I E S    A N D     D A T A
	//
	//
	
	
    /**
     * Returns the ID of the node.
     *
     * @return The ID of the node.
     */
	public int getID();
	
    /**
     * Returns the structure (blockchain, DAG, etc.) associated with the node.
     *
     * @return The structure associated with the node.
     */
	public IStructure getStructure(); 
	
    /**
     * TODO: Conflicts with Sampler. Check to see what the hashpower is.  
     * Set the total hash power of the node in Gigahashes (billions of hashes) per second.
     * @param hashpower The hash power in GH/second.
     */
    public void setHashPower(float hashpower);

    /**
     * Returns the total hashpower of the node in Gigahashes (billions of hashes) per second.
     * @return The hash power in GH/second.
     */
    public float getHashPower();

    /**
     * Sets the cost (in real world currency) of electricity in tokens/kWh (Kilowatt Hours)
     * @param electricityCost The cost of electricity in tokens/kWh.
     */
    public void setElectricityCost(float electricityCost);
    
    /**
     * Get the cost (in conventional currency "tokens") of electricity in tokens/kWh (Kilowatt Hours)
     * @return The cost of electricity in tokens/kWh.
     */
    public float getElectricityCost();
    
    /**
     * Returns the cost in conventional currency tokens in tokens/GH. Calculation is as follows: 
     * [ [electrictiyCost ($/kWh) * electricPower (W) / 1000 (W/kW)] /  [3600 (s/h) * hashPower (GH/s)]] = 
     * [ [electrictiyCost ($/kWh) * electricPowerinkW (kW)] /  [3600 (s/h) * hashPower (GH/s)]] =
     * [ [electrictiyCostPerHour ($/h)] /  [hashesPerHour (GH/h)]] =
     * Tokens per billions of hashes ($/GH)
     * @return Cost in conventional currency tokens in tokens/GH.
     */
    public double getCostPerGH();
    
    
    /**
     * Get the electric power of the node in Watts.
     * @return The electric power of the node in Watts.
     */
    public float getElectricPower();
    

    /**
     * Get the electric power of the node in Watts.
     * @param power The electric power of the node in Watts.
     */
    public void setElectricPower(float power);
    
    
    /**
     * Returns the average connectedness of the node; i.e., the average throughput with the other nodes.
     *
     * @return The average connectedness of the node.
     */
	public float getAverageConnectedness();
    
	
    /**
     * Returns the total PoW cycles the node has expended
     * TODO: Unit of measure?
     * @return The total PoW cycles of the node has expended.
     */
    public double getTotalCycles();

    /**
     * Sets the Simulation object for the node.
     *
     * @param sim The Simulation object.
     */
    public void setSimulation(Simulation sim);
    
    
    /**
     * Sets a behavior type of the node (HONEST, MALICIOUS etc.) 
     *
     * @param h The behavior type.
     */
    public void setBehavior(BehaviorType h);
    
    
    /**
     * Returns the behavior type of the node.
     *
     * @return The behavior type of the node.
     */
    public BehaviorType getBehavior();
    
    
    
    // R E P O R T I N G    R E S P O N S I B I L I T I E S
    //
    //
    //
    
    /**
     * Generates a time advancement report.
     * The method is called from the simulator (or other) environment in for continues logging of events. 
     * The method is called every time a new even is processed.
     * The content and format of the report may vary depending on the implementation.
     * No parameters are required for this method.
     */
	public void timeAdvancementReport();
	
	/**
	 * Generates a time advancement report.
     * The method is called from the simulator (or other) environment in for periodic logging on events status etc.
	 * The content and format of the report may vary depending on the implementation.
	 * This method does not take any parameters.
	 */
	public void periodicReport();
	
	/**
	 * To be called when the node object is not closing though end of simulation or other termination condition. 
	 *
	 * @param n The {@linkplain INode} implementing object to close.
	 */
	public void close(INode n);
    
	
	
    
    // B E H A V I O R S   /   E V E N T   R E S P O N S E S 
	//
	
	
	/**
	* Event: Node receives a client transaction.
	*
	* @param t The client transaction received by the node.
	* @param time The timestamp of the event.
	*/
    public void event_NodeReceivesClientTransaction(Transaction t, long time);

    /**
     * Event: Node receives a propagated transaction.
     *
     * @param trans The propagated transaction received by the node.
     * @param time The timestamp of the event.
     */
	public void event_NodeReceivesPropagatedTransaction(Transaction trans, long time);
    
	/**
	 * Event: Node receives a propagated container.
	 *
	 * @param t The propagated container received by the node.
	 */
	public void event_NodeReceivesPropagatedContainer(ITxContainer t);
    
	/**
	 * Event: Node completes validation of a container.
	 *
	 * @param t The container for which validation is completed.
	 * @param time The timestamp of the event.
	 */
    public void event_NodeCompletesValidation(ITxContainer t, long time);

}
