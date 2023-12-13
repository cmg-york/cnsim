package cmg.cnsim.bitcoin;

import cmg.cnsim.engine.Debug;
import cmg.cnsim.engine.IStructure;
import cmg.cnsim.engine.Simulation;
import cmg.cnsim.engine.transaction.ITxContainer;
import cmg.cnsim.engine.transaction.Transaction;

import java.util.ArrayList;
import java.util.Collections;

/**
 * The Bitcoin's blockchain structure
 * 
 * @author Sotirios Liaskos for the Conceptual Modeling Group @ York University
 * 
 */
public class Blockchain implements IStructure {

	/**
	 * A list of blocks. Each {@linkplain Block}'s getParent() points to a parent {@linkplain Block}
	 */
	ArrayList<Block> blockchain = new ArrayList<Block>();
	
	/**
	 * List of orphan {@linkplain Block}s. {@linkplain Block}s end up here if they refer to a parent that does not exist in the blockchain (e.g., has delayed arrival). At key events the orphan {@linkplain Block}s are revisited. 
	 */
	ArrayList<Block> orphans = new ArrayList<Block>();

	/**
	 * A list of tips.
	 */
	ArrayList<Block> tips = new ArrayList<Block>();
	
	
	
	
	/**
	 * @deprecated
	 */
	public void _______BlockArrival() {}
	
	
	/**
	 * Crudely request the addition of a validated {@linkplain Block block} to the blockchain. 
	 * 
	 * If the {@linkplain Block} has a parent (e.g., it has arrived from another node) the parent must be found (by ID) and the {@linkplain Block} must be appended on that parent. If parent is not found it is placed in orphans. 
	 * 
	 * If the {@linkplain Block} does not have parent (e.g., {@linkplain Block block} was just validated by the {@linkplain BitcoinNode Node} itself) it must be appended on the tallest non-overlapping path to genesis. In reality, the node will in every hash be aware of the parent of the node its it trying to validate and update it based on new events. 
	 *  
	 * @param b A validated block to be added to the chain. 
	 */
	public void addToStructure(Block b) {
		if (b.hasParent()) {
			// Typically it has parent when it is coming from the orphans list or propagation.
			placeBlockInChain(b);
		} else {
			// Has no parent when it is coming from own validation.
			pushBlockToChain(b);
		}
	}
	


