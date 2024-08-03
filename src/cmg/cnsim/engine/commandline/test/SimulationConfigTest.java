package cmg.cnsim.engine.commandline.test;

import cmg.cnsim.engine.SimulationConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Properties;
import java.util.List;

public class SimulationConfigTest {

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

        SimulationConfig.initProperties(properties);
    }

    @AfterEach
    public void tearDown(){
        properties.clear();
    }
    @Test
    public void testGetString() {
        assertEquals("value", SimulationConfig.getPropertyString("test.string"));
        assertNull(SimulationConfig.getPropertyString("non.existent.key"));
    }

    @Test
    public void testGetInt() {
        assertEquals(42, SimulationConfig.getPropertyInt("test.int"));
    }

    @Test
    public void testGetLong() {
        assertEquals(1234567890L, SimulationConfig.getPropertyLong("test.long"));
    }

    @Test
    public void testGetFloat() {
        assertEquals(3.14f, SimulationConfig.getPropertyFloat("test.float"), 0.001);
    }

    @Test
    public void testGetBoolean() {
        assertTrue(SimulationConfig.getPropertyBoolean("test.boolean"));
    }

    @Test
    public void testGetLongList() {
        List<Long> expected = List.of(1L, 2L, 3L, 4L, 5L);
        assertEquals(expected, SimulationConfig.getPropertyLongList("test.longlist"));
    }

    @Test
    public void testHasProperty() {
        assertTrue(SimulationConfig.hasProperty("test.string"));
        assertFalse(SimulationConfig.hasProperty("non.existent.key"));
    }

    @Test
    public void testGetIntWithInvalidValue() {
        properties.setProperty("test.invalid.int", "not an integer");
        SimulationConfig.initProperties(properties);
        assertThrows(NumberFormatException.class, () -> SimulationConfig.getPropertyInt("test.invalid.int"));
    }

    @Test
    public void testGetLongWithInvalidValue() {
        properties.setProperty("test.invalid.long", "not a long");
        SimulationConfig.initProperties(properties);
        assertThrows(NumberFormatException.class, () -> SimulationConfig.getPropertyLong("test.invalid.long"));
    }

    @Test
    public void testGetFloatWithInvalidValue() {
        properties.setProperty("test.invalid.float", "not a float");
        SimulationConfig.initProperties(properties);

        assertThrows(NumberFormatException.class, () -> SimulationConfig.getPropertyFloat("test.invalid.float"));
    }

    @Test
    public void testGetBooleanWithInvalidValue() {
        properties.setProperty("test.invalid.boolean", "not a boolean");
        SimulationConfig.initProperties(properties);
        assertFalse(SimulationConfig.getPropertyBoolean("test.invalid.boolean"));
    }

    @Test
    public void testGetLongListWithInvalidValue() {
        properties.setProperty("test.invalid.longlist", "not a long list");
        SimulationConfig.initProperties(properties);
        assertThrows(NumberFormatException.class, () -> SimulationConfig.getPropertyLongList("test.invalid.longlist"));
    }

    @Test
    public void testGetLongListWithEmptyList() {
        properties.setProperty("test.empty.longlist", "{}");
        SimulationConfig.initProperties(properties);
        assertTrue(SimulationConfig.getPropertyLongList("test.empty.longlist").isEmpty());
    }
}