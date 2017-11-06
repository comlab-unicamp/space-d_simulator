/**
 *
 */
package util;

/**
 * Created in 01/03/2016
 * By @author Alaelson Jatoba
 * @version 1.0
 */
public class Counter {
	
	private int value = 0;
	
	/**
	 * 
	 */
	public Counter() {
		
	}

	/**
	 * @return the counter's value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @param value the counter's value to set
	 */
	public void setValue(int value) {
		this.value = value;
	}
	
	/**
	 * Increment the value of counter
	 */
	public void increment () {
		this.value++;
	}
	
	/**
	 * Decrement the value of counter
	 */
	public void decrement () {
		this.value++;
	}

}
