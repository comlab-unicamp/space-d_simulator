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

/**
 * @author Alaelson Jatobá
 * Version 1.0
 */
public class UpgradeNetwork {


	//	private int metric;
	private AbstractGrafo graph;

	/**
	 * The constructor.
	 * @param graph The graph representing a network.
	 * */
	public UpgradeNetwork (AbstractGrafo graph) {
		this.graph = graph;
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
	 * Returns a link and the metric used to determine what
	 * link is indicated to be installed in order to upgrade the network
	 * @param alg is the {@link AlgorithmType} that indicates which algorithm is going to be executed.
	 * @return a HashMap with a {@link String} with Link's ID and a {@link Integer} as metric value.
	 *
	 * */
	public HashMap<String, Integer> execute (AlgorithmType alg) throws ExcecaoGrafo {
		HashMap<String,Integer> linkMetric = new HashMap<String,Integer>();
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

	public int getSumTotalMetric () {
		HashMap<String, Caminho> routeTable = new HashMap<String, Caminho>();
		int metric = 0;
		try {
			ShortestPathFirst.runDijkstra(routeTable, this.graph);
			//			System.out.println("src-dst\tmetric\tpath");

			for (String key : routeTable.keySet()) {
				NodeEON source = (NodeEON) routeTable.get(key).getOrigem();
				NodeEON destination = (NodeEON) routeTable.get(key).getDestino();
//				int actual = source.getPopulationValue()+destination.getPopulationValue();
				metric = metric + source.getPopulationValue()+destination.getPopulationValue();
				//				System.out.println(key + "\t" + actual+ "\t"+ routeTable.get(key));
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return metric;
	}

	private HashMap<String, Integer> reverseOrderAlgorithm () throws ExcecaoGrafo {
		HashMap<String,Integer> linkMetric = new HashMap<String,Integer>();
		HashMap<String,Integer> linkMetricTemp = new HashMap<String,Integer>();
		ArrayList<Enlace> disabledLinks = getDisabledLinks();

		/*gets the total sum*/
//		int total = getSumTotalMetric();

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
				int metric = 0;
//				String linkName = "";
//				Caminho path =  new Caminho();
				/*populates the temp route table*/
				ShortestPathFirst.runDijkstra(tempRouteTable, graph);

				for (String key : tempRouteTable.keySet()) {
					Caminho tempPath = tempRouteTable.get(key);
					NodeEON source = (NodeEON) tempPath.getOrigem();
					NodeEON destination = (NodeEON) tempPath.getDestino();
					if (!tempPath.getNos().valores().isEmpty()){
						metric = metric + (source.getPopulationValue() + destination.getPopulationValue());
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
		int value = -1;

		for (String key : linkMetricTemp.keySet()) {
			int tempValue = linkMetricTemp.get(key);
			if (tempValue>value) {
				value = tempValue;
				name = key;
			}
		}

		linkMetric.put(name, value);

		//		System.out.println("value" + value);
		return linkMetric;
	}


	private HashMap<String, Integer> directOrderAlgorithm () throws ExcecaoGrafo {
		/*storage the link and the metric*/
		HashMap<String,Integer> linkMetricTemp = new HashMap<String,Integer>();
		HashMap<String,Integer> linkMetric = new HashMap<String,Integer>();

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
				int metric = 0;
//				String linkName = "";
//				Caminho path =  new Caminho();
				for (String key : tempRouteTable.keySet()) {
					Caminho tempPath = tempRouteTable.get(key);
					NodeEON source = (NodeEON) tempPath.getOrigem();
					NodeEON destination = (NodeEON) tempPath.getDestino();
					if (!tempPath.getNos().valores().isEmpty()){
						metric = metric + (source.getPopulationValue() + destination.getPopulationValue());
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
		int value = 0;

		for (String key : linkMetricTemp.keySet()) {
			int tempValue = linkMetricTemp.get(key);
			if (tempValue>value) {
				value = tempValue;
				name = key;
			}
		}

		linkMetric.put(name, value);
		return linkMetric;

	}

}
