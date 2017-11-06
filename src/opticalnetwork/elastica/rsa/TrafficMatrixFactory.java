/**
 * Created on 11/09/2015
 */
package opticalnetwork.elastica.rsa;

import java.util.HashMap;

import algorithm.Dijkstra;
import graph.AbstractGrafo;
import graph.Caminho;
import graph.ExcecaoGrafo;
import graph.Grafo;
import graph.No;
import topology.NSFNet;

/**
 * @author Alaelson Jatobá
 * Version 1.0
 */
public class TrafficMatrixFactory {

	private TrafficMatrix matrix;
	private HashMap<String, Caminho> routeTable;
	private AbstractGrafo graph;

	/**
	 * Generates a traffic matrix
	 */
	public TrafficMatrixFactory(AbstractGrafo graph) {
		this.matrix = new TrafficMatrix();
		this.graph = graph;
		this.routeTable = new HashMap<String,Caminho>();
		try {
			doRouteTable();
//			for (String s : routeTable.keySet()) {
//				matrix.addTrafficInMatrixSimple(s, 0);
//			}

		} catch (Exception e) {
			System.out.println("TrafficMatrixFactory class ---> " + e.getMessage());
		}


	}


	/**
	 * Sets a traffic value with a key
	 * */
	public void setTraffic (String key, double value) {
		matrix.addTrafficInMatrixSimple(key, value);
	}

	/**
	 * Does the Route Table
	 *
	 * */
	public void doRouteTable () throws ExcecaoGrafo {
		runDijkstraSPF(this.routeTable, this.graph);
	}

	//public HashMap<String, Caminho> getAllRoutes (int seed, AbstractGrafo graph, HashMap<String, Integer> bandwidthList) throws ExcecaoGrafo {
	public HashMap<String, Caminho> doRouteTable (AbstractGrafo graph) throws ExcecaoGrafo {
		HashMap<String, Caminho> routeTable = new HashMap<String, Caminho>();
		runDijkstraSPF(routeTable, graph);
		return routeTable;

	}

	public HashMap<String, Caminho> getRouteTable () throws ExcecaoGrafo {
		return routeTable;
	}

	public void runDijkstraSPF (HashMap<String, Caminho> routeTable, AbstractGrafo graph) throws ExcecaoGrafo {
		Caminho path = null;
		for (No source : graph.getNos().valores()) {

			for (No node : graph.getNos().valores()) {
				if (source != node) {
					String key = source.getId()+"-"+node.getId();
					path = Dijkstra.getMenorCaminho(graph, source, node);

					routeTable.put(key, path);
				}
			}
		}
	}


	/**
	 * @return the matrix
	 */
	public TrafficMatrix getMatrix() {
		return matrix;
	}


	/**
	 * @param matrix the matrix to set
	 */
	public void setMatrix(TrafficMatrix matrix) {
		this.matrix = matrix;
	}

	public void calculateMatrixTotalCapacity () throws ExcecaoGrafo {
//			ShortestPathFirst.runDijkstra(routeTable, graph);
			double metric = 0;
			for (String key : routeTable.keySet()) {
				Caminho tempPath = routeTable.get(key);
				if (!tempPath.getNos().valores().isEmpty()){
					metric = metric + matrix.getBandwidth(key);
				}
			}
			metric = metric/2;
			matrix.setTotalCapacity(metric);
	}


	public static void main(String [] args) {

		Grafo nsfnet = new NSFNet();
		TrafficMatrixFactory factory = new TrafficMatrixFactory(nsfnet);

		try {
			HashMap<String, Caminho> routeTable = factory.doRouteTable(nsfnet);
			System.out.println("src-dst\tpath");
			for (String key : routeTable.keySet()) {
				System.out.println(key + 	"\t" + routeTable.get(key));
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}


	}

}
