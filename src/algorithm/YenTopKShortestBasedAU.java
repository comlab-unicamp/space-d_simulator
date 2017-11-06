/**
 * Created on 04/03/2016
 */
package algorithm;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import edu.asu.emit.qyan.alg.control.YenTopKShortestPathsAlg;
import edu.asu.emit.qyan.alg.model.Graph;
import edu.asu.emit.qyan.alg.model.Path;
import edu.asu.emit.qyan.alg.model.abstracts.BaseVertex;
import graph.AbstractGrafo;
import graph.Caminho;
import graph.ExcecaoGrafo;
import graph.No;

/**
 * @author Alaelson Jatobá
 * Version 1.0
 */
public class YenTopKShortestBasedAU extends KShortestPathFirstInterface{


	@SuppressWarnings("unused")
	private Graph _graph ;
	private YenTopKShortestPathsAlg yenAlg ;
	/**
	 *
	 */
	public YenTopKShortestBasedAU(Graph graph) {
		_graph = graph;
		yenAlg = new YenTopKShortestPathsAlg(graph);
	}
	/* (non-Javadoc)
	 * @see algoritmo.KShortestPathFirst#getKShortestPaths(graph.AbstractGrafo, graph.No, graph.No, int)
	 */
	@Override
	public synchronized KShortestPathList getKShortestPaths(AbstractGrafo graph, No source,
			No destination, int k) throws ExcecaoGrafo {
		int s = Integer.parseInt(source.getId());
		int d = Integer.parseInt(destination.getId());
		
		List<Path> shortest_paths_list = this.yenAlg.get_shortest_paths( graph.get_vertex(s), graph.get_vertex(d), k );

		KShortestPathList shortestPaths = new KShortestPathList();

		for (Path p : shortest_paths_list) {

			Vector<String> nodes = new Vector<>();
			Caminho path = new Caminho(source,destination);
			for (BaseVertex vertex : p.get_vertices()) {
				String id = vertex.toString();
				nodes.add(id);
				path.adicionarNo(graph.getNos().getNo(id));
			}

			Enumeration<String> enumNodes = nodes.elements();
			String left = null;
			if (enumNodes.hasMoreElements()) {
				left = enumNodes.nextElement();
			}

			if (left != null) {
				while (enumNodes.hasMoreElements()) {
					String right = enumNodes.nextElement();
					String key = left+"-"+right;
					path.adicionarEnlace(graph.getEnlace(key));
					left=right;
				}
			}

			path.setDistancia(p.get_weight());
			shortestPaths.add(path);

		}

		return shortestPaths;

	}



}
