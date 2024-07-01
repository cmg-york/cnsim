package cmg.cnsim.engine.commandline.test;

import cmg.cnsim.engine.commandline.CommandLineParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

class CommandLineParserTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

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
    void testParseWithInvalidLong() {
        String[] args1 = {"-c", "config.txt", "--ws", "notALong"};
        assertThrows(IllegalArgumentException.class, () -> CommandLineParser.parse(args1));

        String[] args2 = {"-c", "config.txt", "--ws", "1.2"};
        assertThrows(IllegalArgumentException.class, () -> CommandLineParser.parse(args2));
    }

    @Test
    void testParseWithInvalidLongList() {
        String[] args1 = {"-c", "config.txt", "--ns", "{1,2,notALong}"};
        assertThrows(IllegalArgumentException.class, () -> CommandLineParser.parse(args1));

        String[] args2 = {"-c", "config.txt", "--ns", "{1,2,1.2}"};
        assertThrows(IllegalArgumentException.class, () -> CommandLineParser.parse(args2));
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
        assertNull(parser.getWorkloadSeed());
        assertNull(parser.getNodeSeed());
        assertNull(parser.getSwitchTimes());
        assertNull(parser.getNetworkSeed());
    }

    @Test
    void testParseWithAllOptions() {
        String[] args = {
                "-c", "config.txt",
                "--wl", "workload.txt",
                "--net", "network.txt",
                "--node", "node.txt",
                "--out", "output/",
                "--ws", "123456",
                "--ns", "{1,2,3}",
                "--st", "{100,200,300}",
                "--es", "789012"
        };
        CommandLineParser parser = CommandLineParser.parse(args);

        assertNotNull(parser);
        assertEquals("config.txt", parser.getConfigFile());
        assertEquals("workload.txt", parser.getWorkloadFile());
        assertEquals("network.txt", parser.getNetworkFile());
        assertEquals("node.txt", parser.getNodeFile());
        assertEquals("output/", parser.getOutputDirectory());
        assertEquals(123456L, parser.getWorkloadSeed());
        assertEquals(List.of(1L, 2L, 3L), parser.getNodeSeed());
        assertEquals(List.of(100L, 200L, 300L), parser.getSwitchTimes());
        assertEquals(789012L, parser.getNetworkSeed());
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

    @Test
    void testParseWithWorkloadSeed() {
        String[] args = {"-c", "config.txt", "--ws", "123456"};
        CommandLineParser parser = CommandLineParser.parse(args);
        assertEquals(123456L, parser.getWorkloadSeed());
    }

    @Test
    void testParseWithNodeSeed() {
        String[] args = {"-c", "config.txt", "--ns", "{1,2,3}"};
        CommandLineParser parser = CommandLineParser.parse(args);
        assertEquals(List.of(1L, 2L, 3L), parser.getNodeSeed());
    }

    @Test
    void testParseWithSwitchTimes() {
        String[] args = {"-c", "config.txt", "--st", "{100,200,300}"};
        CommandLineParser parser = CommandLineParser.parse(args);
        assertEquals(List.of(100L, 200L, 300L), parser.getSwitchTimes());
    }

    @Test
    void testParseWithNetworkSeed() {
        String[] args = {"-c", "config.txt", "--es", "789012"};
        CommandLineParser parser = CommandLineParser.parse(args);
        assertEquals(789012L, parser.getNetworkSeed());
    }
}