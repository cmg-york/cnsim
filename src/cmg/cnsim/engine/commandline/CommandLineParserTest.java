package cmg.cnsim.engine.commandline;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class CommandLineParserTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @Test
    void testParseWithValidArguments() {
        String[] args = {"-c", "config.txt", "--wl", "workload.txt", "--net", "network.txt", "--node", "node.txt", "--out", "output/"};
        CommandLineParser parser = CommandLineParser.parse(args);

        assertNotNull(parser);
        assertEquals("config.txt", parser.getConfigFile());
        assertEquals("workload.txt", parser.getWorkloadFile());
        assertEquals("network.txt", parser.getNetworkFile());
        assertEquals("node.txt", parser.getNodeFile());
        assertEquals("output/", parser.getOutputDirectory());
    }

    @Test
    void testParseWithMissingConfigFile() {
        String[] args = {"--wl", "workload.txt"};
        assertThrows(IllegalArgumentException.class, () -> CommandLineParser.parse(args));
    }

    @Test
    void testParseWithMissingArgumentValue() {
        String[] args = {"-c", "config.txt", "--wl"};
        assertThrows(IllegalArgumentException.class, () -> CommandLineParser.parse(args));
    }

    @Test
    void testParseWithUnknownOption() {
        String[] args = {"-c", "config.txt", "--unknown", "value"};
        assertThrows(IllegalArgumentException.class, () -> CommandLineParser.parse(args));
    }

    @Test
    void testParseWithHelpOption() {
        String[] args = {"--help"};
        assertNull(CommandLineParser.parse(args));
        assertTrue(outContent.toString().contains("Usage: cnsim -c <config_file> [options]"));
    }

    @Test
    void testParseWithNoArguments() {
        String[] args = {};
        assertNull(CommandLineParser.parse(args));
        assertTrue(outContent.toString().contains("Usage: cnsim -c <config_file> [options]"));
    }

    @Test
    void testParseWithOnlyConfigFile() {
        String[] args = {"-c", "config.txt"};
        CommandLineParser parser = CommandLineParser.parse(args);

        assertNotNull(parser);
        assertEquals("config.txt", parser.getConfigFile());
        assertNull(parser.getWorkloadFile());
        assertNull(parser.getNetworkFile());
        assertNull(parser.getNodeFile());
        assertNull(parser.getOutputDirectory());
    }

    @Test
    void testParseWithAllOptions() {
        String[] args = {
                "-c", "config.txt",
                "--wl", "workload.txt",
                "--net", "network.txt",
                "--node", "node.txt",
                "--out", "output/"
        };
        CommandLineParser parser = CommandLineParser.parse(args);

        assertNotNull(parser);
        assertEquals("config.txt", parser.getConfigFile());
        assertEquals("workload.txt", parser.getWorkloadFile());
        assertEquals("network.txt", parser.getNetworkFile());
        assertEquals("node.txt", parser.getNodeFile());
        assertEquals("output/", parser.getOutputDirectory());
    }

    @Test
    void testParseWithMixedOrderOptions() {
        String[] args = {
                "--out", "output/",
                "-c", "config.txt",
                "--node", "node.txt",
                "--wl", "workload.txt",
                "--net", "network.txt"
        };
        CommandLineParser parser = CommandLineParser.parse(args);

        assertNotNull(parser);
        assertEquals("config.txt", parser.getConfigFile());
        assertEquals("workload.txt", parser.getWorkloadFile());
        assertEquals("network.txt", parser.getNetworkFile());
        assertEquals("node.txt", parser.getNodeFile());
        assertEquals("output/", parser.getOutputDirectory());
    }

    @Test
    void testParseWithSomeOptions() {
        String[] args = {
                "-c", "config.txt",
                "--wl", "workload.txt",
                "--out", "output/"
        };
        CommandLineParser parser = CommandLineParser.parse(args);

        assertNotNull(parser);
        assertEquals("config.txt", parser.getConfigFile());
        assertEquals("workload.txt", parser.getWorkloadFile());
        assertNull(parser.getNetworkFile());
        assertNull(parser.getNodeFile());
        assertEquals("output/", parser.getOutputDirectory());
    }

    @Test
    void testParseWithLongConfigOption() {
        String[] args = {"--config", "config.txt"};
        CommandLineParser parser = CommandLineParser.parse(args);

        assertNotNull(parser);
        assertEquals("config.txt", parser.getConfigFile());
        assertNull(parser.getWorkloadFile());
        assertNull(parser.getNetworkFile());
        assertNull(parser.getNodeFile());
        assertNull(parser.getOutputDirectory());
    }

    @Test
    void testParseWithDuplicateOptions() {
        // Verifies that if an option is provided multiple times, the last value is used
        String[] args = {
                "-c", "config1.txt",
                "--wl", "workload1.txt",
                "-c", "config2.txt",
                "--wl", "workload2.txt"
        };
        CommandLineParser parser = CommandLineParser.parse(args);

        assertNotNull(parser);
        assertEquals("config2.txt", parser.getConfigFile());
        assertEquals("workload2.txt", parser.getWorkloadFile());
        assertNull(parser.getNetworkFile());
        assertNull(parser.getNodeFile());
        assertNull(parser.getOutputDirectory());
    }
}