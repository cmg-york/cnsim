package ca.yorku.cmg.cnsim.engine;


public class StandardTransactionSampler extends AbstractTransactionSampler {
	    
	

    public StandardTransactionSampler(Sampler s) {
    	this.sampler = s;
    }
	
    
    public StandardTransactionSampler(Sampler s, long seed, boolean updateFlag, int simID) {
    	super.random.setSeed(seed + (updateFlag ? simID :0));
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
    	long result; 
    	long minSize = 10;
    	
    	int maxTries = 100;
    	int tries = 0;
    	
    	
    	do {
    		result = (long) sampler.getGaussian(txSizeMean, txSizeSD, random);
    		tries++;
    	} while ((result < minSize) && (tries < maxTries));
    	
    	if (tries == maxTries) {
    		System.err.println("Failed to generate appropriate transaction size after " + tries + " tries. Please check workload.txSizeMean and workload.txSizeSD.");
    		System.exit(-1);
    	}
    	
        return(result);
    }
    
    /**
     * See parent. Use Uniform distribution.
     */
    @Override
    public int getRandomNum(int min, int max) {
        return(random.nextInt((max - min) + 1) + min);
    }
	


}
