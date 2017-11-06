package opticalnetwork.controlplane;

import graph.AbstractGrafo;
import graph.Caminho;
import graph.Enlace;
import graph.ExcecaoGrafo;
import graph.Grafo;
import graph.No;
import opticalnetwork.LinkStateTable;
import opticalnetwork.elastica.rsa.RequestRSA;
import opticalnetwork.elastica.rsa.RequestRSA.RequestType;
import opticalnetwork.elastica.rssa.ResourceAllocation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import algorithm.AlgorithmType;
import algorithm.Dijkstra;
import algorithm.KShortestPathFirstInterface;
import algorithm.KShortestPathList;
import algorithm.YenTopKShortestBasedAU;
import util.Counter;
import event.Event;
import event.EventList;

public class KShortestPathEONController extends Controle implements Serializable{

	/**
	 *
	 */
	public static Logger log = Logger.getLogger("ReproducingLog.log");
	private static final long serialVersionUID = 1L;
	private int numberOfFiberPerCable = 1;
	private int numberOfDimensions = 1;
	private HashMap<Enlace, Boolean[]> stateLinktable;
	private boolean bidirectional = false;
	private int inputNetworkLoad = 0;
	private ArrayList<Integer> inputNetworkLoadDataList;
	private int minimumSpectralSlotsPerCarrier = 4;
	private HashMap<Integer, Caminho> requestPathTable;
	//private AbstractGrafo modGraph;
	boolean debug = false;
	private double totalInputNetworkLoad;
	private double averageInputNetworkLoad;
	private int steadyState=10000;
	private Counter simCounter;
	KShortestPathFirstInterface kSPF;


