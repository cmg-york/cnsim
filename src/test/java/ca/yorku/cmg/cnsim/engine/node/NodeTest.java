//package ca.yorku.cmg.cnsim.engine.node;
//
//import ca.yorku.cmg.cnsim.engine.Simulation;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class NodeTest {
//
//    @Test
//    public void testGetID() {
//        Simulation sim = new Simulation(null);
//        Node node = new NodeStub(sim);
//
//        int expectedID = 1;
//        int actualID = node.getID();
//        assertEquals(expectedID, actualID);
//    }
//
//    @Test
//    public void testSetHashPower() {
//        Simulation sim = new Simulation(null);
//        Node node = new NodeStub(sim);
//        float expectedHashPower = 10.0f;
//        node.setHashPower(expectedHashPower);
//        float actualHashPower = node.getHashPower();
//        assertEquals(expectedHashPower, actualHashPower, 0.001);
//    }
//
//    @Test
//    public void testSetElectricityCost() {
//        Simulation sim = new Simulation(null);
//        Node node = new NodeStub(sim);
//        float expectedElectricityCost = 0.5f;
//        node.setElectricityCost(expectedElectricityCost);
//        float actualElectricityCost = node.getElectricityCost();
//        assertEquals(expectedElectricityCost, actualElectricityCost, 0.001);
//    }
//
//    @Test
//    public void testStartAndStopMining() {
//        Simulation sim = new Simulation(null);
//        Node node = new NodeStub(sim);
//
//        // Initially, node should not be mining
//        assertFalse(node.isMining());
//
//        // Start mining and verify the state
//        node.startMining();
//        assertTrue(node.isMining());
//
//        // Stop mining and verify the state
//        node.stopMining();
//        assertFalse(node.isMining());
//    }
//
//    // Add more test cases for other methods as needed
//}
