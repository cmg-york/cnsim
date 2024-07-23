package cmg.cnsim.engine.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cmg.cnsim.engine.Config;
import static cmg.cnsim.engine.Config.parseStringToArray;

public class ConfigTest {

    /*
     * ParseStringToArray method takes a string of format "{ID1, ID2, ...}, removes the curly braces,
     * splits the string into individual elements based on commas and converts each element into a long array
     * containing Transaction IDs.
     *
     * These tests are written specifically for the parseStringToArrayMethod of the Config class.
     * CNSIMSU24-67 - YS
     */

    private Config config;

    @BeforeEach
    public void setup() {
        config = new Config();
    }

    @Test
    public void testParseStringToArray_RemoveBracesAndSplit() {
        // Valid input test
        String i = "{45, 52, 67, 85, 93}"; // Input string containing transaction IDs
        long[] expected = {45, 52, 67, 85, 93};
        long[] actual = Config.parseStringToArray(i);
        assertArrayEquals(expected, actual);
        System.out.print("Actual output: ");
        for (long num : actual) { System.out.print(num + " ");
        }
    }

    @Test
    public void testParseStringToArray_NumberInputException() {
        // Attempted input with a non-numeric value
        // Condition should be thrown when a non-numeric value is attempted to be parsed as number
        String i = "{5, 34, 97, 102, !}";
        Exception numberFormatException = assertThrows(IllegalArgumentException.class, () -> Config.parseStringToArray(i));
        System.out.println("Exception: " + numberFormatException.getMessage());
    }

    // Existing implementation does not throw an IllegalArgumentException for invalid inputs.
    // TODO: add exception for malformed strings for these tests to pass

    // Issue Re: The Next Two Tests - Case 1 & 2
    //
    // The current parseStringToArray method expects the input string in the form:{ID1, ID2....}.
    // Because the input string is empty or only has curly braces, it attempts
    // parsing, which causes IndexOutOfBoundsException and NumberFormatException.

    @Test
    public void testParseStringToArray_InputStringEmptyCase1() {
        String i = "";
        long[] expected = new long[0];
        long[] actual;
        actual = Config.parseStringToArray(i);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testParseStringToArray_InputStringEmptyCase2 () {
        String i = "{}";
        long[] expected = new long[0];
        long[] actual;
        actual = Config.parseStringToArray(i);
        assertArrayEquals(expected, actual);
    }

    //@Test
    //public void testParseStringToArray_IDsPredefinedLimit () {
    // testing if any transaction IDs are greater than workload.numTransactions }
}



