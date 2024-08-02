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
        
        //updateBlockContext(b);
        // Report a block event

        //reportBlockEvent(b, b.getContext().blockEvt);

        b.setCurrentNodeID(node.getID());
        b.setLastBlockEvent("Node Receives Propagated Block");
        b.setValidationCycles(-1.0);
        b.setValidationDifficulty(-1.0);
     
        BitcoinReporter.reportBlockEvent(
				Simulation.currentSimulationID,
        		Simulation.currTime,
        		System.currentTimeMillis()- Simulation.sysStartTime,
        		b.getCurrentNodeID(),
                b.getID(),
                ((b.getParent() == null) ? -1 : b.getParent().getID()),b.getHeight(),
                b.printIDs(";"),
                b.getLastBlockEvent(), 
                b.getValidationDifficulty(),
                b.getValidationCycles());
                
        if (!node.blockchain.contains(b)){
            handleNewBlockReception(b);
        } else {
            //Discard the block and report the event.
            //reportBlockEvent(b, "Propagated Block Discarded");
            b.setLastBlockEvent("ERROR: propagated Block already exists");
        	BitcoinReporter.reportBlockEvent(
					Simulation.currentSimulationID,
            		Simulation.currTime,
            		System.currentTimeMillis() - Simulation.sysStartTime,
            		b.getCurrentNodeID(),
                    b.getID(),
                    ((b.getParent() == null) ? -1 : b.getParent().getID()),
                    b.getHeight(),
                    b.printIDs(";"),
                    b.getLastBlockEvent(), 
                    b.getValidationDifficulty(),
                    b.getValidationCycles());
        }
    }




    @Override
    public void event_NodeCompletesValidation(ITxContainer t, long time) {
        Block b = (Block) t;
        //Add validation information to the block.
        b.validateBlock(node.miningPool,
                Simulation.currTime,
                System.currentTimeMillis() - Simulation.sysStartTime,
                node.getID(),
                "Node Completes Validation",
                node.getOperatingDifficulty(),
                node.getProspectiveCycles());


        node.completeValidation(node.miningPool, time);


        //Report validation
        //reportBlockEvent(b, b.getContext().blockEvt);

        //Report the validation event
        BitcoinReporter.reportBlockEvent(
				Simulation.currentSimulationID,
        		b.getSimTime_validation(),
        		b.getSysTime_validation(),
        		b.getValidationNodeID(),
                b.getID(),((b.getParent() == null) ? -1 : b.getParent().getID()),
                b.getHeight(),
                b.printIDs(";"),
                "Node Completes Validation",
                b.getValidationDifficulty(),
                b.getValidationCycles());
        
        
        b.setParent(node.blockchain.getLongestTip());
        if (!node.blockchain.contains(b)) {
            b.setParent(null);
            //Add block to blockchain
            node.blockchain.addToStructure(b);
            
            //Propagate a clone of the block to the rest of the network
            try {
				node.propagateContainer((ITxContainer) b.clone(), time);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
        } else {
            //reportBlockEvent(b, "Discarding own Block (ERROR)");
            BitcoinReporter.reportBlockEvent(
					Simulation.currentSimulationID,
            		b.getSimTime_validation(),
            		b.getSysTime_validation()- Simulation.sysStartTime,
            		b.getValidationNodeID(),
                    b.getID(),((b.getParent() == null) ? -1 : b.getParent().getID()),
                    b.getHeight(),
                    b.printIDs(";"),
                    "Discarding own Block (ERROR)",
                    b.getValidationDifficulty(),
                    b.getValidationCycles());
            
        }

        processPostValidationActivities(time);
    }



//    private void updateBlockContext(Block b) {
//        //TODO: updating of context here seems wrong!
//        //Update context information for reporting
//        b.getContext().simTime = Simulation.currTime;
//        b.getContext().sysTime = System.currentTimeMillis();
//        b.getContext().nodeID = node.getID();
//        b.getContext().blockEvt = "Node Receives Propagated Block";
//        b.getContext().cycles = -1;
//        b.getContext().difficulty = -1;
//    }

    protected void handleNewBlockReception(Block b) {
        //Add block to blockchain
        node.blockchain.addToStructure(b);
        //Remove block transactions from pool.
        node.getPool().extractGroup(b);
        //node.miningPool.extractGroup(b);
        // Reconstruct mining pool based on the new information.
        node.reconstructMiningPool();
        //Consider starting or stopping mining.
        node.considerMining(Simulation.currTime);
        //node.blockchain.printLongestChain();
    }

//    private void reportBlockEvent(Block b, String blockEvt) {
//        // Report a block event
//        BitcoinReporter.reportBlockEvent(b.getContext().simTime, b.getContext().sysTime, b.getContext().nodeID,
//                b.getID(),((b.getParent() == null) ? -1 : b.getParent().getID()),b.getHeight(),b.printIDs(";"),
//                blockEvt, b.getContext().difficulty,b.getContext().cycles);
//    }




    protected void processPostValidationActivities(long time) {
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
