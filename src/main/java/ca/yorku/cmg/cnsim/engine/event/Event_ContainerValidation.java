package ca.yorku.cmg.cnsim.engine.event;

import ca.yorku.cmg.cnsim.engine.Reporter;
import ca.yorku.cmg.cnsim.engine.Simulation;
import ca.yorku.cmg.cnsim.engine.node.INode;
import ca.yorku.cmg.cnsim.engine.transaction.ITxContainer;

/**
 * Represents an event of container validation in the simulation.
 * @author Sotirios Liaskos for the Conceptual Modeling Group @ York University
 * 
 */
public class Event_ContainerValidation extends Event {
    private ITxContainer container;
    private INode node;

    
    /**
     * Constructs a new Event_ContainerValidation.
     *
     * @param txc   The container to validate.
     * @param n     The node where validation took place.
     * @param time  The simulation time at which the event occurs.
     */
    public Event_ContainerValidation(ITxContainer txc, INode n, long time){
    	super();
        this.node = n;
        this.container = txc;
        super.setTime(time);
    }

    /**
     * Executes the event in the simulation. If the event is marked to be ignored an entry 
     * is added to the logs. 
     *
     * @param sim The simulation instance.
     */
    @Override
    public void happen(Simulation sim) {
        super.happen(sim);
        String status = "";
        if (!super.ignoreEvt()) {
        	node.event_NodeCompletesValidation(container, getTime());
        } else {
        	status = "_Abandonded";
        }
        Reporter.addEvent(
        		sim.getSimID(),
        		getEvtID(), 
        		getTime(), 
        		System.currentTimeMillis() - Simulation.sysStartTime, 
        		this.getClass().getSimpleName() + status, 
        		node.getID(), 
        		container.getID());
    }

}
