/**
 *
 */
package simulation;

import opticalnetwork.elastica.rsa.Bandwidth;
import opticalnetwork.elastica.rsa.ModulationFormat;
import opticalnetwork.elastica.rsa.OpticalSuperChannel;
import opticalnetwork.elastica.rsa.RequestRSA.RequestType;
import opticalnetwork.elastica.rssa.SwitchingType;
import stats.JobDataBase;


/**
 * Created in 28/04/2016
 * By @author Alaelson Jatoba
 * @version 1.0
 */
public interface SimulatorI {
	
	public JobDataBase simulate () throws Exception;

	/**
	 * @param dir
	 */
	public void setDirectory(String dir);

	/**
	 * @param initialLoad
	 */
	public void setInitialLoad(int initialLoad);

	/**
	 * @param incrementalLoad
	 */
	public void setIncrementalLoad(int incrementalLoad);

	/**
	 * @param endLoad
	 */
	public void setEndLoad(int endLoad);

	/**
	 * @param holdingTime
	 */
	public void setHoldingTime(double holdingTime);

	/**
	 * @param requestLimit
	 */
	public void setRequestLimit(int requestLimit);

	/**
	 * @param seed
	 */
	public void setSeed(int seed);

	/**
	 * @param isBidirectional
	 */
	public void setBidirectional(boolean isBidirectional);

	/**
	 * @param topologia
	 */
	public void setTopology(String topologia);

	/**
	 * @param numCarrierSlots
	 */
	public void setNumCarrierSlots(int numCarrierSlots);

	/**
	 * @param isFixedBw
	 */
	public void setFixedBandwidth(boolean isFixedBw);
	
	/**
	 * @param isFixedBw
	 */
	public void setFixedSuperChannel(OpticalSuperChannel sc);

	/**
	 * @param bandwidth
	 */
	public void setBandwidth(Bandwidth bandwidth);

	/**
	 * @param numk
	 */
	public void setNumKShortestPaths(int numk);

	/**
	 * @param debug
	 */
	public void setDebug(boolean debug);

	/**
	 * @param steadyState
	 */
	public void setSteadyState(int steadyState);

	/**
	 * @param requestType
	 */
	public void setRequestType(RequestType requestType);

	/**
	 * @param filename
	 */
	public void setFilename(String filename);

	/**
	 * @param spatialDimension
	 */
	public void setSpatialDimension(int spatialDimension);

	/**
	 * @param spectralSlots
	 */
	public void setSpectralSlots(int spectralSlots);

	/**
	 * @param name
	 */
	public void setAlgorithm(String name);

	/**
	 * @param isFewModeFiber
	 */
	public void setFewModeFiber(boolean isFewModeFiber);

	/**
	 * @param switchingType
	 */
	public void setSwitchingType(SwitchingType switchingType);
	
	public SwitchingType getSwitchingType();

	/**
	 * @return the signalBw
	 */
	public double getSignalBw() ;

	/**
	 * @param signalBw the signalBw to set
	 */
	public void setSignalBw(double signalBw) ;

	/**
	 * @return the channelSpacing
	 */
	public double getChannelSpacing() ;

	/**
	 * @param channelSpacing the channelSpacing to set
	 */
	public void setChannelSpacing(double channelSpacing);

	/**
	 * @return the bandGuard
	 */
	public double getBandGuard() ;
	/**
	 * @param bandGuard the bandGuard to set
	 */
	public void setBandGuard(double bandGuard) ;

	/**
	 * @return the slotBw
	 */
	public double getSlotBw();
	/**
	 * @param slotBw the slotBw to set
	 */
	public void setSlotBw(double slotBw) ;

	/**
	 * @return the modulationFormat
	 */
	public ModulationFormat getModulationFormat();

	/**
	 * @param modulationFormat the modulationFormat to set
	 */
	public void setModulationFormat(ModulationFormat modulationFormat);
	
	/**
	 * @return the useDataBase
	 */
	public boolean isUseDataBase() ;

	/**
	 * @param useDataBase the useDataBase to set
	 */
	public void setUseDataBase(boolean useDataBase);
}

