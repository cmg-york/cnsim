package cmg.cnsim.engine.node.test;
import cmg.cnsim.engine.IStructure;
import cmg.cnsim.engine.Simulation;
import cmg.cnsim.engine.node.INode;
import cmg.cnsim.engine.node.Node;
import cmg.cnsim.engine.transaction.ITxContainer;
import cmg.cnsim.engine.transaction.Transaction;

public class Nodet extends Node {

    public Nodet(Simulation sim) {
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
}
