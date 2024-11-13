package cmg.cnsim.engine.commandline.test;

import cmg.cnsim.engine.Config;
import cmg.cnsim.engine.ConfigInitializer;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(OrderAnnotation.class)
public class ConfigInitializerTest {

    private static final String TEST_RESOURCES_DIR = "test_resources";
    private static final String CONFIG_NAME = "config.txt";
    private static final String WORKLOAD_NAME = "workload.txt";
    private static final String NETWORK_NAME = "network.txt";
    private static final String NODE_NAME = "node.txt";

    private Path testResourcesPath;
    private Path configPath;
    private Path workloadPath;
    private Path networkPath;
    private Path nodePath;

    @BeforeEach
    public void setUp() throws IOException {
        // Create test_resources directory in the project root
        testResourcesPath = Paths.get(TEST_RESOURCES_DIR).toAbsolutePath();
        Files.createDirectories(testResourcesPath);

        // Set up file paths
        configPath = testResourcesPath.resolve(CONFIG_NAME);
        workloadPath = testResourcesPath.resolve(WORKLOAD_NAME);
        networkPath = testResourcesPath.resolve(NETWORK_NAME);
        nodePath = testResourcesPath.resolve(NODE_NAME);

        // Create the config file with paths to other resources
        try (FileWriter writer = new FileWriter(configPath.toFile())) {
            writer.write("workload.sampler.file=" + workloadPath + "\n");
            writer.write("net.sampler.file=" + networkPath + "\n");
            writer.write("node.sampler.file=" + nodePath + "\n");
        }

        // Create empty files for other resources
        Files.createFile(workloadPath);
        Files.createFile(networkPath);
        Files.createFile(nodePath);

        System.out.println("Test resources created at: " + testResourcesPath);
    }

    @AfterEach
    public void tearDown() throws IOException {
        // Delete the created files and directory
        Files.deleteIfExists(configPath);
        Files.deleteIfExists(workloadPath);
        Files.deleteIfExists(networkPath);
        Files.deleteIfExists(nodePath);
        Files.deleteIfExists(testResourcesPath);
    }

    @Test
    @Order(1)
    public void testCreateWithValidConfig() throws IOException {
        String[] args = {"-c", configPath.toString()};
        ConfigInitializer.initialize(args);

        assertEquals(workloadPath.toString(), Config.getPropertyString("workload.sampler.file"));
        assertEquals(networkPath.toString(), Config.getPropertyString("net.sampler.file"));
        assertEquals(nodePath.toString(), Config.getPropertyString("node.sampler.file"));

        // Ensure these are not set
        assertNull(Config.getPropertyString("node.sampler.seedUpdateTimes"));
    }

    @Test
    public void testCreateWithSwitchTimesAndNodeSeed() throws IOException {
        String[] args = {
                "-c", configPath.toString(),
                "--ns", "{1,2,3}",
                "--st", "{100,200,300}"
        };
        ConfigInitializer.initialize(args);

        assertEquals(workloadPath.toString(), Config.getPropertyString("workload.sampler.file"));
        assertEquals(networkPath.toString(), Config.getPropertyString("net.sampler.file"));
        assertEquals(nodePath.toString(), Config.getPropertyString("node.sampler.file"));
        assertEquals("{1,2,3}", Config.getPropertyString("node.sampler.seed"));
        assertEquals("{100,200,300}", Config.getPropertyString("node.sampler.seedUpdateTimes"));
    }

    @Test
    public void testCreateWithMissingConfigFile() {
        String[] args = {"-c", testResourcesPath.resolve("non_existent_config.txt").toString()};
        assertThrows(IOException.class, () -> ConfigInitializer.initialize(args));
    }

    @Test
    public void testOverrideWithCommandLineArgs() throws IOException {
        Path newWorkloadPath = testResourcesPath.resolve("newWorkload.txt");
        Files.createFile(newWorkloadPath);
        try {
            String[] args = {
                    "-c", configPath.toString(),
                    "--wl", newWorkloadPath.toString(),
                    "--ws", "123",
                    "--ns", "{1,2}",
                    "--st", "{10,20}",
                    "--es", "456"
            };

            ConfigInitializer.initialize(args);

            assertEquals(newWorkloadPath.toString(), Config.getPropertyString("workload.sampler.file"));
            assertEquals("123", Config.getPropertyString("workload.sampler.seed"));
            assertEquals("{1,2}", Config.getPropertyString("node.sampler.seed"));
            assertEquals("{10,20}", Config.getPropertyString("node.sampler.seedUpdateTimes"));
            assertEquals("456", Config.getPropertyString("net.sampler.seed"));
        } finally {
            Files.deleteIfExists(newWorkloadPath);
        }
    }

    @Test
    public void testValidateConfigWithMissingFile() throws IOException {
        Files.delete(workloadPath);
        String[] args = {"-c", configPath.toString()};
        assertThrows(IOException.class, () -> ConfigInitializer.initialize(args));
    }

    @Test
    public void testValidateConfigWithSwitchTimesButNoNodeSeed() throws IOException {
        try (FileWriter writer = new FileWriter(configPath.toFile())) {
            writer.write("workload.sampler.file=" + workloadPath + "\n");
            writer.write("net.sampler.file=" + networkPath + "\n");
            writer.write("node.sampler.file=" + nodePath + "\n");
            writer.write("node.sampler.seedUpdateTimes={10,20}\n");
        }

        String[] args = {"-c", configPath.toString()};
        assertThrows(IllegalArgumentException.class, () -> ConfigInitializer.initialize(args));
    }
}