	/**, int numSpectrumSlots,int numberOfDimensions,int requestLimit, boolean isBidirectional){
	 * The Constructor
	 * @param graph the network topology
	 * @param numSpectrumSlots the number of spectrum slots.
	 * @param requestLimit the limit of requests in the simulation.
	 *
	 * */
	public KShortestPathEONController(Grafo grafo, int numSpectralSlots, int numberOfDimensions, int requestLimit){
		super(numSpectralSlots, requestLimit);
		setGrafo(grafo);
		this.numberOfDimensions = numberOfDimensions;
		iniciaTablelaDeEstados();
		startStateTable(); //for multi-dimensinonal links
		inputNetworkLoadDataList = new ArrayList<Integer>() ;
		requestPathTable = new HashMap<>();
		//this.modGraph = grafo.clone();
		kSPF = new YenTopKShortestBasedAU(grafo);
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
	public KShortestPathEONController(Grafo grafo, int numLambdas, int slotBitRate, int limiteDeRequisicoes,int numberOfFiberPerCable,
			int numberOfModes, AlgorithmType alg ){
		super(numLambdas, limiteDeRequisicoes);
		setGrafo(grafo);
		this.numberOfFiberPerCable = numberOfFiberPerCable;
		this.numberOfDimensions = numberOfModes;
//		this.slotBitRate = slotBitRate;
//		this.alg = alg;

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
	 * @param numberOfTrue the number of <code>true</code> in sequence
	 * @param booleanArray a array of booleans
	 * */
	public int getPosition (int numberOfTrue, Boolean[] booleanArray) {
		Boolean[] mask = getMascara();

		/* get link state mask*/
		for (int i = 0 ; i < mask.length ; i++){
			mask[i] = mask[i] && booleanArray[i];
		}

		int seq = 0;
		int position = -1;
		/* gets the first slot with state "false" */
		for(int i = 0 ; i < mask.length ; i++){
			if (seq >= numberOfTrue) {
				break;
			}
			//			if (i >= 371) {																	//for debug
			if(mask[i]){
				seq++;
				if(position == -1) {
					position = i;
				}
			} else {
				if (seq < numberOfTrue){
					seq = 0;
					position = -1;
				} /*else {																//for debug
						System.out.println("position temp in loop: = " + position);			//for debug
					}*/																		//for debug
			}
			//			}																				//for debug
		}
		if (seq < numberOfTrue){
			seq = 0;
			position = -1;
		}
		//		System.out.println("position retorned: = " + position);								//for debug
		return position;
	}

	/**
	 * Returns a mask with the spectrum slot's states in each dimension in each link on the path;
	 * @param path the path between two nodes
	 * @return the state table's mask
	 */
	public HashMap<Integer, Boolean[]> getMaskStateTable (Caminho path) {
		//		LinkStateTable linkStateMask = new LinkStateTable();
		HashMap<Integer, Boolean[]> maskStateTable = new HashMap<>();
		for (int i = 0 ; i < numberOfDimensions ; i++ ) {
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

			for (int i = 0 ; i < numberOfDimensions ; i++ ) {
				Boolean[] maskSlotsAvailable = maskStateTable.get(i);
				linkSpectralArray = link.getStateSpectralArray(i);
				if (this.isBidirectional()) {
					reverseLinkSpectralArray = linkReverse.getStateSpectralArray(i);
				}
				for (int k = 0 ; k < maskSlotsAvailable.length ; k++){
					maskSlotsAvailable[k] = maskSlotsAvailable[k] && linkSpectralArray[k];
					if (this.isBidirectional()) {
						maskSlotsAvailable[k] = maskSlotsAvailable[k] && reverseLinkSpectralArray[k];
					}
				}
			}
		}

		return maskStateTable;

	}






	/**
	 * Installs the number of slots of the spectrum into each edge of the path
	 * @param numberOfSlots the number of slots to be installed into each link of the path.
	 * @param path the path where of resources will be installed.
	 * @return Map<Boolean, Integer> with resources successfully installed.
	 */
	public Map<Boolean, Integer> installFrequencySlot(int numberOfSlots, Caminho path) {
		/*declares a boolean array to be used to save the link state spectrum*/
		Boolean[] maskSlotsAvailable = getMascara();
		//		boolean isSlotInstalled = false; not used
		//		HashMap<String, Boolean[]> linkStateTable = getTabelaDeEstados();
		LinkedHashMap<String, Boolean[]> linkStateTable = new LinkedHashMap<>();

		/*Gets the state of the spectrum in each direction of the each link on the path */
		for(Iterator<Enlace> it = path.getEnlaces().valores().iterator() ; it.hasNext() ;){
			Enlace edge = it.next();//the edge in the path.
			//the link is the same edge in the graph, but could be a different object.
			Enlace link = getGrafo().getEnlace(edge.getId());
			Enlace linkReverse = null;
			/*Save the state of the spectrum into LinkState Table of the link*/
			Boolean[] linkState = getTabelaDeEstados().get(link.getId());
			Boolean[] reverseLinkState = null;
			linkStateTable.put(link.getId(),linkState);
			if (bidirectional) {
				No left = link.getNoEsquerda();
				No right = link.getNoDireita();
				linkReverse = getGrafo().getEnlace(right, left);
				reverseLinkState = getTabelaDeEstados().get(linkReverse.getId());
				linkStateTable.put(linkReverse.getId(),reverseLinkState);
			}

			for (int i = 0 ; i < maskSlotsAvailable.length ; i++){
				maskSlotsAvailable[i] = maskSlotsAvailable[i] && linkState[i];
				if (bidirectional) {
					maskSlotsAvailable[i] = maskSlotsAvailable[i] && reverseLinkState[i]; //reverse link
				}
			}
		}

		int firstSlot = -1;
		boolean installed = false;

		firstSlot = getPosition(numberOfSlots, maskSlotsAvailable);

		if (firstSlot > -1) {

			for(Iterator<String> it = linkStateTable.keySet().iterator() ; it.hasNext() ;){

				String directlinkKey = it.next();
				String reverselinkKey = null;
				if (bidirectional) {
					reverselinkKey = it.next();
				}


				int seq = 0;
				for (int i = 0 ; i < numberOfSlots ; i++) {
					if (linkStateTable.get(directlinkKey)[firstSlot+i] == true /*&& linkStateTable.get(reverselinkKey)[firstSlot+i] == true*/){
						linkStateTable.get(directlinkKey)[firstSlot+i]= false;
						if (bidirectional) {
							linkStateTable.get(reverselinkKey)[firstSlot+i] = false; //reverse link
						}

						seq = i;
						if (seq + 1 == numberOfSlots) {
							installed = true;

						}
					} else if (seq > 0) {
						for (int k = seq ; k >= 0 ; --k) {
							linkStateTable.get(directlinkKey)[firstSlot+k] = true;
							if (bidirectional) {
								linkStateTable.get(reverselinkKey)[firstSlot+k] = true; //reverse link disabled
							}
						}
						installed = false;
						break;

					}
				}
			}
		}

		Map<Boolean, Integer> returning = new HashMap<>();
		returning.put(installed, firstSlot);

		return returning;

	}

	/**
	 * @deprecated
	 * Installs a sequential set of frequency slots in a link. Return <code>true</code> if there are a sequential set of slot available.
	 * @param numberOfSlots the number of slots to be installed
	 * @param link the link
	 * */
	@Deprecated
	public boolean installFrequencySlot(int numberOfSlots, Enlace link, Boolean[] slotsAllocated) throws ExcecaoControle{
		Boolean[] linkState = getTabelaDeEstados().get(link.getId());

		int firstSlot = -1;

		/* if */
		if(!hasSlotsAvailable(numberOfSlots, linkState.clone())){

			return false;
		} else {
			//			System.out.println("entrou errado false: "+hasSlotsAvailable(numberOfSlots, linkState.clone()) );
			firstSlot = getPosition (numberOfSlots, linkState.clone()) ;
		}

		/* assignment "false" in each slot in link state*/
		for (int i = 0 ; i < numberOfSlots ; i++) {
			if (firstSlot+i > linkState.length - 1) {
				hasSlotsAvailable(numberOfSlots, linkState);
				throw new ExcecaoControle("The number of spectrall slots available is fewer than the number of requested slots, requested slots =" + numberOfSlots +
						", position = " + firstSlot + ",slot's index to install: " + i);
			}
			linkState[firstSlot+i] = false; //change the slot's status to busy;
			slotsAllocated[firstSlot+i] = false; //change the slot's status to busy;
		}

		return true;

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
	 * Returns the K disjoint shortest paths
	 * @param source the source node
	 * @param destination the destination node
	 * @param k the number of paths requested
	 * @return
	 * @throws ExcecaoGrafo
	 */
	public KShortestPathList getKshortestPathList (No source, No destination, int k) throws ExcecaoGrafo {
		KShortestPathList paths = new KShortestPathList();
		
		paths = kSPF.getKShortestPaths(this.getGrafo(), source, destination, k);
		return paths;
	}

	/**
	 * Handles the Space First Request
	 *
	 * @param event the event from scheduler
	 * @param request the request to handle
	 * @throws ExcecaoGrafo
	 */
	public synchronized void spaceFirstRequestHandle(Event event, RequestRSA request) throws ExcecaoGrafo {
//		int numberOfSpectralSlots = request.getBandwidth().getSpectralSlots();
		int numberOfSpatialSlots = request.getBandwidth().getSpatialSlots();
		int numCarrierSlots = this.minimumSpectralSlotsPerCarrier;
		//Clones the graph, nodes and edges
//		AbstractGrafo modGraph = getGrafo().clone();
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
		if (paths.size()==0) {
			throw new ExcecaoGrafo ("There is no enabled paths in modifiable graph!");
		}
		while (paths.size() > 0) {
			//Get the next shortest path
			Caminho path = paths.poll();
			resourceAllocation = installSpaceFirst(numCarrierSlots,numberOfSpatialSlots, path, request);
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
				requestPathTable.put(request.getId(), path);
				//Sets the request as close type
				request.setType(RequestType.CLOSE_SPACE_FIRST);
//				if (simCounter.getCounter() > this.steadyState) {
					//Counts the amount of spectrum occupation in the network
					if(isBidirectional()){
						this.inputNetworkLoad += (2*numberOfSpatialSlots*path.getEnlaces().tamanho());
					} else {
						this.inputNetworkLoad += (numberOfSpatialSlots*path.getEnlaces().tamanho());
					}
//				}
				//modGraph.enableLinks();
				break;
			} /*else {
				System.out.println("BLOCKED: " + path);
			}*/
		}

		if (!success) {
			if (debug) {
				System.out.println("R_ID: "+request.getId()+", CONNECTION WAS BLOCKED");
			}
			if (simCounter.getValue() > this.steadyState) {
				incrementaBloqueios();
			}
			//modGraph.enableLinks();
		} else {
			if (debug) {
				System.out.println("R_ID: "+request.getId()+", CONNECTION WAS SUCCESSFULLY INSTALLED");
			}
		}

		if (debug) {
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
	 */
	public void spectrumFirstRequestHandle(Event event, RequestRSA request) throws ExcecaoGrafo {
		//Gets the number of spectra slots required
		int numberOfSpectraSlots = request.getBandwidth().getSpectralSlots();
		//Gets the number of space slots required
//		int numberOfSpatialSlots = request.getBandwidth().getSpatialSlots();
		//Gets the number of slots per carrier
//		int numCarrierSlots = this.minimumSpectralSlotsPerCarrier;
		//Clones the graph, nodes and edges
//		AbstractGrafo modGraph = getGrafo().clone();
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
			//			Caminho path = paths.poll();
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
				requestPathTable.put(request.getId(), path);
				//Sets the request as close type
				request.setType(RequestType.CLOSE_SPECTRUM_FIRST);
//				if (simCounter.getCounter() > this.steadyState) {
					//Counts the amount of spectrum occupation in the network
					if(isBidirectional()){
						this.inputNetworkLoad += (2*numberOfSpectraSlots*path.getEnlaces().tamanho());
					} else {
						this.inputNetworkLoad += (numberOfSpectraSlots*path.getEnlaces().tamanho());
					}
//				}
				//modGraph.enableLinks();
				break;
			}
		}


		if (!success) {
			if (debug) {
				System.out.println("R_ID: "+request.getId()+", CONNECTION WAS BLOCKED");
//				Enlace link = getGrafo().getEnlace(source, destination);
//				System.out.println("R_ID: "+request.getId()+", LINK STATE:\n"+link.getLinkStateTable());
//				System.out.println(resourceAllocation);
//				LinkStateTable linkStateTableMask = new LinkStateTable();
//				linkStateTableMask.setStateTable(getMaskStateTable(path));
//				System.out.println("Mask: " + linkStateTableMask.toString());
//				for (int i = 0 ; i < numberOfDimensions ; i++) {
//					int pos = getPosition(numberOfSpectraSlots, linkStateTableMask.getStateSpectralArray(i));
//					System.out.println("POSITION: " + pos);
//				}
			}
			if (simCounter.getValue() > this.steadyState) {
				incrementaBloqueios();
			}
			//modGraph.enableLinks();
		} else {
			if (debug) {
				System.out.println("R_ID: "+request.getId()+", CONNECTION WAS SUCCESSFULLY INSTALLED");
			}
		}

		if (debug) {
			printStatistics(request, resourceAllocation);
		}
		/*adds in inputNetworkLoad into the list */
		this.getInputNetworkLoadDataList().add(getInputNetworkLoad());
	}

	public void spectrumFirstSlightlyModifiedHandle (Event event, RequestRSA request) throws ExcecaoGrafo {
		//Gets the number of spectra slots required
				int numberOfSpectraSlots = request.getBandwidth().getSpectralSlots();
				//Gets the number of space slots required
//				int numberOfSpatialSlots = request.getBandwidth().getSpatialSlots();
				//Gets the number of slots per carrier
//				int numCarrierSlots = this.minimumSpectralSlotsPerCarrier;
				//Clones the graph, nodes and edges
//				AbstractGrafo modGraph = getGrafo().clone();
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
						requestPathTable.put(request.getId(), path);
						//Sets the request as close type
						request.setType(RequestType.CLOSE_REQUEST);
						//Counts the amount of spectrum occupation in the network
//						if (simCounter.getCounter() > this.steadyState) {
							if(isBidirectional()){
								this.inputNetworkLoad += (2*numberOfSpectraSlots*path.getEnlaces().tamanho());
							} else {
								this.inputNetworkLoad += (numberOfSpectraSlots*path.getEnlaces().tamanho());
							}
//						}
						//modGraph.enableLinks();
						break;
					}

				}

				if (!success) {
					if (debug) {
						System.out.println("R_ID: "+request.getId()+", CONNECTION WAS BLOCKED");
					}
					if (simCounter.getValue() > this.steadyState) {
						incrementaBloqueios();
					}
					//modGraph.enableLinks();
				} else {
					if (debug) {
						System.out.println("R_ID: "+request.getId()+", CONNECTION WAS SUCCESSFULLY INSTALLED");
					}
				}

				if (debug) {
					printStatistics(request, resourceAllocation);
				}

		/*adds in inputNetworkLoad into the list */
		this.getInputNetworkLoadDataList().add(getInputNetworkLoad());
	}


