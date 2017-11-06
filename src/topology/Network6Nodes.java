/**
 * Created on 03/09/2015
 */
package topology;

import graph.ExcecaoGrafo;
import graph.Grafo;
import opticalnetwork.NoOptico;

/**
 * @author Alaelson Jatobá
 * Version 1.0
 */
public class Network6Nodes extends Grafo{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8387292284341536475L;

	public Network6Nodes() {
		try {
			criaTopologia();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void criaTopologia() throws ExcecaoGrafo{
		//Nos da Topologia da NSFNet

		for (Integer i = 1 ; i < 7 ; i++){
			adicionarNo(new NoOptico(i.toString(),"Node"+i.toString(),this));
		}

		adicionarEnlaceBidirecional(getNo("1"), getNo("3"));
		adicionarEnlaceBidirecional(getNo("2"), getNo("3"));
		adicionarEnlaceBidirecional(getNo("3"), getNo("4"));
		adicionarEnlaceBidirecional(getNo("4"), getNo("5"));
		adicionarEnlaceBidirecional(getNo("4"), getNo("6"));
	}
}
