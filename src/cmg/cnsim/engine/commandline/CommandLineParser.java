package cmg.cnsim.engine.commandline;

public final class CommandLineParser {
    private final String configFile;
    private final String workloadFile;
    private final String networkFile;
    private final String nodeFile;
    private final String outputDirectory;

    private CommandLineParser(String configFile, String workloadFile, String networkFile, String nodeFile, String outputDirectory) {
        this.configFile = configFile;
        this.workloadFile = workloadFile;
        this.networkFile = networkFile;
        this.nodeFile = nodeFile;
        this.outputDirectory = outputDirectory;
    }

    public static CommandLineParser parse(String[] args) {
        if (args.length == 0 || args[0].equals("-h") || args[0].equals("--help")) {
            printUsage();
            return null;
        }

        String configFile = null;
        String workloadFile = null;
        String networkFile = null;
        String nodeFile = null;
        String outputDirectory = null;

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
                default:
                    throw new IllegalArgumentException("Unknown option: " + key);
            }
        }

        if (configFile == null) {
            throw new IllegalArgumentException("Config file is required");
        }

        return new CommandLineParser(configFile, workloadFile, networkFile, nodeFile, outputDirectory);
    }

    private static String getArgumentValue(String[] args, int index, String key) {
        if (index + 1 < args.length) {
            return args[index + 1];
        } else {
            throw new IllegalArgumentException("Missing value for argument " + key);
        }
    }

    public static void printUsage() {
        System.out.println("Usage: cnsim -c <config_file> [options]");
        System.out.println("Options:");
        System.out.println("  -c, --config <file>   Configuration file path (required)");
        System.out.println("  --wl <file>           Workload file path");
        System.out.println("  --net <file>          Network file path");
        System.out.println("  --node <file>         Node file path");
        System.out.println("  --out <directory>     Output directory path");
        System.out.println("  -h, --help            Print this help message");
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
}
