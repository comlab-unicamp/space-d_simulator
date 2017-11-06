package opticalnetwork.rwa;

import graph.Caminho;
import graph.ExcecaoGrafo;
import graph.No;

import java.io.Serializable;



public class Pacote implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4668881306906283433L;
	private No origem;
	private No destino;
	private int id;
	private boolean usarRotaExplicita;
	private Caminho rro;
	private int rotulo;
	
	
	Pacote(){
		
	}
	public Pacote(No origem, No destino){
		this.origem = origem;
		this.destino = destino;
		this.usarRotaExplicita = false;
		this.rro = new Caminho(origem,destino);
	}
	
	public void adicionarNo(No no) throws ExcecaoGrafo{
		rro.adicionarNo(no);
	}
	
	
	public Caminho getRro() {
		return this.rro;
	}
	
	public void setRro(Caminho rro) {
		this.rro = rro;
	}
	/**
	 * @return the usarRotaExplicita
	 */
	public boolean isUsarRotaExplicita() {
		return usarRotaExplicita;
	}
	/**
	 * @param usarRotaExplicita the usarRotaExplicita to set
	 */
	public void setUsarRotaExplicita(boolean usarRotaExplicita) {
		this.usarRotaExplicita = usarRotaExplicita;
	}
	public No getOrigem() {
		return origem;
	}

	public void setOrigem(No origem) {
		this.origem = origem;
	}

	public No getDestino() {
		return destino;
	}

	public void setDestino(No destino) {
		this.destino = destino;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * @return the lambdaSelecionado
	 */
	public int getRotulo() {
		return this.rotulo;
	}

	/**
	 * @param label the lambdaSelecionado to set
	 */
	
	public void setRotulo(int rotulo) {
		this.rotulo = rotulo;
	}
	
}
