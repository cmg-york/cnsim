package ca.yorku.cmg.cnsim.engine;

import java.util.Arrays;
import java.util.Random;

public class SeedManager {
	private long[] seedArray = null; 
	private int currentIndex = 0;
	
	
	public SeedManager () {
	}
	
	public SeedManager (long[] seedChain) {
		super();
		seedArray = seedChain;
	}
	
	protected void setSeedArray(long[] seedChain) {
		seedArray = seedChain;		
	}
	
	public String getSeedArray() {
		return(Arrays.toString(seedArray));
	}
		
	protected long nextSeed() {
		Debug.p("Current Index: " + currentIndex + " for seedArray " + seedArray);
		long newSeed = seedArray[currentIndex];
		currentIndex = (currentIndex + 1) % seedArray.length;
		return newSeed;
	}
	
	
	public void updateSeed(Random random) {
		long randomSeed = nextSeed();
    	Debug.p("Setting the seed of " + random.toString() + " to " + randomSeed);
    	random.setSeed(randomSeed);
    }
}
