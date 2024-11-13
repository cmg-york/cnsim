package cmg.cnsim.engine.network.test;

import org.junit.Before;
import org.junit.Test;

import cmg.cnsim.bitcoin.BitcoinNodeFactory;
import cmg.cnsim.engine.Config;
import cmg.cnsim.engine.Sampler;
import cmg.cnsim.engine.Simulation;
import cmg.cnsim.engine.network.AbstractNetwork;
import cmg.cnsim.engine.network.FileBasedEndToEndNetwork;
import cmg.cnsim.engine.network.NetworkFactory;
import cmg.cnsim.engine.node.NodeSet;

public class NetworkFactoryTest {
	Simulation sim;
	NodeSet ns;
	Sampler samp;
	
	@Before
	void setup() {
		sim = new Simulation();
		samp = new Sampler();
		sim.setSampler(samp);
		ns = new NodeSet(new BitcoinNodeFactory("Honest", sim));
	}
	
	@Test
	void testCreateNetworkFromFile() {
		AbstractNetwork net = NetworkFactory.createNetwork(ns, samp);
		assertTrue(net instanceof FileBasedEndToEndNetwork);
	}
	
	// TODO create working test for this
	void testCreateRandomNetwork() {
		// TODO set config property for file location to null, change 
		// factory method to accept file location, or some other option
		AbstractNetwork net = NetworkFactory.createNetwork(ns, samp);
		assertTrue(net instanceof RandomEndToEndNetwork);
	}
}
