package cmg.cnsim.engine;

import cmg.cnsim.engine.event.Event;
import cmg.cnsim.engine.event.EventTimeComparator;
import cmg.cnsim.engine.event.Event_NewTransactionArrival;
import cmg.cnsim.engine.network.AbstractNetwork;
import cmg.cnsim.engine.node.NodeSet;
import cmg.cnsim.engine.transaction.Transaction;
import cmg.cnsim.engine.transaction.TransactionWorkload;

import java.util.PriorityQueue;
/**
 * The central class of any simulation
 *  
 * @author Sotirios Liaskos for the Conceptual Modeling Group @ York University
 * 
 */
public class Simulation {

	public static long currTime;
	public static long sysStartTime;
	
	private final EventTimeComparator comp = new EventTimeComparator();
	protected PriorityQueue<Event> queue = new PriorityQueue<>(comp);
	
	private AbstractNetwork net;
	//protected AbstractSampler sampler;
	protected Sampler sampler;

//	public Simulation(AbstractSampler a) {
	public Simulation(Sampler a) {
		super();
	    this.sampler = a;
	}

	
	/**
	 * @deprecated
	 */
	public void ____________Components() {}

	
	/**
	 * Retrieves the network associated with this Simulation object.
	 *
	 * @return The AbstractNetwork object representing the network.
	 */
	public AbstractNetwork getNetwork() {
	    return net;
	}

	
	/**
	 * Sets the network associated with this Simulation object.
	 *
	 * @param net The AbstractNetwork object to be set as the network.
	 */
	public void setNetwork(AbstractNetwork net) {
	    this.net = net;
	}

	
	/**
	 * Retrieves the Sampler object associated with this Simulation object.
	 *
	 * @return The Sampler object representing the sampler.
	 */
	public Sampler getSampler() {
	    return sampler;
	}

	
	/**
	 * Sets the Sampler associated with this object.
	 *
	 * @param sampler The Sampler object to be set as the sampler.
	 */
	public void setSampler(Sampler sampler) {
	    this.sampler = sampler;
	}
	
	
	/**
	 * Retrieves the NodeSet (set of participating nodes) from the associated Network object.
	 *
	 * @return The NodeSet object representing the set of nodes in the network.
	 */
	public NodeSet getNodeSet() {
	    return(this.net.getNodeSet());
	}
	 
	
	/**
	 * @deprecated
	 */
	public void ____________Event_Scheduling() {}
	
	/**
	 * Schedules an event by adding it to the queue.
	 *
	 * @param e The Event object to be scheduled.
	 */
	public void schedule(Event e) {
	    queue.add(e);
	}

	
	/**
	 * Schedules a set of transactions given in the form of a TransactionWorkload object by adding them to the events queue.
	 * For each transaction in the TransactionSet, an Event_NewTransactionArrival event is created and scheduled.
	 * If the transaction's nodeID is -1, a random node from the network's NodeSet is selected.
	 * Otherwise, the transaction is assigned to the specific node with the given nodeID.
	 *
	 * @param ts The TransactionWorkload object containing the set of transactions to be scheduled.
	 */
	public void schedule(TransactionWorkload ts) {
        Event_NewTransactionArrival e;
		for (Transaction t : ts.getGroup()) {
			if (t.getNodeID() == -1) {
				e = new Event_NewTransactionArrival(t, this.net.getNodeSet().pickRandomNode(), t.getCreationTime());
			} else {
				e = new Event_NewTransactionArrival(t, this.net.getNodeSet().pickSpecificNode(t.getNodeID()), t.getCreationTime());
			}
	        this.schedule(e);
		}
	}

	
	/**
	 * @deprecated
	 */
	public void ____________Main_Loop() {}
	
	
	/**
	 * Runs the main loop of the simulation.
	 * The method continuously processes events from the queue until it is empty.
	 * For each event in the queue, the method performs the following steps:
	 * 1. Retrieves the next event from the front of the queue by using the `poll` method, which also removes it from the queue.
	 * 2. Updates the current global simulation time to match the time of the retrieved event.
	 * 3. Calls the `happen` method on the event object, passing the current object (`this`) as an argument to handle the event.
	 */
	
	public void run() {
	    //MainLoop
		sysStartTime = System.currentTimeMillis();
	    Event e;
	    while (!queue.isEmpty()){
	        e = queue.poll(); //it removes the last element of the queue
            Simulation.currTime = e.getTime();
            e.happen(this);
	    }
	}
}