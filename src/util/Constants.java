/**
 *
 */
package util;

import opticalnetwork.elastica.rsa.ModulationFormat;
import opticalnetwork.elastica.rsa.OpticalSuperChannel;

/**
 * Created in 17/06/2016
 * By @author Alaelson Jatoba
 * @version 1.0
 */
public final class Constants {

	public static final int NUMBER_SUB_CHANNELS_1 = 1;
	public static final int NUMBER_SUB_CHANNELS_2 = 2;
	public static final int NUMBER_SUB_CHANNELS_3 = 3;
	public static final int NUMBER_SUB_CHANNELS_4 = 4;
	public static final int NUMBER_SUB_CHANNELS_5 = 5;
	public static final int NUMBER_SUB_CHANNELS_6 = 6;
	public static final int NUMBER_SUB_CHANNELS_7 = 7;
	public static final int NUMBER_SUB_CHANNELS_8 = 8;
	public static final int NUMBER_SUB_CHANNELS_9 = 9;
	public static final double SIGNAL_BW_32GHz = 32.0;
	public static final double SIGNAL_BW_50GHz = 50.0;
	public static final double CHANNEL_SPACING_32GHz = 32.0;
	public static final double CHANNEL_SPACING_50GHz = 50.0;
	public static final double CHANNEL_SPACING_37_5GHz = 37.5;
	public static final double CHANNEL_SPACING_34_375GHz = 34.375;
	/**None bandguard*/
	public static final double BANDGUARD_BW_NONE = 0.0;
	/**9 GHz in each side of signal*/
	public static final double BANDGUARD_BW_9GHz = 9.0;
	
	/**12.5 GHz in each side of signal*/
	public static final double BANDGUARD_BW_12_5GHz = 12.5;
	/**Slot size with bandwidth of 50 GHz*/
	public static final double SLOT_SIZE_BW_50GHz = 50;
	/**Slot size with bandwidth of 12.5 GHz*/
	public static final double SLOT_SIZE_BW_12_5GHz = 12.5;
	/**Slot size with bandwidth of 3.123 GHz*/
	public static final double SLOT_SIZE_BW_3_125GHz = 3.125;
	
	public static OpticalSuperChannel SPECTRAL_SC_100Gbps_DP_QPSK = new OpticalSuperChannel(NUMBER_SUB_CHANNELS_1, 
			SIGNAL_BW_32GHz, CHANNEL_SPACING_32GHz, BANDGUARD_BW_9GHz, SLOT_SIZE_BW_12_5GHz, ModulationFormat.MF_DPQPSK);
	public static OpticalSuperChannel SPECTRAL_SC_200Gbps_DP_QPSK = new OpticalSuperChannel(NUMBER_SUB_CHANNELS_2, 
			SIGNAL_BW_32GHz, CHANNEL_SPACING_32GHz, BANDGUARD_BW_9GHz, SLOT_SIZE_BW_12_5GHz, ModulationFormat.MF_DPQPSK);
	public static OpticalSuperChannel SPECTRAL_SC_300Gbps_DP_QPSK = new OpticalSuperChannel(NUMBER_SUB_CHANNELS_3, 
			SIGNAL_BW_32GHz, CHANNEL_SPACING_32GHz, BANDGUARD_BW_9GHz, SLOT_SIZE_BW_12_5GHz, ModulationFormat.MF_DPQPSK);
	public static OpticalSuperChannel SPECTRAL_SC_400Gbps_DP_QPSK = new OpticalSuperChannel(NUMBER_SUB_CHANNELS_4, 
			SIGNAL_BW_32GHz, CHANNEL_SPACING_32GHz, BANDGUARD_BW_9GHz, SLOT_SIZE_BW_12_5GHz, ModulationFormat.MF_DPQPSK);
	
	public static OpticalSuperChannel SPACE_SC_100Gbps_DP_QPSK = new OpticalSuperChannel(NUMBER_SUB_CHANNELS_1, 
			SIGNAL_BW_32GHz, CHANNEL_SPACING_50GHz, BANDGUARD_BW_NONE, SLOT_SIZE_BW_12_5GHz, ModulationFormat.MF_DPQPSK);
	public static OpticalSuperChannel SPACE_SC_200Gbps_DP_QPSK = new OpticalSuperChannel(NUMBER_SUB_CHANNELS_2, 
			SIGNAL_BW_32GHz, CHANNEL_SPACING_50GHz, BANDGUARD_BW_NONE, SLOT_SIZE_BW_12_5GHz, ModulationFormat.MF_DPQPSK);
	public static OpticalSuperChannel SPACE_SC_300Gbps_DP_QPSK = new OpticalSuperChannel(NUMBER_SUB_CHANNELS_3, 
			SIGNAL_BW_32GHz, CHANNEL_SPACING_50GHz, BANDGUARD_BW_NONE, SLOT_SIZE_BW_12_5GHz, ModulationFormat.MF_DPQPSK);
	public static OpticalSuperChannel SPACE_SC_400Gbps_DP_QPSK = new OpticalSuperChannel(NUMBER_SUB_CHANNELS_4, 
			SIGNAL_BW_32GHz, CHANNEL_SPACING_50GHz, BANDGUARD_BW_NONE, SLOT_SIZE_BW_12_5GHz, ModulationFormat.MF_DPQPSK);
	
	
	public static OpticalSuperChannel SPACE_SC_1SB_CH_DP_8QAM = new OpticalSuperChannel(NUMBER_SUB_CHANNELS_1, 
			SIGNAL_BW_32GHz, CHANNEL_SPACING_50GHz, BANDGUARD_BW_12_5GHz, SLOT_SIZE_BW_12_5GHz, ModulationFormat.MF_DP8QAM);
	
	public static final double LIGHT_SPEED = 299792458.00; // [m/s]
	public static final double PLANCK_CONSTANT = 6.62607004*Math.pow(10,-34); //[m²kg/s]
	
	/** WSS's loss is 14 dB*/
	public static final int LOSS_WSS = 14; //14db
	/** OXC's loss is 2 dB*/
	public static final int LOSS_OXC = 2; //14db
	

	private Constants(){
	    //this prevents even the native class from 
	    //calling this ctor as well :
	    throw new AssertionError();
	  }
}
