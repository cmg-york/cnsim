package cmg.cnsim.bitcoin;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import cmg.cnsim.engine.Config;
import cmg.cnsim.engine.ConfigInitializer;
import cmg.cnsim.engine.Debug;
import cmg.cnsim.engine.NetworkSamplerFactory;
import cmg.cnsim.engine.NodeSamplerFactory;
import cmg.cnsim.engine.Profiling;
import cmg.cnsim.engine.Sampler;
import cmg.cnsim.engine.Simulation;
import cmg.cnsim.engine.TransactionSamplerFactory;
import cmg.cnsim.engine.network.AbstractNetwork;
import cmg.cnsim.engine.network.FileBasedEndToEndNetwork;
import cmg.cnsim.engine.network.RandomEndToEndNetwork;
import cmg.cnsim.engine.node.AbstractNodeFactory;
import cmg.cnsim.engine.node.INode;
import cmg.cnsim.engine.node.Node;
import cmg.cnsim.engine.node.NodeSet;
import cmg.cnsim.engine.reporter.ReportEventFactory;
import cmg.cnsim.engine.transaction.Transaction;
import cmg.cnsim.engine.transaction.TransactionWorkload;


public class BitcoinMainDriver {

    public static void main(String[] args) {
        //run simulation with the given configuration for n times
        BitcoinMainDriver b = new BitcoinMainDriver();
        b.run(args);
    }


    private void run(String[] args) {
        //print current directory

        System.out.println("CNSim ver");
        
        
        System.out.println("  * Setting up environment:");

    	System.out.println("    Current directory: " + System.getProperty("user.dir"));


    	
    	System.out.println("    Initializing Configurator");
        // Initialize Config
        try{
            ConfigInitializer.initialize(args);
        } catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }

        // Get the number of simulations to run
        int numSimulations = Config.getPropertyInt("sim.numSimulations");


        // SIM SCOPE STARTS HERE
        for (int simID = 1; simID <= numSimulations; simID++) {
            runSingleSimulation(simID);
        }
        // SIM SCOPE ENDS HERE
        
        
        BitcoinReporter.flushBlockReport();
        BitcoinReporter.flushStructReport();
        BitcoinReporter.flushEvtReport();
        BitcoinReporter.flushNodeReport();
        BitcoinReporter.flushInputReport();
        BitcoinReporter.flushNetworkReport();
        BitcoinReporter.flushBeliefReport();
        BitcoinReporter.flushConfig();
    }

    private void runSingleSimulation(int simID) {
        //
        //
        // Creating simulation object
        //
        //
    	System.out.println("\n  * Setting up simulation #" + simID);
        Simulation s = new Simulation(simID);

        //
        //
        // Creating Sampler
        //
        //
    	System.out.println("    Creating and setting Sampler container for Sim #" + simID);
        Sampler sampler = new Sampler();

        //
        //
        // Set Sampler
        //
        //
        s.setSampler(sampler);


        //Develop sampler 1: Node Sampler
        //
    	System.out.println("    Creating and setting Node Sampler for Sim #" + simID);
        try {
            sampler.setNodeSampler(new NodeSamplerFactory().getSampler(
                    Config.getPropertyString("node.sampler.file"),
                    Config.getPropertyString("node.sampler.seed"),
                    Config.getPropertyString("node.sampler.seedUpdateTimes"),
                    Config.getPropertyString("node.sampler.updateSeedFlags"),
                    sampler,
                    s
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Develop sampler 2: Network Sampler
        //
        System.out.println("    Creating and setting NetworkSampler for Sim #" + simID);
        sampler.setNetworkSampler(new NetworkSamplerFactory().
        		getNetworkSampler(
        				(Config.hasProperty("net.sampler.seed") ? Config.getPropertyLong("net.sampler.seed") : null),
                        (Config.hasProperty("net.sampler.seed.updateSeed") ? Config.getPropertyBoolean("net.sampler.seed.updateSeed") : null),
                        sampler,
                        s));


        //Develop sampler 3: Transaction Sampler
        //
        System.out.println("    Creating and setting Transaction Sampler for Sim #" + simID);
        try {
            sampler.setTransactionSampler(
                    new TransactionSamplerFactory().getSampler(
                            Config.getPropertyString("workload.sampler.file"),
                            (Config.hasProperty("workload.sampler.seed") ? Config.getPropertyLong("workload.sampler.seed") : null),
                            (Config.hasProperty("workload.sampler.seed.updateSeed") ? Config.getPropertyBoolean("workload.sampler.seed.updateSeed") : null),
                            sampler,
                            s));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //
        //
        // Creating the nodes
        //
        //
        System.out.println("    Creating and adding Nodes for Sim #" + simID);
        AbstractNodeFactory nf = new BitcoinNodeFactory("Honest", s);
        NodeSet ns = new NodeSet(nf);
        ns.addNodes(Config.getPropertyInt("net.numOfHonestNodes"));

        ns.setNodeFactory(new BitcoinNodeFactory("Malicious", s, ns));
        ns.addNodes(Config.getPropertyInt("net.numOfMaliciousNodes"));

        
        
        //
        //
        // Creating the network
        //
        //

        //Define network.
        //If a file exists it will be file-based, otherwise, just create a standard network.
        System.out.println("    Creating Network for Sim #" + simID);
        AbstractNetwork net = null;
        String netFilePath = Config.getPropertyString("net.sampler.file");
        if (netFilePath != null) {
            try {
                Debug.p("    Creating file-based network.");
                net = new FileBasedEndToEndNetwork(ns, netFilePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                net = new RandomEndToEndNetwork(ns, sampler);
                Debug.p("     Creating random network.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        s.setNetwork(net);
        //n.printNetwork();

        //
        //
        // Creating the transaction workload
        //
        //

        // Transaction workload
        System.out.println("    Creating and Scheduling Workload for Sim #" + simID);
        TransactionWorkload ts = new TransactionWorkload(sampler);
        try {
            ts.appendTransactions(Config.getPropertyLong("workload.numTransactions"));
        } catch (Exception e) {
            e.printStackTrace();
        }


        //
        // Scheduling
        //
        //

        //Schedule the workload
        s.schedule(ts);

        
        //Schedule reporting events
        ReportEventFactory r = new ReportEventFactory();
        r.scheduleBeliefReports_Interval(1000000, s, 210000000);

        /*
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
		*/

        //
        //
        // Running the simulator
        //
        //

        System.out.println("\n  * Running Simulation #" + simID);
        Profiling.simBeginningTime = System.currentTimeMillis();
        s.run();
        //long realTime = (System.currentTimeMillis() - Profiling.simBeginningTime); // in Milli-Sec
        //System.out.print("\n");
        //System.out.println("    Real time(ms): " + realTime);
        //System.out.println("    Simulation time(ms): " + Simulation.currTime);


        //
        // Print some simulatoin stats
        //
        
        System.out.println(s.getStatistics());

        //
        //
        // Clean-up
        //
        //
        s.getNodeSet().closeNodes();

        
        //
        //
        // Reset Statics
        //
        //
        Node.resetCurrID();
        Transaction.resetCurrID();
        Block.resetCurrID();
        
        
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