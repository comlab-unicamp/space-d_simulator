package graph;

import java.util.HashMap;
import java.util.Map;

public interface No{

	/**
	 *Gets the node's name
	 *@return the nodes name
	 **/
	public String getName();

	/**
	 *Sets the nodes's name
	 *@param name
	 * */
	public void setName(String name);


	/**
	 *Gets the node's name
	 *@return the nodes name
	 **/
	public String getId();

	/**
	 *Insere o nome do n�
	 *@param nome
	 * */
	public void setNome(String nome);

	/**
	 *@return ListaEnlaces lista de enlaces que saem deste n�
	 * */
	public Map<String, Enlace> getEnlaces();

	/**
	 * Adiciona um enlace que sai deste n�
	 *@param enlace � um enlace de sa�da
	 * */
	public void adicionarEnlace(Enlace enlace) throws ExcecaoGrafo;

	public double getPesoDijkstra();
	public void setPesoDijkstra(double pesoDijkstra);

	int compareTo(No no);

	void addPathHopAnterior(int idFluxo, No pathHopAnterior);

	void addResvHopAnterior(int idFluxo, No resvHopAnterior);

	void addFluxoLabel(int idFluxo, int label);

	No getPathHopAnterior(int idFluxo);

	No getResvHopAnterior(int idFluxo);

	int getFluxoLabel(int idFluxo);

	void removerStates(int idFluxo);

	void removerResvHopAnterior(int idFluxo);

	void removerPathHopAnterior(int idFluxo);

	void removerFluxoLabel(int idFluxo);


	void setGrafo(AbstractGrafo grafo);

	AbstractGrafo getGrafo();

	Enlace getProximoEnlace(Caminho caminho) throws ExcecaoGrafo;

	Enlace getProximoEnlace(No destino) throws ExcecaoGrafo;

	void setTabelaDeRotas(HashMap<No, Caminho> tabelaDeRotas);

	Caminho getCaminhoDaTabelaDeRoteamento(No destino);

	Caminho rotear(No destino) throws ExcecaoGrafo;

	/**
	 * @return
	 */
	public Object clone();
	
	public String toString();

	/**
	 * Returns the node degree
	 * @return a integer
	 */
	public int getDegree();
	
	
	/**
	 * Set the node degree
	 * @param degree a int value
	 */
	public void setDegree ( int degree );
	
	/**
	 * Calculates the node degree by counting the connected edges to this
	 * @return a int value
	 */
	public int countDegree () ;


}
