package opticalnetwork.osnr;

import util.Constants;
import util.Converters;

public class OSNR_Node extends OSNR{
	
	
	
	/**
	 * The constructor
	 * @param loss the node loss in dB
	 */
	public OSNR_Node (double loss, OSNR_Parameters pram, Type type) {
		super(type);
		this.param = pram;
		this.setLoss(loss);
		
	}

	
	/** Returns the node OSNR
	 * 
	 */
	public double getOSNR() {
		double gain = param.getPowerGain();
		double launchPower = gain*param.getSymbolRate()*getTotalLoss();
		double freq = Converters.wavelength2Frequency(param.getWavelength());
		double plank = Constants.PLANCK_CONSTANT;
		double noiseFigure = Converters.dB2pow(param.getAmplifierNoiseFigure());
		
		double aseNoise = param.getBandwidthNoise()*plank*freq*noiseFigure;
		
		double osnr = launchPower/aseNoise;
		
		return osnr;
	}

	
	/**
	 * Returns the converted loss from dB to power ration.
	 * Equation is 10*log(-value_in_dB/10)
	 * 
	 * @see opticalnetwork.osnr.OSNR_Parameters#getLoss()
	 */
	public double getTotalLoss() {
		double loss_value = Converters.dB2pow(-getLoss()); 
		return loss_value;
	}


}
