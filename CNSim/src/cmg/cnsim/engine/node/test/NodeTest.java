package cmg.cnsim.engine.node.test;

import cmg.cnsim.engine.Simulation;
import cmg.cnsim.engine.node.Node;
import org.junit.Assert;
import org.junit.Test;

public class NodeTest {

    @Test
    public void testGetID() {
        Simulation sim = new Simulation(null);
        Node node = new Nodet(sim);

        int expectedID = 1;
        int actualID = node.getID();
        Assert.assertEquals(expectedID, actualID);
    }

    @Test
    public void testSetHashPower() {
        Simulation sim = new Simulation(null);
        Node node = new Nodet(sim);
        float expectedHashPower = 10.0f;
        node.setHashPower(expectedHashPower);
        float actualHashPower = node.getHashPower();
        Assert.assertEquals(expectedHashPower, actualHashPower, 0.001);
    }

    @Test
    public void testSetElectricityCost() {
        Simulation sim = new Simulation(null);
        Node node = new Nodet(sim);
        float expectedElectricityCost = 0.5f;
        node.setElectricityCost(expectedElectricityCost);
        float actualElectricityCost = node.getElectricityCost();
        Assert.assertEquals(expectedElectricityCost, actualElectricityCost, 0.001);
    }

    @Test
    public void testStartAndStopMining() {
        Simulation sim = new Simulation(null);
        Node node = new Nodet(sim);

        // Initially, node should not be mining
        Assert.assertFalse(node.isMining());

        // Start mining and verify the state
        node.startMining();
        Assert.assertTrue(node.isMining());

        // Stop mining and verify the state
        node.stopMining();
        Assert.assertFalse(node.isMining());
    }

    // Add more test cases for other methods as needed
}
