package cmg.cnsim.engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class FileBasedSampler extends AbstractSampler {

    private final Queue<Float> transactionArrivalIntervals = new LinkedList<>();
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

    private StandardSampler standardSampler;


    public FileBasedSampler(String transactionsFilePath, String nodesFilePath) {
        this.transactionsFilePath = transactionsFilePath;
        this.nodesFilePath = nodesFilePath;
        this.standardSampler = new StandardSampler();
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

    // Override the methods to return values from the queues
    @Override
    public float getNextTransactionArrivalInterval() {
        return transactionArrivalIntervals.isEmpty() ? standardSampler.getNextTransactionArrivalInterval() : transactionArrivalIntervals.poll();
    }


    @Override
    public long getNextMiningInterval(double hashPower) {
        //TODO modification in transaction generator so it will work fine with mining interval
        return standardSampler.getNextMiningInterval(hashPower);
        //return miningIntervals.isEmpty() ? standardSampler.getNextMiningInterval(hashPower) : miningIntervals.poll();
    }

    @Override
    public float getNextTransactionFeeValue() {
        return transactionFeeValues.isEmpty() ? standardSampler.getNextTransactionFeeValue() : transactionFeeValues.poll();
    }

    @Override
    public long getNextTransactionSize() {
        return transactionSizes.isEmpty() ? standardSampler.getNextTransactionSize() : transactionSizes.poll();
    }

    @Override
    public float getNextNodeElectricPower() {
        return nodeElectricPowers.isEmpty() ? standardSampler.getNextNodeElectricPower() : nodeElectricPowers.poll();
    }

    @Override
    public float getNextNodeHashPower() {
        return nodeHashPowers.isEmpty() ? standardSampler.getNextNodeHashPower() : nodeHashPowers.poll();
    }

    @Override
    public float getNextNodeElectricityCost() {
        return nodeElectricityCosts.isEmpty() ? standardSampler.getNextNodeElectricityCost() : nodeElectricityCosts.poll();
    }

    @Override
    public int getNextRandomNode(int nNodes) {
        return(random.nextInt(nNodes));
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
        return (getGaussian(netThroughputMean, netThroughputSD));
    }

    @Override
    public int getRandomNum(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    @Override
    public void LoadConfig() {
        LoadTransactionConfig();
        LoadNodeConfig();
        LoadStandardSamplerConfig();
        this.setNetThroughputMean(5000);
        this.setNetThroughputSD(500);
    }

    private void LoadStandardSamplerConfig() {
        standardSampler.LoadConfig();
    }

    @Override
    public void setSeed(long seed) {
        // Implement if needed
        super.randomSeed = seed;
        random.setSeed(seed);
    }
}
