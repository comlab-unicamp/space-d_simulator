/**
 *
 */
package opticalnetwork.elastica.rsa;

import random.MersenneTwister;

/**
 * Created in 21/01/2016
 * By @author Alaelson Jatoba
 * @version 1.0
 */
public class UniformBandwidthGenerator {
	private MersenneTwister random;
	
	/**
	 * 
	 */
	public UniformBandwidthGenerator(int seed) {
		random = new MersenneTwister(seed);
	}
	
	private Bandwidth getRandomBandwidth(){
		int r = random.nextInt(Bandwidth.values().length);
		for (Bandwidth b : Bandwidth.values()) {
			if (b.getBw() == r+1) {
				return b;
			} 
		}
		return null;
	}
	
	public Bandwidth getNextBandwidth(){
		Bandwidth bw = getRandomBandwidth();
		while (bw == null) {
			bw=getNextBandwidth();
		}
		return bw;
	}
	
	public static void main (String[] args) {
		System.out.println("Getting a random bandwidth");
		UniformBandwidthGenerator gen = new UniformBandwidthGenerator(667);
		int a=0;
		int b=0;
		int c=0;
		int d=0;
		
		for (int i = 0 ; i < 1000000000 ; i++){
			Bandwidth bw = gen.getNextBandwidth();
			switch (bw) {
			case BW100G:
				a++;
				break;
			case BW200G:
				b++;
				break;
			case BW300G:
				c++;
				break;
			case BW400G:
				d++;
				break;

			default:
				break;
			}
		}
		
		System.out.println(Bandwidth.BW100G + " = " + a);
		System.out.println(Bandwidth.BW200G + " = " + b);
		System.out.println(Bandwidth.BW300G + " = " + c);
		System.out.println(Bandwidth.BW400G + " = " + d);
	}
	

	

}
