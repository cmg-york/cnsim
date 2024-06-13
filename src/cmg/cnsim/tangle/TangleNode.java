package cmg.cnsim.tangle;

import cmg.cnsim.engine.IStructure;
import cmg.cnsim.engine.Simulation;
import cmg.cnsim.engine.node.INode;
import cmg.cnsim.engine.node.Node;
import cmg.cnsim.engine.transaction.ITxContainer;
import cmg.cnsim.engine.transaction.Transaction;


public class TangleNode extends Node {

	//The Tangle structured carried by the node
	protected Tangle tangle;
	
	//The type of behavior (honest, malicious) of the node
	protected BehaviorType behaviorType;
	
	 /**
     * Retrieves the behavior type associated with this instance.
     *
     * @return The behavior type of this instance.
     */
	public BehaviorType getBehavior() {
		return behaviorType;
	}

	
    /**
     * Sets the behavior type for this instance.
     *
     * @param behavior The behavior type to be set.
     */
	public void setBehavior(BehaviorType behavior) {
		behaviorType  = behavior;
	}
	
	
	 /**
     * Constructs a ({@linkplain TangleNode} object with the specified simulation. Initializes a new {@linkplain Tangle} object.
     *
     * @param sim The simulation associated with this TangleNode.
     */
	public TangleNode(Simulation sim) {
		super(sim);
        tangle = new Tangle();
    }

	
    /**
     * Retrieves the structure associated with this TangleNode, which is a {@linkplain Tangle} object.
     *
     * @return The Tangle structure of this TangleNode.
     */
	public IStructure getStructure() {
	    return tangle;
	}

	
	//
	//
	// A C T I O N S  
	//
	//

	/**
	 * Start validating (mining) the transaction.
	 * @param txc A {@plainlink ITxContainer} object to be validated (e.g., a {@link Transaction}
	 * @param time The time of the future validation event.
	 * @author Sotirios Liaskos
	 */
	public void startMiningTransaction(ITxContainer txc, long time) {
		long h = scheduleValidationEvent(txc, time);
	    startMining();
	     // An estimate of the giga-hashes dedicated.
	    // time (s) *hashPower (GH/s)
	    super.startMining((h/1000)); 
	}


	/**
	 * A behavior to call when finishing a mining process. Checks if there are more transactions in the pool
	 * which the node will start mining by calling {@linkplain TangleNode#event_NodeReceivesClientTransaction(Transaction, long) }. 
	 * @param time
	 * @author Sotirios Liaskos
	 */
	private void seeIfThereAreMoreTransactionsInPool(long time) {
	        if(getPool().getCount() > 0){
	            Transaction transaction = getPool().removeNextTx();
	            //pretend that you just received it.
	            event_NodeReceivesClientTransaction(transaction, time);
	        }
	    }

//	public void propagateContainer(ITxContainer txc, long time) {
//	    NodeSet nodes = sim.getNodeSet();
//	    ArrayList<INode> ns_list = nodes.getNodes();
//	    for (INode n : ns_list) {
//	        if (!n.equals(this)){
//	            long inter = sim.getNetwork().getPropagationTime(this.getID(), n.getID(), txc.getSize());
//	            Event_ContainerPropagation e = new Event_ContainerPropagation(txc, n, time + inter);
//	            sim.schedule(e);
//	        }
//	    }
//	}
//	
	
	//
	//
	// E V E N T     H A N D L I N G  
	//
	//
	
	/**
	 * Actions to perform when receiving a new client transaction.
	 * If the Node is not currently mining, start mining the transaction.
	 * If the Node is currently mining, add it in the pool. 
	 */
	@Override
	public void event_NodeReceivesClientTransaction(Transaction t, long time) {
		//System.out.println("Node: (" + this.ID+  ") Receiving New Transaction: " + t.getID());
	    if(!isMining()) {
	    	TangleSite ts = new TangleSite(t);
	    	startMiningTransaction(ts, time);
	    	TangleReporter.appendTangleTransaction(this.getID(), 
					t.getID(),
					-1, 
					-1, 
					Simulation.currTime, 
					"New: Started Mining");
	    }
	    else{
	    	addTransactionToPool(t);
			TangleReporter.appendTangleTransaction(this.getID(), 
					t.getID(),
					-1, 
					-1, 
					Simulation.currTime, 
					"New: Added To Pool");
	    }
	}

	/**
	 * Behavior for node when receiving a validate container t.
	 * Just add it to the Tangle.
	 */
	@Override
	public void event_NodeReceivesPropagatedContainer(ITxContainer t) {
		//System.out.println("Node: (" + this.ID+  ") Receiving Propagated Transaction: " + t.getID());
		tangle.addValidatedTransaction(t);
		TangleReporter.appendTangleTransaction(this.getID(), 
				t.getID(),
				((TangleSite) t).getParents()[0], 
				((TangleSite) t).getParents()[1],
				Simulation.currTime, 
				"Propagated: Considering for addition");
	}

	
	/**
	 * Behavior to engage in when mining is complete.
	 * 1. Add the (now) site to the tangle.
	 * 2. Propagate the site.
	 * 3. stopMining
	 * 4. Check if there are more transaction in the pool waiting. 
	 */
	@Override
	public void event_NodeCompletesValidation(ITxContainer t, long time) {
		super.event_NodeCompletesValidation(t, time);
        
		t = tangle.addNewTransaction(t.getContent()[0]);

        TangleReporter.appendTangleTransaction(this.getID(), 
				t.getID(),
				((TangleSite) t).getParents()[0], 
				((TangleSite) t).getParents()[1], 
				Simulation.currTime,
				"New: Validation Complete");
        propagateContainer(t, time); //
        stopMining();
        seeIfThereAreMoreTransactionsInPool(time);
	}

	
	// R E P O R T I N G    R E S P O N S I B I L I T I E S
	
	@Override
	public void periodicReport() {
		TangleReporter.flushTangleState(sim.getNodeSet());
	}

	public void timeAdvancementReport() {
		TangleReporter.timeAdvancement(this);
	}
	
	/**
	 * Gets the number of tips of the tangle
	 * @return The number of tips of the tangle.
	 * @author Sotirios Liaskos
	 */
	public int getTipCount() {
		return(tangle.getTipCount());
	}

	/**
	 * Retrieve the transaction weight from the Tangle.
	 * @param tx The Transactoin in question
	 * @return The weight of the transaction.
	 * @author Sotirios Liaskos
	 */
	public int getTxWeight(Transaction tx) {
		try {
			return tangle.getTxWeight(tx);
		} catch (Exception e) {e.printStackTrace();return(-1);}
	}

	@Override
	public void close(INode n) {
		System.out.println("Node " + this.getID() + " closing.");
	}

}