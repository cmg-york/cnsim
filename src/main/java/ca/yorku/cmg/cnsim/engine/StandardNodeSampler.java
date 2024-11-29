package ca.yorku.cmg.cnsim.engine;

import java.util.Random;

public class StandardNodeSampler extends AbstractNodeSampler {
    SeedManager seedManager;
    Random random;
    int simID;

    public StandardNodeSampler(Sampler s) {
    	this.sampler = s;
    	random = new Random();
    }
	
    public StandardNodeSampler(Sampler s, long[] seedArray, boolean[] flagArray, int sim) {
    	this(s);
    	seedManager = new SeedManager(seedArray,flagArray,sim);
    	seedManager.updateSeed(random);
    }
	
	public Random getRandom() {
		return random;
	}

	public void setRandom(Random random) {
		this.random = random;
	}
    
	public SeedManager getSeedManager() {
		return seedManager;
	}

	public void updateSeed() {
		seedManager.updateSeed(random);
	}
    
    /**
     * TODO
     * Result in miliseconds.
     */
    @Override
    public long getNextMiningInterval(double hashPower) {

        if(hashPower < 0)
            throw new ArithmeticException("hashPower < 0");
        long inter = Math.round(getNextMiningIntervalMiliSeconds(hashPower, currentDifficulty));
        return((long) inter);
    }

    /**
     * TODO: Test this!
     * Get a random sample of the number of trials needed to successfully validate.
     * Sampling formula is: Math.log(1-Math.random())/Math.log1p(- 1.0/difficulty))
     * @param difficulty The difficulty under which the number of trials is generated. 
     * @return A number of trials needed for validation.
     * @author Sotirios Liaskos
     */
    public double getNextMiningIntervalTrials(double difficulty) {
    	if(difficulty < 0)
    		throw new ArithmeticException("difficulty < 0");
        //return ((double) (Math.log(1-Math.random())/Math.log1p(- 1.0/difficulty)));
        return ((double) (Math.log(1-random.nextDouble())/Math.log1p(- 1.0/difficulty)));
    }

    /**
     * TODO
     *
     * @param hashPower The hashpower for which the interval to be sampled.
     * @param difficulty The difficulty for which the interval to be sampled.
     * @return The interval in milliseconds (msec)
     * @author Sotirios Liaskos
     */
    private double getNextMiningIntervalSeconds(double hashPower, double difficulty) {
        if (hashPower < 0)
            throw new ArithmeticException("hashPower < 0");
        double tris = getNextMiningIntervalTrials(difficulty);
        //System.out.print("Trials: " + tris + ", ");
        return((double) tris / (hashPower*1e9));
    }
    /**
     * Get a random sample of the number of seconds needed to successfull validate given specific hash power and difficulty.
     * Works by first calling {@linkplain StandardNodeSampler#getNextMiningInterval(double)} to get the needed trials and then 
     * dividing by the node speed (hashpower Giga-trials/second) 
     * @param hashPower The node's hashpower in Giga trials per second (billion trials per second)
     * @param difficulty The difficulty under which validation is taking place [Search Space]/[Success Space]
     * @return A mining interval sample in milliseconds (msec).
     * @author Sotirios Liaskos
     */
    public double getNextMiningIntervalMiliSeconds(double hashPower, double difficulty) {
    	double secs = getNextMiningIntervalSeconds(hashPower,difficulty);
    	//System.out.print("Sec: " + secs + ", ");
    	return((double) secs*1000);
    }

    
    /**
     * See parent. Use Normal distribution.
     */
    @Override
    public float getNextNodeElectricPower() {
        return (sampler.getGaussian(nodeElectricPowerMean, nodeElectricPowerSD, getRandom()));
    }

    /**
     * See parent. Use Normal distribution.
     */
    @Override
    public float getNextNodeHashPower() {
        return (sampler.getGaussian(nodeHashPowerMean, nodeHashPowerSD, getRandom()));
    }

    /**
     * See parent. Use Normal distribution.
     */
    @Override
    public float getNextNodeElectricityCost() {
        return (sampler.getGaussian(nodeElectricCostMean, nodeElectricCostSD, getRandom()));
    }

    
    /**
     * See parent. Use Uniform distribution.
     */
    @Override
    public int getNextRandomNode(int nNodes) {
        return(getRandom().nextInt(nNodes));
    }
    
	
}
