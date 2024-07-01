package cmg.cnsim.engine;

import cmg.cnsim.engine.commandline.CommandLineParser;

import java.util.Properties;
import java.io.IOException;
import java.io.File;

public class SimulationConfigBuilder {
    public static SimulationConfig build(CommandLineParser commandLineParser) throws IOException {
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

    private static void validateFileExists(String filePath, String fileDescription) throws IOException {
        if (filePath != null && !new File(filePath).exists()) {
            throw new IOException(fileDescription + " does not exist: " + filePath);
        }
    }
}