    package cmg.cnsim.bitcoin;

    import cmg.cnsim.engine.*;
    import cmg.cnsim.engine.network.AbstractNetwork;
    import cmg.cnsim.engine.network.RandomEndToEndNetwork;
    import cmg.cnsim.engine.node.AbstractNodeFactory;
    import cmg.cnsim.engine.node.INode;
    import cmg.cnsim.engine.node.NodeSet;
    import cmg.cnsim.engine.transaction.Transaction;
    import cmg.cnsim.engine.transaction.TransactionWorkload;

    import java.util.List;
    import java.util.Scanner;


    public class BitcoinMainDriver {

        public static void main(String[] args) {
            BitcoinMainDriver b = new BitcoinMainDriver();
            b.run();
        }

        private void run() {
            Config.init("/home/amir/Projects/CNSim/cnsim/CNSim/resources/config.txt");

            // Initialize components
            AbstractSampler sampler;
            if (Config.getPropertyBoolean("sampler.useFileBasedSampler")) {
                sampler = new FileBasedSampler("/home/amir/Projects/CNSim/cnsim/CNSim/resources/transactions.csv", "/home/amir/Projects/CNSim/cnsim/CNSim/resources/nodes.csv");
            } else {
                sampler = new StandardSampler();
                sampler.LoadConfig();
            }
            Simulation s = new Simulation(sampler);
            AbstractNodeFactory nf = new BitcoinNodeFactory("Honest", s);
            NodeSet ns = new NodeSet(nf);

            // Adding nodes
            ns.addNodes(Config.getPropertyInt("net.numOfNodes"));
            AbstractNetwork n = new RandomEndToEndNetwork(ns, sampler);
            //TODO handel file based throughput matrix
            s.setNetwork(n);

            // Transaction workload
            TransactionWorkload ts = new TransactionWorkload(sampler);
            ts.appendTransactions(Config.getPropertyLong("workload.numTransactions"));
            s.schedule(ts);

            // Assign a target transaction for malicious behavior
            Transaction targetTransaction = getTargetTransactionFromUser(ts.getAllTransactions());
            for (INode node : ns.getNodes()) {
                if (node instanceof BitcoinNode) {
                    BitcoinNode bNode = (BitcoinNode) node;
                    if (bNode.getBehaviorStrategy() instanceof MaliciousNodeBehavior) {
                        ((MaliciousNodeBehavior) bNode.getBehaviorStrategy()).setTargetTransaction(targetTransaction);
                    }
                }
            }

            Profiling.simBeginningTime = System.currentTimeMillis();


            s.run();
            long realTime = (System.currentTimeMillis() - Profiling.simBeginningTime); // in Milli-Sec
            System.out.printf("\n");
            System.out.println("Real time(ms): " + realTime);
            System.out.println("Simulation time(ms): " + Simulation.currTime);

            s.getNodeSet().closeNodes();

            BitcoinReporter.flushBlockReport();
            BitcoinReporter.flushStructReport();
            BitcoinReporter.flushEvtReport();
            BitcoinReporter.flushNodeReport();
            BitcoinReporter.flushInputReport();
            BitcoinReporter.flushConfig();


        }


        private Transaction getTargetTransactionFromUser(List<Transaction> transactions) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Select a transaction ID to target for the attack:");
            String chosenId = scanner.nextLine();
            //TODO Add the target transaction to the config file instead of taking it from user in the begining.
            for (Transaction t : transactions) {
                if (t.getID() == Integer.parseInt(chosenId)) {
                    return t;
                }
            }
            return null; // Or handle invalid selection appropriately
        }


    }