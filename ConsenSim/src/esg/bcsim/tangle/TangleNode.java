package esg.bcsim.tangle;

import cmg.cnsim.engine.IStructure;
import cmg.cnsim.engine.Simulation;
import cmg.cnsim.engine.node.INode;
import cmg.cnsim.engine.node.Node;
import cmg.cnsim.engine.transaction.ITxContainer;
import cmg.cnsim.engine.transaction.Transaction;


public class TangleNode extends Node {

	protected Tangle tangle;
	protected BehaviorType behaviorType;
	
	public BehaviorType getBehavior() {
		return behaviorType;
	}

	public void setBehavior(BehaviorType behavior) {
		behaviorType  = behavior;
	}
	
	
	public TangleNode(Simulation sim) {
		super(sim);
        tangle = new Tangle();
    }

	
	public IStructure getStructure() {
	    return tangle;
	}

	public void setTangle(Tangle tangle) {
	    this.tangle = tangle;
	}

	
	
	//
	//
	// A C T I O N S  
	//
	//

	public void startMiningTransaction(ITxContainer txc, long time) {
		long h = scheduleValidationEvent(txc, time);
	    startMining();
	     // An estimate of the giga-hashes dedicated.
	    // time (s) *hashPower (GH/s)
	    super.startMining((h/1000)); 
	}


	private void setUpNextValidationEvent(long time) {
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
        setUpNextValidationEvent(time);
	}

	
	// R E P O R T I N G    R E S P O N S I B I L I T I E S
	
	@Override
	public void periodicReport() {
		TangleReporter.flushTangleState(sim.getNodeSet());
	}

	public void timeAdvancementReport() {
		TangleReporter.timeAdvancement(this);
	}
	
	public int getTipNo() {
		return(tangle.getTipCount());
	}

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