/**
 *
 */
package opticalnetwork.elastica.rsa;

import java.util.ArrayList;
import java.util.List;

import random.MersenneTwister;
import util.Constants;

/**
 * Created in 21/01/2016
 * By @author Alaelson Jatoba
 * @version 1.0
 */
public class UniformOpticalSuperChannelGenerator {
	private MersenneTwister random;
	List<OpticalSuperChannel> opticalSuperChannelList;
	
	/**
	 * 
	 */
	public UniformOpticalSuperChannelGenerator(int seed, List<OpticalSuperChannel> opticalSuperChannelList ) {
		this.random = new MersenneTwister(seed);
		this.opticalSuperChannelList = opticalSuperChannelList;
	}
	
	
	private OpticalSuperChannel getRandomOpticalSuperChannel(){
		int r = random.nextInt(opticalSuperChannelList.size());
		for (OpticalSuperChannel oSC : opticalSuperChannelList) {
			if (oSC.getNumSubChannels() == r+1) {
				return oSC;
			} 
		}
		return null;
	}
	

	public OpticalSuperChannel getNextOpticalSuperChannel(){
		OpticalSuperChannel bw = getRandomOpticalSuperChannel();
		while (bw == null) {
			bw=getNextOpticalSuperChannel();
		}
		return bw;
	}
	
	public static void main (String[] args) {
		int seed = 666;
		
		System.out.println("Getting random optical super channels");
		ArrayList<OpticalSuperChannel> spaceSC_List = new ArrayList<OpticalSuperChannel>();
		for ( int i = 1 ; i < 10 ; i++ ) {
			spaceSC_List.add(new OpticalSuperChannel( i , 
			Constants.SIGNAL_BW_32GHz, Constants.CHANNEL_SPACING_50GHz, Constants.BANDGUARD_BW_12_5GHz, 
			Constants.SLOT_SIZE_BW_3_125GHz, ModulationFormat.MF_DP8QAM));
		}
		UniformOpticalSuperChannelGenerator gen = new UniformOpticalSuperChannelGenerator(seed , spaceSC_List);
		int a=0;
		int b=0;
		int c=0;
		int d=0;
		int e=0;
		int f=0;
		int g=0;
		int h=0;
		int i=0;
		
		
		for (int k = 0 ; k < 9000 ; k++){
			OpticalSuperChannel sc = gen.getNextOpticalSuperChannel();
			
			if (sc.getNumSubChannels() == 1)
				a++;
			else if (sc.getNumSubChannels() == 2)
				b++;
			else if (sc.getNumSubChannels() == 3)
				c++;
			else if (sc.getNumSubChannels() == 4)
				d++;
			else if (sc.getNumSubChannels() == 5)
				e++;
			else if (sc.getNumSubChannels() == 6)
				f++;
			else if (sc.getNumSubChannels() == 7)
				g++;
			else if (sc.getNumSubChannels() == 8)
				h++;
			else if (sc.getNumSubChannels() == 9)
				i++;
		}
		System.out.println(spaceSC_List.get(0).getCapacity() + " = " + a + ", # slots: " + spaceSC_List.get(0).getNumSlots());
		System.out.println(spaceSC_List.get(1).getCapacity() + " = " + b + ", # slots: " + spaceSC_List.get(1).getNumSlots());
		System.out.println(spaceSC_List.get(2).getCapacity() + " = " + c + ", # slots: " + spaceSC_List.get(2).getNumSlots());
		System.out.println(spaceSC_List.get(3).getCapacity() + " = " + d + ", # slots: " + spaceSC_List.get(3).getNumSlots());
		System.out.println(spaceSC_List.get(4).getCapacity() + " = " + e + ", # slots: " + spaceSC_List.get(4).getNumSlots());
		System.out.println(spaceSC_List.get(5).getCapacity() + " = " + f + ", # slots: " + spaceSC_List.get(5).getNumSlots());
		System.out.println(spaceSC_List.get(6).getCapacity() + " = " + g + ", # slots: " + spaceSC_List.get(6).getNumSlots());
		System.out.println(spaceSC_List.get(7).getCapacity() + " = " + h + ", # slots: " + spaceSC_List.get(7).getNumSlots());
		System.out.println(spaceSC_List.get(8).getCapacity() + " = " + i + ", # slots: " + spaceSC_List.get(8).getNumSlots());
		
	}
	

	

}
