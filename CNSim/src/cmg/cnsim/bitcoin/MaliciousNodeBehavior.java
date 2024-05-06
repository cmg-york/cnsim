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

    private void startAttack() {
        isAttackInProgress = true;
        calculateBlockchainSizeAtAttackStart();
    }


    @Override
    public void event_NodeReceivesPropagatedContainer(ITxContainer t) {
        Block b = (Block) t;
        updateBlockContext(b);
        if (!isAttackInProgress && t.contains(targetTransaction)) {
            lastBlock = (Block) b.parent;
            if (!node.blockchain.contains(b)) {
                reportBlockEvent(b, b.getContext().blockEvt);
                handleNewBlockReceptionInAttack(b);
                startAttack();
            } else {
                reportBlockEvent(b, "Propagated Block Discarded");
            }
        }
        else if (isAttackInProgress) {
            if (!node.blockchain.contains(b)) {
                reportBlockEvent(b, b.getContext().blockEvt);
                handleNewBlockReceptionInAttack(b);

            } else {
                //Discard the block and report the event.
                reportBlockEvent(b, "Propagated Block Discarded");
            }
            checkAndRevealHiddenChain();
        }
        else {
            if (!node.blockchain.contains(b)) {
                reportBlockEvent(b, b.getContext().blockEvt);
                honestBehavior.handleNewBlockReception(b);
            } else {
                reportBlockEvent(b, "Propagated Block Discarded");
            }

        }
    }



    @Override
    public void event_NodeCompletesValidation(ITxContainer t, long time) {
        if (isAttackInProgress) {
            Block newBlock = (Block) t;
            newBlock.validateBlock(node.miningPool.getGroup(), Simulation.currTime, System.currentTimeMillis(), node.getID(), "Node Completes Validation", node.getOperatingDifficulty(), node.getProspectiveCycles());
            node.completeValidation(node.miningPool, time);


            if (!node.blockchain.contains(newBlock)) {
                reportBlockEvent(newBlock, newBlock.getContext().blockEvt);
                hiddenChain.add(newBlock);
            } else {
                //System.out.println(node.getID()+ " contains " + newBlock.getID() + " in its blockchain in completes validation");
                //System.out.println(node.getID()+ " contains " + newBlock.getID() + " in its blockchain in completes validation");
                reportBlockEvent(newBlock, "Discarding own Block (ERROR)");
            }

            manageMiningPostValidation();
            checkAndRevealHiddenChain();

        }
        else{
            Block b = (Block) t;
            b.validateBlock(node.miningPool.getGroup(), Simulation.currTime, System.currentTimeMillis(), node.getID(), "Node Completes Validation", node.getOperatingDifficulty(), node.getProspectiveCycles());node.completeValidation(node.miningPool, time);
            node.completeValidation(node.miningPool, time);

            if(b.contains(targetTransaction)){
                if (!node.blockchain.contains(b)) {
                    //Report validation
                    reportBlockEvent(b, b.getContext().blockEvt);
                    startAttack();

                    //System.out.println(node.getID() + " does not contain " + b.getID() + " in its blockchain in completes validation");
                    node.blockchain.addToStructure(b);
                    node.propagateContainer(b, time);
                    lastBlock = (Block) b.parent;
                    System.out.println("Last Block changed to: " + lastBlock.getID());
                    node.stopMining();
                    //Reset the next validation event. TODO: why do you do this?
                    node.resetNextValidationEvent();
                    node.reconstructMiningPool();
                    node.miningPool.removeTxFromContainer(targetTransaction);
                    node.considerMining(Simulation.currTime);
                } else {
                    System.out.println(node.getID()+ " contains " + b.getID() + " in its blockchain in completes validation");
                    reportBlockEvent(b, "Discarding own Block (ERROR)");
                }
                node.stopMining();
                //Reset the next validation event. TODO: why do you do this?
                node.resetNextValidationEvent();
                //Remove the block's transactions from the mining pool.
                node.reconstructMiningPool();
                node.miningPool.removeTxFromContainer(targetTransaction);
                //Consider if it is worth mining.
                node.considerMining(Simulation.currTime);
            }
            else {
                b.setParent(node.blockchain.getLongestTip());
                if (!node.blockchain.contains(b)){
                    reportBlockEvent(b, b.getContext().blockEvt);
                    b.setParent(null);
                    node.blockchain.addToStructure(b);
                    node.propagateContainer(b, time);
                }
                else{
                    reportBlockEvent(b, "Discarding own Block (ERROR)");
                }
                honestBehavior.processPostValidationActivities(time);
            }
        }
        System.out.println("Malicious node Completes Validation offfff Block " + t.getID() + " that contains: " + t.printIDs(";"));
        System.out.println("target transaction is: " + targetTransaction.getID());
    }

    private void revealHiddenChain() {
        for (int i = hiddenChain.size()-1; i >= 0; i--) {
            Block b = hiddenChain.get(i);
            b.parent = i==0 ? lastBlock : hiddenChain.get(i-1);
            node.blockchain.addToStructure(b);
            node.propagateContainer(b, Simulation.currTime);
            if (b.getParent() == null) {
                System.out.println("hidden chain is revealing. Its parent is: " + "null" + " and its height is: " + b.getHeight() + " and its ID is: " + b.getID());
            }
            else {
                System.out.println("hidden chain is revealing. Its parent is: " + b.getParent().getID() + " and its height is: " + b.getHeight() + " and its ID is: " + b.getID());
            }
            System.out.println(b.printIDs(";"));

        }
        isAttackInProgress = false;
        hiddenChain = new ArrayList<Block>();
        node.removeFromPool(targetTransaction);
    }

    private void reportBlockEvent(Block b, String blockEvt) {
        // Report a block event
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

    private void configureNodeForAttack(float HashPower, float ElectricPower) {
        node.setElectricPower(HashPower);
        node.setHashPower(ElectricPower);
    }

    private void manageMiningPostValidation() {
        node.stopMining();
        //Reset the next validation event. TODO: why do you do this?
        node.resetNextValidationEvent();
        //Remove the block's transactions from the mining pool.
        node.removeFromPool(node.miningPool);
        //Reconstruct mining pool, with whatever other transactions are there.
        node.reconstructMiningPool();
        node.miningPool.removeTxFromContainer(targetTransaction);
        //Consider if it is worth mining.
        node.considerMining(Simulation.currTime);
        //TODO check how modify consider mining to make sure it always mining

    }

    private void calculateBlockchainSizeAtAttackStart() {
        if (node.blockchain.getBlockchainHeight() == 0) {
            blockchainSizeAtAttackStart = 0;
            return;
        }
        Block tip = node.blockchain.getLongestTip();
        blockchainSizeAtAttackStart = tip.contains(targetTransaction) ? tip.getHeight() - 1 : tip.getHeight();
        //Block tip = node.blockchain.getLongestTip().getHeight();
    }

    private void
    handleNewBlockReceptionInAttack(Block b) {
        //Add block to blockchain
        node.blockchain.addToStructure(b);
        // Reconstruct mining pool based on the new information.
        //TODO we should store them so if the attack was not successful we can remove them from the mining pool later.
        node.reconstructMiningPool();
        //remove target transaction from pool
        node.miningPool.removeTxFromContainer(targetTransaction);
        //Consider starting or stopping mining.
        node.considerMining(Simulation.currTime);
    }

    private boolean shouldRevealHiddenChain() {
        return (hiddenChain.size() > publicChainGrowthSinceAttack && publicChainGrowthSinceAttack > MIN_CHAIN_LENGTH)
                || publicChainGrowthSinceAttack > MAX_CHAIN_LENGTH;
    }

    private void checkAndRevealHiddenChain() {
        publicChainGrowthSinceAttack = node.blockchain.getLongestTip().height - blockchainSizeAtAttackStart;
        if (shouldRevealHiddenChain()) {
            revealHiddenChain();
        }
    }


    //Logging
}



