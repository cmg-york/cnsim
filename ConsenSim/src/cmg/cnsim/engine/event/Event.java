package cmg.cnsim.engine.event;

import cmg.cnsim.engine.Config;
import cmg.cnsim.engine.Simulation;
import cmg.cnsim.engine.node.INode;
import cmg.cnsim.engine.transaction.ITxContainer;
import cmg.cnsim.engine.transaction.Transaction;

/**
 * The Event object for the event-driven simulator
 * @author Sotirios Liaskos for the Conceptual Modeling Group @ York University
 * 
 */
public class Event {
	public static long currID = 1;
    public static long lastTransaction;
	public static long currEvt; 
	
	protected boolean ignore = false;
	
    private long time;
    
	
	static {
		currEvt = 0;
	}
	
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
     * Checks if the event should be ignored. Useful for when cancelling future events.
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
     * Executes the event in the simulation. Call node's periodic and time advancement reports. 
     *
     * @param sim The simulation instance.
     */
    public void happen(Simulation sim){
    	// Every little while ask node if it wants to print any period reports.
    	if ((currID % Config.getPropertyLong("sim.reporting.window")) == 0) {
    		for (INode n : sim.getNodeSet().getNodes()) {
    			n.periodicReport();
    		}
    	}

    	// Ask node if it wants to print Node report.    	
		for (INode n : sim.getNodeSet().getNodes()) {
			n.timeAdvancementReport();
		}
    	
    	//long elapsed = (System.currentTimeMillis() - Profiling.simBeginningTime);
    	//long estimated = (Math.round(((float) elapsed/currEvt)*Parameters.numTransactions*(Parameters.NumofNodes + 1))) - elapsed;
    	//long estimated = (Math.round(((float) elapsed/currEvt)*Config.getPropertyLong("workload.numTransactions")*(Config.getPropertyInt("net.numOfNodes") + 1))) - elapsed;
    	
       }

    
    
    
    
	public void debugPrint(String msg, Transaction tx, INode n, long tim) {
		System.out.println(msg + tx.getID() + " node " + n.getID() + " time " + tim);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {e.printStackTrace();}
	}

	public void debugPrint(String msg, ITxContainer txc, INode n, long tim) {
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


