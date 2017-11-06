/**
 *
 */
package simulation;

import graph.AbstractGrafo;
import graph.Caminho;
import graph.ExcecaoGrafo;
import graph.Grafo;
import graph.No;
import opticalnetwork.controlplane.ControllerEON;
import opticalnetwork.elastica.rsa.Bandwidth;
import opticalnetwork.elastica.rsa.ModulationFormat;
import opticalnetwork.elastica.rsa.OpticalSuperChannel;
import opticalnetwork.elastica.rsa.RequestRSA.RequestType;
import opticalnetwork.elastica.rssa.SwitchingType;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import algorithm.Dijkstra;
import topology.NetworkTopology;
import util.Constants;
import event.Event;

/**
 * Created in 04/07/2016
 * By @author Alaelson Jatoba
 * @version 1.0
 */
public abstract class Simulator implements SimulatorI{

	public static final String TROUGHPUT_HEADER = "TROUGHPUT";
	public static final String BP_HEADER = "BLOCKING_PROBABILITY";
	protected static final Calendar calendar = new GregorianCalendar();
	public static Logger logger = Logger.getLogger("Simulator");
	protected DecimalFormat formatter;
	protected String directory="";
	protected String filename="";
	protected String filenameDataSet="";
	protected String topology="";
	protected String algorithm="";
	protected int initialLoad = 0;
	protected int incrementalLoad = 0;
	protected int endLoad = 0;
	protected int spectralSlots = 8;
	protected int spatialDimension = 4;
	protected int requestLimit = 1; 	//Ex.: 10ˆ6 requests
	protected int seed = 666;
	protected int numCarrierSlots = 4;
	protected int numKShortestPaths = 1;
	protected int steadyState = 10000;
	protected long start = 0;
	protected long startTemp = start;
	protected long end = 0;
	protected double holdingTime = 100.0;
	protected double totalInputLoad = 0.0;
	protected double totalInputNetworkLoad = 0.0;
	protected double meanSlotSize = 0.0;
	protected double meanDistanceSize = 0.0;
	protected boolean bidirectional = false;
	protected boolean isIncremental = false;
	protected boolean isFixedBandwidth = false;
	protected boolean debug = false;
	protected Bandwidth bandwidth;
	protected NetworkTopology networkTopology;
	protected Grafo graph;
	protected Map<Integer,ArrayList<Integer>> matrixInputNetworkLoadData;
	protected Map<Integer,Double> matrixAverageInputNetworkLoad; //average Input Network Load
	protected Map<Integer,Double> matrixProbabilities ;
	protected Map<Integer, Double> matrixLoads;
	protected Map<Integer, Double> matrixArrivals;
	protected Map<Integer, Double> matrixThroughput;
	protected RequestType requestType;
	protected ControllerEON controller;
	protected PrintWriter out;
	protected int simulationID = 0;
	protected double signalBw = Constants.SIGNAL_BW_32GHz;
	protected double channelSpacing = Constants.CHANNEL_SPACING_50GHz;
	protected double bandGuard = Constants.BANDGUARD_BW_NONE;
	protected double slotBw = Constants.SLOT_SIZE_BW_12_5GHz;
	protected ModulationFormat modulationFormat = ModulationFormat.MF_DP8QAM;
	protected ArrayList<OpticalSuperChannel> spaceSC_List;
	protected boolean isFewModeFiber;
	protected boolean useDataBase = false;
	protected SwitchingType switchingType;
	protected OpticalSuperChannel fixedSuperChannel;
	protected boolean useBlockingProbabilityThreshold = false;
	protected Results results;
	private boolean useIncSpefJos = false;
	protected double throughputRequired;
	public static FileHandler fileHandler ;
	protected boolean handleConsole;
	protected double growthRate;
	protected double valueOfSpaceActivation = 1.0;
	protected double valueOfEDFA = 1.0;
	protected double valueOfTransceiver = 1.0;
	protected double spanLength = 100;
	protected double lossNode = 14; //dB
	protected double osnrThreshold = 14; //[dB]
	protected int thresholdNumDimensions;
	protected Event.Type eventType;

	public Simulator()  {
		formatter = (DecimalFormat) DecimalFormat.getInstance(new Locale("en","US"));
		formatter.applyPattern("###0.000");
		matrixInputNetworkLoadData = new LinkedHashMap<Integer,ArrayList<Integer>>() ;
		matrixAverageInputNetworkLoad = new LinkedHashMap<Integer,Double>();
		matrixProbabilities = new LinkedHashMap<Integer, Double>();
		matrixArrivals = new HashMap<Integer, Double>();
		matrixThroughput = new HashMap<Integer, Double>();
		results = new Results();

	}

