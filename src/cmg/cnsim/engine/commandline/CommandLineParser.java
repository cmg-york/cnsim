package cmg.cnsim.engine.commandline;

import java.util.ArrayList;
import java.util.List;


/**
 * Parses command line arguments for the CNSim simulator.
 * <p>
 * This class handles parsing of command line arguments, including:
 * - Config file path (required)
 * - Workload file path
 * - Network file path
 * - Node file path
 * - Output directory path
 * - Workload seed
 * - Node seed list
 * - Switch times list
 * - Network seed
 * <p>
 * Usage: cnsim -c <config_file> [options]
 */
public final class CommandLineParser {
    private final String configFile;
    private final String workloadFile;
    private final String networkFile;
    private final String nodeFile;
    private final String outputDirectory;
    private final Long workloadSeed;
    private final List<Long> nodeSeed;
    private final List<Long> switchTimes;
    private final Long networkSeed;

    private CommandLineParser(String configFile, String workloadFile, String networkFile, String nodeFile,
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

    /**
     * Parses the given command line arguments.
     *
     * @param args The command line arguments to parse.
     * @return A CommandLineParser object containing the parsed arguments.
     * @throws IllegalArgumentException If required arguments are missing or invalid.
     */
    public static CommandLineParser parse(String[] args) {
        if (args.length == 0 || args[0].equals("-h") || args[0].equals("--help")) {
            printUsage();
//            System.exit(0);
            return null;
        }

        String configFile = null;
        String workloadFile = null;
        String networkFile = null;
        String nodeFile = null;
        String outputDirectory = null;
        Long workloadSeed = null;
        List<Long> nodeSeed = null;
        List<Long> switchTimes = null;
        Long networkSeed = null;

        for (int i = 0; i < args.length; i++) {
            String key = args[i];

            switch (key) {
                case "-c":
                case "--config":
                    configFile = getArgumentValue(args, i, key);
                    i++;
                    break;
                case "--wl":
                    workloadFile = getArgumentValue(args, i, key);
                    i++;
                    break;
                case "--net":
                    networkFile = getArgumentValue(args, i, key);
                    i++;
                    break;
                case "--node":
                    nodeFile = getArgumentValue(args, i, key);
                    i++;
                    break;
                case "--out":
                    outputDirectory = getArgumentValue(args, i, key);
                    i++;
                    break;
                case "--ws":
                case "--workload-seed":
                    workloadSeed = parseLong(getArgumentValue(args, i, key));
                    i++;
                    break;
                case "--ns":
                case "--node-seed":
                    nodeSeed = parseLongList(getArgumentValue(args, i, key));
                    i++;
                    break;
                case "--st":
                case "--switch-times":
                    switchTimes = parseLongList(getArgumentValue(args, i, key));
                    i++;
                    break;
                case "--es":
                case "--net-seed":
                    networkSeed = parseLong(getArgumentValue(args, i, key));
                    i++;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown option: " + key);
            }
        }

        if (configFile == null) {
            printUsage();
            throw new IllegalArgumentException("Config file is required");
//            System.err.println("Error: Config file is required");
//            System.exit(1);
        }

        return new CommandLineParser(configFile, workloadFile, networkFile, nodeFile, outputDirectory,
                workloadSeed, nodeSeed, switchTimes, networkSeed);
    }

    private static String getArgumentValue(String[] args, int index, String key) {
        if (index + 1 < args.length) {
            return args[index + 1];
        } else {
            throw new IllegalArgumentException("Missing value for argument " + key);
        }
    }

    private static Long parseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid long value: " + value);
        }
    }

    private static List<Long> parseLongList(String value) {
        String[] parts = value.replaceAll("[{}]", "").split(",");
        List<Long> result = new ArrayList<>();
        for (String part : parts) {
            result.add(parseLong(part.trim()));
        }
        return result;
    }

    /**
     * Prints the usage instructions for the simulator.
     */
    public static void printUsage() {
        System.out.println("Usage: cnsim -c <config_file> [options]");
        System.out.println("Options:");
        System.out.println("  -c, --config <file>          Configuration file path (required)");
        System.out.println("  --wl <file>                  Workload file path");
        System.out.println("  --net <file>                 Network file path");
        System.out.println("  --node <file>                Node file path");
        System.out.println("  --out <directory>            Output directory path");
        System.out.println("  --ws, --workload-seed <long> Workload seed");
        System.out.println("  --ns, --node-seed <list>     Node seed list (format: {long,long,...})");
        System.out.println("  --st, --switch-times <list>  Switch times list (format: {long,long,...})");
        System.out.println("  --es, --net-seed <long>      Network seed");
        System.out.println("  -h, --help                   Print this help message");
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

    public String getOutputDirectory() {
        return outputDirectory;
    }
}
