package graph;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import edu.asu.emit.qyan.alg.model.Graph;
import edu.asu.emit.qyan.alg.model.VariableGraph;
import edu.asu.emit.qyan.alg.model.Vertex;
import edu.asu.emit.qyan.alg.model.abstracts.BaseVertex;
import opticalnetwork.EnlaceOptico;
import topology.NetworkTopology;

/**
 *@author Alaelson Jatoba
 *@version 1.0
 */

public abstract class AbstractGrafo extends VariableGraph implements Serializable{

	private static final long serialVersionUID = 1L;
	private static int ID = 0;
	private int id;
	private ListaEnlaces listaEnlaces;
	private ListaNos listaNos;
	private ListaNos routerNodeList;
	private NetworkTopology network;
	/** the number of nodes*/
	private int size;
	private int numberOfDimensions;
	private String name;

	public AbstractGrafo(){
		setEnlaces(new ListaEnlaces());
		listaNos = new ListaNos();
		this.routerNodeList = new ListaNos();
		this.network = null;
		this.setId(ID++);
	}
	
	

	public AbstractGrafo(NetworkTopology network){
		setEnlaces(new ListaEnlaces());
		listaNos = new ListaNos();
		this.routerNodeList = new ListaNos();
		this.network = network;
		this.setId(ID++);
	}

	/**
	 * retorna a lista de enlaces do grafo
	 * @return ListaEnlaces
	 * */
	public ListaEnlaces getEnlaces() {
		return listaEnlaces;
	}

	/**
	 * retorna a lista de nos de grafo
	 * @return ListNos
	 * */
	public ListaNos getNos() {
		return listaNos;
	}
	
	/**
	 * Gets the route node list
	 * @return routeNodeList
	 * */
	public ListaNos getRouteNodeList() {
		return this.routerNodeList;
	}

	/**
	 * adiciona um No no grafo
	 * @param no
	 * @throws ExcecaoGrafo
	 * */
	public void adicionarNo(No no) throws ExcecaoGrafo{
		listaNos.adicionarNo(no);
	}

	/**
	 * Verifica se um Nó está contido no grafo
	 * @param no
	 * @return true o Nó está contido no grafo;
	 * */
	public boolean contemNo(No no) {
		return listaNos.contem(no);
	}

	/**
	 * remover um No do grafo
	 * @param no
	 * @throws ExcecaoGrafo
	 * */
	public void removerNo(No no) throws ExcecaoGrafo {
		listaNos.remover(no);

	}
	
	/**
	 * add a route node into the list
	 * @param no
	 * @throws ExcecaoGrafo
	 * */
	public void addRouterNode(No no) throws ExcecaoGrafo{
		this.routerNodeList.adicionarNo(no);
	}

	/**
	 * check if there is a node in a route node list
	 * @param no
	 * @return true o Nó está contido no grafo;
	 * */
	public boolean containRouterNode(No no) {
		return this.routerNodeList.contem(no);
	}

	/**
	 * remove a node from the route node list
	 * @param no
	 * @throws ExcecaoGrafo
	 * */
	public void removeRouterNode(No no) throws ExcecaoGrafo {
		this.routerNodeList.remover(no);

	}



	/**
	 * retorna um enlace da lista de enlaces do grafo
	 * @return um enlace dados os nos origem e destino
	 * */
	public Enlace getEnlace(No esq, No dir) {
		return getEnlaces().getEnlace(esq, dir);
	}

	/**
	 * Returns a link by identification
	 * @param key The Link's Id
	 * */
	public Enlace getEnlace (String key) {
		return getEnlaces().getEnlaces().get(key);
	}
	/**
	 * adiciona um enlace de um Nó origem para um Nó destino
	 * no grafo
	 * @param esq Nó de origem
	 * @param dir Nó de destino
	 * @throws ExcecaoGrafo
	 * */
	public abstract void  adicionarEnlace(No esq, No dir) throws ExcecaoGrafo;

//	public void adicionarEnlace(String esq, String dir) throws ExcecaoGrafo{
//		if(getNo(esq)!=null && getNo(dir)!=null){
//			adicionarEnlace(getNo(esq), getNo(dir));
//		} else if(getNo(esq)==null){
//			adicionarNo(esq);
//		}
//	}
//
	public void adicionarEnlaceBidirecional(double distancia, No esq, No dir) throws ExcecaoGrafo{
		adicionarEnlace(distancia, esq, dir);
		adicionarEnlace(distancia, dir, esq);
	}

	public void adicionarEnlaceBidirecional(No esq, No dir) throws ExcecaoGrafo{
		adicionarEnlace( esq, dir);
		adicionarEnlace( dir, esq);
	}

	public void adicionarEnlace(double distancia, No esq, No dir)
			throws ExcecaoGrafo {
		Enlace enlace = new EnlaceOptico(distancia,esq,dir);
		esq.adicionarEnlace(enlace);
		getEnlaces().adicionarEnlace(enlace);


	}
	/**
	 * adiciona um enlace no grafo
	 * @param esq Nó de origem
	 * @param dir Nó de destino
	 * @param peso Peso deste enlace
	 * @throws ExcecaoGrafo
	 * */
	public abstract void adicionarEnlace(No esq, No dir, double peso) throws ExcecaoGrafo;

	/**
	 * Verifica se um  Enlace de um Nó origem para um Nó destino
	 * está contido no grafo
	 * @param esq Nó de origem
	 * @param dir Nó de destino
	 * @return true o enlace está contido no grafo;
	 *
	 * */
	public boolean contemEnlace(No esq, No dir) {
		return getEnlaces().contemEnlace(esq,dir);
	}

