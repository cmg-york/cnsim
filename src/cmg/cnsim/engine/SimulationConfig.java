package cmg.cnsim.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Holds the configuration settings for the CNSim simulator.
 * <p>
 * This class provides methods to access various configuration properties, including:
 * - String, integer, long, float, and boolean values
 * - Lists of long values
 * - Specific file paths (workload, network, node)
 * - Output directory
 * <p>
 * It encapsulates all configuration data needed to run a simulation.
 */
public class SimulationConfig {
    private static Properties properties;

    /**
     * Initializes the SimulationConfig using command line arguments.
     *
     * @param args The command line arguments.
     * @throws RuntimeException If there's an error setting up the simulation.
     */
    public static void initialize(String[] args) {
        try {
            SimulationConfigFactory.create(args);
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error setting up simulation");
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize SimulationConfig", e);
        }
    }


    /**
     * Initializes the properties for SimulationConfig.
     * This method should be called by SimulationConfigFactory after creating the properties.
     *
     * @param props The properties containing the configuration settings.
     */
    public static void initProperties(Properties props) {
        properties = new Properties();
        properties.putAll(props);
    }

    /**
     * Gets a string value from the configuration.
     *
     * @param key The key of the property to retrieve.
     * @return The string value associated with the key.
     */
    public static String getPropertyString(String key) {
        return properties.getProperty(key);
    }

    /**
     * Gets an integer value from the configuration.
     *
     * @param key The key of the property to retrieve.
     * @return The integer value associated with the key.
     * @throws NumberFormatException If the value cannot be parsed as an integer.
     */
    public static int getPropertyInt(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }

    /**
     * Gets a long value from the configuration.
     *
     * @param key The key of the property to retrieve.
     * @return The long value associated with the key.
     * @throws NumberFormatException If the value cannot be parsed as a long.
     */
    public static long getPropertyLong(String key) {
        return Long.parseLong(properties.getProperty(key));
    }

    /**
     * Gets a float value from the configuration.
     *
     * @param key The key of the property to retrieve.
     * @return The float value associated with the key.
     * @throws NumberFormatException If the value cannot be parsed as a float.
     */
    public static float getPropertyFloat(String key) {
        return Float.parseFloat(properties.getProperty(key));
    }

    /**
     * Gets a boolean value from the configuration.
     *
     * @param key The key of the property to retrieve.
     * @return The boolean value associated with the key.
     */
    public static boolean getPropertyBoolean(String key) {
        return Boolean.parseBoolean(properties.getProperty(key));
    }

    /**
     * Gets a list of long values from the configuration.
     *
     * @param key The key of the property to retrieve.
     * @return A list of long values, or null if the key doesn't exist.
     */
    public static List<Long> getPropertyLongList(String key) {
        String value = getPropertyString(key);
        if (value == null) {
            return null;
        }
        value = value.replaceAll("[{}]", "").trim();
        if (value.isEmpty()) {
            return new ArrayList<>(); // Return an empty list for "{}"
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty()) // Skip empty strings
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    /**
     * Checks if a property exists in the configuration.
     *
     * @param key The key to check.
     * @return true if the property exists, false otherwise.
     */
    public static boolean hasProperty(String key) {
        return properties.containsKey(key);
    }

    /**
     * Gets the path of the workload file.
     *
     * @return The path of the workload file.
     */
    public static String getWorkloadFile() {
        return getPropertyString("workload.sampler.file");
    }

    /**
     * Gets the path of the network file.
     *
     * @return The path of the network file.
     */
    public static String getNetworkFile() {
        return getPropertyString("net.sampler.file");
    }

    /**
     * Gets the path of the node file.
     *
     * @return The path of the node file.
     */
    public static String getNodeFile() {
        return getPropertyString("node.sampler.file");
    }

    /**
     * Gets the output directory path.
     *
     * @return The output directory path.
     */
    public static String getOutputDirectory() {
        return getPropertyString("sim.output.directory");
    }
}