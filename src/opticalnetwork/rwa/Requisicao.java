/*
 * Modificada em 14/01/2009
 */
package opticalnetwork.rwa;

import graph.No;

import java.io.Serializable;


/**
 * @author Rodrigo
 * @version 1.0
 */
public class Requisicao implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private int idRequisicao;
	private No origem;
	private No destino;
	private Tipo tipo;
	private double duracao;
	private String chaveOrigemDestino;
	private int lambda = -1;

	public int getLambda() {
		return lambda;
	}
	public void setLambda(int lambda) {
		this.lambda = lambda;
	}


	public enum Tipo{
		ABRIR_CONEXAO,
		FECHAR_CONEXAO,
	}

	public Requisicao (){

	}
	public Requisicao(No origem, No destino, int idRequisicao, double duracao){
		this.origem = origem;
		this.destino = destino;
		this.idRequisicao = idRequisicao;
		this.tipo = Tipo.ABRIR_CONEXAO;
		this.duracao = duracao;
		this.chaveOrigemDestino = origem.getId()+"-"+destino.getId();
	}

	/**
	 * @return the idRequisicao
	 */
	public int getIdRequisicao() {
		return idRequisicao;
	}
	public String getChaveOrigemDestino(){
		return chaveOrigemDestino;
	}

	/**
	 * @param idRequisicao the idRequisicao to set
	 */
	public void setIdRequisicao(int idRequisicao) {
		this.idRequisicao = idRequisicao;
	}
	/**
	 * @return the tempoDeVida
	 */
	public double getDuracao() {
		return duracao;
	}

	/**
	 * @param duracao the tempoDeVida to set
	 */
	public void setDuracao(double duracao) {
		this.duracao = duracao;
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
	public Tipo getTipo() {
		return tipo;
	}
	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}


	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append("\n\tTipo: ");
		builder.append(this.getTipo());
		builder.append(", ID: ");
		builder.append(this.getIdRequisicao());
		builder.append(", ");
		builder.append(this.getOrigem());
		builder.append(" <--> ");
		builder.append(this.getDestino());
		builder.append(", Duracao: ");
		builder.append(this.getDuracao());
		return builder.toString();
	}


}
