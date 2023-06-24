/**
 * 
 */
package cmg.cnsim.bitcoin.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cmg.cnsim.bitcoin.Block;
import cmg.cnsim.bitcoin.Blockchain;
import cmg.cnsim.engine.transaction.Transaction;

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

	@Test
	final void testHelpers() {
		//Test overlap
		Block block1 = new Block();
		block1.addTransaction(new Transaction(1,10,10,50)); 
		block1.addTransaction(new Transaction(2,11,20,25));
		block1.addTransaction(new Transaction(3,13,100,500));

		Block block2 = new Block();
		block2.addTransaction(new Transaction(3,10,10,50)); 
		block2.addTransaction(new Transaction(4,11,20,25));
		block2.addTransaction(new Transaction(5,13,100,500));

		assertTrue(block2.overlapsWith(block1));
		assertTrue(block1.overlapsWith(block2));

		//assertTrue(block2.overlapsWithbyID(block1));
		//assertTrue(block1.overlapsWithbyID(block2));
		
		block2 = new Block();
		block2.addTransaction(new Transaction(4,10,10,50)); 
		block2.addTransaction(new Transaction(5,11,20,25));
		block2.addTransaction(new Transaction(6,13,100,500));

		assertFalse(block2.overlapsWith(block1));
		assertFalse(block1.overlapsWith(block2));


		//assertFalse(block2.overlapsWithbyID(block1));
		//assertFalse(block1.overlapsWithbyID(block2));

		
		
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
		
		
		//Transaction 13 is gone, 14 will stay in orphans forever
		/*		
		
		//15 --> 10
		block = new Block();
		block.addTransaction(new Transaction(39,25,10,505)); 
		block.addTransaction(new Transaction(40,26,250,2));
		blockchain.addToStructure(block);
		
		String[] expected_12 = {"BlockID,ParentID,BlockHeight,Transactions",
				"15,10,7,{39,40}",
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
		assertArrayEquals(expected_12, blockchain.printStructure());
		assertArrayEquals(oxpected_5, blockchain.printOrphans());
		
		//16 --> 9
		block = new Block();
		block.addTransaction(new Transaction(41,25,10,505)); 
		block.addTransaction(new Transaction(42,26,250,2));
		block.setParent(keep4_9);
		Block keep_16 = block;

		//17 --> 16
		block = new Block();
		block.addTransaction(new Transaction(43,25,10,505)); 
		block.addTransaction(new Transaction(44,26,250,2));
		block.setParent(keep_16);
		blockchain.addToStructure(block);
		
		assertArrayEquals(expected_12, blockchain.printStructure());
		String[] oxpected_6 = {"BlockID,ParentID,Transactions",
				"14,13,{37,38}",
				"17,16,{43,44}",
		};
		assertArrayEquals(oxpected_6, blockchain.printOrphans());
		
		blockchain.addToStructure(keep_16);
		
		String[] expected_13 = {"BlockID,ParentID,BlockHeight,Transactions",
				"17,16,7,{43,44}",
				"15,10,7,{39,40}",
				"16,9,6,{41,42}",
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

		assertArrayEquals(oxpected_5, blockchain.printOrphans());
		assertArrayEquals(expected_13, blockchain.printStructure());
	
		printBlockchain();
		
		BitcoinReporter.flushStructReport();
		*/
	}

}
