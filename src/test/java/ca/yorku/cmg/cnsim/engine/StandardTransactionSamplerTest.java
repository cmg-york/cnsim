package ca.yorku.cmg.cnsim.engine;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import ca.yorku.cmg.cnsim.engine.StandardTransactionSampler;
import ca.yorku.cmg.cnsim.engine.transaction.Transaction;
import ca.yorku.cmg.cnsim.engine.Sampler;
import ca.yorku.cmg.cnsim.engine.ConfigInitializer;

class StandardTransactionSamplerTest {
	private StandardTransactionSampler s;
	private Sampler s0;
	private long initSeed = 123;
	private boolean flag = false;
	private long switchTx = 100;
	private int simID = 5;
	
	@BeforeEach
	void setUp() throws Exception {
		String[] args = {"-c", "src/test/resources/application.properties"};
		
        try{
            ConfigInitializer.initialize(args);
        } catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }
		s0 = new Sampler();

		s = new StandardTransactionSampler(s0, simID);
		
		s.nailConfig(123, false, 15);
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testGetNextTransactionArrivalInterval() throws Exception {
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

	
	@Test
	void testGetNextTransactionArrivalIntervalSeed_1() throws Exception {
		float lambda = 4; //Tx/sec
		float rounds;
		
		s = new StandardTransactionSampler(s0, simID);
		
		initSeed = 123;
		flag = true;
		switchTx = 15;

		s.nailConfig(initSeed, flag, switchTx);
	
		for (rounds=1;rounds<=30;rounds++) {
			s.setTxArrivalIntervalRate(lambda);
			Transaction.getNextTxID();
			s.getNextTransactionArrivalInterval();
			//System.err.println("Tx just created: " + rounds + ", seed:" + s.getCurrentSeed());
			if (rounds < (switchTx)) {
				assertEquals(this.initSeed,s.getCurrentSeed(), "where rounds =" + rounds + " and switchTx = " + switchTx);
			}
			
			if (rounds == (switchTx)) {
				assertEquals(this.initSeed,s.getCurrentSeed(), "where rounds =" + rounds + " and switchTx = " + switchTx);
			} 
			
			if (rounds > (switchTx)) {
				assertEquals(this.initSeed + this.simID,s.getCurrentSeed(), "where rounds =" + rounds + " and switchTx = " + switchTx);
			} 
			

		}
		
	}
	
	
	@Test
	@Tag("exclude")
	void testGetNextTransactionFeeValue() {
		fail("Not yet implemented");
	}

	@Test
	@Tag("exclude")
	void testGetNextTransactionSize() {
		fail("Not yet implemented");
	}

}
