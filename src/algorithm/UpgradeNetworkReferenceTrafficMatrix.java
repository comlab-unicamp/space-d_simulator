/**
 * Created on 16/09/2015
 */
package algorithm;

import java.util.ArrayList;
import java.util.HashMap;

import graph.AbstractGrafo;
import graph.Caminho;
import graph.Enlace;
import graph.ExcecaoGrafo;
import graph.No;
import opticalnetwork.elastica.NodeEON;
import opticalnetwork.elastica.rsa.TrafficMatrix;

/**
 * @author Alaelson Jatobá
 * Version 1.0
 */
public class UpgradeNetworkReferenceTrafficMatrix {



	//	private int metric;
	private AbstractGrafo graph;
	private TrafficMatrix matrix;

	/**
	 * The constructor.
	 * @param graph The graph representing a network.
	 * */
	public UpgradeNetworkReferenceTrafficMatrix (AbstractGrafo graph, TrafficMatrix matrix) {
		this.graph = graph;
		this.matrix = matrix;
	}

	/**
	 * Returs a list with all disabled links
	 * @return {@link ArrayList} a list with links
	 * */
	public ArrayList<Enlace> getDisabledLinks () {

		ArrayList<Enlace> tempLinks = new ArrayList<Enlace>();
		for (Enlace e : this.graph.getEnlaces().valores()) {
			if (!e.isAtivado()) {
				tempLinks.add(e);
			}
		}

		return tempLinks;

	}

	/**
	 * Returs a list with all disabled links
	 * @param graph the graph representing the network
	 * @return {@link ArrayList} a list with links
	 * */
	public ArrayList<Enlace> getDisabledLinks (AbstractGrafo graph) {

		ArrayList<Enlace> tempLinks = new ArrayList<Enlace>();
		for (Enlace e : graph.getEnlaces().valores()) {
			if (!e.isAtivado()) {
				tempLinks.add(e);
			}
		}

		return tempLinks;

	}



	/**
	 * Returns a link and the metric used to determine what
	 * link is indicated to be installed in order to upgrade the network
	 * @param alg is the {@link AlgorithmType} that indicates which algorithm is going to be executed.
	 * @return a HashMap with a {@link String} with Link's ID and a {@link Integer} as metric value.
	 *
	 * */
	public HashMap<String, Double> execute (AlgorithmType alg) throws ExcecaoGrafo {
		HashMap<String, Double> linkMetric = new HashMap<String,Double>();
		switch (alg) {
		case DIRECT:
			linkMetric = directOrderAlgorithm();
			break;

		case REVERSE:
			linkMetric = reverseOrderAlgorithm();
			break;

		default:
			break;
		}

		return linkMetric;

	}

