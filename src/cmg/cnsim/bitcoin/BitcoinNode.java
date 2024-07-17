package cmg.cnsim.bitcoin;

import cmg.cnsim.engine.Config;
import cmg.cnsim.engine.IStructure;
import cmg.cnsim.engine.Reporter;
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
	private NodeBehaviorStrategy behaviorStrategy;

	protected TransactionGroup miningPool;
	public Blockchain blockchain;

	protected Double operatingDifficulty;
	protected long minValueToMine;
	protected long minSizeToMine;

	public void _______________Constructors() {}

	public BitcoinNode(Simulation sim) {
		super(sim);
		blockchain = new Blockchain();
		miningPool = new TransactionGroup();
		minValueToMine = Config.getPropertyLong("bitcoin.minValueToMine");
		minSizeToMine = Config.getPropertyLong("bitcoin.minSizeToMine");
		this.operatingDifficulty = Config.getPropertyDouble("pow.difficulty");
	}
	public BitcoinNode(Simulation sim, NodeBehaviorStrategy behaviorStrategy) {
		super(sim);
		this.behaviorStrategy = behaviorStrategy;
		blockchain = new Blockchain();
		miningPool = new TransactionGroup();
		minValueToMine = Config.getPropertyLong("bitcoin.minValueToMine");
		minSizeToMine = Config.getPropertyLong("bitcoin.minSizeToMine");

		this.operatingDifficulty = Config.getPropertyDouble("pow.difficulty");
	}



	public TransactionGroup getMiningPool() {
		return miningPool;
	}


	public void setMiningPool(TransactionGroup miningPool) {
		this.miningPool = miningPool;
	}


	public void setStructure(Blockchain blockchain) {
		this.blockchain = blockchain;
	}


	public void setOperatingDifficulty(Double operatingDifficulty) {
		this.operatingDifficulty = operatingDifficulty;
	}


	@Override
	public IStructure getStructure() {
		return blockchain;
	}


	public void _______________PoW_and_Mining() {}

	public void setOperatingDifficulty (double dif) {
		this.operatingDifficulty = dif;
	}

	public double getOperatingDifficulty () {
		return (this.operatingDifficulty);
	}


	public long getMinValueToMine() {
		return minValueToMine;
	}


	public void setMinValueToMine(long minValueToMine) {
		this.minValueToMine = minValueToMine;
	}


	public long getMinSizeToMine() {
		return minSizeToMine;
	}

	public void setMinSizeToMine(long minSizeToMine) {
		this.minSizeToMine = minSizeToMine;
	}


	protected void considerMining(long time) {
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


	public boolean isWorthMining() {
		return((miningPool.getValue() > getMinValueToMine()));
	}

	protected void reconstructMiningPool() {
		miningPool  = pool.getTopN(Config.getPropertyLong("bitcoin.maxBlockSize"), new TxValuePerSizeComparator());
	}

	public void _______________OtherFunctions() {}


	protected void transactionReceipt(Transaction t, long time) {
		addTransactionToPool(t);
		reconstructMiningPool();
		considerMining(time);
	}


	@Override
	public void close(INode n) {
		BitcoinReporter.reportBlockChainState(
				//Simulation.currTime, System.currentTimeMillis(), this.getID(),
				this.blockchain.printStructureReport(this.getID()), 
				this.blockchain.printOrphansReport(this.getID()));
	}


	@Override
	public void event_NodeReceivesClientTransaction(Transaction t, long time) {
		behaviorStrategy.event_NodeReceivesClientTransaction(t, time);
	}


	@Override
	public void event_NodeReceivesPropagatedContainer(ITxContainer t) {
			behaviorStrategy.event_NodeReceivesPropagatedContainer(t);
	}


	public void event_NodeReceivesPropagatedTransaction(Transaction t, long time) {
		behaviorStrategy.event_NodeReceivesPropagatedTransaction(t, time);
	}

	@Override
	public void event_NodeCompletesValidation(ITxContainer t, long time) {
		behaviorStrategy.event_NodeCompletesValidation(t, time);
	}


	public double getProspectiveCycles() {
		return super.prospectiveMiningCycles;
	}

	public void completeValidation(TransactionGroup miningPool, long time) {
		super.event_NodeCompletesValidation(miningPool, time);
		// Any additional logic that needs to be executed after calling the super method
	}

	public void setBehaviorStrategy(NodeBehaviorStrategy strategy) {
		this.behaviorStrategy = strategy;
	}


	public NodeBehaviorStrategy getBehaviorStrategy() {
		return behaviorStrategy;
	}
	
	public Blockchain getBlockchain() {
		return blockchain;
	}

	

	
	//
	// REPORTING ROUTINES
	//
	
	
	
	@Override
	public void timeAdvancementReport() {
		// TODO Auto-generated method stub
	}

	@Override
	public void periodicReport() {
		// TODO Auto-generated method stub
	}

	
	
	@Override
	public void beliefReport(long[] sample, long time) {
		for (int i = 0; i < sample.length; i++) {
			Reporter.addBeliefEntry(this.getID(), sample[i], blockchain.transactionInStructure(sample[i]), time);
		}
	}

	@Override
	public void nodeStatusReport() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void structureReport() {
		// TODO Auto-generated method stub
		
	}

}
