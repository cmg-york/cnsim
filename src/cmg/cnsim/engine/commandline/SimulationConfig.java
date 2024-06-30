package cmg.cnsim.engine.commandline;

import java.util.List;

public class SimulationConfig {
    private final String configFile;
    private final String workloadFile;
    private final String networkFile;
    private final String nodeFile;
    private final String outputDirectory;
    private final Long workloadSeed;
    private final List<Long> nodeSeed;
    private final List<Long> switchTimes;
    private final Long networkSeed;

    // Constructor
    public SimulationConfig(String configFile, String workloadFile, String networkFile, String nodeFile,
                            String outputDirectory, Long workloadSeed, List<Long> nodeSeed,
                            List<Long> switchTimes, Long networkSeed) {
        this.configFile = configFile;
        this.workloadFile = workloadFile;
        this.networkFile = networkFile;
        this.nodeFile = nodeFile;
        this.outputDirectory = outputDirectory;
        this.workloadSeed = workloadSeed;
        this.nodeSeed = nodeSeed;
        this.switchTimes = switchTimes;
        this.networkSeed = networkSeed;
    }

    public String getConfigFile() {
        return configFile;
    }

    public String getWorkloadFile() {
        return workloadFile;
    }

    public String getNetworkFile() {
        return networkFile;
    }

    public String getNodeFile() {
        return nodeFile;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public Long getWorkloadSeed() {
        return workloadSeed;
    }

    public List<Long> getNodeSeed() {
        return nodeSeed;
    }

    public List<Long> getSwitchTimes() {
        return switchTimes;
    }

    public Long getNetworkSeed() {
        return networkSeed;
    }
}