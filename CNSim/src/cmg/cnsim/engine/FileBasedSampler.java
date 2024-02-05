package cmg.cnsim.engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class FileBasedSampler extends AbstractSampler {

    private Queue<Float> transactionArrivalIntervals = new LinkedList<>();
    private Queue<Long> miningIntervals = new LinkedList<>();
    private Queue<Float> transactionFeeValues = new LinkedList<>();
    private Queue<Long> transactionSizes = new LinkedList<>();
    private Queue<Float> nodeElectricPowers = new LinkedList<>();
    private Queue<Float> nodeHashPowers = new LinkedList<>();
    private Queue<Float> nodeElectricityCosts = new LinkedList<>();
    private Queue<Float> connectionThroughputs = new LinkedList<>();
    private Queue<Integer> randomNodeIndices = new LinkedList<>();
    private String transactionsFilePath;
    private String nodesFilePath;
    private int requiredTransactionLines = Config.getPropertyInt("workload.numTransactions");
    private int requiredNodeLines = Config.getPropertyInt("net.numOfNodes");


    public FileBasedSampler(String transactionsFilePath, String nodesFilePath) {
        this.transactionsFilePath = transactionsFilePath;
        this.nodesFilePath = nodesFilePath;
        LoadConfig();
    }
    private Random random = new Random();


    private void LoadTransactionConfig() {
        int lineCount = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(transactionsFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lineCount++;
                String[] values = line.split(",");
                if (values.length != 4) {
                    continue; // Skip lines that don't have exactly 4 values
                }
                try {
                    transactionArrivalIntervals.add(Float.parseFloat(values[0].trim()));
                    miningIntervals.add(Long.parseLong(values[1].trim()));
                    transactionFeeValues.add(Float.parseFloat(values[2].trim()));
                    transactionSizes.add(Long.parseLong(values[3].trim()));
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing transaction line: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (lineCount < requiredTransactionLines) {
            System.err.println("Error: The transaction file does not contain enough lines. Required: " + requiredTransactionLines + ", Found: " + lineCount);
            // Handle this scenario as needed
        }
    }



    private void LoadNodeConfig() {
        int lineCount = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(nodesFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lineCount++;
                String[] values = line.split(",");
                if (values.length != 3) {
                    continue; // Skip lines that don't have exactly 4 values
                }
                try {
                    nodeElectricPowers.add(Float.parseFloat(values[0].trim()));
                    nodeHashPowers.add(Float.parseFloat(values[1].trim()));
                    nodeElectricityCosts.add(Float.parseFloat(values[2].trim()));

                } catch (NumberFormatException e) {
                    System.err.println("Error parsing node line: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (lineCount < requiredNodeLines) {
            System.err.println("Error: The node file does not contain enough lines. Required: " + requiredNodeLines + ", Found: " + lineCount);
            // Handle this scenario as needed
        }
    }


    private void LoadNodeConfi2g() {
        try (BufferedReader br = new BufferedReader(new FileReader(nodesFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length != 4) {
                    continue; // Skip lines that don't have exactly 4 values
                }
                try {
                    nodeElectricPowers.add(Float.parseFloat(values[0].trim()));
                    nodeHashPowers.add(Float.parseFloat(values[1].trim()));
                    nodeElectricityCosts.add(Float.parseFloat(values[2].trim()));
                    connectionThroughputs.add(Float.parseFloat(values[3].trim()));

                } catch (NumberFormatException e) {
                    System.err.println("Error parsing node line: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    // Override the methods to return values from the queues
    @Override
    public float getNextTransactionArrivalInterval() {
        if (transactionArrivalIntervals.isEmpty()) {
            System.out.println("Transaction arrival intervals queue is empty");
            return (float) getPoissonInterval(txArrivalIntervalRate)*1000;
            //TODO: Handle this case
        }
        return transactionArrivalIntervals.poll();
    }


    public double getPoissonInterval(float lambda) {
        if(lambda < 0)
            throw new ArithmeticException("lambda < 0");
        double p = random.nextDouble();
        while (p == 0.0){
            p = random.nextDouble();
        }
        return (double) (Math.log(1-p)/(-lambda));
    }


    @Override
    public long getNextMiningInterval(double hashPower) {
        return miningIntervals.poll();
    }

    @Override
    public float getNextTransactionFeeValue() {
        if (transactionFeeValues.isEmpty()) {
            return 0;
        }
        return transactionFeeValues.poll();
    }

    @Override
    public long getNextTransactionSize() {
        if (transactionSizes.isEmpty()) {
            return 0;
        }
        return transactionSizes.poll();
    }

    @Override
    public float getNextNodeElectricPower() {
        if (nodeElectricPowers.isEmpty()) {
            return 0;
        }
        return nodeElectricPowers.poll();
    }

    @Override
    public float getNextNodeHashPower() {
        if (nodeHashPowers.isEmpty()) {
            return 0;
        }
        return nodeHashPowers.poll();
    }

    @Override
    public float getNextNodeElectricityCost() {
        if (nodeElectricityCosts.isEmpty()) {
            return 0;
        }
        return nodeElectricityCosts.poll();
    }

    @Override
    public int getNextRandomNode(int nNodes) {
        if (randomNodeIndices.isEmpty()) {
            return 0;
        }
        return randomNodeIndices.poll();
    }

    private float getGaussian(float mean, float deviation) {
        if(deviation < 0)
            throw new ArithmeticException("Standard deviation < 0");
        float gaussianValue = mean + (float) random.nextGaussian() * deviation;
        while(gaussianValue <= 0) {
            gaussianValue = mean + (float) random.nextGaussian() * deviation;
        }
        return gaussianValue;
    }
    @Override
    public float getNextConnectionThroughput() {
        return (getGaussian(5000, 500));
    }

    @Override
    public int getRandomNum(int min, int max) {
        // Implement if needed, possibly by reading from the file or generating randomly
        return 0;
    }

    @Override
    public void LoadConfig() {
        LoadTransactionConfig();
        LoadNodeConfig();
    }

    @Override
    public void setSeed(long seed) {
        // Implement if needed
    }
}
