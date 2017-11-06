package stats;

import java.util.LinkedHashMap;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class StatisticResults {
	
	private LinkedHashMap<Integer, SummaryStatistics> annualNumSpatialChannelsStats ;
	private LinkedHashMap<Integer, SummaryStatistics> annualNetworkUtilizationStats ;
	private LinkedHashMap<Integer, SummaryStatistics> annualNetworkCostStats ;
	private LinkedHashMap<Integer, SummaryStatistics> annualNetworkCostTransceiversStats ;
	private LinkedHashMap<Integer, SummaryStatistics> annualNetworkCostActivationsStats ;
	private LinkedHashMap<Integer, SummaryStatistics> annualNetworkCostEDFAsStats ;
	private int firstYear;
	private int lastYear;
	private DataBase database;
	
	public StatisticResults(int firstYear, int lastYear, DataBase database) {
		this.firstYear = firstYear;
		this.lastYear = lastYear;
		this.database = database;
		annualNumSpatialChannelsStats = new LinkedHashMap<Integer, SummaryStatistics>();
		annualNetworkUtilizationStats = new LinkedHashMap<Integer, SummaryStatistics>();
		annualNetworkCostStats = new LinkedHashMap<Integer, SummaryStatistics>();
		annualNetworkCostTransceiversStats = new LinkedHashMap<Integer, SummaryStatistics>();
		annualNetworkCostActivationsStats = new LinkedHashMap<Integer, SummaryStatistics>();
		annualNetworkCostEDFAsStats = new LinkedHashMap<Integer, SummaryStatistics>();
		createDataStructures();
	}
	
	public void createDataStructures () {
		for (int i = firstYear ; i <= lastYear ; i++) {
			SummaryStatistics statsSpatialChannel = new SummaryStatistics();
			annualNumSpatialChannelsStats.put(i, statsSpatialChannel);
			SummaryStatistics statsNetUtilization = new SummaryStatistics();
			annualNetworkUtilizationStats.put(i, statsNetUtilization);
			SummaryStatistics statsNetCost = new SummaryStatistics();
			annualNetworkCostStats.put(i, statsNetCost);
			SummaryStatistics statsNetCostTransceptors = new SummaryStatistics();
			annualNetworkCostTransceiversStats.put(i, statsNetCostTransceptors);
			SummaryStatistics statsNetCostActivations = new SummaryStatistics();
			annualNetworkCostActivationsStats.put(i, statsNetCostActivations);
			SummaryStatistics statsNetCostEdfas = new SummaryStatistics();
			annualNetworkCostEDFAsStats.put(i, statsNetCostEdfas);
		}
	}
	
	public void calculateStatistics () {
		for (JobDataBase table: this.database.getJobs()) {
			for (Record rec: table.getRecords()) {
				//adds the record's number of spatial channels into annual statistics
				annualNumSpatialChannelsStats.get(rec.getYear()).addValue(rec.getNumberOfDimensions());
				annualNetworkUtilizationStats.get(rec.getYear()).addValue(rec.getNetworkUtilization());
				annualNetworkCostStats.get(rec.getYear()).addValue(rec.getCost());
				annualNetworkCostTransceiversStats.get(rec.getYear()).addValue(rec.getCostOfTransceivers());
				annualNetworkCostActivationsStats.get(rec.getYear()).addValue(rec.getCostOfActivations());
				annualNetworkCostEDFAsStats.get(rec.getYear()).addValue(rec.getCostOfEdfas());
			}
		}

	}

	/**
	 * Returns the statistics of the annual number of spatial channel metric
	 * @return a {@link LinkedHashMap} with the statistics where the key is the year 
	 */
	public LinkedHashMap<Integer, SummaryStatistics> getAnnualNumSpatialChannelsStats() {
		return annualNumSpatialChannelsStats;
	}

	/**
	 * Sets the statistics of the annual number of spatial channel metric
	 * @param annualNetworkCostStats is a {@link LinkedHashMap} with the statistics where the key is the year
	 */
	public void setAnnualNumSpatialChannelsStats(LinkedHashMap<Integer, SummaryStatistics> annualNumSpatialChannelsStats) {
		this.annualNumSpatialChannelsStats = annualNumSpatialChannelsStats;
	}
	
	/**
	 * Returns the statistics of the annual network utilization metric
	 * @return a {@link LinkedHashMap} with the statistics where the key is the year 
	 */
	public LinkedHashMap<Integer, SummaryStatistics> getAnnualNetworkUtilizationStats() {
		return annualNetworkUtilizationStats;
	}

	/**
	 * Sets the statistics of the annual network utilization metric
	 * @param annualNetworkCostStats is a {@link LinkedHashMap} with the statistics where the key is the year
	 */
	public void setAnnualNetworkUtilizationStats(LinkedHashMap<Integer, SummaryStatistics> annualNetworkUtilizationStats) {
		this.annualNetworkUtilizationStats = annualNetworkUtilizationStats;
	}

	/**
	 * Returns the statistics of the annual network cost metric
	 * @return a {@link LinkedHashMap} with the statistics where the key is the year 
	 */
	public LinkedHashMap<Integer, SummaryStatistics> getAnnualNetworkCostStats() {
		return annualNetworkCostStats;
	}

	/**
	 * Sets the statistics of the annual network cost metric
	 * @param annualNetworkCostStats is a {@link LinkedHashMap} with the statistics where the key is the year
	 */
	public void setAnnualNetworkCostStats(LinkedHashMap<Integer, SummaryStatistics> annualNetworkCostStats) {
		this.annualNetworkCostStats = annualNetworkCostStats;
	}

	/**
	 * Returns the statistics of the annual cost of transceiver metric
	 * @return a {@link LinkedHashMap} with the statistics where the key is the year 
	 */
	public LinkedHashMap<Integer, SummaryStatistics> getAnnualNetworkCostTransceiversStats() {
		return annualNetworkCostTransceiversStats;
	}

	
	
	/**
	 * Set the statistics of the annual cost of transceiver metric
	 * @param annualNetworkCostTransceiversStats
	 */
	public void setAnnualNetworkCostTransceiversStats(
			LinkedHashMap<Integer, SummaryStatistics> annualNetworkCostTransceiversStats) {
		this.annualNetworkCostTransceiversStats = annualNetworkCostTransceiversStats;
	}

	/**
	 * Returns the statistics of the annual cost with spatial channels activations metric
	 * @return a {@link LinkedHashMap} with the statistics where the key is the year 
	 */
	public LinkedHashMap<Integer, SummaryStatistics> getAnnualNetworkCostActivationsStats() {
		return annualNetworkCostActivationsStats;
	}

	/**
	 * Set the statistics of the annual cost with spatial channels activations metric
	 * @param annualNetworkCostActivationsStats
	 */
	public void setAnnualNetworkCostActivationsStats(
			LinkedHashMap<Integer, SummaryStatistics> annualNetworkCostActivationsStats) {
		this.annualNetworkCostActivationsStats = annualNetworkCostActivationsStats;
	}

	/**
	 * Returns the statistics of the annual cost of EDFAS metric
	 * @return a {@link LinkedHashMap} with the statistics where the key is the year 
	 */
	public LinkedHashMap<Integer, SummaryStatistics> getAnnualNetworkCostEDFAsStats() {
		return annualNetworkCostEDFAsStats;
	}

	/**
	 * Set the statistics of the annual cost of EDFAS metric
	 * @param annualNetworkCostEDFAsStats
	 */
	public void setAnnualNetworkCostEDFAsStats(LinkedHashMap<Integer, SummaryStatistics> annualNetworkCostEDFAsStats) {
		this.annualNetworkCostEDFAsStats = annualNetworkCostEDFAsStats;
	}

	
}
