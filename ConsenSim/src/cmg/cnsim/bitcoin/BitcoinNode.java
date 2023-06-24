package cmg.cnsim.bitcoin;

import cmg.cnsim.engine.Config;
import cmg.cnsim.engine.IStructure;
import cmg.cnsim.engine.Simulation;
import cmg.cnsim.engine.node.INode;
import cmg.cnsim.engine.node.Node;
import cmg.cnsim.engine.transaction.ITxContainer;
import cmg.cnsim.engine.transaction.Transaction;
import cmg.cnsim.engine.transaction.TransactionGroup;
import cmg.cnsim.engine.transaction.TxValuePerSizeComparator;

/**
 * @author Enterprise Systems Group (ESG) @ York University
 *
 */
public class BitcoinNode extends Node {

	protected TransactionGroup miningPool;
	protected Blockchain blockchain;
	protected Double operatingDifficulty;
	protected long minValueToMine;
	protected long minSizeToMine;
	
	
	//
	//
	// Constructors
	//
	//
	/**
	 * @deprecated 
	 */
	public void _______________Constructors() {}
	
	public BitcoinNode(Simulation sim) {
		super(sim);
		blockchain = new Blockchain();
		miningPool = new TransactionGroup();
		minValueToMine = Config.getPropertyLong("bitcoin.minValueToMine");
		minSizeToMine = Config.getPropertyLong("bitcoin.minSizeToMine");

		this.operatingDifficulty = Config.getPropertyDouble("pow.difficulty");
	}

	
	
	
	
	//
	//
	// Properties and Parameters
	//
	//
	/**
	 * @deprecated 
	 */
	public void _______________Properties_and_Parameters() {}
	
	
	/**
	 * Retrieves the mining pool associated with this instance.
	 *
	 * @return The mining pool.
	 */
	public TransactionGroup getMiningPool() {
		return miningPool;
	}

	/**
	 * Sets the mining pool for this instance.
	 *
	 * @param miningPool The mining pool to be set.
	 */
	public void setMiningPool(TransactionGroup miningPool) {
		this.miningPool = miningPool;
	}

	/**
	 * Sets the blockchain structure for this instance.
	 *
	 * @param blockchain The blockchain structure to be set.
	 */
	public void setStructure(Blockchain blockchain) {
		this.blockchain = blockchain;
	}

	/**
	 * Sets the operating difficulty for this instance.
	 *
	 * @param operatingDifficulty The operating difficulty to be set. Measured as a (Search Space) / (Success Space) factor. 
	 */
	public void setOperatingDifficulty(Double operatingDifficulty) {
		this.operatingDifficulty = operatingDifficulty;
	}

	
	/**
	 * Retrieves the blockchain structure associated with this instance.
	 *
	 * @return The blockchain structure.
	 */
	@Override
	public IStructure getStructure() {
		return blockchain;
	}

	
	/**
	 * Generates a time advancement report.
	 * This method is typically called every time simulation time advances, i.e. at the processing of each event.
	 * This method should be implemented to provide the specific functionality required for the report.
	 * It is marked as an override method.
	 */
	@Override
	public void timeAdvancementReport() {
		// TODO Auto-generated method stub
	}

	/**
	 * Generates a time advancement report.
	 * This method is typically called fixed amount of simulation time defined by <tt>sim.reporting.window</tt>.
	 * This method should be implemented to provide the specific functionality required for the report.
	 * It is marked as an override method.
	 */
	@Override
	public void periodicReport() {
		// TODO Auto-generated method stub
	}

	
	//
	// PoW Related
	//
	/**
	 * @deprecated 
	 */
	public void _______________PoW_and_Mining() {}
	
	public void setOperatingDifficulty (double dif) {
		this.operatingDifficulty = dif;
	}

	public double getOperatingDifficulty () {
		return (this.operatingDifficulty);
	}
	
	
	//
	//
	// Mining decision making
	//
	//
	
	
	/**
	 * Retrieves the minimum value (in token currency) that the mining block must contain for mining to be worthwhile.
	 *
	 * @return The minimum value required for mining.
	 */
	public long getMinValueToMine() {
		return minValueToMine;
	}

	
	/**
	 * Sets the minimum value (in token currency) that the mining block must contain for mining to be worthwhile.
	 *
	 * @param minValueToMine The minimum value required for mining to be set.
	 */
	public void setMinValueToMine(long minValueToMine) {
		this.minValueToMine = minValueToMine;
	}

	
	/**
	 * Retrieves the minimum size (in bytes) that the mining block must be for mining to be worthwhile.
	 *
	 * @return The minimum size required for mining.
	 */
	public long getMinSizeToMine() {
		return minSizeToMine;
	}

