/**
 * Created on 15/09/2015
 */
package algorithm;

import graph.AbstractGrafo;
import graph.Caminho;
import graph.ExcecaoGrafo;
import graph.No;

import java.util.HashMap;

/**
 * @author Alaelson Jatobá
 * Version 1.0
 */
public class ShortestPathFirst {

	public static void runDijkstra (HashMap<String, Caminho> routeTable, AbstractGrafo graph) throws ExcecaoGrafo {
		Caminho path = null;
		for (No source : graph.getNos().valores()) {

			for (No node : graph.getNos().valores()) {
				if (source != node) {
					String key = source.getId()+"-"+node.getId();
					path = Dijkstra.getMenorCaminho(graph, source, node);
//					System.out.println(key+"\t"+ path + "\t" + path.getDistancia());
					routeTable.put(key, path);
				}
			}
		}
	}

}
