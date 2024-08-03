package cmg.cnsim.engine;

import cmg.cnsim.engine.commandline.CommandLineParser;

import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
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
        Properties properties = new Properties();
        properties.putAll(Config.prop);

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

        // Validate and adjust output directory
        String outputDir = properties.getProperty("sim.output.directory");
        if (outputDir == null || outputDir.isEmpty()) {
            outputDir = "./log/";  // Default if not specified
        }

        // Ensure the output directory ends with a separator
        if (!outputDir.endsWith(File.separator)) {
            outputDir += File.separator;
        }

        // Validate the output directory name
        if (!isValidDirectoryName(outputDir)) {
            throw new IllegalArgumentException("Invalid output directory name: " + outputDir);
        }

        // Update the property with the validated and adjusted output directory
        properties.setProperty("sim.output.directory", outputDir);
    }

    private static boolean isValidDirectoryName(String dirPath) {
        try {
            Paths.get(dirPath);
            return true;
        } catch (InvalidPathException | NullPointerException ex) {
            return false;
        }
    }

    private static void validateFileExists(String filePath, String fileDescription) throws IOException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException(fileDescription + " path is null or empty");
        }

        try {
            File file = new File(filePath);
            String currentDir = new File(".").getCanonicalPath();

            // Remove "./" prefix if present
            if (filePath.startsWith("./")) {
                filePath = filePath.substring(2);
            }

            // If it's a relative path, try different resolutions
            if (!file.isAbsolute()) {
                // Try resolving against current directory
                file = new File(currentDir, filePath);

                // If not found, try with "cnsim/" prefix
                if (!file.exists()) {
                    file = new File(currentDir, "cnsim/" + filePath);
                }

                // If still not found, try with "cnsim/resources/" prefix
                if (!file.exists()) {
                    file = new File(currentDir, "cnsim/resources/" + filePath);
                }
            }

            file = file.getCanonicalFile();
            System.out.println("Resolved " + fileDescription + " path: " + file.getAbsolutePath());

            if (!file.exists()) {
                throw new IOException(fileDescription + " does not exist: " + file.getAbsolutePath());
            }

            if (!file.isFile()) {
                throw new IOException(fileDescription + " is not a file: " + file.getAbsolutePath());
            }

            if (!file.canRead()) {
                throw new IOException(fileDescription + " is not readable: " + file.getAbsolutePath());
            }
        } catch (SecurityException e) {
            throw new IOException("Security manager denied access to " + fileDescription + ": " + filePath, e);
        }
    }
}