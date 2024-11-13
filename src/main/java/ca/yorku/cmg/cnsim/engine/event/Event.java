package ca.yorku.cmg.cnsim.engine.event;

import ca.yorku.cmg.cnsim.engine.Config;
import ca.yorku.cmg.cnsim.engine.Simulation;
import ca.yorku.cmg.cnsim.engine.node.INode;
import ca.yorku.cmg.cnsim.engine.transaction.ITxContainer;
import ca.yorku.cmg.cnsim.engine.transaction.Transaction;

/**
 * The Event object for the event-driven simulator
 * @author Sotirios Liaskos for the Conceptual Modeling Group @ York University
 * 
 */
public class Event {
	
	// The ID of the current event
	public static long currID = 1;
	public long evtID = 1;
	
	//TODO: erase eventually
	//public static long lastTransaction;
	//public static long currEvt; 

	//static {
	//	currEvt = 0;
	//}	
	
	//Whether the event should be ignored.
	protected boolean ignore = false;
	
	// The simulation time of occurrence of the event.
    private long time;

    
	
	/**
	 * Retrieves the next unique event ID.
	 *
	 * @return The next event ID.
	 * @author Sotirios Liaskos
	 */
	public static long getNextEventID() {
		return(currID++);
	}
	

    
    /**
     * Sets the time value of the event.
     *
     * @param time The time value to set (time greater than 0).
     * @throws ArithmeticException if the provided time value is less than 0.
     */
    public void setTime(long time) {
    	if(time < 0)
    		throw new ArithmeticException("Time < 0");
        this.time = time;
    }

    /**
     * Retrieves the time of the event.
     *
     * @return The simulation time of the event.
     */
    public long getTime() {
        return time;
    }
    
    /**
     * Checks if the event should be ignored. Useful for when canceling future events.
     *
     * @return {@code true} if the event should be ignored, {@code false} otherwise.
     */
    public boolean ignoreEvt() {
    	return ignore;
    }
    
    
    /**
     * Sets the ignore status of the event.
     *
     * @param ignoreEvt true to ignore the event, false otherwise.
     */
    public void ignoreEvt(boolean ignoreEvt) {
    	ignore = ignoreEvt;
    }
    
	/**
	 * The ID of the current event object. IDs are created at the time of processing the event.
	 * @return The ID of the current event object
	 */
	public long getEvtID() {
		return evtID;
	}
    
    
    /**
     * Executes the event in the simulation. Call node's periodic and time advancement reports. 
     *
     * @param sim The simulation instance.
     */
    public void happen(Simulation sim){
    	evtID = getNextEventID();
 
    	// Every little while ask node if it wants to print any period reports.
    	// TODO: the periodic printing should (also) be based on simulation time.
    	if ((currID % Config.getPropertyLong("sim.reporting.window")) == 0) {
    		for (INode n : sim.getNodeSet().getNodes()) {
    			n.periodicReport();
    		}
    	}

    	// Ask node if it wants to print Node report.
    	// TODO: difference between time advancement report and periodic report.
		for (INode n : sim.getNodeSet().getNodes()) {
			n.timeAdvancementReport();
		}
    	
    }
   
        
    
	/**
	 * For debug purposes. Call this to print the even occurrence in the standard output.
	 * TODO: remove the parameters and make it specific on the event data.
	 * @param msg A message to be printed.
	 * @param tx The relevant transaction.
	 * @param n The relevant node.
	 * @param tim The simulation time.
	 * @param delay The time to delay before continuing.
	 */
	public void debugPrint(String msg, Transaction tx, INode n, long tim, long delay) {
		System.out.println(msg + tx.getID() + " node " + n.getID() + " time " + tim);
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {e.printStackTrace();}
	}

	public void debugPrint(String msg, ITxContainer txc, INode n, long tim, long delay) {
		System.out.println(msg + txc.printIDs(",") + " node " + n.getID() + " time " + tim);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {e.printStackTrace();}
	}

}



/* 
 *  	
//        System.out.printf("%s %,6d %s %,6d %s %s %s %s %s \r", 
//        		"Event:",
//        		currEvt++,
//        		"/",
//        		Parameters.numTransactions*(Parameters.NumofNodes + 1),
//        		"Elapsed Time:",
//        		String.format("%02d:%02d:%02d sec",
//        				((elapsed / (1000*60*60)) % 24),
//        				((elapsed / (1000*60)) % 60),
//        				((elapsed / (1000)) % 60)
//        			),
//        		" Remaining: ",
//        		String.format("%02d:%02d:%02d sec",
//        				((estimated / (1000*60*60)) % 24),
//        				((estimated / (1000*60)) % 60),
//        				((estimated / (1000)) % 60)
//        			),
//        		" (sec)"
//        		);
 
//    	TangleReporter.flushEvtReport();
    	
//        if (this instanceof NewTransactionArrival) evtType = "NewTransactionArrival";
//        if (this instanceof TransactionValidation) evtType = "TransactionValidation";
//        if (this instanceof TransactionPropagation) evtType = "TransactionPropagation";
//        System.out.print("Time:" + String.format("%.4f", this.getTime()) + " s \t Event:" + evtType + "\n");

 */


