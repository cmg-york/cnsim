package cmg.cnsim.engine.network;

import cmg.cnsim.engine.Config;
import cmg.cnsim.engine.Debug;
import cmg.cnsim.engine.Sampler;
import cmg.cnsim.engine.node.NodeSet;

public class NetworkFactory {
	public static AbstractNetwork createNetwork(NodeSet ns, Sampler sampler) {
		AbstractNetwork net = null;
		
		// try to read from config file, if available
		String netFilePath = Config.getPropertyString("net.sampler.file");
		if (netFilePath != null) {
			try {
				Debug.p("Creating file-based network.");
				net = new FileBasedEndToEndNetwork(ns, netFilePath);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				net = new RandomEndToEndNetwork(ns, sampler);
				Debug.p("Creating random network.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return net;
	}
}
