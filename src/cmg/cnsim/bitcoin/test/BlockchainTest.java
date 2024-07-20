/**
 *
 */
package cmg.cnsim.bitcoin.test;

import cmg.cnsim.bitcoin.Block;
import cmg.cnsim.bitcoin.Blockchain;
import cmg.cnsim.engine.transaction.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Enterprise Systems Group (ESG) @ York University
 *
 */
class BlockchainTest {
	ArrayList<Block> blocks = new ArrayList<Block>();
	Blockchain blockchain = new Blockchain();
//	Block keep3, keep4;
//	Block keepme;

	private void printBlockchain() {
		String[] result = blockchain.printStructure();
		for (int i = 0 ; i < result.length; i++) {
			System.out.println('"' + result[i] + '"' + ',');
		}
	}

	/**
	 * @throws java.lang.Exception Throws an exception.
	 */
	@BeforeEach
	void setUp() throws Exception {
	}

	/**
	 * Tests the overlap functionality between blocks.
	 * <p>
	 * This test verifies the overlap logic between transactions in different blocks.
	 * It ensures that blocks with overlapping transactions are correctly identified.
	 * <p>
	 * The test covers the following scenarios:
	 * 1. Two blocks with overlapping transactions.
	 * 2. Two blocks without overlapping transactions.
	 */
	@Test
	void testBlockTransactionOverlap() {
		// Scenario 1: Two blocks with overlapping transactions
		Block block1 = new Block();
		block1.addTransaction(new Transaction(1, 10, 10, 50));
		block1.addTransaction(new Transaction(2, 11, 20, 25));
		block1.addTransaction(new Transaction(3, 13, 100, 500));

		Block block2 = new Block();
		block2.addTransaction(new Transaction(3, 10, 10, 50));
		block2.addTransaction(new Transaction(4, 11, 20, 25));
		block2.addTransaction(new Transaction(5, 13, 100, 500));

		// Assert that the two blocks overlap
		assertTrue(block2.overlapsWith(block1), "Block2 should overlap with Block1");
		assertTrue(block1.overlapsWith(block2), "Block1 should overlap with Block2");

		// TODO: Fix overlapsWithByID method
		// assertTrue(block2.overlapsWithByID(block1));
		// assertTrue(block1.overlapsWithByID(block2));

		// Scenario 2: Two blocks without overlapping transactions
		block2 = new Block();
		block2.addTransaction(new Transaction(4, 10, 10, 50));
		block2.addTransaction(new Transaction(5, 11, 20, 25));
		block2.addTransaction(new Transaction(6, 13, 100, 500));

		// Assert that the two blocks do not overlap
		assertFalse(block2.overlapsWith(block1), "Block2 should not overlap with Block1");
		assertFalse(block1.overlapsWith(block2), "Block1 should not overlap with Block2");

		// TODO: Fix overlapsWithByID method
		// assertFalse(block2.overlapsWithByID(block1));
		// assertFalse(block1.overlapsWithByID(block2));
	}


