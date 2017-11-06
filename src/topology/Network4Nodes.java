package topology;

import graph.Grafo;
import opticalnetwork.NoOptico;

public class Network4Nodes extends Grafo{


	private static final long serialVersionUID = 1L;

	/**
	 * Single link for tests proposes
	 *
	 */
	public Network4Nodes() {
		super(NetworkTopology.NETWORK4NODES);
		try {
			createTopology();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createTopology() throws Exception{

		adicionarNo(new NoOptico("1", "1", this));
		adicionarNo(new NoOptico("2", "2", this));
		adicionarNo(new NoOptico("3", "3", this));
		adicionarNo(new NoOptico("4", "4", this));
		

		/*4 bidirectional links with distances between nodes*/
		adicionarEnlaceBidirecional( 1100, getNo("1"), getNo("2") );
		adicionarEnlaceBidirecional( 1750, getNo("2"), getNo("3") );
		adicionarEnlaceBidirecional( 1500, getNo("3"), getNo("4") );
		adicionarEnlaceBidirecional( 1850, getNo("4"), getNo("1") );
		
		
		//installs the vertices and edges in Arizona University Graph Library
		installModelGraph();
	}

	public Grafo getGrafo() {
		return this;
	}
}
