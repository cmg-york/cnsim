package cmg.cnsim.bitcoin.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.PriorityQueue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cmg.cnsim.bitcoin.BitcoinNode;
import cmg.cnsim.bitcoin.BitcoinNodeFactory;
import cmg.cnsim.bitcoin.Block;
import cmg.cnsim.engine.Config;
import cmg.cnsim.engine.Debug;
import cmg.cnsim.engine.NetworkSamplerFactory;
import cmg.cnsim.engine.NodeSamplerFactory;
import cmg.cnsim.engine.Sampler;
import cmg.cnsim.engine.Simulation;
import cmg.cnsim.engine.TransactionSamplerFactory;
import cmg.cnsim.engine.event.Event;
import cmg.cnsim.engine.network.AbstractNetwork;
import cmg.cnsim.engine.network.FileBasedEndToEndNetwork;
import cmg.cnsim.engine.network.RandomEndToEndNetwork;
import cmg.cnsim.engine.node.AbstractNodeFactory;
import cmg.cnsim.engine.node.INode;
import cmg.cnsim.engine.node.NodeSet;
import cmg.cnsim.engine.transaction.Transaction;


class HonestNodeTest {

	private Simulation s; 
	
	
	private Block newBlock(long[] txIds, long parentID) {
		Block block = new Block();
		
		for (int i = 0; i < txIds.length; i++) {
			block.addTransaction(new Transaction(txIds[i],10,10,10)); 
		}
		return block;
	}
	
	@BeforeEach
	void setUp() throws Exception {
        Config.init("./tests/BitcoinNode/HonestNodeTestConfig.txt");
        
        // Creating simulation object
        s = new Simulation(1);

        // Creating Sampler
        Sampler sampler = new Sampler();
        
        // Set Sampler
        s.setSampler(sampler);
        
        //Develop sampler 1: Node Sampler 
        try {
			sampler.setNodeSampler(new NodeSamplerFactory().getSampler(
					Config.getPropertyString("node.sampler.file"),
					Config.getPropertyString("node.sampler.seed"),
					Config.getPropertyString("node.sampler.seedUpdateTimes"),
					sampler,
					s
					));
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        //Develop sampler 2: Network Sampler 
        sampler.setNetworkSampler(new NetworkSamplerFactory().getNetworkSampler(sampler,Config.getPropertyLong("net.sampler.seed")));
        

        //Develop sampler 3: Transaction Sampler 
        try {
			sampler.setTransactionSampler(
					new TransactionSamplerFactory().getSampler(
							Config.getPropertyString("workload.sampler.file"), 
							(Config.hasProperty("workload.sampler.seed") ? Config.getPropertyLong("workload.sampler.seed") : null), 
							sampler));
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        
        // Creating and adding the nodes
        AbstractNodeFactory nf = new BitcoinNodeFactory("Honest", s);
        NodeSet ns = new NodeSet(nf);
        ns.addNodes(Config.getPropertyInt("net.numOfNodes"));
        Debug.p("Nodeset created and added");

        // Creating the network
        AbstractNetwork net = null;
        String netFilePath = Config.getPropertyString("net.sampler.file");
		if (netFilePath != null) {
			try {
				Debug.p("File-based network created.");
				net = new FileBasedEndToEndNetwork(ns, netFilePath);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				net = new RandomEndToEndNetwork(ns, sampler);
				Debug.p("Random network created.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
        s.setNetwork(net);

	}

	@AfterEach
	void tearDown() throws Exception {
	}

	/**
	 * Tests if various series of invocations of event_NodeReceivesPropagatedContainer 
	 * in the correct blockchain structure. 
	 */
	@Test
	void testBlockArrivals() {
		
		BitcoinNode n = (BitcoinNode) s.getNodeSet().getNodes().get(0);
		
		System.out.println("Studying node " + n.getID());
		
		
		//Sending to node block with parent the genesis block (-1) 
		n.event_NodeReceivesPropagatedContainer(newBlock(new long[]{1L, 2L, 3L, 4L, 5L}, -1L));
		String[] expected = {"BlockID,ParentID,BlockHeight,Transactions",
								"1,-1,1,{1,2,3,4,5}"};
		assertArrayEquals(expected, n.getStructure().printStructure());

		//Sending to node block with parent the previous block (-1)
		n.event_NodeReceivesPropagatedContainer(newBlock(new long[]{6L, 7L, 8L, 9L, 10L}, 1L));
		expected = new String[]{"BlockID,ParentID,BlockHeight,Transactions",
				"2,1,2,{6,7,8,9,10}",				
				"1,-1,1,{1,2,3,4,5}"};
		assertArrayEquals(expected, n.getStructure().printStructure());

		
		//Things to check
		// 1. If block overlaps are detected
		// 2. If orphans are handled properly
		// 3. If tips and longest chain is identified properly
	}

	
	/**
	 * Tests if various series of invocations of 
	 * 	event_TransactionPropagation
 	 * 	event_NewTransactionArrival
	 * result in the correct pool formation. 
	 */
	@Test
	void testTxArrivals() {
		BitcoinNode n = (BitcoinNode) s.getNodeSet().getNodes().get(0);
		// Use the following to check e.g., inclusion of a transaction to one of the pools
		n.getMiningPool();
		n.getPool();
		
		//Peek through the event queue to see if transactions are propagated properly.
		// You probably need to iterate over its events and see if events of interest have been added.
		s.getQueue();
	}
	
	
	/**
	 * Tests if various series of invocations of 
	 * 	event_NodeReceivesPropagatedContainer
	 * 	event_TransactionPropagation
	 * 	event_ContainerValidation 
	 * result in the correct blockchain structure and scheduling of events. 
	 */
	@Test
	void testBlockValidations() {
		BitcoinNode n = (BitcoinNode) s.getNodeSet().getNodes().get(0);
		//Peek through the event queue to see if transactions are propagated properly.
		// You probably need to iterate over its events and see if events of interest have been added.
		s.getQueue();
	}

	
	/**
	 * Tests if various series of invocations of 
	 * 	event_ContainerValidation 
	 * result in the correct assignment of cycles (i.e. hashes).  
	 */
	@Test
	void testNodeCycles() {
		BitcoinNode n = (BitcoinNode) s.getNodeSet().getNodes().get(0);
		//The following must calculate the total number of hashes the node performs per second
		// times the time it took it to validate the block
		n.getTotalCycles();
	}
	
}