	/**
	 * Test method for {@linkplain cmg.cnsim.bitcoin.Blockchain#addToStructure(Block)}.
	 */
	@Test
	final void testAddToStructure() {

		//0
		Block block = new Block();
		block.addTransaction(new Transaction(1,10,10,50));
		block.addTransaction(new Transaction(2,11,20,25));
		block.addTransaction(new Transaction(3,13,100,500));
		block.addTransaction(new Transaction(4,14,50,10));
		block.addTransaction(new Transaction(5,15,70,50));
		block.addTransaction(new Transaction(6,16,100,50));
		blockchain.addToStructure(block);


		String[] expected_1 = {"BlockID,ParentID,BlockHeight,Transactions",
				"0,-1,1,{1,2,3,4,5,6}"};
		assertArrayEquals(expected_1, blockchain.printStructure());

		String[] oxpected_1 = {"BlockID,ParentID,Transactions"};
		assertArrayEquals(oxpected_1, blockchain.printOrphans());


		//1 --> 0
		block = new Block();
		block.addTransaction(new Transaction(7,19,10,55));
		block.addTransaction(new Transaction(8,20,25,20));
		block.addTransaction(new Transaction(9,21,105,10));
		block.addTransaction(new Transaction(10,22,55,100));
		Block keep_1 = block;
		blockchain.addToStructure(block);


		String[] expected_2 = {"BlockID,ParentID,BlockHeight,Transactions",
				"1,0,2,{7,8,9,10}",
				"0,-1,1,{1,2,3,4,5,6}"};

		assertArrayEquals(expected_2, blockchain.printStructure());
		assertArrayEquals(oxpected_1, blockchain.printOrphans());

		//2 --> 1
		block = new Block();
		block.addTransaction(new Transaction(11,23,10,505));
		block.addTransaction(new Transaction(12,25,250,2));
		block.addTransaction(new Transaction(13,30,505,10));
		Block keepme_2 = block;
		blockchain.addToStructure(block);


		String[] expected_3 = {"BlockID,ParentID,BlockHeight,Transactions",
				"2,1,3,{11,12,13}",
				"1,0,2,{7,8,9,10}",
				"0,-1,1,{1,2,3,4,5,6}"};
		assertArrayEquals(expected_3, blockchain.printStructure());
		assertArrayEquals(oxpected_1, blockchain.printOrphans());


		//3 --> 1
		block = new Block();
		block.addTransaction(new Transaction(14,35,10,505));
		block.addTransaction(new Transaction(15,40,250,2));
		block.setParent(keep_1);
		Block keep2_3 = block;
		blockchain.addToStructure(block);

		// Slide 1
		String[] expected_4 = {"BlockID,ParentID,BlockHeight,Transactions",
				"3,1,3,{14,15}",
				"2,1,3,{11,12,13}",
				"1,0,2,{7,8,9,10}",
				"0,-1,1,{1,2,3,4,5,6}"};
		assertArrayEquals(expected_4, blockchain.printStructure());
		assertArrayEquals(oxpected_1, blockchain.printOrphans());
		assertEquals(blockchain.printTips(","), "{2,3}");



		//4 --> 3
		block = new Block();
		block.addTransaction(new Transaction(16,41,10,505));
		block.addTransaction(new Transaction(17,42,250,2));
		assertNull(block.getParent());
		blockchain.addToStructure(block);

		//Slide 2
		String[] expected_5 = {"BlockID,ParentID,BlockHeight,Transactions",
				"4,3,4,{16,17}",
				"3,1,3,{14,15}",
				"2,1,3,{11,12,13}",
				"1,0,2,{7,8,9,10}",
				"0,-1,1,{1,2,3,4,5,6}"};
		assertArrayEquals(expected_5, blockchain.printStructure());
		assertArrayEquals(oxpected_1, blockchain.printOrphans());
		assertEquals(blockchain.printTips(","), "{2,4}");



		//5 --> 4
		block = new Block();
		block.addTransaction(new Transaction(18,41,10,505));
		block.addTransaction(new Transaction(19,42,250,2));
		blockchain.addToStructure(block);

		// Slide 3
		String[] expected_6 = {"BlockID,ParentID,BlockHeight,Transactions",
				"5,4,5,{18,19}",
				"4,3,4,{16,17}",
				"3,1,3,{14,15}",
				"2,1,3,{11,12,13}",
				"1,0,2,{7,8,9,10}",
				"0,-1,1,{1,2,3,4,5,6}"};
		assertArrayEquals(expected_6, blockchain.printStructure());
		assertArrayEquals(oxpected_1, blockchain.printOrphans());
		assertEquals(blockchain.printTips(","), "{2,5}");

		//6 --> 1
		block = new Block();
		block.addTransaction(new Transaction(20,25,10,505));
		block.addTransaction(new Transaction(21,26,250,2));
		block.setParent(keep_1);
		blockchain.addToStructure(block);

		String[] expected_7 = {"BlockID,ParentID,BlockHeight,Transactions",
				"5,4,5,{18,19}",
				"4,3,4,{16,17}",
				"6,1,3,{20,21}",
				"3,1,3,{14,15}",
				"2,1,3,{11,12,13}",
				"1,0,2,{7,8,9,10}",
				"0,-1,1,{1,2,3,4,5,6}"};

		assertArrayEquals(expected_7, blockchain.printStructure());
		assertArrayEquals(oxpected_1, blockchain.printOrphans());
		assertEquals("{2,5,6}",blockchain.printTips(","));


		//7 --> 5
		block = new Block();
		block.addTransaction(new Transaction(22,41,10,505));
		block.addTransaction(new Transaction(23,42,250,2));
		block.addTransaction(new Transaction(24,42,250,2));
		blockchain.addToStructure(block);

		String[] expected_8 = {"BlockID,ParentID,BlockHeight,Transactions",
				"7,5,6,{22,23,24}",
				"5,4,5,{18,19}",
				"4,3,4,{16,17}",
				"6,1,3,{20,21}",
				"3,1,3,{14,15}",
				"2,1,3,{11,12,13}",
				"1,0,2,{7,8,9,10}",
				"0,-1,1,{1,2,3,4,5,6}"};

		assertArrayEquals(expected_8, blockchain.printStructure());
		assertArrayEquals(oxpected_1, blockchain.printOrphans());
		assertEquals("{6,2,7}",blockchain.printTips(","));


		//8 --> 3
		block = new Block();
		block.addTransaction(new Transaction(25,25,10,505));
		block.addTransaction(new Transaction(26,26,250,2));
		block.setParent(keep2_3);
		Block keep3_8 = block;

		//9 --> 8
		block = new Block();
		block.addTransaction(new Transaction(27,25,10,505));
		block.addTransaction(new Transaction(28,26,250,2));
		block.setParent(keep3_8);
		Block keep4_9 = block;
		blockchain.addToStructure(block);


		String[] expected_9 = {"BlockID,ParentID,BlockHeight,Transactions",
				"7,5,6,{22,23,24}",
				"5,4,5,{18,19}",
				"4,3,4,{16,17}",
				"6,1,3,{20,21}",
				"3,1,3,{14,15}",
				"2,1,3,{11,12,13}",
				"1,0,2,{7,8,9,10}",
				"0,-1,1,{1,2,3,4,5,6}"};

		assertArrayEquals(expected_9, blockchain.printStructure());
		assertEquals("{6,2,7}",blockchain.printTips(","));

		String[] oxpected_2 = {"BlockID,ParentID,Transactions",
				"9,8,{27,28}"
		};
		assertArrayEquals(oxpected_2, blockchain.printOrphans());
		assertEquals("{6,2,7}",blockchain.printTips(","));

		//10 --> 9
		block = new Block();
		block.addTransaction(new Transaction(29,25,10,505));
		block.addTransaction(new Transaction(30,26,250,2));
		block.addTransaction(new Transaction(31,26,250,2));
		block.setParent(keep4_9);
		blockchain.addToStructure(block);

		assertArrayEquals(expected_9, blockchain.printStructure());

		String[] oxpected_3 = {"BlockID,ParentID,Transactions",
				"9,8,{27,28}",
				"10,9,{29,30,31}"
		};
		assertArrayEquals(oxpected_3, blockchain.printOrphans());
		assertEquals("{6,2,7}",blockchain.printTips(","));

		//Plug 8 now. Orphans must be placed back.
		blockchain.addToStructure(keep3_8);

		String[] expected_10 = {"BlockID,ParentID,BlockHeight,Transactions",
				"10,9,6,{29,30,31}",
				"7,5,6,{22,23,24}",
				"9,8,5,{27,28}",
				"5,4,5,{18,19}",
				"8,3,4,{25,26}",
				"4,3,4,{16,17}",
				"6,1,3,{20,21}",
				"3,1,3,{14,15}",
				"2,1,3,{11,12,13}",
				"1,0,2,{7,8,9,10}",
				"0,-1,1,{1,2,3,4,5,6}"};
		assertArrayEquals(expected_10, blockchain.printStructure());
		assertArrayEquals(oxpected_1, blockchain.printOrphans());
		assertEquals("{6,2,7,10}",blockchain.printTips(","));

		//11 --> 2
		block = new Block();
		block.addTransaction(new Transaction(32,25,10,505));
		block.addTransaction(new Transaction(33,26,250,2));
		block.setParent(keepme_2);
		Block keep_11 = block;

		//12 --> 11
		block = new Block();
		block.addTransaction(new Transaction(34,25,10,505));
		block.addTransaction(new Transaction(35,26,250,2));
		block.setParent(keep_11);
		blockchain.addToStructure(block);

		assertArrayEquals(expected_10, blockchain.printStructure());
		String[] oxpected_4 = {"BlockID,ParentID,Transactions",
				"12,11,{34,35}"
		};
		assertArrayEquals(oxpected_4, blockchain.printOrphans());
		assertEquals("{6,2,7,10}",blockchain.printTips(","));


		blockchain.addToStructure(keep_11);

		String[] expected_11 = {"BlockID,ParentID,BlockHeight,Transactions",
				"10,9,6,{29,30,31}",
				"7,5,6,{22,23,24}",
				"12,11,5,{34,35}",
				"9,8,5,{27,28}",
				"5,4,5,{18,19}",
				"11,2,4,{32,33}",
				"8,3,4,{25,26}",
				"4,3,4,{16,17}",
				"6,1,3,{20,21}",
				"3,1,3,{14,15}",
				"2,1,3,{11,12,13}",
				"1,0,2,{7,8,9,10}",
				"0,-1,1,{1,2,3,4,5,6}"};
		assertArrayEquals(expected_11, blockchain.printStructure());
		assertArrayEquals(oxpected_1, blockchain.printOrphans());
		assertEquals("{6,7,10,12}",blockchain.printTips(","));

		//13 --> -1 (overlaps)
		block = new Block();
		block.addTransaction(new Transaction(14,25,10,505));
		block.addTransaction(new Transaction(36,26,250,2));
		Block keep_13 = block;

		//14 --> 13
		block = new Block();
		block.addTransaction(new Transaction(37,25,10,505));
		block.addTransaction(new Transaction(38,26,250,2));
		block.setParent(keep_13);
		blockchain.addToStructure(block);


		assertArrayEquals(expected_11, blockchain.printStructure());
		String[] oxpected_5 = {"BlockID,ParentID,Transactions",
				"14,13,{37,38}"
		};
		assertArrayEquals(oxpected_5, blockchain.printOrphans());


		//Does not matter.
		blockchain.addToStructure(keep_13);

		String[] expected_111 = {"BlockID,ParentID,BlockHeight,Transactions",
				"14,13,7,{37,38}",
				"13,12,6,{14,36}",
				"10,9,6,{29,30,31}",
				"7,5,6,{22,23,24}",
				"12,11,5,{34,35}",
				"9,8,5,{27,28}",
				"5,4,5,{18,19}",
				"11,2,4,{32,33}",
				"8,3,4,{25,26}",
				"4,3,4,{16,17}",
				"6,1,3,{20,21}",
				"3,1,3,{14,15}",
				"2,1,3,{11,12,13}",
				"1,0,2,{7,8,9,10}",
				"0,-1,1,{1,2,3,4,5,6}"};
		assertArrayEquals(oxpected_1, blockchain.printOrphans());
		assertArrayEquals(expected_111, blockchain.printStructure());
		assertEquals("{10,7,6,14}",blockchain.printTips(","));


		block = new Block();
		block.addTransaction(new Transaction(29,25,10,505));
		block.addTransaction(new Transaction(18,41,10,505));
		block.addTransaction(new Transaction(12,25,250,2));
		blockchain.addToStructure(block);

		//Debug.p("This:"+blockchain.getNonOverlappingTip(block).getID());
		//assertFalse(blockchain.hasChainOverlap(block, blockchain.getNonOverlappingTip(block)));
		String[] expected = {"BlockID,ParentID,BlockHeight,Transactions",
				"14,13,7,{37,38}",
				"13,12,6,{14,36}",
				"10,9,6,{29,30,31}",
				"7,5,6,{22,23,24}",
				"12,11,5,{34,35}",
				"9,8,5,{27,28}",
				"5,4,5,{18,19}",
				"15,6,4,{29,18,12}",
				"11,2,4,{32,33}",
				"8,3,4,{25,26}",
				"4,3,4,{16,17}",
				"6,1,3,{20,21}",
				"3,1,3,{14,15}",
				"2,1,3,{11,12,13}",
				"1,0,2,{7,8,9,10}",
				"0,-1,1,{1,2,3,4,5,6}"};
		assertArrayEquals(oxpected_1, blockchain.printOrphans());
		assertArrayEquals(expected, blockchain.printStructure());
		assertEquals("{14,10,7,15}",blockchain.printTips(","));


		block = new Block();
		block.addTransaction(new Transaction(34,25,10,505));
		block.addTransaction(new Transaction(16,41,10,505));
		block.addTransaction(new Transaction(31,25,250,2));
		block.addTransaction(new Transaction(20,25,250,2));
		blockchain.addToStructure(block);
		assertArrayEquals(oxpected_1, blockchain.printOrphans());
		assertArrayEquals(expected, blockchain.printStructure());
		assertEquals("{14,10,7,15}",blockchain.printTips(","));

		block = new Block();
		block.addTransaction(new Transaction(1,25,10,505));
		blockchain.addToStructure(block);
		assertArrayEquals(oxpected_1, blockchain.printOrphans());
		assertArrayEquals(expected, blockchain.printStructure());
		assertEquals("{14,10,7,15}",blockchain.printTips(","));
	}