	/**
	 * The {@linkplain Block} has parent, i.e. is the result of propagation. If the parent does not exist in the blockchain, add the {@linkplain Block} to the orphans. If it is found, first check for overlaps with the chain (checking transaction IDs).  If those are not found, then just add the {@linkplain Block} to the blockchain with that parent. Replace the parent in the tips list and update the height of the {@linkplain Block}. If overlaps are found, then the {@linkplain Block} is corrupt and must be discarded. 
	 * @param b The {@linkplain Block} to be added to the blockchain.
	 */
	private void placeBlockInChain(Block b) {
		//Find the parent
		Block parent = (Block) findParentOf(b);
		if (parent == null) {
			addToOrphans(b);
		} else {
			//Debug.p("Propagated Block " + b.getID());
			if (!hasChainOverlap(b,parent)) {
				//Debug.p("----> * Block OK, adding on " + parent.getID());

				// Just add the block
				b.setHeight(parent.getHeight() + 1);
				blockchain.add(b);

				//Replace parent with block
				tips.remove(parent);
				tips.add(b);
				
				//Report event.
				BitcoinReporter.reportBlockEvent(b.getContext().simTime, b.getContext().sysTime, b.getContext().nodeID,
						b.getID(),b.getParent().getID(),b.getHeight(),b.printIDs(";"),
						"Appended On Chain (w/ parent)", b.getContext().difficulty,b.getContext().cycles);
				processOrphans();
			} else {
				Debug.p("discarding overlapping block");
				BitcoinReporter.reportBlockEvent(b.getContext().simTime, b.getContext().sysTime, b.getContext().nodeID,
						b.getID(),b.getParent().getID(),b.getHeight(),b.printIDs(";"),
						"Discarded due to overlap with parent's chain", b.getContext().difficulty,b.getContext().cycles);

			}
		}
	}

	
	
	
	//For new parentless blocks. You need to find the tip
	/**
	 * Pushes a newly validated {@linkplain Block block} without parents (e.g., just validated by the node) to blockchain. 
	 * 
	 * It gets the tallest non-overlapping tip and appends the block there while replacing the block's parent with the block in the tips list. 
	 * 
	 * While a block always contains a parent while being validated, we assume here that the parent is selected after validation. This normally does not cause any violence to the consensus proceedings, given that such parents is guaranteed to exist (mining pool is always valid and non-overlapping). In case it does, an error is printed in the Log file. 
	 * 
	 * If the chain is empty, make the block a genesis block with null as a parent. 
	 * 
	 * @param b The block to be pushed to the blockchain
	 * 
	 */
	private void pushBlockToChain(Block b) {
		
		//Nonempty blockchain - find the tallest non conflicting tip
		if (!blockchain.isEmpty()) {

			Block par = this.getNonOverlappingTip(b);
			if (par != null) {
				//Prepare and block to structure
				b.setParent(par);
				b.setHeight(par.getHeight() + 1);
				blockchain.add(b);
				tips.add(b);
				tips.remove(b.getParent());
				BitcoinReporter.reportBlockEvent(b.getContext().simTime, b.getContext().sysTime, b.getContext().nodeID,
						b.getID(),((b.getParent() == null) ? -1 : b.getParent().getID()),b.getHeight(),b.printIDs(";"),
						"Appended On Chain (parentless)", b.getContext().difficulty,b.getContext().cycles);
				
				processOrphans();
			
			} else {
				//Do nothing, block should be discarded.
				BitcoinReporter.reportBlockEvent(b.getContext().simTime, b.getContext().sysTime, b.getContext().nodeID,
						b.getID(),((b.getParent() == null) ? -1 : b.getParent().getID()),b.getHeight(),b.printIDs(";"),
						"Discarding due to chain overlap", b.getContext().difficulty,b.getContext().cycles);
			}
		} else {
			//It is a genesis block
			b.setParent(null); // it was already but for clarity
			b.setHeight(1);
			blockchain.add(b);
			tips.add(b);
			
			processOrphans();
		}
		
	}
	
	/**
	 * @deprecated
	 */
	public void _______OrphansManagement() {}
	
	/**
	 * Recursive function: for each orphan in the orphans list try to place in the chain using placeBlockInChain. Repeat this loop (through a recursive call) until no orphan in the list can be placed in the chain.  
	 */
	private void processOrphans() {
		ArrayList<Block> orphansCpy = new ArrayList<Block>(orphans);
		int initNumOrphans = orphans.size();
		orphans.clear();
		for (Block b:orphansCpy) {
			placeBlockInChain(b);
		}
		if ( (orphans.size() < initNumOrphans) && (orphans.size() > 0) ){
			// do it again if the loop above actually affected the blockchain, and there are still orphans in the list.  
			processOrphans();
		}
	}

	

	/**
	 * Adds a block to the orphans list. 
	 * @param b The block to be added.
	 */
	private void addToOrphans(Block b) {
		orphans.add(b);
		BitcoinReporter.reportBlockEvent(b.getContext().simTime, b.getContext().sysTime, b.getContext().nodeID,
				b.getID(),b.getParent().getID(),-1,b.printIDs(";"),
				"Added to Orphans", b.getContext().difficulty,b.getContext().cycles);
	}
	
	/**
	 * @deprecated
	 */
	public void _______Overlaps() {}
	
	
	/**
	 * Checks if any of the transactions in Block b exist in any of the transactions that are contained in blocks starting from tip and to the genesis block. 
	 * @param b The block to check the overlap for.
	 * @param tip The tip of the chain to check the overlap against.
	 * @return <tt>true</tt> if overlap exists false otherwise
	 */
	public boolean hasChainOverlap(Block b, Block tip) {
		Block pointer = tip;
		boolean overlapExists = false;
		
		//Do the below while you have not reached and have not found an overlap 
		while (pointer!=null && !overlapExists) {
			//Debug.p("----> Checking node " + pointer.getID() + " with transactions " + pointer.printIDs(","));
			
			//Flip overlapExists if overlap is found.
			overlapExists = (overlapExists || pointer.overlapsWith(b)); 
						
			//if (overlapExists) Debug.p("----> Found overlap, abandonging branch."); 
			// move towards the 
			pointer = (Block) pointer.getParent();
		}
		return(overlapExists);
	}
	
	

