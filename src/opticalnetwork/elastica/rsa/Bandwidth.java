/**
 *
 */
package opticalnetwork.elastica.rsa;

import java.util.ArrayList;
import java.util.List;

import util.Constants;



/**
 * Created in 21/01/2016
 * By @author Alaelson Jatoba
 * @version 1.0
 */
public enum Bandwidth {
	
//	BW100G (1,1,4),
	BW100G_1slot (1,1,1),
	BW100G (1,4,4),
	BW200G (2,7,8),
	BW300G (3,10,12),
	BW400G (4,12,16),
	BW100G_DPQPSK(Constants.SPECTRAL_SC_100Gbps_DP_QPSK.getCapacity(), 
			Constants.SPECTRAL_SC_100Gbps_DP_QPSK.getNumSlots(),
			Constants.SPACE_SC_100Gbps_DP_QPSK.getNumSlots()),
	BW200G_DPQPSK(Constants.SPECTRAL_SC_100Gbps_DP_QPSK.getCapacity(), 
			Constants.SPECTRAL_SC_100Gbps_DP_QPSK.getNumSlots(),
			Constants.SPACE_SC_100Gbps_DP_QPSK.getNumSlots()),
	BW300G_DPQPSK(Constants.SPECTRAL_SC_100Gbps_DP_QPSK.getCapacity(), 
			Constants.SPECTRAL_SC_100Gbps_DP_QPSK.getNumSlots(),
			Constants.SPACE_SC_100Gbps_DP_QPSK.getNumSlots()),
	BW400G_DPQPSK(Constants.SPECTRAL_SC_100Gbps_DP_QPSK.getCapacity(), 
			Constants.SPECTRAL_SC_100Gbps_DP_QPSK.getNumSlots(),
			Constants.SPACE_SC_100Gbps_DP_QPSK.getNumSlots());

	
	double bw;
	long spectralSlots;
	long spatialSlots;

	Bandwidth (double bw, long spectralSlots, long spatialSlots) {
		this.bw = bw;
		this.spectralSlots = spectralSlots;
		this.spatialSlots = spatialSlots;
	}

	public double getBw() {
		return bw;
	}

	/**
	 * @return the spectralSlots
	 */
	public int getSpectralSlots() {
		return (int)spectralSlots;
	}

	/**
	 * @return the spatialSlots
	 */
	public int getSpatialSlots() {
		return (int)spatialSlots;
	}

	public static List<Integer> getSpectrumSlotsValues () {
		List<Integer> bws = new ArrayList<>();
		for (Bandwidth b : values()) {
			bws.add(b.getSpectralSlots());
		}
		return bws;
	}
	
	public static List<Integer> getSpatialSlotValues () {
		List<Integer> bws = new ArrayList<>();
		for (Bandwidth b : values()) {
			bws.add(b.getSpatialSlots());
		}
		return bws;
	}
	
	public static void main (String[] args) {
		
		System.out.println("Spatial slots: " + Bandwidth.getSpatialSlotValues());
		System.out.println("Spectrum slots: " + Bandwidth.getSpectrumSlotsValues());
	}


}
