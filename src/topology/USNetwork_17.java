package topology;

import graph.Grafo;
import opticalnetwork.elastica.NodeEON;

public class USNetwork_17 extends Grafo {

	/**
	 * Two extra nodes were added in order to reduce the large distances 
	 * 
	 *This class represents the US Network as a graph with 14 nodes and 21 edges.
	 *For distances between nodes and more details see the reference:
	 *	Betker, A.; Gerlach, C.; Hulsermann, et al, "Reference transport network scenarios",2004.
	 *	ONLINE: www.ikr.uni-stuttgart.de/IKRSimLib/Usage/Referenz_Netze_v14_full.pdf
	 *
	 *
	 *	#	City			State	Population
	 *	1	Seattle			WA		668342
	 *	2	Palo Alto		CA1		66642
	 *	3	San Diego		CA2		1381069
	 *	4	Salt Lake City	UT		191180
	 *	5	Boulder			CO		105112
	 *	6	Houston			TX		2239558
	 *	7	Lincoln			NE		272996
	 *	8	Urbana			IL		41250
	 *	9	Atlanta			GA		447841
	 *	10	Ann Arbor		MI		116121
	 *	11	Pittsburgh		PA		305841
	 *	12	Washington		DC		658893
	 *	13	Princeton		NJ		30108
	 *	14	Ithaca			NY		30014
	 *
	 */
	private static final long serialVersionUID = 1L;

	public USNetwork_17() {
		super(NetworkTopology.US_NETWORK_17);
		try {
			create();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void create() throws Exception{
		adicionarNo(new NodeEON("1","WA",this,668342));
		adicionarNo(new NodeEON("2","CA1",this,66642));
		adicionarNo(new NodeEON("3","CA2",this,1381069));
		adicionarNo(new NodeEON("4","UT",this,191180));
		adicionarNo(new NodeEON("5","CO",this,105112));
		adicionarNo(new NodeEON("6","TX",this,2239558));
		adicionarNo(new NodeEON("7","NE",this,272996));
		adicionarNo(new NodeEON("8","IL",this,41250));
		adicionarNo(new NodeEON("9","GA",this,447841));
		adicionarNo(new NodeEON("10","MI",this,116121));
		adicionarNo(new NodeEON("11","PA",this,305841));
		adicionarNo(new NodeEON("12","DC",this,658893));
		adicionarNo(new NodeEON("13","NJ",this,30108));
		adicionarNo(new NodeEON("14","NY",this,30014));
		adicionarNo(new NodeEON("15","1-8",this,30014));
		adicionarNo(new NodeEON("16","4-10",this,30014));
		adicionarNo(new NodeEON("17","3-6",this,30014));
		
		// route nodes only
		this.addRouterNode(this.getNo("15"));
		this.addRouterNode(this.getNo("16"));
		this.addRouterNode(this.getNo("17"));
		

		//distancia em km, nó 1 e nó 2
		adicionarEnlaceBidirecional(1338, getNo("1"), getNo("2"));//WA-CA1
		adicionarEnlaceBidirecional(2556, getNo("1"), getNo("3"));//WA-CA2
//		adicionarEnlaceBidirecional(3408, getNo("1"), getNo("8"));//WA-IL
		adicionarEnlaceBidirecional(1704, getNo("1"), getNo("15"));//WA-IL
		adicionarEnlaceBidirecional(1704, getNo("15"), getNo("8"));//WA-IL
		adicionarEnlaceBidirecional(834, getNo("2"), getNo("3"));//CA1-CA2
		adicionarEnlaceBidirecional(1152, getNo("2"), getNo("4"));//CA1-UT
//		adicionarEnlaceBidirecional(2520, getNo("3"), getNo("6"));//CA2-TX
		adicionarEnlaceBidirecional(1260, getNo("3"), getNo("17"));//CA2-TX
		adicionarEnlaceBidirecional(1260, getNo("17"), getNo("6"));//CA2-TX
		adicionarEnlaceBidirecional(684, getNo("4"), getNo("5"));//UT-CO
//		adicionarEnlaceBidirecional(2820, getNo("4"), getNo("10"));//UT-MI
		adicionarEnlaceBidirecional(1410, getNo("4"), getNo("16"));//UT-MI
		adicionarEnlaceBidirecional(1410, getNo("16"), getNo("10"));//UT-MI
		adicionarEnlaceBidirecional(1746, getNo("5"), getNo("6"));//CO-TX
		adicionarEnlaceBidirecional(870, getNo("5"), getNo("7"));//CO-NE
		adicionarEnlaceBidirecional(1350, getNo("6"), getNo("9"));//TX-GA
		adicionarEnlaceBidirecional(2364, getNo("6"), getNo("12"));//TX-DC
		adicionarEnlaceBidirecional(864, getNo("7"), getNo("8"));//NE-IL
		adicionarEnlaceBidirecional(846, getNo("8"), getNo("11"));//IL-PA
		adicionarEnlaceBidirecional(1008, getNo("9"), getNo("11"));//GA-PA
		adicionarEnlaceBidirecional(942, getNo("10"), getNo("13"));//MI-NJ
		adicionarEnlaceBidirecional(720, getNo("10"), getNo("14"));//MI-NY
		adicionarEnlaceBidirecional(540, getNo("11"), getNo("13"));//PA-NJ
		adicionarEnlaceBidirecional(438, getNo("11"), getNo("14"));//PA-NY
		adicionarEnlaceBidirecional(312, getNo("12"), getNo("13"));//DC-NJ
		adicionarEnlaceBidirecional(468, getNo("12"), getNo("14"));//DC-NY

		installModelGraph();
		
		setSize(14);

	}

	public Grafo getGrafo() {
		return this;
	}
}
