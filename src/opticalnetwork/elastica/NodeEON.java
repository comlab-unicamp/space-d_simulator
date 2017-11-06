/**
 *
 */
package opticalnetwork.elastica;

import graph.AbstractGrafo;
import opticalnetwork.NoOptico;

/**
 * Created in 18/08/2015
 * By @author Alaelson Jatoba
 * @version 1.0
 */
public class NodeEON extends NoOptico {

	private int populationValue;

	/**
	 *
	 * The Constructor:
	 * @param numberID the identifier
	 * @param name The Node's name
	 * @param grafo The Graph that this node is contained
	 * @param populationValue A population proportional value;
	 */
	public NodeEON(String numberID, String name, AbstractGrafo grafo, int populationValue) {
		super(numberID, name, grafo);
		this.populationValue = populationValue;
	}

	/**
	 * @return the populationValue
	 */
	public int getPopulationValue() {
		return populationValue;
	}

	/**
	 * @param populationValue the populationValue to set
	 */
	public void setPopulationValue(int populationValue) {
		this.populationValue = populationValue;
	}



}
