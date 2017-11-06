/**
 *
 */
package policy;

import graph.Caminho;
import graph.Enlace;
import graph.ExcecaoGrafo;
import opticalnetwork.controlplane.SDMEONController;
import opticalnetwork.elastica.rsa.RequestException;
import opticalnetwork.elastica.rsa.RequestRSA;
import opticalnetwork.elastica.rssa.AllocationException;
import opticalnetwork.elastica.rssa.ResourceAllocation;

/**
 * Created in 02/06/2016
 * By @author Alaelson Jatoba
 * @version 1.0
 */
public abstract class AllocationPolicy {
	
	protected SDMEONController controller;
	
	
	public AllocationPolicy (SDMEONController controller) {
		this.controller = controller;

	}
	
	/**
	 * 
	 * installs the demand for resource in the network
	 * @param request the request with a demand
	 * @param path the path 
	 * @return {@link ResourceAllocation} with the allocated resources.
	 * @param request
	 * @param path
	 * @throws ExcecaoGrafo
	 * @throws AllocationException
	 * @throws RequestException
	 */
	public abstract ResourceAllocation install(RequestRSA request, Caminho path) throws ExcecaoGrafo, AllocationException, RequestException ;

	/**
	 * 
	 * installs the demand for resource in the network
	 * @param request the request with a demand
	 * @param path the path 
	 * @param isReusingPath isReusingPath <code>true</code> if it is reusing a path already stored
	 * @param place a dimension (SPEF policy) or a lambda (SPAF policy) 
	 * @return {@link ResourceAllocation} with the allocated resources.
	 * @throws ExcecaoGrafo
	 * @throws AllocationException
	 * @throws RequestException

	 */
	public abstract ResourceAllocation install(RequestRSA request, Caminho path, boolean isReusingPath, int place) throws ExcecaoGrafo, AllocationException, RequestException ;
	
	/**
	 * Installs the demand in the specified link.
	 * @param link the link to install the resource
	 * @param numberOfSlots number of slots to be installed
	 * @param dimension the dimension of the slots
	 * @param firstSlot the first slot that starts the demand
	 * @throws AllocationException return a exception if the position is already used
	 */
	protected void installResource (Enlace link, int numberOfSlots, int dimension, int firstSlot ) throws AllocationException {
		String key = link.getId();
		String[] parts = key.split("-");
		String source = parts[0]; //
		String dest = parts[1];
		String inverseKey = dest+"-"+source;
		Enlace linkReverse = controller.getGrafo().getEnlace(inverseKey); 
		for (int k = 0 ; k < numberOfSlots ; k++) {
			Boolean[] linkSpectrum = link.getStateSpectralArray(dimension); //just to see in debug
			if ( linkSpectrum[firstSlot+k] == true ){
				link.getStateSpectralArray(dimension)[firstSlot+k]= false;
			} else {
				throw new AllocationException("slot is already used in link " + link + ", slot: " + (firstSlot+k) + ", dimension: " + dimension );
			}
			if (controller.isBidirectional()) {
				if ( linkReverse.getStateSpectralArray(dimension)[firstSlot+k] == true ){
					linkReverse.getStateSpectralArray(dimension)[firstSlot+k]= false;//reverse link

				} else {
					throw new AllocationException("slot is already used in reverse link " + link + ", slot: " + (firstSlot+k) + ", dimension: " + dimension );
				}
			}
		}

	}
	

}
