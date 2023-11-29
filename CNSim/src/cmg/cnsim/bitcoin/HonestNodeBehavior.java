package cmg.cnsim.bitcoin;

import cmg.cnsim.engine.Simulation;
import cmg.cnsim.engine.transaction.ITxContainer;
import cmg.cnsim.engine.transaction.Transaction;

public class HonestNodeBehavior implements NodeBehaviorStrategy {

    private BitcoinNode node; // Reference to the BitcoinNode

    public HonestNodeBehavior(BitcoinNode node) {
        this.node = node;
    }

    @Override
    public void event_NodeReceivesClientTransaction(Transaction t, long time) {
        // Process the transaction as per normal rules
        // For instance, add the transaction to the node's pool if it's valid
        node.transactionReceipt(t,time);
        node.propagateTransaction(t,time);
    }

    @Override
    public void event_NodeReceivesPropagatedTransaction(Transaction t, long time) {
        // Handle reception of propagated transactions
        // Add to the pool if not already present and it's valid
        if (!node.getPool().contains(t) && !node.blockchain.contains(t)) {
            node.transactionReceipt(t,time);
        }
    }

    @Override
    public void event_NodeReceivesPropagatedContainer(ITxContainer t) {
        Block b = (Block) t;
        //TODO: updating of context here seems wrong!
        //Update context information for reporting
        b.getContext().simTime = Simulation.currTime;
        b.getContext().sysTime = System.currentTimeMillis();
        b.getContext().nodeID = node.getID();
        b.getContext().blockEvt = "Node Receives Propagated Block";
        b.getContext().cycles = -1;
        b.getContext().difficulty = -1;

        // Report a block event
        BitcoinReporter.reportBlockEvent(b.getContext().simTime, b.getContext().sysTime, b.getContext().nodeID,
                b.getID(),((b.getParent() == null) ? -1 : b.getParent().getID()),b.getHeight(),b.printIDs(";"),
                b.getContext().blockEvt, b.getContext().difficulty,b.getContext().cycles);

        //TODO: contains should be checking only the parental structure (not the entire blockchain).
        if (!node.blockchain.contains(b)) {
            //Add block to blockchain
            node.blockchain.addToStructure(b);
            //Remove block transactions from pool.
            node.miningPool.extractGroup(b);
            // Reconstruct mining pool based on the new information.
            node.reconstructMiningPool();
            //Consider starting or stopping mining.
            node.considerMining(Simulation.currTime);
        } else {
            //Discard the block and report the event.
            BitcoinReporter.reportBlockEvent(b.getContext().simTime, b.getContext().sysTime, b.getContext().nodeID,
                    b.getID(),((b.getParent() == null) ? -1 : b.getParent().getID()),b.getHeight(),b.printIDs(";"),
                    "Propagated Block Discarded", b.getContext().difficulty,b.getContext().cycles);
        }
    }

    @Override
    public void event_NodeCompletesValidation(ITxContainer t, long time) {
        Block b = (Block) t;

        //Add validation information to the block.
        b.validateBlock(node.miningPool.getGroup(),
                Simulation.currTime,
                System.currentTimeMillis(),
                node.getID(),
                "Node Completes Validation",
                node.getOperatingDifficulty(),
                node.getProspectiveCycles());
        node.completeValidation(node.miningPool, time);

        //Report validation
        //System.out.println("Node:" + this.getID() + " validates " + b.printIDs(";") +" at " + b.getContext().simTime);
        BitcoinReporter.reportBlockEvent(b.getContext().simTime, b.getContext().sysTime, b.getContext().nodeID,
                b.getID(),((b.getParent() == null) ? -1 : b.getParent().getID()),b.getHeight(),b.printIDs(";"),
                b.getContext().blockEvt, b.getContext().difficulty,b.getContext().cycles);

        if (!node.blockchain.contains(b)) {
            //Add block to blockchain
            node.blockchain.addToStructure(b);
            //Propagate block to the rest of the network
            node.propagateContainer(b, time);
        } else {
            BitcoinReporter.reportBlockEvent(b.getContext().simTime, b.getContext().sysTime, b.getContext().nodeID,
                    b.getID(),((b.getParent() == null) ? -1 : b.getParent().getID()),b.getHeight(),b.printIDs(";"),
                    "Discarding own Block (ERROR)", b.getContext().difficulty,b.getContext().cycles);
        }

        //Stop mining for now. TODO: why do you do this?
        node.stopMining();
        //Reset the next validation event. TODO: why do you do this?
        node.resetNextValidationEvent();
        //Remove the block's transactions from the mining pool.
        node.removeFromPool(node.miningPool);
        //Reconstruct mining pool, with whatever other transactions are there.
        node.reconstructMiningPool();
        //Consider if it is worth mining.
        node.considerMining(time);
    }

    // Additional methods and logic, if required...
}