	/**
	 * Given a Block b find the tallest tip whose chain to the genesis does not contain transactions whose ID matches one of the IDs in the transactions in b. Searches form tallest to shortest. Return null if such tip is not found.
	 * @param b The block in question. 
	 * @return The tallest tip whose chain to genesis does not overlap with b. Returns <tt>null</tt> if such tip were not found.
	 */
	public Block getNonOverlappingTip(Block b) {
		Block t,winningTip = null;
		boolean found = false;

		// Sort tips by height
		Collections.sort(this.tips, new BlockHeightComparator());
		//Debug.p("Placing Block: " + b.getID() + " with transactions " + b.printIDs(","));

		// Loop tips from tallest to shortest
		for (int i=0; (i < this.tips.size()) && !found;i++) {
			t = this.tips.get(i);
			//Debug.p("--> Trying tip " + t.getID() + " with height " + t.getHeight() + " and transactions " + t.printIDs(","));
			//Check for overlaps
			if (!hasChainOverlap(b,t)) {
				//Debug.p("--> " + t.getID() + " it is!");
				found = true;
				winningTip = t;
			} else {
				//Debug.p("--> " + t.getID() + " does not work.");
			}
		}
		return (winningTip);
	}
	
	
	/**
	 * @deprecated
	 */
	public void _______Queries() {}
	
	
	/**
	 * Finds the parent of a {@linkplain Block} b.
	 * @param b The {@linkplain Block} whose parent is to be found. 
	 * @return A pointer to an object implementing ITxContainer (e.g., a {@linkplain Block})
	 */
	private ITxContainer findParentOf(Block b) {
		Block parent = (Block) b.getParent();
		Block found = null;
		for (Block l : blockchain) {
			if (l == parent) {
				found = l;
				break;
			}
		}
		return(found);
	}

	/**
	 * Checks if a {@linkplain Transaction} is contained (anywhere) in the blockchain. Likely to be used in the gossip stage.
	 *
	 * @param t The {@linkplain Transaction} to be checked.
	 * @return <tt>true</tt> if it is contained, <tt>false</tt> if it is not.
	 */
	public boolean contains(Transaction t) {
		boolean found = false;
		for (Block b : blockchain) {
			for (Transaction r:b.getGroup()) {
				if (r.getID() == t.getID()) found = true;
			}
		}
		return found;
	}

	/**
	 * Checks if a {@linkplain Block} is contained in the blockchain. Search by ID.
	 * @param block Is the {@linkplain Block} to be checked.
	 * @return Return <tt>true</tt> if it is contained <tt>false</tt> otherwise.
	 */
	public boolean contains(Block block) {
		// Start checking from the parent of the block passed
		int counter=1;
		if (block.getParent() == null) {
			return false;
		}
		Block current = (Block) block.getParent();

		// Traverse the parental structure from the parent of the given block
		while (current != null) {
			counter++;
			boolean found1 = false, found2 = false;

			// Check if any block in the blockchain overlaps with the current block

			if (block.overlapsWith(current)) found1 = true;
			if (block.overlapsWithbyObj(current)) found2 = true;

				// Assert to ensure consistency between overlapsWith and overlapsWithbyObj methods
			assert(!(found1 ^ found2));

			if (found1 || found2) {
				System.out.println("Block " + block.getID() + " is contained in the blockchain at height " + counter);
				return true; // Found the block in the parental structure
				}

			// Move to the next parent in the chain
			current = (Block) current.getParent();
		}

		return false; // Block not found in the parental structure
	}


