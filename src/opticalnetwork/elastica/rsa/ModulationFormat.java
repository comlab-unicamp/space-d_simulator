/**
 *
 */
package opticalnetwork.elastica.rsa;

/**
 * Created in 17/06/2016
 * By @author Alaelson Jatoba
 * @version 1.0
 */
public enum ModulationFormat {
	
	MF_BPSK (2 , "BPSK", false),
	MF_QPSK (4 , "QPSK", false),
	MF_8QAM (8 , "8QAM", false),
	MF_16QAM (16 , "16QAM", false),
	MF_DPQPSK (4 , "DP-QPSK", true),
	MF_DP8QAM (8 , "DP-8QAM", true); //2 * log2M, M=8symbols
	
	int numberOfSymbols;
	String name;
	boolean isDualPolarization;
	
	ModulationFormat (int numberOfSymbols, String name, boolean isDualPolarization) {
		this.numberOfSymbols = numberOfSymbols;
		this.name = name;
		this.isDualPolarization = isDualPolarization;
	}
	
	private double log2 (double number) {
		return Math.log(number)/Math.log(2);
	}
	
	public int getNumberOfBits () {
		int num = (int) (isDualPolarization ? 2*log2(numberOfSymbols) : log2(numberOfSymbols));
		return num;
	}
	
	public String toString () {
		return name;
	}

}
