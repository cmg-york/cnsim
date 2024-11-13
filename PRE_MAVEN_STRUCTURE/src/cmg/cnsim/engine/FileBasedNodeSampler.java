package cmg.cnsim.engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class FileBasedNodeSampler extends AbstractNodeSampler {
		
	private AbstractNodeSampler alternativeSampler = null;
	private String nodesFilePath;

	private Queue<Float> nodeElectricPowers = new LinkedList<>();
	private Queue<Float> nodeHashPowers = new LinkedList<>();
	private Queue<Float> nodeElectricityCosts = new LinkedList<>();
	private int requiredNodeLines = Config.getPropertyInt("net.numOfNodes");


	public FileBasedNodeSampler(String nodesFilePath, AbstractNodeSampler nodeSampler) {
		this.nodesFilePath = nodesFilePath;
		this.alternativeSampler = nodeSampler;
		LoadNodeConfig();
	}

	public void LoadNodeConfig() {
		LoadNodeConfig(true);
	}
	
	public void LoadNodeConfig(boolean hasHeaders) {
		int lineCount = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(nodesFilePath))) {
			String line;
			while ((line = br.readLine()) != null) {
				lineCount++;
				String[] values = line.split(",");
				if (values.length != 4) {
					continue; // Skip lines that don't have exactly 4 values
				}
				if (hasHeaders && lineCount == 1) {
					continue; // Skip first line
				}
				try {
					nodeHashPowers.add(Float.parseFloat(values[1].trim()));
					nodeElectricPowers.add(Float.parseFloat(values[2].trim()));
					nodeElectricityCosts.add(Float.parseFloat(values[3].trim()));
				} catch (NumberFormatException e) {
					System.err.println("Error parsing node line: " + line);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (hasHeaders) lineCount--;
		if (lineCount < requiredNodeLines) {
						System.out.println("    The nodes file does not contain enough lines as per configuration file. Required: "
						+ requiredNodeLines + ", Found: " + lineCount + ". Additional nodes to be drawn from alternative sampler.");
		} else if (lineCount > requiredNodeLines) {
			System.out.println("    Warning: Nodes file contains more lines than required nodes as per configuration file. Required: "
					+ requiredNodeLines + ", Found: " + lineCount);
		}
	}

	public void updateSeed() {
		if (alternativeSampler != null) {
			alternativeSampler.updateSeed();
		} else {
			System.err.print("Error in update seed: alternativeSampler not defined.");
		}
			
	}
	
	
	@Override
	public long getNextMiningInterval(double hashPower) {
		return alternativeSampler.getNextMiningInterval(hashPower);
	}

	@Override
	public float getNextNodeElectricPower() {
		if (!nodeElectricPowers.isEmpty()) {
			return (nodeElectricPowers.poll());
		} else {
			return (alternativeSampler.getNextNodeElectricPower());
		}
	}

	@Override
	public float getNextNodeHashPower() {
		if (!nodeHashPowers.isEmpty()) {
			return (nodeHashPowers.poll());
		} else {
			return (alternativeSampler.getNextNodeHashPower());
		}
	}

	@Override
	public float getNextNodeElectricityCost() {
		if (!nodeElectricityCosts.isEmpty()) {
			return (nodeElectricityCosts.poll());
		} else {
			return (alternativeSampler.getNextNodeElectricityCost());
		}
	}

	@Override
	public int getNextRandomNode(int nNodes) {
		return (alternativeSampler.getNextRandomNode(nNodes));
	}
}
