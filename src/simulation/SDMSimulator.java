/**
 * Created on 04/02/2016
 */
package simulation;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import distribution.Distribution;
import distribution.Poisson;
import event.Event;
import event.EventGenerator;
import event.Scheduler;
import graph.ExcecaoGrafo;
import opticalnetwork.controlplane.SDMEONController;
import opticalnetwork.elastica.rsa.OpticalSuperChannel;
import opticalnetwork.elastica.rsa.TrafficSDM;
import stats.JobDataBase;

public class SDMSimulator extends Simulator{

	public SDMSimulator()  {
		super();
		formatter = (DecimalFormat) DecimalFormat.getInstance(new Locale("en","US"));
		formatter.applyPattern("###0.000");
		matrixInputNetworkLoadData = new LinkedHashMap<Integer,ArrayList<Integer>>() ;
		matrixAverageInputNetworkLoad = new LinkedHashMap<Integer,Double>();
		matrixProbabilities = new LinkedHashMap<Integer, Double>();
		matrixLoads = new HashMap<Integer, Double>();

	}

	public JobDataBase simulate () throws ExcecaoGrafo, Exception {

		//initiates the previous configuration
		init();
		
		//for ( int load = initialLoad; load <= endLoad ; /*load += incrementalLoad*/){
		for ( int inputload = initialLoad; inputload <= endLoad ; ) {
			simulationID++;
			logger.info("Input Load (%): " + inputload);
			//			Double loadD = 0.01*inputload*totalInputNetworkLoad/(meanDistanceSize*meanSlotSize);
			Double loadD = (double) inputload;
			/*			double load = loadD;*/
			Scheduler escalonador = new Scheduler();
			holdingTime = loadD;
			Distribution distribuicao = new Poisson(loadD,holdingTime);
			EventGenerator gerador = new EventGenerator(distribuicao);
			TrafficSDM traffic = null;
			if (!isFixedBandwidth()) {
				traffic = new TrafficSDM(seed, graph , requestLimit, distribuicao, spaceSC_List);
			} else {
				traffic  = new TrafficSDM(seed, graph , requestLimit, distribuicao, fixedSuperChannel);
			}
			traffic.setFixedBandwidth(isFixedBandwidth);
			traffic.setRequestType(requestType);
			traffic.setNumKShortestPaths(numKShortestPaths);

			//sets the switching method type: Joint or Independent
			traffic.setSwitchingType(switchingType);

			gerador.setFonte(traffic);
			escalonador.insertGenerator(gerador);

			SDMEONController controle = new SDMEONController(graph, this.spectralSlots, this.spatialDimension, this.requestLimit);
			controle.setBidirectional(bidirectional);
			controle.setMinimumSpectralSlotsPerCarrier(getNumCarrierSlots());
			//			controle.setDebug(debug);
			controle.setNewDebug(this.debug);
			controle.setTotalInputNetworkLoad(totalInputNetworkLoad);
			controle.setCounter(traffic.getCounter());
			controle.setSteadyState(steadyState);
			controle.setFewModeFiber(isFewModeFiber);
			controle.setUseDataBase(this.useDataBase);
			controle.setIncremental(isIncremental);
			boolean blockingProbabilityThresholdReached = false;

			do {
				Event evento = escalonador.exec();
				escalonador.insertEvent(controle.receberEvento(evento));

				if (isUseBlockingProbabilityThreshold()) {
					if (controle.probabilidadeBloqueio() > 0 ) {
						blockingProbabilityThresholdReached = true;
					}
				}

				//			} while (traffic.getCounter() < requestLimit || escalonador.getListaEventos().tamanho() > 0);
			} while (traffic.getCounter().getValue() < requestLimit && !blockingProbabilityThresholdReached);

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
			String s = algorithm+": Partial simulation's time= "+ time + ", with load = "+ formatter.format(loadD) + ", lambda = " + lambdaS + ", inputLoad (%)= " + meanInputLoad*100;
			logger.info(s);
			System.out.println("Numero de conexões reutilizadas: " + controle.getCountReusedConnectionsByJoS());
			System.out.println("Numero de conexões estabelecidas: " + controle.getNumCaminhosOpticosEstabelecidos());
			System.out.println("Numero de conexões bloqueadas: " + controle.getNumCaminhosOpticosBloqueados());
			double pb = (double) controle.getNumCaminhosOpticosBloqueados()/requestLimit;
			formatter.applyPattern("0.0000000");
			String blockProbability = formatter.format(pb);
			System.out.println("Pb: " + blockProbability);
			for (OpticalSuperChannel opc : spaceSC_List) {
				System.out.println("Class " + opc.getNumSubChannels() + " ("+ opc.getCapacity() +"Gbps) = " + controle.getClasses().get(opc.getNumSubChannels()));
			}
			//			System.out.println("Classes: " + controle.getClasses());
			inputload += incrementalLoad;

		}
		String s = toStringSimulation();
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
		s = "simulation's total time = " + time + "\n#------------# END "+ algorithm+ "_" + this.switchingType +"_SWITCH #------------#\n";
		logger.info(s);
		return null;


	}


}

