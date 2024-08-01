package cmg.cnsim.engine.commandline.test;

import cmg.cnsim.engine.commandline.CommandLineParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Properties;

class CommandLineParserTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private CommandLineParser parser;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        parser = new CommandLineParser();
    }

    @Test
    void testParseWithValidArguments() {
        String[] args = {"-c", "config.txt", "--wl", "workload.txt", "--net", "network.txt", "--node", "node.txt", "--out", "output/"};
        Properties properties = parser.parse(args);

        assertNotNull(properties);
        assertEquals("config.txt", properties.getProperty("config.file"));
        assertEquals("workload.txt", properties.getProperty("workload.sampler.file"));
        assertEquals("network.txt", properties.getProperty("net.sampler.file"));
        assertEquals("node.txt", properties.getProperty("node.sampler.file"));
        assertEquals("output/", properties.getProperty("output.directory"));
    }

    @Test
    void testParseWithMissingConfigFile() {
        String[] args = {"--wl", "workload.txt"};
        assertThrows(IllegalArgumentException.class, () -> parser.parse(args));
    }

    @Test
    void testParseWithMissingArgumentValue() {
        String[] args = {"-c", "config.txt", "--wl"};
        assertThrows(IllegalArgumentException.class, () -> parser.parse(args));
    }

    @Test
    void testParseWithUnknownOption() {
        String[] args = {"-c", "config.txt", "--unknown", "value"};
        assertThrows(IllegalArgumentException.class, () -> parser.parse(args));
    }

    @Test
    void testParseWithInvalidLong() {
        String[] args = {"-c", "config.txt", "--ws", "notALong"};
        assertThrows(IllegalArgumentException.class, () -> parser.parse(args));
    }

    @Test
    void testParseWithInvalidLongList() {
        String[] args = {"-c", "config.txt", "--ns", "{1,2,notALong}"};
        assertThrows(IllegalArgumentException.class, () -> parser.parse(args));
    }

    @Test
    void testParseWithOnlyConfigFile() {
        String[] args = {"-c", "config.txt"};
        Properties properties = parser.parse(args);

        assertNotNull(properties);
        assertEquals("config.txt", properties.getProperty("config.file"));
        assertNull(properties.getProperty("workload.sampler.file"));
        assertNull(properties.getProperty("net.sampler.file"));
        assertNull(properties.getProperty("node.sampler.file"));
        assertNull(properties.getProperty("output.directory"));
        assertNull(properties.getProperty("workload.sampler.seed"));
        assertNull(properties.getProperty("node.sampler.seed"));
        assertNull(properties.getProperty("node.sampler.seedUpdateTimes"));
        assertNull( properties.getProperty("net.sampler.seed"));
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
        Properties properties = parser.parse(args);

        assertNotNull(properties);
        assertEquals("config.txt", properties.getProperty("config.file"));
        assertEquals("workload.txt", properties.getProperty("workload.sampler.file"));
        assertEquals("network.txt", properties.getProperty("net.sampler.file"));
        assertEquals("node.txt", properties.getProperty("node.sampler.file"));
        assertEquals("output/", properties.getProperty("output.directory"));
        assertEquals("123456", properties.getProperty("workload.sampler.seed"));
        assertEquals("{1,2,3}", properties.getProperty("node.sampler.seed"));
        assertEquals("{100,200,300}", properties.getProperty("node.sampler.seedUpdateTimes"));
        assertEquals("789012", properties.getProperty("net.sampler.seed"));
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
        Properties properties = parser.parse(args);

        assertNotNull(properties);
        assertEquals("config.txt", properties.getProperty("config.file"));
        assertEquals("workload.txt", properties.getProperty("workload.sampler.file"));
        assertEquals("network.txt", properties.getProperty("net.sampler.file"));
        assertEquals("node.txt", properties.getProperty("node.sampler.file"));
        assertEquals("output/", properties.getProperty("output.directory"));
    }

    @Test
    void testParseWithLongConfigOption() {
        String[] args = {"--config", "config.txt"};
        Properties properties = parser.parse(args);

        assertNotNull(properties);
        assertEquals("config.txt", properties.getProperty("config.file"));
    }

    @Test
    void testParseWithDuplicateOptions() {
        String[] args = {
                "-c", "config1.txt",
                "--wl", "workload1.txt",
                "-c", "config2.txt",
                "--wl", "workload2.txt"
        };
        Properties properties = parser.parse(args);

        assertNotNull(properties);
        assertEquals("config2.txt", properties.getProperty("config.file"));
        assertEquals("workload2.txt", properties.getProperty("workload.sampler.file"));
    }

//    @Test
//    void testParseWithNoArguments() {
//        String[] args = {};
//        assertThrows(IllegalArgumentException.class, () -> parser.parse(args));
//        assertTrue(outContent.toString().contains("Usage: cnsim [options]"));
//    }

//    @Test
//    void testParseWithHelpOption() {
//        String[] args = {"--help"};
//        assertThrows(IllegalArgumentException.class, () -> parser.parse(args));
//        assertTrue(outContent.toString().contains("Usage: cnsim [options]"));
//    }
}