/**
 *
 */
package simulation;

import java.util.HashMap;
import java.util.Map;

/**
 * Created in 05/07/2016
 * By @author Alaelson Jatoba
 * @version 1.0
 */
public class DataSet {
	
	private String header;
	private HashMap<Integer, Double> data;
	
	public DataSet () {
		data = new HashMap<Integer, Double>();
	}
	
	
	/**
	 * Overwrites the value associated with the key;
	 * @param key key with which the specified value is to be associated
	 * @param value value to be associated with the specified key
	 * @return the previous value associated with the key;
	 */
	public double put( Integer key, Double value) {
		return data.put(key, value);
	}
	
	/**
	 * Adds all elements in a Map into the data set.
	 * @param m a map which values and its key is going to be added
	 */
	public void putAll( Map<? extends Integer, ? extends Double> m) {
		data.putAll(m);
	}
	
	/**
	 * remove a object from the dataset
	 * @param the value's key
	 */
	public void remove ( Integer key ) {
		data.remove(key);
	}
	
	/**
	 * Inserts the value only using unique keys
	 * @param primaryKey a unique key
	 * @param value the value to be inserted
	 * @return a boolean informing if the insert operation was realized 
	 */
	public boolean add ( Integer primaryKey, Double value) {
		if (!data.containsKey(primaryKey)) {
			data.put(primaryKey, value);
		} else {
			return false;
		}
			
		return true;
	}


	/**
	 * @return the header
	 */
	public String getHeader() {
		return header;
	}


	/**
	 * @param header the header to set
	 */
	public void setHeader(String header) {
		this.header = header;
	}


	/**
	 * @return the data
	 */
	public HashMap<Integer, Double> getData() {
		return data;
	}


	/**
	 * @param data the data to set
	 */
	public void setData(HashMap<Integer, Double> data) {
		this.data = data;
	}

}
