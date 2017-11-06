/**
 * Created on 04/02/2016
 */
package simulation;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import distribution.Distribution;
import distribution.Poisson;
import event.Event;
import event.EventGenerator;
import event.Scheduler;
import graph.ExcecaoGrafo;
import graph.Grafo;
import opticalnetwork.controlplane.ControllerEON;
import opticalnetwork.elastica.rsa.TrafficRSA;
import opticalnetwork.elastica.rsa.RequestRSA.RequestType;
import topology.NetworkTopology;

public class OldSimulator {

	public static Logger log = Logger.getLogger("ReproducingLog.log");

	private static final Calendar calendar = new GregorianCalendar();
	private DecimalFormat formatter;
	private String directory;
	private String filename;
	private String topology;
	private int initialLoad = 0;
	private int incrementalLoad = 0;
	private int endLoad = 0;
	private int spectralSlots = 8;
	private int spatialDimension = 4;
	private int requestLimit = 100000; 	//Ex.: 10ˆ6 requests
	private int seed = 666;
	private int numCarrierSlots = 4;



	private double holdingTime = 100;
	private double totalInputLoad;
	private boolean bidirectional = false;
	private NetworkTopology networkTopology;
	private Map<Integer,ArrayList<Integer>> matrixInputNetworkLoadData;
	private Map<Integer,Double> matrixAverageInputNetworkLoad; //average Input Network Load
	private Map<Integer,Double> matrixProbabilities ;
	private RequestType requestType;
	private ControllerEON controller;
	private PrintWriter out;



	public OldSimulator() {
		formatter = (DecimalFormat) DecimalFormat.getInstance(new Locale("en","US"));
		formatter.applyPattern("###0.000");
		matrixInputNetworkLoadData = new LinkedHashMap<Integer,ArrayList<Integer>>() ;
		matrixAverageInputNetworkLoad = new LinkedHashMap<Integer,Double>();
		matrixProbabilities = new LinkedHashMap<Integer, Double>();
	}

	/**
	 * @return the directory
	 */
	public String getDirectory() {
		return directory;
	}

