package cmg.cnsim.bitcoin;

import cmg.cnsim.engine.Simulation;
import cmg.cnsim.engine.transaction.ITxContainer;
import cmg.cnsim.engine.transaction.Transaction;

import java.util.ArrayList;


//TODO we can also remove the target transaction in first. So we do not need to validate it.

public class MaliciousNodeBehavior implements NodeBehaviorStrategy {
    private static final int MIN_CHAIN_LENGTH = 6;
    private static final int MAX_CHAIN_LENGTH = 15;


    private ArrayList<Block> hiddenChain=new ArrayList<Block>();
    private ArrayList<Long> hiddenChainTimes = new ArrayList<Long>();
    private Transaction targetTransaction;
    private boolean isAttackInProgress = false;
    private BitcoinNode node;
    private HonestNodeBehavior honestBehavior;
    private int blockchainSizeAtAttackStart;
    private Block lastBlock;

    private int publicChainGrowthSinceAttack;
    private boolean isAttackFinished;

    public MaliciousNodeBehavior(BitcoinNode node) {
        this.isAttackInProgress = false;
        this.node = node;
        this.honestBehavior = new HonestNodeBehavior(node);
        logCreation();
    }



    @Override
    public void event_NodeReceivesClientTransaction(Transaction t, long time) {
        honestBehavior.event_NodeReceivesClientTransaction(t, time);
        logTransaction("client", t);
    }

    @Override
    public void event_NodeReceivesPropagatedTransaction(Transaction t, long time) {
        honestBehavior.event_NodeReceivesPropagatedTransaction(t, time);
        logTransaction("propagated", t);
    }

    private void startAttack() {
        isAttackInProgress = true;
        configureNodeForAttack(7400000f, 137500f);
        calculateBlockchainSizeAtAttackStart();
        logStartAttack();
    }


    @Override
    public void event_NodeReceivesPropagatedContainer(ITxContainer t) {

        Block b = (Block) t;

        updateBlockContext(b);
        reportBlockEvent(b, b.getContext().blockEvt);

        if (!isAttackInProgress && t.contains(targetTransaction)) {
            lastBlock = (Block) b.parent;
            //System.out.println("Malicious Node Attack started by receiving the target transaction");

            if (!node.blockchain.contains(b)) {
                handleNewBlockReceptionInAttack(b);
                startAttack();
            } else {
                //System.out.println(node.getID()+ " contains " + b.getID() + " in its blockchain in recieves propagated container");
                //Discard the block and report the event.
                reportBlockEvent(b, "Propagated Block Discarded");
            }
        }

        else if (isAttackInProgress) {
            if (!node.blockchain.contains(b)) {
                handleNewBlockReceptionInAttack(b);

            } else {
                //Discard the block and report the event.
                reportBlockEvent(b, "Propagated Block Discarded");
            }
            checkAndRevealHiddenChain();
        }
        else {
            //System.out.println("Malicious node is acting event NodeRecievesPropogatedContainer while attack is not started and target transaction has not been seen");
            if (!node.blockchain.contains(b)) {
                //System.out.println(node.getID() + " does not contain " + b.getID() + " in its blockchain");
                //Add block to blockchain
                honestBehavior.handleNewBlockReception(b);
            } else {
                //System.out.println(node.getID()+ " contains " + b.getID() + " in its blockchain");
                //Discard the block and report the event.
                reportBlockEvent(b, "Propagated Block Discarded");
            }

        }
    }



