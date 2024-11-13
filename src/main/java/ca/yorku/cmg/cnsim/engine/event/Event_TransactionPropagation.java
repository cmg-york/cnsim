package ca.yorku.cmg.cnsim.engine.event;

import ca.yorku.cmg.cnsim.engine.Simulation;
import ca.yorku.cmg.cnsim.engine.node.INode;
import ca.yorku.cmg.cnsim.engine.reporter.Reporter;
import ca.yorku.cmg.cnsim.engine.transaction.Transaction;

/**
 * 
 * Represents an event for transaction propagation in the simulation.
 * It extends the base Event class.
 * 
 * @author Sotirios Liaskos for the Conceptual Modeling Group @ York University
 * 
 */
public class Event_TransactionPropagation extends Event {
    private Transaction trans;
    private INode node;

    
    /**
     * Constructs a new Event_TransactionPropagation object with the specified transaction,
     * node, and time.
     *
     * @param t     The transaction being propagated.
     * @param n     The node receiving the propagated transaction.
     * @param time  The simultion time at which the event occurs.
     */
    public Event_TransactionPropagation(Transaction t, INode n, long time){
    	super();
        this.node = n;
        this.trans = t;
        super.setTime(time);
    }

    /**
     * Performs the actions associated with the transaction propagation event.
     * It invokes the event_NodeReceivesPropagatedTransaction method of the node
     * and records the event in the Reporter.
     *
     * @param sim  The Simulation object.
     */
    @Override
    public void happen(Simulation sim) {
        super.happen(sim);
        node.event_NodeReceivesPropagatedTransaction(trans, getTime());
        Reporter.addEvent(
        		sim.getSimID(),
        		getEvtID(), 
        		getTime(), 
        		System.currentTimeMillis() - Simulation.sysStartTime, 
        		this.getClass().getSimpleName(), 
        		node.getID(), 
        		trans.getID());
    }

}
