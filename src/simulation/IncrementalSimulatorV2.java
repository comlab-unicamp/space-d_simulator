/**
 * Created on 10/11/2016, based on ofc2017/IncrementalSimulatorOFC2017.java
 */
package simulation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import distribution.Distribution;
import distribution.Poisson;
import event.Event;
import event.EventGenerator;
import event.Scheduler;
import graph.ExcecaoGrafo;
import graph.Grafo;
import opticalnetwork.controlplane.SDMEONController;
import opticalnetwork.elastica.rsa.TrafficRSA;
import opticalnetwork.elastica.rsa.TrafficSDM;
import opticalnetwork.elastica.rsa.RequestRSA.RequestType;
import stats.JobDataBase;
import stats.Record;
import topology.NetworkTopology;

/**
 * @author Alaelson Jatoba
 * @version 2.0
 */
public class IncrementalSimulatorV2 extends Simulator{



	/**
	 * The constructor
	 * 
	 * The Incremental simulation keeps all established connection active in the network
	 */
	public IncrementalSimulatorV2()  {
		super();
		formatter = (DecimalFormat) DecimalFormat.getInstance(new Locale("en","US"));
		formatter.applyPattern("###0.000");
		logger = Logger.getLogger("IncrementalSimulator");
	}

	@Override
	public synchronized void init() throws ExcecaoGrafo, SecurityException, IOException {
		Formatter aFormatter = new SimpleFormatter();
		fileHandler.setFormatter(aFormatter);
		logger.setUseParentHandlers(handleConsole);
		logger.addHandler(fileHandler);
		logger.setLevel(Level.ALL);
		StringBuilder builder = new StringBuilder();

		String startTimeString = calendar.get(Calendar.DAY_OF_MONTH)+"."+
				calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT,Locale.getDefault())+"-"+
				calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND);
		start = System.currentTimeMillis();
		startTemp = start;
		builder.append("Simulation started at ").append(startTimeString).append(System.lineSeparator());
		builder.append(Thread.currentThread().getName());
		builder.append("Simulation Setup:").append(System.lineSeparator());
		builder.append("Data Directory: " + directory).append(System.lineSeparator());
		builder.append("RSSA heuristic policy: " + algorithm).append(System.lineSeparator());
		builder.append("Switch Type: " + this.switchingType).append(System.lineSeparator());
		builder.append("Arrival Process: " + (isIncremental? "INCREMENTAL" : "POISSON")).append(System.lineSeparator());
		builder.append("Network topology: " + topology).append(System.lineSeparator());
		builder.append("Number of K shortest paths: " + numKShortestPaths).append(System.lineSeparator());
		builder.append("Number of initial demands: " + throughputRequired).append(System.lineSeparator());
		this.requestLimit = (int) Math.ceil(throughputRequired/fixedSuperChannel.getCapacity());
		builder.append("Number of initial requests: " + requestLimit).append(System.lineSeparator());
		builder.append("Total spectrum slots per fiber: " + spectralSlots).append(System.lineSeparator());
		builder.append("Total of spatial dimensions: " + spatialDimension).append(System.lineSeparator());
		builder.append("Shortest slots per Super Channel: " + numCarrierSlots).append(System.lineSeparator());
		builder.append("SIGNAL BANDWIDTH: ").append(signalBw).append(" GHz").append(System.lineSeparator());
		builder.append("CHANNEL SPACING: ").append(channelSpacing).append(" GHz").append(System.lineSeparator());
		builder.append("BAND GUARD WIDTH: ").append(bandGuard).append(" GHz").append(System.lineSeparator());
		builder.append("SLOT WIDTH: ").append(slotBw).append(" GHz").append(System.lineSeparator());
		builder.append("MODULATION FORMAT: ").append(modulationFormat).append(System.lineSeparator());

		builder.append("Seed: " + seed).append(System.lineSeparator());
		builder.append("Demand's growth rate: " + growthRate).append(System.lineSeparator());
