package cmg.cnsim.engine.event;

import cmg.cnsim.engine.Reporter;
import cmg.cnsim.engine.Simulation;
import cmg.cnsim.engine.node.INode;
import cmg.cnsim.engine.transaction.ITxContainer;

/**
 * Event that signifies the arrival of a container (e.g. block) that has been validated by someone else.
 * 
 * @author Sotirios Liaskos for the Enterprise Systems Group @ York University
 * 
 *  
 */
public class Event_ContainerArrival extends Event {
	//A pointer to the container
    private ITxContainer container;
    
    //A pointer to the node at which the container arrives
    private INode node;

    
    /**
     * Constructs a new Event_ContainerArrival.
     *
     * @param txc   The container that arrives at the node.
     * @param n     The node at which the container arrives.
     * @param time  The simulation time at which the event occurs.
     */
    public Event_ContainerArrival(ITxContainer txc, INode n, long time){
    	super();
        this.node = n;
        this.container = txc;
        super.setTime(time);
    }
    

    /**
     * Executes the event in the simulation, by calling the {@linkplain INode::event_NodeReceivesPropagatedContainer}
     *
     * @param sim The simulation instance.
     */
    @Override
    public void happen(Simulation sim) {
        super.happen(sim);
        node.event_NodeReceivesPropagatedContainer(container);
        Reporter.addEvent(
        		this.getEvtID(), 
        		this.getTime(), 
        		System.currentTimeMillis() - Simulation.sysStartTime, 
        		this.getClass().getSimpleName(), 
        		node.getID(), 
        		container.getID());
    }

}