	/**
	 * Tests the chain reorganization functionality with multiple tips in the blockchain.
	 * <p>
	 * This test verifies that the blockchain correctly handles forks and reorganizes
	 * to maintain the longest chain as the canonical chain. It covers the following scenarios:
	 * 1. Creating a genesis block and adding it to the blockchain.
	 * 2. Adding two blocks that extend the genesis block, creating a fork with two tips.
	 * 3. Extending one of the forks to make it the longest chain and verifying that the blockchain
	 * resolves to this chain as the canonical chain.
	 * 4. Ensuring the blockchain structure matches the expected structure after reorganization.
	 * 5. Ensuring no orphan blocks remain after the reorganization.
	 */
	@Test
	final void testChainReorganizationWithMultipleTips() {
		// Initial setup: Create a genesis block and add it to the blockchain
		Block genesisBlock = new Block();
		genesisBlock.addTransaction(new Transaction(0, 1, 1, 1));
		blockchain.addToStructure(genesisBlock);

		// Create and add the first block extending the genesis block
		Block firstChild = new Block();
		firstChild.setParent(genesisBlock); // Linking to genesis block
		firstChild.addTransaction(new Transaction(1, 10, 2, 2));
		blockchain.addToStructure(firstChild);

		// Create and add a second block extending the genesis block, creating a fork
		Block secondChild = new Block();
		secondChild.setParent(genesisBlock); // Also linking to genesis block
		secondChild.addTransaction(new Transaction(2, 20, 2, 2));
		blockchain.addToStructure(secondChild);

		// Verify that no blocks are identified as orphans
		String[] oxpected = {"BlockID,ParentID,Transactions"};
		assertArrayEquals(oxpected, blockchain.printOrphans());

		// Verify that the blockchain now has two tips, indicating a  fork
		assertTrue(blockchain.printTips(",").contains("{2,3}"), "There should be two tips representing a fork");

		// Add a new block that extends one of the forks, making it the longest chain
		Block extendingBlock = new Block();
		extendingBlock.setParent(firstChild); // Extending the firstChild, making its chain longer
		extendingBlock.addTransaction(new Transaction(3, 30, 3, 2));
		blockchain.addToStructure(extendingBlock);

		// Verify that the blockchain has resolved to a single tip
		assertEquals(4, blockchain.getLongestTip().getID(), "The extending block should make its chain the canonical chain");

		// Additional checks to ensure the blockchain structure is as expected
		String[] expectedStructure = {
				"BlockID,ParentID,BlockHeight,Transactions",
				"4,2,3,{3}",
				"3,1,2,{2}",
				"2,1,2,{1}",
				"1,-1,1,{0}"
		};
		assertArrayEquals(expectedStructure, blockchain.printStructure(), "Blockchain structure should match expected after reorganization");
	}