//		builder.append("Mean holding time: " + holdingTime ).append(System.lineSeparator());
		builder.append("Initial year: " + initialLoad ).append(System.lineSeparator());
		builder.append("End year: " + endLoad).append(System.lineSeparator());
		builder.append("Year incremet: " + incrementalLoad).append(System.lineSeparator());
		builder.append("Use fixed bandwidth: " + isFixedBandwidth).append(System.lineSeparator());
		builder.append("Debug: " + debug).append(System.lineSeparator());
		builder.append("Fiber Type: " + (isFewModeFiber? "Few Mode Fiber" : "Multicore Fiber")).append(System.lineSeparator());


		//gets the graph
		graph = (Grafo) NetworkTopology.getGraph(NetworkTopology.valueOf(topology));
		if (graph == null) {
			throw new ExcecaoGrafo("The value of topology: '" + topology + "' returned null!");
		}
		//sets the total input network load
		totalInputNetworkLoad = spectralSlots*spatialDimension*graph.getEnlaces().tamanho();
		setTotalInputLoad(totalInputNetworkLoad);

		//calculates the mean distance size hop by hop
		meanDistanceSize = getAverageHopsNumberPerPath(graph);
		builder.append("Mean Distance Size: " + meanDistanceSize).append(System.lineSeparator());
		builder.append("Total Input Network Load: " + totalInputNetworkLoad).append(System.lineSeparator());
		logger.info(builder.toString());

	}

	@Override
	public synchronized JobDataBase simulate () throws ExcecaoGrafo, Exception {

		init();

		Scheduler escalonador = new Scheduler();
		double arrivals = initialLoad;
		//			holdingTime = 1;
		Distribution distribuicao = new Poisson(seed, initialLoad,holdingTime);
		EventGenerator gerador = new EventGenerator(distribuicao);

		TrafficRSA traffic = null;
		if (!isFixedBandwidth()) {
			traffic = new TrafficSDM(seed, graph , requestLimit, distribuicao, spaceSC_List);
		} else {
			traffic  = new TrafficSDM(seed, graph , requestLimit, distribuicao, fixedSuperChannel);
		}

		traffic.setEventType(eventType);
		traffic.setRequestType(requestType);
		traffic.setNumKShortestPaths(numKShortestPaths);
		//sets the switching method type: Joint or Independent
		traffic.setSwitchingType(switchingType);

		gerador.setFonte(traffic);
		escalonador.insertGenerator(gerador);
		SDMEONController controle = new SDMEONController(graph, this.spectralSlots, this.spatialDimension, this.requestLimit);
		SDMEONController.log.setUseParentHandlers(handleConsole);
		SDMEONController.log.addHandler(fileHandler);
		controle.setBidirectional(bidirectional);
		controle.setMinimumSpectralSlotsPerCarrier(getNumCarrierSlots());
//		controle.setDebug(debug);
		controle.setNewDebug(debug);
		controle.setTotalInputNetworkLoad(totalInputNetworkLoad);
		controle.setCounter(traffic.getCounter());
		controle.setSteadyState(steadyState);
		controle.setFewModeFiber(isFewModeFiber);
		controle.setUseDataBase(useDataBase);
		controle.setIncremental(isIncremental);
		controle.initParameters(); //TODO
		controle.setValueOfSpaceActivation(valueOfSpaceActivation);
		controle.setValueOfEDFA(valueOfEDFA);
		controle.setValueOfTransceiver(valueOfTransceiver);
		controle.setThresholdNumDimensions(thresholdNumDimensions);
//		spanLength = 100000.0;
		controle.setSpanLength(spanLength);
//		osnrThreshold = 14;
		controle.setOsnrThreshold(osnrThreshold);
//		osnrThreshold = 14;
		controle.setLossNode(lossNode);
		logger.info(controle.printParametets());

		Path pathDir = Paths.get(this.getDirectory());
		if (!Files.exists(pathDir)) {
            Files.createDirectories(pathDir);
        }


		// JOB INSTATIATION
		JobDataBase job = new JobDataBase();
		job.setId(simulationID++);
		double deprecationRate = 0.0;
		//MAIN SIMULATION
		
		for (double year = initialLoad ; year <= endLoad; year++) {

			logger.info(Thread.currentThread().getName()+", Period (year): " + year + ", required throughput: " + throughputRequired);
			arrivals = traffic.getCounter().getValue();

			while (arrivals < requestLimit /*&& pbTemp <= 0*/) {
				Event evento = escalonador.exec();
				escalonador.insertEvent(controle.receberEvento(evento));
				arrivals = traffic.getCounter().getValue();
				
				//just for debug
			
				if(isDebug()){
					int numRegenerators = controle.getNumOfRegenerators();
					int numTransceivers = controle.getNumTransceivers();
					double numEdfas = controle.getNumberOfEDFAs(spanLength);
					int numSpatialChannels = controle.getNumberOfDimensions();
					logger.info("Request id " + arrivals + ", Reg: " + numRegenerators + ", Trans: " + numTransceivers + ", EDFAs: " + numEdfas + ", SCh: " + numSpatialChannels);
				}
				
			}
			
			int numRegenerators = controle.getNumOfRegenerators();
			int numTransceivers = controle.getNumTransceivers();
			double numEdfas = controle.getNumberOfEDFAs(spanLength);
			int numSpatialChannels = controle.getNumberOfDimensions();
			logger.info("Request id " + arrivals + ", Reg: " + numRegenerators + ", Trans: " + numTransceivers + ", EDFAs: " + numEdfas + ", SCh: " + numSpatialChannels);

			//gets the throughput that was installed into the network
			double throughput = getThroughput(controle.getThroughputInstalled());
				

			//saves the record
			//TODO network cost
			Record record = new Record(simulationID, (int)year, requestLimit, controle.getNumberOfDimensions(), throughput, throughputRequired);
			double averageInputNetworkLoad = controle.getAverageInputNetworkLoad();
			setTotalInputLoad(controle.getTotalInputNetworkLoad());
			record.setNetworkUtilization(averageInputNetworkLoad);
		
			double costOfTransceivers = numTransceivers*this.valueOfTransceiver;
			double costOfActivations = numSpatialChannels*this.valueOfSpaceActivation*controle.getTotalNetworkLength()/Math.pow(10, 3);
			double costOfEdfas = numEdfas*this.valueOfEDFA*numSpatialChannels;
			
			double cost = costOfTransceivers+costOfEdfas;
//			cost = cost*(1-deprecationRate);
//			costDeprecation = costDeprecation+0.1;
//			double cost = calculateNetworkCost(requestType, numTransceivers, numSpatialChannels , numEdfas);

			record.setCost(cost);
			record.setCostOfTransceivers(costOfTransceivers);
			record.setCostOfActivations(costOfActivations);
			record.setCostOfEdfas(costOfEdfas);
			job.addRecod(record);
			
			
			//updates the demand accord with factor
			this.throughputRequired = this.throughputRequired*(1+getGrowthRate());
			double requests = this.throughputRequired/fixedSuperChannel.getCapacity();
//			this.requestLimit = requestLimit +(int) Math.ceil(requests);
			this.requestLimit = (int) Math.ceil(requests);
			traffic.setStop(requestLimit);

			//log messages
			logger.info(" # established connections: " + controle.getNumCaminhosOpticosEstabelecidos());
			logger.info("# blocked connections: " + controle.getNumCaminhosOpticosBloqueados());
			logger.info("# requests were updated to " + requestLimit);

			//Calculates and shows the simulation's time
			end = System.currentTimeMillis();
			long partial = end-startTemp;
			startTemp=end;
			String time = String.format("%02d:%02d:%02d",
					TimeUnit.MILLISECONDS.toHours(partial),
					TimeUnit.MILLISECONDS.toMinutes(partial) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(partial)),
					TimeUnit.MILLISECONDS.toSeconds(partial) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(partial)));
			formatter.applyPattern("##0.00000");

			String s = algorithm+": Partial simulation's time= "+ time ;
			logger.info(s);

			//saves the data's set
