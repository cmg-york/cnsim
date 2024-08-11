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
public class ConfigInitializer {

    /**
     * Initialize SimulationConfig with properties based on the provided command line arguments and config file.
     *
     * @param args The command line arguments.
     * @throws IOException If there's an error reading the config file or if required files are missing.
     * @throws IllegalArgumentException If the configuration is invalid.
     */
    public static void initialize(String[] args) throws IOException {
        CommandLineParser parser = new CommandLineParser();
        Properties commandLineProperties = parser.parse(args);

        if (commandLineProperties == null) {
            return; // Help was printed or parsing failed
        }

        String configFile = parser.getConfigFile();
        if (configFile == null) {
            throw new IllegalArgumentException("Config file is required");
        }
        String resolvedConfigFile = validateFileExists(configFile, "Config file");

        // Load config file
        Config.init(resolvedConfigFile);

        // Create properties with config file values
        Properties properties = new Properties();
        properties.putAll(Config.prop);

        // Override with command line arguments
        properties.putAll(commandLineProperties);

        // Perform validations
        validateConfig(properties);

        // Initialize Config prop with the properties
        Config.prop.putAll(properties);
    }

    /**
     * Validates the combined configuration.
     *
     * @param properties The properties to validate.
     * @throws IOException If required files do not exist.
     * @throws IllegalArgumentException If the configuration is invalid.
     */
    private static void validateConfig(Properties properties) throws IOException {
        // Validate file paths and resolve them if relative
        validateFileExists(properties, "workload.sampler.file", "Workload file");
        validateFileExists(properties, "net.sampler.file", "Network file");
        validateFileExists(properties, "node.sampler.file", "Node file");

        // Validate dependency between switch times and seed list
        validatePropertyDependency(properties, "node.sampler.seedUpdateTimes", "node.sampler.seed",
                "Switch times given but no seed list to switch around.");

        // Validate and adjust output directory
        validateDirectory(properties, "sim.output.directory", "./log/");

        // Validate sim.numSimulations
        validateIntProperty(properties, "sim.numSimulations", 1, 1);
    }

    private static void validateIntProperty(Properties properties, String key, int defaultValue, int minValue) {
        String value = properties.getProperty(key);
        int result;

        if (value == null || value.trim().isEmpty()) {
            result = defaultValue;
        } else {
            try {
                result = Integer.parseInt(value.trim());
                if (result < minValue) {
                    throw new IllegalArgumentException(key + " must be at least " + minValue + ", but was " + result);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(key + " must be a valid integer, but was '" + value + "'");
            }
        }
        properties.setProperty(key, Long.toString(result));
    }

    private static void validateDirectory(Properties properties, String key, String defaultPath) {
        String dir = properties.getProperty(key);
        if (dir == null || dir.isEmpty()) {
            dir = defaultPath;  // Default if not specified
        }

        // Ensure the directory ends with a separator
        if (!dir.endsWith(File.separator)) {
            dir += File.separator;
        }

        // Validate the directory name
        if (!isValidDirectoryName(dir)) {
            throw new IllegalArgumentException("Invalid directory name: " + dir);
        }

        properties.setProperty(key, dir);
    }

    private static void validatePropertyDependency(Properties properties, String key1, String key2, String errorMessage) {
        String value1 = properties.getProperty(key1);
        String value2 = properties.getProperty(key2);

        if ((value1 != null && !value1.isEmpty()) && (value2 == null || value2.isEmpty())) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private static boolean isValidDirectoryName(String dirPath) {
        try {
            Paths.get(dirPath);
            return true;
        } catch (InvalidPathException | NullPointerException ex) {
            return false;
        }
    }

    private static String validateFileExists(String filePath, String fileDescription) throws IOException {
        return validateFileExistsInternal(null, null, filePath, fileDescription);
    }

    private static void validateFileExists(Properties properties, String key, String fileDescription) throws IOException {
        validateFileExistsInternal(properties, key, properties.getProperty(key), fileDescription);
    }

    private static String validateFileExistsInternal(Properties properties, String key, String filePath, String fileDescription) throws IOException {
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
            String resolvedPath = file.getAbsolutePath();
            System.out.println("Resolved " + fileDescription + " path: " + resolvedPath);

            if (!file.exists()) {
                throw new IOException(fileDescription + " does not exist: " + resolvedPath);
            }

            if (!file.isFile()) {
                throw new IOException(fileDescription + " is not a file: " + resolvedPath);
            }

            if (!file.canRead()) {
                throw new IOException(fileDescription + " is not readable: " + resolvedPath);
            }

            // Update the property with the resolved path if properties and key are provided
            if (properties != null && key != null) {
                properties.setProperty(key, resolvedPath);
            }

            // Return the resolved path
            return resolvedPath;

        } catch (SecurityException e) {
            throw new IOException("Security manager denied access to " + fileDescription + ": " + filePath, e);
        }
    }
}