package cmg.cnsim.engine.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cmg.cnsim.engine.Config;

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
    }

    @Test
    public void testParseStringToArray_NumberInputException() {
        // Attempted input with a non-numeric value
        // Condition should be thrown when a non-numeric value is attempted to be parsed as number
        String i = "{5, 34, 97, 102, !}";
        Exception numberFormatException = assertThrows(IllegalArgumentException.class, () -> Config.parseStringToArray(i));
        System.out.println("Exception: " + numberFormatException.getMessage());
    }

    // Check the method behaviour when the input string is malformed
    // due to a missing opening curly brace "{" with 4 different scenarios
    //
    // Scenario 4 does not throw an exception as expected
    // TODO: look into this further and fix

    @Test
    public void testParseStringToArray_MissingOpeningBrace() {
        String[] input = {
                "6, 7, 8}", // scenario 1
                "2, 3, 4, 5, 6}", // scenario 2
                "5, 34, 97, 102, 1030}", // scenario 3
                "202, 203, 204}"}; // scenario 4
        for (String i : input) {
            IllegalArgumentException openBracketException = assertThrows(IllegalArgumentException.class, () -> Config.parseStringToArray(i));
            System.out.println("Exception: " + i + " " + openBracketException.getMessage());
        }
    }

    // Check the method behaviour when the input string is malformed
    // due to a missing closing curly brace "}" with 4 different scenarios
    //
    // Scenarios 3 and 4 do not throw exceptions as expected
    // TODO: look into this further and fix

    @Test
    public void testParseStringToArray_MissingClosingBrace() {
        // Test case with missing closing brace "}" with 4 different scenarios
        String[] input = {
                "{6, 7, 8", // scenario 1
                "{2, 3, 4, 5, 6", // scenario 2
                "{5, 34, 97, 102, 1030", // scenario 3
                "{202, 203, 204"}; // scenario 4

        for (String i : input) {
            IllegalArgumentException closedBracketException = assertThrows(IllegalArgumentException.class, () -> Config.parseStringToArray(i));
            System.out.println("Exception: " + i + " " + closedBracketException.getMessage());
        }
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
    public void testParseStringToArray_InputStringEmptyCase2() {
        String i = "{}";
        long[] expected = new long[0];
        long[] actual;
        actual = Config.parseStringToArray(i);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testConfigTxtNumTransactionsRetrieval() {
        // Check whether the retrieved value from config.txt matches the expected value
        Config.init("./resources/config.txt"); // initialize the configuration file
        int numTransactions = Config.getPropertyInt("workload.numTransactions"); // retrieve numTransactions
        // Assert that the value matches the expected value of 100 workload.numTransactions as per config.txt
        assertEquals(100, numTransactions);
    }

    //TODO update implementation to include a check for exceeding workload.numTransactions

    @Test
    public void testParseStringToArray_IDsExceedLimits() {
        Config.init("./resources/config.txt");
        int numTransactions = Config.getPropertyInt("workload.numTransactions");

        // One of the IDs exceeds the max allowed value
        String i = "{9, 15, " + (numTransactions + 1) + "}";

        Exception IDsExceedingLimitException = assertThrows(IllegalArgumentException.class, () -> Config.parseStringToArray(i));
        System.out.println("Exception: " + IDsExceedingLimitException.getMessage());
    }
}


