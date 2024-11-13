package ca.yorku.ca.cmg.cnsim.bitcoin;

import ca.yorku.cmg.cnsim.engine.transaction.ITxContainer;
import ca.yorku.cmg.cnsim.engine.transaction.Transaction;

public interface NodeBehaviorStrategy {
    void event_NodeReceivesClientTransaction(Transaction t, long time);
    void event_NodeReceivesPropagatedTransaction(Transaction t, long time);
    void event_NodeReceivesPropagatedContainer(ITxContainer t);
    void event_NodeCompletesValidation(ITxContainer t, long time);
}
