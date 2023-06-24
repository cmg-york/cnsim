package cmg.cnsim.engine.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cmg.cnsim.engine.transaction.Transaction;
import cmg.cnsim.engine.transaction.TransactionGroup;
import cmg.cnsim.engine.transaction.TxValuePerSizeComparator;

class TransactionGroupTest {

	protected TransactionGroup pool = new TransactionGroup();
	
	@BeforeEach
	void setUp() throws Exception {
		pool.addTransaction(new Transaction(1,10,10,50)); 
		pool.addTransaction(new Transaction(2,11,20,25));
		pool.addTransaction(new Transaction(3,13,100,500));
		pool.addTransaction(new Transaction(4,14,50,10));
		pool.addTransaction(new Transaction(5,15,70,50));
		pool.addTransaction(new Transaction(6,16,100,50));
		System.out.println(pool.printIDs(","));
	}

	@Test
	final void VariousTests() {
		TransactionGroup g = pool.getTopN(186, new TxValuePerSizeComparator());
		ArrayList<Transaction> r = g.getGroup();
		ArrayList<Integer> ids_expected = new ArrayList<Integer>(Arrays.asList(4,6,5,2,1)); 
		ArrayList<Integer> ids_actuals = new ArrayList<Integer>();
		for (Transaction tr: r) {
			ids_actuals.add(tr.getID());
			System.out.println("Transaction: " + tr.getID());
		}
		assertArrayEquals(ids_expected.toArray(),ids_actuals.toArray());

		assertEquals(685,pool.getSize());
		assertEquals(350,pool.getValue());
		
		System.out.println(pool.printIDs(","));
		
		g = pool.getTopN(185, new TxValuePerSizeComparator());
		r = g.getGroup();
		ids_expected = new ArrayList<Integer>(Arrays.asList(4,6,5,2)); 
		ids_actuals = new ArrayList<Integer>();
		for (Transaction tr: r) {
			ids_actuals.add(tr.getID());
			System.out.println("Transaction: " + tr.getID());
		}
		System.out.println(g.printIDs(","));
		assertArrayEquals(ids_expected.toArray(),ids_actuals.toArray());

		
		Transaction t = pool.removeNextTx();
		
		assertEquals(4,t.getID());
		assertEquals(300,pool.getValue());
		assertEquals(675,pool.getSize());
		
		System.out.println(pool.printIDs(","));
		
		t = pool.removeNextTx();
		t = pool.removeNextTx();
		t = pool.removeNextTx();
		
		assertEquals(2,t.getID());
		assertEquals(2,pool.getCount());
		assertEquals(110,pool.getValue());
		assertEquals(550,pool.getSize());

		t = pool.removeNextTx();
		System.out.println(pool.printIDs(","));
		t = pool.removeNextTx();
		System.out.println(pool.printIDs(","));
		
		
		assertEquals(3,t.getID());
		assertEquals(0,pool.getCount());
		assertEquals(0,pool.getValue());
		assertEquals(0,pool.getSize());
		
	}

}
