package cmg.cnsim.engine.event;

import cmg.cnsim.engine.Simulation;
import cmg.cnsim.engine.node.INode;

public class Event_Report_NodeStatusReport extends Event {
    public void happen(Simulation sim){
    	super.happen(sim);
		for (INode n : sim.getNodeSet().getNodes()) {
			n.event_NodeStatusReport(this.getTime());
		}
    }
}