	private double getSumTotalMetric () {
		HashMap<String, Caminho> routeTable = new HashMap<String, Caminho>();
		double metric = 0;
		try {
			ShortestPathFirst.runDijkstra(routeTable, this.graph);
			//			System.out.println("src-dst\tmetric\tpath");

			for (String key : routeTable.keySet()) {
				NodeEON source = (NodeEON) routeTable.get(key).getOrigem();
				NodeEON destination = (NodeEON) routeTable.get(key).getDestino();
				@SuppressWarnings("unused")
				int actual = source.getPopulationValue()+destination.getPopulationValue();
				metric = metric + matrix.getBandwidth(key);
				//				System.out.println(key + "\t" + actual+ "\t"+ routeTable.get(key));
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return metric;
	}

	private HashMap<String, Double> reverseOrderAlgorithm () throws ExcecaoGrafo {
		HashMap<String, Double> linkMetric = new HashMap<String,Double>();
		HashMap<String,Double> linkMetricTemp = new HashMap<String,Double>();
		ArrayList<Enlace> disabledLinks = getDisabledLinks();

		/*gets the total sum*/
		@SuppressWarnings("unused")
		double total = getSumTotalMetric();

		for (Enlace e : graph.getEnlaces().valores()) {

			No left = e.getNoEsquerda();
			No right = e.getNoDireita();
			Enlace ee = graph.getEnlace(right, left);

			if (!disabledLinks.contains(e)) {

				e.setAtivado(false); // activate a link
				ee.setAtivado(false);
				/*declares a temporary route table*/
				HashMap<String, Caminho> tempRouteTable = new HashMap<String, Caminho>();
				/*starts a variable metric to get the capacity of the roteable links */
				Double metric = 0.0;
				@SuppressWarnings("unused")
				String linkName = "";
				@SuppressWarnings("unused")
				Caminho path =  new Caminho();
				/*populates the temp route table*/
				ShortestPathFirst.runDijkstra(tempRouteTable, graph);

				for (String key : tempRouteTable.keySet()) {
					Caminho tempPath = tempRouteTable.get(key);
					//					NodeEON source = (NodeEON) tempPath.getOrigem();
					//					NodeEON destination = (NodeEON) tempPath.getDestino();
					if (!tempPath.getNos().valores().isEmpty()){
						metric = metric + matrix.getBandwidth(key);
					}
				}


				metric=metric/2;
				linkMetricTemp.put(e.getId(), metric);
				//				System.out.println(linkMetricTemp);
				e.setAtivado(true);
				ee.setAtivado(true);
				disabledLinks.add(e);
				disabledLinks.add(ee);
			} //test all routes using temp installed link
		} // end for that install one link per time
		String name = "";
		double value = -1;

		for (String key : linkMetricTemp.keySet()) {
			Double tempValue = linkMetricTemp.get(key);
			if (tempValue>value) {
				value = tempValue;
				name = key;
			}
		}

		linkMetric.put(name, value);

		//		System.out.println("value" + value);
		return linkMetric;
	}


	private HashMap<String, Double> directOrderAlgorithm () throws ExcecaoGrafo {
		/*storage the link and the metric*/
		HashMap<String,Double> linkMetricTemp = new HashMap<String,Double>();
		HashMap<String,Double> linkMetric = new HashMap<String,Double>();

		/*get only disable links from graph*/
		ArrayList<Enlace> disabledLinks = getDisabledLinks();

		for (Enlace e : graph.getEnlaces().valores()) {

			No left = e.getNoEsquerda();
			No right = e.getNoDireita();
			Enlace ee = graph.getEnlace(right, left);

			if (disabledLinks.contains(e)) {

				e.setAtivado(true); // activate a link
				ee.setAtivado(true);

				/*declares a temporary route table*/
				HashMap<String, Caminho> tempRouteTable = new HashMap<String, Caminho>();
				/*populates the temp route table*/
				ShortestPathFirst.runDijkstra(tempRouteTable, graph);
				//				print(tempRouteTable);
				double metric = 0;
				for (String key : tempRouteTable.keySet()) {
					Caminho tempPath = tempRouteTable.get(key);
					//					NodeEON source = (NodeEON) tempPath.getOrigem();
					//					NodeEON destination = (NodeEON) tempPath.getDestino();
					if (!tempPath.getNos().valores().isEmpty()){
						metric = metric + matrix.getBandwidth(key);
					}
				}
				metric=metric/2;
				linkMetricTemp.put(e.getId(), metric);
				e.setAtivado(false);
				ee.setAtivado(false);
				disabledLinks.remove(e);
				disabledLinks.remove(ee);
			} //test all routes using temp installed link
		} // end for that install one link per time
		String name = "";
		double value = 0;

		for (String key : linkMetricTemp.keySet()) {
			double tempValue = linkMetricTemp.get(key);
			if (tempValue>value) {
				value = tempValue;
				name = key;
			}
		}

		linkMetric.put(name, value);
		return linkMetric;

	}

	public Double upgradeByCongestedLink(AbstractGrafo graphMM, Enlace e, TrafficMatrix trafficMatrix)
			throws ExcecaoGrafo{
//		HashMap<String, Double> linkMetric = new HashMap<String,Double>();
		No left = e.getNoEsquerda();
		No right = e.getNoDireita();
		Enlace ee = graphMM.getEnlace(right, left);

		e.setAtivado(true); // activate a link
		ee.setAtivado(true);
		/*declares a temporary route table*/
		HashMap<String, Caminho> tempRouteTable = new HashMap<String, Caminho>();
		/*populates the temp route table*/
		ShortestPathFirst.runDijkstra(tempRouteTable, graphMM);
		//				print(tempRouteTable);
		double metric = 0;
		for (String key : tempRouteTable.keySet()) {
			Caminho tempPath = tempRouteTable.get(key);
			if (!tempPath.getNos().valores().isEmpty()){
				metric = metric + trafficMatrix.getBandwidth(key);
			}
		}
		metric=metric/2;
//		linkMetric.put(e.getId(), metric);
		return metric;

	}

}