	/**
	 * Sets the minimum size (in bytes) that the mining block must be for mining to be worthwhile.
	 *
	 * @param minSizeToMine The minimum size required for mining to be set.
	 */
	public void setMinSizeToMine(long minSizeToMine) {
		this.minSizeToMine = minSizeToMine;
	}
	
	
	/**
	 * Considers whether to start or stop mining based on the current conditions, via consulting {@linkplain BitcoinNode#isWorthMining()}.
	 * If it is determined to be worth mining, the method starts mining and schedules a new validation event.
	 * If it is not worth mining, the method stops mining and invalidates any future validation event.
	 *
	 * @param time The simulation time at which consideration is taking place 
	 */
	private void considerMining(long time) {
		if (isWorthMining()) {
			//Start mining and schedule a new validation event
			if (!isMining()) {
				//It is not mining because it has never OR it has but then abandoned.
				assert((getNextValidationEvent() == null) || ((getNextValidationEvent() != null) ? getNextValidationEvent().ignoreEvt(): true));
				
				long interval = scheduleValidationEvent(new Block(miningPool.getGroup()), time);
				startMining(interval);
			} else {
				assert((getNextValidationEvent() != null) && !getNextValidationEvent().ignoreEvt());
				//All good!
			}
		} else {
			if (!isMining()) {
				assert((getNextValidationEvent() == null) || getNextValidationEvent().ignoreEvt());
				//All good otherwise!
			} else  {
				// Stop mining, invalidate any future validation event.
				assert((getNextValidationEvent() != null) && !getNextValidationEvent().ignoreEvt());
				getNextValidationEvent().ignoreEvt(true);
				stopMining();
				assert((getNextValidationEvent() == null) || ((getNextValidationEvent() != null) ? getNextValidationEvent().ignoreEvt(): true));
			}
		}
				
	}
		
	
	/**
	 * Determines if it is worth mining based on the current mining pool value and the minimum value required for mining.
	 *
	 * @return {@code true} if it is worth mining (current value exceeds minimum value), {@code false} otherwise.
	 */
	public boolean isWorthMining() {
		return((miningPool.getValue() > getMinValueToMine()));
	}
	
	/**
	 * Reconstructs the mining pool with the most valuable subset of pool transactions that can fit within maximum block size set in <tt>bitcoin.maxBlockSize</tt>. 
	 * The transaction evaluation is performed by ranking pool transaction based on value per size. [Ideal solution would be a knapsack algorithm.] 
	 * This method does not have any return value.
	 */
	private void reconstructMiningPool() {
		miningPool  = pool.getTopN(Config.getPropertyLong("bitcoin.maxBlockSize"), new TxValuePerSizeComparator());
	}
	

	//
	// Other event serving functions
	//
	/**
	 * @deprecated 
	 */
	public void _______________OtherFunctions() {}
	
	
	/**
	 * Processes a transaction receipt from a client by adding the transaction to the pool, reconstructing the mining pool,
	 * and considering whether to start or stop mining based on the new miningpool.
	 *
	 * @param t The transaction to process.
	 * @param time The current simulation time.
	 */
	private void transactionReceipt(Transaction t, long time) {
		addTransactionToPool(t);
		reconstructMiningPool();
		considerMining(time);
	}


	/**
	 * To be called when the node object is not closing though end of simulation or other termination condition.
	 * Current implementation prints a structure report. 
	 *
	 * @param n The {@linkplain INode} implementing object to close.
	 */
	@Override
	public void close(INode n) {
		//System.out.println("Node " + this.getID() + " closing.");
		BitcoinReporter.reportBlockChainState(//Simulation.currTime, System.currentTimeMillis(), this.getID(), 
				this.blockchain.printStructureReport(this.getID()), this.blockchain.printOrphansReport(this.getID()));
	}
	
	
	//
	//
	// Events
	//
	//
	/**
	 * @deprecated 
	 */
	public void _______________Events() {}
	
	
	
	/**
	 * Handles the event when a node receives a client transaction.
	 * Calls {@linkplain BitcoinNode#transactionReceipt(Transaction, long)} and {@linkplain BitcoinNode#propagateTransaction(Transaction, long)}.
	 *
	 * @param t The transaction received.
	 * @param time The current simulation time.
	 */
	@Override
	public void event_NodeReceivesClientTransaction(Transaction t, long time) {
		transactionReceipt(t,time);
		propagateTransaction(t,time);
	}

	
	
