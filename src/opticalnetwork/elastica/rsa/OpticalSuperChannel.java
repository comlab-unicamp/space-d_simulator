/**
 *
 */
package opticalnetwork.elastica.rsa;

import util.Constants;

/**
 * Created in 17/06/2016
 * By @author Alaelson Jatoba
 * @version 1.0
 */
public class OpticalSuperChannel {
	
	private int numChannels = 1;
	private double signalBw = 32; //32GHz
	private double capacity ; // C = 2B*log2M
	private double channelSpacing = 50; //50Ghlz
	private double slotBw = 3.125;
	private double bandGuardBw = 12.5; //12.5 GHz
	private long numSlots;
	private ModulationFormat modulationFormat;

	/**
	 * The Constructor
	 * 
	 * @param numChannels number of channels in the super channel
	 * @param signalBandwidth the signal's bandwidth in GHz
	 * @param channelSpacing the channel spacing in GHz
	 * @param bandGuardBw the bandguard's bandwidth in GHz
	 * @param slotBandwidth the slot's bandwidth in GHz
	 * @param modulationFormat the modulation format as object of {@link ModulationFormat}
	 */
	public OpticalSuperChannel ( int numChannels, double signalBandwidth, double channelSpacing, double bandGuardBw, double slotBandwidth, ModulationFormat modulationFormat) {
		this.signalBw = signalBandwidth;
		this.numChannels = numChannels;
		this.channelSpacing = channelSpacing;
		this.modulationFormat = modulationFormat;
		this.bandGuardBw = bandGuardBw;
		this.slotBw = slotBandwidth;
	}
	

	/**
	 * @return the numSubChannels
	 */
	public int getNumSubChannels() {
		return numChannels;
	}

	/**
	 * @param numSubChannels the numSubChannels to set
	 */
	public void setNumSubChannels(int numSubChannels) {
		this.numChannels = numSubChannels;
	}

	/**
	 * @return the signalBw
	 */
	public double getSignalBw() {
		return signalBw;
	}

	/**
	 * @param signalBw the signalBw to set
	 */
	public void setSignalBw(double signalBw) {
		this.signalBw = signalBw;
	}

	/**
	 * @return the capacity
	 */
	public double getCapacity() {
		this.capacity = numChannels*signalBw*modulationFormat.getNumberOfBits();
		return this.capacity;
	}

	/**
	 * @param capacity the capacity to set
	 */
	public void setCapacity(double capacity) {
		this.capacity = capacity;
	}

	/**
	 * @return the subChannelSpace
	 */
	public double getSubChannelSpace() {
		return channelSpacing;
	}

	/**
	 * @param subChannelSpace the subChannelSpace to set
	 */
	public void setSubChannelSpace(double subChannelSpace) {
		this.channelSpacing = subChannelSpace;
	}

	/**
	 * @return the slotBw
	 */
	public double getSlotBw() {
		return slotBw ;
	}

	/**
	 * @param slotBw the slotBw to set
	 */
	public void setSlotBw(double slotBw) {
		this.slotBw = slotBw;
	}

	/**
	 * @return the bandGuardBw
	 */
	public double getBandGuardBw() {
		return bandGuardBw;
	}

	/**
	 * @param bandGuardBw the bandGuardBw to set
	 */
	public void setBandGuardBw(double bandGuardBw) {
		this.bandGuardBw = bandGuardBw;
	}

	/**
	 * @return the numSlots
	 */
	public long getNumSlots() {
		double value  = (numChannels*channelSpacing+2*bandGuardBw)/slotBw;
		numSlots = (long) Math.ceil(value);
		return numSlots;
	}

	/**
	 * @param numSlots the numSlots to set
	 */
	public void setNumSlots(long numSlots) {
		this.numSlots = numSlots;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Capacity: ").append(getCapacity()).append(" Gbps;").append(System.lineSeparator());
		builder.append("Signal Bw: ").append(signalBw).append(" GHz;").append(System.lineSeparator());
		builder.append("Channel spacing: ").append(channelSpacing).append(" GHz;").append(System.lineSeparator());
		builder.append("Slot Bw: ").append(slotBw).append(" Ghz;").append(System.lineSeparator());
		builder.append("Bandguard Bw: ").append(bandGuardBw).append(" GHz;").append(System.lineSeparator());
		builder.append("Number of channels: ").append(numChannels).append(";").append(System.lineSeparator());
		builder.append("Number of slots: ").append(getNumSlots()).append(";").append(System.lineSeparator());
		builder.append("Modulation Format: ").append(modulationFormat).append(";").append(System.lineSeparator());
		return builder.toString();
	}
	
	public static void main ( String[] args ) {
		
		int numChannels = Constants.NUMBER_SUB_CHANNELS_1;
		double signalBw = Constants.SIGNAL_BW_32GHz;
		double channelSpacing = Constants.CHANNEL_SPACING_50GHz;
		double bandGuardBw = Constants.BANDGUARD_BW_NONE;
		double slotBw = Constants.SLOT_SIZE_BW_3_125GHz;
		ModulationFormat modulationFormat = ModulationFormat.MF_DPQPSK;
		
		OpticalSuperChannel C100Gbps = new OpticalSuperChannel(numChannels, signalBw, channelSpacing, bandGuardBw, slotBw, modulationFormat);
		System.out.println("Setup 100Gbps:");
		System.out.println(C100Gbps);
		
		numChannels = 2;
		OpticalSuperChannel C200Gbps = new OpticalSuperChannel(numChannels, signalBw, channelSpacing, bandGuardBw, slotBw, modulationFormat);
		System.out.println("Setup 200Gbps:");
		System.out.println(C200Gbps);
		
		numChannels = 3;
		OpticalSuperChannel C300Gbps = new OpticalSuperChannel(numChannels, signalBw, channelSpacing, bandGuardBw, slotBw, modulationFormat);
		System.out.println("Setup 300Gbps:");
		System.out.println(C300Gbps);
		
		numChannels = 4;
		OpticalSuperChannel C400Gbps = new OpticalSuperChannel(numChannels, signalBw, channelSpacing, bandGuardBw, slotBw, modulationFormat);
		System.out.println("Setup 400Gbps:");
		System.out.println(C400Gbps);
		
	}
	
	

}
