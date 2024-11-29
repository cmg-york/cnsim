package ca.yorku.cmg.cnsim.engine;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import ca.yorku.cmg.cnsim.engine.StandardTransactionSampler;
import ca.yorku.cmg.cnsim.engine.Sampler;
import ca.yorku.cmg.cnsim.engine.ConfigInitializer;

class StandardTransactionSamplerTest {
	private StandardTransactionSampler s;
	private Sampler s0;
	
	@BeforeEach
	void setUp() throws Exception {
		String[] args = {"-c", "src/test/resources/application.properties"};
		long seed = 123;
		boolean flag = false;	
		
        try{
            ConfigInitializer.initialize(args);
        } catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }
		s0 = new Sampler();

		s = new StandardTransactionSampler(s0, seed, flag, 1);
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testGetNextTransactionArrivalInterval() {
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
