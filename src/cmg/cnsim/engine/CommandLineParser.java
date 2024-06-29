package cmg.cnsim.engine;

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
                    if (++i < args.length) {
                        configFile = args[i];
                    } else {
                        System.out.println("Error: Missing value for argument " + key);
                        printUsage();
                        return null;
                    }
                    break;
                case "--wl":
                    if (++i < args.length) {
                        workloadFile = args[i];
                    } else {
                        System.out.println("Error: Missing value for argument " + key);
                        printUsage();
                        return null;
                    }
                    break;
                case "--net":
                    if (++i < args.length) {
                        networkFile = args[i];
                    } else {
                        System.out.println("Error: Missing value for argument " + key);
                        printUsage();
                        return null;
                    }
                    break;
                case "--node":
                    if (++i < args.length) {
                        nodeFile = args[i];
                    } else {
                        System.out.println("Error: Missing value for argument " + key);
                        printUsage();
                        return null;
                    }
                    break;
                case "--out":
                    if (++i < args.length) {
                        outputDirectory = args[i];
                    } else {
                        System.out.println("Error: Missing value for argument " + key);
                        printUsage();
                        return null;
                    }
                    break;
                default:
                    System.out.println("Unknown option: " + key);
                    printUsage();
                    return null;
            }
        }

        if (configFile == null) {
            System.out.println("Error: Config file is required");
            printUsage();
            return null;
        }

        return new CommandLineParser(configFile, workloadFile, networkFile, nodeFile, outputDirectory);
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
