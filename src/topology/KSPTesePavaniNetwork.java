package topology;

import graph.Grafo;
import opticalnetwork.NoOptico;

public class KSPTesePavaniNetwork extends Grafo{


	private static final long serialVersionUID = 1L;

	/**
	 * Single link for tests proposes
	 *
	 */
	public KSPTesePavaniNetwork() {
		super(NetworkTopology.KSP_PAVANI_TESE);
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
		adicionarNo(new NoOptico("5", "5", this));
		adicionarNo(new NoOptico("6", "6", this));
		adicionarNo(new NoOptico("7", "7", this));

		/* 10 bidirectional links with distances between nodes*/
		adicionarEnlaceBidirecional( 1, getNo("1"), getNo("2") );//1-2 1
		adicionarEnlaceBidirecional( 5, getNo("1"), getNo("3") );//1-3 5
		adicionarEnlaceBidirecional( 6, getNo("1"), getNo("4") );//1-4 6
		adicionarEnlaceBidirecional( 3, getNo("2"), getNo("3") );//2-3 3
		adicionarEnlaceBidirecional( 3, getNo("2"), getNo("6") );//2-6 3
		adicionarEnlaceBidirecional( 3, getNo("3"), getNo("5") );//3-5 3
		adicionarEnlaceBidirecional( 5, getNo("4"), getNo("7") );//4-7 5
		adicionarEnlaceBidirecional( 2, getNo("5"), getNo("6") );//5-6 2
		adicionarEnlaceBidirecional( 2, getNo("5"), getNo("7") );//5-7 2
		adicionarEnlaceBidirecional( 3, getNo("6"), getNo("7") );//6-7 3
		
		//installs the vertices and edges in Arizona University Graph Library
		installModelGraph();
	}

	public Grafo getGrafo() {
		return this;
	}
}