	@Override
	public synchronized EventList receberEvento(Event evento) throws Exception{

		if(evento.getConteudo() != null){
			if(evento.getType()==null){
				throw new ExcecaoControle(evento.getTime()+" "+evento.getConteudo().toString());
			}
		} else {
			listaEventos.insert(null);
			return listaEventos;
		}

		switch (evento.getType()){

		case REQUEST_RSA:
			RequestRSA request = (RequestRSA) evento.getConteudo();



			int numberOfSpectralSlots = request.getBandwidth().getSpectralSlots();
			int numberOfSpatialSlots = request.getBandwidth().getSpatialSlots();

			switch (request.getType()){

			case SPACE_FIRST:
				/*sets the traffic pattern of the simulation: event time and holding time of the connection*/
				this.setPadroesDeTrafego(evento.getTime(), request.getHoldingTime());
				//Handles the request
				if(debug) {
					System.out.println("########\nDEBUG: ALGORITHM "+ request.getType().name() + ", REQUEST ID:" + request.getId()+
							", BANDWIDTH = " + request.getBandwidth().name());
				}
				spaceFirstRequestHandle(evento, request);

				break;

			case SPECTRUM_FIRST:
				/*sets the traffic pattern of the simulation: event time and holding time of the connection*/
				this.setPadroesDeTrafego(evento.getTime(), request.getHoldingTime());
				//Handles the request
				if(debug) {
					System.out.println("########\nDEBUG: ALGORITHM "+ request.getType().name() + ", REQUEST ID:" + request.getId()+
							", BANDWIDTH = " + request.getBandwidth().name());
				}
				spectrumFirstRequestHandle(evento, request);

				break;

			case SPECTRUM_FIRST_FIT_SLIGHTLY:
				/*sets the traffic pattern of the simulation: event time and holding time of the connection*/
				this.setPadroesDeTrafego(evento.getTime(), request.getHoldingTime());
				if(debug) {
					System.out.println("########\nDEBUG: ALGORITHM "+ request.getType().name() + ", REQUEST ID:" + request.getId()+
							", BANDWIDTH = " + request.getBandwidth().name());
				}
				//Handles the request
				spectrumFirstSlightlyModifiedHandle(evento, request);

				break;

			case CLOSE_REQUEST:

				Caminho pathSFF = requestPathTable.get(request.getId());
				if(debug) {
					System.out.println("########\nDEBUG: ALGORITHM "+ request.getType().name() + ", REQUEST ID:" + request.getId()+
							", BANDWIDTH = " + request.getBandwidth().name());
				}
				removeAllocatedResources(request.getBandwidth().getSpectralSlots(), request.getResource(), pathSFF,request);
//				if (simCounter.getCounter() > this.steadyState) {
					if(isBidirectional()){
						this.inputNetworkLoad -= (2*numberOfSpectralSlots*pathSFF.getEnlaces().tamanho());
					} else {
						this.inputNetworkLoad -= (numberOfSpectralSlots*pathSFF.getEnlaces().tamanho());
					}
//				}
				break;

			case CLOSE_SPECTRUM_FIRST:
				Caminho pathSpeF = requestPathTable.get(request.getId());
				if(debug) {
					System.out.println("########\nDEBUG: ALGORITHM "+ request.getType().name() + ", REQUEST ID:" + request.getId()+
							", BANDWIDTH = " + request.getBandwidth().name());
				}

				removeAllocatedResources(request.getBandwidth().getSpectralSlots(), request.getResource(), pathSpeF,request);
//				if (simCounter.getCounter() > this.steadyState) {
					if(isBidirectional()){
						this.inputNetworkLoad -= (2*numberOfSpectralSlots*pathSpeF.getEnlaces().tamanho());
					} else {
						this.inputNetworkLoad -= (numberOfSpectralSlots*pathSpeF.getEnlaces().tamanho());
					}
//				}


				break;

			case CLOSE_SPACE_FIRST:

				Caminho pathSpaF = requestPathTable.get(request.getId());
				if(debug) {
					System.out.println("########\nDEBUG: ALGORITHM "+ request.getType().name() + ", REQUEST ID:" + request.getId()+
							", BANDWIDTH = " + request.getBandwidth().name());
				}

				removeAllocatedResources(this.minimumSpectralSlotsPerCarrier, request.getResource(), pathSpaF, request);
//				if (simCounter.getCounter() > this.steadyState) {
					if(isBidirectional()){
						this.inputNetworkLoad -= (2*numberOfSpatialSlots*pathSpaF.getEnlaces().tamanho());
					} else {
						this.inputNetworkLoad -= (numberOfSpatialSlots*pathSpaF.getEnlaces().tamanho());
					}
//				}
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
	 */
	public ResourceAllocation installSpectrumFirst(int numberOfSlots, Caminho path, RequestRSA request) {

		HashMap<Integer, Boolean[]> maskStateTable = getMaskStateTable(path);

		int dimension = -1;
		int firstSlot = -1;

		for(Iterator<Enlace> it = path.getEnlaces().valores().iterator() ; it.hasNext() ;){
			Enlace edge = it.next();//the edge in the path.
			//the link is the same edge in the graph, but could be a different object.
			Enlace link = getGrafo().getEnlace(edge.getId());
			Enlace linkReverse = null;
			if (bidirectional) {
				No left = link.getNoEsquerda();
				No right = link.getNoDireita();
				linkReverse = getGrafo().getEnlace(right, left);
			}

			/*	testes if there is a continuous void range of slots in the first dimension, moving to the highest
			 * 	dimension if there isn't */
			if(debug) {

				System.out.println("R_ID: "+request.getId()+", Before install \n Link:"+link.getId() + ", num slots: " + numberOfSlots);
				System.out.println("R_ID: "+request.getId()+", LINK STATE:\n"+link.getLinkStateTable());
				if (bidirectional) {
					System.out.println("R_ID: "+request.getId()+", REVERSE LINK STATE ("+linkReverse.getId() + "):\n"+linkReverse.getLinkStateTable());
				}
			}
			for (int i = 0 ; i < numberOfDimensions ; i++ ) {
				/*gets the position in a row of the state table*/
				firstSlot = getPosition(numberOfSlots, maskStateTable.get(i));
				if (firstSlot > -1) {
					dimension = i; //gets the dimension's index
					//					int seq = 0;
					for (int k = 0 ; k < numberOfSlots ; k++) {
						if ( link.getStateSpectralArray(dimension)[firstSlot+k] == true ){
							link.getStateSpectralArray(dimension)[firstSlot+k]= false;

						}
						if (bidirectional) {
							if ( linkReverse.getStateSpectralArray(dimension)[firstSlot+k] == true ){
								linkReverse.getStateSpectralArray(dimension)[firstSlot+k]= false;//reverse link

							}
						}
					}
					break;
				}

			}
			if(debug) {
				System.out.println("R_ID: "+request.getId()+", After install \n Link:"+link.getId() + ", num slots: " + numberOfSlots);
				System.out.println("R_ID: "+request.getId()+", LINK STATE:\n"+link.getLinkStateTable());
				if (bidirectional) {
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
	 *
	 * Returns a {@link ResourceAllocation} object with the position and the dimensions where the resources have been installed.
	 * The position's index means place where starts the resource. The dimensions's index means the index of fibers or modes where
	 * there were available slots in the specified position to accommodate the demand.
	 * A {@link ResourceAllocation} with position = -1 means that the installation fails because the there were no available resource for the demand
	 *
	 * @param numCarrierSlots number of slots of a single optical carrier.
	 * @param numberOfSpatialSlots number of slots need in a spatial way.
	 * @param path the path with links between two nodes
	 * @return a {@link ResourceAllocation} object with the position and the dimensions where there are available resources.
	 */
	private ResourceAllocation installSpaceFirst(int numCarrierSlots, int numberOfSpatialSlots, Caminho path, RequestRSA request) {
		HashMap<Integer, Boolean[]> maskStateTable = getMaskStateTable(path);

		ResourceAllocation resource = getPositionAndDimensionSpaceFirst(0, numberOfSpatialSlots, numCarrierSlots, maskStateTable);
		int position = resource.getFirstSlot();

		if (position > -1) {

			for(Iterator<Enlace> it = path.getEnlaces().valores().iterator() ; it.hasNext() ;){
				Enlace edge = it.next();//the edge in the path.
				//the link is the same edge in the graph, but could be a different object.
				Enlace link = getGrafo().getEnlace(edge.getId());
				Enlace linkReverse = null;
				if (bidirectional) {
					No left = link.getNoEsquerda();
					No right = link.getNoDireita();
					linkReverse = getGrafo().getEnlace(right, left);
				}
				if(debug) {
					System.out.println("R_ID: "+request.getId()+", Before install \n Link:"+link.getId() + ", num slots: " + numberOfSpatialSlots);
					System.out.println("R_ID: "+request.getId()+", LINK STATE:\n"+link.getLinkStateTable());
					if (bidirectional) {
						System.out.println("R_ID: "+request.getId()+", REVERSE LINK STATE ("+linkReverse.getId() + "):\n"+linkReverse.getLinkStateTable());
					}
				}
				for (int dimension : resource.getDimensionsAllocated() ) { // loop for dimensions with available resources

					for (int k = 0 ; k < numCarrierSlots ; k++) { // loop for slots in the dimension with available resources
						if ( link.getStateSpectralArray(dimension)[position+k] == true ){
							link.getStateSpectralArray(dimension)[position+k]= false;
						}
						if (bidirectional) {
							if ( linkReverse.getStateSpectralArray(dimension)[position+k] == true ){
								linkReverse.getStateSpectralArray(dimension)[position+k]= false;//reverse link

							}
						}
					}
				}
				if(debug) {
					System.out.println("R_ID: "+request.getId()+", After install \n Link:"+link.getId() + ", num slots: " + numberOfSpatialSlots);
					System.out.println("R_ID: "+request.getId()+", LINK STATE:\n"+link.getLinkStateTable());
					if (bidirectional) {
						System.out.println("R_ID: "+request.getId()+", REVERSE LINK STATE ("+linkReverse.getId() + "):\n"+linkReverse.getLinkStateTable());
					}
				}
			}
		} else {
			if(debug) {
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



	/**
	 * @Deprecated
	 *
	 * Removes slots allocated from spectrum
	 * @param stateTable a HashMap with a collection of links that had slots allocated and it's freqSlotsAllocated
	 * */
	@Deprecated
	public void removeSpectrumAssignment(HashMap<Enlace, Boolean[]> stateTable){
		if (!stateTable.isEmpty()){
			for(Enlace e : stateTable.keySet()){
				/*the link state with the slots allocated on link*/
				Boolean[] linkState = getTabelaDeEstados().get(e.getId());
				Boolean[] freqSlotAllocated = stateTable.get(e);
				//				linkState = getTabelaDeEstados().get(e.getId()); duplicado!
				/* remove each frequency slot by XOR with the freqSlotAllocated that had been stored in Request */
				for (int i = 0 ; i < freqSlotAllocated.length ; i++){
					linkState[i] = !(freqSlotAllocated[i] ^ linkState[i]);
				}

			}
		}
	}

	public void removeAllocatedResources (int numberOfSpectralSlots, ResourceAllocation resource, Caminho path, RequestRSA request) {
		int firstSlot = resource.getFirstSlot();
		int slots = numberOfSpectralSlots*resource.getDimensionsAllocated().size();
		for(Iterator<Enlace> it = path.getEnlaces().valores().iterator() ; it.hasNext() ;){
			Enlace edge = it.next();//the edge in the path.
			//the link is the same edge in the graph, but could be a different object.
			Enlace link = getGrafo().getEnlace(edge.getId());
			Enlace linkReverse = null;
			Boolean[] stateArray = null;
			Boolean[] reverseLinkState = null;

			if (bidirectional) {
				No left = link.getNoEsquerda();
				No right = link.getNoDireita();
				linkReverse = getGrafo().getEnlace(right, left);
			}
			if(debug) {

				System.out.println("R_ID: "+request.getId()+", Before remove \n Link:"+link.getId() + ", num slots: " + slots);
				System.out.println("R_ID: "+request.getId()+", LINK STATE:\n"+link.getLinkStateTable());
				if (bidirectional) {
					System.out.println("R_ID: "+request.getId()+", REVERSE LINK STATE ("+linkReverse.getId() + "):\n"+linkReverse.getLinkStateTable());
				}
			}
			for (int dimension : resource.getDimensionsAllocated()) {
//				System.out.println("dimensions: " + resource.getDimensionsAllocated() + " i:" + dimension);
//				int dimension = resource.getDimensionsAllocated().get(i);
				stateArray = link.getStateSpectralArray(dimension);
				for (int j = firstSlot ; j < firstSlot + numberOfSpectralSlots ; j++) {
					stateArray[j]= true;
					if (bidirectional) {
						reverseLinkState = linkReverse.getStateSpectralArray(dimension);
						reverseLinkState[j] = true;//reverse link
					}
				}
			}
			if(debug) {
				System.out.println("R_ID: "+request.getId()+", After remove \n Link:"+link.getId() + ", num slots: " + slots);
				System.out.println("R_ID: "+request.getId()+", LINK STATE:\n"+link.getLinkStateTable());
				if (bidirectional) {
					System.out.println("R_ID: "+request.getId()+", REVERSE LINK STATE ("+linkReverse.getId() + "):\n"+linkReverse.getLinkStateTable());
				}
				printStatistics(request, resource);
			}

		}

	}

	private void startStateTable(){
		for(Enlace e : getGrafo().getEnlaces().valores()){
			e.installStateTable(this.numberOfDimensions, getMascara());
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
	 * @return the stateLinktable
	 */
	public HashMap<Enlace, Boolean[]> getStateLinktable() {
		return stateLinktable;
	}

	/**
	 * @param stateLinktable the stateLinktable to set
	 */
	public void setStateLinktable(HashMap<Enlace, Boolean[]> stateLinktable) {
		this.stateLinktable = stateLinktable;
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
	 * @return the minimumSpatialSlotsPerCarrier
	 */
	public int getMinimumSpectralSlotsPerCarrier() {
		return minimumSpectralSlotsPerCarrier;
	}

	/**
	 * @param minimumSpectralSlotsPerCarrier the minimumSpatialSlotsPerCarrier to set
	 */
	public void setMinimumSpectralSlotsPerCarrier(int minimumSpectralSlotsPerCarrier) {
		this.minimumSpectralSlotsPerCarrier = minimumSpectralSlotsPerCarrier;
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
		averageInputNetworkLoad = inputNetworkLoad/this.totalInputNetworkLoad;
		return averageInputNetworkLoad;
	}

	/**
	 * @param averageInputNetworkLoad the averageInputNetworkLoad to set
	 */
	public void setAverageInputNetworkLoad(double averageInputNetworkLoad) {
		this.averageInputNetworkLoad = averageInputNetworkLoad;
	}

//	/**
//	 * @param averageInputNetworkLoad the averageInputNetworkLoad to set
//	 */
//	public void setAverageInputNetworkLoad(int inputNetworkLoad) {
//		averageInputNetworkLoad = inputNetworkLoad/this.totalInputNetworkLoad;
//	}

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






}
