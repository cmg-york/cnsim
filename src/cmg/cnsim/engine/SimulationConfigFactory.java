package cmg.cnsim.engine;

import cmg.cnsim.engine.commandline.CommandLineParser;

import java.util.Properties;
import java.io.IOException;
import java.io.File;

/**
 * Builds a SimulationConfig by combining command line arguments and configuration file settings.
 * <p>
 * This class is responsible for:
 * - Loading the configuration file
 * - Overriding config file settings with command line arguments
 * - Validating the resulting configuration
 * - Creating a SimulationConfig object
 * <p>
 * Command line arguments always take priority over config file settings.
 */
public class SimulationConfigFactory {

    /**
     * Creates a SimulationConfig object based on the provided command line arguments and config file.
     *
     * @param args The command line arguments.
     * @throws IOException If there's an error reading the config file or if required files are missing.
     * @throws IllegalArgumentException If the configuration is invalid.
     */
    public static void create(String[] args) throws IOException {
        CommandLineParser parser = new CommandLineParser();
        Properties commandLineProperties = parser.parse(args);

        if (commandLineProperties == null) {
            return; // Help was printed or parsing failed
        }

        String configFile = parser.getConfigFile();
        if (configFile == null) {
            throw new IllegalArgumentException("Config file is required");
        }
        validateFileExists(configFile, "Config file");

        // Load config file
        Config.init(configFile);

        // Create properties with config file values
        Properties properties = new Properties(Config.prop);

        // Override with command line arguments
        properties.putAll(commandLineProperties);

        // Perform validations
        validateConfig(properties);

        // Initialize SimulationConfig with the properties
        SimulationConfig.initProperties(properties);
    }

    /**
     * Validates the combined configuration.
     *
     * @param properties The properties to validate.
     * @throws IOException If required files do not exist.
     * @throws IllegalArgumentException If the configuration is invalid.
     */
    private static void validateConfig(Properties properties) throws IOException {
        validateFileExists(properties.getProperty("workload.sampler.file"), "Workload file");
        validateFileExists(properties.getProperty("net.sampler.file"), "Network file");
        validateFileExists(properties.getProperty("node.sampler.file"), "Node file");

        String switchTimes = properties.getProperty("node.sampler.seedUpdateTimes");
        String nodeSeed = properties.getProperty("node.sampler.seed");

        if (switchTimes != null && !switchTimes.isEmpty() && (nodeSeed == null || nodeSeed.isEmpty())) {
            throw new IllegalArgumentException("Switch times given but no seed list to switch around.");
        }
    }

    /**
     * Validates that a file exists at the given path.
     *
     * @param filePath The path to check.
     * @param fileDescription A description of the file for error messages.
     * @throws IOException If the file does not exist.
     */
    private static void validateFileExists(String filePath, String fileDescription) throws IOException {
        if (filePath != null && !new File(filePath).exists()) {
            throw new IOException(fileDescription + " does not exist: " + filePath);
        }
    }
}