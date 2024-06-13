package cmg.cnsim.tangle.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Random;
import java.util.TreeMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cmg.cnsim.engine.Config;
import cmg.cnsim.engine.transaction.Transaction;
import cmg.cnsim.tangle.TangleCutSetFinder;



class TangleCutSetFinderTest {
	private TangleCutSetFinder finder = new TangleCutSetFinder(0.5f,0.75f);
	
	private TangleForTest tangle;
	private TreeMap<Integer,Integer> hist = new TreeMap<Integer,Integer>();
	private TreeMap<Integer,Integer> hist2 = new TreeMap<Integer,Integer>();
	
	@BeforeEach
	void setUp() throws Exception {
        Config.init("./resources/config.txt");
	}

	@Test
	final void test() {
		Integer totalAtoms = 0;
		int[] values = {1,1,2,1,3,1,4,1,2,2,5,1,2,3,3,6, 1,2,3,4,4,7, 1,2,3,4,5,5,8};
		
		// Expected
		hist.put(1, 2);
		hist.put(2, 1);
		hist.put(3, 1);
		hist.put(4, 1);
		hist.put(5, 2);
		hist.put(8, 1);
		
		
		// Actual
		for(int i = 0; i < values.length;i++) {
			finder.updateFrequencies(values[i],values[i]-1);
			totalAtoms++;
		}
		
		
		
		assertEquals(hist,finder.getTreeMap());
		assertEquals(8,finder.getTotalEntries());
		
		finder.printTreeMap();
		
		finder.setBounds(0.25f, 0.75f);
		assertEquals(1,finder.getLower());
		assertEquals(5,finder.getUpper());
		
		
		finder.setBounds(0.5f, 0.9f);
		assertEquals(3,finder.getLower());
		assertEquals(8,finder.getUpper());
		 

		//Phase two
	
		tangle = new TangleForTest();
		
		for (int i = 1;i<=20 ;i++)
			tangle.addNewTransaction(new Transaction(Transaction.getNextTxID()));
		
		for (int i = 0;i< Config.getPropertyInt("sim.maxTransactions");i++) {
			int weight = tangle.getTangle()[i][0];
			Integer freq = hist2.get(weight);
			if (weight!=0) {
				if (freq!=null)
					hist2.put(weight,freq+1);
				else
					hist2.put(weight, 1);
			}
				
		}
		System.out.println(hist2.toString());
		System.out.println(tangle.getCutSetFinder().getTreeMap().toString());
		assertEquals(hist2,tangle.getCutSetFinder().getTreeMap());
		System.out.println("Lower: " + tangle.getCutSetFinder().getLower());
		System.out.println("Upper: " + tangle.getCutSetFinder().getUpper());
	}

}
