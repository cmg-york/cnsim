package cmg.cnsim.engine.node.test;
import cmg.cnsim.engine.IStructure;
import cmg.cnsim.engine.Simulation;
import cmg.cnsim.engine.node.INode;
import cmg.cnsim.engine.node.Node;
import cmg.cnsim.engine.transaction.ITxContainer;
import cmg.cnsim.engine.transaction.Transaction;

public class NodeStub extends Node {

    public NodeStub(Simulation sim) {
        super(sim);
    }

    @Override
    public IStructure getStructure() {
        return null;
    }

    @Override
    public void timeAdvancementReport() {

    }

    @Override
    public void periodicReport() {

    }

    @Override
    public void close(INode n) {

    }

    @Override
    public void event_NodeReceivesClientTransaction(Transaction t, long time) {

    }

    @Override
    public void event_NodeReceivesPropagatedContainer(ITxContainer t) {

    }

    
	@Override
	public void beliefReport(long[] sample, long time) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void nodeStatusReport() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void structureReport() {
		// TODO Auto-generated method stub
		
	}
}
