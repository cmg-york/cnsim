package cmg.cnsim.engine.commandline.test;

import cmg.cnsim.engine.SimulationConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Properties;
import java.util.List;

public class SimulationConfigTest {

    private SimulationConfig config;
    private Properties properties;

    @BeforeEach
    public void setUp() {
        properties = new Properties();
        properties.setProperty("test.string", "value");
        properties.setProperty("test.int", "42");
        properties.setProperty("test.long", "1234567890");
        properties.setProperty("test.float", "3.14");
        properties.setProperty("test.boolean", "true");
        properties.setProperty("test.longlist", "{1,2,3,4,5}");

        config = new SimulationConfig(properties);
    }

    @Test
    public void testGetString() {
        assertEquals("value", config.getPropertyString("test.string"));
        assertNull(config.getPropertyString("non.existent.key"));
    }

    @Test
    public void testGetInt() {
        assertEquals(42, config.getPropertyInt("test.int"));
    }

    @Test
    public void testGetLong() {
        assertEquals(1234567890L, config.getPropertyLong("test.long"));
    }

    @Test
    public void testGetFloat() {
        assertEquals(3.14f, config.getFloat("test.float"), 0.001);
    }

    @Test
    public void testGetBoolean() {
        assertTrue(config.getPropertyBoolean("test.boolean"));
    }

    @Test
    public void testGetLongList() {
        List<Long> expected = List.of(1L, 2L, 3L, 4L, 5L);
        assertEquals(expected, config.getLongList("test.longlist"));
    }

    @Test
    public void testHasProperty() {
        assertTrue(config.hasProperty("test.string"));
        assertFalse(config.hasProperty("non.existent.key"));
    }

    @Test
    public void testGetIntWithInvalidValue() {
        properties.setProperty("test.invalid.int", "not an integer");
        assertThrows(NumberFormatException.class, () -> config.getPropertyInt("test.invalid.int"));
    }

    @Test
    public void testGetLongWithInvalidValue() {
        properties.setProperty("test.invalid.long", "not a long");
        assertThrows(NumberFormatException.class, () -> config.getPropertyLong("test.invalid.long"));
    }

    @Test
    public void testGetFloatWithInvalidValue() {
        properties.setProperty("test.invalid.float", "not a float");
        assertThrows(NumberFormatException.class, () -> config.getFloat("test.invalid.float"));
    }

    @Test
    public void testGetBooleanWithInvalidValue() {
        properties.setProperty("test.invalid.boolean", "not a boolean");
        assertFalse(config.getPropertyBoolean("test.invalid.boolean"));
    }

    @Test
    public void testGetLongListWithInvalidValue() {
        properties.setProperty("test.invalid.longlist", "not a long list");
        assertThrows(NumberFormatException.class, () -> config.getLongList("test.invalid.longlist"));
    }

    @Test
    public void testGetLongListWithEmptyList() {
        properties.setProperty("test.empty.longlist", "{}");
        assertTrue(config.getLongList("test.empty.longlist").isEmpty());
    }
}