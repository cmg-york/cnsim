package ca.yorku.cmg.cnsim.engine.network;

import ca.yorku.cmg.cnsim.engine.AbstractSampler;
import ca.yorku.cmg.cnsim.engine.Config;
import ca.yorku.cmg.cnsim.engine.Sampler;
import ca.yorku.cmg.cnsim.engine.node.NodeSet;


/**
 * 
 * A network with random connections between nodes. For each pair of nodes a throughput number signifies 
 * the minimum amount of time it takes for a bit to travel from one node to the other (ignoring routing).
 * This can be a result of application of an all-possible-shortest-paths algorithms to the true structure and 
 * connection throughput of the network. Thus, the `RandomEndToEndNetwork is an abstraction that is 
 * agnostic to actual structure and individual link throughputs of the network.     
 * @author Sotirios Liaskos for the Conceptual Modeling Group @ York University
 * 
 */
public class RandomEndToEndNetwork extends AbstractNetwork {
	private Sampler sampler;
	
    /**
     * Create a random network on the basis of Sampler s, using NodeSet ns. Will automatically create the Random network.
     * @param ns The NodeSet for the network
     * @param sampler The sampler to use for random assignment of throughputs.
     * @throws Exception 
     */
    public RandomEndToEndNetwork(NodeSet ns, Sampler sampler) throws Exception{
        super(ns);
        this.sampler = sampler;
        CreateRandomNetwork();
    }

    
    /**
     * Create an empty network object. For testing purposes only!
     */
    public RandomEndToEndNetwork(){
    }
    
    
	/**
	 * Create the random network by adding to every pair of node a sampled throughput. 
	 * @author Sotirios Liaskos
	 */
	private void CreateRandomNetwork(){
		for (int i=1; i <= Config.getPropertyInt("net.numOfNodes"); i++) {
			for (int j=1; j <= Config.getPropertyInt("net.numOfNodes"); j++) {
	            if(i!=j && Net[i][j] == 0)
	            {
	            	//network throughput refers to how much data can be transferred from source to destination within a given time frame
	            	float throughPut = (float) sampler.getNetworkSampler().getNextConnectionThroughput();
	            	super.setThroughput(i, j, throughPut);
	            	super.setThroughput(j, i, throughPut);
	                //Net[i][j] =(float) sampler.getNetworkSampler().getNextConnectionThroughput();  
	                //Net[j][i] = Net[i][j];
	            }
	        }
	    }
	}

	// #TODO
	/**
	 * @deprecated
	 * Create an overload constructor that takes in matrix and populates the networl
	 * each cell is throughput (check what the units are too
	 * think about exceptiions, how matrices are going to be represented, comma seperatd values?
	 * allow 0, prohibit negative values, file missing, etc
	 *
	 * param: filename
	 *
	 *
	 *
	 *
	 * fid a way to update net.nodes in memmory from config file
	 * think about exceptions too(size of files differs from "config.txt")
	 *
	 *
	 *
	 */
	
}
