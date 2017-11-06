/**
 *
 */
package opticalnetwork.elastica.rsa;

import topology.NetworkTopology;
import util.Counter;
import distribution.Distribution;
import distribution.Uniforme;
import event.SourceGenerator;
import event.Event.Type;
import graph.Caminho;
import graph.Grafo;
import graph.No;
import opticalnetwork.elastica.rsa.RequestRSA.RequestType;
import opticalnetwork.elastica.rssa.SwitchingType;


/**
 * Created in 24/08/2015
 * By @author Alaelson Jatoba
 * @version 1.0
 *
 * TrafficRSA class represents a demand to be used in an elastic optical network.
 *
 */
public class TrafficRSA implements SourceGenerator{

	private No source;
	private No destination;
	private Caminho path;
	private Bandwidth bandwidth;
	protected Uniforme distUniform;
	private Grafo graph;
	private Type eventType;
	protected int stop;
	Counter counter;
	Distribution dist;
	UniformBandwidthGenerator bwGenerator;
	RequestType requestType;
	private boolean isFixedBandwidth = false;
	private int numKShortestPaths = 1;
	private SwitchingType switchingType;


	/**
	 * The constructor
	 * @param source is the source node.
	 * @param destination is the destination node.
	 * @param stop the limit of requests to be genereted
	 * @param dist a statically distribution used do get the service time.
	 */
	public TrafficRSA(int seed, Grafo graph, int stop, Distribution dist) {
//		this.source = path.getOrigem();
//		this.destination = path.getDestino();
//		this.bandwidth = numberBwOption;
		this.distUniform = new Uniforme(seed);
		this.graph = graph;
		this.eventType = Type.REQUEST_RSA;
		this.stop = stop;
		this.dist = dist;
		bwGenerator = new UniformBandwidthGenerator(seed+1);
		this.counter = new Counter();
	}

	/**
	 * The constructor
	 * @param source is the source node.
	 * @param destination is the destination node.
	 * @param path contains all nodes in the path from source to destination.
	 * @param stop the limit of requests to be genereted
	 * @param dist a statically distribution used do get the service time.
	 */
	public TrafficRSA(int seed, Caminho path, Grafo graph, int stop, Distribution dist) {
		this.source = path.getOrigem();
		this.destination = path.getDestino();
		this.path = path;
//		this.bandwidth = bandwidth;
		this.distUniform = new Uniforme(seed);
		this.graph = graph;
		this.eventType = Type.REQUEST_RSA;
		this.stop = stop;
		this.dist = dist;
	}

	/**
	 * The constructor
	 * @param source is the source node.
	 * @param destination is the destination node.
	 * @param path contains all nodes in the path from source to destination.
	 * @param stop the limit of requests to be genereted
	 * @param dist a statically distribution used do get the service time.
	 * @param isFixedBandwidht to keep the bandwidth fixed
	 * @param bandwidth to set the bandwidth
	 */
	public TrafficRSA(int seed, Grafo graph, int stop, Distribution dist, boolean isFixedBandwidht, Bandwidth bandwidth) {
		this(seed,graph,stop,dist);
		this.isFixedBandwidth = isFixedBandwidht;
		this.bandwidth = bandwidth;

	}


	/**
	 * @return the source
	 */
	public No getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(No source) {
		this.source = source;
	}

	/**
	 * @return the destination
	 */
	public No getDestination() {
		return destination;
	}

	/**
	 * @param destination the destination to set
	 */
	public void setDestination(No destination) {
		this.destination = destination;
	}

	/**
	 * @return the path
	 */
	public Caminho getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(Caminho path) {
		this.path = path;
	}

	/*
	 * Returns the a new request between a pair of nodes.
	 * The nodes are sorted by Uniform distribution.
	 * @return RequestRSA a {@link RequestRSA} object.
	 *
	public RequestRSA getNewRequestWithRamdomNodes(int bandwidth) {

		Integer no1 = distUniform.sorteiaNo(graph.getNos().tamanho());
		Integer no2 = distUniform.sorteiaNo(graph.getNos().tamanho(), no1);
		No origem = graph.getNo(no1.toString());
		No destino = graph.getNo(no2.toString());
		RequestRSA req = new RequestRSA(origem, destino, bandwidth);
		return req;
	}*/

