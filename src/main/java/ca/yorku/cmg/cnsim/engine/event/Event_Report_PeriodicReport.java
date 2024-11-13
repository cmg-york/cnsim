package ca.yorku.cmg.cnsim.engine.event;

import ca.yorku.cmg.cnsim.engine.Simulation;
import ca.yorku.cmg.cnsim.engine.node.INode;

public abstract class Event_Report_PeriodicReport extends Event {
    public void happen(Simulation sim){
    	super.happen(sim);
		for (INode n : sim.getNodeSet().getNodes()) {
			n.event_PrintPeriodicReport(this.getTime());
		}
    }
}