	/**
	 * @param directory the directory to set
	 */
	public void setDirectory(String directory) {
		this.directory = directory;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
		try {
			out = getWriter(filename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the networkTopology
	 */
	public NetworkTopology getNetworkTopology() {
		return networkTopology;
	}

	/**
	 * @param networkTopology the networkTopology to set
	 */
	public void setNetworkTopology(NetworkTopology networkTopology) {
		this.networkTopology = networkTopology;
	}

	public DecimalFormat getFormatter() {
		return this.formatter;
	}

	public PrintWriter getWriter(String filename) throws FileNotFoundException {
		FileOutputStream outFile = new FileOutputStream(filename);
		PrintWriter out = new PrintWriter(outFile);
		return out;
	}


	/**
	 * Creates a String with the data in a CSV format to be printed;
	 *
	 * @return a string with the simulation's date in a CSV format
	 */
	public String toStringSimulation(){
		StringBuilder builder = new StringBuilder();
		String header = "LOAD,AVERAGE_INPUT_LOAD,BLOKING_PROBABILITY\n";
		builder.append(header);

		for(Iterator<Entry<Integer, Double>> it = matrixProbabilities.entrySet().iterator() ; it.hasNext() ; ) {
			Entry<Integer,Double> e = it.next();
			/*appends the load in Erlangs*/
			builder.append(e.getKey()).append(",");

			/*appends the average of input network load*/
			formatter.applyPattern("0.00");
			double percentAverageInputNetworkLoad = 100.0*getMatrixAverageInputNetworkLoad().get(e.getKey());
			String averageInputNetworkLoad = formatter.format(percentAverageInputNetworkLoad);
			builder.append(averageInputNetworkLoad).append(",");

			/*appends the blocking probability*/
			formatter.applyPattern("0.0000000");
			String blockProbability = formatter.format(e.getValue());
			builder.append(blockProbability);
			if (it.hasNext()) {
				builder.append("\n");
			}
		}
		return builder.toString();
	}


	/**
	 * @return the matrixAverageInputNetworkLoad
	 */
	public Map<Integer, Double> getMatrixAverageInputNetworkLoad() {
		return matrixAverageInputNetworkLoad;
	}

	/**
	 * @param matrixAverageInputNetworkLoad the matrixAverageInputNetworkLoad to set
	 */
	public void setMatrixAverageInputNetworkLoad (Map<Integer, Double> matrixAverageInputNetworkLoad) {
		this.matrixAverageInputNetworkLoad = matrixAverageInputNetworkLoad;
	}

	public void saveInputLoad (int load, ArrayList<Integer> inputNetworkLoadList) {
		double sumInputLoad = 0;
		double meanInputLoad ;

		for ( int i = 0 ; i < inputNetworkLoadList.size() ; i++ ) {
			sumInputLoad += inputNetworkLoadList.get(i);
		}

		meanInputLoad = sumInputLoad/(this.totalInputLoad*this.requestLimit);

		matrixAverageInputNetworkLoad.put(load, meanInputLoad);

	}

	public void saveBlockingProbability (int load , double blockingProbability) {
		matrixProbabilities.put(load, blockingProbability);
	}

	/**
	 * @return the log
	 */
	public static Logger getLog() {
		return log;
	}

	/**
	 * @param log the log to set
	 */
	public static void setLog(Logger log) {
		OldSimulator.log = log;
	}

	/**
	 * @return the initialLoad
	 */
	public int getInitialLoad() {
		return initialLoad;
	}

	/**
	 * @param initialLoad the initialLoad to set
	 */
	public void setInitialLoad(int initialLoad) {
		this.initialLoad = initialLoad;
	}

	/**
	 * @return the incrementalLoad
	 */
	public int getIncrementalLoad() {
		return incrementalLoad;
	}

	/**
	 * @param incrementalLoad the incrementalLoad to set
	 */
	public void setIncrementalLoad(int incrementalLoad) {
		this.incrementalLoad = incrementalLoad;
	}

	/**
	 * @return the endLoad
	 */
	public int getEndLoad() {
		return endLoad;
	}

	/**
	 * @param endLoad the endLoad to set
	 */
	public void setEndLoad(int endLoad) {
		this.endLoad = endLoad;
	}

	/**
	 * @return the matrixProbabilities
	 */
	public Map<Integer, Double> getMatrixProbabilities() {
		return matrixProbabilities;
	}

	/**
	 * @param matrixProbabilities the matrixProbabilities to set
	 */
	public void setMatrixProbabilities(Map<Integer, Double> matrixProbabilities) {
		this.matrixProbabilities = matrixProbabilities;
	}

	/**
	 * @return the controller
	 */
	public ControllerEON getController() {
		return controller;
	}

	/**
	 * @param controller the controller to set
	 */
	public void setController(ControllerEON controller) {
		this.controller = controller;
	}

	/**
	 * @return the spectralSlots
	 */
	public int getSpectralSlots() {
		return spectralSlots;
	}

	/**
	 * @param spectralSlots the spectralSlots to set
	 */
	public void setSpectralSlots(int spectralSlots) {
		this.spectralSlots = spectralSlots;
	}


	/**
	 * @return the holdingTime
	 */
	public double getHoldingTime() {
		return holdingTime;
	}

	/**
	 * @param holdingTime the holdingTime to set
	 */
	public void setHoldingTime(double holdingTime) {
		this.holdingTime = holdingTime;
	}

	/**
	 * @return the spatialDimension
	 */
	public int getSpatialDimension() {
		return spatialDimension;
	}

	/**
	 * @param spatialDimension the spatialDimension to set
	 */
	public void setSpatialDimension(int spatialDimension) {
		this.spatialDimension = spatialDimension;
	}

	/**
	 * @return the requestLimit
	 */
	public int getRequestLimit() {
		return requestLimit;
	}

	/**
	 * @param requestLimit the requestLimit to set
	 */
	public void setRequestLimit(int requestLimit) {
		this.requestLimit = requestLimit;
	}

	/**
	 * @return the seed
	 */
	public int getSeed() {
		return seed;
	}

	/**
	 * @param seed the seed to set
	 */
	public void setSeed(int seed) {
		this.seed = seed;
	}

	/**
	 * @return the bidirectional
	 */
	public boolean isBidirectional() {
		return bidirectional;
	}

	/**
	 * @param bidirectional the bidirectional to set
	 */
	public void setBidirectional(boolean bidirectional) {
		this.bidirectional = bidirectional;
	}

	/**
	 * @return the topology
	 */
	public String getTopology() {
		return topology;
	}

	/**
	 * @param topology the topology to set
	 */
	public void setTopology(String topology) {
		this.topology = topology;
	}

	/**
	 * @return the requestType
	 */
	public RequestType getRequestType() {
		return requestType;
	}

	/**
	 * @param requestType the requestType to set
	 */
	public void setRequestType(RequestType requestType) {
		this.requestType = requestType;
	}


	/**
	 * @return the matrixInputNetworkLoadData
	 */
	public Map<Integer, ArrayList<Integer>> getMatrixInputNetworkLoadData() {
		return matrixInputNetworkLoadData;
	}

	/**
	 * @param matrixInputNetworkLoadData the matrixInputNetworkLoadData to set
	 */
	public void setMatrixInputNetworkLoadData( Map<Integer, ArrayList<Integer>> matrixInputNetworkLoadData) {
		this.matrixInputNetworkLoadData = matrixInputNetworkLoadData;
	}

	/**
	 * @return the totalInputLoad
	 */
	public double getTotalInputLoad() {
		return totalInputLoad;
	}

	/**
	 * @param totalInputLoad the totalInputLoad to set
	 */
	public void setTotalInputLoad(double totalInputLoad) {
		this.totalInputLoad = totalInputLoad;
	}

	/**
	 * @return the numCarrierSlots
	 */
	public int getNumCarrierSlots() {
		return numCarrierSlots;
	}

	/**
	 * @param numCarrierSlots the numCarrierSlots to set
	 */
	public void setNumCarrierSlots(int numCarrierSlots) {
		this.numCarrierSlots = numCarrierSlots;
	}

	public void simulate () throws ExcecaoGrafo, Exception {
		String startTimeString = calendar.get(Calendar.DAY_OF_MONTH)+"."+
				calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT,Locale.getDefault())+"-"+
				calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND);
		long start = System.currentTimeMillis();
		long startTemp = start;
		long end = 0;
		log.info("Simulation started at "+ startTimeString);
		String s = "number of requests in total: " + requestLimit;
		log.info(s);
		s = ("Total spectrum slots per fiber: " + spectralSlots);
		log.info(s);
		s = ("Total of spatial dimentions: " + spatialDimension);
		log.info(s);
		s = ("Seed: " + seed);
		log.info(s);
		s = ("Mean holding time: " + holdingTime );
		log.info(s);
		s = ("initial load: " + initialLoad );
		log.info(s);
		s = ("end load: " + endLoad);
		log.info(s);
		s = ("Load incremental factor: " + incrementalLoad);
		log.info(s);
		s = ("Network Topology: " + topology);
		log.info(s);

		Grafo graph = (Grafo) NetworkTopology.getGraph(NetworkTopology.valueOf(topology));
		double totalInputNetworkLoad = spectralSlots*spatialDimension*graph.getEnlaces().tamanho();
		setTotalInputLoad(totalInputNetworkLoad);
		log.info("Total Input Network Load: " + totalInputNetworkLoad);

		for ( int load = initialLoad; load <= endLoad ; load += incrementalLoad){


			Scheduler escalonador = new Scheduler();
			Distribution distribuicao = new Poisson(load,holdingTime);
			EventGenerator gerador = new EventGenerator(distribuicao);

			TrafficRSA traffic = new TrafficRSA(seed, graph, requestLimit, distribuicao);
			traffic.setRequestType(requestType);

			gerador.setFonte(traffic);
			escalonador.insertGenerator(gerador);

			ControllerEON controle = new ControllerEON(graph, this.spectralSlots, this.spatialDimension, this.requestLimit);
			controle.setBidirectional(bidirectional);
			controle.setMinimumSpectralSlotsPerCarrier(getNumCarrierSlots());


			do {

				Event evento = escalonador.exec();
				escalonador.insertEvent(controle.receberEvento(evento));

			} while (traffic.getCounter().getValue() < requestLimit || escalonador.getEventList().size() > 0);


			/*saves the input load array in the simulator do generete the mean*/
			this.saveInputLoad(load, controle.getInputNetworkLoadDataList());
			/*saves the blocking probability for each load in the loop*/
			this.saveBlockingProbability(load, controle.probabilidadeBloqueio());
			end = System.currentTimeMillis();
			long partial = end-startTemp;
			startTemp=end;
			Calendar time = calendar;
			time.setTimeInMillis(partial);
			s = "Partial simulation's time: "+time.get(Calendar.SECOND) + "s, with load = "+ load;
			log.info(s);

		}
		s = toStringSimulation();
		out.print(s);
		out.flush();
		out.close();
		System.out.println(s);

		end = System.currentTimeMillis();
		long total = end-start;
		Calendar time = GregorianCalendar.getInstance();
		time.setTimeInMillis(total);
		s = "simulation's total time= "+time.get(Calendar.MINUTE)+":"+time.get(Calendar.SECOND);
		log.info(s);


	}


	public static void main(String[] args){
//		String arq_conf = "./configuracao.xml";

		RequestType requestType = RequestType.SPECTRUM_FIRST;
		String topologia = "SPANISH";
		String dir = "./sim_data/";
		String filename = dir+topologia+"_"+requestType+"_data.csv";

		double holdingTime = 100;
		int requestLimit = 1000000;
		int seed = 666;
		int initialLoad = 100;
		int endLoad = 100100;
		int incrementalLoad = 100;
		int spectralSlots = 384;
		int spatialDimension = 4;
		boolean bidirectional = true;
		int numCarrierSlots = 4;

		OldSimulator simulator = new OldSimulator();
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
		simulator.setSpectralSlots(spectralSlots);
		simulator.setSpatialDimension(spatialDimension);
		simulator.setNumCarrierSlots(numCarrierSlots);


		try {

			simulator.simulate();

			requestType = RequestType.SPECTRUM_FIRST_FIT_SLIGHTLY;
			filename = dir+topologia+"_"+requestType+"_data.csv";
			OldSimulator simulator2 = new OldSimulator();

			simulator2.setDirectory(dir);
			simulator2.setFilename(filename);
			simulator2.setInitialLoad(initialLoad);
			simulator2.setIncrementalLoad(incrementalLoad);
			simulator2.setEndLoad(endLoad);
			simulator2.setHoldingTime(holdingTime);
			simulator2.setRequestLimit(requestLimit);
			simulator2.setSeed(seed);
			simulator2.setBidirectional(bidirectional);
			simulator2.setTopology(topologia);
			simulator2.setRequestType(requestType);
			simulator2.setSpectralSlots(spectralSlots*spatialDimension);
			simulator2.setSpatialDimension(1);
			simulator2.setNumCarrierSlots(numCarrierSlots);

			simulator2.simulate();


			simulator.simulate();

			requestType = RequestType.SPACE_FIRST;
			filename = dir+"Reproducing_"+topologia+"_"+requestType+"_data.csv";
			OldSimulator simulator3 = new OldSimulator();

			simulator3.setDirectory(dir);
			simulator3.setFilename(filename);
			simulator3.setInitialLoad(initialLoad);
			simulator3.setIncrementalLoad(incrementalLoad);
			simulator3.setEndLoad(endLoad);
			simulator3.setHoldingTime(holdingTime);
			simulator3.setRequestLimit(requestLimit);
			simulator3.setSeed(seed);
			simulator3.setBidirectional(bidirectional);
			simulator3.setTopology(topologia);
			simulator3.setRequestType(requestType);
			simulator3.setSpectralSlots(spectralSlots);
			simulator3.setSpatialDimension(spatialDimension);
			simulator3.setNumCarrierSlots(numCarrierSlots);

			simulator3.simulate();

		} catch (ExcecaoGrafo e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


}

