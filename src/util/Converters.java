package util;

public class Converters {
	
	/**
	 * Converts a value in Decibel (dB) to power ration in Watts
	 * @param dB a value in dB
	 * @return the power ration value
	 */
	public static double dB2pow(double dB){
		double value = Math.pow(10, (dB/10));
		return value;
	}
	
	/**
	 * Converts a value from power ration to Decibel (dB)
	 * @param value a value to be converted in dB
	 * @return the value in dB
	 */
	public static double pow2dB (double value) {
		double dB = 10*Math.log10(value);
		return dB;
	}
	
	/**
	 * Converts the value in dBm to power ration in Milliwatts
	 * @param dBm the value in dBm
	 * @return
	 */
	public static double dBm2milliWatts(double dBm){
		return dB2pow(dBm);
	}
	
	/**
	 * Converts a value from power ration in Milliwatts to dBm
	 * @param value a value to be converted in dBm
	 * @return the value in dBm
	 */
	public static double pow2dBm (double value) {
		return pow2dB(value);
	}
	
	/**
	 * Converts a wavelength to frequency
	 * @param wavelength the wavelength in meters
	 * @return the frequency value
	 */
	public static double wavelength2Frequency (double wavelength) {
		double freq = Constants.LIGHT_SPEED/wavelength;
		return  freq;
	}
	
	/**
	 * Returns the value of attenuation constant alpha in Neper
	 * @param alphadB a value of alpha in dB
	 * @return the attenuation constant in Neper
	 */
	public static double getAlphaNeper (double alphadB) {
		double alphaNeper = alphadB*Math.pow(10, -3)/4.343;
		return alphaNeper;
	}

}
