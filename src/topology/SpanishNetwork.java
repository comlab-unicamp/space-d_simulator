package topology;

import graph.Grafo;
import opticalnetwork.NoOptico;

public class SpanishNetwork extends Grafo{


	private static final long serialVersionUID = 1L;

	/**
	 *This class represents the Spanish Backbone Network as a graph with 30 nodes and 56 biderectional edges.
	 *For distances between nodes and more details see the reference:
	 *	Franz Rambach et al, "A Multilayer Cost Model for Metro/Core Networks,"
	 *  J. Opt. Commun. Netw. 5, 210-225 (2013)
	 *
	 */
	public SpanishNetwork() {
		super(NetworkTopology.SPANISH);
		try {
			createTopology();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createTopology() throws Exception{

		//Nodes of German Network Topology
		/* #    Name		population
		 * 1	A4
		 * 2	A9
		 * 3	A14
		 * 4	A17
		 * 5	A23
		 * 6	A28
		 * 7	B3
		 * 8	B4
		 * 9	B9
		 * 10	B16
		 * 11	B22
		 * 12	B28
		 * 13	C6
		 * 14	C8
		 * 15	C12
		 * 16	C17
		 * 17	C24
		 * 18	C29
		 * 19	D4
		 * 20	D6
		 * 21	D14
		 * 22	D16
		 * 23	D20
		 * 24	D30
		 * 25	E5
		 * 26	E8
		 * 27	E15
		 * 28	E17
		 * 29	E20
		 * 30	E28
		 *
		 * */
		adicionarNo(new NoOptico("1", "A4", this));
		adicionarNo(new NoOptico("2", "A9", this));
		adicionarNo(new NoOptico("3", "A14", this));
		adicionarNo(new NoOptico("4", "A17", this));
		adicionarNo(new NoOptico("5", "A23", this));
		adicionarNo(new NoOptico("6", "A28", this));
		adicionarNo(new NoOptico("7", "B3", this));
		adicionarNo(new NoOptico("8", "B4", this));
		adicionarNo(new NoOptico("9", "B9", this));
		adicionarNo(new NoOptico("10", "B16", this));
		adicionarNo(new NoOptico("11", "B22", this));
		adicionarNo(new NoOptico("12", "B28", this));
		adicionarNo(new NoOptico("13", "C6", this));
		adicionarNo(new NoOptico("14", "C8", this));
		adicionarNo(new NoOptico("15", "C12", this));
		adicionarNo(new NoOptico("16", "C17", this));
		adicionarNo(new NoOptico("17", "C24", this));
		adicionarNo(new NoOptico("18", "C29", this));
		adicionarNo(new NoOptico("19", "D4", this));
		adicionarNo(new NoOptico("20", "D6", this));
		adicionarNo(new NoOptico("21", "D14", this));
		adicionarNo(new NoOptico("22", "D16", this));
		adicionarNo(new NoOptico("23", "D20", this));
		adicionarNo(new NoOptico("24", "D30", this));
		adicionarNo(new NoOptico("25", "E5", this));
		adicionarNo(new NoOptico("26", "E8", this));
		adicionarNo(new NoOptico("27", "E15", this));
		adicionarNo(new NoOptico("28", "E17", this));
		adicionarNo(new NoOptico("29", "E20", this));
		adicionarNo(new NoOptico("30", "E28", this));
		
		// 16 nodes used only for forwarding 
		this.addRouterNode(this.getNodeByName("A4"));
		this.addRouterNode(this.getNodeByName("A14"));
		this.addRouterNode(this.getNodeByName("A17"));
		this.addRouterNode(this.getNodeByName("A28"));
		this.addRouterNode(this.getNodeByName("B3"));
		this.addRouterNode(this.getNodeByName("B16"));
		this.addRouterNode(this.getNodeByName("C12"));
		this.addRouterNode(this.getNodeByName("C17"));
		this.addRouterNode(this.getNodeByName("C24"));
		this.addRouterNode(this.getNodeByName("D4"));
		this.addRouterNode(this.getNodeByName("D14"));
		this.addRouterNode(this.getNodeByName("D16"));
		this.addRouterNode(this.getNodeByName("E5"));
		this.addRouterNode(this.getNodeByName("E8"));
		this.addRouterNode(this.getNodeByName("E15"));
		this.addRouterNode(this.getNodeByName("E28"));
		

		/* 56 bidirectional links without distances between nodes*/
		adicionarEnlaceBidirecional(getNo("1"), getNo("2"));//1
		adicionarEnlaceBidirecional(getNo("1"), getNo("3"));
		adicionarEnlaceBidirecional(getNo("1"), getNo("4"));
		adicionarEnlaceBidirecional(getNo("2"), getNo("5"));
		adicionarEnlaceBidirecional(getNo("2"), getNo("6"));//5
		adicionarEnlaceBidirecional(getNo("3"), getNo("4"));
		adicionarEnlaceBidirecional(getNo("3"), getNo("12"));
		adicionarEnlaceBidirecional(getNo("4"), getNo("6"));
		adicionarEnlaceBidirecional(getNo("5"), getNo("8"));
		adicionarEnlaceBidirecional(getNo("5"), getNo("25"));//10
		adicionarEnlaceBidirecional(getNo("5"), getNo("26"));
		adicionarEnlaceBidirecional(getNo("6"), getNo("9"));
		adicionarEnlaceBidirecional(getNo("6"), getNo("10"));
		adicionarEnlaceBidirecional(getNo("6"), getNo("12"));
		adicionarEnlaceBidirecional(getNo("7"), getNo("8"));//15
		adicionarEnlaceBidirecional(getNo("7"), getNo("9"));
		adicionarEnlaceBidirecional(getNo("7"), getNo("19"));
		adicionarEnlaceBidirecional(getNo("7"), getNo("25"));
		adicionarEnlaceBidirecional(getNo("8"), getNo("9"));
		adicionarEnlaceBidirecional(getNo("9"), getNo("10"));//20
		adicionarEnlaceBidirecional(getNo("9"), getNo("17"));
		adicionarEnlaceBidirecional(getNo("10"), getNo("11"));
		adicionarEnlaceBidirecional(getNo("10"), getNo("16"));
		adicionarEnlaceBidirecional(getNo("11"), getNo("12"));
		adicionarEnlaceBidirecional(getNo("11"), getNo("15"));//25
		adicionarEnlaceBidirecional(getNo("11"), getNo("16"));
		adicionarEnlaceBidirecional(getNo("13"), getNo("14"));
		adicionarEnlaceBidirecional(getNo("13"), getNo("15"));
		adicionarEnlaceBidirecional(getNo("13"), getNo("18"));
		adicionarEnlaceBidirecional(getNo("14"), getNo("15"));//30
		adicionarEnlaceBidirecional(getNo("14"), getNo("18"));
		adicionarEnlaceBidirecional(getNo("15"), getNo("16"));
		adicionarEnlaceBidirecional(getNo("16"), getNo("18"));
		adicionarEnlaceBidirecional(getNo("17"), getNo("18"));
		adicionarEnlaceBidirecional(getNo("17"), getNo("19"));//35
		adicionarEnlaceBidirecional(getNo("17"), getNo("20"));
		adicionarEnlaceBidirecional(getNo("18"), getNo("20"));
		adicionarEnlaceBidirecional(getNo("19"), getNo("20"));
		adicionarEnlaceBidirecional(getNo("19"), getNo("21"));
		adicionarEnlaceBidirecional(getNo("19"), getNo("27"));//40
		adicionarEnlaceBidirecional(getNo("20"), getNo("21"));
		adicionarEnlaceBidirecional(getNo("21"), getNo("22"));
		adicionarEnlaceBidirecional(getNo("22"), getNo("23"));
		adicionarEnlaceBidirecional(getNo("22"), getNo("24"));
		adicionarEnlaceBidirecional(getNo("23"), getNo("24"));//45
		adicionarEnlaceBidirecional(getNo("23"), getNo("27"));
		adicionarEnlaceBidirecional(getNo("23"), getNo("28"));
		adicionarEnlaceBidirecional(getNo("24"), getNo("30"));
		adicionarEnlaceBidirecional(getNo("25"), getNo("26"));
		adicionarEnlaceBidirecional(getNo("25"), getNo("27"));//50
		adicionarEnlaceBidirecional(getNo("25"), getNo("28"));
		adicionarEnlaceBidirecional(getNo("26"), getNo("28"));
		adicionarEnlaceBidirecional(getNo("26"), getNo("29"));
		adicionarEnlaceBidirecional(getNo("28"), getNo("29"));
		adicionarEnlaceBidirecional(getNo("28"), getNo("30"));//55
		adicionarEnlaceBidirecional(getNo("29"), getNo("30"));
		
		installModelGraph();

	}

	public Grafo getGrafo() {
		return this;
	}
}
