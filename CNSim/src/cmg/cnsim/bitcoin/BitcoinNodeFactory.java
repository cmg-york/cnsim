package cmg.cnsim.bitcoin;

import cmg.cnsim.engine.Config;
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
	private boolean maliciousNodeCreated = false;
	private final boolean createMaliciousNode;
	private final double maliciousHashPower;

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
		this.createMaliciousNode = Config.getPropertyBoolean("node.createMaliciousNode");
		//check if node.maliciousHashPower is set in the config file
		if (Config.hasProperty("node.maliciousHashPower")) {
			this.maliciousHashPower = Config.getPropertyDouble("node.maliciousHashPower");
		} else {
			this.maliciousHashPower = 0.0;
		}

	}

	/**
	 * Create a new node based on the factory.
	 */
	@Override
	public INode createNewNode() throws Exception {
		// First, create a BitcoinNode instance without a behavior strategy
		BitcoinNode node = new BitcoinNode(sim);

		// Set node properties from the sampler
		node.setHashPower(sampler.getNextNodeHashPower());
		node.setElectricPower(sampler.getNextNodeElectricPower());
		node.setElectricityCost(sampler.getNextNodeElectricityCost());
		node.setSimulation(sim);

		// Determine and set the appropriate behavior strategy
		NodeBehaviorStrategy strategy;
		if (this.createMaliciousNode && !this.maliciousNodeCreated) {
			strategy = new MaliciousNodeBehavior(node);
			if (this.maliciousHashPower != 0.0) {node.setHashPower((float) this.maliciousHashPower);}
			this.maliciousNodeCreated = true; // Mark that the malicious node has been created
		} else {
			strategy = new HonestNodeBehavior(node);
		}

		// Set the behavior strategy on the node
		node.setBehaviorStrategy(strategy);

		// Return the fully initialized node
		return node;
	}
}
