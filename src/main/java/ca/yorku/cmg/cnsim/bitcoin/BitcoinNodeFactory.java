package ca.yorku.cmg.cnsim.bitcoin;

import ca.yorku.cmg.cnsim.engine.Config;
import ca.yorku.cmg.cnsim.engine.Simulation;
import ca.yorku.cmg.cnsim.engine.node.AbstractNodeFactory;
import ca.yorku.cmg.cnsim.engine.node.INode;
import ca.yorku.cmg.cnsim.engine.node.NodeSet;

/**
 * A factory for various kinds of blockchain nodes.
 *
 * @author Sotirios Liaskos for the Conceptual Modeling Group @ York University`
 *
 */
public class BitcoinNodeFactory extends AbstractNodeFactory {

	String defaultNodeType;
	NodeSet refNs;

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

	public BitcoinNodeFactory(String defaultNodeType, Simulation sim, NodeSet refNs){
		this.defaultNodeType = defaultNodeType;
		this.sim = sim;
		this.sampler = sim.getSampler();
		this.refNs = refNs;
	}
	
	
	/**
	 * Create a new node based on the factory.
	 */
	@Override
	public INode createNewNode() throws Exception {
		// First, create a BitcoinNode instance without a behavior strategy
		BitcoinNode node = new BitcoinNode(sim);
        
		// Set node properties from the sampler
		node.setHashPower(sampler.getNodeSampler().getNextNodeHashPower());
		node.setElectricPower(sampler.getNodeSampler().getNextNodeElectricPower());
		node.setElectricityCost(sampler.getNodeSampler().getNextNodeElectricityCost());
		node.setSimulation(sim);
		
		// Determine and set the appropriate behavior strategy and update hashpower
		float nodeHashPower;
		NodeBehaviorStrategy strategy;
		if (this.defaultNodeType.equals("Malicious")) {
			strategy = new MaliciousNodeBehavior(node);
			boolean powerByRatio = Config.getPropertyBoolean("node.maliciousPowerByRatio");
			if (powerByRatio) {
				if (this.refNs == null) {
					throw new Exception("Malicious power by ratio requested but reference honest nodeset not provided.");
				} else {
					float powerRatio = Config.getPropertyFloat("node.maliciousRatio");
					nodeHashPower = powerRatio/(1-powerRatio) * refNs.getTotalHonestHP();
				}
			} else { //absolute power
				nodeHashPower = Config.getPropertyFloat("node.maliciousHashPower");
			}
			
			if (nodeHashPower == -1) {
				throw new Exception("Error creating malicious node.");
			} else {
				node.setHashPower((float) nodeHashPower);
			}
			
			//Set target transaction
			int targetTx[] = Config.parseStringToIntArray(Config.getPropertyString("workload.sampleTransaction"));
			((MaliciousNodeBehavior) strategy).setTargetTransaction(targetTx[0]);
			
		} else {
			strategy = new HonestNodeBehavior(node);
		}
		node.setBehaviorStrategy(strategy);
		return node;
	}
}
