package cmg.cnsim.engine.commandline.test;

import cmg.cnsim.engine.SimulationConfig;
import cmg.cnsim.engine.SimulationConfigFactory;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;



@TestMethodOrder(OrderAnnotation.class)
public class SimulationConfigFactoryTest {

    private File configFile;
    private File workloadFile;
    private File networkFile;
    private File nodeFile;

    @BeforeEach
    public void setUp() throws IOException {
        // Create temporary files for testing
        configFile = File.createTempFile("configTest", ".txt");
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
    @Order(1)
    public void testCreateWithValidConfig() throws IOException {
        String[] args = {"-c", configFile.getAbsolutePath()};
        SimulationConfigFactory.create(args);

        assertEquals(workloadFile.getAbsolutePath(), SimulationConfig.getPropertyString("workload.sampler.file"));
        assertEquals(networkFile.getAbsolutePath(), SimulationConfig.getPropertyString("net.sampler.file"));
        assertEquals(nodeFile.getAbsolutePath(), SimulationConfig.getPropertyString("node.sampler.file"));

        // Ensure these are not set
        assertNull(SimulationConfig.getPropertyString("node.sampler.seedUpdateTimes"));
    }

    @Test
    public void testCreateWithSwitchTimesAndNodeSeed() throws IOException {
        String[] args = {
                "-c", configFile.getAbsolutePath(),
                "--ns", "{1,2,3}",
                "--st", "{100,200,300}"
        };
        SimulationConfigFactory.create(args);

        assertEquals(workloadFile.getAbsolutePath(), SimulationConfig.getPropertyString("workload.sampler.file"));
        assertEquals(networkFile.getAbsolutePath(), SimulationConfig.getPropertyString("net.sampler.file"));
        assertEquals(nodeFile.getAbsolutePath(), SimulationConfig.getPropertyString("node.sampler.file"));
        assertEquals("{1,2,3}", SimulationConfig.getPropertyString("node.sampler.seed"));
        assertEquals("{100,200,300}", SimulationConfig.getPropertyString("node.sampler.seedUpdateTimes"));
    }

//    @Test
//    public void testBuildWithNull() {
//        assertThrows(IllegalArgumentException.class, () -> SimulationConfigFactory.create(null));
//    }
//    @Test
//    public void testCreateWithNoArgs() {
//        String[] args = {};
//        assertThrows(IllegalArgumentException.class, () -> SimulationConfigFactory.create(args));
//    }

    @Test
    public void testCreateWithMissingConfigFile() {
        String[] args = {"-c", "non_existent_config.txt"};
        assertThrows(IOException.class, () -> SimulationConfigFactory.create(args));
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

            SimulationConfigFactory.create(args);

            assertEquals(newWorkloadFile.getAbsolutePath(), SimulationConfig.getPropertyString("workload.sampler.file"));
            assertEquals("123", SimulationConfig.getPropertyString("workload.sampler.seed"));
            assertEquals("{1,2}", SimulationConfig.getPropertyString("node.sampler.seed"));
            assertEquals("{10,20}", SimulationConfig.getPropertyString("node.sampler.seedUpdateTimes"));
            assertEquals("456", SimulationConfig.getPropertyString("net.sampler.seed"));
        } finally {
            newWorkloadFile.delete();
        }
    }

    @Test
    public void testValidateConfigWithMissingFile() {
        workloadFile.delete();
        String[] args = {"-c", configFile.getAbsolutePath()};
        assertThrows(IOException.class, () -> SimulationConfigFactory.create(args));
    }

    @Test
    public void testValidateConfigWithSwitchTimesButNoNodeSeed() throws IOException {
        try (FileWriter writer = new FileWriter(configFile)) {
            writer.write("workload.sampler.file=" + workloadFile.getAbsolutePath() + "\n");
            writer.write("net.sampler.file=" + networkFile.getAbsolutePath() + "\n");
            writer.write("node.sampler.file=" + nodeFile.getAbsolutePath() + "\n");
            writer.write("node.sampler.seedUpdateTimes={10,20}\n");
        }

        String[] args = {"-c", configFile.getAbsolutePath()};
        assertThrows(IllegalArgumentException.class, () -> SimulationConfigFactory.create(args));
    }
}