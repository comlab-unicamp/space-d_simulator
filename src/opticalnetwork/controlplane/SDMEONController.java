package opticalnetwork.controlplane;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.logging.Logger;

import algorithm.AlgorithmType;
import algorithm.Dijkstra;
import algorithm.KShortestPathList;
import algorithm.YenTopKShortestBasedAU;
import event.Event;
import event.EventList;
import graph.AbstractGrafo;
import graph.Caminho;
import graph.Enlace;
import graph.ExcecaoGrafo;
import graph.Grafo;
import graph.No;
import opticalnetwork.LinkStateTable;
import opticalnetwork.NodeArchitecture;
import opticalnetwork.SystemState;
import opticalnetwork.SystemStateEntry;
import opticalnetwork.elastica.rsa.RequestException;
import opticalnetwork.elastica.rsa.RequestRSA;
import opticalnetwork.elastica.rsa.RequestRSA.RequestType;
import opticalnetwork.elastica.rssa.AllocationException;
import opticalnetwork.elastica.rssa.ResourceAllocation;
import opticalnetwork.elastica.rssa.SwitchingType;
import opticalnetwork.osnr.OSNR_Manager;
import opticalnetwork.osnr.OSNR_Node;
import opticalnetwork.osnr.OSNR_Parameters;
import opticalnetwork.osnr.OSNR_Span;
import opticalnetwork.osnr.OSNR.Type;
import policy.SpefAllocationPolicy;
import util.Constants;
import util.Converters;
import util.Counter;

/**
 * Created in 01/06/2016
 * Version updated in 08/10/2016
 * New feature network cost add in 10/11/2016 
 * By @author Alaelson Jatoba
 * @version 2.0 
 */
public class SDMEONController extends Controle implements Serializable{

	/**
	 *
	 */
	public static Logger log = Logger.getLogger("SDMEONController");
	DecimalFormat formatter = (DecimalFormat) DecimalFormat.getInstance();
	private static final long serialVersionUID = 1L;
	private int numberOfFiberPerCable = 1;
	private int numberOfDimensions = 1;

	private ArrayList<Integer> inputNetworkLoadDataList;

	private double totalInputNetworkLoad;
	private double averageInputNetworkLoad;
	private double networkCost;
	private double valueOfSpaceActivation = 1.0;
	private double valueOfEDFA = 1.0;
	private double valueOfTransceiver = 1.0;
	
	
	private Counter simCounter;
	private SystemState systemStateDB;
	private SystemState systemStateDB_WDM;
	private SystemState systemStateDB_SDM;
	private int countReusedConnectionsByJoS = 0;
	private HashMap<Integer,Integer> classes;
	private HashMap<String, Integer> pairSourceDestinationDistribution;

	private boolean fewModeFiber = false;
	private boolean isNewDebug = false;
	private boolean useDataBase = false;
	private boolean hasOverlayEverBeenUsed = false;
	
	private int numTransceivers = 0;
	private int numOfRegenerators = 0;
	private int numWSS_RouteSelects = 0;
	private int numWSS_BroadcastSelects = 0;
	private int numOpticalCrossConnects = 0;
	
	private double spanLength = 100*Math.pow(10, 3); // [m]
	private double numberOfChannels = 157;
	private double bandwidthNoise = 12.5*Math.pow(10,9); // 12.5 Ghz by default
	private double symbolRate = 12.5*Math.pow(10,9);
	private double powerGaindBm = 0.0; //[dBm]
	private double wavelength = 1550*Math.pow(10,-9);
	private double amplifierNoiseFigure = 6 ; //[dB]
	private double alpha = 0.22;
	private double beta2 = 21*Math.pow(10, -27);
	private double gama = 1.3*Math.pow(10, -3);
	private double lossNode = 14;
	private double osnrThreshold = 14; //[dB]
	private OSNR_Parameters paramOSNR = new OSNR_Parameters();
	private NodeArchitecture nodeArch;
	


	/**
	 * The Constructor
	 * @param graph the network topology
	 * @param numSpectrumSlots the number of spectrum slots.
	 * @param numberOfDimensions the number of dimensions 
	 * @param requestLimit the limit of requests in the simulation.
	 *
	 * */
	public SDMEONController(Grafo grafo, int numSpectralSlots, int numberOfDimensions, int requestLimit){
		super(numSpectralSlots, requestLimit,grafo);

		this.numberOfDimensions = numberOfDimensions;
		this.overlaySDMGraph.setNumberOfDimensions(numberOfDimensions);
		this.overlayWDMGraph.setNumberOfDimensions(numberOfDimensions);
		iniciaTablelaDeEstados();
		startStateTable(); //for multi-dimensinonal links
		startStateTable(this.overlaySDMGraph); // initiates the overlay SDM graph
		inputNetworkLoadDataList = new ArrayList<Integer>() ;
		this.systemStateDB = new SystemState(numSpectralSlots);

		this.classes = new HashMap<Integer,Integer>();
		this.pairSourceDestinationDistribution = new HashMap<String,Integer>();
		createAllPairSourceDestinations();
		for (int i = 1 ; i <= numberOfDimensions ; i++) {	
			classes.put(i, (new Integer(0)));
		}
		

		this.systemStateDB_WDM = systemStateDB;
		this.systemStateDB_WDM.setIdGraph(overlayWDMGraph.getId());
		this.systemStateDB_SDM = new SystemState(numSpectralSlots);
		this.systemStateDB_SDM.setIdGraph(overlaySDMGraph.getId());


	}

	/**
	 * The Constructor
	 * @param graph the network
	 * @param numLambdas the number of spectrum slots
	 * @param slotBitRate the lightpath's bitrate
	 * @param limiteDeRequisicoes the limit of requests in the simulation
	 * @param numberOfFiberPerCable the number of fibers per cable
	 * @param numberOfModes the number of modes per fiber
	 * @param alg the type of algorithm to choose which link will be installed
	 * */
	public SDMEONController(Grafo grafo, int numLambdas, int slotBitRate, int limiteDeRequisicoes,int numberOfFiberPerCable,
			int numberOfModes, AlgorithmType alg ){
		super(numLambdas, limiteDeRequisicoes);
		setGrafo(grafo);
		this.numberOfFiberPerCable = numberOfFiberPerCable;
		this.numberOfDimensions = numberOfModes;
		this.systemStateDB = new SystemState(numLambdas);
		log.setUseParentHandlers(false);
	}