	/**
	 * Verifica se um enlace está contido no grafo
	 * @param enlace
	 * 	 * @return true o enlace está contido no grafo;
	 * */
	public boolean contemEnlace(Enlace enlace) {
		return getEnlaces().contemEnlace(enlace);
	}

	/**
	 * Retorna o peso de um enlace
	 * @param enlace
	 * @return
	 * */
	public double getPesoEnlace(Enlace enlace) {
		return enlace.getPeso();
	}

	/**
	 * Retorna o peso de um enlace dado o Nó oriem e o Nó destino
	 * @param esq
	 * @param dir
	 * @return peso do enlace
	 * */
	public double getPesoEnlace(No esq, No dir) {
		Enlace enlace = getEnlace(esq, dir);
		return enlace.getPeso();
	}

	/**
	 * Retorna o Nó de origem de um enlace
	 * @param enlace
	 * @return
	 * */
	public No getOrigemEnlace(Enlace enlace) {
		return enlace.getNoEsquerda();
	}

	/**
	 * Retorna o Nó de destino de um enlace
	 * @param enlace
	 * @return
	 * */
	public No getDestinoEnlace(Enlace enlace) {
		return enlace.getNoDireita();
	}

	/**
	 * Retorna a lista de enlaces do no
	 * @param enlace
	 * @return Lista de enlaces
	 * */
	public Map<String, Enlace> getEnlacesDo(No no) {
		return no.getEnlaces();
	}

	/**
	 * remover um enlace do grafo, dado um Nó origem para um Nó destino
	 * @param esq Nó de origem
	 * @param dir Nó de destino
	 * @throws ExcecaoGrafo
	 * */
	public void removerEnlace(No esq, No dir) throws ExcecaoGrafo {
		Enlace enlace = getEnlace(esq, dir);
		removerEnlace(enlace);
	}

	/**
	 * remover um enlace do grafo
	 * @param enlace
	 * @throws ExcecaoGrafo
	 * */
	public void removerEnlace(Enlace enlace) throws ExcecaoGrafo {
		getEnlaces().remover(enlace);

	}

	public void setEnlaces(ListaEnlaces listaEnlaces) {
		this.listaEnlaces = listaEnlaces;
	}
	public void setNos(ListaNos listaNos) {
		this.listaNos = listaNos;
	}
//	public ListaEnlaces getListaEnlaces() {
//		return listaEnlaces;
//	}

	public abstract void adicionarEnlace(Enlace enlace) throws ExcecaoGrafo ;

	public No getNo(String nome) {
		return listaNos.getNo(nome);
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * The size is the number of nodes
	 * @param size the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public abstract AbstractGrafo clone();


	/**
	 * Traverse all links in the graph. If just one is disabled returns <code>false</code>>
	 * @return a boolean <code>true</code> if all links is disabled.
	 *
	 * */
	public boolean hasDisabledLink () {
		for (Enlace e : getEnlaces().valores()) {
			if (!e.isAtivado()){
				return false;
			}
		}

		return true;
	}

	/**
	 * Traverse all links in the graph. If just one is enabled returns <code>true</code>>
	 * @return a boolean <code>false</code> if all links is disabled.
	 *
	 * */
	public boolean hasEnabledLink () {
		for (Enlace e : getEnlaces().valores()) {
			if (e.isAtivado()){
				return true;
			}
		}

		return false;
	}

	/**
	 * Disable all links in graph to not be usable in Dijkstra's shortest path algorithm
	 *
	 * */
	public void disableLinks () {
		for (Enlace e : getEnlaces().valores()) {
			e.setAtivado(false);
		}
	}

	/**
	 * Enable all links in graph to not be usable in Dijkstra's shortest path algorithm
	 *
	 * */
	public void enableLinks () {
		for (Enlace e : getEnlaces().valores()) {
			if (!e.isAtivado()) {
				e.setAtivado(true);
			}
		}
	}

	public No getNodeByName (String name) {
		for (Iterator<No> it = getNos().getIterator(); it.hasNext(); ) {
			No node = it.next();
			if (node.getName().equalsIgnoreCase(name)) {
				return	node;
			}
		}

		return null;
	}

	/**
	 * Returns a type of {@link NetworkTopology}
	 * @return the network
	 */
	public NetworkTopology getNetwork() {
		return network;
	}

	/**
	 * Sets a type of {@link NetworkTopology}
	 * @param network the topology to set
	 */
	public void setNetwork(NetworkTopology network) {
		this.network = network;
	}
	
	public synchronized void installModelGraph () {
		_vertex_num = this.getNos().tamanho() + 1;
		
		for(int i=0; i < _vertex_num; ++i)
		{
			BaseVertex vertex = new Vertex();
			_vertex_list.add(vertex);
			_id_vertex_index.put(vertex.get_id(), vertex);
		}
		
		for (Enlace e : getEnlaces().valores()) {
			int start_vertex_id = Integer.parseInt(e.getNoEsquerda().getId());
			int end_vertex_id = Integer.parseInt(e.getNoDireita().getId());
			double weight = e.getDistancia();
			add_edge(start_vertex_id, end_vertex_id, weight);
		}
		
		Vertex.reset();
		
	}
	
	public Graph getModelGraph () {
		Graph newGraph = new VariableGraph(this);
			
		return newGraph;
	}



	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}



	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
	 * Increases by 1 the number of dimensions  
	 */
	public void increaseNumberOfDimensions() {
		this.numberOfDimensions++;
	}



	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}



	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

 }
