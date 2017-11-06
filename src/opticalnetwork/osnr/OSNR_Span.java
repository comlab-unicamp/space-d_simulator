package opticalnetwork.osnr;

import org.apache.commons.math3.analysis.function.Asinh;

import util.Constants;
import util.Converters;

public class OSNR_Span extends OSNR{
	
	/**
	 * The constructor
	 * @param loss the node loss in dB
	 */
	public OSNR_Span (OSNR_Parameters param, Type type) {
		super(type);
		this.param = param;
		this.setLoss(param.getAlpha());
		
	}

	@Override
	public double getOSNR() {
		double gain = param.getPowerGain();
		double launchPower = gain*param.getSymbolRate()*getTotalLoss();
		double freq = Converters.wavelength2Frequency(param.getWavelength());
		double plank = Constants.PLANCK_CONSTANT;
		double noiseFigure = Converters.dB2pow(param.getAmplifierNoiseFigure());
		
		double aseNoise = param.getBandwidthNoise()*plank*freq*noiseFigure;
		
		double G_WDM3 = Math.pow(getOptimumLaunchPower_G_WDM(), 3);
		double rho_NLI = getRhoNLI();
		double nli = param.getBandwidthNoise()*rho_NLI*G_WDM3;
		
		double osnr = launchPower/(aseNoise+nli);
		
		return osnr;
	}

	@Override
	public double getTotalLoss() {
		double alphaNeper = Converters.getAlphaNeper(param.getAlpha());
		double loss_value = Math.exp(-alphaNeper*param.getSpanLength()); 
		return loss_value;
	}
	
	/**
	 * Returns the Non linear interference noise accumulated after one span 
	 * @return the NLI noise
	 */
	public double getRhoNLI ( ) {
		double alphaNeper = Converters.getAlphaNeper(param.getAlpha());
		double gama2 = Math.pow(param.getGama(), 2);
		double l_eff2 = Math.pow(getEffectiveLength(),2);
		double pi2 = Math.pow(Math.PI, 2);
		double fullBandWDM2 = Math.pow(param.getFullBandwidthWDM(), 2);
		double beta2 = param.getBeta2();
		Asinh asinhfunc = new Asinh();
		double v = pi2*fullBandWDM2*beta2/(4*alphaNeper);
		double asinh = asinhfunc.value( v );
		double loss_a = getTotalLoss();

		double rho_nli = 16*loss_a*alphaNeper*gama2*l_eff2*asinh/(27*Math.PI*param.getBeta2());

		return rho_nli;
	}

	/**
	 * Calculates and returns the effective length of a span
	 * @return the effective length 
	 */
	public double getEffectiveLength () {
		double alphaNeper = Converters.getAlphaNeper(param.getAlpha());
		double l_eff = (1-Math.exp(-2*alphaNeper*param.getSpanLength()))/(2*alphaNeper);
		return l_eff;
	}

	/**
	 * Calculates and return the optimum launch power PSD at each span 
	 * @return the G_WDM value
	 */
	public double getOptimumLaunchPower_G_WDM () {
		double freq = Converters.wavelength2Frequency(param.getWavelength());
		double plank = Constants.PLANCK_CONSTANT;
		double noiseFigure = Converters.dB2pow(param.getAmplifierNoiseFigure());
		double rho_NLI = getRhoNLI();
		double value = plank*freq*noiseFigure/(2*rho_NLI);

		//apply the cubic root to the value
		double value3 = Math.cbrt(value);

		return value3;
	}


}
