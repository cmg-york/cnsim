package ca.yorku.cmg.cnsim.engine;

import java.util.Arrays;
import java.util.Random;

public class SeedManager {
	private long[] seedArray = null;
	private boolean[] seedUpdateFlags = null;
	private int currentIndex = 0;
	private int simID = 0;
	
	public int getSimD() {
		return simID;
	}

	public void setSimD(int simD) {
		this.simID = simD;
	}

	public SeedManager () {
	}
	
	public SeedManager (long[] seedChain, boolean[] flagArray, int sID) {
		super();
		seedArray = seedChain;
		seedUpdateFlags = flagArray;
		simID = sID;
	}
	
	public String getSeedUpdateFlag() {
		return Arrays.toString(seedUpdateFlags);
	}

	public void setSeedUpdateFlag(boolean[] seedUpdateFlag) {
		this.seedUpdateFlags = seedUpdateFlag;
	}

	protected void setSeedArray(long[] seedChain) {
		seedArray = seedChain;		
	}
	
	public String getSeedArray() {
		return(Arrays.toString(seedArray));
	}
		
	protected long nextSeed() {
		
		//Debug.p("     UPDATING seed:");
		//Debug.p("       Current Index: " + currentIndex);
		//Debug.p("       Next seed in line: " + seedArray[currentIndex]);
		//Debug.p("       Seed to be updated?: " + seedUpdateFlags[currentIndex]);
		//Debug.p("       Seed to be updated?: " +  (seedArray[currentIndex] + (seedUpdateFlags[currentIndex] ? simID : 0)));

		long newSeed = seedArray[currentIndex] + (seedUpdateFlags[currentIndex] ? simID : 0);
		currentIndex = (currentIndex + 1) % seedArray.length;
		return newSeed;
	}
	
	
	public void updateSeed(Random random) {
		long randomSeed = nextSeed();
    	random.setSeed(randomSeed);
      	//Debug.p("Setting the seed of " + random.toString() + " to " + randomSeed);
	}
}
