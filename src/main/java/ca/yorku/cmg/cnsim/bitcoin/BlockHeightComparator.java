package ca.yorku.cmg.cnsim.bitcoin;

import java.util.Comparator;

public class BlockHeightComparator implements Comparator<Block> {

	
	/**
	 * Compares two {@link cmg.cnsim.bitcoin.Block} objects based on their height.
	 *
	 * @param b1 The first block to compare.
	 * @param b2 The second block to compare.
	 * @return A negative integer if <tt>b1</tt> is shorter than <tt>b2</tt>, a positive integer if <tt>b1</tt> is considered taller than <tt>b2</tt>. If they are equal, compare IDs accordingly.
	 */
	@Override
	public int compare(Block b1, Block b2) {
		if (b1.getHeight() < b2.getHeight())
			return 1;
		else 
			if (b1.getHeight() == b2.getHeight()) 
				if (b1.getID() < b2.getID())
					return 1;
				else
					return -1;
			else //height is greater
				return -1;
	}

}
