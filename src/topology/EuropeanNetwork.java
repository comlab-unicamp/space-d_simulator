package topology;

import graph.Grafo;
import opticalnetwork.NoOptico;

public class EuropeanNetwork extends Grafo{


	private static final long serialVersionUID = 1L;

	/**
	 *This class represents the German Network as a graph with 17 nodes and 26 edges.
	 *For distances between nodes and more details see the reference:
	 *	Betker, A.; Gerlach, C.; Hulsermann, et al, "Reference transport network scenarios",2004.
	 *	ONLINE: www.ikr.uni-stuttgart.de/IKRSimLib/Usage/Referenz_Netze_v14_full.pdf
	 *
	 */
	public EuropeanNetwork() {
		super(NetworkTopology.EUROPEAN);
		try {
			createTopology();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Amsterdam
	 * Athens
	 * Barcelona
	 * Belgrade
	 * Berlin
	 * Bordeaux
	 * Brussels
	 * Budapest
	 * Copenhagen
	 * Dublin
	 * Frankfurt
	 * Glasgow
	 * Hamburg
	 * London
	 * Lyon
	 * Madrid
	 * Milan
	 * Munich
	 * Oslo
	 * Paris
	 * Prague
	 * Rome
	 * Stockholm
	 * Strasbourg
	 * Vienna
	 * Warsaw
	 * Zagreb
	 * Zurich
*/


	private void createTopology() throws Exception{

		adicionarNo(new NoOptico("1","Amsterdam",this));
		adicionarNo(new NoOptico("2","Athens",this));
		adicionarNo(new NoOptico("3","Barcelona",this));
		adicionarNo(new NoOptico("4","Belgrade",this));
		adicionarNo(new NoOptico("5","Berlin",this));
		adicionarNo(new NoOptico("6","Bordeaux",this));
		adicionarNo(new NoOptico("7","Brussels",this));
		adicionarNo(new NoOptico("8","Budapest",this));
		adicionarNo(new NoOptico("9","Copenhagen",this));
		adicionarNo(new NoOptico("10","Dublin",this));
		adicionarNo(new NoOptico("11","Frankfurt",this));
		adicionarNo(new NoOptico("12","Glasgow",this));
		adicionarNo(new NoOptico("13","Hamburg",this));
		adicionarNo(new NoOptico("14","London",this));
		adicionarNo(new NoOptico("15","Lyon",this));
		adicionarNo(new NoOptico("16","Madrid",this));
		adicionarNo(new NoOptico("17","Milan",this));
		adicionarNo(new NoOptico("18","Munich",this));
		adicionarNo(new NoOptico("19","Oslo",this));
		adicionarNo(new NoOptico("20","Paris",this));
		adicionarNo(new NoOptico("21","Prague",this));
		adicionarNo(new NoOptico("22","Rome",this));
		adicionarNo(new NoOptico("23","Stockholm",this));
		adicionarNo(new NoOptico("24","Strasbourg",this));
		adicionarNo(new NoOptico("25","Vienna",this));
		adicionarNo(new NoOptico("26","Warsaw",this));
		adicionarNo(new NoOptico("27","Zagreb",this));
		adicionarNo(new NoOptico("28","Zurich",this));

		/* 26 links with distances between nodes*/
		adicionarEnlaceBidirecional(259, getNo("1"), getNo("7"));//Amsterdam-Brussels
		adicionarEnlaceBidirecional(1067, getNo("1"), getNo("12"));//Amsterdam-Glasgow
		adicionarEnlaceBidirecional(552, getNo("1"), getNo("13"));//Amsterdam-Hamburg
		adicionarEnlaceBidirecional(540, getNo("1"), getNo("14"));//Amsterdam-London
		adicionarEnlaceBidirecional(1209, getNo("2"), getNo("4"));//Athens-Belgrade
		adicionarEnlaceBidirecional(1500, getNo("2"), getNo("22"));//Athens-Rome
		adicionarEnlaceBidirecional(796, getNo("3"), getNo("15"));//Barcelona-Lyon
		adicionarEnlaceBidirecional(760, getNo("3"), getNo("16"));//Barcelona-Madrid
		adicionarEnlaceBidirecional(474, getNo("4"), getNo("8"));//Belgrade-Budapest
		adicionarEnlaceBidirecional(551, getNo("4"), getNo("27"));//Belgrade-Zagreb
		adicionarEnlaceBidirecional(540, getNo("5"), getNo("9"));//Berlin-Copenhagen
		adicionarEnlaceBidirecional(381, getNo("5"), getNo("13"));//Berlin-Hamburg
		adicionarEnlaceBidirecional(757, getNo("5"), getNo("18"));//Berlin-Munich
		adicionarEnlaceBidirecional(420, getNo("5"), getNo("21"));//Berlin-Prague
		adicionarEnlaceBidirecional(834, getNo("6"), getNo("16"));//Bordeaux-Madrid
		adicionarEnlaceBidirecional(747, getNo("6"), getNo("20"));//Bordeaux-Paris
		adicionarEnlaceBidirecional(474, getNo("7"), getNo("11"));//Brussels-Frankfurt
		adicionarEnlaceBidirecional(393, getNo("7"), getNo("20"));//Brussels-Paris
		adicionarEnlaceBidirecional(668, getNo("8"), getNo("21"));//Budapest-Prague
		adicionarEnlaceBidirecional(819, getNo("8"), getNo("26"));//Budapest-Warsaw
		adicionarEnlaceBidirecional(722, getNo("9"), getNo("26"));//Copenhagen-Oslo
		adicionarEnlaceBidirecional(462, getNo("10"), getNo("12"));//Dublin-Glasgow
		adicionarEnlaceBidirecional(690, getNo("10"), getNo("14"));//Dublin-London
		adicionarEnlaceBidirecional(592, getNo("11"), getNo("13"));//Frankfurt-Hamburg
		adicionarEnlaceBidirecional(456, getNo("11"), getNo("18"));//Frankfurt-Munich
		adicionarEnlaceBidirecional(271, getNo("11"), getNo("24"));//Frankfurt-Strasbourg
		adicionarEnlaceBidirecional(514, getNo("14"), getNo("20"));//London-Paris
		adicionarEnlaceBidirecional(594, getNo("15"), getNo("20"));//Lyon-Paris
		adicionarEnlaceBidirecional(507, getNo("15"), getNo("28"));//Lyon-Zurich
		adicionarEnlaceBidirecional(522, getNo("17"), getNo("18"));//Milan-Munich
		adicionarEnlaceBidirecional(720, getNo("17"), getNo("22"));//Milan-Rome
		adicionarEnlaceBidirecional(327, getNo("17"), getNo("28"));//Milan-Zurich
		adicionarEnlaceBidirecional(534, getNo("18"), getNo("25"));//Munich-Vienna
		adicionarEnlaceBidirecional(623, getNo("19"), getNo("23"));//Oslo-Stockholm
		adicionarEnlaceBidirecional(600, getNo("20"), getNo("24"));//Paris-Strasbourg
		adicionarEnlaceBidirecional(376, getNo("21"), getNo("25"));//Prague-Vienna
		adicionarEnlaceBidirecional(783, getNo("22"), getNo("27"));//Prague-Zagreb
		adicionarEnlaceBidirecional(1213, getNo("23"), getNo("26"));//Stockholm-Warsaw
		adicionarEnlaceBidirecional(218, getNo("24"), getNo("28"));//Strasbourg-Zurich
		adicionarEnlaceBidirecional(400, getNo("25"), getNo("27"));//Vienna-Zagreb

		installModelGraph();

	}

	public Grafo getGrafo() {
		return this;
	}
}
