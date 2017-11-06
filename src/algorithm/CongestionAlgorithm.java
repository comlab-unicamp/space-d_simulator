/**
 * Created on 29/09/2015
 */
package algorithm;

import graph.AbstractGrafo;
import graph.Caminho;
import graph.Enlace;
import graph.ExcecaoGrafo;
import opticalnetwork.elastica.LinkUtilization;
import opticalnetwork.elastica.rsa.TrafficMatrix;

import java.io.Serializable;
import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * @author Alaelson Jatobá
 * Version 1.0
 */
public class CongestionAlgorithm implements Serializable{



	/**
	 *
	 */
	private static final long serialVersionUID = -4061238972454093915L;
	private AbstractGrafo graph;
	private TrafficMatrix trafficMatrix;
	private HashMap<String, Caminho> routeTable;
	private PriorityQueue<LinkUtilization> linkUtilizationList;

	public CongestionAlgorithm (AbstractGrafo graph, TrafficMatrix trafficMatrix) throws ExcecaoGrafo {
		this.graph = graph;
		this.trafficMatrix = trafficMatrix;
		this.routeTable = new HashMap<>();
		this.linkUtilizationList = new PriorityQueue<LinkUtilization>();
		ShortestPathFirst.runDijkstra(routeTable, graph);
	}

	public Enlace getCongestedLink() {
		return linkUtilizationList.peek().getLink();
	}

	public void sumUtilizationPerLink() {
		for (Enlace link : this.graph.getEnlaces().valores()) {
			double utilization = 0.0;
			for (Caminho path : routeTable.values()) {
				if (path.contemEnlace(link)) {
					String key = path.getOrigem().getId()+"-"+path.getDestino().getId();
					utilization += trafficMatrix.getBandwidth(key);
				}
			}
			//			utilization = trafficMatrix.getBandwidth(link.getId());
			linkUtilizationList.add(new LinkUtilization(link, utilization));
		}

	}

	/**
	 * @return the linkUtilizationList
	 */
	public PriorityQueue<LinkUtilization> getLinkUtilizationList() {
		return linkUtilizationList;
	}

	/**
	 * @param linkUtilizationList the linkUtilizationList to set
	 */
	public void setLinkUtilizationList(
			PriorityQueue<LinkUtilization> linkUtilizationList) {
		this.linkUtilizationList = linkUtilizationList;
	}


}
