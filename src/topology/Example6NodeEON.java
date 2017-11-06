/**
 * Created on 03/09/2015
 */
package topology;

import graph.ExcecaoGrafo;
import graph.Grafo;
import opticalnetwork.elastica.NodeEON;

/**
 * @author Alaelson Jatobá
 * Version 1.0
 */
public class Example6NodeEON extends Grafo{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7450405248592533736L;

	public Example6NodeEON() {
		try {
			criaTopologia();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void criaTopologia() throws ExcecaoGrafo{
		//Nos da Topologia da NSFNet
		NodeEON node1 = new NodeEON("1","A", this, 10);
		adicionarNo(node1);
		NodeEON node2 = new NodeEON("2","B", this, 3);
		adicionarNo(node2);
		NodeEON node3 = new NodeEON("3","C", this, 1);
		adicionarNo(node3);
		NodeEON node4 = new NodeEON("4","D", this, 2);
		adicionarNo(node4);
		NodeEON node5 = new NodeEON("5","E",this, 15);
		adicionarNo(node5);
		NodeEON node6 = new NodeEON("6","F",this, 1);
		adicionarNo(node6);


		adicionarEnlaceBidirecional(getNo("1"), getNo("3"));
		adicionarEnlaceBidirecional(getNo("2"), getNo("3"));
		adicionarEnlaceBidirecional(getNo("3"), getNo("4"));
		adicionarEnlaceBidirecional(getNo("4"), getNo("5"));
		adicionarEnlaceBidirecional(getNo("4"), getNo("6"));
	}
}