	/**
	 * Handles the event when a node receives a propagated container (i.e., a block validated by some other miner).
	 * If the block's transactions are not included in any transaction in the blockchain: update the context information for reporting,
	 * report the block event, attempt to add the block to the blockchain (may end up orphan or stale),
	 * remove the block transactions from the pool, reconstructs the mining pool,
	 * and consider starting or stopping mining based on the current conditions.
	 *
	 * If there is an ovelap between block's transactions and blockchain, discard the block and report the event.
	 * @param t The propagated {@linkplain Block} received.
	 */
	@Override
	public void event_NodeReceivesPropagatedContainer(ITxContainer t) {
		Block b = (Block) t;
		//TODO: updating of context here seems wrong!
		//Update context information for reporting
		b.getContext().simTime = Simulation.currTime;
		b.getContext().sysTime = System.currentTimeMillis();
		b.getContext().nodeID = this.getID();
		b.getContext().blockEvt = "Node Receives Propagated Block";
		b.getContext().cycles = -1;
		b.getContext().difficulty = -1;
		
		// Report a block event
		BitcoinReporter.reportBlockEvent(b.getContext().simTime, b.getContext().sysTime, b.getContext().nodeID,
				b.getID(),((b.getParent() == null) ? -1 : b.getParent().getID()),b.getHeight(),b.printIDs(";"),
				b.getContext().blockEvt, b.getContext().difficulty,b.getContext().cycles);
		
		//TODO: contains should be checking only the parental structure (not the entire blockchain). 
		if (!blockchain.contains(b)) {
			//Add block to blockchain
			blockchain.addToStructure(b);
			//Remove block transactions from pool.
			pool.extractGroup(b);
			// Reconstruct mining pool based on the new information.
			reconstructMiningPool();
			//Consider starting or stopping mining. 
			considerMining(Simulation.currTime);
		} else {
			//Discard the block and report the event.
			BitcoinReporter.reportBlockEvent(b.getContext().simTime, b.getContext().sysTime, b.getContext().nodeID,
					b.getID(),((b.getParent() == null) ? -1 : b.getParent().getID()),b.getHeight(),b.printIDs(";"),
					"Propagated Block Discarded", b.getContext().difficulty,b.getContext().cycles);
		}
	}


	/**
   *  Handles the event when a node receives a propagated transaction.
   * This method checks if the transaction is not already in the Node's pool and not contained in the {@linkplain BitcoinNode#blockchain}.
   * If these conditions are met, the transaction is processed using the {@linkplain BitcoinNode#transactionReceipt(Transaction, long)} method.
   * @param t The propagated transaction received.
   * @param time The current time when the event occurs.
   */
	public void event_NodeReceivesPropagatedTransaction(Transaction t, long time) {
		if (!pool.contains(t) && !blockchain.contains(t)) {
			// Consider transaction only if it is not already in the pool and is not contained in the blockchain.
			transactionReceipt(t,time);
		}
	}
	
	
	/**
	 * Handles the event when a node completes block validation (i.e., solves the hashcash problem.
	 * This method is called when a block has finished validation.
	 * The method performs the following steps:
	 * 1. Adds validation information to the block by invoking the {@linkplain Block#validateBlock(java.util.ArrayList, long, long, int, String, double, double)} method on the block object.
	 * 3. Calls the {@linkplain Node#event_NodeCompletesValidation(ITxContainer, long)} method on the superclass to handle additional validation logic.
	 * 4. Reports the validation event by invoking {@linkplain BitcoinReporter#reportBlockEvent(long, long, int, int, int, int, String, String, double, double)}.
	 * 5. Checks if the block is not already present in the blockchain. If it's not, adds the block to the blockchain structure and propagates it to the rest of the network.
	 * 6. If the block is already present in the blockchain, reports an error indicating the block is being discarded. This shouldn't occur in the simulation.
	 * 7. Stops the mining process temporarily.
	 * 8. Resets the next validation event.
	 * 9. Removes the block's transactions from the mining pool.
	 * 10. Reconstructs the mining pool with any remaining transactions.
	 * 11. Considers whether it is worth continuing mining.
	 *
	 * @param t    The transaction container that completed validation (expected to be a Block object).
	 * @param time The simulation time of the completion event.
	 */
	@Override
	public void event_NodeCompletesValidation(ITxContainer t, long time) {
		// A new block object is created with the current miningPool.
		Block b = (Block) t; 
		
		//Add validation information to the block.
		b.validateBlock(miningPool.getGroup(),
				Simulation.currTime,
				System.currentTimeMillis(),
				this.getID(),
				"Node Completes Validation",
				this.getOperatingDifficulty(),
				super.prospectiveMiningCycles
				);
		super.event_NodeCompletesValidation(miningPool, time);
		
		//Report validation
		//System.out.println("Node:" + this.getID() + " validates " + b.printIDs(";") +" at " + b.getContext().simTime);
		BitcoinReporter.reportBlockEvent(b.getContext().simTime, b.getContext().sysTime, b.getContext().nodeID,
				b.getID(),((b.getParent() == null) ? -1 : b.getParent().getID()),b.getHeight(),b.printIDs(";"),
				b.getContext().blockEvt, b.getContext().difficulty,b.getContext().cycles);
		
		if (!blockchain.contains(b)) {
			//Add block to blockchain
			blockchain.addToStructure(b);
			//Propagate block to the rest of the network
			propagateContainer(b, time);
		} else {
			BitcoinReporter.reportBlockEvent(b.getContext().simTime, b.getContext().sysTime, b.getContext().nodeID,
					b.getID(),((b.getParent() == null) ? -1 : b.getParent().getID()),b.getHeight(),b.printIDs(";"),
					"Discarding own Block (ERROR)", b.getContext().difficulty,b.getContext().cycles);
		}
		
		//Stop mining for now. TODO: why do you do this?
		stopMining();
		//Reset the next validation event. TODO: why do you do this?
		resetNextValidationEvent();
		//Remove the block's transactions from the mining pool.
		removeFromPool(miningPool);
		//Reconstruct mining pool, with whatever other transactions are there.
        reconstructMiningPool();
        //Consider if it is worth mining.
        considerMining(time);
	}


	
}
