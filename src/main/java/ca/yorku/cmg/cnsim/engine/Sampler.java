package ca.yorku.cmg.cnsim.engine;

import java.util.Random;

public class Sampler {
	AbstractTransactionSampler transactionSampler;
	AbstractNodeSampler nodeSampler;
	AbstractNetworkSampler networkSampler;
	
	
	public AbstractTransactionSampler getTransactionSampler() {
		return transactionSampler;
	}
	public void setTransactionSampler(AbstractTransactionSampler txSampler) {
		this.transactionSampler = txSampler;
	}
	public AbstractNodeSampler getNodeSampler() {
		return nodeSampler;
	}
	public void setNodeSampler(AbstractNodeSampler nodeSampler) {
		this.nodeSampler = nodeSampler;
	}
	public AbstractNetworkSampler getNetworkSampler() {
		return networkSampler;
	}
	public void setNetworkSampler(AbstractNetworkSampler netSampler) {
		this.networkSampler = netSampler;
	}
	
	
	//
	// Generic Sampling
	//
	
	/**
     * Calculates a random interval following the Poisson distribution.
     *
     * @param lambda The parameter of the Poisson distribution (lambda greater or equal to 0). To be used for arrival rates.
     * @return The random interval following the Poisson distribution.
     * @throws ArithmeticException if the provided lambda value is less than 0.
     */
    public double getPoissonInterval(float lambda, Random random) {
    	if(lambda < 0)
    		throw new ArithmeticException("lambda < 0");
		double p = random.nextDouble();
		while (p == 0.0){
			p = random.nextDouble();
		}
        return (double) (Math.log(1-p)/(-lambda));
    }
    
    
    /**
     * Generates a random value following the Gaussian distribution (normal distribution), 
     * truncated to ensure it is positive
     *
     * @param mean  The mean value of the distribution.
     * @param deviation The standard deviation of the distribution (deviation greater or equal to 0).
     * @return The generated random value following the Gaussian distribution.
     * @throws ArithmeticException if the provided deviation value is less than 0.
     */
    protected float getGaussian(float mean, float deviation, Random random) {
    	if(deviation < 0)
    		throw new ArithmeticException("Standard deviation < 0");
    	float gaussianValue = mean + (float) random.nextGaussian() * deviation;
    	while(gaussianValue <= 0) {
    		gaussianValue = mean + (float) random.nextGaussian() * deviation;
    	}
    	return gaussianValue;
    }
		
    protected int getRandomNum(int min, int max, Random random) {
        return(random.nextInt((max - min) + 1) + min);
    }
	
}
