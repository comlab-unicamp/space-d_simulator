package opticalnetwork.controlplane;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import algorithm.AlgorithmType;
import algorithm.Dijkstra;
import event.Event;
import event.EventList;
import graph.Caminho;
import graph.Enlace;
import graph.Grafo;
import graph.No;
import opticalnetwork.elastica.rsa.RequestRSA;
import opticalnetwork.elastica.rsa.RequestRSA.RequestType;
import opticalnetwork.elastica.rssa.ResourceAllocation;
import opticalnetwork.rwa.Requisicao;

public class ControllerEON extends Controle implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private boolean firstAllocate = true;
	private int numberOfFiberPerCable = 1;
	private int numberOfDimensions = 1;
	@SuppressWarnings("unused")
	private int slotBitRate = 100;
	@SuppressWarnings("unused")
	private AlgorithmType alg;
	private HashMap<Enlace, Boolean[]> stateLinktable;
	private boolean bidirectional = false;
	private int inputNetworkLoad = 0;
	private ArrayList<Integer> inputNetworkLoadDataList;
	private int minimumSpectralSlotsPerCarrier = 4;


	/**, int numSpectrumSlots,int numberOfDimensions,int requestLimit, boolean isBidirectional){
	 * The Constructor
	 * @param graph the network topology
	 * @param numSpectrumSlots the number of spectrum slots.
	 * @param requestLimit the limit of requests in the simulation.
	 *
	 * */
	public ControllerEON(Grafo grafo, int numSpectralSlots, int numberOfDimensions, int requestLimit){
		super(numSpectralSlots, requestLimit);
		setGrafo(grafo);
		this.numberOfDimensions = numberOfDimensions;
		iniciaTablelaDeEstados();
		startStateTable(); //for multi-dimensinonal links
		inputNetworkLoadDataList = new ArrayList<Integer>() ;
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
	public ControllerEON(Grafo grafo, int numLambdas, int slotBitRate, int limiteDeRequisicoes,int numberOfFiberPerCable,
			int numberOfModes, AlgorithmType alg ){
		super(numLambdas, limiteDeRequisicoes);
		setGrafo(grafo);
		this.numberOfFiberPerCable = numberOfFiberPerCable;
		this.numberOfDimensions = numberOfModes;
		this.slotBitRate = slotBitRate;
		this.alg = alg;

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
			Enlace link = it.next();
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
	 * @return <code>true</code> if the number of slots specified was successfully installed.
	 */
	public Map<Boolean,Integer> installFrequencySlot(int numberOfSlots, Caminho path) {
		/*declares a boolean array to be used to save the link state spectrum*/
		Boolean[] maskSlotsAvailable = getMascara();
		//		boolean isSlotInstalled = false; not used
		//		HashMap<String, Boolean[]> linkStateTable = getTabelaDeEstados();
		LinkedHashMap<String, Boolean[]> linkStateTable = new LinkedHashMap<>();

		/*Gets the state of the spectrum in each direction of the each link on the path */
		for(Iterator<Enlace> it = path.getEnlaces().valores().iterator() ; it.hasNext() ;){
			Enlace link = it.next();
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


	@Override
	public EventList receberEvento(Event evento) throws Exception{

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
			Caminho path = null;
			/*sets the traffic pattern of the simulation: event time and holding time of the connection*/


			/*Gets a key with the numbers of source and destination*/
			String key = request.getKeySourceDestination();

			int numberOfSpectralSlots = request.getBandwidth().getSpectralSlots();
			int numberOfSpatialSlots = request.getBandwidth().getSpatialSlots();

			if(getTabelaDeRotas().containsKey(key)){
				path = getRota(key);
			} else {
				path = Dijkstra.getMenorCaminho(getGrafo(), request.getSource(), request.getDestination());
				getTabelaDeRotas().put(key, path);
			}

			switch (request.getType()){

			case SPACE_FIRST:
				this.setPadroesDeTrafego(evento.getTime(), request.getHoldingTime());
				int numCarrierSlots = this.minimumSpectralSlotsPerCarrier;
				ResourceAllocation resourceAllocation = installSpaceFirst(numCarrierSlots,numberOfSpatialSlots, path);
				int firstSlot = resourceAllocation.getFirstSlot() ;
				if ( firstSlot > -1) {

					request.setResource(resourceAllocation);
					incrementaCaminhosEstabelecidos();
					request.setType(RequestType.CLOSE_SPACE_FIRST);
					evento.setTempo(evento.getTime()+request.getHoldingTime());
					listaEventos.insert(evento);
					if(isBidirectional()){
						this.inputNetworkLoad += (2*numberOfSpatialSlots*path.getEnlaces().tamanho());
					} else {
						this.inputNetworkLoad += (numberOfSpatialSlots*path.getEnlaces().tamanho());
					}



				} else {
					incrementaBloqueios();
				}
				/*adds in inputNetworkLoad into the list */
				this.getInputNetworkLoadDataList().add(getInputNetworkLoad());

				break;


			case SPECTRUM_FIRST:
				this.setPadroesDeTrafego(evento.getTime(), request.getHoldingTime());
				resourceAllocation = installSpectrumFirst(numberOfSpectralSlots, path);

				//must to have only one dimension, so gets the 0 index in list of allocated dimensions

				firstSlot = resourceAllocation.getFirstSlot() ;
				if ( firstSlot > -1) {

					//					int dimension = resourceAllocation.getDimensionsAllocated().get(0) ;
					//					request.setFirstFrequencySlot(resourceAllocation.getFirstSlot());
					request.setResource(resourceAllocation);
					incrementaCaminhosEstabelecidos();
					request.setType(RequestType.CLOSE_SPECTRUM_FIRST);
					evento.setTempo(evento.getTime()+request.getHoldingTime());
					listaEventos.insert(evento);
					if(isBidirectional()){
						this.inputNetworkLoad += (2*numberOfSpectralSlots*path.getEnlaces().tamanho());
					} else {
						this.inputNetworkLoad += (numberOfSpectralSlots*path.getEnlaces().tamanho());
					}

				} else {
					incrementaBloqueios();
				}

				/*adds in inputNetworkLoad into the list */
				this.getInputNetworkLoadDataList().add(getInputNetworkLoad());

				break;


			case SPECTRUM_FIRST_FIT_SLIGHTLY:
				this.setPadroesDeTrafego(evento.getTime(), request.getHoldingTime());
				Map<Boolean, Integer> isSlotsInstalled = new HashMap<>();

				isSlotsInstalled = installFrequencySlot(numberOfSpectralSlots, path);
				int spectrumSlotPosition = -1;
				boolean wasInstalled = false;

				if (!isSlotsInstalled.isEmpty()) {
					for (Iterator<Boolean> it = isSlotsInstalled.keySet().iterator() ; it.hasNext() ; ){
						wasInstalled = it.next(); //gets a boolean with the state of installation
						if (wasInstalled) {
							if (isSlotsInstalled.get(wasInstalled) > -1) {
								//								System.out.println("Processed Request: " + request.getId() + ",FirstSlot: " + isSlotsInstalled.get(wasInstalled));
								spectrumSlotPosition = isSlotsInstalled.get(wasInstalled);
								request.setFirstFrequencySlot(spectrumSlotPosition);
								incrementaCaminhosEstabelecidos();
								request.setType(RequestType.CLOSE_REQUEST);
								evento.setTempo(evento.getTime()+request.getHoldingTime());
								listaEventos.insert(evento);
								if(isBidirectional()){
									this.inputNetworkLoad += (2*numberOfSpectralSlots*path.getEnlaces().tamanho());
								} else {
									this.inputNetworkLoad += (numberOfSpectralSlots*path.getEnlaces().tamanho());
								}
							}
						} else {
							incrementaBloqueios();
							break;
						}
					}

				}


				/*adds in inputNetworkLoad into the list */
				this.getInputNetworkLoadDataList().add(getInputNetworkLoad());


				break;

			case CLOSE_REQUEST:

				int firstSlotAllocated = request.getFirstFrequencySlot();


				/*Gets the state of the spectrum in each direction of the each link on the path */
				for(Iterator<Enlace> it = path.getEnlaces().valores().iterator() ; it.hasNext() ;){
					Enlace link = it.next();
					Enlace linkReverse = null;
					/*Save the state of the spectrum into LinkState Table of the link*/
					Boolean[] linkState = getTabelaDeEstados().get(link.getId());
					Boolean[] reverseLinkState = null;

					if (bidirectional) {
						No left = link.getNoEsquerda();
						No right = link.getNoDireita();
						linkReverse = getGrafo().getEnlace(right, left);
						reverseLinkState = getTabelaDeEstados().get(linkReverse.getId());
					}

					for (int i = firstSlotAllocated ; i < firstSlotAllocated+numberOfSpectralSlots ; i++){
						linkState[i] = true;
						if (bidirectional) {
							reverseLinkState[i] = true;//reverse link
						}
					}
				}
				if(isBidirectional()){
					this.inputNetworkLoad -= (2*numberOfSpectralSlots*path.getEnlaces().tamanho());
				} else {
					this.inputNetworkLoad -= (numberOfSpectralSlots*path.getEnlaces().tamanho());
				}
				break;

			case CLOSE_SPECTRUM_FIRST:

				firstSlot = request.getResource().getFirstSlot();

				//must to have only one dimension, so gets the 0 index in list of allocated dimensions
				int dimension = request.getResource().getDimensionsAllocated().get(0);

				for(Iterator<Enlace> it = path.getEnlaces().valores().iterator() ; it.hasNext() ;){
					Enlace link = it.next();
					Enlace linkReverse = null;
					Boolean[] stateArray = link.getStateSpectralArray(dimension);
					Boolean[] reverseLinkState = null;

					if (bidirectional) {
						No left = link.getNoEsquerda();
						No right = link.getNoDireita();
						linkReverse = getGrafo().getEnlace(right, left);
						reverseLinkState = linkReverse.getStateSpectralArray(dimension);
					}

					for (int i = firstSlot ; i < firstSlot+numberOfSpectralSlots ; i++){
						stateArray[i]= true;
						if (bidirectional) {
							reverseLinkState[i] = true;//reverse link
						}
					}
				}
				if(isBidirectional()){
					this.inputNetworkLoad -= (2*numberOfSpectralSlots*path.getEnlaces().tamanho());
				} else {
					this.inputNetworkLoad -= (numberOfSpectralSlots*path.getEnlaces().tamanho());
				}


				break;

			case CLOSE_SPACE_FIRST:

				removeAllocatedResources(this.minimumSpectralSlotsPerCarrier, request.getResource(), path);


				if(isBidirectional()){
					this.inputNetworkLoad -= (2*numberOfSpatialSlots*path.getEnlaces().tamanho());
				} else {
					this.inputNetworkLoad -= (numberOfSpatialSlots*path.getEnlaces().tamanho());
				}


				break;

			default:
				break;
			}

			break;

		case NEW_REQUEST:

			Requisicao requisicao = (Requisicao)evento.getConteudo();

			//			CaminhoOptico caminhoOptico = null;
			Caminho rota = null;

			switch (requisicao.getTipo()){


			case ABRIR_CONEXAO:

				setPadroesDeTrafego(evento.getTime(), requisicao.getDuracao());

				Boolean[] estado = null; //estado do enlace
				Boolean[] mascara = getMascara();

				String chave = requisicao.getChaveOrigemDestino();
				if(isUsarRotasExplicitas()){
					rota = getRota(chave);
				} else if(getTabelaDeRotas().containsKey(chave)){
					rota = getRota(chave);
				}else{
					rota = Dijkstra.getMenorCaminho(getGrafo(), requisicao.getOrigem(), requisicao.getDestino());
					getTabelaDeRotas().put(chave, rota);
				}


				/* Verifica os estados dos enlaces da rota */
				for(Enlace e : rota.getEnlaces().valores()){

					estado = getTabelaDeEstados().get(e.getId()).clone();

					/* Para cada posic�o da m�scara, faz o "ou" l�gico o estado do enlace*/
					for (int i = 0 ; i < mascara.length ; i++){
						mascara[i] = mascara[i] && estado[i];
					}

				}
				//
				int lambda = -1;

				/* Pega o primeiro lambda com estado de utiliza��o "false" */
				for(int i = 0 ; i < mascara.length ; i++){
					if(mascara[i]){
						lambda = i;
						break;
					}
				}


				/* Se o Lambda n�o foi modificado, ent�o bloqueia a conex�o*/
				if(lambda == -1){
					incrementaBloqueios();
					break;
				}

				/* Atribui o estado de utiliza��o True em cada lambda dos enlaces da rota */
				for(Enlace e : rota.getEnlaces().valores()){
					estado = getTabelaDeEstados().get(e.getId());
					estado[lambda] = false;
				}

				incrementaCaminhosEstabelecidos();

				//caminhoOptico = new CaminhoOptico(rota,lambda);
				//this.tabelaDeCaminhosOpticos.put(requisicao.getIdRequisicao(), caminhoOptico);

				requisicao.setTipo(Requisicao.Tipo.FECHAR_CONEXAO);
				requisicao.setLambda(lambda);
				//				listaEventos.inserir(getGerador().criaEvento((getGerador().getTempoProximo() + requisicao.getDuracao()), requisicao));
				evento.setTempo(evento.getTime()+requisicao.getDuracao());
				listaEventos.insert(evento);
				break;

			case FECHAR_CONEXAO:
				//				int id = requisicao.getIdRequisicao();
				Caminho caminho = getRota(requisicao.getChaveOrigemDestino());
				estado = null;
				lambda = requisicao.getLambda();

				for(Enlace e : caminho.getEnlaces().valores()){
					estado = getTabelaDeEstados().get(e.getId());
					//					System.out.println(e.getId());
					//					mostrarEstado(estado);

					estado[lambda] = true;
					//					mostrarEstado(estado);


				}

				break;

			}

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
	public ResourceAllocation installSpectrumFirst(int numberOfSlots, Caminho path) {

		HashMap<Integer, Boolean[]> maskStateTable = getMaskStateTable(path);

		int dimension = -1;
		int firstSlot = -1;

		for(Iterator<Enlace> it = path.getEnlaces().valores().iterator() ; it.hasNext() ;){
			Enlace link = it.next();
			Enlace linkReverse = null;
			if (bidirectional) {
				No left = link.getNoEsquerda();
				No right = link.getNoDireita();
				linkReverse = getGrafo().getEnlace(right, left);
			}
			/*	testes if there is a continuous void range of slots in the first dimension, moving to the highest
			 * 	dimension if there isn't */
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
	private ResourceAllocation installSpaceFirst(int numCarrierSlots, int numberOfSpatialSlots, Caminho path) {
		HashMap<Integer, Boolean[]> maskStateTable = getMaskStateTable(path);

		ResourceAllocation resource = getPositionAndDimensionSpaceFirst(0, numberOfSpatialSlots, numCarrierSlots, maskStateTable);
		int position = resource.getFirstSlot();

		if (position > -1) {

			for(Iterator<Enlace> it = path.getEnlaces().valores().iterator() ; it.hasNext() ;){
				Enlace link = it.next();
				Enlace linkReverse = null;
				if (bidirectional) {
					No left = link.getNoEsquerda();
					No right = link.getNoDireita();
					linkReverse = getGrafo().getEnlace(right, left);
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
					if(section[j]){
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

	public void removeAllocatedResources (int numberOfSpectralSlots, ResourceAllocation resource, Caminho path) {

		int firstSlot = resource.getFirstSlot();

		for(Iterator<Enlace> it = path.getEnlaces().valores().iterator() ; it.hasNext() ;){
			Enlace link = it.next();
			Enlace linkReverse = null;
			Boolean[] stateArray = null;
			Boolean[] reverseLinkState = null;

			if (bidirectional) {
				No left = link.getNoEsquerda();
				No right = link.getNoDireita();
				linkReverse = getGrafo().getEnlace(right, left);
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






}
