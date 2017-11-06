/**
 * Created on 03/02/2016
 */
package opticalnetwork;

import graph.ExcecaoGrafo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Alaelson Jatobá
 * Version 1.0
 */
public class LinkStateTable {
	HashMap<Integer,Boolean[]> stateTable;

	/**
	 *
	 */
	public LinkStateTable() {
		this.stateTable = new HashMap<Integer,Boolean[]>();
	}
	
	/**
	 * Puts a new dimension in the state table
	 * @param dimension the dimension's key to be installed
	 * @param stateVector a Boolean[] array
	 * @throws ExcecaoGrafo throws a error if the dimension is already installed.
	 */
	public void put(int dimension, Boolean[] stateVector) throws ExcecaoGrafo {
		if (this.stateTable.containsKey(dimension)) {
			throw new ExcecaoGrafo("Error installing new dimension: there is a dimension with identifier = " + dimension);
		}
		
		this.stateTable.put(dimension, stateVector);
		
	} 

	/**
	 * Starts a state table for each spectrum array in each dimension
	 * @param dimensions a int value which representing the number of dimensions in the link. Eg. The number of fibers.
	 * @param mask the mask as a boolean array be installed in each row of the state table.
	 *
	 * */
	public void installStateTable ( int dimensions, Boolean[] mask ) {
		for(int i = 0 ; i < dimensions ; i++){
			stateTable.put(i, mask.clone());
		}
	}

	/**
	 * Returns a boolean array representing the spectral slots of a dimension (Eg. SMF fiber or mode)
	 * @param dimension is the index of the dimension
	 * @return {@link Boolean}[] a boolean array
	 * */
	public Boolean[] getStateSpectralArray (int dimension) {
		return this.stateTable.get(dimension);
	}
	
	/**
	 * Sets a boolean array representing the spectral slots of a dimension (Eg. SMF fiber or mode)
	 * @param dimension is the index of the dimension
	 * @param newSpectralArray a boolean array representing the spectrum slots
	 * */
	public void setStateSpectralArray (int dimension, Boolean[] newSpectralArray) {
		this.stateTable.put(dimension, newSpectralArray);
	}

	/**
	 * @return the stateTable
	 */
	public HashMap<Integer, Boolean[]> getStateTable() {
		return stateTable;
	}

	/**
	 * @param stateTable the stateTable to set
	 */
	public void setStateTable(HashMap<Integer, Boolean[]> stateTable) {
		this.stateTable = stateTable;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Iterator<Integer> it = stateTable.keySet().iterator() ; it.hasNext() ; ) {
			int index = it.next();
			if (it.hasNext()) {
				builder.append("{").append(index).append(":").append(Arrays.toString(stateTable.get(index))).append("}\n");
			} else {
				builder.append("{").append(index).append(":").append(Arrays.toString(stateTable.get(index))).append("}");
			}
		}

		return builder.toString();
	}
	
	/**
	 * @deprecated
	 * Check if there is no frequency slot allocated under the position passed as parameter 
	 * @param frequencySlot the frequency slot's position to check
	 * @return the availability as a boolean value
	 */
	public boolean isFMFStateOfFrequencySlotAvailable (int frequencySlot) {
		boolean isAvailable = true;
		
		for (Iterator<Integer> it = stateTable.keySet().iterator() ; it.hasNext() ;) {
			int dimension = it.next();
			Boolean[] frequencySlots = stateTable.get(dimension);
			if (frequencySlots[frequencySlot] == false) {
				isAvailable = false;
				break;
			} 
		}
		return isAvailable;
	}

}