	/**
	 * Tests the handling of complex forks and their reunification in the blockchain.
	 * <p>
	 * This test verifies that the blockchain correctly manages multiple forks and reunifies them
	 * while maintaining the correct structure. It covers the following scenarios:
	 * 1. Creating a genesis block and adding it to the blockchain.
	 * 2. Adding two blocks that extend the genesis block, creating a fork with two tips.
	 * 3. Extending both forks by adding blocks to each fork.
	 * 4. Verifying the existence of multiple tips before reunification.
	 * 5. Adding a new block that reunifies the forks by extending one of the forks.
	 * 6. Ensuring the longest tip is correctly identified after reunification.
	 * 7. Verifying the blockchain structure matches the expected structure after reunification.
	 */
	@Test
	final void testHandlingComplexForks() {
		// Initial setup: Create a genesis block and add it to the blockchain
		Block genesisBlock = new Block();
		genesisBlock.addTransaction(new Transaction(0, 1, 1, 1));
		blockchain.addToStructure(genesisBlock);

		// Create and add two blocks extending the genesis block, creating initial fork
		Block firstFork = new Block();
		firstFork.setParent(genesisBlock);
		firstFork.addTransaction(new Transaction(1, 10, 2, 2));
		blockchain.addToStructure(firstFork);

		Block secondFork = new Block();
		secondFork.setParent(genesisBlock);
		secondFork.addTransaction(new Transaction(2, 20, 2, 2));
		blockchain.addToStructure(secondFork);

		// Extending the first fork
		Block thirdFork = new Block();
		thirdFork.setParent(firstFork);
		thirdFork.addTransaction(new Transaction(3, 30, 3, 3));
		blockchain.addToStructure(thirdFork);

		// Extending the second fork
		Block fourthFork = new Block();
		fourthFork.setParent(secondFork);
		fourthFork.addTransaction(new Transaction(4, 40, 4, 4));
		blockchain.addToStructure(fourthFork);

		// Verify the existence of multiple tips before reunification
		assertTrue(blockchain.printTips(",").contains("{4,5}"), "There should be two tips representing the forks");

		// Adding a new block that reunites the forks by choosing one as its parent
		Block reunificationBlock = new Block();
		reunificationBlock.setParent(thirdFork);
		reunificationBlock.addTransaction(new Transaction(5, 50, 5, 5));
		blockchain.addToStructure(reunificationBlock);

		// Verify that the longest tip is now the reunification block
		assertEquals(6, blockchain.getLongestTip().getID(), "After reunification, there should be only one tip");

		// Ensure the blockchain structure reflects the reunification correctly
		String[] expectedStructure = {
				"BlockID,ParentID,BlockHeight,Transactions",
				"6,4,4,{5}",
				"5,3,3,{4}",
				"4,2,3,{3}",
				"3,1,2,{2}",
				"2,1,2,{1}",
				"1,-1,1,{0}"
		};
		assertArrayEquals(expectedStructure, blockchain.printStructure(), "Blockchain structure should match expected after complex forks and reunification");
	}

