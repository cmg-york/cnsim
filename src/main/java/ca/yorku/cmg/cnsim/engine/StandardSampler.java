package ca.yorku.cmg.cnsim.engine;

import java.util.Random;

/**
 * An implementation of the Abstract sampler based (mostly) on the Normal distribution. 
 * @author Sotirios Liaskos for the Enterprise Systems Group @ York University
 * 
 */
public class StandardSampler extends AbstractSampler {
    private Random random = new Random();


    
    public StandardSampler() {
    }
    
    /**
     * Constructs a new Sampler object with the given parameters. Parameters define means and standard deviations 
     * of the corresponding distribution (mostly Normal).
     *
     * @param txArrivalIntervalRate The arrival interval rate of transactions (tx/sec).
     * @param txSizeMean            The mean transaction size (bytes).
     * @param txSizeSD              The standard deviation of transaction size (bytes).
     * @param txValueMean           The mean transaction value (local tokens).
     * @param txValueSD             The standard deviation of transaction value (local tokens).
     * @param nodeHashPowerMean     The mean hash power of a node (hashes/second).
     * @param nodeHashPowerSD       The standard deviation of node hash power (hashes/second).
     * @param nodeElectricPowerMean The mean electric power consumption of a node (Watts).
     * @param nodeElectricPowerSD   The standard deviation of node electric power consumption (Watts).
     * @param nodeElectricCostMean  The mean electric cost of a node (real currency per kWh).
     * @param nodeElectricCostSD    The standard deviation of node electric cost (real currency per kWh).
     * @param netThroughputMean     The mean network throughput (bps).
     * @param netThroughputSD       The standard deviation of network throughput (bps).
     * @param difficulty            The current difficulty level ([Search Space] / [Success Space].
     */
    public StandardSampler(float txArrivalIntervalRate,
            float txSizeMean, float txSizeSD,
            float txValueMean, float txValueSD,
            float nodeHashPowerMean, float nodeHashPowerSD,
            float nodeElectricPowerMean, float nodeElectricPowerSD,
            float nodeElectricCostMean, float nodeElectricCostSD,
            float netThroughputMean, float netThroughputSD,
            double difficulty) {
        this.txArrivalIntervalRate = txArrivalIntervalRate;
        this.txSizeMean = txSizeMean;
        this.txSizeSD = txSizeSD;
        this.txValueMean = txValueMean;
        this.txValueSD = txValueSD;
        this.nodeHashPowerMean = nodeHashPowerMean;
        this.nodeHashPowerSD = nodeHashPowerSD;
        this.nodeElectricPowerMean = nodeElectricPowerMean;
        this.nodeElectricPowerSD = nodeElectricPowerSD;
        this.nodeElectricCostMean = nodeElectricCostMean;
        this.nodeElectricCostSD = nodeElectricCostSD;
        this.netThroughputMean = netThroughputMean;
        this.netThroughputSD = netThroughputSD;
        this.currentDifficulty = difficulty;
    }

    
    /**
     * Load configuration using Config object.
     */
    public void LoadConfig() {
        this.setTxArrivalIntervalRate(Config.getPropertyFloat("workload.lambda")); //How often transactions arrive at the system.
        this.setTxSizeMean(Config.getPropertyFloat("workload.txSizeMean"));
        this.setTxSizeSD(Config.getPropertyFloat("workload.txSizeSD"));
        this.setTxFeeValueMean(Config.getPropertyFloat("workload.txFeeValueMean"));
        this.setTxFeeValueSD(Config.getPropertyFloat("workload.txFeeValueSD"));
        this.setNodeHashPowerMean(Config.getPropertyFloat("pow.hashPowerMean"));
        this.setNodeHashPowerSD(Config.getPropertyFloat("pow.hashPowerSD"));
        this.setNodeElectricPowerMean(Config.getPropertyFloat("node.electricPowerMean"));
        this.setNodeElectricPowerSD(Config.getPropertyFloat("node.electricPowerSD"));
        this.setNodeElectricCostMean(Config.getPropertyFloat("node.electricCostMean"));
        this.setNodeElectricCostSD(Config.getPropertyFloat("node.electricCostSD"));
        this.setNetThroughputMean(Config.getPropertyFloat("net.throughputMean"));
        this.setNetThroughputSD(Config.getPropertyFloat("net.throughputSD"));
        this.setCurrentDifficulty(Config.getPropertyDouble("pow.difficulty"));
        this.setSeed(Config.getPropertyLong("sim.randomSeed"));
    }
    