	/**
	 * @deprecated
	 */
	public void ______Printing(){}
		
	
	/**
	 * Print structure for direct presentation. Returns comma separated entries of the format: BlockID,ParentID,BlockHeight,Transactions
	 */
	@Override
	public String[] printStructure() {
		ArrayList<String> result = new ArrayList<String>();
		String s,par;
		result.add("BlockID,ParentID,BlockHeight,Transactions");
		Collections.sort(blockchain, new BlockHeightComparator());
		for (Block b: blockchain) {
			if (b.hasParent()) {
				par = "" + b.getParent().getID();
			} else {
				par = "" + -1;
			}
			s = b.getID() + "," + par + "," + b.getHeight() + "," + b.printIDs(",");
			result.add(s);
		}
		return (result.toArray(new String[result.size()]));
	}

	
	/**
	 * Like <tt>printStructure()</tt> but with additional information including the ID of the node it exists. 
	 * @param nodeID Node ID to be included in the report.
	 * @return An array of comma separated entries with format: SimTime, SysTime, (both at the time of the report) NodeID, BlockID, ParentID, Height, Content (list of transactions), Place (blockchain).
	 */
	public String[] printStructureReport(int nodeID) {
		ArrayList<String> result = new ArrayList<String>();
		String s,par;
		Long realTime = System.currentTimeMillis();
		Collections.sort(blockchain, new BlockHeightComparator());
		for (Block b: blockchain) {
			if (b.hasParent()) {
				par = "" + b.getParent().getID();
			} else {
				par = "" + -1;
			}
			//SimTime, SysTime, NodeID, BlockID, ParentID, Height, Content, Place
			s = Simulation.currTime + "," + realTime + "," +  nodeID + "," +  b.getID() + "," + par + "," + b.getHeight() + "," + b.printIDs(";") + ", blockchain";
			result.add(s);
		}
		return (result.toArray(new String[result.size()]));
	}
	
	/**
	 * Prints the orphans list.
	 * @return An array of comma separated entries, one orphan per entry, in the format: BlockID, ParentID, Transactions (list of contained transaction IDs).
	 */
	public String[] printOrphans() {
		ArrayList<String> result = new ArrayList<String>();
		String s;
		result.add("BlockID,ParentID,Transactions");
		for (Block b: orphans) {
			s = b.getID() + "," + b.getParent().getID() + "," + b.printIDs(",");
			result.add(s);
		}
		return (result.toArray(new String[result.size()]));
	}
	
	/**
	 * Like {@link Blockchain#printOrphans()} with additional information.
	 * @param nodeID The node ID to be reported.
	 * @return  An array of comma separated entries, one orphan per entry, in the format: SimTime, SysTime, (both at the time of the report) NodeID, BlockID, ParentID, Height, Content (list of transactions), Place (blockchain).
	 */
	public String[] printOrphansReport(int nodeID) {
		ArrayList<String> result = new ArrayList<String>();
		String s;
		Long realTime = System.currentTimeMillis();
		for (Block b: orphans) {
			//SimTime, SysTime, NodeID, BlockID, ParentID, Height, Content, Place
			s = Simulation.currTime + "," + realTime + "," + nodeID + "," +  b.getID() + "," + b.getParent().getID() + ",-1," + b.printIDs(";") + ", orphans";
			result.add(s);
		}
		return (result.toArray(new String[result.size()]));
	}
		
    /**
     * Prints the IDs of the {@linkplain Block}s currently in the list of tips
     * @param sep The character to separate the entries with (e.g. "," for comma)
     * @return A string listing the IDs of the tips.
     */
    public String printTips(String sep) {
    	String s = "{";
    	for(Block t:tips) {
    		s += t.getID() + sep;
    	}
    	if (s.length()>1) 
    		s = s.substring(0, s.length()-1) + "}";
    	else 
    		s = s + "}";
    	return (s);
    }

	public int getBlockchainHeight() {
		int maxHeight = 0;
		for (Block block : blockchain) {
			if (block.getHeight() > maxHeight) {
				maxHeight = block.getHeight();
			}
		}
		return maxHeight;
	}

	/**
	 * Returns the tip with the longest height.
	 * @return The Block with the longest height from the tips list. If the list is empty, returns null.
	 */
	public Block getLongestTip() {
		if (tips.isEmpty()) {
			return null;
		}

		Block longestTip = tips.get(0);
		for (Block tip : tips) {
			if (tip.getHeight() > longestTip.getHeight()) {
				longestTip = tip;
			}
		}
		return longestTip;
	}




}
