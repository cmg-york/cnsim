package ca.yorku.cmg.cnsim.engine.network.test;

import ca.yorku.cmg.cnsim.engine.network.AbstractNetwork;
import ca.yorku.cmg.cnsim.engine.network.RandomEndToEndNetwork;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RandomEndToEndNetworkTest {

	AbstractNetwork n;
	
	@BeforeEach
	void setUp() throws Exception {
		n = new RandomEndToEndNetwork();
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testGetPropagationTimeFloatFloat() {
		/**
		 * Throughput: 1MBit/sec = 1,000,000 bits/sec
		 * Message Size: 10Mb = 10,000,000 bytes
		 * Time: 1 minute and 20 seconds, i.e. 80 seconds, i.e. 80,000 milliseconds 
		 */
		System.out.println("Propagation time: " + n.getPropagationTime(1000000, 10000000));
		assertEquals(80000, n.getPropagationTime(1000000, 10000000));
		
		/**
		 * Throughput: 50MBit/sec = 50,000,000 bits/sec
		 * Message Size: 1Mb = 1,000,000 bytes (Bitcoin case)
		 * 160 milliseconds 
		 */
		System.out.println("Propagation time: " + n.getPropagationTime(50000000, 1000000));
		assertEquals(160, n.getPropagationTime(50000000, 1000000));

		
		
		
	}

}
