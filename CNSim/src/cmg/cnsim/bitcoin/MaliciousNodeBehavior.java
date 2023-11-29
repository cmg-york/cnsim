package cmg.cnsim.bitcoin;

import cmg.cnsim.engine.transaction.ITxContainer;
import cmg.cnsim.engine.transaction.Transaction;

import java.util.ArrayList;

public class MaliciousNodeBehavior implements NodeBehaviorStrategy {
    private ArrayList<Block> hiddenChain;
    private Transaction targetTransaction;
    private boolean isAttackInProgress;
    private int hiddenChainLength;
    private BitcoinNode node;
    private HonestNodeBehavior honestBehavior;
    private int blockchainSizeAtAttackStart;
    final int MIN_CHAIN_LENGTH = 6;
    private Block lastBlock;

    public MaliciousNodeBehavior(BitcoinNode node) {
        this.isAttackInProgress = false;
        this.node = node;
        this.honestBehavior = new HonestNodeBehavior(node);
        this.hiddenChain = new ArrayList<Block>();
        this.hiddenChainLength = 0;
    }

    @Override
    public void event_NodeReceivesClientTransaction(Transaction t, long time) {
        if (!t.equals(targetTransaction)) {
            honestBehavior.event_NodeReceivesClientTransaction(t, time);
        } else if (!isAttackInProgress) {
            startAttack();
        }
    }

    private void startAttack() {
        // Start building a hidden chain including the double-spent transaction
        isAttackInProgress = true;
        hiddenChain = new ArrayList<Block>();
        blockchainSizeAtAttackStart = node.blockchain.blockchain.size();
        // create a getter method for the blockchain
    }

    @Override
    public void event_NodeReceivesPropagatedTransaction(Transaction t, long time) {
        honestBehavior.event_NodeReceivesPropagatedTransaction(t, time);
    }


    @Override
    public void event_NodeReceivesPropagatedContainer(ITxContainer t) {
        // Process the container as an honest node
        honestBehavior.event_NodeReceivesPropagatedContainer(t);

        if (isAttackInProgress) {
            // Calculate the growth of the public blockchain since the attack started
            int publicChainGrowthSinceAttack = node.blockchain.blockchain.size() - blockchainSizeAtAttackStart;
            //TODO check for the len of the blockchain to substitute
            //TODO getter method for Blockchain

            // Reveal the hidden chain only if it's longer than the growth of the public blockchain
            if (hiddenChain.size() > publicChainGrowthSinceAttack  && publicChainGrowthSinceAttack > MIN_CHAIN_LENGTH){
                revealHiddenChain();
            }
        }
    }

    @Override
    public void event_NodeCompletesValidation(ITxContainer t, long time) {

        if (isAttackInProgress) {
            if (!hiddenChain.isEmpty()){
                ((Block) t).parent = hiddenChain.get(hiddenChain.size()-1);
            }
            else {
                ((Block) t).parent = lastBlock;
            }
            hiddenChain.add((Block) t);
        }
        else {
            honestBehavior.event_NodeCompletesValidation(t, time);
        }

        //TODO Pointer to the previous block
    }

    private void revealHiddenChain() {
        for (Block b : hiddenChain) {
            honestBehavior.event_NodeCompletesValidation(b, System.currentTimeMillis());
            //TODO change the currentTimeMillis to the simulation time (and also check if we need it)Done
        }

        isAttackInProgress = false;
        hiddenChain = new ArrayList<Block>();
        hiddenChainLength = 0;
    }


}
