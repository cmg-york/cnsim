package esg.bcsim.tangle;

import cmg.cnsim.engine.Simulation;
import cmg.cnsim.engine.node.AbstractNodeFactory;
import cmg.cnsim.engine.node.INode;

public class TangleNodeFactory extends AbstractNodeFactory {

	String defaultNodeType;


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
