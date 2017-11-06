package topology;

import graph.Grafo;
import opticalnetwork.NoOptico;

public class NTTNet extends Grafo{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public NTTNet() {
		try {
			criaTopologiaNTTNet();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void criaTopologiaNTTNet() throws Exception{
		//Nos da Topologia da NTTNet

		for (Integer i = 1 ; i < 58 ; i++){
			adicionarNo(new NoOptico(i.toString(),"Node"+i.toString(),this));
		}

		//		inserir enlaces bidirecionais
		adicionarEnlaceBidirecional(getNo("1"), getNo("2"));//1
		adicionarEnlaceBidirecional(getNo("1"), getNo("3"));
		adicionarEnlaceBidirecional(getNo("2"), getNo("4"));
		adicionarEnlaceBidirecional(getNo("3"), getNo("5"));
		adicionarEnlaceBidirecional(getNo("4"), getNo("5"));//5
		adicionarEnlaceBidirecional(getNo("4"), getNo("15"));
		adicionarEnlaceBidirecional(getNo("4"), getNo("6"));
		adicionarEnlaceBidirecional(getNo("5"), getNo("6"));
		adicionarEnlaceBidirecional(getNo("5"), getNo("8"));
		adicionarEnlaceBidirecional(getNo("6"), getNo("7"));//10
		adicionarEnlaceBidirecional(getNo("7"), getNo("8"));
		adicionarEnlaceBidirecional(getNo("7"), getNo("10"));
		adicionarEnlaceBidirecional(getNo("8"), getNo("9"));
		adicionarEnlaceBidirecional(getNo("9"), getNo("11"));
		adicionarEnlaceBidirecional(getNo("10"), getNo("13"));//15
		adicionarEnlaceBidirecional(getNo("11"), getNo("12"));
		adicionarEnlaceBidirecional(getNo("12"), getNo("13"));
		adicionarEnlaceBidirecional(getNo("11"), getNo("14"));
		adicionarEnlaceBidirecional(getNo("13"), getNo("15"));
		adicionarEnlaceBidirecional(getNo("14"), getNo("15"));//20
		adicionarEnlaceBidirecional(getNo("14"), getNo("16"));
		adicionarEnlaceBidirecional(getNo("14"), getNo("17"));
		adicionarEnlaceBidirecional(getNo("15"), getNo("18"));
		adicionarEnlaceBidirecional(getNo("15"), getNo("21"));
		adicionarEnlaceBidirecional(getNo("16"), getNo("17"));//25
		adicionarEnlaceBidirecional(getNo("16"), getNo("18"));
		adicionarEnlaceBidirecional(getNo("17"), getNo("19"));
		adicionarEnlaceBidirecional(getNo("18"), getNo("19"));
		adicionarEnlaceBidirecional(getNo("18"), getNo("22"));
		adicionarEnlaceBidirecional(getNo("19"), getNo("20"));//30
		adicionarEnlaceBidirecional(getNo("20"), getNo("23"));
		adicionarEnlaceBidirecional(getNo("20"), getNo("24"));
		adicionarEnlaceBidirecional(getNo("21"), getNo("22"));
		adicionarEnlaceBidirecional(getNo("21"), getNo("25"));
		adicionarEnlaceBidirecional(getNo("22"), getNo("23"));//35
		adicionarEnlaceBidirecional(getNo("23"), getNo("26"));
		adicionarEnlaceBidirecional(getNo("24"), getNo("27"));
		adicionarEnlaceBidirecional(getNo("25"), getNo("29"));
		adicionarEnlaceBidirecional(getNo("25"), getNo("30"));
		adicionarEnlaceBidirecional(getNo("26"), getNo("28"));//40
		adicionarEnlaceBidirecional(getNo("26"), getNo("30"));
		adicionarEnlaceBidirecional(getNo("27"), getNo("28"));
		adicionarEnlaceBidirecional(getNo("27"), getNo("31"));
		adicionarEnlaceBidirecional(getNo("28"), getNo("31"));
		adicionarEnlaceBidirecional(getNo("28"), getNo("30"));//45
		adicionarEnlaceBidirecional(getNo("29"), getNo("32"));
		adicionarEnlaceBidirecional(getNo("30"), getNo("33"));
		adicionarEnlaceBidirecional(getNo("31"), getNo("35"));
		adicionarEnlaceBidirecional(getNo("32"), getNo("34"));
		adicionarEnlaceBidirecional(getNo("32"), getNo("37"));//50
		adicionarEnlaceBidirecional(getNo("33"), getNo("34"));
		adicionarEnlaceBidirecional(getNo("34"), getNo("36"));
		adicionarEnlaceBidirecional(getNo("35"), getNo("36"));
		adicionarEnlaceBidirecional(getNo("35"), getNo("39"));
		adicionarEnlaceBidirecional(getNo("36"), getNo("39"));//55
		adicionarEnlaceBidirecional(getNo("36"), getNo("38"));
		adicionarEnlaceBidirecional(getNo("36"), getNo("37"));
		adicionarEnlaceBidirecional(getNo("37"), getNo("40"));
		adicionarEnlaceBidirecional(getNo("38"), getNo("41"));
		adicionarEnlaceBidirecional(getNo("39"), getNo("43"));//60
		adicionarEnlaceBidirecional(getNo("40"), getNo("45"));
		adicionarEnlaceBidirecional(getNo("41"), getNo("46"));
		adicionarEnlaceBidirecional(getNo("42"), getNo("43"));
		adicionarEnlaceBidirecional(getNo("43"), getNo("44"));
		adicionarEnlaceBidirecional(getNo("42"), getNo("47"));//65
		adicionarEnlaceBidirecional(getNo("45"), getNo("49"));
		adicionarEnlaceBidirecional(getNo("45"), getNo("46"));
		adicionarEnlaceBidirecional(getNo("46"), getNo("47"));
		adicionarEnlaceBidirecional(getNo("46"), getNo("48"));
		adicionarEnlaceBidirecional(getNo("47"), getNo("51"));//70
		adicionarEnlaceBidirecional(getNo("48"), getNo("50"));
		adicionarEnlaceBidirecional(getNo("49"), getNo("50"));
		adicionarEnlaceBidirecional(getNo("49"), getNo("53"));
		adicionarEnlaceBidirecional(getNo("50"), getNo("51"));
		adicionarEnlaceBidirecional(getNo("51"), getNo("52"));//75
		adicionarEnlaceBidirecional(getNo("51"), getNo("53"));
		adicionarEnlaceBidirecional(getNo("51"), getNo("54"));
		adicionarEnlaceBidirecional(getNo("53"), getNo("54"));
		adicionarEnlaceBidirecional(getNo("53"), getNo("55"));
		adicionarEnlaceBidirecional(getNo("54"), getNo("56"));//80
		adicionarEnlaceBidirecional(getNo("54"), getNo("57"));


	}

	public Grafo getGrafo() {
		return this;
	}
}
