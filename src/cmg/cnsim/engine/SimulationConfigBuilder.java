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
public class SimulationConfigBuilder {

    /**
     * Builds a SimulationConfig object based on the provided command line arguments and config file.
     *
     * @param commandLineParser The parsed command line arguments.
     * @return A SimulationConfig object containing the combined and validated configuration.
     * @throws IOException If there's an error reading the config file or if required files are missing.
     * @throws IllegalArgumentException If the configuration is invalid.
     */
    public static SimulationConfig build(CommandLineParser commandLineParser) throws IOException {
        if (commandLineParser == null || commandLineParser.getConfigFile() == null) {
            throw new IllegalArgumentException("Config file is required");
        }

        // Load config file
        Config.init(commandLineParser.getConfigFile());

        // Create properties with config file values
        Properties properties = new Properties();
        for (String key : Config.prop.stringPropertyNames()) {
            properties.setProperty(key, Config.prop.getProperty(key));
        }

        // Override with command line arguments
        overrideWithCommandLineArgs(properties, commandLineParser);

        // Perform validations
        validateConfig(properties);

        return new SimulationConfig(properties);
    }

    /**
     * Overrides config file properties with command line arguments.
     *
     * @param properties The properties from the config file.
     * @param commandLineParser The parsed command line arguments.
     */
    private static void overrideWithCommandLineArgs(Properties properties, CommandLineParser commandLineParser) {
        if (commandLineParser.getWorkloadFile() != null) {
            properties.setProperty("workload.sampler.file", commandLineParser.getWorkloadFile());
        }
        if (commandLineParser.getNetworkFile() != null) {
            properties.setProperty("net.sampler.file", commandLineParser.getNetworkFile());
        }
        if (commandLineParser.getNodeFile() != null) {
            properties.setProperty("node.sampler.file", commandLineParser.getNodeFile());
        }
        if (commandLineParser.getOutputDirectory() != null) {
            properties.setProperty("output.directory", commandLineParser.getOutputDirectory());
        }
        if (commandLineParser.getWorkloadSeed() != null) {
            properties.setProperty("workload.sampler.seed", commandLineParser.getWorkloadSeed().toString());
        }
        if (commandLineParser.getNodeSeed() != null) {
            properties.setProperty("node.sampler.seed", commandLineParser.getNodeSeed().toString().replaceAll("\\[|\\]", ""));
        }
        if (commandLineParser.getSwitchTimes() != null) {
            properties.setProperty("switch.times", commandLineParser.getSwitchTimes().toString().replaceAll("\\[|\\]", ""));
        }
        if (commandLineParser.getNetworkSeed() != null) {
            properties.setProperty("net.sampler.seed", commandLineParser.getNetworkSeed().toString());
        }
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

        String switchTimes = properties.getProperty("switch.times");
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