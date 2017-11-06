/**
 *
 */
package util;

import java.util.List;

/**
 * Created in 09/09/2016
 * By @author Alaelson Jatoba
 * @version 1.0
 */
public class Mathematics {
	
	/**
	 * Return the arithmetic mean of a list with numbers 
	 * @param a the list with numbers
	 * @return
	 */
	public static double mean (List<? extends Number> a){
		int sum = sum(a);
		double mean = 0;
		mean = sum / (a.size() * 1.0);
		return mean;
	}
	
	/**
	 * Executes the sum for the numbers in a list
	 * @param numbers a list of numbers
	 * @return
	 */
	public static int sum (List<? extends Number> numbers){
		if (numbers.size() > 0) {
			int sum = 0;

			for ( int i = 0 ; i < numbers.size() ; i++) {
				if (numbers.get(i) instanceof Double ) {
					sum += numbers.get(i).intValue();
				} else {
					sum += (int) numbers.get(i);
				}
			}
			return sum;
		}
		return 0;
	}
	
	public static double standardDeviation (List<? extends Number> numbers){
		return 0.0;
	}

	/**
	 * The standard error (SE) is the standard deviation of the sampling distribution 
	 * of a statistic,[1] most commonly of the mean. The term may also be used to refer 
	 * to an estimate of that standard deviation, derived from a particular sample used 
	 * to compute the estimate.
	 * @return
	 */
	public static double standardError (){
		return 0.0;
	}
	
	public static Double[] getConfidenceInterval () {
		return null;
	}
	
	
}
