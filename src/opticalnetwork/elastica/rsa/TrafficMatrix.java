/**
 *
 */
package opticalnetwork.elastica.rsa;

import java.util.HashMap;

/**
 * Created in 08/09/2015
 * By @author Alaelson Jatoba
 * @version 1.0
 */
public class TrafficMatrix {

	private HashMap<TrafficRSA, Integer> matrix;
	private HashMap<String, Double> matrixSimple;
	private double totalCapacity;

	/**
	 * @return the totalCapacity
	 */
	public double getTotalCapacity() {
		return totalCapacity;
	}

	/**
	 * @param totalCapacity the totalCapacity to set
	 */
	public void setTotalCapacity(double totalCapacity) {
		this.totalCapacity = totalCapacity;
	}

	/**
	 *The Constructor
	 * */
	public TrafficMatrix () {
		matrix = new HashMap<TrafficRSA, Integer>();
		matrixSimple = new HashMap<String, Double>();
	}

	/**
	 * It returns a {@link HashMap} with the traffic matrix;
	 * @return the matrix
	 */
	public HashMap<TrafficRSA, Integer> getMatrix() {
		return matrix;
	}

	/**
	 * It returns a {@link HashMap} with the traffic matrix;
	 * @return the matrix
	 */
	public HashMap<String, Double> getMatrixSimple() {
		return matrixSimple;
	}

	/**
	 * It sets the traffic matrix
	 * @param matrix the matrix to set
	 */
	public void setMatrix(HashMap<TrafficRSA, Integer> matrix) {
		this.matrix = matrix;
	}

	/**
	 * It sets the traffic matrix
	 * @param matrix the matrix to set
	 */
	public void setMatrixSimple(HashMap<String, Double> matrix) {
		this.matrixSimple = matrix;
	}

	public void addTraffic (TrafficRSA traffic, int value ) {
		if (traffic != null) {
			this.matrix.put(traffic, value);
		} else {
			throw new IllegalArgumentException("traffic argument is can't be null!");
		}
	}

	public void removeTraffic ( TrafficRSA traffic ) {
		if (traffic != null) {
			this.matrix.remove(traffic);
		} else {
			throw new IllegalArgumentException("traffic argument is can't be null!");
		}
	}


	public void addTrafficInMatrixSimple (String key, double value ) {
		if (key != null) {
			this.matrixSimple.put(key, value);
		} else {
			throw new IllegalArgumentException("traffic argument is can't be null!");
		}
	}

	public void removeTrafficFromMatrixSimple ( String key ) {
		if (key != null) {
			this.matrixSimple.remove(key);
		} else {
			throw new IllegalArgumentException("traffic argument is can't be null!");
		}
	}

	/**
	 * Returns a integer with a bandwidth related with the key (pair source-destination)
	 * Example: for source 1 to destination 2, the <code>key</code> is <code>1-2</code>.
	 * @param key the pair source-destination
	 * @return the bandwidth
	 * */
	public Double getBandwidth (String key) {
		if (key != null) {
			return matrixSimple.get(key);
		} else {
			throw new IllegalArgumentException("traffic argument is can't be null!");
		}

	}

}
