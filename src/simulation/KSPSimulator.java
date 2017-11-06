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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import algorithm.Dijkstra;
import distribution.Distribution;
import distribution.Poisson;
import event.Event;
import event.EventGenerator;
import event.Scheduler;
import graph.AbstractGrafo;
import graph.Caminho;
import graph.ExcecaoGrafo;
import graph.Grafo;
import graph.No;
import opticalnetwork.controlplane.ControllerEON;
import opticalnetwork.controlplane.KShortestPathEONController;
import opticalnetwork.elastica.rsa.Bandwidth;
import opticalnetwork.elastica.rsa.RequestRSA;
import opticalnetwork.elastica.rsa.TrafficRSA;
import opticalnetwork.elastica.rsa.RequestRSA.RequestType;
import opticalnetwork.elastica.rssa.SwitchingType;
import stats.JobDataBase;
import topology.NetworkTopology;

public class KSPSimulator extends Simulator{

	public Logger logger ;
	
	private static final Calendar calendar = new GregorianCalendar();
	private DecimalFormat formatter;
	private String directory="";
	private String filename="";
	private String topology="";
	private String algorithm="";
	private int initialLoad = 0;
	private int incrementalLoad = 0;
	private int endLoad = 0;
	private int spectralSlots = 8;
	private int spatialDimension = 4;
	private int requestLimit = 1; 	//Ex.: 10ˆ6 requests
	private int seed = 666;
	private int numCarrierSlots = 4;
	private int numKShortestPaths = 1;
	private int steadyState = 10000;
	private double holdingTime = 100;
	private double totalInputLoad;
	private boolean bidirectional = false;
	private boolean isFixedBandwidth = false;
	private boolean debug = false;
	private Bandwidth bandwidth;
	private NetworkTopology networkTopology;
	private Map<Integer,ArrayList<Integer>> matrixInputNetworkLoadData;
	private Map<Integer,Double> matrixAverageInputNetworkLoad; //average Input Network Load
	private Map<Integer,Double> matrixProbabilities ;
	private Map<Integer, Double> matrixLoads;
	private RequestType requestType;
	private ControllerEON controller;
	private PrintWriter out;
	private int simulationID = 0;
	private SwitchingType switchingType;
	@SuppressWarnings("unused")
	private boolean isFewModeFiber;

	public KSPSimulator() {
		formatter = (DecimalFormat) DecimalFormat.getInstance(new Locale("en","US"));
		formatter.applyPattern("###0.000");
		matrixInputNetworkLoadData = new LinkedHashMap<Integer,ArrayList<Integer>>() ;
		matrixAverageInputNetworkLoad = new LinkedHashMap<Integer,Double>();
		matrixProbabilities = new LinkedHashMap<Integer, Double>();
		matrixLoads = new HashMap<Integer, Double>();
		
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
		String header = "SIM_ID,AVERAGE_INPUT_LOAD, LOAD,BLOKING_PROBABILITY\n";
		builder.append(header);

		for(Iterator<Entry<Integer, Double>> it = matrixProbabilities.entrySet().iterator() ; it.hasNext() ; ) {
			Entry<Integer,Double> e = it.next();
			/*appends the Simulation's ID*/
			builder.append(e.getKey()).append(",");

			/*appends the average of input network load*/
			formatter.applyPattern("0.00");
			double percentAverageInputNetworkLoad = 100.0*getMatrixAverageInputNetworkLoad().get(e.getKey());
			String averageInputNetworkLoad = formatter.format(percentAverageInputNetworkLoad);
			builder.append(averageInputNetworkLoad).append(",");
			
			/*appends the load in Erlangs*/
			formatter.applyPattern("0.00");
			double load = matrixLoads.get(e.getKey());
			String loadS = formatter.format(load);
			builder.append(loadS).append(",");

			/*appends the blocking probability*/
			formatter.applyPattern("0.0000000");
			String blockProbability = formatter.format(e.getValue());
			builder.append(blockProbability);
			builder.append("\n");
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
			if (i > steadyState) {
				sumInputLoad += inputNetworkLoadList.get(i);
			}
		}

		meanInputLoad = sumInputLoad/(this.totalInputLoad*(this.requestLimit-steadyState));

		matrixAverageInputNetworkLoad.put(load, meanInputLoad);

	}

