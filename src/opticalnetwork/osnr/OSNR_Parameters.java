package opticalnetwork.osnr;

import org.apache.commons.math3.analysis.function.Asinh;
import org.apache.commons.math3.analysis.function.Log;
import org.apache.commons.math3.analysis.function.Pow;
import org.apache.commons.math3.analysis.function.Sqrt;

import util.Converters;

public class OSNR_Parameters{

	/**number of channels*/
	private double numberOfChannels = 157;
	/**the output launch power or gain in dBm after amplification*/
	private double powerGaindB = 0.0; //[0dB]
	private double powerGain = 0.0; //[0dB]
	/**the bandwidth noise is set to 12.5 Ghz by default*/
	private double bandwidthNoise = 12.5*Math.pow(10,9); // 12.5 Ghz by default
	/**the symbol rate is set to 12.5 Ghz by default*/
	private double symbolRate = 12.5*Math.pow(10,9); // 12.5 Ghz by default
	/**the wavelength is set to 1550 x 10⁻⁹ m by default*/
	private double wavelength = 1550*Math.pow(10,-9); // [m] default is 1550nm
	/**the EDFA amplifier noise figure in dB*/
	private double amplifierNoiseFigure = 0.0 ; //[dB]
	/**the span length in meters*/
	private double spanLength = 0.0; // [m]
	/**fiber attenuation coefficient. 0.2 dB by default*/
	private double alpha = 0.2;
	/**beta2 is the GVD parameter also called group delay dispersion parameter */
	private double beta2 = 0.0;
	/**gama is the non-linear parameter which ranges between 1-5 [W⁻¹/Km]*/
	private double gama = 0.0;
	/**the full WDM Bandwidth in Hz, 5Thz by default*/
	private double fullBandwidthWDM = 5*Math.pow(10,12); // [Hz]


	

	public double getPowerGaindB() {
		return powerGaindB;
	}
	public void setPowerLaunchdB(double powerLaunchdB) {
		this.powerGaindB = powerLaunchdB;
		this.powerGain = Converters.dB2pow(powerLaunchdB);
	}

	public void setPowerLaunchdBm(double powerLaunchdBm) {
		this.powerGain = Converters.dB2pow(powerLaunchdBm)*Math.pow(10, -3);
		this.powerGaindB = Converters.pow2dB(powerGain);
	} 

	public double getBandwidthNoise() {
		return bandwidthNoise;
	}
	public void setBandwidthNoise(double bandwidthNoise) {
		this.bandwidthNoise = bandwidthNoise;
	}
	public double getSymbolRate() {
		return symbolRate;
	}
	public void setSymbolRate(double symbolRate) {
		this.symbolRate = symbolRate;
	}
	public double getWavelength() {
		return wavelength;
	}
	public void setWavelength(double wavelength) {
		this.wavelength = wavelength;
	}
	public double getAmplifierNoiseFigure() {
		return amplifierNoiseFigure;
	}
	public void setAmplifierNoiseFigure(double amplifierNoiseFigure) {
		this.amplifierNoiseFigure = amplifierNoiseFigure;
	}
	public double getSpanLength() {
		return spanLength;
	}
	public void setSpanLength(double spanLength) {
		this.spanLength = spanLength;
	}
	public double getBeta2() {
		return beta2;
	}
	public void setBeta2(double beta2) {
		this.beta2 = beta2;
	}
	public double getGama() {
		return gama;
	}
	public void setGama(double gama) {
		this.gama = gama;
	}
	public double getFullBandwidthWDM() {
		return fullBandwidthWDM;
	}
	public void setFullBandwidthWDM(double fullBandwidthWDM) {
		this.fullBandwidthWDM = fullBandwidthWDM;
	}



	public static void main (String[] args) {
		Pow pow = new Pow();
		pow.value(2, 2);
		double x = 6.561934770468690*Math.pow(10, -5);
		double a1 = Math.log(x + Math.sqrt(x*x + 1.0));
		Asinh asinh = new Asinh();
		double a2 = asinh.value(x);
		Log log_ = new Log();
		double a3 = log_.value(x + new Sqrt().value(x*x + 1.0));
		System.out.println("Asynh 1 =" + a1 );
		System.out.println("Asynh 2 =" + a2 );
		System.out.println("Asynh 3 =" + a3 );
		System.out.println("pow "  + pow.value(2, 2));
	}

	public double getPowerGain() {
		return powerGain;
	}

	public void setPowerGain(double powerGain) {
		this.powerGain = powerGain;
	}
	public double getAlpha() {
		return alpha;
	}
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}
	public double getNumberOfChannels() {
		return numberOfChannels;
	}
	public void setNumberOfChannels(double numberOfChannels) {
		this.numberOfChannels = numberOfChannels;
	}
}