	/**
	 * Returns the <code>true</code> if there a sequence of free slots with the amount specified.
	 * @param amount the amount of free slots in sequence
	 * @param booleanArray a array of booleans representing the spetrum.
	 * */
	public boolean hasSlotsAvailable (int amount, Boolean[] booleanArray) {
		if (getPosition(amount, booleanArray) == -1) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the first position if the boolean array contains a sequence of <code>true</code>.
	 * The numberOfTrue represent a sequence of free slots in the spectrum on an optical network.
	 * The booleanArray is the spectrum.
	 * @param numberOfSlots the number of <code>true</code> in sequence in the spectrum
	 * @param booleanArray a array of booleans
	 * */
	public int getPosition (int numberOfSlots, Boolean[] booleanArray) {
		Boolean[] mask = getMascara();

		/* get link state mask*/
		for (int i = 0 ; i < mask.length ; i++){
			mask[i] = mask[i] && booleanArray[i];
		}

		int seq = 0;
		int position = -1;
		/* gets the first slot with state "false" */
		for(int i = 0 ; i < mask.length ; i++){
			if (seq >= numberOfSlots) {
				break;
			}

			if(mask[i]){
				seq++;
				if(position == -1) {
					position = i;
				}
			} else {
				if (seq < numberOfSlots){
					seq = 0;
					position = -1;
				} 
			}

		}
		if (seq < numberOfSlots){
			seq = 0;
			position = -1;
		}

		return position;
	}



	/**
	 * Used to create a boolean array mask with specified size
	 * @param size the boolean array mask size
	 * @return
	 */
	public Boolean[] createMaskArray (int size) {
		Boolean[] maskArray = new Boolean[size];
		for(int i = 0 ; i < size ; i++){
			maskArray[i] = true;
		}
		return maskArray;
	}


	/**
	 * Returns the carrier spectrum mask for each dimension in each link into the path corresponding.
	 * @param position the position slot where the carrier begins 
	 * @param path the path between two nodes
	 * @return the state table's mask
	 */
	public HashMap<Integer, Boolean[]> getMaskPerCarrier (int carrierPosition, Caminho path) {
		//Integer is the dimension and Boolean[] is the spectrum
		HashMap<Integer, Boolean[]> maskCarrier = new HashMap<>();
		LinkStateTable maskTable = new LinkStateTable();
		maskTable.setStateTable(maskCarrier);

		for (int i = 0 ; i < numberOfDimensions ; i++ ) {
			maskCarrier.put(i, createMaskArray (this.getMinimumSpectralSlotsPerCarrier()) );
		}
		/*Gets the state table of the each link in the path */
		for(Iterator<Enlace> it = path.getEnlaces().valores().iterator() ; it.hasNext() ;){
			Enlace edge = it.next();//the edge in the path.
			//the link is the same edge in the graph, but could be a different object.
			Enlace link = getGrafo().getEnlace(edge.getId());
			Enlace linkReverse = null;
			Boolean[] linkSpectralArray = null;
			Boolean[] reverseLinkSpectralArray = null;
			if (this.isBidirectional()) {
				No left = link.getNoEsquerda();
				No right = link.getNoDireita();
				linkReverse = getGrafo().getEnlace(right, left);
			}

			//tracks slot by slot in each dimension
			for (int i = 0 ; i < this.numberOfDimensions ; i++ ) {
				Boolean[] maskSlotsAvailable = maskCarrier.get(i);
				linkSpectralArray = link.getStateSpectralArray(i);
				if (this.isBidirectional()) {
					reverseLinkSpectralArray = linkReverse.getStateSpectralArray(i);
				}
				for (int k = 0 ; k < maskSlotsAvailable.length ; k++){
					maskSlotsAvailable[k] = maskSlotsAvailable[k] && linkSpectralArray[carrierPosition+k];
					if (this.isBidirectional()) {
						maskSlotsAvailable[k] = maskSlotsAvailable[k] && reverseLinkSpectralArray[carrierPosition+k];
					}

				}
			}
		}
		return maskCarrier;
	}


	/**
	 * Returns a mask with the spectrum slot's states in each dimension in each link on the path;
	 * @param path the path between two nodes
	 * @param isJointSwitching to be used for the joint switching scheme
	 * @return the state table's mask
	 * @throws ExcecaoGrafo 
	 */
	public HashMap<Integer, Boolean[]> getMaskStateTable (Caminho path) throws ExcecaoGrafo {
		HashMap<Integer, Boolean[]> maskStateTable = new HashMap<>();
		for (int i = 0 ; i < this.getGrafo().getNumberOfDimensions() ; i++ ) {
			maskStateTable.put(i, getMascara());
		}

		/*Gets the state table of the each link on the path */
		for(Iterator<Enlace> it = path.getEnlaces().valores().iterator() ; it.hasNext() ;){
			Enlace edge = it.next();//the edge in the path.
			//the link is the same edge in the graph, but could be a different object.
			Enlace link = getGrafo().getEnlace(edge.getId());
			Enlace linkReverse = null;
			Boolean[] linkSpectralArray = null;
			Boolean[] reverseLinkSpectralArray = null;
			if (this.isBidirectional()) {
				No left = link.getNoEsquerda();
				No right = link.getNoDireita();
				linkReverse = getGrafo().getEnlace(right, left);
			}

			/*Save the state of the spectrum into LinkState Table of the link*/
			for (int i = 0 ; i < this.getGrafo().getNumberOfDimensions() ; i++ ) {
				Boolean[] spectrumInDimension = maskStateTable.get(i);
				linkSpectralArray = link.getStateSpectralArray(i);
				if (linkSpectralArray == null ) {
					throw new ExcecaoGrafo("There is no dimension " + i + " in the graph: "+ getGrafo().getName()+ " = " + getGrafo().getNetwork().getName());
				}
				if (this.isBidirectional()) {
					reverseLinkSpectralArray = linkReverse.getStateSpectralArray(i);
				}
				for (int k = 0 ; k < spectrumInDimension.length ; k++){
					spectrumInDimension[k] = spectrumInDimension[k] && linkSpectralArray[k];
					if (this.isBidirectional()) {
						spectrumInDimension[k] = spectrumInDimension[k] && reverseLinkSpectralArray[k];
					}

				}
			}
		}

		return maskStateTable;

	}



	/**
	 * Returns the K disjoint shortest paths
	 *
	 * @param graph the graph
	 * @param source the source node
	 * @param destination the destination node
	 * @param k the number of paths requested
	 * @return
	 * @throws ExcecaoGrafo
	 */
	public KShortestPathList getKshortestPathList (AbstractGrafo graph, No source, No destination, int k) throws ExcecaoGrafo {
		KShortestPathList paths = new KShortestPathList();

		for (int i = 0 ; i < k ; i++) {
			Caminho path = Dijkstra.getMenorCaminho(graph, source, destination);
			if ( path.getDistancia() > 0 && path.getDistancia() < Double.MAX_VALUE) {
				paths.add(path);
				path.disableLinks();
			}
		}
		return paths;
	}

	/**
	 * Handles the Flex SDM Requests
	 *
	 * @param event the event from scheduler
	 * @param request the request to handle
	 * @throws ExcecaoGrafo
	 * @throws AllocationException 
	 */
	public synchronized void flexSDMRequestHandle(Event event, RequestRSA request) throws ExcecaoGrafo, AllocationException {
		//		int numberOfSpectralSlots = request.getBandwidth().getSpectralSlots();
		int numberOfSpatialSlots = request.getBandwidth().getSpatialSlots();
		int numCarrierSlots = getMinimumSpectralSlotsPerCarrier();
		//Gets the source node
		No source = request.getSource();
		//Gets the destination node ID
		No destination = request.getDestination();
		//Gets the number of shortestPath
		int numK = request.getKshortestPaths();
		int firstSlot = -1;
		boolean success = false;

		ResourceAllocation resourceAllocation = null;
		//Gets a list with the shortest paths between the source and destination nodes
		KShortestPathList paths = getKshortestPathList( source, destination, numK);
		if (paths.size()==0) {
			throw new ExcecaoGrafo ("There is no enabled paths in modifiable graph!");
		}
		while (paths.size() > 0) {
			//Get the next shortest path
			Caminho path = paths.poll();
			//			double distance = path.getDistancia();

			resourceAllocation = installSpaceFirst(numCarrierSlots,numberOfSpatialSlots, path, request, false);
			firstSlot = resourceAllocation.getFirstSlot() ;

			if ( firstSlot > -1) {
				//Stores the resource allocation in the request
				request.setResource(resourceAllocation);
				//Sets the success flag to true
				success = true;
				//Increments the number of success
				incrementaCaminhosEstabelecidos();
				//Updates the holding time to close the connection
				event.setTempo(event.getTime()+request.getHoldingTime());
				//Inserts the event into schedule's list
				listaEventos.insert(event);
				//Stores the request ID and the established path
				getRequestPathTable().put(request.getId(), path);
				//Sets the request as close type
				request.setType(RequestType.CLOSE_SPACE_FIRST);

				//Counts the amount of spectrum occupation in the network
				if(isBidirectional()){
					this.inputNetworkLoad += (2*numberOfSpatialSlots*path.getEnlaces().tamanho());
				} else {
					this.inputNetworkLoad += (numberOfSpatialSlots*path.getEnlaces().tamanho());
				}

				break;
			} 
		}

		if (!success) {
			if (isDebug()) {
				System.out.println("R_ID: "+request.getId()+", CONNECTION WAS BLOCKED");
			}
			if (simCounter.getValue() > this.steadyState) {
				incrementaBloqueios();
			}

		} else {
			if (isDebug()) {
				System.out.println("R_ID: "+request.getId()+", CONNECTION WAS SUCCESSFULLY INSTALLED");
			}
		}

		if (isDebug()) {
			printStatistics(request, resourceAllocation);
		}
		//adds in inputNetworkLoad into the list
		this.getInputNetworkLoadDataList().add(getInputNetworkLoad());
	}

	/**
	 * Handles the Space First Request
	 *
	 * @param event the event from scheduler
	 * @param request the request to handle
	 * @throws ExcecaoGrafo
	 * @throws AllocationException 
	 */
	public synchronized void spaceFirstRequestHandle(Event event, RequestRSA request) throws ExcecaoGrafo, AllocationException {

		int numberOfSpatialSlots = 0;
		if (request.getBandwidth() != null ) {
			numberOfSpatialSlots = request.getBandwidth().getSpatialSlots();
		} else if (request.getOpticalSuperChannel() != null ){
			numberOfSpatialSlots = (int)request.getOpticalSuperChannel().getNumSlots();

		}
		int numCarrierSlots = numberOfSpatialSlots/request.getOpticalSuperChannel().getNumSubChannels();

		//Gets the source as No object in the graph 
		No source =   request.getSource();
		//Gets the destination as No object in the graph 
		No destination =  request.getDestination();
		//Gets the number of shortestPath
		int numK = request.getKshortestPaths();
		int firstSlot = -1;
		boolean success = false;
		ResourceAllocation resourceAllocation = null;
		//Gets a list with the shortest paths between the source and destination nodes
		KShortestPathList paths = getKshortestPathList( source, destination, numK);
		if (paths.size()==0) {
			throw new ExcecaoGrafo ("There is no enabled paths in modifiable graph!");
		}
		while (paths.size() > 0) {
			//Get the next shortest path
			Caminho path = paths.poll();
			resourceAllocation = installSpaceFirst(numCarrierSlots,numberOfSpatialSlots, path, request, false);
			firstSlot = resourceAllocation.getFirstSlot() ;

			if ( firstSlot > -1) {
				//				String key = request.getKey();
				saveAndScheduleToClose(request, path, resourceAllocation, event);

				//Sets the success flag to true
				success = true;

				break;
			} 
		}

		if (!success) {
			if (isDebug()) {
				System.out.println("R_ID: "+request.getId()+", CONNECTION WAS BLOCKED");
			}
			if (simCounter.getValue() > this.steadyState) {
				incrementaBloqueios();
			}

		} else {
			if (isDebug()) {
				System.out.println("R_ID: "+request.getId()+", CONNECTION WAS SUCCESSFULLY INSTALLED");
			}
		}

		if (isDebug()) {
			printStatistics(request, resourceAllocation);
		}
		//adds in inputNetworkLoad into the list
		this.getInputNetworkLoadDataList().add(getInputNetworkLoad());
	}

	
	/**
	 * Handles the Spectrum First Request
	 *
	 * @param event the event from scheduler
	 * @param request the request to handle
	 * @throws ExcecaoGrafo
	 * @throws AllocationException throws an error exception if the slots are already allocated
	 */
	public void spectrumFirstRequestHandle(Event event, RequestRSA request) throws ExcecaoGrafo, AllocationException {
		//Gets the number of spectra slots required
		//		int numberOfSpectraSlots = request.getBandwidth().getSpectralSlots();
		int numberOfSpectraSlots = 0;
		if (request.getBandwidth() != null ) {
			numberOfSpectraSlots = request.getBandwidth().getSpatialSlots();
		} else if (request.getOpticalSuperChannel() != null ){
			numberOfSpectraSlots = (int)request.getOpticalSuperChannel().getNumSlots();
			int i = (int)request.getOpticalSuperChannel().getNumSubChannels();
			Integer a = this.classes.get(i);
			a+=1;
			classes.put(i, a);
		}
		//Gets the source node ID
		String sourceID = request.getSource().getId();
		//Gets the destination node ID
		String destID = request.getDestination().getId();
		//Gets the source as No object in the graph 
		No source =  getGrafo().getNo(sourceID);
		//Gets the destination as No object in the graph 
		No destination =  getGrafo().getNo(destID);
		//Gets the number of shortestPath
		int numK = request.getKshortestPaths();
		int firstSlot = -1;
		boolean success = false;
		ResourceAllocation resourceAllocation = null;
		//Gets a list with the shortest paths between the source and destination nodes
		KShortestPathList paths = getKshortestPathList( source, destination, numK);
		//
		Caminho path = null;
		if (paths.size()==0) {
			throw new ExcecaoGrafo ("There is no enabled paths in modifiable graph!");
		}
		while (paths.size() > 0) {

			path = paths.poll();
			resourceAllocation = installSpectrumFirst(numberOfSpectraSlots, path, request);
			firstSlot = resourceAllocation.getFirstSlot() ;
			if ( firstSlot > -1) {
				//Stores the resource allocation in the request
				request.setResource(resourceAllocation);
				//Sets the success flag to true
				success = true;
				//Increments the number of success
				incrementaCaminhosEstabelecidos();
				//Updates the holding time to close the connection
				event.setTempo(event.getTime()+request.getHoldingTime());
				//Inserts the event into schedule's list
				listaEventos.insert(event);
				//Stores the request ID and the established path
				getRequestPathTable().put(request.getId(), path);
				//Sets the request as close type
				request.setType(RequestType.CLOSE_SPECTRUM_FIRST);
				//Counts the amount of spectrum occupation in the network
				if(isBidirectional()){
					this.inputNetworkLoad += (2*numberOfSpectraSlots*path.getEnlaces().tamanho());
				} else {
					this.inputNetworkLoad += (numberOfSpectraSlots*path.getEnlaces().tamanho());
				}

				break;
			}
		}


		if (!success) {
			if (isDebug()) {
				System.out.println("R_ID: "+request.getId()+", CONNECTION WAS BLOCKED");

			}
			if (simCounter.getValue() > this.steadyState) {
				incrementaBloqueios();
			}
			//modGraph.enableLinks();
		} else {
			if (isDebug()) {
				System.out.println("R_ID: "+request.getId()+", CONNECTION WAS SUCCESSFULLY INSTALLED");
			}
		}

		if (isDebug()) {
			printStatistics(request, resourceAllocation);
		}
		/*adds in inputNetworkLoad into the list */
		this.getInputNetworkLoadDataList().add(getInputNetworkLoad());
	}

	/**
	 * @param event
	 * @param request
	 * @throws ExcecaoGrafo
	 * @throws AllocationException throws an error exception if the slots are already allocated
	 */
	public void spectrumFirstSlightlyModifiedHandle (Event event, RequestRSA request) throws ExcecaoGrafo, AllocationException {
		//Gets the number of spectra slots required
		int numberOfSpectraSlots = request.getBandwidth().getSpectralSlots();
		//Gets the source node ID
		String sourceID = request.getSource().getId();
		//Gets the destination node ID
		String destID = request.getDestination().getId();
		//Gets the source as No object in the  graph 
		No source =  getGrafo().getNo(sourceID);
		//Gets the destination as No object in the modifiable graph (modGraph)
		No destination =  getGrafo().getNo(destID);
		//Gets the number of shortestPath
		int numK = request.getKshortestPaths();
		int firstSlot = -1;
		boolean success = false;
		ResourceAllocation resourceAllocation = null;
		//Gets a list with the shortest paths between the source and destination nodes
		KShortestPathList paths = getKshortestPathList( source, destination, numK);
		if (paths.size()==0) {
			throw new ExcecaoGrafo ("There is no enabled paths in modifiable graph!");
		}
		while (paths.size() > 0) {
			Caminho path = paths.poll();
			resourceAllocation = installSpectrumFirst(numberOfSpectraSlots, path, request);
			firstSlot = resourceAllocation.getFirstSlot() ;
			if ( firstSlot > -1) {
				//Stores the resource allocation in the request
				request.setResource(resourceAllocation);
				request.setFirstFrequencySlot(firstSlot);
				//Sets the success flag to true
				success = true;
				//Increments the number of success
				incrementaCaminhosEstabelecidos();
				//Updates the holding time to close the connection
				event.setTempo(event.getTime()+request.getHoldingTime());
				//Inserts the event into schedule's list
				listaEventos.insert(event);
				//Stores the request ID and the established path
				getRequestPathTable().put(request.getId(), path);
				//Sets the request as close type
				request.setType(RequestType.CLOSE_REQUEST);
				//Counts the amount of spectrum occupation in the network

				if(isBidirectional()){
					this.inputNetworkLoad += (2*numberOfSpectraSlots*path.getEnlaces().tamanho());
				} else {
					this.inputNetworkLoad += (numberOfSpectraSlots*path.getEnlaces().tamanho());
				}

				break;
			}

		}

		if (!success) {
			if (isDebug()) {
				System.out.println("R_ID: "+request.getId()+", CONNECTION WAS BLOCKED");
			}
			if (simCounter.getValue() > this.steadyState) {
				incrementaBloqueios();
			}

		} else {
			if (isDebug()) {
				System.out.println("R_ID: "+request.getId()+", CONNECTION WAS SUCCESSFULLY INSTALLED");
			}
		}

		if (isDebug()) {
			printStatistics(request, resourceAllocation);
		}

		/*adds in inputNetworkLoad into the list */
		this.getInputNetworkLoadDataList().add(getInputNetworkLoad());
	}

	/**
	 * Returns <code>true</code> if the carrier is occupied for another request
	 * @param maskStateTable a mask table representing all link state to be analyzed 
	 * @param firstPosition the carrier's first slot
	 * @param numCarrierSlots the carrier's number of slots
	 * @return true if the carrier is locked
	 */
	public boolean isCarrierLocked (HashMap<Integer, Boolean[]> maskStateTable, int firstPosition, int numCarrierSlots) throws AllocationException{
		if (firstPosition == -1) { 
			throw new AllocationException("The labmda/carrier or its position in the spectrum can't be " + firstPosition);
		}
		//searches in all dimensions
		for (int i = 0 ; i < maskStateTable.size(); i++) {
			Boolean[] spectrum = maskStateTable.get(i);
			Boolean[] carrierSpectrum = Arrays.copyOfRange(spectrum, firstPosition, firstPosition+numCarrierSlots);
			
//						boolean isCarrierFree = carrierSpectrum[firstPosition];
			boolean isCarrierFree = isAllSlotsFree(carrierSpectrum); //need to review, doesn't checking only in carrier's position 
			if (!isCarrierFree) {
				return true;
			}


		}


		return false;
	}

	/**
	 * Searches if there is at least one spectrum's slot in use.
	 * 
	 * Returns <code>true</code> if the carrier is occupied for another request
	 * @param spectrum	 
	 * @return true if the spectrum slots is locked
	 * @throws AllocationException 
	 */
	/**
	 * @param spectrum
	 * @return
	 * @throws AllocationException
	 */
	public boolean isSpectrumLocked (Boolean[] spectrum) throws AllocationException {
		if (spectrum == null) {
			throw new AllocationException(" Error! Null spectrum parameter");
		}
		//searches in all dimensions

		for (int i = 0 ; i < spectrum.length; i++) {
			boolean isCarrierFree = spectrum[i];
			if (!isCarrierFree) {
				return true;
			}

		}

		return false;
	}

	/**
	 * Uses Java 8 statement to search elements in the array
	 * Searches all elements in a boolean array 
	 * @param array a boolean array representing the spectrum slots
	 * @return true if all slots are free
	 */
	public boolean isAllSlotsFree (Boolean[] array) {
		return Arrays.asList(array).stream().allMatch(val -> val == true);
	}
	
	/**
	 * Uses Java 8 statement to search elements in the array
	 * Searches all elements in a boolean array 
	 * @param array a boolean array representing the spectrum slots
	 * @return true if all slots are busy
	 */
	public boolean isAllSlotsBusy (Boolean[] array) {
		return Arrays.asList(array).stream().allMatch(val -> val == false);
	}

	/**
	 * Save the request installed in SystemStateDB and Schedule to be closed using the request type <code>RequestType.CLOSE_SPACE_FIRST</code>
	 * 
	 * @param request the requestRSA 
	 * @param path the path 
	 * @param resourceAllocation the resources: first slot and dimensions
	 * @param event the event 
	 * @param numUsedSlots number of spectrum and spatial slots needed for the request 
	 * @throws ExcecaoGrafo
	 */
	public void saveAndScheduleToClose(RequestRSA request, Caminho path, ResourceAllocation resourceAllocation, Event event) throws ExcecaoGrafo {
		int numberOfSpatialSlots = 0 ;
		if (request.getBandwidth() != null ) {
			numberOfSpatialSlots = request.getBandwidth().getSpatialSlots();
			this.getThroughputInstalled().add(request.getBandwidth().getBw());
		} else if (request.getOpticalSuperChannel() != null ){
			numberOfSpatialSlots = (int)request.getOpticalSuperChannel().getNumSlots();
			this.getThroughputInstalled().add(request.getOpticalSuperChannel().getCapacity()); //TODO Check if it is right
		}
		//		if (useDataBase) { //deprecated
		//			systemStateDB.addEntry(key, request, path, resourceAllocation);
		//		}
		//Stores the resource allocation in the request
		request.setResource(resourceAllocation);
		//Increments the number of success
		incrementaCaminhosEstabelecidos();

		//Stores the request ID and the established path
		getRequestPathTable().put(request.getId(), path);

		int totalOccupiedSlots = numberOfSpatialSlots*path.getEnlaces().tamanho();
		//Counts the amount of spectrum occupation in the network
		if(isBidirectional()){
			this.inputNetworkLoad += (2*totalOccupiedSlots);
		} else {
			this.inputNetworkLoad += (totalOccupiedSlots);
		}
		//incremental requests
		if (!isIncremental()) {
			//Sets the request as close type
			request.setType(RequestType.CLOSE_SPACE_FIRST);
			//Inserts the event into schedule's list
			listaEventos.insert(event);
			//Updates the holding time to close the connection
			event.setTempo(event.getTime()+request.getHoldingTime());

		}

		int num3R = checkTransparentReach(path, resourceAllocation);
		if (num3R > -1) {
			this.numOfRegenerators += num3R;
			this.numTransceivers = this.numTransceivers + (2*num3R+2); //2 transc. for pair Tx-Rx 
			if (num3R > 0 && isNewDebug()) {
				log.info(resourceAllocation + "; Path: " + path + ", installed regenerators: " + num3R + ", total of regenerators: " + this.numOfRegenerators +", total of Transceivers: " + this.numTransceivers);
			}
		} else {
			if (isNewDebug()){
				log.warning(path.toString()+"\n"+printParametets());
			}
//			System.out.println(printParametets());
			throw new ExcecaoGrafo("Alcance transparente necessário!");
		}
	}
	
	/**
	 * Handles the policies Spaf and Spef with the joint switch scheme
	 * @param event the event to be handled
	 * @return a boolean <code>true</code> if the demand in the event was successfully installed, returned <code>false</code> otherwise.
	 * @throws ExcecaoGrafo Exception for graph's errors
	 * @throws AllocationException Exception for allocation's errors
	 * @throws RequestException Exception for request's errors
	 */
	
	public synchronized boolean handleJointSwitch(Event event) throws ExcecaoGrafo, AllocationException, RequestException {
		//gets the request inside of event
		RequestRSA request = (RequestRSA) event.getConteudo();
		
		int numberOfSpatialSlots = 0;
		int numCarrierSlots = 0;
		if (request.getBandwidth() != null ) {
			numberOfSpatialSlots = request.getBandwidth().getSpatialSlots();
			numCarrierSlots = this.getMinimumSpectralSlotsPerCarrier();
		} else if (request.getOpticalSuperChannel() != null ){
			numberOfSpatialSlots = (int)request.getOpticalSuperChannel().getNumSlots();
			numCarrierSlots = numberOfSpatialSlots/request.getOpticalSuperChannel().getNumSubChannels();
		} else {
			throw new RequestException("The optical superchannel in request " + request + " is null");
		}

		//Gets the source as No object in the graph 
		No source =   request.getSource();
		//Gets the destination as No object in the graph 
		No destination =  request.getDestination();
		//Gets the number of shortestPath

		String key = request.getKey();
		Caminho path = null;

		int firstSlot = -1;
		boolean reusedConnection = false;
		boolean success = false;
		boolean isReusingPath = false;
		
		ResourceAllocation newResource = null;
		if (isNewDebug) {
			System.out.println("\nGraph: " + getGrafo().getName() + ", Network: " + getGrafo().getNetwork().name());
			System.out.println("Request: "+request.getId()+" key: "  + request.getKey());
			System.out.println(request.getOpticalSuperChannel());
		}

		if (request.getSwitchingType()== null ) {
			throw new RequestException("No switching type was found!");
		}
		if( request.getSwitchingType()==SwitchingType.JOINT ) {

			if (useDataBase) {
				boolean isSpef = request.getType() == RequestType.SPEF;
				List<SystemStateEntry> entriesWithKey = systemStateDB.getEntriesByKey(key, isBidirectional(), isSpef);
				if (!entriesWithKey.isEmpty()) {
					int dimension = -1;
					for (SystemStateEntry entry: entriesWithKey) {
						
						path = entry.getPath();						
						//tries to install the request in the empty dimensions of a carrier
						if (request.getType() == RequestType.SPAF) {
							if (entry.getAllocatedResource().getDimensionsAllocated().size() == this.getGrafo().getNumberOfDimensions()) {
								continue; //skips the entry which have all dimensions already in use;
							}
							newResource = installSpafInPosition(entry.getAllocatedResource().getFirstSlot(),numCarrierSlots,numberOfSpatialSlots, path, request);
						} else if (request.getType() == RequestType.SPEF){
							isReusingPath = true;
							SpefAllocationPolicy spef = new SpefAllocationPolicy(this);
							Iterator<Integer> it = entry.getAllocatedResource().getDimensionsAllocated().iterator();
							dimension = it.next();
							newResource = spef.install(request, path, isReusingPath, dimension);
						}
						firstSlot = newResource.getFirstSlot() ;

						if ( firstSlot > -1) {
							//if first slot up to -1 then success installing the request
							//sets the resuedConnection flag to true
							reusedConnection = true;
							// saves and schedule to close the connection
							saveAndScheduleToClose(request, path, newResource, event);
							if (isIncremental()) {
								if (request.getType() == RequestType.SPAF) {
									systemStateDB.updateEntryAdd(entry, newResource.getDimensionsAllocated());
								} else if (request.getType() == RequestType.SPEF){
									int newDimension =  newResource.getDimensionsAllocated().get(0);
									if(dimension != newDimension) { //check if the dimension has changed
										systemStateDB.addEntry(key, request, path, newResource); //add a new entry because the original is full
									} else {
										systemStateDB.updateEntryToAdd(entry, firstSlot); // update the entry in the same dimension
									}
								}
							} else {
								systemStateDB.addEntry(key, request, path, newResource);
							}
							
							//							}
							success = true;
							//count the reused connections using JOINT
							countReusedConnectionsByJoS++;

							//	for debug propose
							if (isNewDebug) {
								System.out.println("\nGraph: " + getGrafo().getName() + ", Network: " + getGrafo().getNetwork().name());
								System.out.println("R_ID: "+request.getId()+", CONNECTION WAS SUCCESSFULLY INSTALLED REUSING RESOURCES");
								System.out.println("Path: " + path);
								System.out.println("Allocated Resources: " + newResource);
								LinkStateTable mask = new LinkStateTable();
								mask.setStateTable(this.getMaskStateTable(path));
								System.out.println(mask) ;
							} // end IF (debug)

							break;
						} // end IF (firstSlot > -1) 

					} //end FOR (entries)

				} // end IF (Entries != 0)

			} // end IF (is useDB)

		} // end IF (is JOINT)

		/*independent switch from this point on*/
		PriorityQueue<Caminho> pathDebug = new PriorityQueue<Caminho>(); 
		ResourceAllocation resourceAllocation = null;
		if (!reusedConnection) {
			
			int numK = request.getKshortestPaths();

			
			//Gets a list with the shortest paths between the source and destination nodes
			this.setkSPF(new YenTopKShortestBasedAU(getGrafo()));
			KShortestPathList paths = getKshortestPathList( source, destination, numK);

			//added only for debug proposes
			pathDebug.addAll(paths.getPaths());

			if (paths.size()==0) {
				throw new ExcecaoGrafo ("There is no enabled paths available between nodes " + key);
			}

			while (paths.size() > 0) {
				//Get the next shortest path
				path = paths.poll();
				if (request.getType() == RequestType.SPAF) {
					resourceAllocation = installSpaceFirst(numCarrierSlots,numberOfSpatialSlots, path, request, true);
				} else if (request.getType() == RequestType.SPEF){
					SpefAllocationPolicy spef = new SpefAllocationPolicy(this);
					resourceAllocation = spef.install(request, path);
				}


				firstSlot = resourceAllocation.getFirstSlot() ;

				if ( firstSlot > -1) {

					if (useDataBase) { 
						this.systemStateDB.addEntry(key, request, path, resourceAllocation);
					}
					saveAndScheduleToClose(request, path, resourceAllocation, event);
				
					//Sets the success flag to true
					success = true;
					break;
				} 

			}
		}

		if (!success) {
			//				System.out.println("\nRequest key: "  + request.getKey());
			//				System.out.println(request.getOpticalSuperChannel());
			if (isDebug()) {
				System.out.println("R_ID: "+request.getId()+", CONNECTION WAS BLOCKED");
			}

			if (simCounter.getValue() > this.steadyState) {
				if (isNewDebug) {
					System.out.println("\nGraph: " + getGrafo().getName() + ", Network: " + getGrafo().getNetwork().name());
					System.out.println("R_ID: "+request.getId()+", CONNECTION WAS BLOCKED");
					System.out.println(this.systemStateDB);
					for (Caminho p : pathDebug) {
						System.out.println("Path: " + p);
						LinkStateTable mask = new LinkStateTable();
						mask.setStateTable(this.getMaskStateTable(p));
						System.out.println(mask) ;
					}
				}
				incrementaBloqueios();
			} 

		} else {
			if (isNewDebug && !reusedConnection) {
				System.out.println("\nGraph: " + getGrafo().getName() + ", Network: " + getGrafo().getNetwork().name());
				System.out.println("R_ID: "+request.getId()+", CONNECTION WAS SUCCESSFULLY INSTALLED");
				System.out.println("Path: " + path);
				System.out.println("Allocated Resources: " + resourceAllocation);
				LinkStateTable mask = new LinkStateTable();
				mask.setStateTable(this.getMaskStateTable(path));
				System.out.println(mask) ;
			}

			if (isDebug()) {
				System.out.println("R_ID: "+request.getId()+", CONNECTION WAS SUCCESSFULLY INSTALLED");
			}
		} 
		if (isDebug()) {
			printStatistics(request, resourceAllocation);
		}
		//adds in inputNetworkLoad into the list
		this.getInputNetworkLoadDataList().add(getInputNetworkLoad());
		
		return success;

	}

	/**
	 * Handles the request using the Space First Policy and Joint Switching scheme
	 *
	 * @param event the event from scheduler
	 * @param request the request to handle
	 * @throws ExcecaoGrafo
	 * @throws AllocationException throws an error exception if the slots are already allocated
	 * @throws RequestException  throws an error exception if the switch type is not properly configured
	 */
	public synchronized boolean handleSpafJointSwitch(Event event, RequestRSA request) throws ExcecaoGrafo, AllocationException, RequestException {

		int numberOfSpatialSlots = 0;
		int numCarrierSlots = 0;
		if (request.getBandwidth() != null ) {
			numberOfSpatialSlots = request.getBandwidth().getSpatialSlots();
			numCarrierSlots = this.getMinimumSpectralSlotsPerCarrier();
		} else if (request.getOpticalSuperChannel() != null ){
			numberOfSpatialSlots = (int)request.getOpticalSuperChannel().getNumSlots();
			numCarrierSlots = numberOfSpatialSlots/request.getOpticalSuperChannel().getNumSubChannels();
			/*int i = (int)request.getOpticalSuperChannel().getNumSubChannels();
			Integer a = this.classes.get(i);
			a+=1;
			classes.put(i, a);
			if (!((i>0)&&(i<=9))){
				System.out.println("Erro");
			}	*/
		} else {
			throw new RequestException("The optical superchannel in request " + request + " is null");
		}


		//Gets the source as No object in the graph 
		No source =   request.getSource();
		//Gets the destination as No object in the graph 
		No destination =  request.getDestination();
		//Gets the number of shortestPath

		String key = request.getKey();
		Caminho path = null;

		int firstSlot = -1;
		boolean reusedConnection = false;
		boolean success = false;
		PriorityQueue<Caminho> pathDebug = new PriorityQueue<Caminho>(); 

		if (request.getSwitchingType() == null) {
			throw new RequestException("The switchtype attribute in request must be set up!");
		}

		/*different switching schemes*/
		switch (request.getSwitchingType()) {
		case JOINT:


			ResourceAllocation newResource = null;
			if (isNewDebug) {
				System.out.println("\nRequest: "+request.getId()+" key: "  + request.getKey());
				System.out.println(request.getOpticalSuperChannel());
			}

			if (useDataBase) {
				boolean isSpef = request.getType() == RequestType.SPEF;
				List<SystemStateEntry> entriesWithKey = systemStateDB.getEntriesByKey(key, isBidirectional(), isSpef);
				if (!entriesWithKey.isEmpty()) {
					/*if (request.getId() > targetDebug) {
				System.out.println("There are  " + entriesWithKey.size()  + "entries in SystemStateDB with Key " + key);
				System.out.println(entriesWithKey);
				}*/
					for (SystemStateEntry entry: entriesWithKey) {
						path = entry.getPath();
						//tries to install the request in the empty dimensions of a carrier 
						newResource = installSpafInPosition(entry.getAllocatedResource().getFirstSlot(),numCarrierSlots,numberOfSpatialSlots, path, request);
						firstSlot = newResource.getFirstSlot() ;

						if ( firstSlot > -1) {
							//if first slot up to -1 then success installing the request
							//sets the resuedConnection flag to true
							reusedConnection = true;
							// saves and schedule to close the connection
							systemStateDB.updateEntryAdd(entry, newResource.getDimensionsAllocated());
							saveAndScheduleToClose(request, path, newResource, event);
							success = true;
							//count the reused connections using JOINT
							countReusedConnectionsByJoS++;

							//	for debug propose
							if (isNewDebug) {
								System.out.println("R_ID: "+request.getId()+", CONNECTION WAS SUCCESSFULLY INSTALLED REUSING RESOURCES");
								System.out.println("Path: " + path);
								System.out.println("Allocated Resources: " + newResource);
								LinkStateTable mask = new LinkStateTable();
								mask.setStateTable(this.getMaskStateTable(path));
								System.out.println(mask) ;
							}

							break;
						} 
					}

				} 

			}

			ResourceAllocation resourceAllocation = null;
			if (!reusedConnection) {
				/*if (request.getId() > targetDebug) {
					System.out.println("There is no entry to be reused");
				}*/
				int numK = request.getKshortestPaths();

				//Gets a list with the shortest paths between the source and destination nodes
				KShortestPathList paths = getKshortestPathList( source, destination, numK);

				//added only for debug proposes
				pathDebug.addAll(paths.getPaths());

				if (paths.size()==0) {
					throw new ExcecaoGrafo ("There is no enabled paths available between nodes " + key);
				}
				//removes the existent path in SystemStateDB from the SPF list
				/*
				if (!entriesWithKey.isEmpty()) {
					for (SystemStateEntry e : entriesWithKey) {
						if (paths.getPaths().contains(e.getPath())) {
							paths.remove(e.getPath());
						}
					}
				}*/
				if (paths.size()==0) {
					throw new ExcecaoGrafo ("There is no enabled paths available between nodes " + key);
				}


				while (paths.size() > 0) {
					//Get the next shortest path
					path = paths.poll();
					resourceAllocation = installSpaceFirst(numCarrierSlots,numberOfSpatialSlots, path, request, true);
					firstSlot = resourceAllocation.getFirstSlot() ;

					if ( firstSlot > -1) {
						systemStateDB.addEntry(key, request, path, resourceAllocation);
						saveAndScheduleToClose(request, path, resourceAllocation, event);
						//Sets the success flag to true
						success = true;
						break;
					} 

				}


				if (!success) {
					//					System.out.println("\nRequest key: "  + request.getKey());
					//					System.out.println(request.getOpticalSuperChannel());
					if (isDebug()) {
						System.out.println("R_ID: "+request.getId()+", CONNECTION WAS BLOCKED");
					}

					if (simCounter.getValue() > this.steadyState) {
						if (isNewDebug) {
							System.out.println("R_ID: "+request.getId()+", CONNECTION WAS BLOCKED");
							System.out.println(this.systemStateDB);
							for (Caminho p : pathDebug) {
								System.out.println("Path: " + p);
								LinkStateTable mask = new LinkStateTable();
								mask.setStateTable(this.getMaskStateTable(p));
								System.out.println(mask) ;
							}
						}
						incrementaBloqueios();
					} 

				} else {
					if (isNewDebug) {
						System.out.println("R_ID: "+request.getId()+", CONNECTION WAS SUCCESSFULLY INSTALLED");
						System.out.println("Path: " + path);
						System.out.println("Allocated Resources: " + resourceAllocation);
						LinkStateTable mask = new LinkStateTable();
						mask.setStateTable(this.getMaskStateTable(path));
						System.out.println(mask) ;
					}

					if (isDebug()) {
						System.out.println("R_ID: "+request.getId()+", CONNECTION WAS SUCCESSFULLY INSTALLED");
					}
				} 
				if (isDebug()) {
					printStatistics(request, resourceAllocation);
				}
				//adds in inputNetworkLoad into the list
				this.getInputNetworkLoadDataList().add(getInputNetworkLoad());
			}

			break;
		case INDEPENDENT:
			spaceFirstRequestHandle(event, request);
			break;


		default:
			break;
		}


		return success;



	}


	@Override
	public synchronized EventList receberEvento(Event evento) throws Exception{

		if(evento.getConteudo() != null){
			if(evento.getType()==null){
				throw new ExcecaoControle(evento.getTime()+" "+evento.getConteudo().toString());
			}
		} else {
			throw new ExcecaoControle(evento.getTime()+" - Event cound't be null!");
			/*			listaEventos.inserir(null);
			return listaEventos;*/
		}

		switch (evento.getType()){
		
		case IS_TO_FULLSS:
			
			RequestRSA req = (RequestRSA) evento.getConteudo();
			
//			int	numOfSpatialSlots = (int)req.getOpticalSuperChannel().getNumSlots();

			switch (req.getType()){

			case SPAF: case SPEF:
				/*sets the traffic pattern of the simulation: event time and holding time of the connection*/
				this.setPadroesDeTrafego(evento.getTime(), req.getHoldingTime());
				//Handles the request
				if(isDebug()) {
					System.out.println("########\nDEBUG: ALGORITHM "+ req.getType().name() + ", REQUEST ID:" + req.getId()+
							", Optical Superchannel = " + req.getOpticalSuperChannel().toString());
				}
				//	
				boolean success = false;
				int loop = 0;
				
				/*incremental simulation*/
				if (isIncremental()) {
					do {
						setGrafo(overlayWDMGraph);
						this.setSystemStateDB(systemStateDB_WDM);
						setLossNode(Constants.LOSS_WSS);
						success = handleJointSwitch(evento); 
						if (!success) {
							/*change the switching strategy based on threshold of number of dimensions */
							if (this.getGrafo().getNumberOfDimensions() == getThresholdNumDimensions()) {
								if (!hasOverlayEverBeenUsed) {
									this.numberOfDimensions += this.overlaySDMGraph.getNumberOfDimensions();
									hasOverlayEverBeenUsed = true;
								}
								setGrafo(overlaySDMGraph);
								this.setSystemStateDB(systemStateDB_SDM);
								setLossNode(Constants.LOSS_OXC);
								req.setSwitchingType(SwitchingType.JOINT);
								success = handleJointSwitch(evento);
							}
							
							/*scale the network*/
							if (!success) {
								
								//TODO
								log.info(req.getType()+": installing new dimension in " + this.getGrafo().getName() +". from " + this.getGrafo().getNumberOfDimensions() + " to " + (this.getGrafo().getNumberOfDimensions()+1));
								upgradeNetworkDimension();
								loop++;
								if (loop > 10) {
									throw new AllocationException("Loop was found when controller was trying to install de request: " + req+ "!");
								}
								
							}
							
						}
						/*no blocking probability is expected*/
					} while (!success) ;
				} else {
					/*doesn't scale the network, the network capacity 
					 * must be set up in the configuration*/
					
					setGrafo(overlayWDMGraph);
					setLossNode(Constants.LOSS_WSS);
					success = handleJointSwitch(evento); 
					if (!success) {
						/*change the switching strategy based on threshold of number of dimensions */
						if (this.getNumberOfDimensions() == getThresholdNumDimensions()) {
							setGrafo(overlaySDMGraph);
							setLossNode(Constants.LOSS_OXC);
							req.setSwitchingType(SwitchingType.JOINT);
							success = handleJointSwitch(evento);
						}
					}
				}
				

				break;
			default:
				break;
			}

		break;
			
			
			

		case REQUEST_RSA:
			RequestRSA request = (RequestRSA) evento.getConteudo();
			int numberOfSpectralSlots = 0;
			int numberOfSpatialSlots = 0;

			if (request.getBandwidth() != null ) {
				numberOfSpectralSlots = request.getBandwidth().getSpectralSlots();
				numberOfSpatialSlots = request.getBandwidth().getSpatialSlots();
			} else if (request.getOpticalSuperChannel() != null ){
				numberOfSpatialSlots = (int)request.getOpticalSuperChannel().getNumSlots();

			}

			//to account how many times each pair source-destination is sorted 
			Integer a = this.pairSourceDestinationDistribution.get(request.getKey());
			a++;
			pairSourceDestinationDistribution.put(request.getKey(), a);

			switch (request.getType()){

			case SPAF: case SPEF:
				/*sets the traffic pattern of the simulation: event time and holding time of the connection*/
				this.setPadroesDeTrafego(evento.getTime(), request.getHoldingTime());
				//Handles the request
				if(isDebug()) {
					System.out.println("########\nDEBUG: ALGORITHM "+ request.getType().name() + ", REQUEST ID:" + request.getId()+
							", Optical Superchannel = " + request.getOpticalSuperChannel().toString());
				}
				//	
				boolean success = false;
				int loop = 0;
				if (isIncremental()) {
					do {						
						success = handleJointSwitch(evento); 
						if (!success) {
							log.info(request.getType()+": installing new dimension. from " + getNumberOfDimensions() + " to " + (getNumberOfDimensions()+1) +  ", requests = " + getNumRequisicoes());
							upgradeNetworkDimension();
							loop++;
							if (loop > 10) {
								throw new AllocationException("Loop was found when controller was trying to install de request: " + request+ "!");
							}
							
						}
						
					} while (!success) ;
				} else {
					handleJointSwitch(evento);
				}
				
				

				break;


			case FLEX_SDM:

				/*sets the traffic pattern of the simulation: event time and holding time of the connection*/
				this.setPadroesDeTrafego(evento.getTime(), request.getHoldingTime());
				//Handles the request
				if(isDebug()) {
					System.out.println("########\nDEBUG: ALGORITHM "+ request.getType().name() + ", REQUEST ID:" + request.getId()+
							", BANDWIDTH = " + request.getBandwidth().name());
				}
				spaceFirstRequestHandle(evento, request);

				break;

			case SPACE_FIRST:
				/*sets the traffic pattern of the simulation: event time and holding time of the connection*/
				this.setPadroesDeTrafego(evento.getTime(), request.getHoldingTime());
				//Handles the request
				if(isDebug()) {
					System.out.println("########\nDEBUG: ALGORITHM "+ request.getType().name() + ", REQUEST ID:" + request.getId()+
							//							", BANDWIDTH = " + request.getBandwidth().name());
							", Optical Superchannel = " + request.getOpticalSuperChannel().toString());
				}
				spaceFirstRequestHandle(evento, request);

				break;

			case SPECTRUM_FIRST:
				/*sets the traffic pattern of the simulation: event time and holding time of the connection*/
				this.setPadroesDeTrafego(evento.getTime(), request.getHoldingTime());
				//Handles the request
				if(isDebug()) {
					System.out.println("########\nDEBUG: ALGORITHM "+ request.getType().name() + ", REQUEST ID:" + request.getId()+
							", Optical Superchannel = " + request.getOpticalSuperChannel().toString());
				}
				spectrumFirstRequestHandle(evento, request);

				break;

			case SPECTRUM_FIRST_FIT_SLIGHTLY:
				/*sets the traffic pattern of the simulation: event time and holding time of the connection*/
				this.setPadroesDeTrafego(evento.getTime(), request.getHoldingTime());
				if(isDebug()) {
					System.out.println("########\nDEBUG: ALGORITHM "+ request.getType().name() + ", REQUEST ID:" + request.getId()+
							", BANDWIDTH = " + request.getBandwidth().name());
				}
				//Handles the request
				spectrumFirstSlightlyModifiedHandle(evento, request);

				break;

			case CLOSE_REQUEST:

				Caminho pathSFF = getRequestPathTable().get(request.getId());
				if(isDebug()) {
					System.out.println("########\nDEBUG: ALGORITHM "+ request.getType().name() + ", REQUEST ID:" + request.getId()+
							", BANDWIDTH = " + request.getBandwidth().name());
				}
				removeAllocatedResources(request.getBandwidth().getSpectralSlots(), request.getResource(), pathSFF,request);

				if(isBidirectional()){
					this.inputNetworkLoad -= (2*numberOfSpectralSlots*pathSFF.getEnlaces().tamanho());
				} else {
					this.inputNetworkLoad -= (numberOfSpectralSlots*pathSFF.getEnlaces().tamanho());
				}

				break;

			case CLOSE_SPECTRUM_FIRST:
				Caminho pathSpeF = getRequestPathTable().get(request.getId());
				int numSlots = 0;

				if (request.getBandwidth() != null ) {
					numSlots = request.getBandwidth().getSpectralSlots();
				} else if (request.getOpticalSuperChannel() != null ){
					numSlots = (int) request.getOpticalSuperChannel().getNumSlots();

				}

				if(isDebug()) {
					if (request.getBandwidth() != null ) {
						System.out.println("########\nDEBUG: ALGORITHM "+ request.getType().name() + ", REQUEST ID:" + request.getId()+
								", BANDWIDTH = " + request.getBandwidth().name());
					} else if (request.getOpticalSuperChannel() != null ){
						System.out.println("########\nDEBUG: ALGORITHM "+ request.getType().name() + ", REQUEST ID:" + request.getId()+
								", Optical SuperChannel = " + request.getOpticalSuperChannel());

					}

				}



				removeAllocatedResources(numSlots, request.getResource(), pathSpeF,request);

				if(isBidirectional()){
					this.inputNetworkLoad -= (2*numberOfSpectralSlots*pathSpeF.getEnlaces().tamanho());
				} else {
					this.inputNetworkLoad -= (numberOfSpectralSlots*pathSpeF.getEnlaces().tamanho());
				}



				break;

			case CLOSE_SPACE_FIRST:

				Caminho pathSpaF = getRequestPathTable().get(request.getId());
				if(isDebug()) {
					if (request.getBandwidth() != null ) {
						System.out.println("########\nDEBUG: ALGORITHM "+ request.getType().name() + ", REQUEST ID:" + request.getId()+
								", BANDWIDTH = " + request.getBandwidth().name());
					} else if (request.getOpticalSuperChannel() != null ){
						System.out.println("########\nDEBUG: ALGORITHM "+ request.getType().name() + ", REQUEST ID:" + request.getId()+
								", Optical SuperChannel = " + request.getOpticalSuperChannel());

					}

				}

				removeAllocatedResources(getMinimumSpectralSlotsPerCarrier(), request.getResource(), pathSpaF, request);

				if(isBidirectional()){
					this.inputNetworkLoad -= (2*numberOfSpatialSlots*pathSpaF.getEnlaces().tamanho());
				} else {
					this.inputNetworkLoad -= (numberOfSpatialSlots*pathSpaF.getEnlaces().tamanho());
				}

				break;

			default:
				break;
			}

			break;

		default:
			break;


		}

		return listaEventos;

	}



	/**
	 * Installs the number of slots in the spectrum using the algorithm spectral first allocation.
	 * runs in the first spatial dimension from left to right spectral slots,
	 * moving to highest dimension if no void spectral slots are found in the actual dimension
	 * @param numberOfSlots the number of slots to be installed into each link of the path.
	 * @param path the path where of resources will be installed.
	 * @return {@link ResourceAllocation} a object with the resource that have been allocated for the connection.
	 * @throws AllocationException 
	 * @throws ExcecaoGrafo 
	 */
	public ResourceAllocation installSpectrumFirst(int numberOfSlots, Caminho path, RequestRSA request) throws AllocationException, ExcecaoGrafo {

		HashMap<Integer, Boolean[]> maskStateTable = getMaskStateTable(path);

		int dimension = -1;
		int firstSlot = -1;

		for(Iterator<Enlace> it = path.getEnlaces().valores().iterator() ; it.hasNext() ;){
			Enlace edge = it.next();//the edge in the path.
			//the link is the same edge in the graph, but could be a different object.
			Enlace link = getGrafo().getEnlace(edge.getId());
			Enlace linkReverse = null;
			if (isBidirectional()) {
				No left = link.getNoEsquerda();
				No right = link.getNoDireita();
				linkReverse = getGrafo().getEnlace(right, left);
			}

			/*	testes if there is a continuous void range of slots in the first dimension, moving to the highest
			 * 	dimension if there isn't */
			if(isDebug()) {

				System.out.println("R_ID: "+request.getId()+", Before install \n Link:"+link.getId() + ", num slots: " + numberOfSlots);
				System.out.println("R_ID: "+request.getId()+", LINK STATE:\n"+link.getLinkStateTable());
				if (isBidirectional()) {
					System.out.println("R_ID: "+request.getId()+", REVERSE LINK STATE ("+linkReverse.getId() + "):\n"+linkReverse.getLinkStateTable());
				}
			}


			for (int i = 0 ; i < numberOfDimensions ; i++ ) {
				/*gets the position in a row of the state table*/
				firstSlot = getPosition(numberOfSlots, maskStateTable.get(i));
				if (firstSlot > -1) {
					dimension = i; //gets the dimension's index

					for (int k = 0 ; k < numberOfSlots ; k++) {
						if ( link.getStateSpectralArray(dimension)[firstSlot+k] == true ){
							link.getStateSpectralArray(dimension)[firstSlot+k]= false;
							if (isFewModeFiber()) {
								Boolean[] linkStateFMF = getStateLinkWdmTable().get(link.getId()); 
								if (linkStateFMF[firstSlot+k]) {
									linkStateFMF[firstSlot+k] = false;
								}
							}

						} else {
							//							System.out.println(maskStateCarrier);
							throw new AllocationException("slot is already used in link " + link + ", slot: " + (firstSlot+k) + ", dimension: " + dimension );
						}
						if (isBidirectional()) {
							if ( linkReverse.getStateSpectralArray(dimension)[firstSlot+k] == true ){
								linkReverse.getStateSpectralArray(dimension)[firstSlot+k]= false;//reverse link
								if (isFewModeFiber()) {
									Boolean[] linkStateFMF = getStateLinkWdmTable().get(linkReverse.getId()); 
									if (linkStateFMF[firstSlot+k]) {
										linkStateFMF[firstSlot+k] = false;
									}
								}

							} else {
								throw new AllocationException("slot is already used in reverse link " + link + ", slot: " + (firstSlot+k) + ", dimension: " + dimension );
							}
						}
					}
					break;
				}

			}
			if(isDebug()) {
				System.out.println("R_ID: "+request.getId()+", After install \n Link:"+link.getId() + ", num slots: " + numberOfSlots);
				System.out.println("R_ID: "+request.getId()+", LINK STATE:\n"+link.getLinkStateTable());
				if (isBidirectional()) {
					System.out.println("R_ID: "+request.getId()+", REVERSE LINK STATE ("+linkReverse.getId() + "):\n"+linkReverse.getLinkStateTable());
				}
			}
		}

		ResourceAllocation resource = new ResourceAllocation();
		resource.addDimensionInList(dimension);
		resource.setFirstSlot(firstSlot);

		return resource;

	}


	/**
	 * Tries to install the request in the empty dimensions of a carrier.
	 * Returns a {@link ResourceAllocation} object with the position and the dimensions where the resources have been installed.
	 * The position's index means place where starts the resource. The dimensions's index means the index of fibers or modes where
	 * there are available slots in the specified position (carrier) to install the demand.
	 * A {@link ResourceAllocation} with position = -1 means that the installation fails because the there were no available resource for the demand
	 *
	 * @param position the position slot where the carrier begins.
	 * @param numCarrierSlots number of slots of a single optical carrier.
	 * @param numberOfSpatialSlots number of slots need in a spatial way.
	 * @param path the path with links between two nodes
	 * @param request 
	 * @return a {@link ResourceAllocation} object with the position in the spectrum and the dimensions to allocate the resources.
	 * @throws AllocationException throws an error exception if the slots are already allocated
	 */
	public ResourceAllocation installSpafInPosition(int positionCarrier, int numCarrierSlots, int numberOfSpatialSlots, Caminho path, RequestRSA request) throws AllocationException {

		HashMap<Integer, Boolean[]> carrierMask = getMaskPerCarrier(positionCarrier, path);

		//to debug
		LinkStateTable maskStateCarrier = new LinkStateTable();
		maskStateCarrier.setStateTable(carrierMask);

		int dimensionsNeed = numberOfSpatialSlots/numCarrierSlots;
		ResourceAllocation resource = getDimensionsAvailableInCarrier(dimensionsNeed, numCarrierSlots, carrierMask);
		int position = resource.getFirstSlot();

		if (position > -1) {
			resource.setFirstSlot(positionCarrier);
			if(isDebug()){
				System.out.println("Path: " + path);
			}

		
			@SuppressWarnings("unused")
			Enlace lastLink = null;
			for(Iterator<Enlace> it = path.getEnlaces().valores().iterator() ; it.hasNext() ;){
				Enlace edge = it.next();//the edge in the path.
				//the link is the same edge in the graph, but could be a different object.
				Enlace link = getGrafo().getEnlace(edge.getId());
				Enlace linkReverse = null;
				if (isBidirectional()) {
					No left = link.getNoEsquerda();
					No right = link.getNoDireita();
					linkReverse = getGrafo().getEnlace(right, left);
				}
				if(isDebug()) {
					System.out.println("R_ID: "+request.getId()+", Before install \n Link:"+link.getId() + ", num slots: " + numberOfSpatialSlots);
					System.out.println("R_ID: "+request.getId()+", LINK STATE:\n"+link.getLinkStateTable());
					if (isBidirectional()) {
						System.out.println("R_ID: "+request.getId()+", REVERSE LINK STATE ("+linkReverse.getId() + "):\n"+linkReverse.getLinkStateTable());
					}
				}


				for (int dimension : resource.getDimensionsAllocated() ) { // loop for dimensions with available resources

					for (int k = 0 ; k < numCarrierSlots ; k++) { // loop for slots in the dimension with available resources
						if ( link.getStateSpectralArray(dimension)[positionCarrier+k] == true ){
							link.getStateSpectralArray(dimension)[positionCarrier+k]= false;
						} else {
							System.out.println(maskStateCarrier);
							throw new AllocationException("slot is already used in link " + link + ", slot: " + (positionCarrier+k) + ", dimension: " + dimension );
						}
						if (isBidirectional()) {
							if ( linkReverse.getStateSpectralArray(dimension)[positionCarrier+k] == true ){
								linkReverse.getStateSpectralArray(dimension)[positionCarrier+k]= false;//reverse link

							} else {
								throw new AllocationException("slot is already used in reverse link " + link + ", slot: " + (positionCarrier+k) + ", dimension: " + dimension );
							}
						}
					}
				}

				if(isDebug()) {
					System.out.println("R_ID: "+request.getId()+", After install \n Link:"+link.getId() + ", num slots: " + numberOfSpatialSlots);
					System.out.println("R_ID: "+request.getId()+", LINK STATE:\n"+link.getLinkStateTable());
					if (isBidirectional()) {
						System.out.println("R_ID: "+request.getId()+", REVERSE LINK STATE ("+linkReverse.getId() + "):\n"+linkReverse.getLinkStateTable());
					}
				}
				lastLink = link; //for debugging proposes
			}
			
		} else {
			if(isDebug()) {
				LinkStateTable linkStateTableMask = new LinkStateTable();
				linkStateTableMask.setStateTable(carrierMask);
				int lastSlot = positionCarrier+numCarrierSlots;
				System.out.println("R_ID: "+request.getId()+",\nMask of the carrier: [" + positionCarrier + "-" + lastSlot + "] between nodes: "+ request.getKeySourceDestination() + ", num slots required: " + numberOfSpatialSlots);
				System.out.println("R_ID: "+request.getId()+", LINK STATE:\n"+linkStateTableMask);

			}
		}

		return resource;
	}



	/**
	 *
	 * Returns a {@link ResourceAllocation} object with the position and the dimensions where the resources have been installed.
	 * The position's index means place where starts the resource. The dimensions's index means the index of fibers or modes where
	 * there were available slots in the specified position to accommodate the demand.
	 * A {@link ResourceAllocation} with position = -1 means that the installation fails because the there were no available resource for the demand
	 *
	 * @param numCarrierSlots number of slots of a single optical carrier.
	 * @param numberOfSpatialSlots number of slots need in a spatial way.
	 * @param path the path with links between two nodes
	 * @param isJointSwitching used for JOINT SWITCHING scheme
	 * @return a {@link ResourceAllocation} object with the position and the dimensions where there are available resources.
	 * @throws AllocationException throws a exception if occurring a allocation error
	 * @throws ExcecaoGrafo 
	 */
	public ResourceAllocation installSpaceFirst(int numCarrierSlots, int numberOfSpatialSlots, Caminho path, RequestRSA request, boolean isJointSwitching) throws AllocationException, ExcecaoGrafo {
		//		int targetDebug = 60000;
		HashMap<Integer, Boolean[]> maskStateTable = getMaskStateTable(path);
		LinkStateTable pathMask = new LinkStateTable();
		pathMask.setStateTable(maskStateTable);
		ResourceAllocation resource = getPositionAndDimensionSpaceFirst(0, numberOfSpatialSlots, numCarrierSlots, maskStateTable);
		int position = resource.getFirstSlot();

		boolean isLocked = true;
		int spectrumLength = this.getNumLambdas() -1;
		//used to joint switching
		if ( position > -1 && isJointSwitching) {
			int pointer = position;
			while ( isLocked && pointer <= spectrumLength  ){
				isLocked = isCarrierLocked(maskStateTable, pointer, numCarrierSlots);
				if (isLocked){
					//					System.out.println("Carrier is locked by other connection: First position = " + pointer);
					pointer += numCarrierSlots;
					//					System.out.println("Getting next carrier pointer = " + pointer);
					resource = getPositionAndDimensionSpaceFirst(pointer, numberOfSpatialSlots, numCarrierSlots, maskStateTable);
					position = resource.getFirstSlot();
					pointer = position;
					if (position == -1) {
						break;

					} 
				} /*else {
					isLocked = false;
				}*/
			}
		} 

		if ( position > -1 ) {
			if(isDebug()){
				System.out.println("Path: " + path);
			}
			for(Iterator<Enlace> it = path.getEnlaces().valores().iterator() ; it.hasNext() ;){
				Enlace edge = it.next();//the edge in the path.
				//the link is the same edge in the graph, but could be a different object.
				Enlace link = getGrafo().getEnlace(edge.getId());
				Enlace linkReverse = null;
				if (isBidirectional()) {
					No left = link.getNoEsquerda();
					No right = link.getNoDireita();
					linkReverse = getGrafo().getEnlace(right, left);
				}
				if(isDebug()) {
					System.out.println("R_ID: "+request.getId()+", Before install \n Link:"+link.getId() + ", num slots: " + numberOfSpatialSlots);
					System.out.println("R_ID: "+request.getId()+", LINK STATE:\n"+link.getLinkStateTable());
					if (isBidirectional()) {
						System.out.println("R_ID: "+request.getId()+", REVERSE LINK STATE ("+linkReverse.getId() + "):\n"+linkReverse.getLinkStateTable());
					}
				}
				for (int dimension : resource.getDimensionsAllocated() ) { // loop for dimensions with available resources

					for (int k = 0 ; k < numCarrierSlots ; k++) { // loop for slots in the dimension with available resources

						if ( link.getStateSpectralArray(dimension)[position+k] == true ){
							link.getStateSpectralArray(dimension)[position+k]= false;

						} else {
							throw new AllocationException("slot is already used in link " + link + ", slot: " + (position+k) + ", dimension: " + dimension );
						}

						if (isBidirectional()) {

							if ( linkReverse.getStateSpectralArray(dimension)[position+k] == true ){
								linkReverse.getStateSpectralArray(dimension)[position+k]= false;//reverse link
							} else {
								throw new AllocationException("slot is already used in reverse link " + linkReverse + ", slot: " + (position+k) + ", dimension: " + dimension );
							}

						}

					}

				}
				if(isDebug()) {
					System.out.println("R_ID: "+request.getId()+", After install \n Link:"+link.getId() + ", num slots: " + numberOfSpatialSlots);
					System.out.println("R_ID: "+request.getId()+", LINK STATE:\n"+link.getLinkStateTable());
					if (isBidirectional()) {
						System.out.println("R_ID: "+request.getId()+", REVERSE LINK STATE ("+linkReverse.getId() + "):\n"+linkReverse.getLinkStateTable());
					}
				}
			}
		} else {
			//			if (request.getId() == targetDebug) {
			//			System.out.println("there is no resouces available in path mask for " + path);
			//			System.out.println(pathMask);
			//			}
			if(isDebug()) {
				LinkStateTable linkStateTableMask = new LinkStateTable();
				linkStateTableMask.setStateTable(maskStateTable);
				System.out.println("R_ID: "+request.getId()+",\nMask of all bidirections links between nodes: "+ request.getKeySourceDestination() + ", num slots required: " + numberOfSpatialSlots);
				System.out.println("R_ID: "+request.getId()+", LINK STATE:\n"+linkStateTableMask);

			}
		}
		return resource;
	}

	/**
	 * Returns a {@link ResourceAllocation} object with the position and the dimensions where there are available resources.
	 * The position's index means place where starts the resource. The dimensions's index means the index of fibers or modes where
	 * there are available slots in the specified position to accommodate the demand.
	 * A {@link ResourceAllocation} with position = -1 means the there isn't resource for the demand.
	 *
	 * @param position the position in the spectrum to start the search.
	 * @param numberOfSpatialSlots number of slots need in a spatial way.
	 * @param numCarrierSlots number of slots of a single optical carrier.
	 * @param maskStateTable the mask with the state table of the links in a path
	 * @return a {@link ResourceAllocation} object with the position and the dimensions where there are available resources.
	 */
	public ResourceAllocation getPositionAndDimensionSpaceFirst(int position, int numberOfSpatialSlots, int numCarrierSlots,
			HashMap<Integer, Boolean[]> maskStateTable)  {
		ResourceAllocation resource = new ResourceAllocation();
		HashMap<Integer, Boolean[]> sectionTable =new HashMap<>();
		int dimensionsNeed = numberOfSpatialSlots/numCarrierSlots;

		/*gets only the section of spectrum which the length is equal the carriet's slot length*/
		for (Iterator<Integer> it = maskStateTable.keySet().iterator() ; it.hasNext() ; ) {
			int dimension = it.next();
			int length = position+numCarrierSlots;
			Boolean[] spectrum = maskStateTable.get(dimension);
			Boolean[] carrierSlots = Arrays.copyOfRange(spectrum, position, length);
			sectionTable.put(dimension, carrierSlots);
		}

		/*if can find slots available*/
		Iterator<Integer> it = sectionTable.keySet().iterator();
		int dimension = -1;
		boolean hasAvailableSlots = false;
		if(it.hasNext() ) {

			int counter = 0;
			for ( ; it.hasNext() ; ) { // loop for dimensions
				dimension = it.next();
				Boolean[] section = sectionTable.get(dimension);
				int sequence = 0;
				for (int j = 0; j < section.length; j++) { // loop for slots
					if(section[j]!= null && section[j]){
						sequence++;
					} else {
						if (sequence < numCarrierSlots){
							sequence = 0;
						}
					}

				}

				if (sequence == numCarrierSlots) {
					resource.addDimensionInList(dimension);
					counter++;
					if (counter == dimensionsNeed) {

						hasAvailableSlots = true;
						break;
					}
				}
			}

		}

		if (hasAvailableSlots) {
			resource.setFirstSlot(position);
			return resource;
		} else {
			if (position+numCarrierSlots >= maskStateTable.get(0).length ){
				resource.getDimensionsAllocated().clear();
				resource.setFirstSlot(-1);
				return resource;
			} else {
				return getPositionAndDimensionSpaceFirst(position+numCarrierSlots, numberOfSpatialSlots, numCarrierSlots, maskStateTable);
			}
		}

	}

	public ResourceAllocation getDimensionsAvailableInCarrier(int numDimensionsRequired, int numCarrierSlots,
			HashMap<Integer, Boolean[]> carrierMask)  {

		ResourceAllocation resource = new ResourceAllocation();

		/*if can find slots available*/
		Iterator<Integer> it = carrierMask.keySet().iterator();
		int dimension = -1;
		boolean hasAvailableSlots = false;
		if(it.hasNext() ) {

			int counter = 0;
			for ( ; it.hasNext() ; ) { // loop for dimensions
				dimension = it.next();
				Boolean[] section = carrierMask.get(dimension);
				int sequence = 0;
				for (int j = 0; j < section.length; j++) { // loop for slots
					if(section[j]!= null && section[j]){
						sequence++;
					} else {
						if (sequence < numDimensionsRequired){
							sequence = 0;
						}
					}

				}

				if (sequence == numCarrierSlots) {
					resource.addDimensionInList(dimension);
					counter++;
					if (counter == numDimensionsRequired) {

						hasAvailableSlots = true;
						break;
					}
				}
			}

		}

		if (hasAvailableSlots) {
			resource.setFirstSlot(0);
			return resource;
		} else {
			resource.getDimensionsAllocated().clear();
			resource.setFirstSlot(-1);
			return resource;
		}

	}

	public void removeAllocatedResources (int numberOfSpectralSlots, ResourceAllocation resource, Caminho path, RequestRSA request) {
		int firstSlot = resource.getFirstSlot();
		int slots = numberOfSpectralSlots*resource.getDimensionsAllocated().size();
		if (useDataBase) {
			SystemStateEntry entry = systemStateDB.getEntryByRequest(request);
			if (entry != null) {
				systemStateDB.remove(entry);
			}
		}
		for(Iterator<Enlace> it = path.getEnlaces().valores().iterator() ; it.hasNext() ;){
			Enlace edge = it.next();//the edge in the path.
			//the link is the same edge in the graph, but could be a different object.
			Enlace link = getGrafo().getEnlace(edge.getId());
			Enlace linkReverse = null;
			Boolean[] stateArray = null;
			Boolean[] reverseLinkState = null;

			if (isBidirectional()) {
				No left = link.getNoEsquerda();
				No right = link.getNoDireita();
				linkReverse = getGrafo().getEnlace(right, left);
			}
			if(isDebug()) {

				System.out.println("R_ID: "+request.getId()+", Before remove \n Link:"+link.getId() + ", num slots: " + slots);
				System.out.println("R_ID: "+request.getId()+", LINK STATE:\n"+link.getLinkStateTable());
				if (isBidirectional()) {
					System.out.println("R_ID: "+request.getId()+", REVERSE LINK STATE ("+linkReverse.getId() + "):\n"+linkReverse.getLinkStateTable());
				}
			}
			for (int dimension : resource.getDimensionsAllocated()) {
				stateArray = link.getStateSpectralArray(dimension);
				for (int j = firstSlot ; j < firstSlot + numberOfSpectralSlots ; j++) {
					stateArray[j]= true;
					if (isFewModeFiber() && request.getType().equals(RequestRSA.RequestType.CLOSE_SPECTRUM_FIRST)) {
						Boolean[] linkStateFMF = getStateLinkWdmTable().get(link.getId()); 
						if (!linkStateFMF[j]) {
							linkStateFMF[j] = false;
						}
					}
					if (isBidirectional()) {
						reverseLinkState = linkReverse.getStateSpectralArray(dimension);
						reverseLinkState[j] = true;//reverse link
						if (isFewModeFiber() && request.getType().equals(RequestRSA.RequestType.CLOSE_SPECTRUM_FIRST)) {
							Boolean[] linkStateFMF = getStateLinkWdmTable().get(linkReverse.getId()); 
							if (!linkStateFMF[j]) {
								linkStateFMF[j] = false;
							}
						}
					}
				}
			}
			if(isDebug()) {
				System.out.println("R_ID: "+request.getId()+", After remove \n Link:"+link.getId() + ", num slots: " + slots);
				System.out.println("R_ID: "+request.getId()+", LINK STATE:\n"+link.getLinkStateTable());
				if (isBidirectional()) {
					System.out.println("R_ID: "+request.getId()+", REVERSE LINK STATE ("+linkReverse.getId() + "):\n"+linkReverse.getLinkStateTable());
				}
				printStatistics(request, resource);
			}

		}

	}


	/**
	 * Installs the link state table in each Graph's edge
	 */
	private void startStateTable(){
		for(Enlace e : getGrafo().getEnlaces().valores()){
			e.installStateTable(this.getGrafo().getNumberOfDimensions(), getMascara());
		}
	}
	
	/**
	 * Installs the link state table in each Graph's edge
	 * @param graph the network graph
	 */
	private void startStateTable(AbstractGrafo graph){
		for(Enlace e : graph.getEnlaces().valores()){
			e.installStateTable(graph.getNumberOfDimensions(), getMascara());
		}
	}


	/**
	 * @return the inputNetworkLoad
	 */
	public int getInputNetworkLoad() {
		return inputNetworkLoad;
	}

	/**
	 * @param inputNetworkLoad the inputNetworkLoad to set
	 */
	public void setInputNetworkLoad(int inputNetworkLoad) {
		this.inputNetworkLoad = inputNetworkLoad;
	}

	/**
	 * @deprecated
	 * use getGrafo().getNumberofDimension();
	 * @return the numberOfDimensions
	 */
	public int getNumberOfDimensions() {
		return numberOfDimensions;
	}

	/**
	 * @param numberOfDimensions the numberOfDimensions to set
	 */
	public void setNumberOfDimensions(int numberOfDimensions) {
		this.numberOfDimensions = numberOfDimensions;
	}



	/**
	 * @return the numberOfFiberPerCable
	 */
	public int getNumberOfFiberPerCable() {
		return numberOfFiberPerCable;
	}

	/**
	 * @param numberOfFiberPerCable the numberOfFiberPerCable to set
	 */
	public void setNumberOfFiberPerCable(int numberOfFiberPerCable) {
		this.numberOfFiberPerCable = numberOfFiberPerCable;
	}

	/**
	 * @return the stateLinkWdmTable
	 */
	public HashMap<String, Boolean[]> getStateLinkWdmTable() {
		//		return stateLinkWdmTable;
		return getTabelaDeEstados();
	}

	/**
	 * @param stateLinkWdmTable the stateLinkWdmTable to set
	 */
	public void setStateLinkWdmTable(HashMap<String, Boolean[]> stateLinkWdmTable) {
		setTabelaDeEstados(stateLinkWdmTable);
	}


	/**
	 * @return the inputNetworkLoadDataList
	 */
	public ArrayList<Integer> getInputNetworkLoadDataList() {
		return inputNetworkLoadDataList;
	}

	/**
	 * @param inputNetworkLoadDataList the inputNetworkLoadDataList to set
	 */
	public void setInputNetworkLoadDataList(
			ArrayList<Integer> inputNetworkLoadDataList) {
		this.inputNetworkLoadDataList = inputNetworkLoadDataList;
	}





	/**
	 * @return the totalInputNetworkLoad
	 */
	public double getTotalInputNetworkLoad() {
		return totalInputNetworkLoad;
	}

	/**
	 * @param totalInputNetworkLoad the totalInputNetworkLoad to set
	 */
	public void setTotalInputNetworkLoad(double totalInputNetworkLoad) {
		this.totalInputNetworkLoad = totalInputNetworkLoad;
	}

	/**
	 * @return the averageInputNetworkLoad
	 */
	public double getAverageInputNetworkLoad() {
		averageInputNetworkLoad = (double)inputNetworkLoad/this.totalInputNetworkLoad;
		return averageInputNetworkLoad;
	}

	/**
	 * @param averageInputNetworkLoad the averageInputNetworkLoad to set
	 */
	public void setAverageInputNetworkLoad(double averageInputNetworkLoad) {
		this.averageInputNetworkLoad = averageInputNetworkLoad;
	}


	public void printStatistics (RequestRSA request, ResourceAllocation resourceAllocation) {
		System.out.println("R_ID: "+request.getId()+", Blocking probability = " + probabilidadeBloqueio());
		System.out.println("R_ID: "+request.getId()+", Average Input Load (%) = " + getAverageInputNetworkLoad());
		if (resourceAllocation != null) {
			System.out.println(resourceAllocation);
		}
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

	/**
	 * @return the Simulation's Counter
	 */
	public Counter getCounter() {
		return simCounter;
	}

	/**
	 * @param simCounter the Simulation's Counter to set
	 */
	public void setCounter(Counter simCounter) {
		this.simCounter = simCounter;
	}

	/**
	 * @return the fewModeFiber
	 */
	public boolean isFewModeFiber() {
		return fewModeFiber;
	}

	/**
	 * @param fewModeFiber the fewModeFiber to set
	 */
	public void setFewModeFiber(boolean fewModeFiber) {
		this.fewModeFiber = fewModeFiber;
	}




	/** 
	 * @overwrite
	 * returns the blocking probability of the simulation
	 * 
	 * @see opticalnetwork.controlplane.Controle#probabilidadeBloqueio()
	 */
	public double probabilidadeBloqueio() {
		//		System.out.println("Bloqueios no controlador: " + (double)getNumCaminhosOpticosBloqueados()/getNumRequisicoes());
		double pb = (double)getNumCaminhosOpticosBloqueados()/getNumRequisicoes();
		if (Double.isNaN(pb)) {
			return 0.0;
		}
		return pb;

	}

	/**
	 * 
	 * @return the systemStateDB
	 */
	public SystemState getSystemStateDB() {
		return this.systemStateDB;
	}

	/**
	 * @param systemStateDB the systemStateDB to set
	 */
	public void setSystemStateDB(SystemState systemStateDB) {
		this.systemStateDB = systemStateDB;
	}

	/**
	 * @return the countReusedConnectionsByJoS
	 */
	public int getCountReusedConnectionsByJoS() {
		return countReusedConnectionsByJoS;
	}

	/**
	 * @param countReusedConnectionsByJoS the countReusedConnectionsByJoS to set
	 */
	public void setCountReusedConnectionsByJoS(int countReusedConnectionsByJoS) {
		this.countReusedConnectionsByJoS = countReusedConnectionsByJoS;
	}

	/**
	 * @return the classes
	 */
	public HashMap<Integer, Integer> getClasses() {
		return classes;
	}

	/**
	 * @return the isNewDebug
	 */
	public boolean isNewDebug() {
		return isNewDebug;
	}

	/**
	 * @param isNewDebug the isNewDebug to set
	 */
	public void setNewDebug(boolean isNewDebug) {
		this.isNewDebug = isNewDebug;
	}

	/**
	 * @return the useDataBase
	 */
	public boolean isUseDataBase() {
		return useDataBase;
	}

	/**
	 * @param useDataBase the useDataBase to set
	 */
	public void setUseDataBase(boolean useDataBase) {
		this.useDataBase = useDataBase;
	}



	/**
	 * Upgrades the whole network keeping the states of the links
	 * @throws ExcecaoGrafo throws a error if the new dimension's key is already installed.
	 */
	public void upgradeNetworkDimension () throws ExcecaoGrafo {
		this.upgradeDimensionIntoNetwork(this.getGrafo().getNumberOfDimensions());
		this.numberOfDimensions++;
		this.getGrafo().increaseNumberOfDimensions();
		this.totalInputNetworkLoad = this.getNumLambdas()*this.numberOfDimensions*this.getGrafo().getEnlaces().tamanho();
	}

	/**
	 * Upgrades the all links with the new spectra slot size keeping the state's links
	 * @throws ExcecaoGrafo throws a error if the new dimension's key is already installed.
	 */
	public void upgradeNetworkSpectrum( int newNumSpectralSlots) throws ExcecaoGrafo {
		this.upgradeSpectrumIntoNetwork(newNumSpectralSlots);;
		this.setNumLambdas(newNumSpectralSlots);
		this.setMascara(new Boolean[newNumSpectralSlots]);
		this.criaMascara();

	}
	

	public void createAllPairSourceDestinations () {
		int numNodes = this.getGrafo().getNos().tamanho();
		for (int i = 1 ; i <= numNodes; i++) {

			for (int k = 1 ; k <= numNodes ; k++) {
				if (k != i) {
					String key = i+"-"+k;
					if (!this.pairSourceDestinationDistribution.containsKey(key)) {
						this.pairSourceDestinationDistribution.put(key,new Integer(0));
					}
				}
			}
		}
	}

	public HashMap<String, Integer> getPairSourceDestinationDistribution() {
		return pairSourceDestinationDistribution;
	}
	
	

	

	/**
	 * Returns the total amount of network cost
	 * @return a double value
	 */
	public double getNetworkCost() {
		return this.networkCost;
	}

	/**
	 * Sets the total amount of network cost
	 * @param networkCost a double value
	 */
	public void setNetworkCost(double networkCost) {
		this.networkCost = networkCost;
	}
	
	
	public int checkTransparentReach ( Caminho path, ResourceAllocation resource) {

		StringBuffer logMessage = new StringBuffer();
		int numberOfRegenerators = 0;
			OSNR_Manager manager = new OSNR_Manager();
			double distanceSum = 0;
			boolean regeneratedSignal = false;
			Iterator<Enlace> it = path.getEnlaces().valores().iterator();
			String lastRegeneratedLink = "";
			Enlace edge = null;
			boolean next = true;
			for( ; next ;){
				if (!regeneratedSignal) { // used to doesn't forward to next edge if there is regeneration between actual nodes
					edge = it.next();//the edge in the path.
				} 
				regeneratedSignal = false;
				double edgeLength = edge.getDistancia()*Math.pow(10, 3);
//				System.out.println("Link: " + edge + ", distance:  " + edgeLength/1000 + "km");	
				logMessage.append("Link: " + edge + ", distance:  " + edgeLength/1000 + "km\n");
				double nSpan = Math.ceil(edgeLength/spanLength);
//				System.out.println("number of spans: " + nSpan);
				logMessage.append("number of spans: " + nSpan+"\n");
				StringBuilder spansData = new StringBuilder();
				double spanLengthTemp = 0.0;
				double distanceSumTemp = 0.0;
				for (int n = 1 ; n <= nSpan; n++) {
					if (n < nSpan) { 
						spanLengthTemp = spanLength;
					} else { 
						spanLengthTemp = edgeLength - (n-1)*spanLength ;
					}
					distanceSum+=spanLengthTemp;
					distanceSumTemp+=spanLengthTemp;
					OSNR_Parameters param = new OSNR_Parameters();
					param.setAmplifierNoiseFigure(amplifierNoiseFigure);
					param.setBandwidthNoise(bandwidthNoise);
					param.setWavelength(wavelength);
					param.setBandwidthNoise(bandwidthNoise);
					param.setBeta2(beta2);
					param.setGama(gama);
					param.setPowerGain(Converters.dBm2milliWatts(powerGaindBm)*Math.pow(10, -3)/symbolRate);
					param.setSpanLength(spanLengthTemp);
					param.setAlpha(alpha);
					param.setNumberOfChannels(numberOfChannels);
					OSNR_Span spanOSNR = new OSNR_Span(param, Type.SPAN);
					manager.add(spanOSNR);

					double osnrRxTemp = manager.getOSNR_Rx_dB();
					
					if (osnrRxTemp < osnrThreshold) {
						manager = new OSNR_Manager();
						numberOfRegenerators++;
//						System.out.println("Regeneration 3R was applied! Low OSNR: " + osnrRxTemp);
						logMessage.append("Detected low OSNR at node "+edge.getNoDireita()+": " + osnrRxTemp + " dB in link " 
								+ edge.getId() + "\nRegeneration 3R was applied!\n\n");
						regeneratedSignal = true;
						distanceSum = distanceSum - distanceSumTemp;

						if (lastRegeneratedLink.equals(edge.getId())) {
							return -1;
						}

						lastRegeneratedLink = edge.getId();
						break;
					}
					spansData.append(distanceSum+","+osnrRxTemp+"\n");
//					System.out.printf("Link "+ edge+": span "+n+", length = "+spanLengthTemp/1000+" km, OSNR = %.4f dB, \n", osnrRxTemp );
					logMessage.append("Link "+ edge+": span "+n+", length = "+spanLengthTemp/1000+String.format(" km, OSNR = %.4f dB, \n", osnrRxTemp ));

				}
				
				if (!regeneratedSignal) {
//					printer.print(spansData.toString());			
					OSNR_Node node = new OSNR_Node(lossNode, paramOSNR, Type.NODE);
					manager.add(node);
					double osnrRxTemp = manager.getOSNR_Rx_dB();
//					System.out.printf("Node "+ edge.getNoDireita()+": OSNR_Rx in node "+edge.getNoDireita()+" = %.4f dB \n\n", osnrRxTemp );
					logMessage.append("Node "+ edge.getNoDireita()+String.format(": OSNR_Rx = %.4f dB \n\n", osnrRxTemp ));
					next = it.hasNext();
				}
				
			}

			double osnrRx = manager.getOSNR_Rx_dB();
			
			String msgLog = "Graph: " + getGrafo().getName()+"\n"
			                + resource + "; Path: " + path + ", distance = "+ path.getDistancia() 
			                + ", OSNR_Rx Total in Rx = " + String.format("%.4f", osnrRx) + ", dB\n\n";

			if (isNewDebug) {
				logMessage.append(msgLog);
				log.info(logMessage.toString());
			} /*else {
				log.info(msgLog.toString());
			}*/
			
			
			
			
		
		
		return numberOfRegenerators;

	}
	
	public void initParameters () {
		paramOSNR.setAmplifierNoiseFigure(amplifierNoiseFigure);
		paramOSNR.setBandwidthNoise(bandwidthNoise);
		paramOSNR.setWavelength(wavelength);
		paramOSNR.setBandwidthNoise(bandwidthNoise);
		paramOSNR.setBeta2(beta2);
		paramOSNR.setGama(gama);
		paramOSNR.setPowerGain(Converters.dBm2milliWatts(powerGaindBm)*Math.pow(10, -3)/symbolRate);
		paramOSNR.setSpanLength(spanLength);
		paramOSNR.setAlpha(alpha);
		paramOSNR.setNumberOfChannels(numberOfChannels);
	}

	public String printParametets () {
		StringBuilder builder = new StringBuilder();
		formatter.applyPattern("#0.000E0");

		builder.append("Parameters:").append("\n");
		builder.append("\tBandwidth Noise: " + formatter.format(bandwidthNoise) + " Hz").append("\n");
		builder.append("\tSymbol Rate: " + formatter.format(symbolRate)+ " Hz").append("\n");
		builder.append("\tSpan length: " + formatter.format(spanLength)+ " m").append("\n");
		builder.append("\tWavelength: " + formatter.format(wavelength)+ " nm").append("\n");
		builder.append("\tAlpha: " + formatter.format(alpha)+ " dB/km").append("\n");
		builder.append("\tBeta2: " + formatter.format(beta2)+ " s^2/m").append("\n");
		builder.append("\tGama: " + formatter.format(gama)+ " W^-1/m").append("\n");
		formatter.applyPattern("0.0");
		builder.append("\tLaunch Power: " + formatter.format(powerGaindBm)+ " dBm").append("\n");
		builder.append("\tEDFA Noise Figure: " + formatter.format(amplifierNoiseFigure)+ " dB").append("\n");
		builder.append("\tWSS insertion loss: " + formatter.format(lossNode)+ " dB").append("\n");

		return builder.toString();
	}
	
	/**
	 * Calculates the total length of the graph
	 * @param graph
	 * @return
	 */
	public double getTotalNetworkLength () {
		double totalLength = 0.0;
		
		for( Iterator<Enlace> it = this.getGrafo().getEnlaces().valores().iterator() ; it.hasNext() ; ){
			Enlace edge =  it.next();
			totalLength += edge.getDistancia()*Math.pow(10, 3);
		}
		
		return totalLength;
	}
	
	/**
	 * Calculates the number of EDFAs in a network
	 * @param spanLength
	 * @param graph
	 * @return
	 */
	public double getNumberOfEDFAs(double spanLength) {
		double sum = 0.0;
		
		for( Iterator<Enlace> it = this.getGrafo().getEnlaces().valores().iterator() ; it.hasNext() ; ){
			Enlace edge =  it.next();
			sum += (Math.ceil(edge.getDistancia()*Math.pow(10, 3)/spanLength) - 1 + 1);// plus one for pre and booster amplifiers
		}
	
		double numEDFAs = sum ;
		return numEDFAs;
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

	/**
	 * Returns the number of transceivers that have been activated in the network
	 * @return
	 */
	public int getNumTransceivers() {
		return numTransceivers;
	}

	/**
	 * Set the number of transceivers that have been activated in the network
	 * @param numTransceivers
	 */
	public void setNumTransceivers(int numTransceivers) {
		this.numTransceivers = numTransceivers;
	}


	/**
	 * Returns the length of the spans
	 * @return
	 */
	public double getSpanLength() {
		return spanLength;
	}

	/**
	 * Set the length of the spans
	 * @param spanLength
	 */
	public void setSpanLength(double spanLength) {
		this.spanLength = spanLength;
	}

	/**
	 * Returns the minimum value of the OSNR threshold needed at the receiver
	 * @return
	 */
	public double getOsnrThreshold() {
		return osnrThreshold;
	}

	/**
	 * Set the minimum value of the OSNR threshold needed at the receiver
	 * @param osnrThreshold
	 */
	public void setOsnrThreshold(double osnrThreshold) {
		this.osnrThreshold = osnrThreshold;
	}

	/**
	 * Returns the nodal loss in dB
	 * @return
	 */
	public double getLossNode() {
		return lossNode;
	}

	/**
	 * Sets the nodal loss in dB
	 * @param lossNode
	 */
	public void setLossNode(double lossNode) {
		this.lossNode = lossNode;
	}

	public int getNumOfRegenerators() {
		return numOfRegenerators;
	}

	public void setNumOfRegenerators(int numOfRegenerators) {
		this.numOfRegenerators = numOfRegenerators;
	}


}
