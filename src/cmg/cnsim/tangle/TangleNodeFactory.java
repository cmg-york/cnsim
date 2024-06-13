package cmg.cnsim.tangle;

import cmg.cnsim.engine.AbstractSampler;
import cmg.cnsim.engine.Simulation;
import cmg.cnsim.engine.node.AbstractNodeFactory;
import cmg.cnsim.engine.node.INode;

/**
 * 
 * A class of objects that create {@link TangleNode} objects, based on a sampler contained in a {@link Simulation} object.
 * 
 * @author Sotirios Liaskos for the Conceptual Modeling Group @ York University
 */
public class TangleNodeFactory extends AbstractNodeFactory {

	String defaultNodeType;

	/**
	 * Construct a new {@link TangleNode} factory based on a {@link Simulation} object and its contained {@link AbstractSampler}.
	 * Node type supported is only "Honest" 
	 * @param defaultNodeType
	 * @param sim
	 */
	public TangleNodeFactory(String defaultNodeType, Simulation sim){
		this.defaultNodeType = defaultNodeType;
		this.sim = sim;
		this.sampler = sim.getSampler();
	}
	
	@Override
	public INode createNewNode() throws Exception {
		INode o;
		if (defaultNodeType.equalsIgnoreCase("Honest")) {
			o = new TangleNode(sim);
	        o.setHashPower (sampler.getNextNodeHashPower());
	        o.setElectricPower(sampler.getNextNodeElectricPower());
	        o.setElectricityCost(sampler.getNextNodeElectricityCost());
	        o.setBehavior(INode.BehaviorType.HONEST);
	        o.setSimulation(sim);
			return (o);
		} else throw new Exception("Node type " + defaultNodeType + " not found."); 
	}


}