//			StringBuilder b = new StringBuilder();
//			b.append((int)year).append(",").append((int)arrivals).append(",").append(controle.getNumberOfDimensions()).append(",").append((int)throughput).append("\n");


		}
		String systemDBFileName = "sysdb_simId"+simulationID+"_jobID"+job.getId()+algorithm+".txt";
		File file = new File(getDirectory()+systemDBFileName);
		FileWriter writer = new FileWriter(file, false);
		PrintWriter printer = new PrintWriter(writer);
		printer.append(controle.getSystemStateDB().toString());
		printer.close();

		//Calculates and shows the simulation's time
		end = System.currentTimeMillis();
		long total = end-start;
		String time = String.format("%02d:%02d:%02d",
				TimeUnit.MILLISECONDS.toHours(total),
				TimeUnit.MILLISECONDS.toMinutes(total) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(total)),
				TimeUnit.MILLISECONDS.toSeconds(total) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(total)));
		String s = "simulation's total time = " + time + "\n#------------# END "+ algorithm +" #------------#";
		logger.info(s);

		return job;
	}



	/**
	 * @return the factor
	 */
	public double getFactor() {
		return getGrowthRate();
	}

	/**
	 * @param factor the factor to set
	 */
	public void setFactor(double factor) {
		this.setGrowthRate(factor);
	}


	public void resetSimulationID () {
		simulationID = 0;
	}



	/**
	 * TODO calculate the network cost
	 * calculates the network cost regarding the switching parameter. 
	 * use RequestType.SPAF for Wavelength Switching or RequestType.SPEF for Statial Switching 
	 * @param switching a String. 
	 */
	public double calculateNetworkCost (RequestType type, int numTransceivers, int numberOfSpatialChannels, double numberOfEDFAs) {
		//TODO
		double cost = 0.0;
//		double nodeCost = 0.0;
//		for (No no : getGrafo().getNos().valores()) {
//			no.countDegree();
//			if (type.equals(RequestType.SPAF)){ // for WS
//				nodeCost += no.getDegree();
//			} else if (type.equals(RequestType.SPEF)){ // for SS
//				nodeCost += 1;
//			}
//		}
//		cost += nodeCost;
		
		double costOfTransceivers = numTransceivers*this.valueOfTransceiver;
		double costOfActivations = numberOfSpatialChannels*this.valueOfSpaceActivation*this.graph.getEnlaces().tamanho();
		double costOfEdfas = numberOfEDFAs*this.valueOfEDFA;
		
		cost = costOfTransceivers+costOfActivations+costOfEdfas;
		return cost;
	}







}

