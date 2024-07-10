package cmg.cnsim.engine;

import java.util.List;
import java.util.Properties;

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
    private final Properties properties;

    /**
     * Constructs a SimulationConfig with the given properties.
     *
     * @param properties The properties containing the configuration settings.
     */
    public SimulationConfig(Properties properties) {
        this.properties = properties;
    }

    /**
     * Gets a string value from the configuration.
     *
     * @param key The key of the property to retrieve.
     * @return The string value associated with the key.
     */
    public String getPropertyString(String key) {
        return properties.getProperty(key);
    }

    /**
     * Gets an integer value from the configuration.
     *
     * @param key The key of the property to retrieve.
     * @return The integer value associated with the key.
     * @throws NumberFormatException If the value cannot be parsed as an integer.
     */
    public int getPropertyInt(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }

    /**
     * Gets a long value from the configuration.
     *
     * @param key The key of the property to retrieve.
     * @return The long value associated with the key.
     * @throws NumberFormatException If the value cannot be parsed as a long.
     */
    public long getPropertyLong(String key) {
        return Long.parseLong(properties.getProperty(key));
    }

    /**
     * Gets a float value from the configuration.
     *
     * @param key The key of the property to retrieve.
     * @return The float value associated with the key.
     * @throws NumberFormatException If the value cannot be parsed as a float.
     */
    public float getPropertyFloat(String key) {
        return Float.parseFloat(properties.getProperty(key));
    }

    /**
     * Gets a boolean value from the configuration.
     *
     * @param key The key of the property to retrieve.
     * @return The boolean value associated with the key.
     */
    public boolean getPropertyBoolean(String key) {
        return Boolean.parseBoolean(properties.getProperty(key));
    }

    /**
     * Gets a list of long values from the configuration.
     *
     * @param key The key of the property to retrieve.
     * @return A list of long values, or null if the key doesn't exist.
     */
    public List<Long> getPropertyLongList(String key) {
        String value = getPropertyString(key);
        if (value == null) {
            return null;
        }
        return java.util.Arrays.stream(value.replaceAll("[{}]", "").split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Checks if a property exists in the configuration.
     *
     * @param key The key to check.
     * @return true if the property exists, false otherwise.
     */
    public boolean hasProperty(String key) {
        return properties.containsKey(key);
    }

    /**
     * Gets the path of the workload file.
     *
     * @return The path of the workload file.
     */
    public String getWorkloadFile() {
        return getPropertyString("workload.sampler.file");
    }

    /**
     * Gets the path of the network file.
     *
     * @return The path of the network file.
     */
    public String getNetworkFile() {
        return getPropertyString("net.sampler.file");
    }

    /**
     * Gets the path of the node file.
     *
     * @return The path of the node file.
     */
    public String getNodeFile() {
        return getPropertyString("node.sampler.file");
    }

    /**
     * Gets the output directory path.
     *
     * @return The output directory path.
     */
    public String getOutputDirectory() {
        return getPropertyString("output.directory");
    }
}