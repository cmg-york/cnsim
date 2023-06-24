package esg.bcsim.tangle.test;

import static org.junit.Assert.assertArrayEquals;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import cmg.cnsim.engine.Config;
import cmg.cnsim.engine.Simulation;
import cmg.cnsim.engine.transaction.Transaction;
import esg.bcsim.tangle.TangleSite;

@TestInstance(Lifecycle.PER_CLASS)
public class TangleTest {
	private TangleForTest t; 
	private static int test = 0;
	private FileWriter writer = null;
	private int total = 20000;
	private int[] noTips = new int[total+2];

	/**
	 * Open a file for outputting the state of the tangle in every state.
	 * @throws Exception
	 */
	@BeforeAll
	void startUp() throws Exception{
		SimpleDateFormat formatter= new SimpleDateFormat("yyyy.MM.dd");
		Date date = new Date(System.currentTimeMillis());
		writer = new FileWriter("./log/TangleUnitTest - " + formatter.format(date) + ".csv",false);
		writer.write( "NodeID, TransID, Weight, Parent1, Parent2, isTip, level, SimTime" + System.lineSeparator());
	}
	
	/**
	 * Workflow:
	 * 1. Create various scenarios here. Pay attention to random seed and reset it in the global parameter.  
	 * 2. Study the output using R scripts and validate it.
	 * 3. Hard-code the output here for implementing the automated regression test  
	 * @throws Exception
	 */
	@BeforeEach
	void setup() throws Exception {

		//TOD Test cases: 
		// 1. Different numbers of founders, including 1, 0 and negative.
		// 2. Small, large numbers of particles
		// 3. Different alphas
		// 4. Different prevalence of orphans
		
		
		
		//Parameters.randomSeed = 12345;
		//TangleParameters.NumofParticles = 5;
		//TangleParameters.NumofFounders = 5;
		//TangleParameters.ALPHA = 0.001;
		//TangleParameters.WminPercentile = 0.25f; 
	    //TangleParameters.WmaxPercentile = 0.5f;
	    //TangleParameters.StartingCutset = 1000;

		Random r = new Random(Config.getPropertyLong("sim.randomSeed"));		
		
		int i;
		int par[] = new int[2];
	    
		t = new TangleForTest();
		noTips[test] = t.getTipCount();
		flushTangleState(test++);


		for(i = 1;i<=total;i=i+2) {
			TangleSite s = t.addNewTransaction(new Transaction(i));
			//System.out.printf("New:\t\t %3d \t %3d \t %3d \n", s.getID(),s.getParents()[0],s.getParents()[1]);
			noTips[test] = t.getTipCount();
			flushTangleState(test++);
			System.out.printf("Adding Transaciton %5d \r",i);

			par[0] = r.nextInt(i);
			par[1] = r.nextInt(i);
			s = new TangleSite(new Transaction(i),par);
			t.addValidatedTransaction(s);
			//System.out.printf("Existing: \t %3d \t %3d \t %3d \n", s.getID(),s.getParents()[0],s.getParents()[1]);
			noTips[test] = t.getTipCount();
			flushTangleState(test++);
			i++;
		}
	}

	
	private void flushTangleState(int testID) {
		try {
			ArrayList<String> tangleState = new ArrayList<String>(Arrays.asList(t.printStructure()));
			for(String str: tangleState) {
				  writer.write(testID + "," + str + "," + Simulation.currTime + System.lineSeparator());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	

	@Test
	final void testTangle() {
		
		System.out.println("Hello:" + t.getPerformanceStatsHeaders());
		System.out.println(t.getPerformanceStats());
		
		
		int[][] testTangle = {
			{22,-1,-1},
			{5,0,0},
			{13,0,0},
			{7,0,0},
			{2,0,0},
			{9,0,0},
			{11,2,2},
			{8,5,2},
			{7,6,7},
			{7,6,0},
			{4,8,9},
			{2,1,10},
			{1,11,11},
			{1,0,9},
			{5,3,3},
			{1,1,9},
			{3,14,14},
			{1,1,4},
			{2,16,16},
			{2,8,3},
			{1,18,19},
			{1,14,10}
			};

		System.out.println(Arrays.toString(noTips));
		assertArrayEquals(Arrays.copyOfRange(t.getTangle(),0,total+2),testTangle);
		
		int[] tipNumber = {1,1,2,3,4,5,5,5,4,5,4,3,3,4,4,5,5,5,5,6,5,6};

		assertArrayEquals(noTips,tipNumber);
	}


	
	@AfterAll
	private void teardown() {
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
