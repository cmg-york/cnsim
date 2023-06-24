package cmg.cnsim.engine.node;

import cmg.cnsim.engine.AbstractSampler;
import cmg.cnsim.engine.Simulation;

public abstract class AbstractNodeFactory {
	
	protected Simulation sim;
	protected AbstractSampler sampler;
	
	public Simulation getSim() {
		return sim;
	}

	public void setSim(Simulation sim) {
		this.sim = sim;
	}

	public AbstractSampler getSampler() {
		return sampler;
	}

	public void setSampler(AbstractSampler sampler) {
		this.sampler = sampler;
	}
	
	public abstract INode createNewNode() throws Exception;  
}
