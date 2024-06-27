package cmg.cnsim.engine;


public class StandardTransactionSampler extends AbstractTransactionSampler {
	    
	

    public StandardTransactionSampler(Sampler s) {
    	this.sampler = s;
    }
	
    
    public StandardTransactionSampler(Sampler s, long seed) {
    	super.random.setSeed(seed);
    	this.sampler = s;
    }
	
	
    /**
     * See parent. Use Poisson distribution.
     */
    @Override
    public float getNextTransactionArrivalInterval() {
    	return (float) sampler.getPoissonInterval(txArrivalIntervalRate,random)*1000;
    }

	
    /**
     * See parent. Use Normal distribution.
     */
    @Override
    public float getNextTransactionFeeValue() {
        return(sampler.getGaussian(txValueMean, txValueSD, random));
    }

    /**
     * See parent. Use Normal distribution.
     */
    @Override
    public long getNextTransactionSize() {
        return(long) (sampler.getGaussian(txSizeMean, txSizeSD, random));
    }
    
    /**
     * See parent. Use Uniform distribution.
     */
    @Override
    public int getRandomNum(int min, int max) {
        return(random.nextInt((max - min) + 1) + min);
    }
	


}
