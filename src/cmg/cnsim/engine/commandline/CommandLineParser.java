package cmg.cnsim.engine.commandline;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Parses command line arguments for the CNSim simulator.
 * <p>
 * This class handles parsing of command line arguments, including:
 * - Config file path (required)
 * - Number of simulations
 * - Workload file path
 * - Network file path
 * - Node file path
 * - Output directory path
 * - Workload seed
 * - Node seed list
 * - Switch times list
 * - Network seed
 * <p>
 * Usage: cnsim [options]
 */
public final class CommandLineParser {
    private final Map<String, Field> optionFields = new HashMap<>();

    @CommandLineOption(
            key = "config.file",
            description = "Configuration file path",
            argument = "<file>",
            required = true,
            aliases = {"-c", "--config"}
    )
    private String configFile;

    @CommandLineOption(
            key = "sim.numSimulations",
            description = "Number of simulations (Default 1)",
            argument = "<long>",
            aliases = {"-m", "--sims"}
    )
    private Long numSimulations;

    @CommandLineOption(
            key = "workload.sampler.file",
            description = "Workload file path",
            argument = "<file>",
            aliases = "--wl"
    )
    private String workloadFile;

    @CommandLineOption(
            key = "net.sampler.file",
            description = "Network file path",
            argument = "<file>",
            aliases = "--net"
    )
    private String networkFile;

    @CommandLineOption(
            key = "node.sampler.file",
            description = "Node file path",
            argument = "<file>",
            aliases = "--node"
    )
    private String nodeFile;

    @CommandLineOption(
            key = "sim.output.directory",
            description = "Output directory path",
            argument = "<directory>",
            aliases = "--out"
    )
    private String outputDirectory;

    @CommandLineOption(
            key = "workload.sampler.seed",
            description = "Workload seed",
            argument = "<long>",
            aliases = {"--ws", "--workload-seed"}
    )
    private Long workloadSeed;

    @CommandLineOption(
            key = "node.sampler.seed",
            description = "Node seed list (format: {long,long,...})",
            argument = "<list>",
            aliases = {"--ns", "--node-seed"}
    )
    private List<Long> nodeSeed;

    @CommandLineOption(
            key = "node.sampler.seedUpdateTimes",
            description = "Switch times list (format: {long,long,...})",
            argument = "<list>",
            aliases = {"--st", "--switch-times"}
    )
    private List<Long> switchTimes;

    @CommandLineOption(
            key = "net.sampler.seed",
            description = "Network seed",
            argument = "<long>",
            aliases = {"--es", "--net-seed"}
    )
    private Long networkSeed;


    /**
     * Constructs a new CommandLineParser instance.
     * <p>
     * This constructor initializes the CommandLineParser by scanning all declared fields
     * of the class for the {@link CommandLineOption} annotation. For each annotated field,
     * it populates the optionFields map with entries for both the main key and all aliases
     * of the option, associating them with the corresponding Field object.
     * </p>
     */
    public CommandLineParser() {
        for (Field field : getClass().getDeclaredFields()) {
            CommandLineOption annotation = field.getAnnotation(CommandLineOption.class);
            if (annotation != null) {
                optionFields.put(annotation.key(), field);
                for (String alias : annotation.aliases()) {
                    optionFields.put(alias, field);
                }
            }
        }
    }

    /**
     * Parses the given command line arguments.
     *
     * @param args The command line arguments to parse.
     * @return A Properties object containing the parsed arguments.
     * @throws IllegalArgumentException If (1) required arguments are missing or invalid,
     * (2) args has an unknown option, or (3) there is a missing value for argument.
     */
    public Properties parse(String[] args) {
        Properties properties = new Properties();

        if (args == null || args.length == 0 || args[0].equals("-h") || args[0].equals("--help")) {
            printUsage();
            System.exit(0);
            return null;
        }
        parseArguments(args, properties);
        validateRequiredOptions();

        System.out.println(properties);
        return properties;
    }

    /**
     * Parses the arguments and populates the properties object.
     */
    private void parseArguments(String[] args, Properties properties) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            Field field = optionFields.get(arg);
            if (field != null) {
                String value = getArgumentValue(args, ++i);
                setFieldValue(field, value, properties);
            } else {
                throw new IllegalArgumentException("Unknown option: " + arg);
            }
        }
    }

    /**
     * Sets the value of a field and adds it to the properties.
     */
    private void setFieldValue(Field field, String value, Properties properties) {
        try {
            Object parsedValue = parseValue(field.getType(), value);
            field.setAccessible(true);
            field.set(this, parsedValue);
            properties.setProperty(getKeyForField(field), value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error setting field value", e);
        }
    }

    /**
     * Gets the argument value for a given option.
     */
    private String getArgumentValue(String[] args, int index) {
        if (index < args.length) {
            return args[index];
        } else {
            throw new IllegalArgumentException("Missing value for argument " + args[index - 1]);
        }
    }

    /**
     * Validates that all required options are present.
     */
    private void validateRequiredOptions() {
        for (Field field : getClass().getDeclaredFields()) {
            CommandLineOption annotation = field.getAnnotation(CommandLineOption.class);
            if (annotation != null && annotation.required()) {
                try {
                    field.setAccessible(true);
                    if (field.get(this) == null) {
                        throw new IllegalArgumentException("Required option missing: " + annotation.key());
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Error accessing field", e);
                }
            }
        }
    }

    /**
     * Gets the key for a given field from its annotation.
     */
    private String getKeyForField(Field field) {
        return field.getAnnotation(CommandLineOption.class).key();
    }

    /**
     * Prints the usage instructions for the simulator.
     */
    public void printUsage() {
        System.out.println("Usage: cnsim [options]");
        System.out.println("Options:");
        for (Field field : getClass().getDeclaredFields()) {
            CommandLineOption annotation = field.getAnnotation(CommandLineOption.class);
            if (annotation != null) {
                System.out.println(getUsageString(annotation));
            }
        }
        System.out.println("  -h, --help\t\tPrint this help message");
    }

    private String getUsageString(CommandLineOption option) {
        StringBuilder sb = new StringBuilder();
        sb.append("  ");
        if (option.aliases().length > 0) {
            sb.append(String.join(", ", option.aliases()));
        }
        sb.append(" " + option.argument()).append("\t\t").append(option.description() + " - " + option.key());
        if (option.required()) {
            sb.append(" (required)");
        }
        return sb.toString();
    }

    private Object parseValue(Class<?> type, String value) {
        if (type == String.class) {
            return value;
        } else if (type == Long.class) {
            return Long.parseLong(value);
        } else if (type == List.class) {
            return parseLongList(value);
        }
        throw new IllegalArgumentException("Unsupported type: " + type);
    }

    private List<Long> parseLongList(String value) {
        String[] parts = value.replaceAll("[{}\\[\\]]", "").split(",");
        List<Long> result = new ArrayList<>();
        for (String part : parts) {
            result.add(Long.parseLong(part.trim()));
        }
        return result;
    }

    public String getConfigFile() { return configFile; }
    public String getWorkloadFile() { return workloadFile; }
    public String getNetworkFile() {return networkFile; }
    public String getNodeFile(){ return nodeFile; }
    public Long getWorkloadSeed() { return workloadSeed; }
    public List<Long> getNodeSeed() { return nodeSeed; }
    public List<Long> getSwitchTimes() { return switchTimes; }
    public Long getNetworkSeed() { return networkSeed; }
    public String getOutputDirectory() { return outputDirectory; }
}
