/**
 * Created on 03/03/2016
 */
package algorithm;

import graph.AbstractGrafo;
import graph.ExcecaoGrafo;
import graph.No;

/**
 * @author Alaelson Jatobá
 * Version 1.0
 */
public abstract class KShortestPathFirstInterface {

	protected AbstractGrafo graph;
	protected KShortestPathList kShortestPaths;

	/**
	 * @param kShortestPaths the kShortestPaths to set
	 */
	public void setkShortestPaths(KShortestPathList kShortestPaths) {
		this.kShortestPaths = kShortestPaths;
	}

	/**
	 * @return a {@link KShortestPathList} with the K shortest paths
	 * @throws ExcecaoGrafo
	 */
	public abstract KShortestPathList getKShortestPaths (AbstractGrafo graph, No source, No destination, int k) throws ExcecaoGrafo ;

}