	//TODO check again
	@Test
	final void testHandlingRapidSuccessionOfBlocks() {
		// Initial setup: Create a genesis block and add it to the blockchain
		Block genesisBlock = new Block();
		genesisBlock.addTransaction(new Transaction(0, 1, 1, 1)); // Simplified genesis transaction
		blockchain.addToStructure(genesisBlock);

		// Rapidly add multiple blocks to the blockchain
		Block previousBlock = genesisBlock;
		int numberOfBlocksToAdd = 10; // Simulate adding 10 blocks in rapid succession
		for (int i = 1; i <= numberOfBlocksToAdd; i++) {
			Block newBlock = new Block();
			newBlock.setParent(previousBlock);
			newBlock.addTransaction(new Transaction(i, 10 * i, 100 * i, 2)); // Add a unique transaction to each block
			blockchain.addToStructure(newBlock);
			previousBlock = newBlock; // Update the reference for the next block's parent
		}

		// Verify that all blocks were added correctly
		for (int i = 1; i <= numberOfBlocksToAdd; i++) {
			final int blockID = i;
			// Assuming a method exists to retrieve a block by its ID for verification
			Block block = blockchain.getBlockByID(blockID);
			assertNotNull(block, "Block " + blockID + " should exist in the blockchain");
			System.out.println(block.printIDs(","));
			assertEquals(i, block.getCount(), "Block " + blockID + " should contain exactly one transaction");
		}

		// Ensure the blockchain's integrity is maintained
		assertEquals(numberOfBlocksToAdd + 1, blockchain.getBlockchainHeight(), "Blockchain should have the correct number of blocks");

		// This test checks if the blockchain can handle a high throughput of block additions without compromising data integrity or the correct order of blocks.
	}

