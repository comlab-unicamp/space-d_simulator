package topology;

import graph.Grafo;
import opticalnetwork.elastica.NodeEON;

public class GermanNetwork2 extends Grafo{


	private static final long serialVersionUID = 1L;

	/**
	 *This class represents the German Network as a graph with 17 nodes and 26 edges.
	 *For distances between nodes and more details see the reference:
	 *	Betker, A.; Gerlach, C.; Hulsermann, et al, "Reference transport network scenarios",2004.
	 *	ONLINE: www.ikr.uni-stuttgart.de/IKRSimLib/Usage/Referenz_Netze_v14_full.pdf
	 *
	 */
	public GermanNetwork2() {
		super(NetworkTopology.GERMAN);
		try {
			createTopology();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private synchronized void createTopology() throws Exception{

		//Nodes of German Network Topology
		/* #    Name		population
		 * 1	Norden		25099
		 * 2	Bremen		546451
		 * 3	Hamburg		1751775
		 * 4	Berlin		3562166
		 * 5	Hannover	518386
		 * 6	Essen		569884
		 * 7	Dortmund	575944
		 * 8	Düsseldorf	598686
		 * 9	Leipzig		544479
		 * 10	Köln		1034175
		 * 11	Frankfurt	701350
		 * 12	Mannheim	296690
		 * 13	Nürnberg	498876
		 * 14	Karlsruhe	299103
		 * 15	Stuttgart	604297
		 * 16	Ulm			119218
		 * 17	München		1407836
		 *
		 * */
		adicionarNo(new NodeEON("1","Norden",this, 25099));
		adicionarNo(new NodeEON("2","Bremen",this, 546451));
		adicionarNo(new NodeEON("3","Hamburg",this, 1751775));
		adicionarNo(new NodeEON("4","Berlin",this, 3562166));
		adicionarNo(new NodeEON("5","Hannover",this, 518386));
		adicionarNo(new NodeEON("6","Essen",this, 569884));
		adicionarNo(new NodeEON("7","Dortmund",this, 575944));
		adicionarNo(new NodeEON("8","Düsseldorf",this, 598686));
		adicionarNo(new NodeEON("9","Leipzig",this, 544479));
		adicionarNo(new NodeEON("10","Köln",this, 1034175));
		adicionarNo(new NodeEON("11","Frankfurt",this, 701350));
		adicionarNo(new NodeEON("12","Mannheim",this, 296690));
		adicionarNo(new NodeEON("13","Nürnberg",this, 498876));
		adicionarNo(new NodeEON("14","Karlsruhe",this, 299103));
		adicionarNo(new NodeEON("15","Stuttgart",this, 604297));
		adicionarNo(new NodeEON("16","Ulm",this, 119218));
		adicionarNo(new NodeEON("17","München",this, 1407836));


		/* 26 links with distances between nodes*/
		adicionarEnlaceBidirecional(144, getNo("1"), getNo("2"));//Norden-Bremen No-HB
		adicionarEnlaceBidirecional(278, getNo("1"), getNo("7"));//Norden-Dortmund No-Do
		adicionarEnlaceBidirecional(114, getNo("2"), getNo("3"));//Bremen-Hamburg HB-HH
		adicionarEnlaceBidirecional(120, getNo("2"), getNo("5"));//Bremen-Hannover HB-H
		adicionarEnlaceBidirecional(306, getNo("3"), getNo("4"));//Hamburg-Berlin HH-B
		adicionarEnlaceBidirecional(157, getNo("3"), getNo("5"));//Hamburg-Hannover HH-H
		adicionarEnlaceBidirecional(298, getNo("4"), getNo("5"));//Berlin-Hannover B-H
		adicionarEnlaceBidirecional(174, getNo("4"), getNo("9"));//Berlin-Leipzig B-L
		adicionarEnlaceBidirecional(208, getNo("5"), getNo("7"));//Hannover-Dortmund H-Do
		adicionarEnlaceBidirecional(258, getNo("5"), getNo("9"));//Hannover-Leipzig H-L
		adicionarEnlaceBidirecional(316, getNo("5"), getNo("11"));//Hannover-Frankfurt H-F
		adicionarEnlaceBidirecional(37, getNo("6"), getNo("7"));//Essen-Dortmund E-Do
		adicionarEnlaceBidirecional(36, getNo("6"), getNo("8"));//Essen-Düsseldorf E-D
		adicionarEnlaceBidirecional(88, getNo("7"), getNo("10"));//Dortmund-Köln Do-K
		adicionarEnlaceBidirecional(41, getNo("8"), getNo("10"));//Düsseldorf-Köln D-K
		adicionarEnlaceBidirecional(353, getNo("9"), getNo("11"));//Leipzig-Frankfurt L-F
		adicionarEnlaceBidirecional(275, getNo("9"), getNo("13"));//Leipzig-Nürnberg L-N
		adicionarEnlaceBidirecional(182, getNo("10"), getNo("11"));//Köln-Frankfurt K-F
		adicionarEnlaceBidirecional(85, getNo("11"), getNo("12"));//Frankfurt-Mannheim F-Ma
		adicionarEnlaceBidirecional(224, getNo("11"), getNo("13"));//Frankfurt-Nürnberg N-F
		adicionarEnlaceBidirecional(64, getNo("12"), getNo("14"));//Mannheim-Karlsruhe Ma-Ka
		adicionarEnlaceBidirecional(187, getNo("13"), getNo("15"));//Nürnberg-Stuttgart N-S
		adicionarEnlaceBidirecional(179, getNo("13"), getNo("17"));//Nürnberg-München N-M
		adicionarEnlaceBidirecional(74, getNo("14"), getNo("15"));//Karlsruhe-Stuttgart Ka-S
		adicionarEnlaceBidirecional(86, getNo("15"), getNo("16"));//Stuttgart-Ulm S-Ul
		adicionarEnlaceBidirecional(143, getNo("16"), getNo("17"));//Ulm-München Ul-M

		installModelGraph();
	}

	public Grafo getGrafo() {
		return this;
	}
}
