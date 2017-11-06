package opticalnetwork.osnr;

public abstract class OSNR {
	
	public enum Type {
		NODE,SPAN;
	}
	
	private Type type;
	private double loss;
	protected OSNR_Parameters param;
	
	
	public OSNR(Type type) {
		this.type = type;
	}
	
	/**
	 * Gets the Optical Signal Noise Ration - OSNR
	 * @return 
	 */
	public abstract double getOSNR() ;
	
	/**
	 * Returns the amount of optical signal loss in db
	 * @return 
	 */
	public double getLoss() {
		return this.loss;
	}
	
	/**
	 * Returns the converted loss from dB to power ration
	 * If the OSNR will be applied for a node, it must do 10*log(-value_in_dB/10)
	 * If the OSNR will be applied for a span, it must do e^(-alpha*Lspan), been alpha in Neper/km

	 */
	public abstract double getTotalLoss();
	
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}

	public void setLoss(double loss) {
		this.loss = loss;
	}

}
