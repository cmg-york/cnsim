package cmg.cnsim.engine.event;

import cmg.cnsim.engine.Simulation;
import cmg.cnsim.engine.node.INode;
import cmg.cnsim.engine.reporter.Reporter;
import cmg.cnsim.engine.transaction.Transaction;


/**
 * Represents an event of a new transaction arrival in the simulation.
 * @author Sotirios Liaskos for the Conceptual Modeling Group @ York University
 * 
 */
public class Event_NewTransactionArrival extends Event {
    private Transaction transaction;
    private INode node;

    
    /**
     * Constructs a new Event_NewTransactionArrival.
     *
     * @param tx    The new transaction.
     * @param n     The node where the transaction arrives.
     * @param time  The simulation time at which the event occurs.
     */
    public Event_NewTransactionArrival(Transaction tx, INode n, long time) {
    	super();
        this.node = n;
        this.transaction = tx;
        super.setTime(time);
    }


    /**
     * Executes the event in the simulation and adds the corresponding logs.
     *
     * @param sim The simulation instance.
     */
    @Override
    public void happen(Simulation sim) {
        super.happen(sim);
        node.event_NodeReceivesClientTransaction(transaction, getTime());
        Reporter.addEvent(
        		sim.getSimID(),
        		getEvtID(), 
        		getTime(), 
        		System.currentTimeMillis() - Simulation.sysStartTime, 
        		this.getClass().getSimpleName(), 
        		node.getID(), 
        		transaction.getID());
        Reporter.addTx(
        		sim.getSimID(),
        		transaction.getID(), 
        		transaction.getSize(), 
        		transaction.getValue(),
        		getTime());
    }
}
