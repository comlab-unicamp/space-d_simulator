package topology;

import graph.Grafo;
import opticalnetwork.NoOptico;

public class NSFNet extends Grafo{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public NSFNet() {
		super(NetworkTopology.NSFNET);
		try {
			criaTopologiaNSFNet();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void criaTopologiaNSFNet() throws Exception{
		//Nos da Topologia da NSFNet

		for (Integer i = 1 ; i < 15 ; i++){
			adicionarNo(new NoOptico(i.toString(),"Node"+i.toString(),this));
		}


		//inserir enlaces bidirecionais
		/*adicionarEnlaceBidirecional(getNo("1"), getNo("2"));//1
		adicionarEnlaceBidirecional(getNo("1"), getNo("3"));
		adicionarEnlaceBidirecional(getNo("1"), getNo("8"));
		adicionarEnlaceBidirecional(getNo("2"), getNo("3"));
		adicionarEnlaceBidirecional(getNo("2"), getNo("4"));//5
		adicionarEnlaceBidirecional(getNo("3"), getNo("6"));
		adicionarEnlaceBidirecional(getNo("4"), getNo("5"));
		adicionarEnlaceBidirecional(getNo("4"), getNo("10"));
		adicionarEnlaceBidirecional(getNo("5"), getNo("6"));
		adicionarEnlaceBidirecional(getNo("5"), getNo("7"));//10
		adicionarEnlaceBidirecional(getNo("6"), getNo("9"));
		adicionarEnlaceBidirecional(getNo("6"), getNo("12"));
		adicionarEnlaceBidirecional(getNo("7"), getNo("8"));
		adicionarEnlaceBidirecional(getNo("8"), getNo("11"));
		adicionarEnlaceBidirecional(getNo("9"), getNo("11"));//15
		adicionarEnlaceBidirecional(getNo("10"), getNo("13"));
		adicionarEnlaceBidirecional(getNo("10"), getNo("14"));
		adicionarEnlaceBidirecional(getNo("11"), getNo("13"));
		adicionarEnlaceBidirecional(getNo("11"), getNo("14"));
		adicionarEnlaceBidirecional(getNo("12"), getNo("13"));//20
		adicionarEnlaceBidirecional(getNo("12"), getNo("14"));
*/
		//distancia em km, nó 1 e nó 2
		adicionarEnlaceBidirecional(500, getNo("1"), getNo("2"));//1
		adicionarEnlaceBidirecional(1000, getNo("1"), getNo("3"));
		adicionarEnlaceBidirecional(2000, getNo("1"), getNo("8"));
		adicionarEnlaceBidirecional(500, getNo("2"), getNo("3"));
		adicionarEnlaceBidirecional(300, getNo("2"), getNo("4"));//5
		adicionarEnlaceBidirecional(500, getNo("3"), getNo("6"));
		adicionarEnlaceBidirecional(300, getNo("4"), getNo("5"));
		adicionarEnlaceBidirecional(2000, getNo("4"), getNo("10"));
		adicionarEnlaceBidirecional(500, getNo("5"), getNo("6"));
		adicionarEnlaceBidirecional(300, getNo("5"), getNo("7"));//10
		adicionarEnlaceBidirecional(1000, getNo("6"), getNo("9"));
		adicionarEnlaceBidirecional(2000, getNo("6"), getNo("12"));
		adicionarEnlaceBidirecional(300, getNo("7"), getNo("8"));
		adicionarEnlaceBidirecional(300, getNo("8"), getNo("11"));
		adicionarEnlaceBidirecional(800, getNo("9"), getNo("11"));//15
		adicionarEnlaceBidirecional(500, getNo("10"), getNo("13"));
		adicionarEnlaceBidirecional(200, getNo("10"), getNo("14"));
		adicionarEnlaceBidirecional(500, getNo("11"), getNo("13"));
		adicionarEnlaceBidirecional(300, getNo("11"), getNo("14"));
		adicionarEnlaceBidirecional(200, getNo("12"), getNo("13"));//20
		adicionarEnlaceBidirecional(800, getNo("12"), getNo("14"));
		
		installModelGraph();

	}

	public Grafo getGrafo() {
		return this;
	}
}
