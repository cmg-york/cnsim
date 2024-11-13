package cmg.cnsim.engine.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cmg.cnsim.engine.AbstractSampler;
import cmg.cnsim.engine.StandardSampler;

class StandardSamplerTest {
	private StandardSampler s;
	
	@BeforeEach
	void setUp() throws Exception {
		s = new StandardSampler();
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testGetNextMiningIntervalTrials() {
		
		Random r = new Random();

		
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
	void testArrivalInterval() {
		
		float lambda = 4; //Tx/sec
		
		/**
		 * 4 Transactions per second means 1 (sec)/4 = 0.25 seconds interval.
		 * Hence 250 msec.
		 */
		
		float interv = 0;
		float rounds;
		for (rounds=1;rounds<=1000;rounds++) {
			s.setTxArrivalIntervalRate(lambda);
			interv += s.getNextTransactionArrivalInterval();
		}
		System.out.println("Average interval:" + ((float) interv)/((float) rounds));
		assertEquals(250,((float) interv)/((float) rounds),50);
	}
}
