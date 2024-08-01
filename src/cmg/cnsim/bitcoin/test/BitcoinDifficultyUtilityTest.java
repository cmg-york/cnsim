package cmg.cnsim.bitcoin.test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import cmg.cnsim.bitcoin.BitcoinDifficultyUtility;

public class BitcoinDifficultyUtilityTest {
	// initial target is 0x00000000FFFF0000000000000000000000000000000000000000000000000000,
	// out of FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF possible hash values
	// so the equivalent CNSIM difficulty should be FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF
	// divided by 00000000FFFF0000000000000000000000000000000000000000000000000000
	// using online calculators, this value is roughly 4.29503×10^9
	private static final double CNSIM_INITIAL_DIFF = 4295030000.0;
	private static final double ERROR_BAND = 10000.0;
	private static final int MAX_BTC_DIFF = Integer.MAX_VALUE - 1;
	private static final int NUM_TRIALS = 10000;

	@Test
	public void testBTCToCNSIM_initialTarget() {
		double CNSIMDiff = BitcoinDifficultyUtility.BTCToCNSIM(1.0);

		assertEquals(CNSIMDiff, CNSIM_INITIAL_DIFF, 10000.0);
	}

	@Test
	public void testBTCToCNSIM_randomDifficulties() {
		// test random difficulty values
		Random gen = new Random();
		for(int i = 0; i < NUM_TRIALS; i++) {
			double BTCDiff = gen.nextInt(MAX_BTC_DIFF) * gen.nextDouble() + 1;
			double CNSIMDiff = BitcoinDifficultyUtility.BTCToCNSIM(BTCDiff);

			assertEquals(CNSIMDiff, CNSIM_INITIAL_DIFF*BTCDiff, ERROR_BAND*BTCDiff);
		}
	}

	@Test
	public void testCNSIMToBTC_initialTarget() {
		double BTCDiff = BitcoinDifficultyUtility.CNSIMToBTC(CNSIM_INITIAL_DIFF);

		assertEquals(BTCDiff, 1, ERROR_BAND/CNSIM_INITIAL_DIFF);
	}

	@Test
	public void testCNSIMToBTC_randomDifficulties() {
		// test random difficulty values
		Random gen = new Random();
		for(int i = 0; i < NUM_TRIALS; i++) {
			double factor = gen.nextInt(MAX_BTC_DIFF);
			double CNSIMDiff = CNSIM_INITIAL_DIFF*factor;
			double BTCDiff = BitcoinDifficultyUtility.CNSIMToBTC(CNSIMDiff);

			assertEquals(BTCDiff, 1*factor, (ERROR_BAND*factor)/CNSIM_INITIAL_DIFF);
		}
	}
	
	// TODO: test for edge cases, such as floating point underflow
	// TODO: test for construction of target strings
}