	//TODO write more tests for orphans

	@Test
	void testOrphanBlockHandlingAndIntegration() {
		// Initial setup: Create a genesis block and add it to the blockchain
		Block genesisBlock = new Block();
		genesisBlock.addTransaction(new Transaction(0, System.currentTimeMillis(), 1.0f, 0.1f)); // Genesis transaction
		blockchain.addToStructure(genesisBlock);

		// Create a block that should be an orphan initially (its parent is not yet in the blockchain)
		Block orphanBlock = new Block();
		orphanBlock.addTransaction(new Transaction(2, System.currentTimeMillis(), 3.0f, 0.3f));
		// This parent ID points to a block that has not been added yet, simulating an orphan
		Block missingParentBlock = new Block();
		missingParentBlock.addTransaction(new Transaction(1, System.currentTimeMillis(), 2.0f, 0.2f));
		orphanBlock.setParent(missingParentBlock);

		// Attempt to add the orphan block to the blockchain
		blockchain.addToStructure(orphanBlock);

		// Verify that the blockchain recognizes the block as an orphan
		assertEquals(1, blockchain.printOrphans().length - 1, "There should be one orphan block waiting for its parent");

		// Now add the missing parent block to the blockchain
		blockchain.addToStructure(missingParentBlock);

		// Verify that the orphan block is no longer an orphan and is integrated into the blockchain
		assertEquals(0, blockchain.printOrphans().length - 1, "There should be no orphans after adding the missing parent");

		// Verify the blockchain structure to ensure both the previously orphan block and its parent are correctly integrated
		String[] expectedStructure = {
				"BlockID,ParentID,BlockHeight,Transactions",
				String.format("%d,0,2,{2}", missingParentBlock.getID()), // Assuming ID is assigned sequentially by the blockchain
				String.format("%d,%d,3,{2}", orphanBlock.getID(), missingParentBlock.getID()),
				"0,-1,1,{0}" // Genesis block
		};
		assertArrayEquals(expectedStructure, blockchain.printStructure(), "Blockchain structure should include the orphan block and its parent correctly after integration");
	}


