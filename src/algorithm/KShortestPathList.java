/**
 * Created on 26/02/2016
 */
package algorithm;

import graph.Caminho;

import java.util.PriorityQueue;

/**
 * @author Alaelson Jatobá
 * Version 1.0
 */
public class KShortestPathList {

	PriorityQueue<Caminho> pathsList;

	/**
	 *
	 */
	public KShortestPathList() {
		pathsList = new PriorityQueue<>();
	}

	public void add(Caminho path) {
		pathsList.add(path);
	}

	public Caminho poll(){
		return pathsList.poll();
	}

	public boolean remove(Caminho path) {
		return pathsList.remove(path);
	}

	public Caminho peek() {
		return pathsList.peek();
	}

	public PriorityQueue<Caminho> getPaths () {
		return pathsList;
	}

	public void setPaths( PriorityQueue<Caminho> paths) {
		pathsList = paths;
	}

	public int size() {
		return pathsList.size();
	}
	
	public String toString(){
		return pathsList.toString();
	}


}
