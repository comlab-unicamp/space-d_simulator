/**
 * Created on 02/02/2016
 */
package opticalnetwork.elastica.rssa;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alaelson Jatobá
 * Version 1.0
 */
public class ResourceAllocation {



	private int firstSlot = -1;
	private List<Integer> dimensionsAllocated;

	/**
	 * The Constructor
	 */
	public ResourceAllocation() {
		dimensionsAllocated = new ArrayList<Integer>();
	}

	/**
	 * @return the firstSlot
	 */
	public int getFirstSlot() {
		return firstSlot;
	}

	/**
	 * @param firstSlot the firstSlot to set
	 */
	public void setFirstSlot(int firstSlot) {
		this.firstSlot = firstSlot;
	}

	/**
	 * @return the dimensionsAllocated
	 */
	public List<Integer> getDimensionsAllocated() {
		return dimensionsAllocated;
	}

	/**
	 * @param dimensionsAllocated the dimensionsAllocated to set
	 */
	public void setDimensionsAllocated(List<Integer> dimensionsAllocated) {
		this.dimensionsAllocated = dimensionsAllocated;
	}

	/**
	 * @param dimension the dimension to set
	 */
	public void addDimensionInList(int dimension) {
		this.dimensionsAllocated.add(dimension);
	}
	
	/**
	 * Appends all of the elements in the specified list to the end of this list
	 * @param list list a list with indexes of dimensions
	 */
	public void addAllDimensionsInList (List<Integer> list) {
		this.dimensionsAllocated.addAll(list);
	}

	@Override
	public String toString() {
		return "FirstSlot: " + firstSlot + ", id dimensions: " + dimensionsAllocated;
	}




}
