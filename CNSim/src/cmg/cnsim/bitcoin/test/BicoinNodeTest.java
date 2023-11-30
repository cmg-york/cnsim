package cmg.cnsim.bitcoin.test;

import cmg.cnsim.bitcoin.BitcoinNode;
import cmg.cnsim.bitcoin.BitcoinNodeFactory;
import cmg.cnsim.bitcoin.Block;
import cmg.cnsim.engine.*;
import cmg.cnsim.engine.network.AbstractNetwork;
import cmg.cnsim.engine.network.RandomEndToEndNetwork;
import cmg.cnsim.engine.node.AbstractNodeFactory;
import cmg.cnsim.engine.node.NodeSet;
import cmg.cnsim.engine.transaction.TransactionGroup;
import cmg.cnsim.engine.transaction.TransactionWorkload;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BicoinNodeTest {
	private AbstractSampler sampler;
	private AbstractNetwork n;
	private Simulation s;
	private NodeSet ns;
	private AbstractNodeFactory nf;


	@BeforeEach
	void setUp() throws Exception {
        
        Config.init(".\\tests\\BitcoinNode\\Case 1 - config.txt");

        
        //Creating sampler
        sampler = new StandardSampler();
        sampler.LoadConfig();

        //Create first the simulator
        s = new Simulation(sampler);
        
        //
        // Network Construction
        // ...
        
        //Create the node factory
        nf = new BitcoinNodeFactory("Honest",s);
		nf = new BitcoinNodeFactory("Malicious",s);
        //Create and populate a NodeSet.
        ns = new NodeSet(nf);
        //ns.addNodes(Parameters.NumofNodes); //a network where all nodes are honest
        ns.addNodes(3);
        //Create a network based on the NodeSet and the sampler
        n = new RandomEndToEndNetwork(ns,sampler);
        //Set this network to the simulator
        s.setNetwork(n);
        
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testPool() {
		
		TransactionWorkload tg = null;
		
		try {
			//TODO: move this to the test folder!
			tg = new TransactionWorkload(".\\tests\\BitcoinNode\\Case 1 - workload.csv",true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		BitcoinNode node = (BitcoinNode) ns.getNodes().get(0);
		TransactionGroup pl; 
		
		//node.setMinSizeToMine(0);
		node.setMinValueToMine(150);
		
		// add a 100 coin transaction. Added to pool and miningpool but not mining
		int transID = 0;
		node.event_NodeReceivesClientTransaction(tg.getTransaction(transID),tg.getTransaction(transID).getCreationTime());
		
		pl = node.getPool();
		assert(pl.getCount() == 1);
		Debug.p("" + pl.getSize());
		assert(pl.getSize() == 1000);
		assert(pl.getValue() == 100);
		assert(pl.contains(1));
		Debug.p("Pool: " + pl.printIDs(","));
		Debug.p("Mining Pool: " + node.getMiningPool().printIDs(","));
		assert(!node.isWorthMining());
		assert(!node.isMining());

		transID = 1;
		node.event_NodeReceivesClientTransaction(tg.getTransaction(transID),tg.getTransaction(transID).getCreationTime());
		assert(pl.getValue() == 149);
		assert(!node.isMining());
		assert(node.getMiningPool().getValue() == 149);
		Debug.p("Pool: " + pl.printIDs(","));
		Debug.p("Mining Pool: " + node.getMiningPool().printIDs(","));
		
		transID = 2;
		node.event_NodeReceivesClientTransaction(tg.getTransaction(transID),tg.getTransaction(transID).getCreationTime());
		assert(pl.getValue() == 151);
		assert(node.isMining());
		assert(node.isWorthMining());
		assert(node.getMiningPool().getValue() == 151);
		Debug.p("Pool: " + pl.printIDs(","));
		Debug.p("Mining Pool: " + node.getMiningPool().printIDs(","));
		
		transID = 3;
		node.event_NodeReceivesClientTransaction(tg.getTransaction(transID),tg.getTransaction(transID).getCreationTime());
		assert(pl.getValue() == 251);
		assert(node.isMining());
		assert(node.isWorthMining());
		assert(node.getMiningPool().getValue() == 249);
		Debug.p("Pool: " + pl.printIDs(","));
		Debug.p("Mining Pool: " + node.getMiningPool().printIDs(","));
		assertEquals("{4,1,2,3}", pl.printIDs(","));
		assertEquals("{4,1,2}", node.getMiningPool().printIDs(","));
		
		transID = 4;
		node.event_NodeReceivesClientTransaction(tg.getTransaction(transID),tg.getTransaction(transID).getCreationTime());
		assert(pl.getValue() ==301);
		assert(node.isMining());
		assert(node.isWorthMining());
		assert(node.getMiningPool().getValue() == 250);
		Debug.p("Pool: " + pl.printIDs(","));
		Debug.p("Mining Pool: " + node.getMiningPool().printIDs(","));
		assertEquals("{4,1,5,2,3}", pl.printIDs(","));
		assertEquals("{4,1,5}", node.getMiningPool().printIDs(","));

		transID = 5;
		node.event_NodeReceivesClientTransaction(tg.getTransaction(transID),tg.getTransaction(transID).getCreationTime());
		assert(pl.getValue() == 381);
		assert(node.isMining());
		assert(node.isWorthMining());
		assert(node.getMiningPool().getValue() == 330);
		Debug.p("Pool: " + pl.printIDs(","));
		Debug.p("Mining Pool: " + node.getMiningPool().printIDs(","));
		assertEquals("{4,6,1,5,2,3}", pl.printIDs(","));
		assertEquals("{4,6,1,5}", node.getMiningPool().printIDs(","));

		transID = 6;
		node.event_NodeReceivesClientTransaction(tg.getTransaction(transID),tg.getTransaction(transID).getCreationTime());
		assert(pl.getValue() == 451);
		assert(node.isMining());
		assert(node.isWorthMining());
		assert(node.getMiningPool().getValue() == 400);
		Debug.p("Pool: " + pl.printIDs(","));
		Debug.p("Mining Pool: " + node.getMiningPool().printIDs(","));
		assertEquals("{4,6,7,1,5,2,3}", pl.printIDs(","));
		assertEquals("{4,6,7,1,5}", node.getMiningPool().printIDs(","));

		
		Block devastator = new Block();
		devastator.addTransaction(tg.getTransaction(0));
		devastator.addTransaction(tg.getTransaction(3));
		devastator.addTransaction(tg.getTransaction(4));
		devastator.addTransaction(tg.getTransaction(5));
		devastator.addTransaction(tg.getTransaction(6));
		
		assertFalse(node.getNextValidationEvent().ignoreEvt());
		node.event_NodeReceivesPropagatedContainer(devastator);
		Debug.p("Pool: " + pl.printIDs(","));
		Debug.p("Mining Pool: " + node.getMiningPool().printIDs(","));
		assertEquals(51,pl.getValue());
		assertFalse(node.isWorthMining());
		assertFalse(node.isMining());
		assert(node.getMiningPool().getValue() == 51);
		assertEquals("{2,3}", pl.printIDs(","));
		assertEquals("{2,3}", node.getMiningPool().printIDs(","));
		assertTrue(node.getNextValidationEvent().ignoreEvt());
		
		
		//Rebuilding
		
		transID = 7;
		node.event_NodeReceivesClientTransaction(tg.getTransaction(transID),tg.getTransaction(transID).getCreationTime());
		assertEquals(100,pl.getValue());
		assertFalse(node.isMining());
		assertFalse(node.isWorthMining());
		assertEquals(100,node.getMiningPool().getValue());
		assertEquals(2400,node.getMiningPool().getSize());
		Debug.p("Pool: " + pl.printIDs(","));
		Debug.p("Mining Pool: " + node.getMiningPool().printIDs(","));
		assertEquals("{8,2,3}", pl.printIDs(","));
		assertEquals("{8,2,3}", node.getMiningPool().printIDs(","));
		
		transID = 8;
		node.event_NodeReceivesClientTransaction(tg.getTransaction(transID),tg.getTransaction(transID).getCreationTime());
		assertEquals(145,pl.getValue());
		assertFalse(node.isMining());
		assertFalse(node.isWorthMining());
		assertEquals(145,node.getMiningPool().getValue());
		assertEquals(2900,node.getMiningPool().getSize());
		Debug.p("Pool: " + pl.printIDs(","));
		Debug.p("Mining Pool: " + node.getMiningPool().printIDs(","));
		assertEquals("{8,9,2,3}", pl.printIDs(","));
		assertEquals("{8,9,2,3}", node.getMiningPool().printIDs(","));

		
		transID = 9;
		node.event_NodeReceivesClientTransaction(tg.getTransaction(transID),tg.getTransaction(transID).getCreationTime());
		assertEquals(151,pl.getValue());
		assertFalse(node.isMining());
		assertFalse(node.isWorthMining());
		assertEquals(149,node.getMiningPool().getValue());
		assertEquals(3000,node.getMiningPool().getSize());
		Debug.p("Pool: " + pl.printIDs(","));
		Debug.p("Mining Pool: " + node.getMiningPool().printIDs(","));
		assertEquals("{8,9,2,10,3}", pl.printIDs(","));
		assertEquals("{8,9,2,10}", node.getMiningPool().printIDs(","));
		
		assertTrue(node.getNextValidationEvent().ignoreEvt());
		
		transID = 10;
		node.event_NodeReceivesClientTransaction(tg.getTransaction(transID),tg.getTransaction(transID).getCreationTime());
		assertEquals(161,pl.getValue());
		assertTrue(node.isMining());
		assertTrue(node.isWorthMining());
		assertEquals(153,node.getMiningPool().getValue());
		assertEquals(2800,node.getMiningPool().getSize());
		Debug.p("Pool: " + pl.printIDs(","));
		Debug.p("Mining Pool: " + node.getMiningPool().printIDs(","));
		assertEquals("{8,9,11,2,10,3}", pl.printIDs(","));
		assertEquals("{8,9,11,2}", node.getMiningPool().printIDs(","));
		
		assertFalse(node.getNextValidationEvent().ignoreEvt());
	}

}
