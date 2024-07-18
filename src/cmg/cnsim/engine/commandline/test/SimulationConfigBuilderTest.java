package cmg.cnsim.engine.commandline.test;

import cmg.cnsim.engine.SimulationConfig;
import cmg.cnsim.engine.SimulationConfigFactory;
import cmg.cnsim.engine.commandline.CommandLineParser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class SimulationConfigBuilderTest {

    private File configFile;
    private File workloadFile;
    private File networkFile;
    private File nodeFile;

    @BeforeEach
    public void setUp() throws IOException {
        // Create temporary files for testing
        configFile = File.createTempFile("config", ".txt");
        workloadFile = File.createTempFile("workload", ".txt");
        networkFile = File.createTempFile("network", ".txt");
        nodeFile = File.createTempFile("node", ".txt");

        // Write some content to the config file
        try (FileWriter writer = new FileWriter(configFile)) {
            writer.write("workload.sampler.file=" + workloadFile.getAbsolutePath() + "\n");
            writer.write("net.sampler.file=" + networkFile.getAbsolutePath() + "\n");
            writer.write("node.sampler.file=" + nodeFile.getAbsolutePath() + "\n");
        }
    }

    @AfterEach
    public void tearDown() {
        // Delete temporary files
        configFile.delete();
        workloadFile.delete();
        networkFile.delete();
        nodeFile.delete();
    }

    @Test
    public void testBuildWithValidConfig() throws IOException {
        String[] args = {"-c", configFile.getAbsolutePath()};
        CommandLineParser parser = CommandLineParser.parse(args);
        SimulationConfigFactory.create(parser);

//        assertNotNull(config);
        assertEquals(workloadFile.getAbsolutePath(), SimulationConfig.getPropertyString("workload.sampler.file"));
        assertEquals(networkFile.getAbsolutePath(), SimulationConfig.getPropertyString("net.sampler.file"));
        assertEquals(nodeFile.getAbsolutePath(), SimulationConfig.getPropertyString("node.sampler.file"));
    }

    @Test
    public void testBuildWithNullCommandLineParser() {
        assertThrows(IllegalArgumentException.class, () -> SimulationConfigFactory.create(null));
    }

    @Test
    public void testBuildWithNullConfigFile() {
        String[] args = {};
        CommandLineParser parser = CommandLineParser.parse(args);
        assertThrows(IllegalArgumentException.class, () -> SimulationConfigFactory.create(parser));
    }

    @Test
    public void testOverrideWithCommandLineArgs() throws IOException {
        File newWorkloadFile = File.createTempFile("newWorkload", ".txt");
        try {
            String[] args = {
                    "-c", configFile.getAbsolutePath(),
                    "--wl", newWorkloadFile.getAbsolutePath(),
                    "--ws", "123",
                    "--ns", "{1,2}",
                    "--st", "{10,20}",
                    "--es", "456"
            };
            CommandLineParser parser = CommandLineParser.parse(args);

            SimulationConfigFactory.create(parser);

            assertEquals(newWorkloadFile.getAbsolutePath(), SimulationConfig.getPropertyString("workload.sampler.file"));
            assertEquals("123", SimulationConfig.getPropertyString("workload.sampler.seed"));
            assertEquals("{1, 2}", SimulationConfig.getPropertyString("node.sampler.seed"));
            assertEquals("10, 20", SimulationConfig.getPropertyString("switch.times"));
            assertEquals("456", SimulationConfig.getPropertyString("net.sampler.seed"));
        } finally {
            newWorkloadFile.delete();
        }
    }

    @Test
    public void testValidateConfigWithMissingFile() {
        workloadFile.delete();
        String[] args = {"-c", configFile.getAbsolutePath()};
        CommandLineParser parser = CommandLineParser.parse(args);
        assertThrows(IOException.class, () -> SimulationConfigFactory.create(parser));
    }

    @Test
    public void testValidateConfigWithSwitchTimesButNoNodeSeed() {
        String[] args = {
                "-c", configFile.getAbsolutePath(),
                "--st", "{10,20}"
        };
        CommandLineParser parser = CommandLineParser.parse(args);
        assertThrows(IllegalArgumentException.class, () -> SimulationConfigFactory.create(parser));
    }
}