    @Override
    public void setSeed(long seed) {
    	super.randomSeed = seed;
    	random.setSeed(seed);
    }

    /**
     * Calculates a random interval following the Poisson distribution.
     *
     * @param lambda The parameter of the Poisson distribution (lambda greater or equal to 0). To be used for arrival rates.
     * @return The random interval following the Poisson distribution.
     * @throws ArithmeticException if the provided lambda value is less than 0.
     */
    public double getPoissonInterval(float lambda) {
    	if(lambda < 0)
    		throw new ArithmeticException("lambda < 0");
		double p = random.nextDouble();
		while (p == 0.0){
			p = random.nextDouble();
		}
        return (double) (Math.log(1-p)/(-lambda));
    }
    
    
    /**
     * Generates a random value following the Gaussian distribution (normal distribution).
     *
     * @param mean      The mean value of the distribution.
     * @param deviation The standard deviation of the distribution (deviation greater or equal to 0).
     * @return The generated random value following the Gaussian distribution.
     * @throws ArithmeticException if the provided deviation value is less than 0.
     */
    private float getGaussian(float mean, float deviation) {
    	if(deviation < 0)
    		throw new ArithmeticException("Standard deviation < 0");
    	float gaussianValue = mean + (float) random.nextGaussian() * deviation;
    	while(gaussianValue <= 0) {
    		gaussianValue = mean + (float) random.nextGaussian() * deviation;
    	}
    	return gaussianValue;
    }
 
    
    /**
     * See parent. Use Poisson distribution.
     */
    @Override
    public float getNextTransactionArrivalInterval() {
    	return (float) getPoissonInterval(txArrivalIntervalRate)*1000;
    }

    /**
     * See parent. Use Uniform distribution.
     */
    @Override
    public int getRandomNum(int min, int max) {
        return(random.nextInt((max - min) + 1) + min);
    }


    /**
     * See parent. Uses method {@linkplain StandardSampler#getNextMiningIntervalMiliSeconds(double, double)}.
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
        return ((double) (Math.log(1-Math.random())/Math.log1p(- 1.0/difficulty)));

    }

    /**
     * As {@linkplain StandardSampler#getNextMiningIntervalMiliSeconds(double, double) but in seconds}
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
     * Works by first calling {@linkplain StandardSampler#getNextMiningInterval(double)} to get the needed trials and then 
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
    public float getNextTransactionFeeValue() {
        return(getGaussian(txValueMean, txValueSD));
    }

    /**
     * See parent. Use Normal distribution.
     */
    @Override
    public long getNextTransactionSize() {
        return(long) (getGaussian(txSizeMean, txSizeSD));
    }

    /**
     * See parent. Use Normal distribution.
     */
    @Override
    public float getNextNodeElectricPower() {
        return (getGaussian(nodeElectricPowerMean, nodeElectricPowerSD));
    }

    /**
     * See parent. Use Normal distribution.
     */
    @Override
    public float getNextNodeHashPower() {
        return (getGaussian(nodeHashPowerMean, nodeHashPowerSD));
    }

    /**
     * See parent. Use Normal distribution.
     */
    @Override
    public float getNextNodeElectricityCost() {
        return (getGaussian(nodeElectricCostMean, nodeElectricCostSD));
    }

    /**
     * See parent. Use Uniform distribution.
     */
    @Override
    public int getNextRandomNode(int nNodes) {
        return(random.nextInt(nNodes));
    }

    /**
     * See parent. Use Normal distribution.
     */
    @Override
    public float getNextConnectionThroughput() {
        return (getGaussian(netThroughputMean, netThroughputSD));
    }


}







