package cmg.cnsim.engine;

import java.util.List;
import java.util.Properties;

public class SimulationConfig {
    private final Properties properties;

    public SimulationConfig(Properties properties) {
        this.properties = properties;
    }

    public String getString(String key) {
        return properties.getProperty(key);
    }

    public int getInt(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }

    public long getLong(String key) {
        return Long.parseLong(properties.getProperty(key));
    }

    public float getFloat(String key) {
        return Float.parseFloat(properties.getProperty(key));
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(properties.getProperty(key));
    }

    public List<Long> getLongList(String key) {
        String value = getString(key);
        if (value == null) {
            return null;
        }
        return java.util.Arrays.stream(value.replaceAll("[{}]", "").split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .collect(java.util.stream.Collectors.toList());
    }

    public boolean hasProperty(String key) {
        return properties.containsKey(key);
    }
}