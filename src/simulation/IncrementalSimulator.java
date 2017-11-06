/**
 * Created on 24/05/2016
 */
package simulation;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import distribution.Distribution;
import distribution.Poisson;
import event.Event;
import event.EventGenerator;
import event.Scheduler;
import graph.ExcecaoGrafo;
import opticalnetwork.controlplane.SDMEONController;
import opticalnetwork.elastica.rsa.TrafficRSA;
import opticalnetwork.elastica.rsa.TrafficSDM;
import stats.JobDataBase;

public class IncrementalSimulator extends Simulator{
	
	HashMap<Integer, Double > throughputA = new HashMap<Integer, Double>();
	HashMap<Integer, Integer > dimensionsA = new HashMap<Integer, Integer>();
	HashMap<Integer, Integer > arrivalsA = new HashMap<Integer, Integer>();
	


	public IncrementalSimulator()  {
		super();
		formatter = (DecimalFormat) DecimalFormat.getInstance(new Locale("en","US"));
		formatter.applyPattern("###0.000");
		matrixInputNetworkLoadData = new LinkedHashMap<Integer,ArrayList<Integer>>() ;
		matrixAverageInputNetworkLoad = new LinkedHashMap<Integer,Double>();
		matrixProbabilities = new LinkedHashMap<Integer, Double>();
		matrixArrivals = new HashMap<Integer, Double>();
		matrixThroughput = new HashMap<Integer, Double>();
		logger = Logger.getLogger("IncrementalSimulator");

	}
	
	@SuppressWarnings("unused") 
	public JobDataBase simulate () throws ExcecaoGrafo, Exception {

		init();

		Scheduler escalonador = new Scheduler();
		double arrivals = initialLoad;
		//			holdingTime = 1;
		Distribution distribuicao = new Poisson(seed, initialLoad,holdingTime);
		EventGenerator gerador = new EventGenerator	(distribuicao);

		TrafficRSA traffic = null;
		if (!isFixedBandwidth()) {
			traffic = new TrafficSDM(seed, graph , requestLimit, distribuicao, spaceSC_List);
		} else {
			traffic  = new TrafficSDM(seed, graph , requestLimit, distribuicao, fixedSuperChannel);
		}

		/*if (!isFixedBandwidth()) {
			traffic = new TrafficRSA(seed, graph, requestLimit, distribuicao);
		} else {
			traffic = new TrafficRSA(seed, graph, requestLimit, distribuicao, isFixedBandwidth, bandwidth);
		}*/
		traffic.setRequestType(requestType);
		traffic.setNumKShortestPaths(numKShortestPaths);
		//sets the switching method type: Joint or Independent
		traffic.setSwitchingType(switchingType);

		gerador.setFonte(traffic);
		escalonador.insertGenerator(gerador);
		this.requestLimit = (int) Math.ceil(throughputRequired/fixedSuperChannel.getCapacity());
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

		//The beginning of simulation per year
		double period = 12; //12 months in a year	
		double factor = 0.3; // traffic grows with this factor
		int firstYearArrials=0;
		StringBuilder builder = new StringBuilder();
		/*
		 * deprecated only increase the dimensions
		 * if (this.isUseIncSpefJos()) {
			builder.append("YEAR,ARRIVALS,SLOTS,THROUGHPUT\n");
		} else {
			builder.append("YEAR,ARRIVALS,DIMENSIONS,THROUGHPUT\n");
		}*/
		builder.append("YEAR,ARRIVALS,DIMENSIONS,THROUGHPUT\n");
		Path pathDir = Paths.get(this.getDirectory());
		if (!Files.exists(pathDir)) {
            Files.createDirectories(pathDir);
        }

		File file = new File(this.getDirectory()+"dataset.csv");
		FileWriter writer;

		writer = new FileWriter(file, false);
		PrintWriter printer = new PrintWriter(writer);
		printer.append(builder.toString());
				
		for (double year = initialLoad ; year <= endLoad; year++) {
			simulationID++;
			logger.info("Period (year): " + year);
			double pbTemp = 0.0;
			boolean printed = false;
			arrivals = traffic.getCounter().getValue();
			//				while (evento.getTempo() < period) { //old incremental way
			while (arrivals < requestLimit /*&& pbTemp <= 0*/) {

				Event evento = escalonador.exec();
				escalonador.insertEvent(controle.receberEvento(evento));
				pbTemp = controle.probabilidadeBloqueio();

				arrivals = traffic.getCounter().getValue();
//				double throughputInstalled = this.getThroughput(controle.getThroughputInstalled());
				//old method to save data
//				saveDataArray((int)year, controle.getNumberOfDimensions(), throughputInstalled, (int) arrivals);

			} 
			
			this.throughputRequired = this.throughputRequired*(1+factor);
			double requests = this.throughputRequired/fixedSuperChannel.getCapacity();
			this.requestLimit = requestLimit +(int) Math.ceil(requests);
			traffic.setStop(requestLimit); 
			logger.info(" # established connections: " + controle.getNumCaminhosOpticosEstabelecidos());
			logger.info("# blocked connections: " + controle.getNumCaminhosOpticosBloqueados());
			logger.info("# requests were updated to " + requestLimit);

			//looks for mean arrivals rate: lambda
			double lambda = 1/controle.getTempoMedioEntreChegadas();

			matrixAverageInputNetworkLoad.put(simulationID, controle.getAverageInputNetworkLoad());
			//			matrixArrivals.put(simulationID, lambda);
			matrixArrivals.put(simulationID, arrivals);
			this.matrixThroughput.put(simulationID, this.getThroughput(controle.getThroughputInstalled()));
			/*saves the blocking probability for each load in the loop*/
			this.saveBlockingProbability(simulationID, controle.probabilidadeBloqueio());
			//Calculates and shows the simulation's time
			end = System.currentTimeMillis();
			long partial = end-startTemp;
			startTemp=end;
			String time = String.format("%02d:%02d:%02d",
					TimeUnit.MILLISECONDS.toHours(partial),
					TimeUnit.MILLISECONDS.toMinutes(partial) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(partial)),
					TimeUnit.MILLISECONDS.toSeconds(partial) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(partial)));
			formatter.applyPattern("##0.00000");
			String lambdaS = formatter.format(lambda);
			String s = algorithm+": Partial simulation's time= "+ time + ", lambda = " + lambdaS + ", PB = " + controle.probabilidadeBloqueio();
			logger.info(s);

