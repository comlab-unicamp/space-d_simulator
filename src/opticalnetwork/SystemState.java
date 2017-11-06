/**
 *
 */
package opticalnetwork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import graph.Caminho;
import opticalnetwork.elastica.rsa.RequestRSA;
import opticalnetwork.elastica.rssa.ResourceAllocation;

/**
 * Created in 09/06/2016
 * By @author Alaelson Jatoba
 * @version 1.0
 */
public class SystemState {
	//ID (Integer) and entry (SystemStateEntry) 
	private HashMap<Integer,SystemStateEntry> entries;
	private int numSpectralSlots;
	private int idGraph;
	
	public SystemState (int numSpectralSlots) {
		entries = new HashMap<Integer, SystemStateEntry>();
		this.numSpectralSlots = numSpectralSlots;
		SystemStateEntry.id_key = 1; //resets the id of the entries 
	}

	
	/**
	 * Creates and storage a new entry
	 * @param pathKey a path's key with the source and destination pair
	 * @param request the request 
	 * @param path the path
	 * @param allocatedResource the allocated resources used in the path
	 */
	public void addEntry ( String pathKey, RequestRSA request, Caminho path, ResourceAllocation allocatedResource ) {
		SystemStateEntry entry = new SystemStateEntry();
		entry.setPathKey(pathKey);
		entry.setRequest(request);
		entry.setPath(path);
		entry.setAllocatedResource(allocatedResource);
//		entry.getAllocatedResources().add(allocatedResource);
		this.entries.put(entry.getId(), entry);
	}
	
	/**
	 * Returns a SystemStateEntry object 
	 * @param id the SystemStateEntry´s id
	 * @return the {@link SystemStateEntry}
	 */
	public SystemStateEntry getEntry (int id) {
		return this.entries.get(id);
	}
	
	/**
	 * Returns a {@link ArrayList} with the SystemStateEntry entries using the key and the inverse key
	 * @param key key is the path's key
	 * @param isBidirectional isBidirectional a boolean for bidirectional connections
	 * @return
	 */
	public List<SystemStateEntry> getEntriesByKey(String key, boolean isBidirectional, boolean isSpef) {

		ArrayList<SystemStateEntry> list = new ArrayList<SystemStateEntry>();
		
		String[] parts = key.split("-");
		String source = parts[0]; //
		String dest = parts[1];
		String inverseKey = dest+"-"+source;
		
		for (SystemStateEntry entry : entries.values()) {
			int numOfSlots = (int) entry.getRequest().getOpticalSuperChannel().getNumSlots();
			int lastIndexActualChannelInstalled = entry.getAllocatedResource().getFirstSlot();
			int firstIndexLastChannelAvalilable = this.numSpectralSlots - numOfSlots;
			
			if (entry.getPathKey().equals(key)) {
				if (isSpef) {
					if ( firstIndexLastChannelAvalilable - lastIndexActualChannelInstalled >= numOfSlots){ // add only if there is at least one channel available
						list.add(entry);
					}
				} else {
					list.add(entry);
				}
			}
			if (isBidirectional) {
				if (entry.getPathKey().equals(inverseKey)) {
					if (isSpef) {
						if ( firstIndexLastChannelAvalilable - lastIndexActualChannelInstalled >= numOfSlots){ // add only if there is at least one channel available
							list.add(entry);
						}
					} else {
						list.add(entry);
					}
				}
			}
			
		}
		
		
		return list;
	}
	
	public String toString(){
		
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		for (Iterator<SystemStateEntry> it = entries.values().iterator() ; it.hasNext() ; ) {
			SystemStateEntry entry = it.next();
			if (it.hasNext()) {
				builder.append(entry).append(";\n");
			} else {
				builder.append(entry).append("}");
			}
		}
		if (entries.isEmpty()) {
			builder.append("}");
		}

		return builder.toString();
		
	}
	
	/**
	 * Remove the element from the SystemStateDB
	 * @param entry
	 */
	public void remove(SystemStateEntry entry) {
		entries.remove(entry.getId());
	}
	
	/**
	 * Remove each element in the list if it is saved in SystemStateDb
	 * @param entryList
	 */
	public void removeEntryList (List<SystemStateEntry> entryList) {
		for (SystemStateEntry e : entryList) {
			if (entries.containsKey(e.getId()))
				entries.remove(e.getId());
		}
	}
	
	public SystemStateEntry getEntryByRequest ( RequestRSA request ) {
		for (Iterator<SystemStateEntry> it = entries.values().iterator() ; it.hasNext() ; ) {
			SystemStateEntry entry = it.next();
			if (entry.getRequest().getId() == request.getId()) {
				return entry;
			}
				
		}
		return null;
	}


	/**
	 * Updates a entry with new index of dimensions inside 
	 * @param entry
	 * @param newDimensions
	 */
	public void updateEntryAdd(SystemStateEntry entry,
			List<Integer> newDimensions) {
		entry.getAllocatedResource().addAllDimensionsInList(newDimensions);
		
	}
	
	/**
	 * Updates a entry with a new position of the first slot 
	 * @param entry
	 * @param newDimensions
	 */
	public void updateEntryToAdd(SystemStateEntry entry,
			int firstSlot) {
		entry.getAllocatedResource().setFirstSlot(firstSlot);
		
	}
	
	/**
	 * Updates a entry with new index of dimensions inside 
	 * @param entry
	 * @param dimensions
	 */
	public void updateEntryRemove(SystemStateEntry entry,
			List<Integer> dimensions) {
		entry.getAllocatedResource().getDimensionsAllocated().removeAll(dimensions);
		
	}
	
	/**
	 * Updates a entry with a new position of the first slot 
	 * @param entry
	 * @param newDimensions
	 */
	public void updateEntryToRemove(SystemStateEntry entry,
			int firstSlot) {
		entry.getAllocatedResource().setFirstSlot(firstSlot);
		
	}
	
	public int size() {
		return entries.size();
	}


	/**
	 * @return the idGraph
	 */
	public int getIdGraph() {
		return idGraph;
	}


	/**
	 * @param idGraph the idGraph to set
	 */
	public void setIdGraph(int idGraph) {
		this.idGraph = idGraph;
	}
	
}
