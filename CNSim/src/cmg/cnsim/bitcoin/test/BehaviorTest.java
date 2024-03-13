package cmg.cnsim.bitcoin.test;

import cmg.cnsim.bitcoin.BitcoinNode;
import cmg.cnsim.bitcoin.BitcoinNodeFactory;
import cmg.cnsim.bitcoin.Block;
import cmg.cnsim.bitcoin.MaliciousNodeBehavior;
import cmg.cnsim.engine.*;
import cmg.cnsim.engine.network.AbstractNetwork;
import cmg.cnsim.engine.network.RandomEndToEndNetwork;
import cmg.cnsim.engine.node.AbstractNodeFactory;
import cmg.cnsim.engine.node.INode;
import cmg.cnsim.engine.node.NodeSet;
import cmg.cnsim.engine.transaction.Transaction;
import cmg.cnsim.engine.transaction.TransactionWorkload;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class BehaviorTest {
    private AbstractSampler sampler;
    private AbstractNetwork n;
    private Simulation s;
    private NodeSet ns;
    private AbstractNodeFactory nf;
    private Transaction target;

    @BeforeEach
    void setUp() throws Exception {
        Config.init("./CNSim/tests/BitcoinNode/Case 1 - config.txt");
        sampler = new FileBasedSampler("./CNSim/resources/transactions.csv", "./CNSim/resources/nodes.csv");


    }

    @AfterEach
    void tearDown() throws Exception {
    }


    @Test
    void testDoubleSpendingAttack() throws Exception{

        Simulation s = new Simulation(sampler);
        AbstractNodeFactory nf = new BitcoinNodeFactory("Honest", s);
        NodeSet ns = new NodeSet(nf);
        ns.addNodes(3);

        //ns.addNodes(Config.getPropertyInt("net.numOfNodes"));
        AbstractNetwork n = new RandomEndToEndNetwork(ns, sampler);
        s.setNetwork(n);

        TransactionWorkload ts = new TransactionWorkload(sampler);
        ts.appendTransactions(100);
        s.schedule(ts);

        // Assign a target transaction for malicious behavior
        System.out.println(ts.printIDs(","));
        for (INode node : ns.getNodes()) {
            if (node instanceof BitcoinNode) {
                BitcoinNode bNode = (BitcoinNode) node;
                if (bNode.getBehaviorStrategy() instanceof MaliciousNodeBehavior) {
                    ((MaliciousNodeBehavior) bNode.getBehaviorStrategy()).setTargetTransaction(ts.getTransaction(15));
                    target = ts.getTransaction(15);
                }
            }
        }

        s.run();
        long realTime = (System.currentTimeMillis() - Profiling.simBeginningTime); // in Milli-Sec
        System.out.printf("\n");
        System.out.println("Real time(ms): " + realTime);
        System.out.println("Simulation time(ms): " + Simulation.currTime);

        s.getNodeSet().closeNodes();

        //print the blockchain structure after simulation
        ns.getNodes().forEach(node -> {
            BitcoinNode bNode = (BitcoinNode) node;
            bNode.blockchain.printLongestChain();
        });

        //check if the target transaction is in the final structure
        ns.getNodes().forEach(node -> {
            BitcoinNode bNode = (BitcoinNode) node;
            if(bNode.blockchain.contains(target)){
                System.out.println("Transaction 15 is in the final structure");
                Block longestBlock = bNode.blockchain.getLongestTip();
                //assertFalse(bNode.blockchain.contains(longestBlock));
            }
        });

    }
}