	public void init() throws ExcecaoGrafo, SecurityException, IOException {
//		fileHandler = new FileHandler(directory+"simulation.log");
		logger.setUseParentHandlers(handleConsole);
		logger.addHandler(fileHandler);
		StringBuilder builder = new StringBuilder();

		String startTimeString = calendar.get(Calendar.DAY_OF_MONTH)+"."+
				calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT,Locale.getDefault())+"-"+
				calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND);
		start = System.currentTimeMillis();
		startTemp = start;
		builder.append("Simulation started at ").append(startTimeString).append(System.lineSeparator());

		builder.append("Simulation Setup:").append(System.lineSeparator());
		builder.append("Data Directory: " + directory).append(System.lineSeparator());
		builder.append("RSSA heuristic policy: " + algorithm).append(System.lineSeparator());
		builder.append("Switch Type: " + this.switchingType).append(System.lineSeparator());
		builder.append("Arrival Process: " + (isIncremental? "INCREMENTAL" : "POISSON")).append(System.lineSeparator());
		builder.append("Network topology: " + topology).append(System.lineSeparator());
		builder.append("Numbef of K shortest paths: " + numKShortestPaths).append(System.lineSeparator());
		builder.append("number of requests in total: " + requestLimit).append(System.lineSeparator());
		builder.append("Total spectrum slots per fiber: " + spectralSlots).append(System.lineSeparator());
		builder.append("Total of spatial dimensions: " + spatialDimension).append(System.lineSeparator());
		builder.append("Shortest slots per Super Channel: " + numCarrierSlots).append(System.lineSeparator());
		builder.append("SIGNAL BANDWIDTH: ").append(signalBw).append(" GHz").append(System.lineSeparator());
		builder.append("CHANNEL SPACING: ").append(channelSpacing).append(" GHz").append(System.lineSeparator());
		builder.append("BAND GUARD WIDTH: ").append(bandGuard).append(" GHz").append(System.lineSeparator());
		builder.append("SLOT WIDTH: ").append(slotBw).append(" GHz").append(System.lineSeparator());
		builder.append("MODULATION FORMAT: ").append(modulationFormat).append(System.lineSeparator());

		builder.append("Seed: " + seed).append(System.lineSeparator());
		builder.append("Mean holding time: " + holdingTime ).append(System.lineSeparator());
		builder.append("initial load: " + initialLoad ).append(System.lineSeparator());
		builder.append("end load: " + endLoad).append(System.lineSeparator());
		builder.append("Load incremental factor: " + incrementalLoad).append(System.lineSeparator());
		builder.append("Use fixed bandwidth: " + isFixedBandwidth).append(System.lineSeparator());
		builder.append("Debug: " + debug).append(System.lineSeparator());
		builder.append("Fiber Type: " + (isFewModeFiber? "Few Mode Fiber" : "Multicore Fiber")).append(System.lineSeparator());


		//gets the graph
		graph = (Grafo) NetworkTopology.getGraph(NetworkTopology.valueOf(topology));
		if (graph == null) {
			throw new ExcecaoGrafo("The value of topology: '" + topology + "' returned null!");
		}
		//sets the total input network load
		totalInputNetworkLoad = spectralSlots*spatialDimension*graph.getEnlaces().tamanho()/2;
		setTotalInputLoad(totalInputNetworkLoad);

		//creates the spatial super channel classes list
		createSpaceClasses();

		//calculates the mean slot size
		ArrayList<Integer> slotsSizeList = new ArrayList<>();
		for (OpticalSuperChannel osc : spaceSC_List) {
			slotsSizeList.add((int)osc.getNumSlots());
		}
		meanSlotSize = mean( slotsSizeList );;

		//calculates the mean distance size hop by hop
		meanDistanceSize = getAverageHopsNumberPerPath(graph);

		builder.append("Total Input Network Load: " + totalInputNetworkLoad).append(System.lineSeparator());
		builder.append("Mean Slot Size: " + meanSlotSize).append(System.lineSeparator());
		builder.append("Mean Distance Size: " + meanDistanceSize).append(System.lineSeparator());

		logger.info(builder.toString());

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
//			builder.append(System.lineSeparator());
			builder.append("\n");
		}
		return builder.toString();
	}


	/**
	 * Creates the number of spatial classes of super channels and put it in
	 * a list of spatial super channels, which depends of the number of dimensions, the signalBw, channelSpacing,
	 * bandGuard, slotBw and the modulationFormat that were set up.
	 */
	public void createSpaceClasses(){
	//add the number of classe demand in a list of superchannel
		spaceSC_List = new ArrayList<OpticalSuperChannel>();
		for ( int i = 1 ; i < this.spatialDimension+1 ; i++ ) {
			spaceSC_List.add(new OpticalSuperChannel( i , this.signalBw, this.channelSpacing, this.bandGuard,
					this.slotBw, this.modulationFormat));
		}


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

	/**
	 * Executes the sum for the numbers in a list
	 * @param numbers a list of numbers
	 * @return
	 */
	public int sum (List<? extends Number> numbers){
		if (numbers.size() > 0) {
			int sum = 0;

			for ( int i = 0 ; i < numbers.size() ; i++) {
				if (numbers.get(i) instanceof Double ) {
					sum += numbers.get(i).intValue();
				} else {
					sum += (int) numbers.get(i);
				}
			}
			return sum;
		}
		return 0;
	}

	/**
	 * Returns the Average Node Path Length in the graph
	 * @param graph
	 * @return
	 * @throws ExcecaoGrafo
	 */
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


	/**
	 * @return the directory
	 */
	public String getDirectory() {
		return directory;
	}

	/**
	 * @param directory the directory to set
	 */
	@Override
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
	@Override
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
		logger = log;
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
	@Override
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
	@Override
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
	@Override
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
	@Override
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
	@Override
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
	@Override
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
	@Override
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
	@Override
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
	@Override
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
	@Override
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
	@Override
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
	 * @return the numCarrierSlots
	 */
	public int getNumCarrierSlots() {
		return numCarrierSlots;
	}

	/**
	 * @param numCarrierSlots the numCarrierSlots to set
	 */
	@Override
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
	@Override
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
	@Override
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
	@Override
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
	@Override
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
	@Override
	public void setDebug(boolean debug) {
		this.debug = debug;
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
	@Override
	public void setSteadyState(int steadyState) {
		this.steadyState = steadyState;
	}








	/**
	 * @param isFewModeFiber
	 */
	@Override
	public void setFewModeFiber(boolean isFewModeFiber) {
		this.isFewModeFiber = isFewModeFiber;

	}

	/**
	 * @return the switchingType
	 */
	@Override
	public SwitchingType getSwitchingType() {
		return switchingType;
	}

	/**
	 * @param switchingType the switchingType to set
	 */
	@Override
	public void setSwitchingType(SwitchingType switchingType) {
		this.switchingType = switchingType;
	}

	/**
	 * @return the signalBw
	 */
	@Override
	public double getSignalBw() {
		return signalBw;
	}

	/**
	 * @param signalBw the signalBw to set
	 */
	@Override
	public void setSignalBw(double signalBw) {
		this.signalBw = signalBw;
	}

	/**
	 * @return the channelSpacing
	 */
	@Override
	public double getChannelSpacing() {
		return channelSpacing;
	}

	/**
	 * @param channelSpacing the channelSpacing to set
	 */
	@Override
	public void setChannelSpacing(double channelSpacing) {
		this.channelSpacing = channelSpacing;
	}

	/**
	 * @return the bandGuard
	 */
	@Override
	public double getBandGuard() {
		return bandGuard;
	}

	/**
	 * @param bandGuard the bandGuard to set
	 */
	@Override
	public void setBandGuard(double bandGuard) {
		this.bandGuard = bandGuard;
	}

	/**
	 * @return the slotBw
	 */
	@Override
	public double getSlotBw() {
		return slotBw;
	}

	/**
	 * @param slotBw the slotBw to set
	 */
	@Override
	public void setSlotBw(double slotBw) {
		this.slotBw = slotBw;
	}

	/**
	 * @return the modulationFormat
	 */
	@Override
	public ModulationFormat getModulationFormat() {
		return modulationFormat;
	}

	/**
	 * @param modulationFormat the modulationFormat to set
	 */
	@Override
	public void setModulationFormat(ModulationFormat modulationFormat) {
		this.modulationFormat = modulationFormat;
	}

	/**
	 * @return the useDataBase
	 */
	@Override
	public boolean isUseDataBase() {
		return useDataBase;
	}

	/**
	 * @param useDataBase the useDataBase to set
	 */
	@Override
	public void setUseDataBase(boolean useDataBase) {
		this.useDataBase = useDataBase;
	}

	/* (non-Javadoc)
	 * @see simulation.Simulator#setFixedSuperChannel(redeoptica.elastica.rsa.OpticalSuperChannel)
	 */
	@Override
	public void setFixedSuperChannel(OpticalSuperChannel sc) {
		this.fixedSuperChannel = sc;

	}

	/**
	 * @return the useBlockingProbabilityThreshold
	 */
	public boolean isUseBlockingProbabilityThreshold() {
		return useBlockingProbabilityThreshold;
	}

	/**
	 * @param useBlockingProbabilityThreshold the useBlockingProbabilityThreshold to set
	 */
	public void setUseBlockingProbabilityThreshold(
			boolean useBlockingProbabilityThreshold) {
		this.useBlockingProbabilityThreshold = useBlockingProbabilityThreshold;
	}

	/**
	 * Returns the sum of throughput list
	 * @param values the list of the throughput values
	 * @return
	 */
	public double getThroughput( ArrayList<Double> values ) {
		return sum(values);
	}

	/**
	 * @return the isIncremental
	 */
	public boolean isIncremental() {
		return isIncremental;
	}

	/**
	 * @param isIncremental the isIncremental to set
	 */
	public void setIncremental(boolean isIncremental) {
		this.isIncremental = isIncremental;
	}

	/**
	 * @return the results
	 */
	public Results getResults() {
		return results;
	}

	/**
	 * @param results the results to set
	 */
	public void setResults(Results results) {
		this.results = results;
	}

	/**
	 * @return the filenameDataSet
	 */
	public String getFilenameDataSet() {
		return filenameDataSet;
	}

	/**
	 * @param filenameDataSet the filenameDataSet to set
	 */
	public void setFilenameDataSet(String filenameDataSet) {
		this.filenameDataSet = filenameDataSet;
	}

	/**
	 * @return the useIncSpefJos
	 */
	public boolean isUseIncSpefJos() {
		return useIncSpefJos;
	}

	/**
	 * @param useIncSpefJos the useIncSpefJos to set
	 */
	public void setUseIncSpefJos(boolean useIncSpefJos) {
		this.useIncSpefJos = useIncSpefJos;
	}

	/**
	 * Gets the total throughput to be installed in the network ()
	 * @return the throughput
	 */
	public double getThroughput () {
		return throughputRequired;
	}

	/**
	 * Sets the total throughput to be installed in the network
	 *
	 * @param throughput the throughput to set
	 */
	public void setThroughput(double throughput) {
		this.throughputRequired = throughput;
	}

	/**
	 * @return the handleConsole
	 */
	public boolean isHandleConsole() {
		return handleConsole;
	}

	/**
	 * @param handleConsole the handleConsole to set
	 */
	public void setHandleConsole(boolean handleConsole) {
		this.handleConsole = handleConsole;
	}

	/**
	 * @return the growthRate
	 */
	public double getGrowthRate() {
		return growthRate;
	}

	/**
	 * @param growthRate the growthRate to set
	 */
	public void setGrowthRate(double growthRate) {
		this.growthRate = growthRate;
	}


	/**
	 * Returns the value of a space (been a fiber/core/mode) activation. 
	 * @return
	 */
	public double getValueOfSpaceActivation() {
		return valueOfSpaceActivation;
	}

	/**
	 * Set the value of a space (been a fiber/core/mode) activation.
	 * @param valueOfSpaceActivation
	 */
	public void setValueOfSpaceActivation(double valueOfSpaceActivation) {
		this.valueOfSpaceActivation = valueOfSpaceActivation;
	}

	/**
	 * Returns the value of a Erbium Doped Fiber Amplifier (EDFA)
	 * @return
	 */
	public double getValueOfEDFA() {
		return valueOfEDFA;
	}

	/**
	 * Sets the value of a Erbium Doped Fiber Amplifier (EDFA)
	 * @param valueOfEDFA the value of a EDFA
	 */
	public void setValueOfEDFA(double valueOfEDFA) {
		this.valueOfEDFA = valueOfEDFA;
	}

	/**
	 * Returns the value of a transceiver
	 * @return
	 */
	public double getValueOfTransceiver() {
		return valueOfTransceiver;
	}

	/**
	 * Set the value of a transceiver
	 * @param valueOfTransceiver
	 */
	public void setValueOfTransceiver(double valueOfTransceiver) {
		this.valueOfTransceiver = valueOfTransceiver;
	}

	public int getThresholdNumDimensions() {
		return thresholdNumDimensions;
	}

	public void setThresholdNumDimensions(int thresholdNumDimensions) {
		this.thresholdNumDimensions = thresholdNumDimensions;
	}

	public Event.Type getEventType() {
		return eventType;
	}

	public void setEventType(Event.Type eventType) {
		this.eventType = eventType;
	}

	public double getSpanLength() {
		return spanLength;
	}

	public void setSpanLength(double spanLength) {
		this.spanLength = spanLength;
	}

	public double getLossNode() {
		return lossNode;
	}

	public void setLossNode(double lossNode) {
		this.lossNode = lossNode;
	}

	public double getOsnrThreshold() {
		return osnrThreshold;
	}

	public void setOsnrThreshold(double osnrThreshold) {
		this.osnrThreshold = osnrThreshold;
	}

}
