package cmg.cnsim.engine.transaction;

import java.util.Comparator;

public class TxValuePerSizeComparator implements Comparator<Transaction> {

	@Override
	public int compare(Transaction t1, Transaction t2) {
			if((t1.getValue()/t1.getSize()) <= (t2.getValue()/t2.getSize()))
				return 1;
			else
				return -1;
	}
}
