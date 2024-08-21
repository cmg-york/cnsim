//package ca.yorku.cmg.cnsim.bitcoin.test;
//
//import ca.yorku.cmg.cnsim.bitcoin.BitcoinNode;
//import ca.yorku.cmg.cnsim.bitcoin.BitcoinNodeFactory;
//import ca.yorku.cmg.cnsim.bitcoin.Block;
//import ca.yorku.cmg.cnsim.bitcoin.MaliciousNodeBehavior;
//import ca.yorku.cmg.cnsim.engine.*;
//import ca.yorku.cmg.cnsim.engine.network.AbstractNetwork;
//import ca.yorku.cmg.cnsim.engine.network.RandomEndToEndNetwork;
//import ca.yorku.cmg.cnsim.engine.node.AbstractNodeFactory;
//import ca.yorku.cmg.cnsim.engine.node.INode;
//import ca.yorku.cmg.cnsim.engine.node.NodeSet;
//import ca.yorku.cmg.cnsim.engine.transaction.Transaction;
//import ca.yorku.cmg.cnsim.engine.transaction.TransactionGroup;
//import ca.yorku.cmg.cnsim.engine.transaction.TransactionWorkload;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class BitcoinNodeTest {
//	private AbstractSampler sampler;
//	private AbstractNetwork n;
//	private Simulation s;
//	private NodeSet ns;
//	private AbstractNodeFactory nf;
//
//
//
//	@BeforeEach
//	void setUp() throws Exception {
//		Config.init("./CNSim/tests/BitcoinNode/Case 1 - config.txt");
//
//		//Creating sampler
//		sampler = new StandardSampler();
//		sampler.LoadConfig();
//
//		//Create first the simulator
//		s = new Simulation(sampler);
//
//		//
//		// Network Construction
//		// ...
//
//		//Create the node factory
//		nf = new BitcoinNodeFactory("Honest",s);
//		//nf = new BitcoinNodeFactory("Malicious",s);
//		//Create and populate a NodeSet.
//		ns = new NodeSet(nf);
//		//ns.addNodes(Parameters.NumofNodes); //a network where all nodes are honest
//		ns.addNodes(3);
//		//Create a network based on the NodeSet and the sampler
//		n = new RandomEndToEndNetwork(ns,sampler);
//		//Set this network to the simulator
//		s.setNetwork(n);
//
//	}
//
//	@AfterEach
//	void tearDown() throws Exception {
//	}
//
//	@Test
//	void testPool() {
//
//		TransactionWorkload tg = null;
//
//		try {
//			//TODO: move this to the test folder!
//			tg = new TransactionWorkload("./CNSim/tests/BitcoinNode/Case 1 - workload.csv",true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		BitcoinNode node = (BitcoinNode) ns.getNodes().get(0);
//		node.setBehavior(INode.BehaviorType.HONEST);
//		TransactionGroup pl;
//
//		//node.setMinSizeToMine(0);
//		node.setMinValueToMine(150);
//
//		// add a 100 coin transaction. Added to pool and miningpool but not mining
//		int transID = 0;
//		node.event_NodeReceivesClientTransaction(tg.getTransaction(transID),tg.getTransaction(transID).getCreationTime());
//
//		pl = node.getPool();
//		assert(pl.getCount() == 1);
//		Debug.p("" + pl.getSize());
//		assert(pl.getSize() == 1000);
//		assert(pl.getValue() == 100);
//		assert(pl.contains(1));
//		Debug.p("Pool: " + pl.printIDs(","));
//		Debug.p("Mining Pool: " + node.getMiningPool().printIDs(","));
//		assert(!node.isWorthMining());
//		assert(!node.isMining());
//
//		transID = 1;
//		node.event_NodeReceivesClientTransaction(tg.getTransaction(transID),tg.getTransaction(transID).getCreationTime());
//		assert(pl.getValue() == 149);
//		assert(!node.isMining());
//		assert(node.getMiningPool().getValue() == 149);
//		Debug.p("Pool: " + pl.printIDs(","));
//		Debug.p("Mining Pool: " + node.getMiningPool().printIDs(","));
//
//		transID = 2;
//		node.event_NodeReceivesClientTransaction(tg.getTransaction(transID),tg.getTransaction(transID).getCreationTime());
//		assert(pl.getValue() == 151);
//		assert(node.isMining());
//		assert(node.isWorthMining());
//		assert(node.getMiningPool().getValue() == 151);
//		Debug.p("Pool: " + pl.printIDs(","));
//		Debug.p("Mining Pool: " + node.getMiningPool().printIDs(","));
//
//		transID = 3;
//		node.event_NodeReceivesClientTransaction(tg.getTransaction(transID),tg.getTransaction(transID).getCreationTime());
//		assert(pl.getValue() == 251);
//		assert(node.isMining());
//		assert(node.isWorthMining());
//		assert(node.getMiningPool().getValue() == 249);
//		Debug.p("Pool: " + pl.printIDs(","));
//		Debug.p("Mining Pool: " + node.getMiningPool().printIDs(","));
//		assertEquals("{4,1,2,3}", pl.printIDs(","));
//		assertEquals("{4,1,2}", node.getMiningPool().printIDs(","));
//
//		transID = 4;
//		node.event_NodeReceivesClientTransaction(tg.getTransaction(transID),tg.getTransaction(transID).getCreationTime());
//		assert(pl.getValue() ==301);
//		assert(node.isMining());
//		assert(node.isWorthMining());
//		assert(node.getMiningPool().getValue() == 250);
//		Debug.p("Pool: " + pl.printIDs(","));
//		Debug.p("Mining Pool: " + node.getMiningPool().printIDs(","));
//		assertEquals("{4,1,5,2,3}", pl.printIDs(","));
//		assertEquals("{4,1,5}", node.getMiningPool().printIDs(","));
//
//		transID = 5;
//		node.event_NodeReceivesClientTransaction(tg.getTransaction(transID),tg.getTransaction(transID).getCreationTime());
//		assert(pl.getValue() == 381);
//		assert(node.isMining());
//		assert(node.isWorthMining());
//		assert(node.getMiningPool().getValue() == 330);
//		Debug.p("Pool: " + pl.printIDs(","));
//		Debug.p("Mining Pool: " + node.getMiningPool().printIDs(","));
//		assertEquals("{4,6,1,5,2,3}", pl.printIDs(","));
//		assertEquals("{4,6,1,5}", node.getMiningPool().printIDs(","));
//
//		transID = 6;
//		node.event_NodeReceivesClientTransaction(tg.getTransaction(transID),tg.getTransaction(transID).getCreationTime());
//		assert(pl.getValue() == 451);
//		assert(node.isMining());
//		assert(node.isWorthMining());
//		assert(node.getMiningPool().getValue() == 400);
//		Debug.p("Pool: " + pl.printIDs(","));
//		Debug.p("Mining Pool: " + node.getMiningPool().printIDs(","));
//		assertEquals("{4,6,7,1,5,2,3}", pl.printIDs(","));
//		assertEquals("{4,6,7,1,5}", node.getMiningPool().printIDs(","));
//
//
//		Block devastator = new Block();
//		devastator.addTransaction(tg.getTransaction(0));
//		devastator.addTransaction(tg.getTransaction(3));
//		devastator.addTransaction(tg.getTransaction(4));
//		devastator.addTransaction(tg.getTransaction(5));
//		devastator.addTransaction(tg.getTransaction(6));
//
//
//		//node.setBehaviorStrategy(new HonestNodeBehavior(node));
//		assertFalse(node.getNextValidationEvent().ignoreEvt());
//		node.event_NodeReceivesPropagatedContainer(devastator);
//		pl = node.getMiningPool();
//		Debug.p("Pool: " + pl.printIDs(","));
//		Debug.p("Mining Pool: " + node.getMiningPool().printIDs(","));
//		assertEquals(51,pl.getValue());
//		assertFalse(node.isWorthMining());
//		assertFalse(node.isMining());
//		assert(node.getMiningPool().getValue() == 51);
//		assertEquals("{2,3}", pl.printIDs(","));
//		assertEquals("{2,3}", node.getMiningPool().printIDs(","));
//		assertTrue(node.getNextValidationEvent().ignoreEvt());
//
//
//		//Rebuilding
//
//		transID = 7;
//		node.event_NodeReceivesClientTransaction(tg.getTransaction(transID),tg.getTransaction(transID).getCreationTime());
//		pl = node.getPool();
//		assertEquals(100,pl.getValue());
//		assertFalse(node.isMining());
//		assertFalse(node.isWorthMining());
//		assertEquals(100,node.getMiningPool().getValue());
//		assertEquals(2400,node.getMiningPool().getSize());
//		Debug.p("Pool: " + pl.printIDs(","));
//		Debug.p("Mining Pool: " + node.getMiningPool().printIDs(","));
//		assertEquals("{8,2,3}", pl.printIDs(","));
//		assertEquals("{8,2,3}", node.getMiningPool().printIDs(","));
//
//		transID = 8;
//		node.event_NodeReceivesClientTransaction(tg.getTransaction(transID),tg.getTransaction(transID).getCreationTime());
//		assertEquals(145,pl.getValue());
//		assertFalse(node.isMining());
//		assertFalse(node.isWorthMining());
//		assertEquals(145,node.getMiningPool().getValue());
//		assertEquals(2900,node.getMiningPool().getSize());
//		Debug.p("Pool: " + pl.printIDs(","));
//		Debug.p("Mining Pool: " + node.getMiningPool().printIDs(","));
//		assertEquals("{8,9,2,3}", pl.printIDs(","));
//		assertEquals("{8,9,2,3}", node.getMiningPool().printIDs(","));
//
//
//		transID = 9;
//		node.event_NodeReceivesClientTransaction(tg.getTransaction(transID),tg.getTransaction(transID).getCreationTime());
//		assertEquals(151,pl.getValue());
//		assertFalse(node.isMining());
//		assertFalse(node.isWorthMining());
//		assertEquals(149,node.getMiningPool().getValue());
//		assertEquals(3000,node.getMiningPool().getSize());
//		Debug.p("Pool: " + pl.printIDs(","));
//		Debug.p("Mining Pool: " + node.getMiningPool().printIDs(","));
//		assertEquals("{8,9,2,10,3}", pl.printIDs(","));
//		assertEquals("{8,9,2,10}", node.getMiningPool().printIDs(","));
//
//		assertTrue(node.getNextValidationEvent().ignoreEvt());
//
//		transID = 10;
//		node.event_NodeReceivesClientTransaction(tg.getTransaction(transID),tg.getTransaction(transID).getCreationTime());
//		assertEquals(161,pl.getValue());
//		assertTrue(node.isMining());
//		assertTrue(node.isWorthMining());
//		assertEquals(153,node.getMiningPool().getValue());
//		assertEquals(2800,node.getMiningPool().getSize());
//		Debug.p("Pool: " + pl.printIDs(","));
//		Debug.p("Mining Pool: " + node.getMiningPool().printIDs(","));
//		assertEquals("{8,9,11,2,10,3}", pl.printIDs(","));
//		assertEquals("{8,9,11,2}", node.getMiningPool().printIDs(","));
//
//		assertFalse(node.getNextValidationEvent().ignoreEvt());
//	}
//
//
//	/*
//	ID 1 to 3: These transactions are high-value, intended to prompt the node to start mining based on the profitability of the transactions in the pool."""
//	ID 4 to 10: These represent a surge of transactions with lower value, challenging the node's decision on whether it remains profitable to continue mining. This sequence tests the node's ability to adapt to changes in the pool's composition and decide if mining is still worthwhile given the new, less favorable transactions."""
//	This workload tests the node's strategic response to sudden changes in network conditions, particularly its ability to discern and prioritize transactions that align with its mining profitability criteria. It will help in assessing the robustness of the node's transaction selection logic under simulated stress conditions akin to real-world scenarios.
//	*/
//	@Test
//	void testHandlingSuddenTransactionVolumeIncrease() throws Exception {
//		TransactionWorkload tg = null;
//
//		try {
//			//TODO: move this to the test folder!
//			tg = new TransactionWorkload("./CNSim/tests/BitcoinNode/SuddenTransactionVolumeIncrease - workload.csv", true);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		BitcoinNode node = (BitcoinNode) ns.getNodes().get(0);
//		node.setBehavior(INode.BehaviorType.HONEST);
//
//		// Initially populate the pool with high-value transactions
//		for (int transID = 0; transID < 3; transID++) {
//			node.event_NodeReceivesClientTransaction(tg.getTransaction(transID), tg.getTransaction(transID).getCreationTime());
//		}
//
//		// Check initial mining decision based on high-value transactions
//		assertTrue(node.isWorthMining());
//		assertTrue(node.isMining());
//
//		// Introduce a rapid sequence of low-value transactions
//		for (int transID = 3; transID < 10; transID++) {
//			node.event_NodeReceivesClientTransaction(tg.getTransaction(transID), tg.getTransaction(transID).getCreationTime());
//		}
//
//		// Evaluate the impact of the sudden increase in transaction volume on mining decisions
//		assertTrue(node.isWorthMining(), "Node should still find it worth mining after evaluating transaction pool value.");
//		assertTrue(node.isMining(), "Node should continue mining despite the influx of low-value transactions.");
//
//		// Assess the transaction pool and mining pool composition
//		assertNotEquals(0, node.getPool().getValue(), "The transaction pool should have a non-zero value.");
//		assertNotEquals(0, node.getMiningPool().getValue(), "The mining pool should selectively include valuable transactions.");
//
//		// Confirm that the node can efficiently manage the transaction pool against spam or sudden volume increase
//		assertTrue(node.getPool().getSize() > node.getMiningPool().getSize(), "The mining pool should be more selective than the general transaction pool.");
//	}
//
//	/*
//	High-value, small-size transactions (IDs 1, 2, 5, 7, 9): These transactions are designed to be clearly profitable, with a high value-to-size ratio, making them ideal candidates for mining.
//
//	Large-size, low-value transactions (IDs 3, 4, 6, 8, 10): These transactions are significantly larger in size but offer much less value, presenting a challenge to the node's pool management and mining strategy.
//	This workload is structured to test the node's ability to prioritize transactions effectively under conditions where the decision to include a transaction in the pool could significantly impact overall profitability. The expectation is that the node will prioritize transactions with IDs 1, 2, 5, 7, and 9 for mining, given their higher profitability, and may deprioritize or exclude transactions with IDs 3, 4, 6, 8, and 10 due to their lower value-to-size ratio.
//
//	Creating this workload file and using it in the test will help verify that the node's logic for managing its transaction pool and making mining decisions is aligned with profitability objectives, ensuring that it does not fill its capacity with less profitable transactions at the expense of more valuable ones.
//	*/
//
//	@Test
//	void testManagingLargeTransactionsWithMixedValues() throws Exception {
//		TransactionWorkload tg = null;
//
//		try {
//			tg = new TransactionWorkload("./CNSim/tests/BitcoinNode/LargeTransactionsMixedValues - workload.csv", true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		BitcoinNode node = (BitcoinNode) ns.getNodes().get(0);
//		node.setBehavior(INode.BehaviorType.HONEST);
//		node.setMinValueToMine(100); // Adjust based on test scenario needs
//
//		// Simulate receiving mixed transactions
//		for (int transID = 0; transID < tg.getCount(); transID++) {
//			node.event_NodeReceivesClientTransaction(tg.getTransaction(transID), tg.getTransaction(transID).getCreationTime());
//		}
//
//		// Assertions to verify the node's behavior and decision-making
//		assertTrue(node.isWorthMining(), "Node should find it worth mining with a strategic selection of transactions.");
//		assertTrue(node.getPool().getSize() < tg.getSize(), "Node pool should selectively include profitable transactions, ignoring large, low-value ones.");
//		assertNotEquals(0, node.getMiningPool().getValue(), "The mining pool should have a significant value, focusing on high-value transactions.");
//
//		// Further detailed assertions can be added based on specific transaction IDs, values, and sizes
//	}
//
//	/*
//	Each transaction is designed to be significantly above the node's mining value threshold, arriving in quick succession to test the node's responsiveness and decision-making regarding when to start mining.
//	The transactions also vary in size to ensure the node's selection algorithm is tested for both value and size considerations.
//	This test scenario and workload aim to assess the node's efficiency and responsiveness in managing its transaction pool and making mining decisions under conditions of rapid, high-value transaction arrivals. It will help verify that the node prioritizes profitability and responsiveness appropriately in its mining operations.
//	 */
//	@Test
//	void testRapidHighValueTransactions() throws Exception {
//		TransactionWorkload tg = null;
//
//		try {
//			tg = new TransactionWorkload("./CNSim/tests/BitcoinNode/RapidHighValueTransactions - workload.csv", true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		BitcoinNode node = (BitcoinNode) ns.getNodes().get(0);
//		node.setBehavior(INode.BehaviorType.HONEST);
//		node.setMinValueToMine(100); // Set a threshold that these transactions will clearly exceed
//
//		// Simulate receiving each high-value transaction in rapid succession
//		for (int transID = 0; transID < tg.getCount(); transID++) {
//			node.event_NodeReceivesClientTransaction(tg.getTransaction(transID), tg.getTransaction(transID).getCreationTime());
//			assertTrue(node.isWorthMining(), "Node should find it worth mining immediately after receiving a high-value transaction.");
//			if (transID == 0) { // Check right after the first transaction
//				assertTrue(node.isMining(), "Node should start mining as soon as a high-value transaction is received.");
//			}
//		}
//
//		// Ensure the mining pool is populated with all high-value transactions
//		assertEquals(tg.getCount(), node.getMiningPool().getCount(), "All high-value transactions should be in the mining pool.");
//	}
//
//	//TODO test for malicious node
//	@Test
//	void testDoubleSpendingAttack() throws Exception{
//
//		Config.init("./CNSim/tests/BitcoinNode/Case 1 - config.txt");
//		sampler = new FileBasedSampler("./CNSim/tests/BitcoinNode/DoubleSpending - workload.csv", "./CNSim/resources/nodes.csv");
//		Simulation s = new Simulation(sampler);
//		AbstractNodeFactory nf = new BitcoinNodeFactory("Honest", s);
//		NodeSet ns = new NodeSet(nf);
//		ns.addNodes(3);
//
//		//ns.addNodes(Config.getPropertyInt("net.numOfNodes"));
//		AbstractNetwork n = new RandomEndToEndNetwork(ns, sampler);
//		s.setNetwork(n);
//
//		TransactionWorkload ts = new TransactionWorkload(sampler);
//		ts.appendTransactions(10);
//		s.schedule(ts);
//
//		// Assign a target transaction for malicious behavior
//		Transaction targetTransaction = ts.getTransaction(5);
//		for (INode node : ns.getNodes()) {
//			if (node instanceof BitcoinNode) {
//				BitcoinNode bNode = (BitcoinNode) node;
//				if (bNode.getBehaviorStrategy() instanceof MaliciousNodeBehavior) {
//					((MaliciousNodeBehavior) bNode.getBehaviorStrategy()).setTargetTransaction(targetTransaction);
//				}
//			}
//		}
//
//		s.run();
//		long realTime = (System.currentTimeMillis() - Profiling.simBeginningTime); // in Milli-Sec
//		System.out.printf("\n");
//		System.out.println("Real time(ms): " + realTime);
//		System.out.println("Simulation time(ms): " + Simulation.currTime);
//
//		s.getNodeSet().closeNodes();
//
//		//print the blockchain structure after simulation
//		ns.getNodes().forEach(node -> {
//			BitcoinNode bNode = (BitcoinNode) node;
//			System.out.println(bNode.blockchain.printLongestChain());
//		});
//
//
//		}
//
//	//TODO test if it detects the tip correctly. Also the structure.
//	@Test
//	void testBlockchainTipWithCompetingChains() {
//		// Setup environment and transactions are assumed to be done
//
//		// Create blocks with a simple linking mechanism (using a mock hash for simplicity)
//		Block genesisBlock = new Block(); // Starting point of the blockchain
//
//		// Chain 1
//		Block block1A = new Block();
//		block1A.setParent(genesisBlock);
//		Block block2A = new Block();
//		block2A.setParent(block1A);
//		Block block3A = new Block();
//		block3A.setParent(block2A);
//
//		// Chain 2 (Competing chain)
//		Block block1B = new Block();
//		block1B.setParent(genesisBlock);
//		Block block2B = new Block();
//		block2B.setParent(block1B);
//		Block block3B = new Block();
//		block3B.setParent(block2B);
//		Block block4B = new Block(); // This chain will be longer
//		block4B.setParent(block3B);
//
//		// Get a node from the simulation
//		BitcoinNode node = (BitcoinNode) ns.getNodes().get(0);
//
//		// Simulate receiving blocks out of order and from competing chains
//		node.event_NodeReceivesPropagatedContainer(block1A);
//		assertEquals(block1A, node.blockchain.getLongestTip(), "Block 1A should be the current tip");
//
//		node.event_NodeReceivesPropagatedContainer(block1B);
//		// Still, block 1A is the tip since we're on that chain
//		assertEquals(block1A, node.blockchain.getLongestTip(), "Block 1A should still be the current tip");
//
//		node.event_NodeReceivesPropagatedContainer(block2A);
//		assertEquals(block2A, node.blockchain.getLongestTip(), "Block 2A should be the current tip");
//
//		node.event_NodeReceivesPropagatedContainer(block3A);
//		assertEquals(block3A, node.blockchain.getLongestTip(), "Block 3A should be the current tip");
//
//		// Now introduce the competing longer chain
//		node.event_NodeReceivesPropagatedContainer(block2B);
//		node.event_NodeReceivesPropagatedContainer(block3B);
//		// Tip doesn't change until the competing chain becomes longer
//		assertEquals(block3A, node.blockchain.getLongestTip());
//
//		node.event_NodeReceivesPropagatedContainer(block4B);
//		// The competing chain (B) becomes longer, so it becomes the new tip
//		assertEquals(block4B, node.blockchain.getLongestTip(), "Block 4B should now be the current tip due to longer chain");
//
//		// Further extend chain A to see if the node correctly switches back
//		Block block4A = new Block();
//		block4A.setParent(block3A);
//		Block block5A = new Block(); // This will make chain A longer again
//		block5A.setParent(block4A);
//		node.event_NodeReceivesPropagatedContainer(block4A);
//		node.event_NodeReceivesPropagatedContainer(block5A);
//		// Verify the tip is now the end of chain A, which is longer than chain B
//		assertEquals(block5A, node.blockchain.getLongestTip(), "Block 5A should now be the current tip due to longer chain A");
//
//		// This test scenario simulates a more dynamic and realistic blockchain environment,
//		// where the node must continuously evaluate the longest chain amidst receiving blocks out of order and from competing chains.
//	}
//
//	//TODO test recieving invalid block
//	//TODO test Blockchain Object
//
//
//
//}
