package ca.yorku.cmg.cnsim.engine;

public class NetworkSamplerFactory {
	public AbstractNetworkSampler getNetworkSampler(Long seed, boolean seedFlag, Sampler outerSampler, Simulation sim) {
		AbstractNetworkSampler netSampler;
		netSampler = new StandardNetworkSampler(outerSampler);
		if (seed != null) {
			netSampler.setSeed(seed + (seedFlag ? sim.getSimID() : 0));
		}
		return(netSampler);
    }
}