			period += 12;
			
			StringBuilder b = new StringBuilder();
			double throughput = getThroughput(controle.getThroughputInstalled());
			
			b.append((int)year).append(",").append((int)arrivals).append(",").append(controle.getNumberOfDimensions()).append(",").append((int)throughput).append("\n");
			printer.append(b.toString());
			builder.append(b.toString());
			
//			logger.info("Showing Connection's DataBase. # of records: " + controle.getSystemStateDB().size()+ "\n"+controle.getSystemStateDB());

		}
		logger.info("\n"+builder.toString());
		printer.close();

/*		String s = toStringSimulation();

		out.print(s);
		out.flush();
		out.close();
		System.out.println(s);
*/

		end = System.currentTimeMillis();
		long total = end-start;
		String time = String.format("%02d:%02d:%02d",
				TimeUnit.MILLISECONDS.toHours(total),
				TimeUnit.MILLISECONDS.toMinutes(total) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(total)),
				TimeUnit.MILLISECONDS.toSeconds(total) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(total)));
		String s = "simulation's total time = " + time + "\n#------------# END "+ algorithm +" #------------#\n";
		logger.info(s);
		return null;
		
		/*
		DataSet bp_data = new DataSet();
		bp_data.setHeader(BP_HEADER);
		bp_data.putAll(matrixProbabilities);
		this.results.insert(bp_data);
		DataSet tput_data = new DataSet();
		tput_data.setHeader(TROUGHPUT_HEADER);
		tput_data.putAll(matrixThroughput);
		this.results.insert(tput_data);
		 */

		/* temporary proposys
		 * System.out.println(getStringData());
		File data_file = new File(this.getDirectory()+"datasetMaxValues.csv");
		FileWriter writerData = new FileWriter(data_file, false);
		writerData.append(getStringData());
		writerData.close();
		 */


	}

	public void saveDataArray (Integer _year, Integer _dimensions, Double _throughput, int arrivals){
		/*if (!this.throughputA.containsKey(_year)) {
			this.throughputA.put(_year, _throughput);
		} else if ( this.throughputA.get(_year) > _throughput ){*/
		this.throughputA.put(_year, _throughput);
		/*}*/
		/*if (!this.dimensionsA.containsKey(_year) ){
			this.dimensionsA.put(_year, _dimensions);
		} else if ( this.dimensionsA.get(_year) > _dimensions ){*/
		this.dimensionsA.put(_year, _dimensions);
		this.arrivalsA.put(_year, arrivals);
		/*}*/
	}

	public String getStringData (){
		StringBuilder b = new StringBuilder();
		b.append("YEAR,DIMENSIONS,THROUGHPUT\n");
		for (Integer i : this.throughputA.keySet()) {
			b.append(i).append(",").append(this.dimensionsA.get(i)).append(",").append(this.throughputA.get(i)).append(",").append(this.arrivalsA.get(i)).append("\n");
		}

		return b.toString();

	}

	/**
	 * Creates a String with the data in a CSV format to be printed;
	 *
	 * @return a string with the simulation's date in a CSV format
	 */
	public String toStringSimulation(){
		StringBuilder builder = new StringBuilder();
		String header = "SIM_ID,AVERAGE_INPUT_LOAD, ARRIVALS,BLOKING_PROBABILITY, THROUGHPUT\n";
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
			/*
			formatter.applyPattern("0.00");
			double load = matrixArrivals.get(e.getKey());
			String loadS = formatter.format(load);
			builder.append(loadS).append(",");
			 */

			/*appends the arrival's rate in connections/year*/
			formatter.applyPattern("0.##");
			double arrivals = matrixArrivals.get(e.getKey());
			//			Calendar cal = new GregorianCalendar();
			//			cal.set(Calendar.YEAR, 2016);
			//			int daysInYear = cal.getActualMaximum(Calendar.DAY_OF_YEAR);
			//			long yearInSeconds = TimeUnit.DAYS.toSeconds(daysInYear);
			////			System.out.println("segundos do an0 2016: " + yearInSeconds);
			String loadS = formatter.format(arrivals);
			builder.append(loadS).append(",");

			/*appends the blocking probability*/
			formatter.applyPattern("0.0000000");
			String blockProbability = formatter.format(e.getValue());
			builder.append(blockProbability);
			builder.append(",");

			/*appends the blocking probability*/
			formatter.applyPattern("#.##");
			double throughputD = matrixThroughput.get(e.getKey());
			String throughput = formatter.format(throughputD);
			builder.append(throughput);
			builder.append("\n");
		}
		return builder.toString();
	}


	










}

