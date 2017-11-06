/**
 *
 */
package opticalnetwork;

import java.util.ArrayList;

import graph.Caminho;
import opticalnetwork.elastica.rsa.RequestRSA;
import opticalnetwork.elastica.rssa.ResourceAllocation;

/**
 * Created in 09/06/2016
 * By @author Alaelson Jatoba
 * @version 1.0
 * 
 * Represents an entry to be used for the SystemState class
 * 
 */
public class SystemStateEntry {
	
	public static int id_key = 1;
	private int id ;
	private String pathKey;
	private RequestRSA request;
	private Caminho path;
	private boolean isFullAllocatedResources = false;
	private ArrayList<ResourceAllocation> allocatedResources;
	private ResourceAllocation allocatedResource;
	
	public SystemStateEntry () {
		allocatedResources = new ArrayList<ResourceAllocation>();
		id = id_key++;
	}

	/**
	 * Returns the entry's id
	 * @return the id
	 */
	public int getId() {
		return id ;
	}

	/**
	 * Returns the path's key with the source and destination
	 * @return the pairSourceDestination
	 */
	public String getPathKey() {
		return pathKey;
	}

	/**
	 * Sets the path's key with the source and destination
	 * @param pairSourceDestination the pairSourceDestination to set
	 */
	public void setPathKey(String pathKey) {
		this.pathKey = pathKey;
	}

	/**
	 * Returns the RequestRSA with the demand's requeriments
	 * @return the request
	 */
	public RequestRSA getRequest() {
		return request;
	}

	/**
	 * Sets the RequestRSA
	 * @param request the request to set
	 */
	public void setRequest(RequestRSA request) {
		this.request = request;
	}

	/**
	 * Returns the path
	 * @return the path
	 */
	public Caminho getPath() {
		return path;
	}

	/**
	 * Sets the path
	 * @param path the path to set
	 */
	public void setPath(Caminho path) {
		this.path = path;
	}

	/**
	 * Check if the resources in the path are full allocated
	 * @return the isFullAllocatedResources
	 */
	public boolean isFullAllocatedResources() {
		return isFullAllocatedResources;
	}

	/**
	 * Sets the resources in the path to full allocated
	 * @param isFullAllocatedResources the isFullAllocatedResources to set
	 */
	public void setFullAllocatedResources(boolean isFullAllocatedResources) {
		this.isFullAllocatedResources = isFullAllocatedResources;
	}

	/**
	 * Returns the resources that were allocated in the path
	 * @return the allocatedResource
	 */
	public ArrayList<ResourceAllocation> getAllocatedResources() {
		return allocatedResources;
	}
	
	
	
	public void addAllocatedResource(ResourceAllocation resource) {
		this.allocatedResources.add(resource);
	}
	
	public void removeResource(ResourceAllocation resource) {
		if (allocatedResources.contains(resource)) {
			this.allocatedResources.remove(resource);
		}
	}

	/**
	 * Sets the resources that were allocated in the path
	 * @param allocatedResource the allocatedResource to set
	 */
	public void setAllocatedResource(ArrayList<ResourceAllocation> allocatedResources) {
		this.allocatedResources = allocatedResources;
	}
	
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append(id).append(", ").append(this.pathKey).append(", ").append(this.path).append(", ").append(allocatedResource);
		return builder.toString();
	}

	
	
	public ResourceAllocation getAllocatedResource(ResourceAllocation resource) {
		for (ResourceAllocation r : getAllocatedResources()) {
			if (r.hashCode() == resource.hashCode()) {
			return r;
			}
		}
		return null;
	}

	/**
	 * @return the allocatedResource
	 */
	public ResourceAllocation getAllocatedResource() {
		return allocatedResource;
	}

	/**
	 * @param allocatedResource the allocatedResource to set
	 */
	public void setAllocatedResource(ResourceAllocation allocatedResource) {
		this.allocatedResource = allocatedResource;
	}
	

}
