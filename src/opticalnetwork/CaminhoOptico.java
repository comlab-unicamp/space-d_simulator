package opticalnetwork;

import graph.Caminho;
import graph.Enlace;
import graph.ExcecaoGrafo;
import graph.ListaEnlaces;
import graph.ListaNos;
import graph.No;

public class CaminhoOptico {
	private Caminho caminho;
	private int lambda  = -1;
	
	public CaminhoOptico(){
		caminho = new Caminho();
	}
	
	public CaminhoOptico(No origem, No destino){
		caminho = new Caminho(origem,destino);
	}
	
	public CaminhoOptico(Caminho caminho, int lambda){
		this(caminho.getOrigem(), caminho.getDestino());
		this.setCaminho(caminho);
		this.lambda = lambda;
	}

	public Caminho getCaminho() {
		return caminho;
	}

	public void setCaminho(Caminho caminho) {
		this.caminho = caminho;
	}
	
	public ListaEnlaces getEnlaces(){
		return caminho.getEnlaces();
	}
	
	public ListaNos getNos(){
		return caminho.getNos();
	}
	
	public void adicionarNo(No no) throws ExcecaoGrafo{
		caminho.adicionarNo(no);
		
	}
	
	public void adicionarEnlace(No esq, No dir) throws ExcecaoGrafo{
		caminho.adicionarEnlace(esq, dir);
		
	}
	
	public void adicionarEnlace(Enlace enlace) throws ExcecaoGrafo {
		caminho.adicionarEnlace(enlace);	
	}
	
	public int getLambda() {
		return lambda;
	}

	public void setLambda(int lambda) {
		this.lambda = lambda;
	}
	
	public No getOrigem() {
		return caminho.getOrigem();
	}

	public void setOrigem(No origem) {
		caminho.setOrigem(origem);
	}

	public No getDestino() {
		return caminho.getDestino();
	}

	public void setDestino(No destino) {
		caminho.setDestino(destino);
	}
	
}
