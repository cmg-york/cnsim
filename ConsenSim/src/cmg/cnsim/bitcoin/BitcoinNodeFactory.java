package cmg.cnsim.bitcoin;

import cmg.cnsim.engine.Simulation;
import cmg.cnsim.engine.node.AbstractNodeFactory;
import cmg.cnsim.engine.node.INode;

/**
 * A factory for various kinds of blockchain nodes. 
 * 
 * @author Sotirios Liaskos for the Conceptual Modeling Group @ York University`
 * 
 */
public class BitcoinNodeFactory extends AbstractNodeFactory {

	String defaultNodeType;


	/**
	 * Create a new factory of a specific type (e.g., Honest, Malicious, etc.) based on the sampler embedded in <tt>Simulator sim</tt>.  
	 * 
	 * @param defaultNodeType Is one of a list of strings identifying node type. Currently "Honest" is implemented.
	 * @param sim The simulator to which the node is attached, and from which sampling services are drawn.
	 */
	public BitcoinNodeFactory(String defaultNodeType, Simulation sim){
		this.defaultNodeType = defaultNodeType;
		this.sim = sim;
		this.sampler = sim.getSampler();
	}
	
	/**
	 * Crate a new node based on the factory.
	 */
	@Override
	public INode createNewNode() throws Exception {
		INode o;
		if (defaultNodeType.equalsIgnoreCase("Honest")) {
			o = new BitcoinNode(sim);
	        o.setHashPower (sampler.getNextNodeHashPower());
	        o.setElectricPower(sampler.getNextNodeElectricPower());
	        o.setElectricityCost(sampler.getNextNodeElectricityCost());
	        o.setBehavior(INode.BehaviorType.HONEST);
	        o.setSimulation(sim);
			return (o);
		} else throw new Exception("Node type " + defaultNodeType + " not supported."); 
	}

}
