package cmg.cnsim.engine.transaction;

import java.util.Comparator;

public class TxSizeComparator implements Comparator<Transaction>{

	@Override
	public int compare(Transaction t1, Transaction t2) {
			if(t1.getSize() >= t2.getSize())
				return 1;
			else
				return -1;
	}
}
