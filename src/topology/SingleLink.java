package topology;

import graph.Grafo;
import opticalnetwork.NoOptico;

public class SingleLink extends Grafo{


	private static final long serialVersionUID = 1L;

	/**
	 * Single link for tests proposes
	 *
	 */
	public SingleLink() {
		super(NetworkTopology.SINGLE_LINK);
		try {
			createTopology();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createTopology() throws Exception{

		adicionarNo(new NoOptico("1", "A", this));
		adicionarNo(new NoOptico("2", "B", this));

		/* 1 bidirectional links without distances between nodes*/
		adicionarEnlaceBidirecional(getNo("1"), getNo("2"));//1
		
		installModelGraph();
	}

	public Grafo getGrafo() {
		return this;
	}
}
