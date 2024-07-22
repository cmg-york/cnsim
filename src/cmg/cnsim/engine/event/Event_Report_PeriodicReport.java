package cmg.cnsim.engine.event;

import cmg.cnsim.engine.Simulation;
import cmg.cnsim.engine.node.INode;

public abstract class Event_Report_PeriodicReport extends Event {
    public void happen(Simulation sim){
    	super.happen(sim);
		for (INode n : sim.getNodeSet().getNodes()) {
			n.event_PrintPeriodicReport(this.getTime());
		}
    }
}
