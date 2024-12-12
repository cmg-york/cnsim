package ca.yorku.cmg.cnsim.engine;

import ca.yorku.cmg.cnsim.engine.transaction.Transaction;

public class StandardTransactionSampler extends AbstractTransactionSampler {
	    
	
	private int simID;
	
	private long seedSwitchTx;
	private long initialSeed;
	private long currentSeed;
	
	private boolean seedUpdateEnabled = false;

	
    public void updateSeed() {
    	if ((seedUpdateEnabled) && (seedSwitchTx < Transaction.currID - 1)) {
    		currentSeed = this.initialSeed + this.simID;
    		super.random.setSeed(currentSeed);
    		seedUpdateEnabled = false;
    	}
    }

    public long getCurrentSeed() {
    	return this.currentSeed;
    }
    
    
	@Override
	public long getSeedChangeTx() {
		return (this.seedSwitchTx);
	}
    
	@Override
	public boolean seedUpdateEnabled() {
		return (this.seedUpdateEnabled);
	}

	
	
    public StandardTransactionSampler(Sampler s) {
    	this.sampler = s;
    	LoadConfig();
    }
	    
    public StandardTransactionSampler(Sampler s, int simID) {
    	this(s);
    	this.simID = simID;
    }
	
	
    /**
     * See parent. Use Poisson distribution.
     */
	@Override
	public float getNextTransactionArrivalInterval() throws Exception {
    	updateSeed();
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
        return(sampler.getTransactionSampler().getRandom().nextInt((max - min) + 1) + min);
    }

    @Override
    public void LoadConfig() {
    	super.LoadConfig();
    	this.seedUpdateEnabled = (Config.hasProperty("workload.sampler.seed.updateSeed") ? Config.getPropertyBoolean("workload.sampler.seed.updateSeed") : false);
    	this.seedSwitchTx = (Config.hasProperty("workload.sampler.seed.updateTransaction") ? Config.getPropertyLong("workload.sampler.seed.updateTransaction") : 0);
    	this.currentSeed = (Config.hasProperty("workload.sampler.seed") ? Config.getPropertyLong("workload.sampler.seed") : 0);
    	this.initialSeed = this.currentSeed;
    }

    
    //For testing
    public void nailConfig(long initSeed, boolean seedUpdateEnabled, long seedSwitchTx) {
    	this.seedUpdateEnabled = seedUpdateEnabled;
    	this.seedSwitchTx = seedSwitchTx;
    	this.currentSeed = initSeed;
    	this.initialSeed = this.currentSeed;
    }

}
