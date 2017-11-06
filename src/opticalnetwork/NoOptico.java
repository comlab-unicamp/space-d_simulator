package opticalnetwork;

import graph.AbstractGrafo;
import graph.Caminho;
import graph.Enlace;
import graph.ExcecaoGrafo;
import graph.ListaEnlaces;
import graph.No;

import java.util.HashMap;
import java.util.Map;

import algorithm.Dijkstra;

public class NoOptico implements No, Comparable<No>{


	private String numberId;
	private String name;
	private ListaEnlaces enlaces;
	private double pesoDijkstra;
	private HashMap<Integer,No> pathHopAnterior;
	private HashMap<Integer,No> resvHopAnterior;
	private HashMap<Integer,Integer> fluxoLabel;
	private AbstractGrafo grafo;
	private HashMap<No, Caminho> tabelaDeRotas;
	private int degree;


	public NoOptico(String numberId, String name, AbstractGrafo grafo){
		setNome(numberId);
		setName(name);
		enlaces = new ListaEnlaces();
		setGrafo(grafo);

		this.pathHopAnterior = new HashMap<Integer, No>(20);
		this.resvHopAnterior = new HashMap<Integer, No>(20);
		this.fluxoLabel = new HashMap<Integer, Integer>(20);
		this.tabelaDeRotas = new HashMap<No, Caminho>();
	}
	@Override
	public void setTabelaDeRotas(HashMap<No,Caminho> tabelaDeRotas){
		this.tabelaDeRotas = tabelaDeRotas;
	}
	@Override
	public Caminho getCaminhoDaTabelaDeRoteamento(No destino){
		return tabelaDeRotas.get(destino);
	}
	@Override
	public String getId() {
		return numberId;
	}

	@Override
	public void setNome(String numId) {
		this.numberId = numId;

	}

	@Override
	public Map<String, Enlace> getEnlaces() {
		return enlaces.getEnlaces();
	}


	@Override
	public void adicionarEnlace(Enlace enlace) throws ExcecaoGrafo{
		enlaces.adicionarEnlace(enlace);
	}



	@Override
	public double getPesoDijkstra() {
		return this.pesoDijkstra;
	}



	@Override
	public void setPesoDijkstra(double pesoDijkstra) {
		this.pesoDijkstra = pesoDijkstra;

	}

	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append(numberId);
		return builder.toString();
	}





	@Override
	public int compareTo(No no) {

		if (no.getPesoDijkstra() < this.getPesoDijkstra())
			return 1;
		else if(no.getPesoDijkstra() > this.getPesoDijkstra())
			return -1;
		else return 0;
	}

	@Override
	public void addPathHopAnterior(int idFluxo, No pathHopAnterior){
		this.pathHopAnterior.put(idFluxo, pathHopAnterior);
	}
	@Override
	public void addResvHopAnterior(int idFluxo, No resvHopAnterior){
		this.resvHopAnterior.put(idFluxo, resvHopAnterior);
	}
	@Override
	public void addFluxoLabel(int idFluxo, int label){
		this.fluxoLabel.put(idFluxo, label);
	}
	@Override
	public No getPathHopAnterior(int idFluxo){
		return this.pathHopAnterior.get(idFluxo);
	}
	@Override
	public No getResvHopAnterior(int idFluxo){
		return this.resvHopAnterior.get(idFluxo);
	}
	@Override
	public int getFluxoLabel(int idFluxo){
		return this.fluxoLabel.get(idFluxo);
	}
	@Override
	public void removerStates(int idFluxo){
		removerResvHopAnterior(idFluxo);
		removerPathHopAnterior(idFluxo);
		removerFluxoLabel(idFluxo);
	}

	@Override
	public void removerResvHopAnterior(int idFluxo) {
		resvHopAnterior.remove(idFluxo);

	}
	@Override
	public void removerPathHopAnterior(int idFluxo) {
		pathHopAnterior.remove(idFluxo);

	}
	@Override
	public void removerFluxoLabel(int idFluxo){
		this.fluxoLabel.remove(idFluxo);
	}

	@Override
	public void setGrafo(AbstractGrafo grafo){
		this.grafo = grafo;
	}

	@Override
	public AbstractGrafo getGrafo(){
		return this.grafo;
	}


	@Override
	public Enlace getProximoEnlace(Caminho caminho) throws ExcecaoGrafo {
		for(Enlace e : caminho.getEnlaces().valores()){
			if(e.getNoEsquerda().equals(this)){
				return e;
			}
		}
		return null;
	}

	@Override
	public Enlace getProximoEnlace(No destino) throws ExcecaoGrafo {
		Caminho caminho = null;
		if(tabelaDeRotas.containsKey(destino)){
			caminho = getCaminhoDaTabelaDeRoteamento(destino);
		} else {
			caminho = rotear(destino);
			tabelaDeRotas.put(destino, caminho);
		}

		for(Enlace e : caminho.getEnlaces().valores()){
			if(e.getNoEsquerda().equals(this)){
				return e;
			}
		}
		return null;
	}
	@Override
	public Caminho rotear(No destino) throws ExcecaoGrafo{
		Caminho caminho = Dijkstra.getMenorCaminho(getGrafo(), this, destino);
		return caminho;
	}
	/**
	 * @return the name
	 */
	@Override
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Object clone () {
		No newNo = new NoOptico(this.getId(), this.getName(), null);
		return newNo;
	}
	
	@Override
	public int getDegree() {
		return this.degree;
	}
	
	@Override
	public void setDegree(int degree) {
		this.degree = degree;
	}
	@Override
	public int countDegree() {
		int count =0;
		
		for (String s : this.enlaces.chaves()) {
			String[] pair = s.split("-");
			if (pair[1] != getId()) {
				count++;
			} 
		}
		return count;
		//TODO check!
	}
	
	
	
	
}
