    package cmg.cnsim.bitcoin;

    import cmg.cnsim.engine.*;
    import cmg.cnsim.engine.network.AbstractNetwork;
    import cmg.cnsim.engine.network.FileBasedEndToEndNetwork;
    import cmg.cnsim.engine.network.RandomEndToEndNetwork;
    import cmg.cnsim.engine.node.AbstractNodeFactory;
    import cmg.cnsim.engine.node.INode;
    import cmg.cnsim.engine.node.NodeSet;
    import cmg.cnsim.engine.transaction.Transaction;
    import cmg.cnsim.engine.transaction.TransactionWorkload;

    import java.util.Arrays;
    import java.util.List;
    import java.util.Scanner;


    public class BitcoinMainDriver {

        public static void main(String[] args) {
            //run simulation with the given configuration for n times
            BitcoinMainDriver b = new BitcoinMainDriver();
            b.run();
        }

        private void run() {
            Config.init("/home/amir/Projects/CNSim/cnsim/CNSim/resources/config.txt");

            // Initialize components
            AbstractSampler sampler;
            if (Config.getPropertyBoolean("sampler.useFileBasedSampler")) {
                sampler = new FileBasedSampler("./CNSim/resources/transactions.csv", "./CNSim/resources/nodes.csv");
                sampler.LoadConfig();
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
            //check for using random or file based network
            if (Config.getPropertyBoolean("net.useFileBasedNetwork")) {
                n = new FileBasedEndToEndNetwork(ns, sampler, "./CNSim/resources/network.csv");
            } else {
                n = new RandomEndToEndNetwork(ns, sampler);
            }
            //TODO handel file based throughput matrix
            s.setNetwork(n);
            System.out.println("Network matrix:");
            n.printNetwork();



            // Transaction workload
            TransactionWorkload ts = new TransactionWorkload(sampler);
            ts.appendTransactions(Config.getPropertyLong("workload.numTransactions"));
            s.schedule(ts);

            // Assign a target transaction for malicious behavior
            Transaction targetTransaction = null;
            if (Config.getPropertyBoolean("node.createMaliciousNode")) {
                targetTransaction = getTargetTransactionFromUser(ts.getAllTransactions());
            }
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
            System.out.print("\n");
            System.out.println("Real time(ms): " + realTime);
            System.out.println("Simulation time(ms): " + Simulation.currTime);

            s.getNodeSet().closeNodes();

            BitcoinReporter.flushBlockReport();
            BitcoinReporter.flushStructReport();
            BitcoinReporter.flushEvtReport();
            BitcoinReporter.flushNodeReport();
            BitcoinReporter.flushInputReport();
            BitcoinReporter.flushConfig();
            // each node should log its own blockchain in the end
            for (INode node : ns.getNodes()) {
                System.out.println(ns.getNodes());
                System.out.println("Node " + node.getID() + " blockchain:");
                ((BitcoinNode) node).logLongestChain();
                //System.out.println(Arrays.toString(((BitcoinNode) node).blockchain.printStructure()));
            }

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