package ca.yorku.cmg.cnsim.engine;

import ca.yorku.cmg.cnsim.engine.event.Event;
import ca.yorku.cmg.cnsim.engine.event.EventTimeComparator;
import ca.yorku.cmg.cnsim.engine.event.Event_NewTransactionArrival;
import ca.yorku.cmg.cnsim.engine.network.AbstractNetwork;
import ca.yorku.cmg.cnsim.engine.node.NodeSet;
import ca.yorku.cmg.cnsim.engine.transaction.Transaction;
import ca.yorku.cmg.cnsim.engine.transaction.TransactionWorkload;

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
	public static long sysEndTime;
	public static int currentSimulationID = 1;

	
	
	private int simID;
		
	private final EventTimeComparator comp = new EventTimeComparator();
	protected PriorityQueue<Event> queue = new PriorityQueue<>(comp);
	
	private AbstractNetwork net;
	//protected AbstractSampler sampler;
	protected Sampler sampler;


	public int totalqueuedTransactions = 0;
		
	private long latestKnownEventTime = 0;
	private long terminationTime = 0;
	
	private long numEventsScheduled = 0;
	private long numEventsProcessed = 0;
	
	public long getLatestKnownEventTime() {
		return latestKnownEventTime;
	}

	public long getNumEventsScheduled() {
		return numEventsScheduled;
	}
	
	public long getNumEventsProcessed() {
		return numEventsProcessed;
	}

	public void setTerminationTime(long terminationTime) {
		this.terminationTime = terminationTime;
	}

	

	public String getStatistics() {
		String s;
		s = "    Total Simulation Time: " + currTime + " (ms)\n";
		s = s + "    Total Real Time: " + (sysEndTime - sysStartTime) + " (ms)\n";
		s = s + "    Speed-up factor: " + currTime/(sysEndTime - sysStartTime) + "\n";
		s = s + "    Total Events Scheduled: " + numEventsScheduled + "\n";
		s = s + "    Total Events Processed: " + numEventsProcessed + "\n";
		return(s);
	}
	



	public Simulation(int simID) {
		this.simID = simID;
		currentSimulationID = simID;
	}

	
	public Simulation(Sampler a, int simID) {
		this(simID);
	    this.sampler = a;
	}
	
	public int getSimID() {
		return (simID);
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
	 
	
	public PriorityQueue<Event> getQueue() {
		return queue;
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
		if (e.getTime() > this.latestKnownEventTime) {
			this.latestKnownEventTime = e.getTime();
		}
		numEventsScheduled++;
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
		for (Transaction t : ts.getTransactions()) {
			if (t.getNodeID() == -1) {
				e = new Event_NewTransactionArrival(t, this.net.getNodeSet().pickRandomNode(), t.getCreationTime());
			} else {
				e = new Event_NewTransactionArrival(t, this.net.getNodeSet().pickSpecificNode(t.getNodeID()), t.getCreationTime());
			}
	        this.schedule(e);
		}
		this.totalqueuedTransactions += ts.getCount();
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
	        numEventsProcessed++;
            Simulation.currTime = e.getTime();
            if (Simulation.currTime > this.terminationTime) {
            	System.out.println("\n\n    Sim #" + this.getSimID() + ": reached termination time. Ignoring remaining queue and exiting.");
            	break;
            }
            e.happen(this);
	    }
		sysEndTime = System.currentTimeMillis();
	}
}