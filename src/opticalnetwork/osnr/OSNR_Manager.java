package opticalnetwork.osnr;

import java.util.ArrayList;

import opticalnetwork.osnr.OSNR.Type;
import util.Converters;

public class OSNR_Manager {
	
	
	private ArrayList<OSNR> osnrElements;
	
	public OSNR_Manager() {
		this.osnrElements = new ArrayList<OSNR>();
	}
	
	public double getOSNR_Rx (){
		double sumOSNRn = 0;
		for (OSNR n : osnrElements) {
			sumOSNRn += (1/n.getOSNR());
		}
		double osnrRx = 1/sumOSNRn;
		return osnrRx;
	} 
	
	public double getOSNR_Rx_dB () {
		return Converters.pow2dB(getOSNR_Rx());
	}
	
	public void add (OSNR n) {
		osnrElements.add(n);
	}
	
	
	public static void main (String[] args) {
		/**number of channels*/
		double numberOfChannels = 157;
		double bandwidthNoise = 12.5*Math.pow(10,9); // 12.5 Ghz by default
		double symbolRate = 12.5*Math.pow(10,9);
		double powerGaindBm = 0.0; //[dBm]
		double wavelength = 1550*Math.pow(10,-9);
		double amplifierNoiseFigure = 6 ; //[dB]
		double spanLength = 100*Math.pow(10, 3); // [m]
		double alpha = 0.22;
		double beta2 = 21*Math.pow(10, -27);
		double gama = 1.3*Math.pow(10, -3);
		double lossNode = 14;
		double totalLength = 2000*Math.pow(10, 3);
		double nSpan = Math.ceil(totalLength/spanLength);
		
		OSNR_Parameters param = new OSNR_Parameters();
		param.setAmplifierNoiseFigure(amplifierNoiseFigure);
		param.setBandwidthNoise(bandwidthNoise);
		param.setWavelength(wavelength);
		param.setBandwidthNoise(bandwidthNoise);
		param.setBeta2(beta2);
		param.setGama(gama);
		param.setPowerGain(Converters.dBm2milliWatts(powerGaindBm)*Math.pow(10, -3)/symbolRate);
		param.setSpanLength(spanLength);
		param.setAlpha(alpha);
		param.setNumberOfChannels(numberOfChannels);
		
		OSNR_Node node = new OSNR_Node(lossNode, param, Type.NODE);
		
//		double osnr_n1 = node.getOSNR();
		
		OSNR_Span span = new OSNR_Span(param, Type.SPAN);
		
//		double osnr_n2 = span.getOSNR();
		
		OSNR_Manager manager = new OSNR_Manager();
		
		manager.add(node);
		manager.add(span);
		
		double osnrRx = Converters.pow2dB(manager.getOSNR_Rx());
		
		System.out.printf("OSNR Total in Rx = %.4f \n", osnrRx );
		
		
		double spanOSNRTotal = nSpan*(1/span.getOSNR());
		double nodeOSNRTotal = 5*(1/node.getOSNR());
		double sumOSNRn = spanOSNRTotal+nodeOSNRTotal;
		double osnrRxTotal = 1/sumOSNRn;
		
		System.out.printf("OSNR Total in Rx = %.4f \n", Converters.pow2dB(osnrRxTotal ));
		
		
	}

	
	
	
}
