package distribution;

/**
 * @author Alaelson
 * @version 1.1
 */
public interface Distribution {

	public double getHoldingTime();
	public double getTimeBetweenArrivals();
	
	/**
	 * Sets the average rate of arrivals per unit of time (lambda)
	 * @lambda the arrivals rate to set
	 */
	public void setArrivalsRate(double lambda);
	
}
