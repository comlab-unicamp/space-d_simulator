/**
 *
 */
package opticalnetwork.elastica.rsa;

import java.io.Serializable;

import graph.No;
import opticalnetwork.elastica.rssa.ResourceAllocation;
import opticalnetwork.elastica.rssa.SwitchingType;


/**
 * Represents the request for allocate a bandwidth between a pair of nodes on network.
 *
 * Created in 24/08/2015
 * By @author Alaelson Jatoba
 * @version 1.0
 */
public class RequestRSA implements Serializable{

	/**
	 *
	 */
	private int id;
	private double holdingTime;
	private static final long serialVersionUID = 1L;
	/*the source node*/
	private No source;
	/*the destination node*/
	private No destination;
	/*the bandwidth*/
	private Bandwidth bandwidth;
	private OpticalSuperChannel opticalSuperChannel;
	/*the traffic matrix*/
//	private TrafficMatrix matrix;
	/*the type of this request*/
	RequestType type;
	private int firstFrequencySlot = -1;
	private Boolean[] frequencySlotAllocated;
	private ResourceAllocation resource;
	private int KshortestPaths = 1;
	private SwitchingType switchingType;




	private String key;

	public enum RequestType {
		SPECTRUM_FIRST_FIT_SLIGHTLY,ASSIGNMENT_MATRIX, CLOSE_REQUEST, SPECTRUM_FIRST, SPACE_FIRST, CLOSE_SPECTRUM_FIRST,CLOSE_SPACE_FIRST, FLEX_SDM, INCREMENTAL_SPAF, SPAF, SPEF
	}

	/**
	 * @param id the Request's identifier
	 * @param source the source node
	 * @param destination the destination node
	 * @param holdingTime the service time
	 * @param type the request's type
	 */
	public RequestRSA(int id, No source, No destination, double holdingTime,RequestType type) {
		this.id = id;
		this.source = source;
		this.destination = destination;
		this.key = source.getId()+"-"+destination.getId();
		this.type = type;
		this.holdingTime = holdingTime;
		this.resource = new ResourceAllocation();
	}

	/**
	 * @param id the Request's identifier
	 * @param source the source node
	 * @param destination the destination node
	 * @param bandwidth the bandwidth to be allocated
	 * @param holdingTime the service time
	 * @param type the request's type
	 */
	public RequestRSA(int id, No source, No destination, Bandwidth bandwidth, double holdingTime,RequestType type) {
		this(id, source, destination, holdingTime, type);
		this.bandwidth = bandwidth;
	}


	/**
	 * @param value
	 * @param origem
	 * @param destino
	 * @param opticalSuperChannel
	 * @param tempoServico
	 * @param requestType
	 */
	public RequestRSA(int value, No origem, No destino,
			OpticalSuperChannel opticalSuperChannel, double tempoServico,
			RequestType requestType) {
		this(value, origem, destino, tempoServico, requestType);
		this.opticalSuperChannel = opticalSuperChannel;
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


	/**
	 * @return the holding Time
	 */
	public double getHoldingTime() {
		return holdingTime;
	}


	/**
	 * @param holdingTime the serviceTime to set
	 */
	public void setHoldingTime(double holdingTime) {
		this.holdingTime = holdingTime;
	}


	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}


	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
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
	 * @return the bandwidth
	 */
	public Bandwidth getBandwidth() {
		return bandwidth;
	}


	/**
	 * @param bandwidth the bandwidth to set
	 */
	public void setBandwidth(Bandwidth bandwidth) {
		this.bandwidth = bandwidth;
	}


	/**
	 * @return the type
	 */
	public RequestType getType() {
		return type;
	}


	/**
	 * @param type the type to set
	 */
	public void setType(RequestType type) {
		this.type = type;
	}


	/**
	 * @return
	 */
	public String getKeySourceDestination() {
		return this.key;
	}


	/**
	 * @return the frequency slot
	 */
	public Boolean[] getFrequencySlot() {
		return frequencySlotAllocated;
	}


	/**
	 * @param frequencySlot the frequency slot to set
	 */
	public void setFrequencySlot(Boolean[] frequencySlot) {
		this.frequencySlotAllocated = frequencySlot;
	}


	/**
	 * @param frequencySlot the first frequency slot allocated in the spectrum
	 */
	public void setFirstFrequencySlot(int frequencySlot) {
		this.firstFrequencySlot = frequencySlot;
	}


	/**
	 * @return the firstFrequencySlot
	 */
	public int getFirstFrequencySlot() {
		return firstFrequencySlot;
	}


	/**
	 * @return the resource
	 */
	public ResourceAllocation getResource() {
		return resource;
	}


	/**
	 * @param resource the resource to set
	 */
	public void setResource(ResourceAllocation resource) {
		this.resource = resource;
	}


	/**
	 * @return the kshortestPaths
	 */
	public int getKshortestPaths() {
		return KshortestPaths;
	}


	/**
	 * @param kshortestPaths the kshortestPaths to set
	 */
	public void setKshortestPaths(int kshortestPaths) {
		KshortestPaths = kshortestPaths;
	}
	
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append("ID: ").append(getId()).append(", Key: ").append(getKey());
		return builder.toString();
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
	 * @return the opticalSuperChannel
	 */
	public OpticalSuperChannel getOpticalSuperChannel() {
		return opticalSuperChannel;
	}

	/**
	 * @param opticalSuperChannel the opticalSuperChannel to set
	 */
	public void setOpticalSuperChannel(OpticalSuperChannel opticalSuperChannel) {
		this.opticalSuperChannel = opticalSuperChannel;
	}


}
