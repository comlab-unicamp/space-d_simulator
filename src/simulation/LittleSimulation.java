/**
 * Created on 07/02/2016
 */
package simulation;

import graph.ExcecaoGrafo;
import opticalnetwork.elastica.rsa.RequestRSA.RequestType;

/**
 * @author Alaelson Jatobá
 * Version 1.0
 */
public class LittleSimulation {

	public static void main (String[] args) {
		RequestType requestType = RequestType.SPECTRUM_FIRST_FIT_SLIGHTLY;
//		RequestType requestType = RequestType.SPECTRUM_FIRST;
//		RequestType requestType = RequestType.SPACE_FIRST;
		String topologia = "NSFNET";
		String dir = "./sim_data/";
		String filename = dir+"ksp"+topologia+"_"+requestType+"_data.csv";

		double holdingTime = 100;
		int requestLimit = 1000000;
		int seed = 666;
		int initialLoad = 10;
		int endLoad = 200;
		int incrementalLoad = 10;
		int spectralSlots = 8;
		int spatialDimension = 4;
		boolean bidirectional = true;
		int numCarrierSlots = 4;



		KSPSimulator simulator = new KSPSimulator();
		simulator.setDirectory(dir);
		simulator.setFilename(filename);
		simulator.setInitialLoad(initialLoad);
		simulator.setIncrementalLoad(incrementalLoad);
		simulator.setEndLoad(endLoad);
		simulator.setHoldingTime(holdingTime);
		simulator.setRequestLimit(requestLimit);
		simulator.setSeed(seed);
		simulator.setBidirectional(bidirectional);
		simulator.setTopology(topologia);
		simulator.setRequestType(requestType);
		if (requestType.equals(RequestType.SPECTRUM_FIRST_FIT_SLIGHTLY)) {
			simulator.setSpectralSlots(spectralSlots*spatialDimension);
			simulator.setSpatialDimension(1);
		} else {
			simulator.setSpectralSlots(spectralSlots);
			simulator.setSpatialDimension(spatialDimension);
		}

		simulator.setNumCarrierSlots(numCarrierSlots);


		try {
			simulator.simulate();
		} catch (ExcecaoGrafo e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