	/**
	 * Returns the a new request between a pair of nodes.
	 * The nodes are sorted by Uniform distribution.
	 * @return RequestRSA a {@link RequestRSA} object.
	 * */
	public RequestRSA getNewRequest() {
		
		int numNodes = graph.getNetwork().equals(NetworkTopology.US_NETWORK_17) ? graph.getSize() : graph.getNos().tamanho(); 

		Integer no1 = distUniform.sorteiaNo(numNodes);
		Integer no2 = distUniform.sorteiaNo(numNodes, no1);
		No origem = graph.getNo(no1.toString());
		No destino = graph.getNo(no2.toString());
		RequestRSA req = null;
		if(counter.getValue()<stop){
			counter.increment();

			if(isFixedBandwidth) {
				req = new RequestRSA(this.counter.getValue(), origem, destino,this.bandwidth, dist.getHoldingTime(), getRequestType());
				req.setSwitchingType(switchingType);
			} else {
				req = new RequestRSA(this.counter.getValue(), origem, destino, bwGenerator.getNextBandwidth(), dist.getHoldingTime(),getRequestType());
				req.setSwitchingType(switchingType);
			}
			req.setKshortestPaths(numKShortestPaths);
			return req;
		}
		

		return null;
	}
	
	/**
	 * Returns the a new request between a pair of nodes.
	 * The nodes are sorted by Uniform distribution.
	 * @return RequestRSA a {@link RequestRSA} object.
	 * */
	public RequestRSA getRequest() {

		Integer no1 = distUniform.sorteiaNo(graph.getNos().tamanho());
		Integer no2 = distUniform.sorteiaNo(graph.getNos().tamanho(), no1);
		No origem = graph.getNo(no1.toString());
		No destino = graph.getNo(no2.toString());
		RequestRSA req = null;
		
		if(isFixedBandwidth) {
			req = new RequestRSA(this.counter.getValue(), origem, destino,this.bandwidth, dist.getHoldingTime(), getRequestType());
			req.setSwitchingType(switchingType);
		} else {
			req = new RequestRSA(this.counter.getValue(), origem, destino, bwGenerator.getNextBandwidth(), dist.getHoldingTime(),getRequestType());
			req.setSwitchingType(switchingType);
		}
		counter.increment();
		req.setKshortestPaths(numKShortestPaths);
		return req;
	}

	/**
	 * Returns the type of event in the simulator
	 * */
	@Override
	public Type getEventType() {
		return eventType;
	}

	/** Returns the RequestRSA as a Java Object to be used with the Escalonador class in the original simulator
	 * Use a cast to recover the RequestRSA.
	 * Call the <code>getNewRequest()</code> method.
	 * @see event.SourceGenerator#getContent()
	 *
	 */
	@Override
	public Object getContent() {
		if (stop==0){
			return getRequest();
		} else {
			return getNewRequest();
		}
		
	}

	/**
	 * @return the counter
	 */
	public Counter getCounter() {
		return counter;
	}

	/**
	 * @param counter the counter to set
	 */
	public void setCounter(Counter counter) {
		this.counter = counter;
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
	 * @return the numKShortestPaths
	 */
	public int getNumKShortestPaths() {
		return this.numKShortestPaths;
	}


	/**
	 * @param numKShortestPaths the numKShortestPaths to set
	 */
	public void setNumKShortestPaths(int numKShortestPaths) {
		this.numKShortestPaths = numKShortestPaths;
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

	/**
	 * @return the graph
	 */
	public Grafo getGraph() {
		return graph;
	}

	/**
	 * @param graph the graph to set
	 */
	public void setGraph(Grafo graph) {
		this.graph = graph;
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
	 * @return the stop
	 */
	public int getStop() {
		return stop;
	}

	/**
	 * @param stop the stop to set
	 */
	public void setStop(int stop) {
		this.stop = stop;
	}

	@Override
	public void setEventType(Type eventType) {
		this.eventType = eventType;
		
	}

	



}