	public double getInputLoad (ArrayList<Integer> inputNetworkLoadList) {
		double sumInputLoad = 0;
		double meanInputLoad ;

		for ( int i = 0 ; i < inputNetworkLoadList.size() ; i++ ) {
			if (i > steadyState) {
				sumInputLoad += inputNetworkLoadList.get(i);
			}
		}

		meanInputLoad = sumInputLoad/(this.totalInputLoad*(this.requestLimit-steadyState));
		return meanInputLoad;
	} 

	public void saveBlockingProbability (int simID, double blockingProbability) {
		matrixProbabilities.put(simID, blockingProbability);
	}

	/**
	 * @return the log
	 */
	public Logger getLog() {
		return logger;
	}

	/**
	 * @param log the log to set
	 */
	public void setLog(Logger log) {
		this.logger = log;
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

	/**
	 * @return the isFixedBandwidth
	 */
	public boolean isFixedBandwidth() {
		return isFixedBandwidth;
	}

	/**
	 * @param isFixedBandwidth the isFixedBandwidth to set
	 */
	public void setFixedBandwidth(boolean isFixedBandwidth) {
		this.isFixedBandwidth = isFixedBandwidth;
	}

	/**
	 * @return the bandwidth
	 */
	public Bandwidth getBandwidth() {
		return bandwidth;
	}

	/**
	 * @param bandwidth the bandwidth to set
	 */
	public void setBandwidth(Bandwidth bandwidth) {
		this.bandwidth = bandwidth;
	}

	/**
	 * @return the numKShortestPaths
	 */
	public int getNumKShortestPaths() {
		return numKShortestPaths;
	}

	/**
	 * @param numKShortestPaths the numKShortestPaths to set
	 */
	public void setNumKShortestPaths(int numKShortestPaths) {
		this.numKShortestPaths = numKShortestPaths;
	}

	/**
	 * @return the algorithm
	 */
	public String getAlgorithm() {
		return algorithm;
	}

	/**
	 * @param algorithm the algorithm to set
	 */
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	/**
	 * @return the debug
	 */
	public boolean isDebug() {
		return debug;
	}

	/**
	 * @param debug the debug to set
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public JobDataBase simulate () throws ExcecaoGrafo, Exception {
		
		/*if (debug) {
//			logger.setUseParentHandlers(false);
		}*/
		this.logger = Logger.getLogger(algorithm);
		
		String startTimeString = calendar.get(Calendar.DAY_OF_MONTH)+"."+
				calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT,Locale.getDefault())+"-"+
				calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND);
		long start = System.currentTimeMillis();
		long startTemp = start;
		long end = 0;
		logger.info("Simulation started at "+ startTimeString);
		String s = ("RSSA heuristic policy: " + algorithm);
		logger.info(s);
		s = ("Network topology: " + topology);
		logger.info(s);
		s = ("Numbef of K shortest paths: " + numKShortestPaths);
		logger.info(s);
		s = "number of requests in total: " + requestLimit;
		logger.info(s);
		s = ("Total spectrum slots per fiber: " + spectralSlots);
		logger.info(s);
		s = ("Total of spatial dimentions: " + spatialDimension);
		logger.info(s);
		s = ("Shortest slots per Super Channel : " + numCarrierSlots);
		logger.info(s);
		s = ("Seed: " + seed);
		logger.info(s);
		s = ("Mean holding time: " + holdingTime );
		logger.info(s);
		s = ("initial load: " + initialLoad );
		logger.info(s);
		s = ("end load: " + endLoad);
		logger.info(s);
		s = ("Load incremental factor: " + incrementalLoad);
		logger.info(s);
		s = ("Use fixed bandwidth: " + isFixedBandwidth);
		logger.info(s);
		s = ("Debug: " + debug);
		logger.info(s);


		Grafo graph = (Grafo) NetworkTopology.getGraph(NetworkTopology.valueOf(topology));
		double totalInputNetworkLoad = spectralSlots*spatialDimension*graph.getEnlaces().tamanho()/2;
		setTotalInputLoad(totalInputNetworkLoad);
		logger.info("Total Input Network Load: " + totalInputNetworkLoad);

		double meanSlotSize = 0.0;
		if (algorithm.equals(RequestRSA.RequestType.SPACE_FIRST.name())) {
			meanSlotSize = mean( Bandwidth.getSpatialSlotValues());
//			meanSlotSize = mean( Bandwidth.getSpectrumSlotsValues());
		} else {
			meanSlotSize = mean( Bandwidth.getSpectrumSlotsValues());
		}
		
		logger.info("Mean Slot Size: " + meanSlotSize);
		
		double meanDistanceSize = getAverageHopsNumberPerPath(graph);
		logger.info("Mean Distance Size: " + meanDistanceSize);

		//for ( int load = initialLoad; load <= endLoad ; /*load += incrementalLoad*/){
		for ( int inputload = initialLoad; inputload <= endLoad ; ) {
			simulationID++;
			logger.info("Input Load (%): " + inputload);
//			Double loadD = 0.01*inputload*totalInputNetworkLoad/(meanDistanceSize*meanSlotSize);
			Double loadD = (double)inputload;
			@SuppressWarnings("unused")
			int load = loadD.intValue();
			Scheduler escalonador = new Scheduler();
			holdingTime = loadD;
			Distribution distribuicao = new Poisson(seed,loadD,holdingTime);
			EventGenerator gerador = new EventGenerator(distribuicao);

			TrafficRSA traffic = null;
			if (!isFixedBandwidth()) {
				traffic = new TrafficRSA(seed, graph, requestLimit, distribuicao);
			} else {
				traffic = new TrafficRSA(seed, graph, requestLimit, distribuicao, isFixedBandwidth, bandwidth);
			}
			traffic.setRequestType(requestType);
			traffic.setNumKShortestPaths(numKShortestPaths);

			gerador.setFonte(traffic);
			escalonador.insertGenerator(gerador);

			KShortestPathEONController controle = new KShortestPathEONController(graph, this.spectralSlots, this.spatialDimension, this.requestLimit);
			controle.setBidirectional(bidirectional);
			controle.setMinimumSpectralSlotsPerCarrier(getNumCarrierSlots());
			controle.setDebug(debug);
			controle.setTotalInputNetworkLoad(totalInputNetworkLoad);
			controle.setCounter(traffic.getCounter());
			controle.setSteadyState(steadyState);



			do {
				Event evento = escalonador.exec();
				escalonador.insertEvent(controle.receberEvento(evento));
				
				//			} while (traffic.getCounter() < requestLimit || escalonador.getListaEventos().tamanho() > 0);
			} while (traffic.getCounter().getValue() < requestLimit);
			
			double lambda = 1/controle.getTempoMedioEntreChegadas();
			double mu = 1/controle.getTempoMedioDuracoes();
			//changing for inputload increment
			double meanInputLoad = lambda*meanSlotSize*meanDistanceSize/(mu*totalInputNetworkLoad);
//			System.out.println(algorithm+": mean input load generated: " + meanInputLoad);
//			System.out.println(algorithm+": input load : " + inputload);
			/*saves the input load array in the simulator do generete the mean*/
//			this.saveInputLoad(load, controle.getInputNetworkLoadDataList());
			matrixAverageInputNetworkLoad.put(simulationID, meanInputLoad);
			matrixLoads.put(simulationID, loadD);
			/*saves the blocking probability for each load in the loop*/
			this.saveBlockingProbability(simulationID, controle.probabilidadeBloqueio());
			end = System.currentTimeMillis();
			//Calculates and shows the simulation's time
			long partial = end-startTemp;
			startTemp=end;
			String time = String.format("%02d:%02d:%02d",
					TimeUnit.MILLISECONDS.toHours(partial),
					TimeUnit.MILLISECONDS.toMinutes(partial) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(partial)),
					TimeUnit.MILLISECONDS.toSeconds(partial) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(partial)));
			formatter.applyPattern("##0.00000");
			String lambdaS = formatter.format(lambda);
			s = algorithm+": Partial simulation's time= "+ time + ", with load = "+ formatter.format(loadD) + ", lambda = " + lambdaS + ", inputLoad (%)= " + meanInputLoad*100;
			logger.info(s);

			inputload += incrementalLoad;
			
		}
		s = toStringSimulation();
		out.print(s);
		out.flush();
		out.close();
		System.out.println(s);

		end = System.currentTimeMillis();
		long total = end-start;
		String time = String.format("%02d:%02d:%02d",
				TimeUnit.MILLISECONDS.toHours(total),
				TimeUnit.MILLISECONDS.toMinutes(total) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(total)),
				TimeUnit.MILLISECONDS.toSeconds(total) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(total)));
		s = "simulation's total time = " + time + "\n#------------# END "+ algorithm +" #------------#\n";
		logger.info(s);
		return null;


	}

	/**
	 * @return the steadyState
	 */
	public int getSteadyState() {
		return steadyState;
	}

	/**
	 * @param steadyState the steadyState to set
	 */
	public void setSteadyState(int steadyState) {
		this.steadyState = steadyState;
	}

	public int sum (List<? extends Number> a){
		if (a.size() > 0) {
			int sum = 0;

			for ( int i = 0 ; i < a.size() ; i++) {
				if (a.get(i) instanceof Double ) {
					sum += a.get(i).intValue();
				} else {
					sum += (int) a.get(i);
				}
			}
			return sum;
		}
		return 0;
	}

	
	/**
	 * Return the arithmetic mean of a list with numbers 
	 * @param a the list with numbers
	 * @return
	 */
	public double mean (List<? extends Number> a){
		int sum = sum(a);
		double mean = 0;
		mean = sum / (a.size() * 1.0);
		return mean;
	}

	public double getAverageHopsNumberPerPath (AbstractGrafo graph) throws ExcecaoGrafo {
		int numNodes = graph.getNos().tamanho();
		HashMap<String, Caminho> routeTable = new HashMap<String, Caminho>();
		List<Double> distances = new ArrayList<Double>();
		for (int i = 1 ; i <= numNodes; i++) {

			for (int k = 1 ; k <= numNodes ; k++) {
				if (k != i) {
					No source = graph.getNo(new Integer(i).toString());
					No destination = graph.getNo(new Integer(k).toString());
					Caminho path = Dijkstra.getMenorCaminho(graph, source, destination);
					String key = source.getId()+"-"+destination.getId();
					if (!routeTable.containsKey(key)) {
						routeTable.put(key,path);
					}		
				}
			}	

			for (Iterator<String> it = routeTable.keySet().iterator() ; it.hasNext() ; ) {
				String key = it.next();
				Caminho path = routeTable.get(key);
				distances.add(path.getDistancia());
			}

		}

		return this.mean(distances);
	}

	/* (non-Javadoc)
	 * @see simulation.Simulator#setFewModeFiber(boolean)
	 */
	@Override
	public void setFewModeFiber(boolean isFewModeFiber) {
		this.isFewModeFiber = isFewModeFiber;
		
	}

	/**
	 * @return the switchingType
	 */
	public SwitchingType getSwitchingType() {
		return switchingType;
	}

	/**
	 * @param switchingType the switchingType to set
	 */
	public void setSwitchingType(SwitchingType switchingType) {
		this.switchingType = switchingType;
	}

}

