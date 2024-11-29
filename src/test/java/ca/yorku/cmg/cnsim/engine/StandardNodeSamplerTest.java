package ca.yorku.cmg.cnsim.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Random;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import ca.yorku.cmg.cnsim.bitcoin.BitcoinDifficultyUtility;
import ca.yorku.cmg.cnsim.engine.StandardNodeSampler;
import ca.yorku.cmg.cnsim.engine.Sampler;

class StandardNodeSamplerTest {
	private StandardNodeSampler s;
	private Sampler s0;
	
	@BeforeEach
	void setUp() throws Exception {
		String[] args = {"-c", "src/test/resources/application.properties"};
		long[] seedArray = {123};
		boolean[] flagArray = {false};	
		

		System.out.print("Current directory" + System.getProperty("user.dir"));
		
        try{
            ConfigInitializer.initialize(args);
        } catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }
		
		s0 = new Sampler();

		s = new StandardNodeSampler(s0, seedArray.clone(), flagArray.clone(), 1);
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testGetNextMiningIntervalTrials() {
		
		Random r = s.getRandom();

		
		/**  
		 * Average number of times to get it right under difficulty d is equal to d.
		 * Specifically for a repeated process with probability of successful outcome p 
		 * the average number of repeated trials until we get successful outcome is 1/p. 
		 * Since we define d = [Search Space]/[Success Space], it follows that p = 1/d. 
		 * 
		 */
		
		/** 
		 * Try with difficulty = 10
		 */
		double count = 0;
		double trials = 0;
		float difficulty = 10;
		for (trials=1;trials<=1000;trials++) {
			count += s.getNextMiningIntervalTrials(difficulty);
		}
		
		System.out.println("Average Trials to Success:" + ((float) count)/((float) trials));
		assertEquals(10.0,((float) count)/((float) trials),1);
		
		
		
		// Try the same but experimentally
		count = 0;
		trials = 0;
		difficulty = 10;
		for (trials=1;trials<=1000;trials++) {
			boolean found = false;
			int tri = 0;
			while(!found) {
				found = (r.nextInt((int) difficulty)==0);
				tri++;
			}
			count = count + tri;
		}
		System.out.println("Average Trials to Success (experimental):" + ((float) count)/((float) trials));
		assertEquals(10.0,((float) count)/((float) trials),1);
		
		
		/** 
		 * Try with difficulty = 100
		 */
		count = 0;
		trials = 0;
		difficulty = 100;
		for (trials=1;trials<=1000;trials++) {
			count += s.getNextMiningIntervalTrials(difficulty);
		}
		
		System.out.println("Average Trials to Success:" + ((float) count)/((float) trials));
		assertEquals(100.0,((float) count)/((float) trials),10);
		

		count = 0;
		trials = 0;
		difficulty = 100;
		for (trials=1;trials<=1000;trials++) {
			boolean found = false;
			int tri = 0;
			while(!found) {
				found = (r.nextInt((int) difficulty)==0);
				tri++;
			}
			count = count + tri;
		}
		System.out.println("Average Trials to Success (experimental):" + ((float) count)/((float) trials));
		assertEquals(100.0,((float) count)/((float) trials),10);

	}

	@Test
	void testGetNextMiningInterval() {

		/** 
		 * The time to validate ("solve the puzzle") is simply the expected number of trials, 
		 * divided by the search capability of the node in trials per second.
		 * Here the parameter is GTrials / second (billion trials per second).
		 * The result is in miliseconds.
		 * 
		 */
		
		double difficulty = 1000; //Avg = 1000 trials
		double hashpower = 1.0/1E6; //1000 trials per second
		
		/**
		 * For 1000 trials per second, the node needs on average 1 second to perform 1000 trials.
		 * So on average solves the puzzle in 1 second, i.e. 1000 mseconds
		 */
		
		//s.setSeed(1);
		s.setCurrentDifficulty(difficulty);
		
		double totalMillis = 0;
		int rounds;
		for (rounds=1;rounds<=1000;rounds++) {
			totalMillis += s.getNextMiningInterval(hashpower);
		}
		System.out.println("Average time to success:" + ((float) totalMillis)/((float) rounds));
		assertEquals(1000.0,((float) totalMillis)/((float) rounds),50);

		
		
		hashpower = 1.0/1E9; //1 trial per second

		/**
		 * For 1 trial per second, the node needs on average 1000 seconds to perform 1000 trials.
		 * So on average solves the puzzle in 1000 seconds, i.e. 1000000 mseconds
		 */
		
		s.setCurrentDifficulty(difficulty);
		
		totalMillis = 0;
		for (rounds=1;rounds<=1000;rounds++) {
			totalMillis += s.getNextMiningInterval(hashpower);
		}
		System.out.println("Average time to success:" + ((float) totalMillis)/((float) rounds));
		assertEquals(1000000.0,((float) totalMillis)/((float) rounds),100000);
		
		
		
		//fail("Not yet implemented");
	}

	
	@Test
	//@Tag("exclude")
	void testGetNextMiningIntervalRealistic() {
		
		/** 
		 * The time to validate ("solve the puzzle") is simply the expected number of trials, 
		 * divided by the search capability of the node in trials per second.
		 * Here the parameter is GTrials / second (billion trials per second).
		 * The result is in miliseconds.
		 * 
		 * In this test we try with realistic data from Bitcoin sources:
		 * 2024-11-25 parameters:
		 * 	Difficulty: 102.29 T (in Bitcoin terms) = 102.29 E12 = 1.0229 e14
		 *  Hash Rate: 706791930.063 TH/sec = 706791930.063 E12 (H/sec) = 706,791,930,063 (GH/sec) = 7.06791930063E11 (GH/sec) 
		 *  Expected confirmation time: 9.15 minutes
		 *  
		 *  
		 */
		
		double difficulty = BitcoinDifficultyUtility.BTCToCNSIM(1.0229E14);
		System.err.println(difficulty);
		double hashpower = 7.06791930063E11; //Giga-trials per second
		
		
		s.setCurrentDifficulty(difficulty);
		
		double totalMillis = 0;
		int rounds;
		for (rounds=1;rounds<=1000;rounds++) {
			totalMillis += s.getNextMiningInterval(hashpower);
		}
		System.out.println("Average time to success: " + ((float) totalMillis)/((float) rounds));
		System.out.println("..........   in seconds: " + ((float) totalMillis)/((float) rounds)/(60000) + " sec");
		//60 seconds/minute x 1000 miliseconds/second x 9.15 minutes
		// give or take 2 minutes =60 seconds/minute x 1000 miliseconds/second x 2 minutes
		assertEquals(1000*60*9.15,((float) totalMillis)/((float) rounds),1000*60*9.15);
	}
	
}
