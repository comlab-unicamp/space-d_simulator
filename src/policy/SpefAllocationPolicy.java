/**
 *
 */
package policy;

import java.util.HashMap;
import java.util.Iterator;

import graph.Caminho;
import graph.Enlace;
import graph.ExcecaoGrafo;
import graph.No;
import opticalnetwork.LinkStateTable;
import opticalnetwork.controlplane.SDMEONController;
import opticalnetwork.elastica.rsa.RequestException;
import opticalnetwork.elastica.rsa.RequestRSA;
import opticalnetwork.elastica.rssa.AllocationException;
import opticalnetwork.elastica.rssa.ResourceAllocation;
import opticalnetwork.elastica.rssa.SwitchingType;

/**
 * Created in 01/08/2016
 * By @author Alaelson Jatoba
 * @version 1.0
 */
public class SpefAllocationPolicy extends AllocationPolicy {

	/**
	 * The Constructor
	 * @param controller the controller of the simulation
	 */
	public SpefAllocationPolicy(SDMEONController controller) {
		super(controller);

	}

	/**
	 * Spectrum First Allocation Policy - SPEF.
	 * Installs the demand for slots using the Spectrum First Allocation Policy.
	 * 
	 * @param request the request with a demand
	 * @param path the path 
	 * @param isReusingPath isReusingPath <code>true</code> if it is reusing a path already stored
	 * @param place a dimension (SPEF policy) or a lambda (SPAF policy) 
	 * @return {@link ResourceAllocation} with the allocated resources.
	 * @throws ExcecaoGrafo
	 * @throws AllocationException
	 * @throws RequestException
	 */
	@Override
	public ResourceAllocation install(RequestRSA request, Caminho path, boolean isReusingPath, int place) 
			throws ExcecaoGrafo, AllocationException, RequestException {

		int numberOfSlots = 0;
		if (request.getOpticalSuperChannel() != null ){
			numberOfSlots = (int)request.getOpticalSuperChannel().getNumSlots();
		} else {
			throw new RequestException("The optical superchannel in request " + request + " is null");
		}

		HashMap<Integer, Boolean[]> maskStateTable = controller.getMaskStateTable(path);
		LinkStateTable pathMask = new LinkStateTable();
		pathMask.setStateTable(maskStateTable);

		int dimension = -1;
		int firstSlot = -1;


		/*	tests if there is a continuous void range of slots in the first dimension, moving to the highest
		 * 	dimension if there isn't */

		/*tries reuse the slots in the dimension specified*/

		Boolean[] spectrum = maskStateTable.get(place);
		if (isReusingPath ) {
			if (!controller.isAllSlotsBusy(spectrum)) { //TODO check if it is right
				dimension = place;
				firstSlot = controller.getPosition(numberOfSlots, maskStateTable.get(dimension));

				if (dimension == -1) {
					throw new AllocationException("invalide dimension = -1!");
				}
				if (firstSlot > -1) {
					//installs the resource
					for(Iterator<Enlace> it = path.getEnlaces().valores().iterator() ; it.hasNext() ;){
						Enlace edge = it.next();//the edge in the path.
						//the link is the same edge in the graph, but could be a different object.
						Enlace link = controller.getGrafo().getEnlace(edge.getId());
						Enlace linkReverse = null;
						if (controller.isBidirectional()) {
							No left = link.getNoEsquerda();
							No right = link.getNoDireita();
							linkReverse = controller.getGrafo().getEnlace(right, left);
						}

						if(controller.isDebug()) {

							System.out.println("R_ID: "+request.getId()+", Before install \n Link:"+link.getId() + ", num slots: " + numberOfSlots);
							System.out.println("R_ID: "+request.getId()+", LINK STATE:\n"+link.getLinkStateTable());
							if (controller.isBidirectional()) {
								System.out.println("R_ID: "+request.getId()+", REVERSE LINK STATE ("+linkReverse.getId() + "):\n"+linkReverse.getLinkStateTable());
							}
						}
						//does the installment itself
						installResource(link, numberOfSlots, dimension, firstSlot);	
						link.addDimensionInPathTable(dimension, path);
						if (controller.isBidirectional()) {
							linkReverse.addDimensionInPathTable(dimension, path);
						}
						if(controller.isDebug()) {
							System.out.println("R_ID: "+request.getId()+", After install \n Link:"+link.getId() + ", num slots: " + numberOfSlots);
							System.out.println("R_ID: "+request.getId()+", LINK STATE:\n"+link.getLinkStateTable());
							if (controller.isBidirectional()) {
								System.out.println("R_ID: "+request.getId()+", REVERSE LINK STATE ("+linkReverse.getId() + "):\n"+linkReverse.getLinkStateTable());
							}
						}
					}
				} else {
					SDMEONController.log.info("Dimension " + dimension +" in path " + path + " hasn't enough slots to install the request "+request+"!");
				}

			} //IF is all slots busy in dimension
		} else { //IF reusingPath
			//search for one dimension (first fit) with slots available
			for (int i = 0 ; i < controller.getGrafo().getNumberOfDimensions() ; i++ ) {
				dimension = i;
				//Check the if it is joint switching and if there are at least one slot used by other connection 
				spectrum = maskStateTable.get(i);
				if (controller.isAllSlotsBusy(spectrum)) {
					continue; //skip if all slots are busy in this dimension
				}
				if (!controller.isAllSlotsFree(spectrum) && 
						request.getSwitchingType().equals(SwitchingType.JOINT)) { //if all slots are free goes to get position
					if (!isReusingPath) {
						//TODO check if it is right
						//skip the dimension that has already used at least one slot if JOINT is set up.   
						continue;
					}
					// to check if the path is the dimension's owner 
					boolean isPathUsingDimension = true;
					for (Enlace e : path.getEnlaces().getEnlaces().values()) {
						if (controller.isBidirectional()) {
							if (e.getDimensionPathTable().get(i).isBidirectionalEquals(path)) {
								isPathUsingDimension = isPathUsingDimension && true;
							} else {
								isPathUsingDimension = isPathUsingDimension && false;
							}
						} else {
							if (e.getDimensionPathTable().get(i).equals(path)) {
								isPathUsingDimension = isPathUsingDimension && true;
							} else {
								isPathUsingDimension = isPathUsingDimension && false;
							}
						}
					}
					// if false the path isn't the dimension's owner, then skip
					if (!isPathUsingDimension) {
						continue;
					}

				}

				/*gets the position in a row of the state table*/
				firstSlot = controller.getPosition(numberOfSlots, maskStateTable.get(i));
				//				boolean isLocked = true; // to check if the dimension is locked . Obsolete variable
				if (firstSlot > -1) {
					dimension = i; //gets the dimension's index			

					//installs the resource
					for(Iterator<Enlace> it = path.getEnlaces().valores().iterator() ; it.hasNext() ;){
						Enlace edge = it.next();//the edge in the path.
						//the link is the same edge in the graph, but could be a different object.
						Enlace link = controller.getGrafo().getEnlace(edge.getId());
						Enlace linkReverse = null;
						if (controller.isBidirectional()) {
							No left = link.getNoEsquerda();
							No right = link.getNoDireita();
							linkReverse = controller.getGrafo().getEnlace(right, left);
						}

						if(controller.isDebug()) {

							System.out.println("R_ID: "+request.getId()+", Before install \n Link:"+link.getId() + ", num slots: " + numberOfSlots);
							System.out.println("R_ID: "+request.getId()+", LINK STATE:\n"+link.getLinkStateTable());
							if (controller.isBidirectional()) {
								System.out.println("R_ID: "+request.getId()+", REVERSE LINK STATE ("+linkReverse.getId() + "):\n"+linkReverse.getLinkStateTable());
							}
						}
						//does the installment itself
						installResource(link, numberOfSlots, dimension, firstSlot);	
						link.addDimensionInPathTable(dimension, path);
						if (controller.isBidirectional()) {
							linkReverse.addDimensionInPathTable(dimension, path);
						}
						if(controller.isDebug()) {
							System.out.println("R_ID: "+request.getId()+", After install \n Link:"+link.getId() + ", num slots: " + numberOfSlots);
							System.out.println("R_ID: "+request.getId()+", LINK STATE:\n"+link.getLinkStateTable());
							if (controller.isBidirectional()) {
								System.out.println("R_ID: "+request.getId()+", REVERSE LINK STATE ("+linkReverse.getId() + "):\n"+linkReverse.getLinkStateTable());
							}
						}
					}
					break;
				}

			} // end FOR loop in the dimensions
		}


		ResourceAllocation resource = new ResourceAllocation();
		resource.addDimensionInList(dimension);
		resource.setFirstSlot(firstSlot);

		return resource;


	}

	/**
	 * Spectrum First Allocation Policy - SPEF.
	 * Installs the demand for slots using the Spectrum First Allocation Policy.
	 * @param request the request with a demand
	 * @param path the path 
	 * @return {@link ResourceAllocation} with the allocated resources.
	 * @throws ExcecaoGrafo
	 * @throws AllocationException
	 * @throws RequestException
	 */
	@Override
	public ResourceAllocation install(RequestRSA request, Caminho path)
			throws ExcecaoGrafo, AllocationException, RequestException {
		int place = -1;
		boolean isReusingPath = false;
		return install(request, path, isReusingPath, place);
	}






}
