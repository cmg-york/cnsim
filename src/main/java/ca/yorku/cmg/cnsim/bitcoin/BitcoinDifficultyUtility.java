package ca.yorku.cmg.cnsim.bitcoin;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BitcoinDifficultyUtility {
	// specified rounding precision TODO verify that bitcoin uses same rounding policy
	private static MathContext rounding = new MathContext(64, RoundingMode.HALF_EVEN);

	// max value of 64 hex digits (i.e. the search space of hash values)
	public static final BigDecimal MAX_HASH_VALUE = new BigDecimal(new BigInteger("F".repeat(64), 16));

	// hex representation of initial target (i.e. max allowable value of hash - defines the success space)
	// TODO verify initial target for mining pools, use whichever is more accurate
	private static final String INITIAL_TARGET_STR = "00000000FFFF0000000000000000000000000000000000000000000000000000";

	// initial target (target for difficulty of 1) that all difficulties are scaled for
	public static final BigDecimal INITIAL_TARGET = new BigDecimal(new BigInteger(INITIAL_TARGET_STR, 16));

	// equivalent CNSIM difficulty for the initial bitcoin target (search space/success space)
	public static final double CNSIM_INITIAL_DIFFICULTY = 1.0/INITIAL_TARGET.divide(MAX_HASH_VALUE, rounding).doubleValue();
	
	// six conversions: difficulty <-> CNSIM, bdiff <-> CNSIM, pdiff <-> CNSIM
	
	/**
	 * Converts from Bitcoin difficulty to CNSIM difficulty. 
	 * Note that when BTC difficulty is given directly, simply scale the converted 
	 * initial BTC difficulty.
	 * 
	 * @param BTCDiff The given Bitcoin difficulty.
	 * @return The equivalent CNSIM difficulty.
	 */
	public static double BTCToCNSIM(double BTCDiff) {
		return CNSIM_INITIAL_DIFFICULTY * BTCDiff;
	}

	/**
	 * Converts from CNSIM difficulty to Bitcoin difficulty.
	 * Note that we simply scale the converted initial CNSIM to BTC difficulty
	 * for the current CNSIM difficulty level.
	 * 
	 * WARNING: may potentially result in impossible Bitcoin difficulty levels
	 * if the CNSIM difficulty is too low. Be aware that returned BTC difficulties
	 * may be less than 1!
	 * 
	 * @param CNSIMDiff The given CNSIM difficulty.
	 * @return The equivalent Bitcoin difficulty.
	 */
	public static double CNSIMToBTC(double CNSIMDiff) {
		return CNSIMDiff/CNSIM_INITIAL_DIFFICULTY;
	}

	/**
	 * Often, bitcoin difficulty is not given directly, but implicitly through
	 * a packed target value ("bits"). This convenience function converts from the 
	 * packed target value to the equivalent CNSIM difficulty directly.
	 * 
	 * @param bits The bitcoin metric representing the truncated current target.
	 * @return The equivalent CNSIM difficulty.
	 */
	public static double packedTargetToCNSIM(String bits) {
		return BTCToCNSIM(packedTargetToBTCDifficulty(bits).doubleValue());
	}
	
	/**
	 * Convenience function to directly convert a CNSIM difficulty to the 
	 * equivalent packed bitcoin target value (bdiff).
	 * 
	 * @param CNSIMDiff The given CNSIM difficulty level.
	 * @return The equivalent bitcoin packed target value.
	 */
	public static String CNSIMToPackedTarget(double CNSIMDiff) {
		return BTCDifficultyToPackedTarget(new BigDecimal(CNSIMToBTC(CNSIMDiff)));
	}

	/**
	 * Often, bitcoin difficulty is not given directly, but implicitly through
	 * a target value (the pool target is the full, untruncated value of this 
	 * target). This convenience function converts from the pool target value 
	 * to the equivalent CNSIM difficulty directly.
	 * 
	 * @param poolTarget The bitcoin metric representing the full current target.
	 * @return The equivalent CNSIM difficulty.
	 */
	public static double poolTargetToCNSIM(String poolTarget) {
		return BTCToCNSIM(poolTargetToBTCDifficulty(poolTarget).doubleValue());
	}

	/**
	 * Convenience function to directly convert a CNSIM difficulty to the 
	 * equivalent full bitcoin target value (i.e. pool target value).
	 * 
	 * @param CNSIMDiff The given CNSIM difficulty level.
	 * @return The equivalent bitcoin pool target value.
	 */
	public static String CNSIMToPoolTarget(double CNSIMDiff) {
		return BTCDifficultyToPoolTarget(new BigDecimal(CNSIMToBTC(CNSIMDiff)));
	}
	
	

	// HELPER FUNCTIONS - conversions between packed/pool targets and BTC difficulty

	/**
	 * Converts current target to equivalent, implied difficulty rating.
	 * Format of packed target is 8 hex digits, with the first two digits the index,
	 * and the remaining 6 digits representing the coefficient.
	 * 
	 * The target is calculated as: coefficient * 2^(8 * (index - 3)), with the leading
	 * digits being padded with zeroes. In other words, adding 1 to the value of the 
	 * index places another two trailing hex zeroes to the target.
	 * 
	 * @param bits The bitcoin metric representing the truncated current target.
	 * @return The equivalent difficulty for this current target.
	 * @throws The hexstring should be 8 hex digits, and additionally, the 24th bit in 
	 * the packed target is a sign bit, which cannot be negative.
	 */
	public static BigDecimal packedTargetToBTCDifficulty(String bits) throws IllegalArgumentException {
		// first check that the string is valid (8 hex digits, optional "0x" prefix)
		Pattern hexPattern = Pattern.compile("^(0[xX])?([0-9a-fA-F]{2})([0-9a-fA-F]{6})$");
		Matcher hexMatch = hexPattern.matcher(bits);
		if(!hexMatch.find()) {
			throw new IllegalArgumentException("Target string is not 8 hex digits!");
		}

		// convert to hex numbers
		BigInteger index = new BigInteger(hexMatch.group(2), 16);
		BigInteger coefficient = new BigInteger(hexMatch.group(3), 16);

		// ensure 24th bit is not set (would indicate negative target) 
		// Note: this is leading bit of coefficient
		if((coefficient.intValue() & (1 << 24)) == 1) {
			throw new IllegalArgumentException("Negative target value!");
		}

		// calculate actual value of target, noting that:
		// 2^(8 * (target - 3)) = 2^(4 * 2 * (target - 3)) = (2^4)^(2 * target - 6)
		int numZeroes = 2 * index.intValue() - 6;
		BigInteger multiplier = new BigInteger("16").pow(numZeroes);
		BigDecimal target = new BigDecimal(coefficient.multiply(multiplier));

		// calculate implied difficulty from target value
		BigDecimal difficulty = INITIAL_TARGET.divide(target, rounding);

		return difficulty;
	}

	/**
	 * From the pool difficulty (i.e. full, untruncated target value), calculates
	 * and returns the implied difficulty level.
	 * 
	 * @param poolTarget
	 * @return The equivalent difficulty for this current target.
	 * @throws IllegalArgumentException The hexstring should be 64 hex digits, and 
	 * should not be less the minimum target value.
	 */
	public static BigDecimal poolTargetToBTCDifficulty(String poolTarget) throws IllegalArgumentException {
		// first check that the string is valid (64 hex digits, optional "0x" prefix, min 8 leading zeroes)
		Pattern hexPattern = Pattern.compile("^(0[xX])?0{8}([0-9a-fA-F]{56})$");
		Matcher hexMatch = hexPattern.matcher(poolTarget);
		if(!hexMatch.find()) {
			throw new IllegalArgumentException("Invalid target value!");
		}

		// convert target to actual value
		BigDecimal target = new BigDecimal(new BigInteger(hexMatch.group(2), 16));


		// calculate implied difficulty from target value
		BigDecimal difficulty = INITIAL_TARGET.divide(target, rounding);

		return difficulty;
	}
	
	/**
	 * From the bitcoin difficulty, generates the equivalent full target 
	 * string (i.e. pool target).
	 * 
	 * @param difficulty The given level of bitcoin difficulty.
	 * @return The pool target string implied by the given difficulty.
	 */
	public static String BTCDifficultyToPoolTarget(BigDecimal difficulty) {
		// difficulty is a scaling factor on the initial target
		BigInteger scaledTarget = INITIAL_TARGET.divide(difficulty, rounding).toBigInteger();
		
		String targetStr = String.format("0x%64S", scaledTarget.toString(16)).replace(" ", "0");
		
		return targetStr;
	}
	
	// TODO verify that this matches actual BTC behaviour
	/**
	 * From the given bitcoin difficulty, generates the packed target string that
	 * is closest to the specified difficulty.
	 * 
	 * @param difficulty The given level of bitcoin difficulty.
	 * @return The packed target string implied by the given difficulty.
	 */
	public static String BTCDifficultyToPackedTarget(BigDecimal difficulty) {
		// first get full target string
		String fullTarget = BTCDifficultyToPoolTarget(difficulty);
		
		// find six most significant hex digits, noting the leading zeroes
		Pattern packedPattern = Pattern.compile("^(0[xX])?(0+)([1-9a-fA-F][0-9a-fA-F]{5})[0-9a-fA-F]+$");
		Matcher packedMatch = packedPattern.matcher(fullTarget);
		if(!packedMatch.find()) {
			throw new IllegalArgumentException(
					String.format("Difficulty %s corresponds to invalid target value: %s%n", difficulty, fullTarget));
		}
		
		int leadingZeroes = packedMatch.group(2).length();
		String coefficient = packedMatch.group(3);
		
		// generate correct values for the index and coefficient strings, based on count of leading 0s
		int index;
		String indexStr;
		
		// if it is impossible to correctly place coefficient due to number of leading 0s
		// (due to index shifting by 2 zeroes at a time) give up a digit to right shift coefficient 
		if((58 - leadingZeroes) % 2 != 0) {
			// right shift in coefficient and increase the index 
			// net effect of one left shift on value, but losing a digit of precision
			index = (59 - leadingZeroes)/2 + 3;
			coefficient = String.format("0%S", coefficient.substring(0, 5));
		}else {
			index = (58 - leadingZeroes)/2 + 3;
			
		}
		
		// convert to hex
		indexStr = String.format("%02X", index);
		
		// construct combined packed target string
		String targetStr = String.format("0x%S%S", indexStr, coefficient);
		
		return targetStr;
	}
}
