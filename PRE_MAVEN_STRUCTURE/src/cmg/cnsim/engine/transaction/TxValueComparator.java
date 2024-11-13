package cmg.cnsim.engine.transaction;

import java.util.Comparator;

public class TxValueComparator implements Comparator<Transaction>{

	@Override
	public int compare(Transaction t1, Transaction t2) {
			if(t1.getValue() >= t2.getValue())
				return 1;
			else
				return -1;
	}
}