	//TODO while a block comes that the longest chain has one transaction of it already

	@Test
	void testBlockWithDuplicateTransactionIsRejected() {
		// Initial setup: Create a genesis block and add it to the blockchain
		Block genesisBlock = new Block();
		genesisBlock.addTransaction(new Transaction(0, System.currentTimeMillis(), 1.0f, 0.1f)); // Simplified genesis transaction
		blockchain.addToStructure(genesisBlock);

		// Create and add a valid block extending the genesis block
		Block validBlock = new Block();
		validBlock.setParent(genesisBlock); // Linking to genesis block
		Transaction uniqueTransaction = new Transaction(1, System.currentTimeMillis(), 2.0f, 0.2f);
		validBlock.addTransaction(uniqueTransaction);
		blockchain.addToStructure(validBlock);

		// Attempt to add a new block containing a duplicate of the existing transaction
		Block blockWithDuplicateTransaction = new Block();
		blockWithDuplicateTransaction.setParent(genesisBlock); // Also linking to genesis block
		blockWithDuplicateTransaction.addTransaction(uniqueTransaction); // Adding the same transaction again
		blockchain.addToStructure(blockWithDuplicateTransaction);

		// Verify that the blockchain does not accept the block with a duplicate transaction
		String expectedTips = String.format("{%d}", validBlock.getID());
		assertEquals(expectedTips, blockchain.printTips(","), "Blockchain should only have one tip, excluding the block with the duplicate transaction");

		// Verify that the blockchain structure does not include the invalid block
		String[] expectedStructure = {
				"BlockID,ParentID,BlockHeight,Transactions",
				String.format("%d,0,2,{1}", validBlock.getID()),
				"0,-1,1,{0}" // Genesis block
		};
		assertArrayEquals(expectedStructure, blockchain.printStructure(), "Blockchain structure should not include the block with the duplicate transaction");

		// Additionally, check if the rejected block is considered an orphan or simply discarded
		assertTrue(blockchain.printOrphans().length - 1 == 0, "There should be no orphans from rejected blocks with duplicate transactions");
	}


}