    @Override
    public void event_NodeCompletesValidation(ITxContainer t, long time) {
        logBlockValidation(t);

        if (isAttackInProgress) {
            Block newBlock = (Block) t;
            newBlock.validateBlock(node.miningPool.getGroup(), Simulation.currTime, System.currentTimeMillis(), node.getID(), "Node Completes Validation", node.getOperatingDifficulty(), node.getProspectiveCycles());
            node.completeValidation(node.miningPool, time);
            reportBlockEvent(newBlock, newBlock.getContext().blockEvt);

            if (!node.blockchain.contains(newBlock)) {
                hiddenChain.add(newBlock);
                hiddenChainTimes.add(time);
                //TODO you can remove the HiddenChainTimes
            } else {
                //System.out.println(node.getID()+ " contains " + newBlock.getID() + " in its blockchain in completes validation");
                reportBlockEvent(newBlock, "Discarding own Block (ERROR)");
            }

            manageMiningPostValidation();
            checkAndRevealHiddenChain();

        }
        else if (t.contains(targetTransaction) && !isAttackInProgress) {
            Block b = (Block) t;

            //TODO start attack only if our blockchain does not contain it - Done by moving it under if condition

            //TODO we have problem here. the block context will disappear after b.validate block*******
            b.validateBlock(node.miningPool.getGroup(), Simulation.currTime, System.currentTimeMillis(), node.getID(), "Node Completes Validation", node.getOperatingDifficulty(), node.getProspectiveCycles());node.completeValidation(node.miningPool, time);
            //Report validation
            reportBlockEvent(b, b.getContext().blockEvt);

            if (!node.blockchain.contains(b)) {
                startAttack();
                logStartAttackByValidation(t);

                //System.out.println(node.getID() + " does not contain " + b.getID() + " in its blockchain in completes validation");
                node.blockchain.addToStructure(b);
                node.propagateContainer(b, time);
            } else {
                //System.out.println(node.getID()+ " contains " + b.getID() + " in its blockchain in completes validation");
                reportBlockEvent(b, "Discarding own Block (ERROR)");
            }
            lastBlock = (Block) b.parent;
            manageMiningPostValidation();
        }
        else {
            honestBehavior.event_NodeCompletesValidation(t, time);
        }

    }



    private void revealHiddenChain() {
        System.out.println("********Revealing hidden chain******");
        for (int i = hiddenChain.size()-1; i >= 0; i--) {
            Block b = hiddenChain.get(i);
            b.parent = i==0 ? lastBlock : hiddenChain.get(i-1);
            node.blockchain.addToStructure(b);
            //if (b.getParent()!=null){
            //    System.out.println("goooda" + b.getParent().getID());}
            node.propagateContainer(b, hiddenChainTimes.get(i));
            if (b.getParent() == null) {
                System.out.println("hidden chain is revealing. Its parent is: " + "null" + " and its height is: " + b.getHeight() + " and its ID is: " + b.getID());
            }
            else {
                System.out.println("hidden chain is revealing. Its parent is: " + b.getParent().getID() + " and its height is: " + b.getHeight() + " and its ID is: " + b.getID());
            }
            System.out.println(b.printIDs(";"));

        }
        isAttackInProgress = false;
        isAttackFinished = true;
        //TODO add document to explain why we need isAttackFinished
        hiddenChain = new ArrayList<Block>();
        hiddenChainTimes = new ArrayList<Long>();
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
        logBlockchainGrowth(publicChainGrowthSinceAttack, hiddenChain);
        if (shouldRevealHiddenChain()) {
            revealHiddenChain();
        }
    }


    //Logging

    private void logCreation() {
        System.out.println("Malicious node created");
        System.out.println("Malicious Node ID: " + node.getID());
        System.out.println("Malicious Hash Power: " + node.getHashPower());
        System.out.println("Malicious Electric Power: " + node.getElectricPower());
    }

    private void logTransaction(String client, Transaction t) {
        System.out.println("Malicious node receives " + client + " transaction with ID: " + t.getID());
    }

    private void logStartAttack() {
        System.out.println("Hash power and electricity power of Malicious node : " + node.getHashPower() + " " + node.getElectricPower());
        System.out.println("---------------------------------------Starting attack------------------------------------------");
        System.out.println("Blockchain size at attack start: "+ blockchainSizeAtAttackStart);
    }

    public void logBlockchainGrowth(int publicChainGrowthSinceAttack, ArrayList<Block> hiddenChain) {
        System.out.println(node.blockchain.getBlockchainHeight() + " blockchain height");
        System.out.println("public chain growth since attack: " + publicChainGrowthSinceAttack);
        System.out.println("hidden chain size: " + hiddenChain.size());
    }

    private void logBlockValidation(ITxContainer t) {
        System.out.println("Malicious node completes validation of Block + " + t.getID() + ". You can see the transactions below: ");
        System.out.println(t.printIDs(";"));
    }

    private void logStartAttackByValidation(ITxContainer t) {
        System.out.println("________________________Attack started by validating the target transaction________________________________");
        System.out.println("Node ID: " + node.getID() + " completes validation" + " Hash power: " + node.getHashPower() + " Electricity power: " + node.getElectricPower());
        System.out.println("The block contains: " + t.printIDs(";"));
    }

}



