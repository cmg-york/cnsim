package cmg.cnsim.engine.node;

import cmg.cnsim.engine.AbstractSampler;
import cmg.cnsim.engine.Sampler;
import cmg.cnsim.engine.Simulation;

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
