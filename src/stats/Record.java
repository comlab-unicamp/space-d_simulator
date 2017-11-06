/**
 *
 */
package stats;

/**
 * Created in 08/09/2016
 * Modified in 10/10/2016
 * By @author Alaelson Jatoba
 * @version 1.1
 */
public class Record {
	
	private int id;
	private int jobId;
	private int year;
	private int numberOfRequests;
	private int numberOfDimensions;
	private double throughput;
	private double requiredDemand;
	private double networkUtilization;
	private double cost;
	private double costOfTransceivers ;
	private double costOfActivations ;
	private double costOfEdfas ;

	public Record () {
		
	}
	
	public Record (int jobId, int year, int numberOfRequests, int numberOfDimensions, double throughput, double requiredDemand) {
		this.jobId = jobId;
		this.year = year;
		this.numberOfRequests = numberOfRequests;
		this.numberOfDimensions = numberOfDimensions;
		this.throughput = throughput;
		this.requiredDemand = requiredDemand;
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
	 * @return the year
	 */
	public int getYear() {
		return year;
	}

	/**
	 * @param year the year to set
	 */
	public void setYear(int year) {
		this.year = year;
	}

	/**
	 * @return the numberOfRequests
	 */
	public int getNumberOfRequests() {
		return numberOfRequests;
	}

	/**
	 * @param numberOfRequests the numberOfRequests to set
	 */
	public void setNumberOfRequests(int numberOfRequests) {
		this.numberOfRequests = numberOfRequests;
	}

	/**
	 * @return the numberOfDimensions
	 */
	public int getNumberOfDimensions() {
		return numberOfDimensions;
	}

	/**
	 * @param numberOfDimensions the numberOfDimensions to set
	 */
	public void setNumberOfDimensions(int numberOfDimensions) {
		this.numberOfDimensions = numberOfDimensions;
	}

	/**
	 * @return the throughput
	 */
	public double getThroughput() {
		return throughput;
	}

	/**
	 * @param throughput the throughput to set
	 */
	public void setThroughput(double throughput) {
		this.throughput = throughput;
	}

	/**
	 * @return the requiredDemand
	 */
	public double getRequiredDemand() {
		return requiredDemand;
	}

	/**
	 * @param requiredDemand the requiredDemand to set
	 */
	public void setRequiredDemand(double requiredDemand) {
		this.requiredDemand = requiredDemand;
	}
	
	/**
	 * @return the jobId
	 */
	public int getJobId() {
		return jobId;
	}

	/**
	 * @param jobId the jobId to set
	 */
	public void setJobId(int jobId) {
		this.jobId = jobId;
	}
	
	/**
	 * Gets the amount of network utilization
	 * @return the network utilization
	 */
	public double getNetworkUtilization() {
		return networkUtilization;
	}

	/**
	 * Sets the amount of network utilization
	 * @param networkUtilization the network utilization
	 */
	public void setNetworkUtilization(double networkUtilization) {
		this.networkUtilization = networkUtilization;
	}

	/**
	 * Gets the network cost;
	 * @return a double value
	 */
	public double getCost() {
		return cost;
	}

	/**
	 * Sets the network cost
	 * @param cost a double value
	 */
	public void setCost(double cost) {
		this.cost = cost;
	}
	
	/** 
	 * Return the object as a String
	 * @see java.lang.Object#toString()
	 */
	public String toString (){
		StringBuilder sb = new StringBuilder();
		//"SIM_ID,YEAR,REQUIRED_DEMAND,REQUESTS,THROUGHPUT,DIMENSIONS\n");
		sb.append(jobId).append(",").append(this.year).append(",").append(requiredDemand);
		sb.append(",").append(numberOfRequests).append(",").append(throughput);
		sb.append(",").append(numberOfDimensions).append(",").append(networkUtilization);
		sb.append(",").append(cost).append(",").append(costOfTransceivers);
		sb.append(",").append(costOfActivations).append(",").append(costOfEdfas);
		return sb.toString();
	}

	/**
	 * Returns the total cost with transceivers that have been installed in the network
	 * @return
	 */
	public double getCostOfTransceivers() {
		return costOfTransceivers;
	}

	/**
	 * Set the total cost with transceivers that have been installed in the network
	 * @param costOfTransceivers
	 */
	public void setCostOfTransceivers(double costOfTransceivers) {
		this.costOfTransceivers = costOfTransceivers;
	}

	/**
	 * Returns the total cost of spatial channels activations in the network
	 * @return
	 */
	public double getCostOfActivations() {
		return costOfActivations;
	}

	/**
	 * Set the total cost of spatial channels activations in the network
	 * 
	 * @param costOfActivations
	 */
	public void setCostOfActivations(double costOfActivations) {
		this.costOfActivations = costOfActivations;
	}

	/**
	 * Returns the total cost with EDFAs that have been installed in the network
	 * @return
	 */
	public double getCostOfEdfas() {
		return costOfEdfas;
	}

	/**
	 * Set the total cost with EDFAs that have been installed in the network
	 * 
	 * @param costOfEdfas
	 */
	public void setCostOfEdfas(double costOfEdfas) {
		this.costOfEdfas = costOfEdfas;
	}
	

}
