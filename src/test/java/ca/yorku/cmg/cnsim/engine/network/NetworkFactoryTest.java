//// Compilation error
//package ca.yorku.cmg.cnsim.engine.network;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import ca.yorku.cmg.cnsim.bitcoin.BitcoinNodeFactory;
//import ca.yorku.cmg.cnsim.engine.Config;
//import ca.yorku.cmg.cnsim.engine.Sampler;
//import ca.yorku.cmg.cnsim.engine.Simulation;
//import ca.yorku.cmg.cnsim.engine.network.AbstractNetwork;
//import ca.yorku.cmg.cnsim.engine.network.FileBasedEndToEndNetwork;
//import ca.yorku.cmg.cnsim.engine.network.NetworkFactory;
//import ca.yorku.cmg.cnsim.engine.node.NodeSet;
//
//public class NetworkFactoryTest {
//	Simulation sim;
//	NodeSet ns;
//	Sampler samp;
//
//	@Before
//	void setup() {
//		sim = new Simulation();
//		samp = new Sampler();
//		sim.setSampler(samp);
//		ns = new NodeSet(new BitcoinNodeFactory("Honest", sim));
//	}
//
//	@Test
//	void testCreateNetworkFromFile() {
//		AbstractNetwork net = NetworkFactory.createNetwork(ns, samp);
//		assertTrue(net instanceof FileBasedEndToEndNetwork);
//	}
//
//	// TODO create working test for this
//	void testCreateRandomNetwork() {
//		// TODO set config property for file location to null, change
//		// factory method to accept file location, or some other option
//		AbstractNetwork net = NetworkFactory.createNetwork(ns, samp);
//		assertTrue(net instanceof RandomEndToEndNetwork);
//	}
//}
