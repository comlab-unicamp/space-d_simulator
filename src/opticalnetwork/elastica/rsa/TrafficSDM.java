/**
 *
 */
package opticalnetwork.elastica.rsa;

import graph.Grafo;
import graph.No;
import topology.NetworkTopology;

import java.util.List;

import distribution.Distribution;


/**
 * Created in 17/06/2016
 * By @author Alaelson Jatoba
 * @version 1.0
 *
 * TrafficSDM class represents a demand to be used in an elastic optical network for space division multiplexing.
 *
 */
public class TrafficSDM extends TrafficRSA{

	List<OpticalSuperChannel> opticalSuperChannelList;
	UniformOpticalSuperChannelGenerator opticalSuperChannelGen;
	private OpticalSuperChannel superChannel;

	/**
	 * The constructor
	 * @param source is the source node.
	 * @param destination is the destination node.
	 * @param stop the limit of requests to be genereted
	 * @param dist a statically distribution used do get the service time.
	 * @param opticalSuperChannel a super channel (see: {@link OpticalSuperChannel})
	 */
	public TrafficSDM(int seed, Grafo graph, int stop, Distribution dist, List<OpticalSuperChannel> opticalSuperChannelList) {
		super(seed, graph, stop, dist);
		this.opticalSuperChannelList = opticalSuperChannelList;
		this.opticalSuperChannelGen = new UniformOpticalSuperChannelGenerator(seed, opticalSuperChannelList);
	}
	
	/**
	 * The constructor
	 * @param source is the source node.
	 * @param destination is the destination node.
	 * @param stop the limit of requests to be genereted
	 * @param dist a statically distribution used do get the service time.
	 * @param opticalSuperChannelList a list of super channels (see: {@link OpticalSuperChannel})
	 */
	public TrafficSDM(int seed, Grafo graph, int stop, Distribution dist, OpticalSuperChannel opticalSuperChannel) {
		super(seed, graph, stop, dist);
//		this.opticalSuperChannelList = opticalSuperChannelList;
//		this.opticalSuperChannelGen = new UniformOpticalSuperChannelGenerator(seed, opticalSuperChannelList);
		this.superChannel = opticalSuperChannel;
		setFixedBandwidth(true);
	}
	
	

	/**
	 * Returns the a new request between a pair of nodes.
	 * The nodes are sorted by Uniform distribution.
	 * @return RequestRSA a {@link RequestRSA} object.
	 * */
	public RequestRSA getNewRequest() {

//		int numNodes = getGraph().getNetwork().equals(NetworkTopology.US_NETWORK_17) ? getGraph().getSize() : getGraph().getNos().tamanho();
		
		int numNodes = 0;
		
//		if (getGraph().getNetwork().equals(NetworkTopology.US_NETWORK_17) ) {
//			numNodes = getGraph().getSize();
//		} else {
			numNodes = getGraph().getNos().tamanho();
//		}

		Integer no1 = distUniform.sorteiaNo(numNodes);
		while (getGraph().containRouterNode(getGraph().getNo(no1.toString())) ) {
			no1 = distUniform.sorteiaNo(numNodes);
		}
		Integer no2 = distUniform.sorteiaNo(numNodes, no1);
		while (getGraph().containRouterNode(getGraph().getNo(no2.toString())) ) {
			no2 = distUniform.sorteiaNo(numNodes,no1);
		}
		No origem = getGraph().getNo(no1.toString());
		No destino = getGraph().getNo(no2.toString());
		RequestRSA req = null;
		if(counter.getValue()<stop){
			
			if(isFixedBandwidth()) {
				req = new RequestRSA(this.counter.getValue(), origem, destino,this.superChannel, dist.getHoldingTime(), getRequestType());
				req.setSwitchingType(getSwitchingType());
			} else {
				req = new RequestRSA(this.counter.getValue(), origem, destino, opticalSuperChannelGen.getNextOpticalSuperChannel(), dist.getHoldingTime(),getRequestType());
				req.setSwitchingType(getSwitchingType());
			}		
			counter.increment();
			req.setKshortestPaths(getNumKShortestPaths());
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

		Integer no1 = distUniform.sorteiaNo(getGraph().getNos().tamanho());
		Integer no2 = distUniform.sorteiaNo(getGraph().getNos().tamanho(), no1);
		No origem = getGraph().getNo(no1.toString());
		No destino = getGraph().getNo(no2.toString());
		RequestRSA req = null;
		
		if(isFixedBandwidth()) {
			req = new RequestRSA(this.counter.getValue(), origem, destino,this.superChannel, dist.getHoldingTime(), getRequestType());
		} else {
			req = new RequestRSA(this.counter.getValue(), origem, destino, opticalSuperChannelGen.getNextOpticalSuperChannel(), dist.getHoldingTime(),getRequestType());
			
		}
		req.setSwitchingType(getSwitchingType());
		
		getCounter().increment();
		req.setKshortestPaths(getNumKShortestPaths());
		return req;
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



}
