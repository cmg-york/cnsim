package ca.yorku.cmg.cnsim.engine;

public class StandardNetworkSampler extends AbstractNetworkSampler {

    public StandardNetworkSampler(Sampler s) {
    	this.sampler = s;
    }
	
	
    /**
     * See parent. Use Normal distribution.
     */
    @Override
    public float getNextConnectionThroughput() {
        return (sampler.getGaussian(netThroughputMean, netThroughputSD, random));
    }


}
