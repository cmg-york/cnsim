package cmg.cnsim.bitcoin;
import cmg.cnsim.engine.Simulation;
import cmg.cnsim.engine.transaction.ITxContainer;
import cmg.cnsim.engine.transaction.Transaction;

import java.util.ArrayList;

public class MaliciousNodeBehavior implements NodeBehaviorStrategy {
    private static final int MIN_CHAIN_LENGTH = 6;
    private static final int MAX_CHAIN_LENGTH = 15;

    private ArrayList<Block> hiddenChain=new ArrayList<Block>();
    private Transaction targetTransaction;
    private boolean isAttackInProgress = false;
    private BitcoinNode node;
    private HonestNodeBehavior honestBehavior;
    private int blockchainSizeAtAttackStart;
    private Block lastBlock;
    private int publicChainGrowthSinceAttack;

    
    /**
     * Constructor. Creates also a shadow honest behavior object.
     * @param node The node which has the behavior.
     */
    public MaliciousNodeBehavior(BitcoinNode node) {
        this.isAttackInProgress = false;
        this.node = node;
        this.honestBehavior = new HonestNodeBehavior(node);
    }



    @Override
    public void event_NodeReceivesClientTransaction(Transaction t, long time) {
        honestBehavior.event_NodeReceivesClientTransaction(t, time);
    }

    @Override
    public void event_NodeReceivesPropagatedTransaction(Transaction t, long time) {
        honestBehavior.event_NodeReceivesPropagatedTransaction(t, time);
    }

    private void startAttack(Block b) {
        BitcoinReporter.reportBlockEvent(
        		Simulation.currTime,
        		System.currentTimeMillis() - Simulation.sysStartTime,
        		b.getCurrentNodeID(),
                b.getID(),
                ((b.getParent() == null) ? -1 : b.getParent().getID()),
                b.getHeight(),
                b.printIDs(";"),
                "Target Transaction Appeared - Attack Starts", 
                b.getValidationDifficulty(),
                b.getValidationCycles());
        isAttackInProgress = true;
        calculateBlockchainSizeAtAttackStart();
    }


    @Override
    public void event_NodeReceivesPropagatedContainer(ITxContainer t) {
        Block b = (Block) t;
        
        //updateBlockContext(b);
        
        b.setCurrentNodeID(node.getID());
        b.setLastBlockEvent("Node Receives Propagated Block");
        b.setValidationCycles(-1.0);
        b.setValidationDifficulty(-1.0);
     
        BitcoinReporter.reportBlockEvent(
        		Simulation.currTime,
        		System.currentTimeMillis() - Simulation.sysStartTime,
        		b.getCurrentNodeID(),
                b.getID(),
                ((b.getParent() == null) ? -1 : b.getParent().getID()),b.getHeight(),
                b.printIDs(";"),
                b.getLastBlockEvent(), 
                b.getValidationDifficulty(),
                b.getValidationCycles());
        
        
        if (!isAttackInProgress && t.contains(targetTransaction)) {
            lastBlock = (Block) b.parent;
            if (!node.blockchain.contains(b)) {
                //reportBlockEvent(b, b.getContext().blockEvt);
                handleNewBlockReceptionInAttack(b);
                startAttack(b);
            } else {
                BitcoinReporter.reportBlockEvent(
                		Simulation.currTime,
                		System.currentTimeMillis() - Simulation.sysStartTime,
                		b.getCurrentNodeID(),
                        b.getID(),
                        ((b.getParent() == null) ? -1 : b.getParent().getID()),b.getHeight(),
                        b.printIDs(";"),
                        "Propagated Block Discarded (already exists)", 
                        b.getValidationDifficulty(),
                        b.getValidationCycles());
                //reportBlockEvent(b, "Propagated Block Discarded");
            }
        }
        else if (isAttackInProgress) {
            if (!node.blockchain.contains(b)) {
                //reportBlockEvent(b, b.getContext().blockEvt);
                handleNewBlockReceptionInAttack(b);
            } else {
                //Discard the block and report the event.
                BitcoinReporter.reportBlockEvent(
                		Simulation.currTime,
                		System.currentTimeMillis() - Simulation.sysStartTime,
                		b.getCurrentNodeID(),
                        b.getID(),
                        ((b.getParent() == null) ? -1 : b.getParent().getID()),b.getHeight(),
                        b.printIDs(";"),
                        "Propagated Block Discarded (already exists)", 
                        b.getValidationDifficulty(),
                        b.getValidationCycles());
                //reportBlockEvent(b, "Propagated Block Discarded");
            }
            checkAndRevealHiddenChain(b);
        }
        else {
            if (!node.blockchain.contains(b)) {
                //reportBlockEvent(b, b.getContext().blockEvt);
                honestBehavior.handleNewBlockReception(b);
            } else {
            	//reportBlockEvent(b, "Propagated Block Discarded");
                BitcoinReporter.reportBlockEvent(
                		Simulation.currTime,
                		System.currentTimeMillis() - Simulation.sysStartTime,
                		b.getCurrentNodeID(),
                        b.getID(),
                        ((b.getParent() == null) ? -1 : b.getParent().getID()),b.getHeight(),
                        b.printIDs(";"),
                        "Propagated Block Discarded (already exists)", 
                        b.getValidationDifficulty(),
                        b.getValidationCycles());
            }

        }
    }



