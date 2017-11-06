package topology;

import graph.Grafo;
import opticalnetwork.elastica.NodeEON;

/**
 * JAPAN PHOTONIC NETWORK MODEL - 12 NODES
 * [1] “Japan Photonic Network Model,” 2013. 
 * [Online]. Available: http://www.ieice.org/cs/pn/jpn/jpnm.html. [Accessed: 16-Sep-2016].
 * 
 * Created in 16/09/2016
 * By @author Alaelson Jatoba
 * @version 1.0
 */
public class JNP12 extends Grafo{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Class for create a graph based on Japan Photonic Network with 12 nodes 
	 * 
	 * JAPAN PHOTONIC NETWORK MODEL - 12 NODES
	 * [1] “Japan Photonic Network Model,” 2013. 
	 * [Online]. Available: http://www.ieice.org/cs/pn/jpn/jpnm.html. [Accessed: 16-Sep-2016].
	 */
	public JNP12() {
		super(NetworkTopology.JNP12);
		try {
			create();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void create() throws Exception{
		//Nos da Topologia da NTTNet
		
		/*
		 * JPN12 Node Information
		 * Prefecture	Node Name	JPN12	Node code	Population under Node
		 * Hokkaido		Sapporo		010		SPPR		5506419
		 * Miyagi		Sendai		040		SEND		9335636
		 * Tokyo (east)	Tokyo		131		TKYE		24967992
		 * Tokyo (west)	Hachioji	132		TKYW		18499168
		 * Ishikawa		Kanazawa	170		KNZW		3069349
		 * Nagano		Nagano		200		NAGN		4526899
		 * Aichi		Nagoya		230		NAGY		15111223
		 * Osaka		Osaka		270		OSAK		20903173
		 * Hiroshima	Hiroshima	340		HRSM		7563428
		 * Ehime		Matsuyama	380		MTYM		3977282
		 * Fukuoka		Hakata		400		HAKT		13203965
		 * Okinawa		Naha		470		NAHA		1392818
		 * 										  total	128057352

		 * 
		 * 
		 * JPN12 Link Information				
		 * Link number	Source node	Destination node	Distance[km]	Remarkable
		 * 010040		Sapporo		Sendai				593,3			Ferry route: 242km
		 * 010132		Sapporo		Hachioji			1256,4
		 * 040131		Sendai		Tokyo				351,8
		 * 131132		Tokyo		Hachioji			47,4
		 * 131230		Tokyo		Nagoya				366
		 * 132200		Hachioji	Nagano				250,7
		 * 170200		Nagano		Kanazawa			252,2
		 * 200230		Nagano		Nagoya				250,8
		 * 230270		Nagoya		Osaka				186,6			Distance between Nagoya and Shin-Osaka
		 * 170270		Kanazawa	Osaka				263,8			Distance between Kanazawa and Shin-Osaka
		 * 230380		Nagoya		Matsuyama			490,7			Ferry route: 54.8km
		 * 270340		Osaka		Hiroshima			341,6			Distance between Shin-Osaka and Hiroshima
		 * 340380		Hiroshima	Matsuyama			66,2
		 * 340400		Hiroshima	Hakata				280,7
		 * 380470		Matsuyama	Naha				1158,7	Ferry route: 741.8km
		 * 380400		Matsuyama	Hakata				365	Ferry route: 68.1km
		 * 400470		Hakata		Naha				911,9	Straight-line distance between two prefectural capitals

		 * */

			
			adicionarNo(new NodeEON("1","Sapporo",this, 5506419));
			adicionarNo(new NodeEON("2","Sendai",this, 9335636));
			adicionarNo(new NodeEON("3","Tokyo",this, 24967992));
			adicionarNo(new NodeEON("4","Hachioji",this, 18499168));
			adicionarNo(new NodeEON("5","Kanazawa",this, 3069349));
			adicionarNo(new NodeEON("6","Nagano",this, 4526899));
			adicionarNo(new NodeEON("7","Nagoya",this, 15111223));
			adicionarNo(new NodeEON("8","Osaka",this, 20903173));
			adicionarNo(new NodeEON("9","Hiroshima",this, 7563428));
			adicionarNo(new NodeEON("10","Matsuyama",this, 3977282));
			adicionarNo(new NodeEON("11","Hakata",this, 13203965));
			adicionarNo(new NodeEON("12","Naha",this, 1392818));
			


			/* 17 links with distances between nodes*/
			adicionarEnlaceBidirecional(593.3 , getNo("1"),  getNo("2"));	//Sapporo-Sendai		593,3		Ferry route: 242km
			adicionarEnlaceBidirecional(1256.4, getNo("1"),  getNo("4"));	//Sapporo-Hachioji 		1256,4
			adicionarEnlaceBidirecional(351.8 , getNo("2"),  getNo("3"));	//Sendai- Tokyo			351,8
			adicionarEnlaceBidirecional(47.4  , getNo("3"),  getNo("4"));	//Tokyo-Hachioji		47,4
			adicionarEnlaceBidirecional(366   , getNo("3"),  getNo("7"));	//Tokyo-Nagoya			366
			adicionarEnlaceBidirecional(250.7 , getNo("4"),  getNo("6"));	//Hachioji-Nagano		250,7
			adicionarEnlaceBidirecional(252.2 , getNo("6"),  getNo("5"));	//Nagano-Kanazawa		252,2
			adicionarEnlaceBidirecional(250.8 , getNo("6"),  getNo("7"));	//Nagano-Nagoya			250,8
			adicionarEnlaceBidirecional(186.6 , getNo("7"),  getNo("8"));	//Nagoya-Osaka			186,6		Distance between Nagoya and Shin-Osaka
			adicionarEnlaceBidirecional(263.8 , getNo("5"),  getNo("8"));	//Kanazawa-Osaka		263,8		Distance between Kanazawa and Shin-Osaka
			adicionarEnlaceBidirecional(490.7 , getNo("7"),  getNo("10"));	//Nagoya-Matsuyama		490,7		Ferry route: 54.8km
			adicionarEnlaceBidirecional(341.6 , getNo("8"),  getNo("9"));	//Osaka-Hiroshima		341,6		Distance between Shin-Osaka and Hiroshima
			adicionarEnlaceBidirecional(66.2  , getNo("9"),  getNo("10"));	//Hiroshima-Matsuyama	66,2
			adicionarEnlaceBidirecional(280.7 , getNo("9"),  getNo("11"));	//Hiroshima-Hakata		280,7
			adicionarEnlaceBidirecional(1158.7, getNo("10"), getNo("12"));	//Matsuyama-Naha		1158,7		Ferry route: 741.8km
			adicionarEnlaceBidirecional(365   , getNo("10"), getNo("11"));	//Matsuyama-Hakata		365			Ferry route: 68.1km
			adicionarEnlaceBidirecional(911.9 , getNo("11"), getNo("12"));	//Hakata-Naha			911,9

			installModelGraph();


	}

	public Grafo getGrafo() {
		return this;
	}
}
