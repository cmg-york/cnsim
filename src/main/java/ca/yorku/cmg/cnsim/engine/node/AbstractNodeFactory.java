package ca.yorku.cmg.cnsim.engine.node;

import ca.yorku.cmg.cnsim.engine.Sampler;
import ca.yorku.cmg.cnsim.engine.Simulation;

public abstract class AbstractNodeFactory {
	
	protected Simulation sim;
	protected Sampler sampler;
	
	public Simulation getSim() {
		return sim;
	}

	public void setSim(Simulation sim) {
		this.sim = sim;
	}

	public Sampler getSampler() {
		return sampler;
	}

	public void setSampler(Sampler sampler) {
		this.sampler = sampler;
	}
	
	public abstract INode createNewNode() throws Exception;  
}
