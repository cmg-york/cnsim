package cmg.cnsim.engine;

public class NetworkSamplerFactory {
	public AbstractNetworkSampler getNetworkSampler(Sampler outerSampler, Long seed) {
		AbstractNetworkSampler netSampler;
		netSampler = new StandardNetworkSampler(outerSampler);
		if (seed != null) {
			netSampler.setSeed(seed);
		}
		return(netSampler);
    }
}
