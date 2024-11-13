package ca.yorku.cmg.cnsim.engine.event;

import ca.yorku.cmg.cnsim.engine.IMultiSowable;
import ca.yorku.cmg.cnsim.engine.Simulation;
import ca.yorku.cmg.cnsim.engine.reporter.Reporter;


public class Event_SeedUpdate extends Event {
	IMultiSowable sampler;
	long randomSeed;
	
    
    public Event_SeedUpdate(IMultiSowable sampler, long time){
    	super();
    	this.sampler = sampler;
    	super.setTime(time);
    }
    

    /**
     * Executes the event in the simulation, by calling the {@linkplain }
     *
     * @param sim The simulation instance.
     */
    @Override
    public void happen(Simulation sim) {
        super.happen(sim);
        sampler.updateSeed();
        Reporter.addEvent(
        		sim.getSimID(),
        		this.getEvtID(), 
        		this.getTime(), 
        		System.currentTimeMillis() - Simulation.sysStartTime, 
        		this.getClass().getSimpleName(), 
        		-1, 
        		-1);
    }
}