    @Override
    public void event_NodeCompletesValidation(ITxContainer t, long time) {
        if (isAttackInProgress) {
            Block newBlock = (Block) t;
            newBlock.validateBlock(node.miningPool.getGroup(), 
            		Simulation.currTime, 
            		System.currentTimeMillis(), 
            		node.getID(), 
            		"Node Completes Validation", 
            		node.getOperatingDifficulty(), 
            		node.getProspectiveCycles());
            
            node.completeValidation(node.miningPool, time);

            BitcoinReporter.reportBlockEvent(
            		newBlock.getSimTime_validation(),
            		newBlock.getSysTime_validation() - Simulation.sysStartTime,
            		newBlock.getValidationNodeID(),
            		newBlock.getID(),((newBlock.getParent() == null) ? -1 : newBlock.getParent().getID()),
            		newBlock.getHeight(),
            		newBlock.printIDs(";"),
                    "Node Completes Validation",
                    newBlock.getValidationDifficulty(),
                    newBlock.getValidationCycles());
            
            
            if (!node.blockchain.contains(newBlock)) {
                //reportBlockEvent(newBlock, newBlock.getContext().blockEvt);
                BitcoinReporter.reportBlockEvent(
                		newBlock.getSimTime_validation(),
                		newBlock.getSysTime_validation() - Simulation.sysStartTime,
                		newBlock.getValidationNodeID(),
                		newBlock.getID(),((newBlock.getParent() == null) ? -1 : newBlock.getParent().getID()),
                		newBlock.getHeight(),
                		newBlock.printIDs(";"),
                        "Adding block to hidden chain",
                        newBlock.getValidationDifficulty(),
                        newBlock.getValidationCycles());
                hiddenChain.add(newBlock);
            } else {
                //System.out.println(node.getID()+ " contains " + newBlock.getID() + " in its blockchain in completes validation");
                //System.out.println(node.getID()+ " contains " + newBlock.getID() + " in its blockchain in completes validation");
                //reportBlockEvent(newBlock, "Discarding own Block (ERROR)");
                BitcoinReporter.reportBlockEvent(
                		newBlock.getSimTime_validation(),
                		newBlock.getSysTime_validation() - Simulation.sysStartTime,
                		newBlock.getValidationNodeID(),
                		newBlock.getID(),((newBlock.getParent() == null) ? -1 : newBlock.getParent().getID()),
                		newBlock.getHeight(),
                		newBlock.printIDs(";"),
                        "ERROR: Discarding own Block",
                        newBlock.getValidationDifficulty(),
                        newBlock.getValidationCycles());
            }
            manageMiningPostValidation();
            checkAndRevealHiddenChain(newBlock);
        } else { //Attack not in progress
            Block b = (Block) t;
            b.validateBlock(node.miningPool.getGroup(), 
            		Simulation.currTime, 
            		System.currentTimeMillis(), 
            		node.getID(), 
            		"Node Completes Validation", 
            		node.getOperatingDifficulty(), 
            		node.getProspectiveCycles());
            //node.completeValidation(node.miningPool, time);
            node.completeValidation(node.miningPool, time);


            
            if(b.contains(targetTransaction)){
                if (!node.blockchain.contains(b)) {
                    //Report validation
                    //reportBlockEvent(b, b.getContext().blockEvt);
                    BitcoinReporter.reportBlockEvent(
                    		b.getSimTime_validation(),
                    		b.getSysTime_validation() - Simulation.sysStartTime,
                    		b.getValidationNodeID(),
                    		b.getID(),((b.getParent() == null) ? -1 : b.getParent().getID()),
                    		b.getHeight(),
                    		b.printIDs(";"),
                            "Node Completes Validation",
                            b.getValidationDifficulty(),
                            b.getValidationCycles());
                    
                    startAttack(b);
                    node.blockchain.addToStructure(b);
                    node.propagateContainer(b, time);
                    lastBlock = (Block) b.parent;
                    node.stopMining();
                    node.resetNextValidationEvent();
                    node.reconstructMiningPool();
                    node.miningPool.removeTransaction(targetTransaction);
                    node.considerMining(Simulation.currTime);
                } else {
                    BitcoinReporter.reportBlockEvent(
                    		b.getSimTime_validation(),
                    		b.getSysTime_validation() - Simulation.sysStartTime,
                    		b.getValidationNodeID(),
                    		b.getID(),((b.getParent() == null) ? -1 : b.getParent().getID()),
                    		b.getHeight(),
                    		b.printIDs(";"),
                            "Error: Discarding own Block",
                            b.getValidationDifficulty(),
                            b.getValidationCycles());
                    System.out.println(node.getID()+ " contains " + b.getID() + " in its blockchain in completes validation");
                    //reportBlockEvent(b, "Discarding own Block (ERROR)");
                }
                node.stopMining();
                node.resetNextValidationEvent();
                node.reconstructMiningPool();
                node.miningPool.removeTransaction(targetTransaction);
                node.considerMining(Simulation.currTime);
            } else {
                b.setParent(node.blockchain.getLongestTip());
                if (!node.blockchain.contains(b)){
                    //reportBlockEvent(b, b.getContext().blockEvt);
                    BitcoinReporter.reportBlockEvent(
                    		b.getSimTime_validation(),
                    		b.getSysTime_validation() - Simulation.sysStartTime,
                    		b.getValidationNodeID(),
                    		b.getID(),((b.getParent() == null) ? -1 : b.getParent().getID()),
                    		b.getHeight(),
                    		b.printIDs(";"),
                            "Node Completes Validation",
                            b.getValidationDifficulty(),
                            b.getValidationCycles());
                	
                    b.setParent(null);
                    node.blockchain.addToStructure(b);
                    try {
                    	//Propagate a clone of the block to the rest of the network
						node.propagateContainer((ITxContainer) b.clone(), time);
					} catch (CloneNotSupportedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                } else {
                    //reportBlockEvent(b, "Discarding own Block (ERROR)");
                    BitcoinReporter.reportBlockEvent(
                    		b.getSimTime_validation(),
                    		b.getSysTime_validation() - Simulation.sysStartTime,
                    		b.getValidationNodeID(),
                    		b.getID(),((b.getParent() == null) ? -1 : b.getParent().getID()),
                    		b.getHeight(),
                    		b.printIDs(";"),
                            "Error: Discarding own Block",
                            b.getValidationDifficulty(),
                            b.getValidationCycles());
                }
                honestBehavior.processPostValidationActivities(time);
            }
        }
    }

    private void revealHiddenChain() {
        for (int i = hiddenChain.size()-1; i >= 0; i--) {
            Block b = hiddenChain.get(i);
            b.parent = i==0 ? lastBlock : hiddenChain.get(i-1);
            node.blockchain.addToStructure(b);
            node.propagateContainer(b, Simulation.currTime);
        }
        isAttackInProgress = false;
        hiddenChain = new ArrayList<Block>();
        node.removeFromPool(targetTransaction);
    }

    private void reportBlockEvent(Block b, String blockEvt) {
        BitcoinReporter.reportBlockEvent(b.getContext().simTime, b.getContext().sysTime, b.getContext().nodeID,
                b.getID(),((b.getParent() == null) ? -1 : b.getParent().getID()),b.getHeight(),b.printIDs(";"),
                blockEvt, b.getContext().difficulty,b.getContext().cycles);
    }

    private void updateBlockContext(Block b) {
        //TODO: updating of context here seems wrong!
        //Update context information for reporting
        b.getContext().simTime = Simulation.currTime;
        b.getContext().sysTime = System.currentTimeMillis();
        b.getContext().nodeID = node.getID();
        b.getContext().blockEvt = "Node Receives Propagated Block";
        b.getContext().cycles = -1;
        b.getContext().difficulty = -1;
    }

    public void setTargetTransaction(Transaction targetTransaction) {
        this.targetTransaction = targetTransaction;
    }


    private void manageMiningPostValidation() {
        node.stopMining();
        node.resetNextValidationEvent();
        node.removeFromPool(node.miningPool);
        node.reconstructMiningPool();
        node.miningPool.removeTransaction(targetTransaction);
        node.considerMining(Simulation.currTime);
    }

    private void calculateBlockchainSizeAtAttackStart() {
        if (node.blockchain.getBlockchainHeight() == 0) {
            blockchainSizeAtAttackStart = 0;
            return;
        }
        Block tip = node.blockchain.getLongestTip();
        blockchainSizeAtAttackStart = tip.contains(targetTransaction) ? tip.getHeight() - 1 : tip.getHeight();
    }

    private void handleNewBlockReceptionInAttack(Block b) {
        node.blockchain.addToStructure(b);
        node.reconstructMiningPool();
        node.miningPool.removeTransaction(targetTransaction);
        node.considerMining(Simulation.currTime);
    }

    private boolean shouldRevealHiddenChain() {
        return (hiddenChain.size() > publicChainGrowthSinceAttack && publicChainGrowthSinceAttack > MIN_CHAIN_LENGTH)
                || publicChainGrowthSinceAttack > MAX_CHAIN_LENGTH;
    }

    private void checkAndRevealHiddenChain(Block b) {
        publicChainGrowthSinceAttack = node.blockchain.getLongestTip().height - blockchainSizeAtAttackStart;
        if (shouldRevealHiddenChain()) {
            BitcoinReporter.reportBlockEvent(
            		Simulation.currTime,
            		System.currentTimeMillis() - Simulation.sysStartTime,
            		b.getCurrentNodeID(),
                    b.getID(),
                    ((b.getParent() == null) ? -1 : b.getParent().getID()),b.getHeight(),
                    b.printIDs(";"),
                    "Reveal of hidden chain starts here.", 
                    b.getValidationDifficulty(),
                    b.getValidationCycles());
            revealHiddenChain();
        }
    }
}



