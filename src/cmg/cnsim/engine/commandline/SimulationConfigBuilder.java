package cmg.cnsim.engine.commandline;

import cmg.cnsim.engine.Config;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SimulationConfigBuilder {
    public static SimulationConfig build(CommandLineParser commandLineParser) throws IOException {
        // Load config file
        Config.init(commandLineParser.getConfigFile());
        // Build SimulationConfig
        String configFile = commandLineParser.getConfigFile();
        String workloadFile = getString(commandLineParser.getWorkloadFile(), Config.getPropertyString("workload.sampler.file"));
        String networkFile = getString(commandLineParser.getNetworkFile(), Config.getPropertyString("net.sampler.file"));
        String nodeFile = getString(commandLineParser.getNodeFile(), Config.getPropertyString("node.sampler.file"));
        String outputDirectory = getString(commandLineParser.getOutputDirectory(), "./log/"); // ./log/ is default
        Long workloadSeed = getLong(commandLineParser.getWorkloadSeed(), "workload.sampler.seed");
        List<Long> nodeSeed = commandLineParser.getNodeSeed();
        List<Long> switchTimes = commandLineParser.getSwitchTimes();
        Long networkSeed = getLong(commandLineParser.getNetworkSeed(), "net.sampler.seed");

        // Perform validations
        validateConfig(configFile, workloadFile, networkFile, nodeFile, nodeSeed, switchTimes);

        return new SimulationConfig(configFile, workloadFile, networkFile, nodeFile, outputDirectory,
                workloadSeed, nodeSeed, switchTimes, networkSeed);
    }

    private static String getString(String cmdLineValue, String configValue) {
        if (cmdLineValue != null && !cmdLineValue.isEmpty()) {
            return cmdLineValue;
        } else {
            return configValue;
        }
    }

    private static Long getLong(Long cmdLineValue, String configKey) {
        if (cmdLineValue != null) {
            return cmdLineValue;
        } else if (Config.hasProperty(configKey)) {
            return Config.getPropertyLong(configKey);
        }
        return null;
    }

    private static void validateConfig(String configFile, String workloadFile, String networkFile, String nodeFile,
                                       List<Long> nodeSeed, List<Long> switchTimes) throws IOException {
        if (!new File(configFile).exists()) {
            throw new IOException("Config file does not exist: " + configFile);
        }

        if (workloadFile != null && !new File(workloadFile).exists()) {
            throw new IOException("Workload file does not exist: " + workloadFile);
        }

        if (networkFile != null && !new File(networkFile).exists()) {
            throw new IOException("Network file does not exist: " + networkFile);
        }

        if (nodeFile != null && !new File(nodeFile).exists()) {
            throw new IOException("Node file does not exist: " + nodeFile);
        }

        if (switchTimes != null && !switchTimes.isEmpty() && (nodeSeed == null || nodeSeed.isEmpty())) {
            throw new IllegalArgumentException("Switch times given but no seed list to switch around.");
        }
    }
}